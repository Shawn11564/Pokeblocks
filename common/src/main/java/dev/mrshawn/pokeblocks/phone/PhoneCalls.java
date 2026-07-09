package dev.mrshawn.pokeblocks.phone;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.compendium.CompendiumVariantKey;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator.DollVariant;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.custom.PokedollPhoneItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

/**
 * Server-side Pokedoll Phone call logic: who calls, what gets buried, and how the player's
 * accept/hang-up answer is handled. The per-stack ring lifecycle itself lives on
 * {@link PokedollPhoneItem}; the dig-site quest that an accepted call starts lives in
 * {@link DigQuestManager}.
 * <p>
 * Doll variants travel as {@link CompendiumVariantKey}-style strings ({@code "species flag1 flag2"})
 * so the same identity convention is reused across the phone tags, the saved quest and the payloads.
 */
public final class PhoneCalls {

	private PhoneCalls() {}

	// Injected per loader; defaults fail safe so an un-wired loader just never syncs dig sites.
	private static Predicate<ServerPlayer> channelCheck = player -> false;
	private static BiConsumer<ServerPlayer, byte[]> sender = (player, data) -> {};

	/** Wires the loader-specific "can this player receive the payload" check and payload sender. */
	public static void setNetworkBridge(Predicate<ServerPlayer> canSend, BiConsumer<ServerPlayer, byte[]> send) {
		channelCheck = canSend;
		sender = send;
	}

	/** Whether the player's client negotiated the phone payloads (older/vanilla clients: no). */
	public static boolean canSendTo(ServerPlayer player) {
		return channelCheck.test(player);
	}

	public static void sendDigSites(ServerPlayer player, byte[] data) {
		sender.accept(player, data);
	}

	// ------------------------------------------------------------------
	// Variant keys
	// ------------------------------------------------------------------

	/** One parsed variant key: the species plus its active model flags. */
	public record Variant(String species, Set<ModelFlag> flags) {}

	/** The canonical key of a species + flags, shared with the compendium convention. */
	public static String buildVariantKey(String species, Set<ModelFlag> flags) {
		List<String> tagNames = new ArrayList<>();
		for (ModelFlag flag : flags) {
			tagNames.add(flag.getTagName());
		}
		return CompendiumVariantKey.of(species, tagNames);
	}

	/**
	 * Parses a {@code "species flag1 flag2"} key back into species + flags. Unknown flag tokens are
	 * ignored (a newer server may know flags this build doesn't) rather than failing the whole key.
	 */
	public static Variant parseVariantKey(String key) {
		String[] tokens = key.trim().toLowerCase(Locale.ROOT).split("\\s+");
		Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		for (int i = 1; i < tokens.length; i++) {
			ModelFlag flag = flagByTagName(tokens[i]);
			if (flag != null) {
				flags.add(flag);
			}
		}
		return new Variant(tokens[0], flags);
	}

	private static ModelFlag flagByTagName(String tagName) {
		for (ModelFlag flag : ModelFlag.values()) {
			if (flag.getTagName().equalsIgnoreCase(tagName)) {
				return flag;
			}
		}
		return null;
	}

	/** Builds the doll ItemStack for a variant key. */
	public static ItemStack createDoll(String variantKey) {
		Variant variant = parseVariantKey(variantKey);
		Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
		for (ModelFlag flag : variant.flags()) {
			flagMap.put(flag, true);
		}
		return PokedollItem.createPokedoll(variant.species(), flagMap);
	}

	/**
	 * The display name of the doll behind a variant key, without the trailing "Pokedoll" word (the
	 * phone talks about the character, not the item — {@code "Shiny Bulbasaur"}), tinted by the
	 * variant's rarity colour so it stands out in the phone's chat messages.
	 */
	public static Component dollName(String variantKey) {
		Variant variant = parseVariantKey(variantKey);
		DollRarity rarity = RarityScoreCalculator.resolvedRarity(variant.species(), variant.flags());
		return PokedollItem.displayName(createDoll(variantKey), false).copy().withStyle(rarity.getFormatting());
	}

	// ------------------------------------------------------------------
	// Picking dolls
	// ------------------------------------------------------------------

	/**
	 * Picks the calling doll: a uniformly random species, then a RARITY-WEIGHTED valid variant of it
	 * (so the caller is usually a common doll but occasionally a rarer, flagged one — its rarity in
	 * turn raises the buried doll's, see {@link #pickBuriedDollKey}). Using a valid variant of the
	 * species also avoids the broken bare stack some species have (e.g. Combee has no flagless
	 * texture). Null when no species is registered.
	 */
	public static String pickCallerKey(RandomSource random) {
		List<String> species = new ArrayList<>(PokemonRegistry.ALL_POKEMON.keySet());
		if (species.isEmpty()) return null;

		for (int attempt = 0; attempt < 20; attempt++) {
			String name = species.get(random.nextInt(species.size()));
			PokemonData data = PokemonRegistry.ALL_POKEMON.get(name);
			Set<ModelFlag> flags = pickRarityWeightedVariant(name, data, random);
			if (flags != null) {
				return buildVariantKey(name, flags);
			}
		}
		return null;
	}

	/** One valid variant of a species with its loot weight, for the caller's rarity-weighted pick. */
	private record WeightedCombo(Set<ModelFlag> flags, double weight) {}

	/**
	 * A rarity-weighted valid variant of the species (including the flagless one): each valid combo is
	 * weighted by its loot weight, so a common variant is the usual caller and a rarer one shows up
	 * occasionally. Null when the species has no valid, non-NONE variant.
	 */
	private static Set<ModelFlag> pickRarityWeightedVariant(String species, PokemonData data, RandomSource random) {
		if (data == null) return EnumSet.noneOf(ModelFlag.class);
		List<WeightedCombo> pool = new ArrayList<>();
		for (Set<ModelFlag> combo : data.generatePowerSet()) { // generatePowerSet already keeps only valid combos
			if (!data.getMissingRequiredFlags(combo).isEmpty()) continue;
			if (RarityScoreCalculator.resolvedRarity(species, combo) == DollRarity.NONE) continue;
			double weight = RarityScoreCalculator.effectiveWeight(species, combo);
			if (weight <= 0) continue;
			pool.add(new WeightedCombo(combo, weight));
		}
		WeightedCombo picked = pickWeighted(pool, WeightedCombo::weight, random.nextDouble());
		return picked == null ? null : picked.flags();
	}

	/**
	 * Picks the buried doll: rarity-weighted over every valid variant, using the same exclusions the
	 * loot pipeline applies (excluded flags/dolls from the config, no NONE-rarity variants), but only
	 * among variants at least as rare as {@code minRarity} — so a rarer caller buries a rarer (and
	 * potentially rarer still) doll. Falls back to the whole pool when nothing meets the floor (e.g. a
	 * top-tier caller whose tier is excluded from loot), so a quest is never starved of a buried doll.
	 * Null when nothing qualifies at all.
	 */
	public static String pickBuriedDollKey(RandomSource random, DollRarity minRarity) {
		List<DollVariant> pool = new ArrayList<>();
		for (DollVariant variant : RarityScoreCalculator.computeAllVariants(PokeblocksConfig.getExcludedLootFlags())) {
			if (variant.rarity() == DollRarity.NONE) continue;
			if (variant.weight() <= 0) continue;
			if (PokeblocksConfig.isDollExcludedFromLoot(variant.pokemon(), variant.flags())) continue;
			pool.add(variant);
		}
		if (pool.isEmpty()) return null;

		List<DollVariant> eligible = new ArrayList<>();
		for (DollVariant variant : pool) {
			if (variant.rarity().getSortOrder() >= minRarity.getSortOrder()) {
				eligible.add(variant);
			}
		}
		List<DollVariant> chosen = eligible.isEmpty() ? pool : eligible;

		DollVariant picked = pickWeighted(chosen, DollVariant::weight, random.nextDouble());
		return picked == null ? null : buildVariantKey(picked.pokemon(), picked.flags());
	}

	/**
	 * Cumulative-weight selection: {@code roll01} in [0,1) is scaled by the total weight and walked
	 * across the entries. Null for an empty (or zero-weight) pool. Pure, so it's unit-testable.
	 */
	public static <T> T pickWeighted(List<T> items, ToDoubleFunction<T> weight, double roll01) {
		double total = 0;
		for (T item : items) {
			total += Math.max(0, weight.applyAsDouble(item));
		}
		if (total <= 0) return null;

		double roll = roll01 * total;
		double running = 0;
		for (T item : items) {
			running += Math.max(0, weight.applyAsDouble(item));
			if (roll < running) {
				return item;
			}
		}
		return items.get(items.size() - 1);
	}

	// ------------------------------------------------------------------
	// Call responses (C2S)
	// ------------------------------------------------------------------

	/** Handles the client's accept/hang-up answer for a call the player picked up. */
	public static void handleCallResponse(MinecraftServer server, ServerPlayer player, byte[] data) {
		boolean accept;
		try {
			accept = PhonePayloadCodec.decodeCallResponse(data);
		} catch (Exception e) {
			PokeblocksLog.LOGGER.warn("Malformed phone call response from {}", player.getGameProfile().getName(), e);
			return;
		}

		ItemStack phone = PokedollPhoneItem.findAwaitingDecision(player);
		if (phone.isEmpty()) {
			player.displayClientMessage(Component.translatable("message.pokeblocks.phone.call_ended"), true);
			return;
		}

		String caller = PokedollPhoneItem.getCaller(phone);
		PokedollPhoneItem.endCall(phone, player.level().getGameTime());

		if (!accept || caller == null || caller.isBlank()) {
			player.displayClientMessage(Component.translatable("message.pokeblocks.phone.hung_up"), true);
			player.level().playSound(null, player.blockPosition(), SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundSource.PLAYERS, 0.6f, 1.2f);
			return;
		}

		// A quest is already running (shouldn't normally happen — phones don't ring during one):
		// never overwrite it, or its site blocks would be orphaned in the world.
		if (DigQuestManager.hasQuest(player)) {
			DigQuestStore.Quest quest = DigQuestManager.getQuest(player);
			player.displayClientMessage(Component.translatable("message.pokeblocks.phone.reminder",
					dollName(quest.callerKey()), quest.sites().size()), false);
			return;
		}

		if (!DigQuestManager.startQuest(player, caller)) {
			player.displayClientMessage(Component.translatable("message.pokeblocks.phone.no_sites"), false);
		}
	}
}
