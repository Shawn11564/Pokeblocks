package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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

	private static DecorativeRegistry.DecorativeEntry findEntry(CommandContext<CommandSourceStack> context) {
		try {
			String id = StringArgumentType.getString(context, "decorative").toLowerCase();
			for (DecorativeRegistry.DecorativeEntry e : DecorativeRegistry.ALL_ENTRIES) {
				if (e.definition().id().equals(id)) return e;
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	private static SuggestionProvider<CommandSourceStack> suggestFlags(String... previousArgs) {
		return (context, builder) -> {
			DecorativeRegistry.DecorativeEntry entry = findEntry(context);
			if (entry == null) return builder.buildFuture();

			Set<String> used = new HashSet<>();
			for (String name : previousArgs) {
				try {
					used.add(StringArgumentType.getString(context, name).toLowerCase());
				} catch (Exception ignored) {
				}
			}

			for (ModelFlag flag : entry.definition().supportedFlags()) {
				if (!used.contains(flag.getTagName())) {
					builder.suggest(flag.getTagName());
				}
			}

			// Suggest non-stackable NBT variants
			for (DecorativeDefinition.NbtVariant variant : entry.definition().nbtVariants()) {
				if (variant.stackable()) continue;
				for (String value : variant.prefixMap().keySet()) {
					String literal = variant.nbtKey() + "=" + value;
					if (!used.contains(literal)) {
						builder.suggest(literal);
					}
				}
			}

			return builder.buildFuture();
		};
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("pokeblocks").then(
						Commands.literal("decorativegive")
								.requires(src -> src.hasPermission(2))
								.then(Commands.argument("player", EntityArgument.player())
										.then(Commands.argument("decorative", StringArgumentType.word())
												.suggests(SUGGEST_DECORATIVES)
												.executes(DecorativeGiveCMD::execute)
												.then(Commands.argument("flag1", StringArgumentType.word())
														.suggests(suggestFlags())
														.executes(ctx -> executeWithArgs(ctx, "flag1"))
														.then(Commands.argument("flag2", StringArgumentType.word())
																.suggests(suggestFlags("flag1"))
																.executes(ctx -> executeWithArgs(ctx, "flag1", "flag2"))
																.then(Commands.argument("flag3", StringArgumentType.word())
																		.suggests(suggestFlags("flag1", "flag2"))
																		.executes(ctx -> executeWithArgs(ctx, "flag1", "flag2", "flag3"))
																		.then(Commands.argument("flag4", StringArgumentType.word())
																				.suggests(suggestFlags("flag1", "flag2", "flag3"))
																				.executes(ctx -> executeWithArgs(ctx, "flag1", "flag2", "flag3", "flag4"))
																		)
																)
														)
												)
										)
								)
				));
	}

	private static int execute(CommandContext<CommandSourceStack> context) {
		return giveDecorative(context, Collections.emptySet(), Collections.emptyMap());
	}

	private static int executeWithArgs(CommandContext<CommandSourceStack> context, String... argNames) {
		Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		Map<String, String> customNbt = new HashMap<>();

		for (String argName : argNames) {
			try {
				String value = StringArgumentType.getString(context, argName);

				// Check if it's a key=value NBT pair
				if (value.contains("=")) {
					String[] parts = value.split("=", 2);
					customNbt.put(parts[0], parts[1]);
					continue;
				}

				// Otherwise treat as a flag
				String lower = value.toLowerCase();
				boolean found = false;
				for (ModelFlag flag : ModelFlag.values()) {
					if (flag.getTagName().equals(lower)) {
						flags.add(flag);
						found = true;
						break;
					}
				}

				if (!found) {
					context.getSource().sendFailure(Component.literal("Unknown flag or variant: '" + value + "'"));
					return 0;
				}
			} catch (Exception ignored) {
			}
		}

		return giveDecorative(context, flags, customNbt);
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

			List<String> details = new ArrayList<>();
			for (ModelFlag f : activeFlags) details.add(f.getTagName());
			for (Map.Entry<String, String> nbtEntry : customNbt.entrySet()) {
				details.add(nbtEntry.getKey() + "=" + nbtEntry.getValue());
			}

			DecorativeRegistry.DecorativeEntry finalEntry = entry;
			source.sendSuccess(() -> Component.literal("Gave " + finalEntry.definition().displayName()
					+ " to " + player.getName().getString()
					+ (details.isEmpty() ? "" : " with: " + String.join(", ", details))), true);
			return 1;
		} catch (Exception e) {
			context.getSource().sendFailure(Component.literal("Failed to give decorative: " + e.getMessage()));
			return 0;
		}
	}
}