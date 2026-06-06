package dev.mrshawn.pokeblocks.recipe;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;

/**
 * A dynamic crafting recipe that converts four identical pokedolls arranged in a
 * plus pattern around any wool block into the gigantic version of that doll.
 *
 * <pre>
 *   .  D  .
 *   D  W  D
 *   .  D  .
 * </pre>
 *
 * <ul>
 *   <li>{@code D} — any non-gigantic pokedoll; all four must share the same species and flags.</li>
 *   <li>{@code W} — any wool block (any colour).</li>
 *   <li>Corner slots must be empty.</li>
 * </ul>
 *
 * <p>The result carries the same pokemon and flags as the inputs, with
 * {@link ModelFlag#GIGANTIC} added.
 *
 * <p>Registered as {@code pokeblocks:gigantic_doll}. The recipe JSON requires no
 * additional parameters:
 * <pre>{ "type": "pokeblocks:gigantic_doll" }</pre>
 */
public class GiganticDollRecipe implements CraftingRecipe {

    // Linear indices into a 3×3 CraftingInput (index = col + row * width, width = 3):
    //
    //   0  [1]  2
    //  [3] [4] [5]
    //   6  [7]  8
    //
    // [n] = slots used by this recipe; 0/2/6/8 are the corners (must be empty).
    private static final int TOP    = 1;
    private static final int LEFT   = 3;
    private static final int CENTER = 4;
    private static final int RIGHT  = 5;
    private static final int BOTTOM = 7;

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() < 3 || input.height() < 3) return false;

        // Corners must be empty
        if (!input.getItem(0).isEmpty()) return false;
        if (!input.getItem(2).isEmpty()) return false;
        if (!input.getItem(6).isEmpty()) return false;
        if (!input.getItem(8).isEmpty()) return false;

        // Center must be any wool
        ItemStack center = input.getItem(CENTER);
        if (center.isEmpty() || !center.is(ItemTags.WOOL)) return false;

        // Top doll anchors the species + flags; the other three must match it exactly
        ItemStack top = input.getItem(TOP);
        if (!isValidDoll(top)) return false;

        String pokemon = PokedollItem.getPokemonFromStack(top);
        Set<ModelFlag> flags = PokedollItem.getFlagsFromStack(top);

        return dollMatches(input.getItem(LEFT),   pokemon, flags)
            && dollMatches(input.getItem(RIGHT),  pokemon, flags)
            && dollMatches(input.getItem(BOTTOM), pokemon, flags);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack source = input.getItem(TOP);
        String pokemon = PokedollItem.getPokemonFromStack(source);
        Map<ModelFlag, Boolean> flags = PokedollItem.getFlagsMapFromStack(source);
        flags.put(ModelFlag.GIGANTIC, true);
        return PokedollItem.createPokedoll(pokemon, flags);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    /**
     * Static preview shown in the recipe book. Returns a Gigantic Substitute Pokedoll
     * as a representative output, since the actual result depends on the input species.
     */
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return PokedollItem.createPokedoll(ModSettings.DEFAULT_POKEMON, ModelFlag.GIGANTIC);
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PokeblocksRecipeSerializers.GIGANTIC_DOLL.get();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Returns true when the stack is a non-gigantic pokedoll. */
    private static boolean isValidDoll(ItemStack stack) {
        if (stack.isEmpty() || !stack.is(ItemRegistry.POKEDOLL_ITEM.get())) return false;
        return !PokedollItem.getFlagsFromStack(stack).contains(ModelFlag.GIGANTIC);
    }

    /** Returns true when the stack is a non-gigantic doll with the given species and flags. */
    private static boolean dollMatches(ItemStack stack, String pokemon, Set<ModelFlag> flags) {
        if (!isValidDoll(stack)) return false;
        return PokedollItem.getPokemonFromStack(stack).equalsIgnoreCase(pokemon)
            && PokedollItem.getFlagsFromStack(stack).equals(flags);
    }

    // ── Serializer ────────────────────────────────────────────────────────────

    /**
     * Trivial serializer — this recipe has no configurable parameters, so the codec
     * always encodes/decodes the same singleton instance.
     */
    public static class Serializer implements RecipeSerializer<GiganticDollRecipe> {

        private static final GiganticDollRecipe INSTANCE = new GiganticDollRecipe();

        public static final MapCodec<GiganticDollRecipe> CODEC = MapCodec.unit(INSTANCE);
        public static final StreamCodec<RegistryFriendlyByteBuf, GiganticDollRecipe> STREAM_CODEC =
                StreamCodec.unit(INSTANCE);

        @Override
        public MapCodec<GiganticDollRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GiganticDollRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
