package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.mrshawn.pokeblocks.item.custom.PokedollPhoneItem;
import dev.mrshawn.pokeblocks.phone.PhoneCalls;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * {@code /pokeblocks phonering <player> [pokemon] [flags...]} — forces an incoming call on a target
 * player's Pokedoll Phone. Unlike a natural call it bypasses the random ring roll, the between-call
 * cooldown and the {@code [phone] enabled} config, but it still respects the things that would make a
 * forced ring pointless: the target must be carrying a phone, that phone must not already be
 * ringing/mid-call, and the target's client must have negotiated the phone channel so it can show the
 * call screen.
 * <p>
 * With no {@code pokemon} argument a random doll (same picker as natural calls) is placed on the
 * line. Supplying a {@code pokemon} — and optionally up to four model flags — forces that exact
 * doll variant to be the caller; the combination is validated the same way the natural picker
 * validates its representative, so an invalid variant is rejected rather than ringing with a broken
 * doll. The pokemon/flag argument tree mirrors {@link PokeGiveCMD}.
 */
public class PhoneRingCMD {

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
			} catch (Exception ignored) {
			}
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
				Commands.literal("pokeblocks").then(
						Commands.literal("phonering")
								.requires(src -> src.hasPermission(2))
								.then(Commands.argument("player", EntityArgument.player())
										// /pokeblocks phonering <player> — random caller
										.executes(PhoneRingCMD::execute)
										// /pokeblocks phonering <player> <pokemon> [flags...] — specific caller
										.then(Commands.argument("pokemon", StringArgumentType.word())
												.suggests(SUGGEST_POKEMON)
												.executes(ctx -> executeWithCaller(ctx))
												.then(Commands.argument("flag1", StringArgumentType.word())
														.suggests(suggestFlags())
														.executes(ctx -> executeWithCaller(ctx, "flag1"))
														.then(Commands.argument("flag2", StringArgumentType.word())
																.suggests(suggestFlags("flag1"))
																.executes(ctx -> executeWithCaller(ctx, "flag1", "flag2"))
																.then(Commands.argument("flag3", StringArgumentType.word())
																		.suggests(suggestFlags("flag1", "flag2"))
																		.executes(ctx -> executeWithCaller(ctx, "flag1", "flag2", "flag3"))
																		.then(Commands.argument("flag4", StringArgumentType.word())
																				.suggests(suggestFlags("flag1", "flag2", "flag3"))
																				.executes(ctx -> executeWithCaller(ctx, "flag1", "flag2", "flag3", "flag4"))
																		)
																)
														)
												)
										)
								)
				));
	}

	/** {@code /pokeblocks phonering <player>} — ring with a random caller. */
	private static int execute(CommandContext<CommandSourceStack> context) {
		return ring(context, false);
	}

	/** {@code /pokeblocks phonering <player> <pokemon> [flags...]} — ring with a specific caller. */
	private static int executeWithCaller(CommandContext<CommandSourceStack> context, String... flagArgNames) {
		return ring(context, true, flagArgNames);
	}

	private static int ring(CommandContext<CommandSourceStack> context, boolean specificCaller, String... flagArgNames) {
		CommandSourceStack source = context.getSource();
		try {
			ServerPlayer player = EntityArgument.getPlayer(context, "player");

			if (!PokedollPhoneItem.hasPhone(player)) {
				source.sendFailure(Component.literal(player.getName().getString() + " isn't carrying a Pokedoll Phone."));
				return 0;
			}

			if (!PhoneCalls.canSendTo(player)) {
				source.sendFailure(Component.literal(player.getName().getString()
						+ "'s client hasn't loaded the Pokedoll Phone features."));
				return 0;
			}

			ItemStack phone = PokedollPhoneItem.findIdlePhone(player);
			if (phone.isEmpty()) {
				source.sendFailure(Component.literal(player.getName().getString() + "'s Pokedoll Phone is already ringing."));
				return 0;
			}

			String caller;
			if (specificCaller) {
				caller = resolveSpecificCaller(context, source, flagArgNames);
				if (caller == null) return 0; // resolveSpecificCaller already reported the reason
			} else {
				caller = PhoneCalls.pickCallerKey(player.level().getRandom());
				if (caller == null) {
					source.sendFailure(Component.literal("No pokedolls are registered to place a call."));
					return 0;
				}
			}

			String finalCaller = caller;
			PokedollPhoneItem.beginRing(player, phone, finalCaller, player.level().getGameTime());

			source.sendSuccess(() -> Component.literal("Rang " + player.getName().getString() + "'s Pokedoll Phone — ")
					.append(PhoneCalls.dollName(finalCaller))
					.append(Component.literal(" is calling.")), true);
			return 1;
		} catch (Exception e) {
			source.sendFailure(Component.literal("Failed to ring phone: " + e.getMessage()));
			return 0;
		}
	}

	/**
	 * Turns the {@code pokemon} argument and any {@code flagN} arguments into a caller variant key,
	 * validating that the combination is a real, renderable doll (same rule the natural caller picker
	 * enforces). Returns null and reports the reason via {@code source} when the variant is invalid.
	 */
	private static String resolveSpecificCaller(CommandContext<CommandSourceStack> context, CommandSourceStack source, String... flagArgNames) {
		String pokemon = StringArgumentType.getString(context, "pokemon").toLowerCase();
		if (!PokemonRegistry.isRegistered(pokemon)) {
			source.sendFailure(Component.literal("Pokemon '" + pokemon + "' is not registered"));
			return null;
		}
		PokemonData data = PokemonRegistry.getPokemonData(pokemon);

		Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		for (String argName : flagArgNames) {
			String value = StringArgumentType.getString(context, argName).toLowerCase();
			ModelFlag flag = flagByTagName(value);
			if (flag == null) {
				source.sendFailure(Component.literal("Unknown flag: " + value));
				return null;
			}
			flags.add(flag);
		}

		Set<ModelFlag> missing = data.getMissingRequiredFlags(flags);
		if (!missing.isEmpty()) {
			List<String> missingNames = new ArrayList<>();
			for (ModelFlag f : missing) missingNames.add(f.getTagName());
			source.sendFailure(Component.literal("The flag combination for '" + pokemon
					+ "' also requires: " + String.join(", ", missingNames)));
			return null;
		}
		if (!data.isValidCombination(flags)) {
			source.sendFailure(Component.literal("'" + pokemon + "' has no doll variant for that flag combination."));
			return null;
		}

		return PhoneCalls.buildVariantKey(pokemon, flags);
	}

	private static ModelFlag flagByTagName(String tagName) {
		for (ModelFlag flag : ModelFlag.values()) {
			if (flag.getTagName().equalsIgnoreCase(tagName)) {
				return flag;
			}
		}
		return null;
	}
}
