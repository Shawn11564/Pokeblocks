package com.example.examplemod.item;

import com.example.examplemod.client.renderer.item.PokedollItemRenderer;
import com.example.examplemod.constants.ModSettings;
import com.example.examplemod.pokemon.ModelFlag;
import com.example.examplemod.pokemon.PokemonData;
import com.example.examplemod.registry.ItemRegistry;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;

public class PokedollItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PokedollItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private PokedollItemRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new PokedollItemRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * Gets the pokemon name from the item's NBT data
     */
    public static String getPokemonFromStack(ItemStack stack) {
        CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null) {
            CompoundTag tag = blockEntityData.copyTag();
            if (tag.contains("pokemon")) {
                String pokemon = tag.getString("pokemon");
                return pokemon.isEmpty() ? ModSettings.DEFAULT_POKEMON : pokemon;
            }
        }
        return ModSettings.DEFAULT_POKEMON;
    }

    /**
     * Gets the animated flag from the item's NBT data
     */
    public static boolean isAnimatedFromStack(ItemStack stack) {
        CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null) {
            CompoundTag tag = blockEntityData.copyTag();
            if (tag.contains("animated")) {
                return tag.getBoolean("animated");
            }
        }
        return false;
    }

    /**
     * Reads all ModelFlag values from the item's BLOCK_ENTITY_DATA CompoundTag.
     */
    public static Set<ModelFlag> getFlagsFromStack(ItemStack stack) {
        EnumSet<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
        CustomData blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null) {
            CompoundTag tag = blockEntityData.copyTag();
            for (ModelFlag flag : ModelFlag.values()) {
                if (tag.contains(flag.getTagName()) && tag.getBoolean(flag.getTagName())) {
                    flags.add(flag);
                }
            }
        }
        return flags;
    }

    /**
     * Helper method to create a pokedoll item with specific pokemon and flags
     */
    public static ItemStack createPokedoll(String pokemon, boolean animated) {
        ItemStack stack = new ItemStack(ItemRegistry.POKEDOLL_ITEM.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("pokemon", pokemon);
        tag.putBoolean("animated", animated);
        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
        return stack;
    }

    /**
     * Create a pokedoll with PokemonData and optional flags written to NBT.
     */
    public static ItemStack createPokedoll(PokemonData pokemonData) {
        String name = ModSettings.DEFAULT_POKEMON;
        if (pokemonData != null) {
            // pokemonData is just flags map; use default name unless registry has single entry
            name = ModSettings.DEFAULT_POKEMON;
        }
        return createPokedoll(name, pokemonData);
    }

    public static ItemStack createPokedoll(String name, PokemonData pokemonData) {
        ItemStack stack = new ItemStack(ItemRegistry.POKEDOLL_ITEM.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("pokemon", name == null || name.isEmpty() ? ModSettings.DEFAULT_POKEMON : name);

        if (pokemonData != null && pokemonData.modelFlags() != null) {
            for (var entry : pokemonData.modelFlags().entrySet()) {
                if (Boolean.TRUE.equals(entry.getValue())) {
                    tag.putBoolean(entry.getKey().getTagName(), true);
                }
            }
        }

        stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
        return stack;
    }
}