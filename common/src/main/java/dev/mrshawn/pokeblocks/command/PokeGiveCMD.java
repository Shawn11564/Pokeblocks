package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.mrshawn.pokeblocks.item.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class PokeGiveCMD {

	private static final SuggestionProvider<CommandSourceStack> SUGGEST_POKEMON = (context, builder) -> {
		for (String id : PokemonRegistry.ALL_POKEMON.keySet()) builder.suggest(id);
		return builder.buildFuture();
	};

	private static Set<String> getAvailableFlags(CommandContext<CommandSourceStack> context) {
		try {
			String pokemon = StringArgumentType.getString(context, "pokemon").toLowerCase();
			PokemonData data = PokemonRegistry.getPokemonData(pokemon);
			if (data == null) return Collections.emptySet();

			Set<String> available = new LinkedHashSet<>();
			for (var entry : data.modelFlags().entrySet()) {
				if (Boolean.TRUE.equals(entry.getValue())) {
					available.add(entry.getKey().getTagName());
				}
			}
			return available;
		} catch (Exception e) {
			return Collections.emptySet();
		}
	}

	private static Set<String> getUsedFlags(CommandContext<CommandSourceStack> context, String... argNames) {
		Set<String> used = new HashSet<>();
		for (String name : argNames) {
			try {
				used.add(StringArgumentType.getString(context, name).toLowerCase());
			} catch (Exception ignored) {}
		}
		return used;
	}

	private static SuggestionProvider<CommandSourceStack> suggestFlags(String... previousArgs) {
		return (context, builder) -> {
			Set<String> available = getAvailableFlags(context);
			Set<String> used = getUsedFlags(context, previousArgs);
			for (String flag : available) {
				if (!used.contains(flag)) {
					builder.suggest(flag);
				}
			}
			return builder.buildFuture();
		};
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		// Build up to 4 flag depth (one per ModelFlag)
		dispatcher.register(
				Commands.literal("pokegive")
						.requires(src -> src.hasPermission(2))
						.then(Commands.argument("player", EntityArgument.player())
								.then(Commands.argument("pokemon", StringArgumentType.word())
										.suggests(SUGGEST_POKEMON)
										.executes(PokeGiveCMD::execute)
										.then(Commands.argument("flag1", StringArgumentType.word())
												.suggests(suggestFlags())
												.executes(ctx -> executeWithFlags(ctx, "flag1"))
												.then(Commands.argument("flag2", StringArgumentType.word())
														.suggests(suggestFlags("flag1"))
														.executes(ctx -> executeWithFlags(ctx, "flag1", "flag2"))
														.then(Commands.argument("flag3", StringArgumentType.word())
																.suggests(suggestFlags("flag1", "flag2"))
																.executes(ctx -> executeWithFlags(ctx, "flag1", "flag2", "flag3"))
																.then(Commands.argument("flag4", StringArgumentType.word())
																		.suggests(suggestFlags("flag1", "flag2", "flag3"))
																		.executes(ctx -> executeWithFlags(ctx, "flag1", "flag2", "flag3", "flag4"))
																)
														)
												)
										)
								)
						)
		);
	}

	private static int execute(CommandContext<CommandSourceStack> context) {
		return givePokedoll(context, Collections.emptySet());
	}

	private static int executeWithFlags(CommandContext<CommandSourceStack> context, String... argNames) {
		Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		for (String argName : argNames) {
			try {
				String value = StringArgumentType.getString(context, argName).toLowerCase();
				for (ModelFlag flag : ModelFlag.values()) {
					if (flag.getTagName().equals(value)) {
						flags.add(flag);
						break;
					}
				}
			} catch (Exception ignored) {}
		}
		return givePokedoll(context, flags);
	}

	private static int givePokedoll(CommandContext<CommandSourceStack> context, Set<ModelFlag> activeFlags) {
		try {
			ServerPlayer player = EntityArgument.getPlayer(context, "player");
			String pokemon = StringArgumentType.getString(context, "pokemon").toLowerCase();
			CommandSourceStack source = context.getSource();

			if (!PokemonRegistry.isRegistered(pokemon)) {
				source.sendFailure(Component.literal("Pokemon '" + pokemon + "' is not registered"));
				return 0;
			}

			Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) {
				flagMap.put(flag, activeFlags.contains(flag));
			}

			ItemStack stack = PokedollItem.createPokedoll(pokemon, new PokemonData(flagMap));

			if (!player.getInventory().add(stack)) {
				player.drop(stack, false);
			}

			List<String> flagNames = new ArrayList<>();
			for (ModelFlag f : activeFlags) flagNames.add(f.getTagName());

			source.sendSuccess(() -> Component.literal("Gave " + pokemon + " pokedoll to " + player.getName().getString()
					+ (flagNames.isEmpty() ? "" : " with flags: " + String.join(", ", flagNames))), true);
			return 1;
		} catch (Exception e) {
			context.getSource().sendFailure(Component.literal("Failed to give pokedoll: " + e.getMessage()));
			return 0;
		}
	}
}