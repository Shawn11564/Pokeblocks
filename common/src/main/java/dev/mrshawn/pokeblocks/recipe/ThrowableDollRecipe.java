package dev.mrshawn.pokeblocks.recipe;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.ThrowableDolls;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Shapeless recipe that combines any pokedoll with a single snowball into the throwable version of
 * that exact doll: the same species and flags, plus the {@link ThrowableDolls} marker. Right-clicking
 * the result throws it like a snowball (see
 * {@link dev.mrshawn.pokeblocks.entity.custom.ThrownPokedollEntity} for what happens on impact).
 * Already-throwable dolls don't match again, so a snowball can't be crafted into nothing.
 *
 * <p>Registered as {@code pokeblocks:throwable_doll}; the JSON needs no parameters:
 * <pre>{ "type": "pokeblocks:throwable_doll" }</pre>
 */
public class ThrowableDollRecipe implements CraftingRecipe {

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack doll = ItemStack.EMPTY;
		ItemStack snowball = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;

			if (stack.is(ItemRegistry.POKEDOLL_ITEM.get())) {
				if (!doll.isEmpty()) return false; // at most one doll
				doll = stack;
			} else if (stack.is(Items.SNOWBALL)) {
				if (!snowball.isEmpty()) return false; // at most one snowball
				snowball = stack;
			} else {
				return false; // anything else disqualifies the recipe
			}
		}

		return !doll.isEmpty() && !snowball.isEmpty() && !ThrowableDolls.isThrowable(doll);
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.is(ItemRegistry.POKEDOLL_ITEM.get())) {
				return ThrowableDolls.makeThrowable(stack);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	/**
	 * Static preview shown in the recipe book. Returns a throwable Substitute Pokedoll as a
	 * representative output, since the actual result depends on the input doll.
	 */
	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return ThrowableDolls.makeThrowable(PokedollItem.createPokedoll(ModSettings.DEFAULT_POKEMON));
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PokeblocksRecipeSerializers.THROWABLE_DOLL.get();
	}

	// ── Serializer ────────────────────────────────────────────────────────────

	/** Trivial serializer — the recipe has no configurable parameters, so it encodes a singleton. */
	public static class Serializer implements RecipeSerializer<ThrowableDollRecipe> {

		private static final ThrowableDollRecipe INSTANCE = new ThrowableDollRecipe();

		public static final MapCodec<ThrowableDollRecipe> CODEC = MapCodec.unit(INSTANCE);
		public static final StreamCodec<RegistryFriendlyByteBuf, ThrowableDollRecipe> STREAM_CODEC =
				StreamCodec.unit(INSTANCE);

		@Override
		public MapCodec<ThrowableDollRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, ThrowableDollRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
