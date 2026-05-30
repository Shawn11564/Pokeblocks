package dev.mrshawn.pokeblocks.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A shaped crafting recipe that understands Pokeblocks doll ingredients and component-based
 * results (dolls / figurines / decorations) in addition to ordinary items and tags.
 * <p>
 * It is registered under {@link net.minecraft.world.item.crafting.RecipeType#CRAFTING} (via the
 * {@link CraftingRecipe} default) with a custom serializer, so it behaves exactly like a vanilla
 * shaped recipe in the crafting table and recipe book. Matching mirrors vanilla shaped behaviour:
 * the pattern may sit anywhere in the grid and may be horizontally mirrored.
 *
 * @see PokeblocksIngredient
 * @see PokeblocksRecipeResult
 */
public class PokeblocksShapedRecipe implements CraftingRecipe {

	private final CraftingBookCategory category;
	private final List<String> pattern;
	private final Map<String, PokeblocksIngredient> key;
	private final PokeblocksRecipeResult result;

	private final int width;
	private final int height;
	/** Row-major grid of ingredients; {@code null} marks an empty slot (a space in the pattern). */
	private final PokeblocksIngredient[] grid;

	public PokeblocksShapedRecipe(CraftingBookCategory category, List<String> pattern, Map<String, PokeblocksIngredient> key, PokeblocksRecipeResult result) {
		this.category = category;
		this.pattern = pattern;
		this.key = key;
		this.result = result;

		// Trim to the bounding box of non-space cells, mirroring vanilla shaped recipes, so patterns
		// with leading/trailing padding match against the (cropped) crafting grid.
		int minRow = Integer.MAX_VALUE, maxRow = -1, minCol = Integer.MAX_VALUE, maxCol = -1;
		for (int row = 0; row < pattern.size(); row++) {
			String line = pattern.get(row);
			for (int col = 0; col < line.length(); col++) {
				if (line.charAt(col) != ' ') {
					minRow = Math.min(minRow, row);
					maxRow = Math.max(maxRow, row);
					minCol = Math.min(minCol, col);
					maxCol = Math.max(maxCol, col);
				}
			}
		}

		if (maxRow < 0) {
			this.width = 0;
			this.height = 0;
			this.grid = new PokeblocksIngredient[0];
			return;
		}

		this.height = maxRow - minRow + 1;
		this.width = maxCol - minCol + 1;
		this.grid = new PokeblocksIngredient[width * height];
		for (int row = 0; row < height; row++) {
			String line = pattern.get(minRow + row);
			for (int col = 0; col < width; col++) {
				int srcCol = minCol + col;
				char ch = srcCol < line.length() ? line.charAt(srcCol) : ' ';
				if (ch == ' ') continue;
				PokeblocksIngredient ingredient = key.get(String.valueOf(ch));
				if (ingredient == null) {
					throw new IllegalArgumentException("Pattern references symbol '" + ch + "' which is missing from the recipe key");
				}
				grid[col + row * width] = ingredient;
			}
		}
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		if (width == 0 || height == 0) return false;
		for (int startX = 0; startX <= input.width() - width; startX++) {
			for (int startY = 0; startY <= input.height() - height; startY++) {
				if (matchesAt(input, startX, startY, true) || matchesAt(input, startX, startY, false)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean matchesAt(CraftingInput input, int startX, int startY, boolean mirrored) {
		for (int col = 0; col < input.width(); col++) {
			for (int row = 0; row < input.height(); row++) {
				int relX = col - startX;
				int relY = row - startY;
				PokeblocksIngredient expected = null;
				if (relX >= 0 && relX < width && relY >= 0 && relY < height) {
					int gridX = mirrored ? (width - 1 - relX) : relX;
					expected = grid[gridX + relY * width];
				}
				ItemStack stack = input.getItem(col + row * input.width());
				if (expected == null) {
					if (!stack.isEmpty()) return false;
				} else if (!expected.test(stack)) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		return result.toStack();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width >= this.width && height >= this.height;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries) {
		return result.toStack();
	}

	@Override
	public CraftingBookCategory category() {
		return category;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PokeblocksRecipeSerializers.SHAPED.get();
	}

	public static class Serializer implements RecipeSerializer<PokeblocksShapedRecipe> {

		public static final MapCodec<PokeblocksShapedRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
				Codec.STRING.listOf().fieldOf("pattern").forGetter(recipe -> recipe.pattern),
				Codec.unboundedMap(Codec.STRING, PokeblocksIngredient.CODEC).fieldOf("key").forGetter(recipe -> recipe.key),
				PokeblocksRecipeResult.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
		).apply(instance, PokeblocksShapedRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, PokeblocksShapedRecipe> STREAM_CODEC =
				StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

		@Override
		public MapCodec<PokeblocksShapedRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, PokeblocksShapedRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		private static void toNetwork(RegistryFriendlyByteBuf buf, PokeblocksShapedRecipe recipe) {
			buf.writeEnum(recipe.category);
			buf.writeVarInt(recipe.pattern.size());
			for (String row : recipe.pattern) {
				buf.writeUtf(row);
			}
			buf.writeVarInt(recipe.key.size());
			for (Map.Entry<String, PokeblocksIngredient> entry : recipe.key.entrySet()) {
				buf.writeUtf(entry.getKey());
				PokeblocksIngredient.STREAM_CODEC.encode(buf, entry.getValue());
			}
			PokeblocksRecipeResult.STREAM_CODEC.encode(buf, recipe.result);
		}

		private static PokeblocksShapedRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
			CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
			int rows = buf.readVarInt();
			List<String> pattern = new ArrayList<>(rows);
			for (int i = 0; i < rows; i++) {
				pattern.add(buf.readUtf());
			}
			int keyCount = buf.readVarInt();
			Map<String, PokeblocksIngredient> key = new LinkedHashMap<>();
			for (int i = 0; i < keyCount; i++) {
				String symbol = buf.readUtf();
				key.put(symbol, PokeblocksIngredient.STREAM_CODEC.decode(buf));
			}
			PokeblocksRecipeResult result = PokeblocksRecipeResult.STREAM_CODEC.decode(buf);
			return new PokeblocksShapedRecipe(category, pattern, key, result);
		}
	}
}
