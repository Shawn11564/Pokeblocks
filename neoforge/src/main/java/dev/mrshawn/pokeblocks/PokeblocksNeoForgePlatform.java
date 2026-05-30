package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.platform.PokeblocksPlatform;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class PokeblocksNeoForgePlatform implements PokeblocksPlatform {
    @Override
    public <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> blockEntityType) {
        return PokeblocksNeoForge.BLOCK_ENTITIES.register(id, blockEntityType);
    }

    @Override
    public <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, Supplier<EntityType<T>> entity) {
        return PokeblocksNeoForge.ENTITIES.register(id, entity);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
        return PokeblocksNeoForge.BLOCKS.register(id, block);
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return PokeblocksNeoForge.ITEMS.register(id, item);
    }

    @Override
    public <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab) {
        return PokeblocksNeoForge.CREATIVE_TABS.register(id, tab);
    }

    @Override
    public <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
        return PokeblocksNeoForge.SOUND_EVENTS.register(id, sound);
    }

    @Override
    public <T extends RecipeSerializer<?>> Supplier<T> registerRecipeSerializer(String id, Supplier<T> serializer) {
        return PokeblocksNeoForge.RECIPE_SERIALIZERS.register(id, serializer);
    }

    @Override
    public CreativeModeTab.Builder newCreativeTabBuilder() {
        return CreativeModeTab.builder();
    }
}
