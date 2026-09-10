package dev.mrshawn.pokeblocks.integration.jei;

import dev.mrshawn.pokeblocks.item.custom.CustomDecorationItem;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * JEI subtype interpreters for the component-based Pokeblocks items. Each maps a stack to its
 * {@link JeiVariantKeys canonical variant key}, so every valid variant of a doll, figurine or
 * decoration is a distinct JEI entry (and searchable by its own name), while transient state on the
 * same variant collapses back onto that entry.
 */
final class PokeblocksSubtypes {

	private PokeblocksSubtypes() {}

	/** Pokedolls: species + variant flags. */
	static final ISubtypeInterpreter<ItemStack> POKEDOLL = new KeyInterpreter(stack ->
			JeiVariantKeys.doll(PokedollItem.getPokemonFromStack(stack), PokedollItem.getFlagsFromStack(stack)));

	/** Figurines: base id + figurine flags (e.g. devoured), boxed vs. boxless doll form. */
	static final ISubtypeInterpreter<ItemStack> FIGURINE = new KeyInterpreter(stack ->
			JeiVariantKeys.figurine(FigurineItem.getFigurineFromStack(stack),
					FigurineItem.getFigurineFlagsFromStack(stack), FigurineItem.isBoxless(stack)));

	/** Registered decorations (baskets, cushions, head piles, …): the flag variant only. */
	static final ISubtypeInterpreter<ItemStack> DECORATIVE = new KeyInterpreter(stack ->
			JeiVariantKeys.decorative(DecorativeItem.getFlagsFromStack(stack)));

	/** Generic data-driven decorations: the decoration id. */
	static final ISubtypeInterpreter<ItemStack> CUSTOM_DECORATION = new KeyInterpreter(stack ->
			JeiVariantKeys.customDecoration(CustomDecorationItem.getDecorationFromStack(stack)));

	/** The same key serves both the modern object-based and the legacy string-based subtype APIs. */
	private record KeyInterpreter(Function<ItemStack, String> key) implements ISubtypeInterpreter<ItemStack> {
		@Override
		public Object getSubtypeData(ItemStack stack, UidContext context) {
			return key.apply(stack);
		}

		@Override
		public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
			return key.apply(stack);
		}
	}
}
