package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.client.renderer.item.FigurineItemRenderer;
import dev.mrshawn.pokeblocks.client.renderer.item.PokedollItemRenderer;
import dev.mrshawn.pokeblocks.compendium.ClientCompendiumSync;
import dev.mrshawn.pokeblocks.compendium.CompendiumKind;
import dev.mrshawn.pokeblocks.compendium.CompendiumProgress;
import dev.mrshawn.pokeblocks.compendium.CompendiumVariantKey;
import dev.mrshawn.pokeblocks.item.FigurineDescriptionOverrides;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.FigurineRegistry;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * The two collections a compendium book can browse. Everything type-specific about the shared
 * {@link CompendiumScreen}/{@link CompendiumDetailScreen} pair — which stacks to list, how to
 * identify one, what counts as collected, variants, silhouette toggling, descriptions — lives
 * here, so the screens themselves stay collection-agnostic.
 * <p>
 * Two granularities of identity: {@link #idOf} is the index-level id (doll species / figurine id),
 * while {@link #progressKeyOf} is the exact progress key (doll species + flags / figurine id).
 * Progress stores keys; the index derives species-level "collected" from them.
 */
public enum CompendiumType {

	DOLLS(CompendiumKind.DOLL) {
		@Override
		public Component title() {
			return Component.translatable("item.pokeblocks.compendium");
		}

		@Override
		public List<ItemStack> entries() {
			List<ItemStack> entries = new ArrayList<>();
			// Alphabetical species order keeps page contents stable between opens. Each species is
			// represented by its least-flagged VALID variant — species like Combee have no base
			// texture (gender is mandatory), so a bare no-flag stack would render broken.
			for (String species : new TreeSet<>(PokemonRegistry.ALL_POKEMON.keySet())) {
				List<ItemStack> variants = variantsOf(species);
				entries.add(variants.isEmpty()
						? PokedollItem.createPokedoll(species, new EnumMap<>(ModelFlag.class))
						: variants.get(0));
			}
			return entries;
		}

		@Override
		public List<ItemStack> variantsOf(String id) {
			List<ItemStack> variants =
					PokedollItem.getAllMutations(id, PokemonRegistry.getPokemonData(id));
			variants.sort(VARIANT_ORDER);
			return variants;
		}

		@Override
		public String idOf(ItemStack stack) {
			return PokedollItem.getPokemonFromStack(stack).toLowerCase(Locale.ROOT);
		}

		@Override
		public String progressKeyOf(ItemStack stack) {
			return PokedollItem.compendiumKey(stack);
		}

		@Override
		boolean matches(ItemStack stack) {
			return stack.getItem() instanceof PokedollItem;
		}

		@Override
		public void setSilhouette(boolean silhouette) {
			PokedollItemRenderer.SILHOUETTE = silhouette;
		}

		@Override
		public boolean hasHideableBox() {
			return false;
		}

		@Override
		public Component lockedText() {
			return Component.translatable("screen.pokeblocks.compendium.locked.doll");
		}

		@Override
		public Component descriptionFor(String id) {
			return Component.translatable("screen.pokeblocks.compendium.description.doll");
		}
	},

	FIGURINES(CompendiumKind.FIGURINE) {
		@Override
		public Component title() {
			return Component.translatable("item.pokeblocks.figurine_compendium");
		}

		@Override
		public List<ItemStack> entries() {
			List<ItemStack> entries = new ArrayList<>();
			// Alphabetical id order keeps page contents stable between opens.
			for (String figurine : new TreeSet<>(FigurineRegistry.ALL_FIGURINES)) {
				entries.add(FigurineItem.createFigurine(figurine));
			}
			return entries;
		}

		@Override
		public List<ItemStack> variantsOf(String id) {
			return FigurineItem.getAllMutations(id);
		}

		@Override
		public String idOf(ItemStack stack) {
			return FigurineItem.getFigurineFromStack(stack).toLowerCase(Locale.ROOT);
		}

		@Override
		public String progressKeyOf(ItemStack stack) {
			return FigurineItem.compendiumKey(stack);
		}

		@Override
		boolean matches(ItemStack stack) {
			return stack.getItem() instanceof FigurineItem;
		}

		@Override
		public void setSilhouette(boolean silhouette) {
			FigurineItemRenderer.SILHOUETTE = silhouette;
		}

		@Override
		public boolean hasHideableBox() {
			return true;
		}

		@Override
		public Component lockedText() {
			return Component.translatable("screen.pokeblocks.compendium.locked.figurine");
		}

		@Override
		public Component descriptionFor(String id) {
			// Per-figurine blurbs come from figurine_descriptions.json (server-authoritative when
			// a served pack carries overrides); anything without one gets the generic line.
			String custom = FigurineDescriptionOverrides.getOverride(id);
			return custom != null
					? Component.literal(custom)
					: Component.translatable("screen.pokeblocks.compendium.description.figurine");
		}
	};

	/**
	 * Orders a species' valid doll variants for display and representative-picking: plain forms
	 * first, then shiny, then gigantic, fewer flags before more, canonical key last so the order
	 * is fully deterministic.
	 */
	private static final Comparator<ItemStack> VARIANT_ORDER = Comparator
			.comparing((ItemStack stack) -> PokedollItem.getFlagsFromStack(stack).contains(ModelFlag.GIGANTIC))
			.thenComparing(stack -> PokedollItem.getFlagsFromStack(stack).contains(ModelFlag.SHINY))
			.thenComparing(stack -> PokedollItem.getFlagsFromStack(stack).size())
			.thenComparing(PokedollItem::compendiumKey);

	private final CompendiumKind kind;

	CompendiumType(CompendiumKind kind) {
		this.kind = kind;
	}

	public abstract Component title();

	/** One representative stack per registered entry, in stable display order. */
	public abstract List<ItemStack> entries();

	/** All valid variant stacks of one entry, display-ordered; empty for variant-less collections. */
	public abstract List<ItemStack> variantsOf(String id);

	/** The lower-case index-level id of a stack (doll species / figurine id, flags ignored). */
	public abstract String idOf(ItemStack stack);

	/** The exact progress key of a stack (doll species + flags / figurine id). */
	public abstract String progressKeyOf(ItemStack stack);

	/** Whether an inventory stack belongs to this collection. */
	abstract boolean matches(ItemStack stack);

	/** Flips the shared item renderer between textured and solid-silhouette drawing. */
	public abstract void setSilhouette(boolean silhouette);

	/** Whether this collection's models have a display case the detail page can hide. */
	public abstract boolean hasHideableBox();

	/** Right-page body text for an entry the player hasn't discovered. */
	public abstract Component lockedText();

	/** Right-page body text for a discovered entry. */
	public abstract Component descriptionFor(String id);

	/**
	 * Every exact progress key the player has: the server-synced discovery snapshot (when one is
	 * live for this connection) plus keys derived from the inventory right now. The scan keeps the
	 * book useful on servers too old to send the payload and fills the sync round-trip gap.
	 */
	public Set<String> collectedKeys(Player player) {
		Set<String> keys = CompendiumCollection.fromInventory(player, this);
		CompendiumProgress synced = ClientCompendiumSync.current();
		if (synced != null) {
			keys.addAll(synced.ids(kind));
		}
		return keys;
	}

	/** Index-level collected ids derived from {@link #collectedKeys}: any variant counts. */
	public Set<String> collectedIds(Player player) {
		Set<String> ids = new HashSet<>();
		for (String key : collectedKeys(player)) {
			ids.add(CompendiumVariantKey.speciesOf(key));
		}
		return ids;
	}
}
