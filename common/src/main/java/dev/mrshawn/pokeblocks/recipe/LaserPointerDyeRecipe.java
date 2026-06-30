package dev.mrshawn.pokeblocks.recipe;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.item.custom.LaserPointerItem;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Shapeless recipe that recolours a {@link LaserPointerItem}'s beam by combining the pointer with a single
 * dye in a crafting grid. The dye's standard colour is written into the laser's vanilla
 * {@link DyedItemColor} component (all other components are preserved); {@link LaserPointerItem#getColor}
 * reads it back to colour the beam.
 *
 * <p>Registered as {@code pokeblocks:laser_dye}; the JSON needs no parameters:
 * <pre>{ "type": "pokeblocks:laser_dye" }</pre>
 */
public class LaserPointerDyeRecipe implements CraftingRecipe {

	@Override
	public boolean matches(CraftingInput input, Level level) {
		ItemStack laser = ItemStack.EMPTY;
		ItemStack dye = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;

			if (stack.getItem() instanceof LaserPointerItem) {
				if (!laser.isEmpty()) return false; // at most one pointer
				laser = stack;
			} else if (stack.getItem() instanceof DyeItem) {
				if (!dye.isEmpty()) return false; // at most one dye
				dye = stack;
			} else {
				return false; // anything else disqualifies the recipe
			}
		}

		return !laser.isEmpty() && !dye.isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		ItemStack laser = ItemStack.EMPTY;
		DyeItem dye = null;

		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty()) continue;
			if (stack.getItem() instanceof LaserPointerItem) {
				laser = stack;
			} else if (stack.getItem() instanceof DyeItem dyeItem) {
				dye = dyeItem;
			}
		}

		if (laser.isEmpty() || dye == null) return ItemStack.EMPTY;

		ItemStack result = laser.copyWithCount(1);
		int rgb = dye.getDyeColor().getTextureDiffuseColor() & 0xFFFFFF;
		result.set(DataComponents.DYED_COLOR, new DyedItemColor(rgb, false));
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		ItemStack stack = new ItemStack(ItemRegistry.LASER_POINTER_ITEM.get());
		stack.set(DataComponents.DYED_COLOR, new DyedItemColor(LaserPointerItem.DEFAULT_COLOR, false));
		return stack;
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PokeblocksRecipeSerializers.LASER_DYE.get();
	}

	// ── Serializer ────────────────────────────────────────────────────────────

	/** Trivial serializer — the recipe has no configurable parameters, so it encodes a singleton. */
	public static class Serializer implements RecipeSerializer<LaserPointerDyeRecipe> {

		private static final LaserPointerDyeRecipe INSTANCE = new LaserPointerDyeRecipe();

		public static final MapCodec<LaserPointerDyeRecipe> CODEC = MapCodec.unit(INSTANCE);
		public static final StreamCodec<RegistryFriendlyByteBuf, LaserPointerDyeRecipe> STREAM_CODEC =
				StreamCodec.unit(INSTANCE);

		@Override
		public MapCodec<LaserPointerDyeRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, LaserPointerDyeRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
