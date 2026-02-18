package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FigurineGiveCMD {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_FIGURINES = (context, builder) -> {
        for (String id : FigurineRegistry.ALL_FIGURINES.keySet()) builder.suggest(id);
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("figurinegive")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("figurine", StringArgumentType.word())
                        .suggests(SUGGEST_FIGURINES)
                        .executes(FigurineGiveCMD::execute)
                    )
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String figurine = StringArgumentType.getString(context, "figurine").toLowerCase();
            CommandSourceStack source = context.getSource();

            if (!FigurineRegistry.isRegistered(figurine)) {
                source.sendFailure(Component.literal("Figurine '" + figurine + "' is not registered"));
                return 0;
            }

            ItemStack stack = FigurineItem.createFigurine(figurine);

            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }

            source.sendSuccess(() -> Component.literal("Gave " + figurine + " figurine to " + player.getName().getString()), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Failed to give figurine: " + e.getMessage()));
            return 0;
        }
    }
}