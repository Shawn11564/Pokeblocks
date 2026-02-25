package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
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
import java.util.concurrent.ThreadLocalRandom;

public class PokeGiveCMD {

	private static final SuggestionProvider<CommandSourceStack> SUGGEST_POKEMON = (context, builder) -> {
		for (String id : PokemonRegistry.ALL_POKEMON.keySet()) builder.suggest(id);
		return builder.buildFuture();
	};

	private static final SuggestionProvider<CommandSourceStack> SUGGEST_RARITY = (context, builder) -> {
		for (DollRarity rarity : DollRarity.values()) {
			if (rarity != DollRarity.NONE) {
				builder.suggest(rarity.name().toLowerCase());
			}
		}
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
		dispatcher.register(
				Commands.literal("pokegive")
						.requires(src -> src.hasPermission(2))
						.then(Commands.argument("player", EntityArgument.player())
								// Subcommand: /pokegive <player> random
								.then(Commands.literal("random")
										.executes(PokeGiveCMD::executeRandom)
								)
								// Subcommand: /pokegive <player> rarity <rarity>
								.then(Commands.literal("rarity")
										.then(Commands.argument("rarity", StringArgumentType.word())
												.suggests(SUGGEST_RARITY)
												.executes(PokeGiveCMD::executeRarity)
										)
								)
								// Existing: /pokegive <player> <pokemon> [flags...]
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

	private static int executeRandom(CommandContext<CommandSourceStack> context) {
		try {
			ServerPlayer player = EntityArgument.getPlayer(context, "player");
			CommandSourceStack source = context.getSource();

			// Collect all valid pokemon + flag combinations
			List<RarityEntry> candidates = new ArrayList<>();

			for (var entry : PokemonRegistry.ALL_POKEMON.entrySet()) {
				String name = entry.getKey();
				PokemonData data = entry.getValue();

				List<ModelFlag> availableFlags = new ArrayList<>();
				for (var flagEntry : data.modelFlags().entrySet()) {
					if (Boolean.TRUE.equals(flagEntry.getValue())) {
						availableFlags.add(flagEntry.getKey());
					}
				}

				List<Set<ModelFlag>> combinations = generateFlagCombinations(availableFlags);

				for (Set<ModelFlag> flagCombo : combinations) {
					Set<ModelFlag> missing = data.getMissingRequiredFlags(flagCombo);
					if (!missing.isEmpty()) continue;

					candidates.add(new RarityEntry(name, flagCombo));
				}
			}

			if (candidates.isEmpty()) {
				source.sendFailure(Component.literal("No dolls are registered"));
				return 0;
			}

			RarityEntry chosen = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));

			Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) {
				flagMap.put(flag, chosen.flags.contains(flag));
			}

			ItemStack stack = PokedollItem.createPokedoll(chosen.pokemon, new PokemonData(flagMap));

			if (!player.getInventory().add(stack)) {
				player.drop(stack, false);
			}

			List<String> flagNames = new ArrayList<>();
			for (ModelFlag f : chosen.flags) flagNames.add(f.getTagName());

			DollRarity rarity = resolveRarity(chosen.pokemon, chosen.flags);
			source.sendSuccess(() -> Component.literal("Gave random pokedoll ("
					+ chosen.pokemon + (flagNames.isEmpty() ? "" : " [" + String.join(", ", flagNames) + "]")
					+ ", " + rarity.getDisplayName()
					+ ") to " + player.getName().getString()), true);
			return 1;
		} catch (Exception e) {
			context.getSource().sendFailure(Component.literal("Failed to give random pokedoll: " + e.getMessage()));
			return 0;
		}
	}

	private static int executeRarity(CommandContext<CommandSourceStack> context) {
		try {
			ServerPlayer player = EntityArgument.getPlayer(context, "player");
			String rarityStr = StringArgumentType.getString(context, "rarity").toUpperCase();
			CommandSourceStack source = context.getSource();

			DollRarity targetRarity;
			try {
				targetRarity = DollRarity.valueOf(rarityStr);
			} catch (IllegalArgumentException e) {
				source.sendFailure(Component.literal("Unknown rarity: " + rarityStr.toLowerCase()));
				return 0;
			}

			if (targetRarity == DollRarity.NONE) {
				source.sendFailure(Component.literal("Cannot give a doll with rarity NONE"));
				return 0;
			}

			// Collect all pokemon + flag combinations that match the target rarity
			List<RarityEntry> candidates = new ArrayList<>();

			for (var entry : PokemonRegistry.ALL_POKEMON.entrySet()) {
				String name = entry.getKey();
				PokemonData data = entry.getValue();

				// Collect all available flags for this pokemon
				List<ModelFlag> availableFlags = new ArrayList<>();
				for (var flagEntry : data.modelFlags().entrySet()) {
					if (Boolean.TRUE.equals(flagEntry.getValue())) {
						availableFlags.add(flagEntry.getKey());
					}
				}

				// Generate all valid flag combinations (power set of available flags)
				List<Set<ModelFlag>> combinations = generateFlagCombinations(availableFlags);

				for (Set<ModelFlag> flagCombo : combinations) {
					// Skip if this combo has missing required flags
					Set<ModelFlag> missing = data.getMissingRequiredFlags(flagCombo);
					if (!missing.isEmpty()) continue;

					// Determine effective rarity for this combo
					DollRarity effectiveRarity = resolveRarity(name, flagCombo);

					if (effectiveRarity == targetRarity) {
						candidates.add(new RarityEntry(name, flagCombo));
					}
				}
			}

			if (candidates.isEmpty()) {
				source.sendFailure(Component.literal("No dolls found with rarity: " + targetRarity.getDisplayName()));
				return 0;
			}

			// Pick a random candidate
			RarityEntry chosen = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));

			Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) {
				flagMap.put(flag, chosen.flags.contains(flag));
			}

			ItemStack stack = PokedollItem.createPokedoll(chosen.pokemon, new PokemonData(flagMap));

			if (!player.getInventory().add(stack)) {
				player.drop(stack, false);
			}

			List<String> flagNames = new ArrayList<>();
			for (ModelFlag f : chosen.flags) flagNames.add(f.getTagName());

			source.sendSuccess(() -> Component.literal("Gave random " + targetRarity.getDisplayName() + " pokedoll ("
					+ chosen.pokemon + (flagNames.isEmpty() ? "" : " [" + String.join(", ", flagNames) + "]")
					+ ") to " + player.getName().getString()), true);
			return 1;
		} catch (Exception e) {
			context.getSource().sendFailure(Component.literal("Failed to give random pokedoll: " + e.getMessage()));
			return 0;
		}
	}

	/**
	 * Determines the effective rarity for a pokemon + flag combination.
	 * Checks DollRarityOverrides first, then falls back to highest flag rarity.
	 */
	private static DollRarity resolveRarity(String pokemon, Set<ModelFlag> flags) {
		// Check for explicit override
		DollRarity override = DollRarityOverrides.getOverride(pokemon, flags);
		if (override != null) return override;

		// Fall back to highest rarity from active flags
		if (flags.isEmpty()) return DollRarity.COMMON;
		return DollRarity.getHighestRarity(flags);
	}

	/**
	 * Generates all subsets of the given flags, including the empty set.
	 */
	private static List<Set<ModelFlag>> generateFlagCombinations(List<ModelFlag> flags) {
		List<Set<ModelFlag>> result = new ArrayList<>();
		int n = flags.size();
		for (int mask = 0; mask < (1 << n); mask++) {
			Set<ModelFlag> combo = EnumSet.noneOf(ModelFlag.class);
			for (int i = 0; i < n; i++) {
				if ((mask & (1 << i)) != 0) {
					combo.add(flags.get(i));
				}
			}
			result.add(combo);
		}
		return result;
	}

	private record RarityEntry(String pokemon, Set<ModelFlag> flags) {}

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

			PokemonData data = PokemonRegistry.getPokemonData(pokemon);

			// Check required flag combinations
			Set<ModelFlag> missing = data.getMissingRequiredFlags(activeFlags);
			if (!missing.isEmpty()) {
				List<String> missingNames = new ArrayList<>();
				for (ModelFlag f : missing) missingNames.add(f.getTagName());
				source.sendFailure(Component.literal("The flag combination for '" + pokemon + "' also requires: " + String.join(", ", missingNames)));
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