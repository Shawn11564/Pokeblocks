package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.DecorativeRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class DecorativeGiveCMD {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_DECORATIVES = (context, builder) -> {
        for (DecorativeRegistry.DecorativeEntry entry : DecorativeRegistry.ALL_ENTRIES) {
            builder.suggest(entry.definition().id());
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var decorativeArg = Commands.argument("decorative", StringArgumentType.word())
                .suggests(SUGGEST_DECORATIVES)
                .executes(DecorativeGiveCMD::execute);

        // Attach flag literals dynamically based on each decorative's supported flags
        attachFlagLiterals(decorativeArg);

        dispatcher.register(
                Commands.literal("decorativegive")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(decorativeArg)
                        )
        );
    }

    private static void attachFlagLiterals(com.mojang.brigadier.builder.ArgumentBuilder<CommandSourceStack, ?> parent) {
        // Build up to 4 levels of flag nesting
        attachFlagLevel(parent, new HashSet<>(), 0);
    }

    private static void attachFlagLevel(com.mojang.brigadier.builder.ArgumentBuilder<CommandSourceStack, ?> parent, Set<ModelFlag> usedFlags, int depth) {
        if (depth >= 4) return;

        for (ModelFlag flag : ModelFlag.values()) {
            if (usedFlags.contains(flag)) continue;

            Set<ModelFlag> nextUsed = EnumSet.copyOf(usedFlags.isEmpty() ? EnumSet.noneOf(ModelFlag.class) : usedFlags);
            nextUsed.add(flag);

            LiteralArgumentBuilder<CommandSourceStack> flagLiteral = Commands.literal(flag.getTagName())
                    .executes(ctx -> executeWithFlags(ctx, nextUsed));

            attachFlagLevel(flagLiteral, nextUsed, depth + 1);
            parent.then(flagLiteral);
        }

        // Attach NBT variant arguments after flags
        attachNbtVariants(parent, usedFlags);
    }

    private static void attachNbtVariants(com.mojang.brigadier.builder.ArgumentBuilder<CommandSourceStack, ?> parent, Set<ModelFlag> flags) {
        Set<String> addedLiterals = new HashSet<>();

        for (DecorativeRegistry.DecorativeEntry entry : DecorativeRegistry.ALL_ENTRIES) {
            for (DecorativeDefinition.NbtVariant variant : entry.definition().nbtVariants()) {
                // Skip stackable variants — they are controlled by right-click, not commands
                if (variant.stackable()) continue;

                for (String value : variant.prefixMap().keySet()) {
                    String literal = variant.nbtKey() + "=" + value;
                    if (addedLiterals.contains(literal)) continue;
                    addedLiterals.add(literal);

                    Set<ModelFlag> flagsCopy = flags.isEmpty() ? EnumSet.noneOf(ModelFlag.class) : EnumSet.copyOf(flags);
                    parent.then(Commands.literal(literal)
                            .executes(ctx -> executeWithFlagsAndNbt(ctx, flagsCopy, Map.of(variant.nbtKey(), value))));
                }
            }
        }
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        return giveDecorative(context, Collections.emptySet(), Collections.emptyMap());
    }

    private static int executeWithFlags(CommandContext<CommandSourceStack> context, Set<ModelFlag> flags) {
        return giveDecorative(context, flags, Collections.emptyMap());
    }

    private static int executeWithFlagsAndNbt(CommandContext<CommandSourceStack> context, Set<ModelFlag> flags, Map<String, String> nbt) {
        return giveDecorative(context, flags, nbt);
    }

    private static int giveDecorative(CommandContext<CommandSourceStack> context, Set<ModelFlag> activeFlags, Map<String, String> customNbt) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String id = StringArgumentType.getString(context, "decorative").toLowerCase();
            CommandSourceStack source = context.getSource();

            DecorativeRegistry.DecorativeEntry entry = null;
            for (DecorativeRegistry.DecorativeEntry e : DecorativeRegistry.ALL_ENTRIES) {
                if (e.definition().id().equals(id)) {
                    entry = e;
                    break;
                }
            }

            if (entry == null) {
                source.sendFailure(Component.literal("Decorative block '" + id + "' is not registered"));
                return 0;
            }

            // Validate flags are supported
            for (ModelFlag flag : activeFlags) {
                if (!entry.definition().supportedFlags().contains(flag)) {
                    source.sendFailure(Component.literal("Flag '" + flag.getTagName() + "' is not supported for " + id));
                    return 0;
                }
            }

            ItemStack stack = DecorativeItem.createStack(
                    entry.item().get(),
                    "pokeblocks:" + id,
                    activeFlags,
                    customNbt
            );

            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }

            List<String> flagNames = new ArrayList<>();
            for (ModelFlag f : activeFlags) flagNames.add(f.getTagName());
            for (Map.Entry<String, String> nbtEntry : customNbt.entrySet()) {
                flagNames.add(nbtEntry.getKey() + "=" + nbtEntry.getValue());
            }

            DecorativeRegistry.DecorativeEntry finalEntry = entry;
            source.sendSuccess(() -> Component.literal("Gave " + finalEntry.definition().displayName()
                    + " to " + player.getName().getString()
                    + (flagNames.isEmpty() ? "" : " with: " + String.join(", ", flagNames))), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to give decorative: " + e.getMessage()));
            return 0;
        }
    }
}