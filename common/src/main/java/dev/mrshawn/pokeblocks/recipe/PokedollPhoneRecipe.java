package dev.mrshawn.pokeblocks.recipe;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollPhoneItem;
import dev.mrshawn.pokeblocks.phone.PhoneCalls;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
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

import java.util.Set;

/**
 * A dynamic crafting recipe that builds a Pokedoll Phone ATTUNED to whichever doll is placed in the
 * centre. The attuned phone only ever rings from that one doll (see {@link PokedollPhoneItem}).
 *
 * <pre>
 *   C  C  C
 *   C  D  C
 *   C  R  C
 * </pre>
 *
 * <ul>
 *   <li>{@code C} — copper ingot (the eight edge slots except bottom-centre).</li>
 *   <li>{@code D} — any pokedoll; the phone attunes to this exact species + flags.</li>
 *   <li>{@code R} — redstone (bottom centre).</li>
 * </ul>
 *
 * <p>Registered as {@code pokeblocks:pokedoll_phone}. The recipe JSON needs no parameters:
 * <pre>{ "type": "pokeblocks:pokedoll_phone" }</pre>
 * A vanilla shaped recipe can't copy the doll's variant onto the result, so this reproduces the fixed
 * shape in code (the same approach as {@link GiganticDollRecipe}).
 */
public class PokedollPhoneRecipe implements CraftingRecipe {

    // Linear indices into a 3×3 CraftingInput (index = col + row * width, width = 3):
    //
    //   0   1   2       copper  copper  copper
    //   3   4   5   =   copper  DOLL    copper
    //   6   7   8       copper  REDST.  copper
    private static final int CENTER = 4;
    private static final int REDSTONE = 7;
    // The seven copper slots (everything except the centre doll and the bottom-centre redstone).
    private static final int[] COPPER_SLOTS = {0, 1, 2, 3, 5, 6, 8};

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() < 3 || input.height() < 3) return false;

        for (int slot : COPPER_SLOTS) {
            if (!input.getItem(slot).is(Items.COPPER_INGOT)) return false;
        }
        if (!input.getItem(REDSTONE).is(Items.REDSTONE)) return false;

        return input.getItem(CENTER).is(ItemRegistry.POKEDOLL_ITEM.get());
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack doll = input.getItem(CENTER);
        String pokemon = PokedollItem.getPokemonFromStack(doll);
        Set<ModelFlag> flags = PokedollItem.getFlagsFromStack(doll);
        return PokedollPhoneItem.createAttuned(PhoneCalls.buildVariantKey(pokemon, flags));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    /**
     * Static preview shown in the recipe book. Returns a phone attuned to the default Substitute doll
     * as a representative output, since the real attunement depends on the doll placed in the centre.
     */
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return PokedollPhoneItem.createAttuned(ModSettings.DEFAULT_POKEMON);
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PokeblocksRecipeSerializers.POKEDOLL_PHONE.get();
    }

    // ── Serializer ────────────────────────────────────────────────────────────

    /** Trivial serializer — the recipe has no configurable parameters, so it encodes a singleton. */
    public static class Serializer implements RecipeSerializer<PokedollPhoneRecipe> {

        private static final PokedollPhoneRecipe INSTANCE = new PokedollPhoneRecipe();

        public static final MapCodec<PokedollPhoneRecipe> CODEC = MapCodec.unit(INSTANCE);
        public static final StreamCodec<RegistryFriendlyByteBuf, PokedollPhoneRecipe> STREAM_CODEC =
                StreamCodec.unit(INSTANCE);

        @Override
        public MapCodec<PokedollPhoneRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PokedollPhoneRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
