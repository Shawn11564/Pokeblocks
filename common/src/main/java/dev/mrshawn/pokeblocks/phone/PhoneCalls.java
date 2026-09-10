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
import net.minecraft.ChatFormatting;
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

	// ------------------------------------------------------------------
	// Lost-doll rarity descriptor
	// ------------------------------------------------------------------

	/** Whether a lost-doll descriptor names a rarity tier or a rarity-percent window. */
	public enum LostKind { RARITY, PERCENT }

	/**
	 * What the caller says they lost: either a named rarity {@link LostKind#RARITY tier} (e.g. "Rare"),
	 * or a {@link LostKind#PERCENT} rarity-percent window ({@code minPercent}–{@code maxPercent}). In
	 * both cases {@link #rarity} carries the colour the descriptor is tinted with (for a percent window,
	 * the caller's own rarity). The buried doll is later drawn to MATCH this descriptor.
	 */
	public record LostDollTarget(LostKind kind, DollRarity rarity, double minPercent, double maxPercent) {
		public static LostDollTarget rarity(DollRarity rarity) {
			return new LostDollTarget(LostKind.RARITY, rarity, 0, 0);
		}

		public static LostDollTarget percent(DollRarity colourRarity, double minPercent, double maxPercent) {
			return new LostDollTarget(LostKind.PERCENT, colourRarity, minPercent, maxPercent);
		}
	}

	/**
	 * Relative weights of the four lost-doll possibilities. "Below" deliberately dominates: SAME +
	 * PERCENT (a window around the caller's own percent, so effectively same-tier) + ABOVE sum to
	 * ~25% of the total, so a default 4-durability phone averages about ONE doll of the caller's
	 * rarity or better over its lifetime — the rest of its calls pay out a tier below.
	 */
	private static final double LOST_WEIGHT_SAME = 12;
	private static final double LOST_WEIGHT_BELOW = 75;
	private static final double LOST_WEIGHT_PERCENT = 10;
	private static final double LOST_WEIGHT_ABOVE = 3;

	/**
	 * Half-width of the {@link LostKind#PERCENT} window as a FRACTION of the caller's rarity percent.
	 * Proportional on purpose: a rarer caller (smaller percent) gets a narrower absolute window, so the
	 * window only ever pulls in dolls of a comparable rarity.
	 */
	private static final double LOST_PERCENT_RANGE_FRACTION = 0.35;

	/** Rarity tiers in ascending order, for stepping one level up/down the ladder. */
	private static final DollRarity[] RARITY_LADDER = {
			DollRarity.COMMON, DollRarity.UNCOMMON, DollRarity.RARE, DollRarity.EPIC,
			DollRarity.LEGENDARY, DollRarity.SHINY, DollRarity.GIGANTIC
	};

	/**
	 * How many rarity tiers above Common a rarity sits — Common=0, Uncommon=1, … Gigantic=6; 0 for NONE
	 * or an unknown tier. Used to scale a phone's dig-site and guarantee counts by its attuned doll's
	 * rarity (a Rare phone is two steps up, so it gets +2× the per-tier bonus).
	 */
	public static int rarityTierBonusSteps(DollRarity rarity) {
		for (int i = 0; i < RARITY_LADDER.length; i++) {
			if (RARITY_LADDER[i] == rarity) return i;
		}
		return 0;
	}

	/** The rarity {@code delta} steps up (+) or down (−) the ladder from {@code rarity}, or null off-ends. */
	public static DollRarity stepRarity(DollRarity rarity, int delta) {
		for (int i = 0; i < RARITY_LADDER.length; i++) {
			if (RARITY_LADDER[i] == rarity) {
				int j = i + delta;
				return (j >= 0 && j < RARITY_LADDER.length) ? RARITY_LADDER[j] : null;
			}
		}
		return null;
	}

	/**
	 * The buried-doll percent window for a "similarly rare" call: the caller's rarity percent scaled by
	 * ±{@value #LOST_PERCENT_RANGE_FRACTION}. Pure, so the scaling is unit-testable. {@code [min, max]}.
	 */
	public static double[] percentRange(double callerPercent) {
		double min = callerPercent * (1 - LOST_PERCENT_RANGE_FRACTION);
		double max = callerPercent * (1 + LOST_PERCENT_RANGE_FRACTION);
		return new double[]{Math.max(0, min), max};
	}

	/**
	 * The coloured "lost doll" descriptor shown in the call popup and the chat messages: either the
	 * rarity tier's name (e.g. "Rare"), or a {@code min–max rarity} rarity-percent window (e.g.
	 * "6.5%–13.5% rarity", phrased like the doll tooltips' rarity lore), tinted with the descriptor's
	 * rarity colour. Shared by client and server so both always read identically.
	 */
	public static Component describeLostDoll(LostDollTarget target) {
		ChatFormatting colour = target.rarity().getFormatting();
		String text = target.kind() == LostKind.RARITY
				? target.rarity().getDisplayName()
				: RarityScoreCalculator.formatChance(target.minPercent())
						+ "–" + RarityScoreCalculator.formatChance(target.maxPercent()) + " rarity";
		return Component.literal(text).withStyle(colour);
	}

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
	 * The shared candidate pool for buried dolls: every valid variant, using the same exclusions the
	 * loot pipeline applies (excluded flags/dolls from the config, no NONE-rarity or zero-weight variants).
	 */
	private static List<DollVariant> buildBuriedPool() {
		List<DollVariant> pool = new ArrayList<>();
		for (DollVariant variant : RarityScoreCalculator.computeAllVariants(PokeblocksConfig.getExcludedLootFlags())) {
			if (variant.rarity() == DollRarity.NONE) continue;
			if (variant.weight() <= 0) continue;
			if (PokeblocksConfig.isDollExcludedFromLoot(variant.pokemon(), variant.flags())) continue;
			pool.add(variant);
		}
		return pool;
	}

	/** A variant's rarity percent, on the same full-registry denominator the doll tooltips use. */
	private static double variantPercent(DollVariant variant) {
		double total = RarityScoreCalculator.totalVariantWeight();
		return total <= 0 ? 0 : variant.weight() / total * 100.0;
	}

	/** Whether a variant satisfies a lost-doll descriptor: same rarity tier, or inside the percent window. */
	private static boolean matchesTarget(DollVariant variant, LostDollTarget target) {
		if (target.kind() == LostKind.RARITY) {
			return variant.rarity() == target.rarity();
		}
		double percent = variantPercent(variant);
		return percent >= target.minPercent() && percent <= target.maxPercent();
	}

	/**
	 * Picks the doll the caller "lost" and buried: a rarity-weighted valid variant that MATCHES the
	 * announced {@code target} (its rarity tier, or its rarity-percent window). Falls back to the whole
	 * pool when nothing matches the descriptor (or {@code target} is null), so a quest is never starved
	 * of a buried doll. Null only when no valid doll exists at all.
	 */
	public static String pickBuriedDollKey(RandomSource random, LostDollTarget target) {
		List<DollVariant> pool = buildBuriedPool();
		if (pool.isEmpty()) return null;

		List<DollVariant> eligible = new ArrayList<>();
		if (target != null) {
			for (DollVariant variant : pool) {
				if (matchesTarget(variant, target)) {
					eligible.add(variant);
				}
			}
		}
		List<DollVariant> chosen = eligible.isEmpty() ? pool : eligible;

		DollVariant picked = pickWeighted(chosen, DollVariant::weight, random.nextDouble());
		return picked == null ? null : buildVariantKey(picked.pokemon(), picked.flags());
	}

	/** One lost-doll possibility with its selection weight. */
	private record WeightedTarget(LostDollTarget target, double weight) {}

	/**
	 * Chooses what the caller says they lost, given the caller's own variant key. Weighs the four
	 * possibilities — a doll of the SAME rarity, one rarity BELOW, a rarity-PERCENT window around the
	 * caller, and (rarely) one rarity ABOVE — but only keeps possibilities that actually have a matching
	 * doll in the pool, so the announcement is always fulfillable. Never null (falls back to the caller's
	 * own rarity, which {@link #pickBuriedDollKey} then satisfies from the whole pool if need be).
	 */
	public static LostDollTarget pickLostDoll(String callerKey, RandomSource random) {
		Variant caller = parseVariantKey(callerKey);
		DollRarity callerRarity = RarityScoreCalculator.resolvedRarity(caller.species(), caller.flags());
		double callerPercent = RarityScoreCalculator.computeChance(caller.species(), caller.flags());

		List<DollVariant> pool = buildBuriedPool();

		List<WeightedTarget> options = new ArrayList<>();
		addOption(options, LostDollTarget.rarity(callerRarity), LOST_WEIGHT_SAME, pool);
		DollRarity below = stepRarity(callerRarity, -1);
		if (below != null) addOption(options, LostDollTarget.rarity(below), LOST_WEIGHT_BELOW, pool);
		double[] range = percentRange(callerPercent);
		addOption(options, LostDollTarget.percent(callerRarity, range[0], range[1]), LOST_WEIGHT_PERCENT, pool);
		DollRarity above = stepRarity(callerRarity, 1);
		if (above != null) addOption(options, LostDollTarget.rarity(above), LOST_WEIGHT_ABOVE, pool);

		WeightedTarget picked = pickWeighted(options, WeightedTarget::weight, random.nextDouble());
		return picked == null ? LostDollTarget.rarity(callerRarity) : picked.target();
	}

	/** Adds a lost-doll option only if at least one pool variant matches it (so it's always fulfillable). */
	private static void addOption(List<WeightedTarget> options, LostDollTarget target, double weight, List<DollVariant> pool) {
		for (DollVariant variant : pool) {
			if (matchesTarget(variant, target)) {
				options.add(new WeightedTarget(target, weight));
				return;
			}
		}
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
		LostDollTarget lost = PokedollPhoneItem.getLostDoll(phone);
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

		if (!DigQuestManager.startQuest(player, caller, lost)) {
			player.displayClientMessage(Component.translatable("message.pokeblocks.phone.no_sites"), false);
			return;
		}

		// The accepted call started a quest — that's when the phone spends a durability point (and may
		// break). A call that fizzled for want of digging ground above costs nothing.
		PokedollPhoneItem.spendDurability(phone, player);
	}
}
