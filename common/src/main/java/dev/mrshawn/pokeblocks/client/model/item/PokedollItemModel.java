package dev.mrshawn.pokeblocks.client.model.item;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.client.renderer.animation.AnimationResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.pokemon.PokemonData;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.*;

public class PokedollItemModel extends GeoModel<PokedollItem> {
    private static final Set<String> validatedPokemon = new HashSet<>();

    private ItemStack currentStack = ItemStack.EMPTY;

    public PokedollItemModel() {
        validatedPokemon.add(ModSettings.DEFAULT_POKEMON);
    }

    public void setCurrentItemStack(ItemStack stack) {
        this.currentStack = stack;
    }

    private String getValidatedPokemon(String pokemon) {
        if (pokemon == null || pokemon.isEmpty()) {
            return ModSettings.DEFAULT_POKEMON;
        }

        if (validatedPokemon.contains(pokemon)) {
            return pokemon;
        }

        ResourceLocation modelPath = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "geo/block/pokedoll_" + pokemon + ".geo.json"
        );

        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            if (resourceManager.getResource(modelPath).isPresent()) {
                validatedPokemon.add(pokemon);
                return pokemon;
            }
        } catch (Exception e) {}

        return ModSettings.DEFAULT_POKEMON;
    }

    private String computeSuffixes() {
        if (this.currentStack == null || this.currentStack.isEmpty()) return "";
        Set<ModelFlag> flags = PokedollItem.getFlagsFromStack(this.currentStack);

        List<ModelFlag> activeFlags = new ArrayList<>();
        for (ModelFlag flag : flags) {
            if (!flag.getModelSuffix().isEmpty() || !flag.getTextureSuffix().isEmpty()) {
                activeFlags.add(flag);
            }
        }

        activeFlags.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

        StringBuilder modelSuffix = new StringBuilder();
        StringBuilder textureSuffix = new StringBuilder();

        for (ModelFlag flag : activeFlags) {
            modelSuffix.append(flag.getModelSuffix());
            textureSuffix.append(flag.getTextureSuffix());
        }

        return modelSuffix + "|" + textureSuffix;
    }

    private String getPokemonFromCurrent() {
        if (!currentStack.isEmpty()) {
            return getValidatedPokemon(PokedollItem.getPokemonFromStack(currentStack));
        }
        return ModSettings.DEFAULT_POKEMON;
    }

    @Override
    public BakedGeoModel getBakedModel(ResourceLocation location) {
        try {
            return super.getBakedModel(location);
        } catch (RuntimeException e) {
            ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath(
                    PokeblocksCommon.MOD_ID,
                    "geo/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + ".geo.json"
            );
            return super.getBakedModel(fallback);
        }
    }

    @Override
    public ResourceLocation getModelResource(PokedollItem animatable) {
        String pokemon = getPokemonFromCurrent();
        String suffixes = computeSuffixes();
        String modelSuffix = "";
        if (suffixes.contains("|")) modelSuffix = suffixes.split("\\|", 2)[0];

        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        // Try with full suffix
        ResourceLocation full = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "geo/block/pokedoll_" + pokemon + modelSuffix + ".geo.json"
        );
        try {
            if (rm.getResource(full).isPresent()) return full;
        } catch (Exception e) {}

        // Fall back to base model
        return ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "geo/block/pokedoll_" + pokemon + ".geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(PokedollItem animatable) {
        String pokemon = getPokemonFromCurrent();
        String suffixes = computeSuffixes();
        String textureSuffix = "";
        if (suffixes.contains("|")) textureSuffix = suffixes.split("\\|", 2)[1];

        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        // Try: pokedoll_<name><suffix>_texture.png
        ResourceLocation suffixThenTexture = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/pokedoll_" + pokemon + textureSuffix + "_texture.png"
        );
        try {
            if (rm.getResource(suffixThenTexture).isPresent()) return suffixThenTexture;
        } catch (Exception e) {}

        // Try: pokedoll_<name>_texture<suffix>.png
        ResourceLocation textureThenSuffix = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/pokedoll_" + pokemon + "_texture" + textureSuffix + ".png"
        );
        try {
            if (rm.getResource(textureThenSuffix).isPresent()) return textureThenSuffix;
        } catch (Exception e) {}

        // Try: pokedoll_<name><suffix>.png
        ResourceLocation withSuffix = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/pokedoll_" + pokemon + textureSuffix + ".png"
        );
        try {
            if (rm.getResource(withSuffix).isPresent()) return withSuffix;
        } catch (Exception e) {}

        // Try: pokedoll_<name>_texture.png
        ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/pokedoll_" + pokemon + "_texture.png"
        );
        try {
            if (rm.getResource(baseTexture).isPresent()) return baseTexture;
        } catch (Exception e) {}

        // Try: pokedoll_<name>.png
        ResourceLocation plain = ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/pokedoll_" + pokemon + ".png"
        );
        try {
            if (rm.getResource(plain).isPresent()) return plain;
        } catch (Exception e) {}

        // Fall back to default
        return ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "textures/block/pokedoll_" + ModSettings.DEFAULT_POKEMON + "_texture.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(PokedollItem animatable) {
        String pokemon = getPokemonFromCurrent();
        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        PokemonData data = PokemonRegistry.getPokemonData(pokemon);
        if (data == null) {
            return ResourceLocation.fromNamespaceAndPath(
                    PokeblocksCommon.MOD_ID,
                    "animations/block/empty.animation.json"
            );
        }

        // Build flags map from item stack
        Set<ModelFlag> stackFlags = PokedollItem.getFlagsFromStack(currentStack);
        Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
        for (ModelFlag flag : ModelFlag.values()) {
            flagMap.put(flag, stackFlags.contains(flag));
        }

        AnimationResolver.AnimationType type = AnimationResolver.resolve(pokemon, flagMap, data.animationProfile());

        if (type == AnimationResolver.AnimationType.VARIANT) {
            String suffixes = computeSuffixes();
            String modelSuffix = "";
            if (suffixes.contains("|")) modelSuffix = suffixes.split("\\|", 2)[0];

            ResourceLocation variantAnim = ResourceLocation.fromNamespaceAndPath(
                    PokeblocksCommon.MOD_ID,
                    "animations/block/pokedoll_" + pokemon + modelSuffix + ".animation.json"
            );
            try {
                if (rm.getResource(variantAnim).isPresent()) return variantAnim;
            } catch (Exception ignored) {}
        }

        if (type == AnimationResolver.AnimationType.BASE || type == AnimationResolver.AnimationType.VARIANT) {
            ResourceLocation baseAnim = ResourceLocation.fromNamespaceAndPath(
                    PokeblocksCommon.MOD_ID,
                    "animations/block/pokedoll_" + pokemon + ".animation.json"
            );
            try {
                if (rm.getResource(baseAnim).isPresent()) return baseAnim;
            } catch (Exception ignored) {}
        }

        return ResourceLocation.fromNamespaceAndPath(
                PokeblocksCommon.MOD_ID,
                "animations/block/empty.animation.json"
        );
    }

    @Override
    public RenderType getRenderType(PokedollItem animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}