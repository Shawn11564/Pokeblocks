package dev.mrshawn.pokeblocks.recipe;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.TrappedDolls;
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
 * Shapeless recipe that combines any pokedoll with a single TNT into the trapped version of that
 * exact doll: the same species and flags, plus the {@link TrappedDolls} marker. A trapped doll
 * placed and popped explodes with block damage; thrown (craft it with a snowball too — the two
 * markers stack in either crafting order) it arms a detonation countdown on whoever it hits. See
 * {@link TrappedDolls} for the full behaviour. Already-trapped dolls don't match again, so TNT
 * can't be crafted into nothing.
 *
 * <p>Registered as {@code pokeblocks:trapped_doll}; the JSON needs no parameters:
 * <pre>{ "type": "pokeblocks:trapped_doll" }</pre>
 */
public class TrappedDollRecipe implements CraftingRecipe {

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack doll = ItemStack.EMPTY;
		ItemStack tnt = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;

			if (stack.is(ItemRegistry.POKEDOLL_ITEM.get())) {
				if (!doll.isEmpty()) return false; // at most one doll
				doll = stack;
			} else if (stack.is(Items.TNT)) {
				if (!tnt.isEmpty()) return false; // at most one tnt
				tnt = stack;
			} else {
				return false; // anything else disqualifies the recipe
			}
		}

		return !doll.isEmpty() && !tnt.isEmpty() && !TrappedDolls.isTrapped(doll);
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.is(ItemRegistry.POKEDOLL_ITEM.get())) {
				return TrappedDolls.makeTrapped(stack);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	/**
	 * Static preview shown in the recipe book. Returns a trapped Substitute Pokedoll as a
	 * representative output, since the actual result depends on the input doll.
	 */
	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return TrappedDolls.makeTrapped(PokedollItem.createPokedoll(ModSettings.DEFAULT_POKEMON));
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PokeblocksRecipeSerializers.TRAPPED_DOLL.get();
	}

	// ── Serializer ────────────────────────────────────────────────────────────

	/** Trivial serializer — the recipe has no configurable parameters, so it encodes a singleton. */
	public static class Serializer implements RecipeSerializer<TrappedDollRecipe> {

		private static final TrappedDollRecipe INSTANCE = new TrappedDollRecipe();

		public static final MapCodec<TrappedDollRecipe> CODEC = MapCodec.unit(INSTANCE);
		public static final StreamCodec<RegistryFriendlyByteBuf, TrappedDollRecipe> STREAM_CODEC =
				StreamCodec.unit(INSTANCE);

		@Override
		public MapCodec<TrappedDollRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, TrappedDollRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
