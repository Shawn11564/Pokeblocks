package dev.mrshawn.pokeblocks.platform;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * Base service interface for the mod, handling distribution of all non-client platform-specific functionality
 */
public interface PokeblocksPlatform {
    <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> blockEntityType);
    <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block);
    <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item);
    <T extends CreativeModeTab> Supplier<T> registerCreativeModeTab(String id, Supplier<T> tab);

    CreativeModeTab.Builder newCreativeTabBuilder();

    // Optional registration helpers used by some platform implementations (default to throwing so platforms that don't implement them will fail fast if used)
    default <T extends Entity> Supplier<EntityType<T>> registerEntity(String id, Supplier<EntityType<T>> entity) {
        throw new UnsupportedOperationException("registerEntity not supported on this platform");
    }

    default <T extends ArmorMaterial> Holder<T> registerArmorMaterial(String id, Supplier<T> armorMaterial) {
        throw new UnsupportedOperationException("registerArmorMaterial not supported on this platform");
    }

    default <T extends SoundEvent> Supplier<T> registerSound(String id, Supplier<T> sound) {
        throw new UnsupportedOperationException("registerSound not supported on this platform");
    }

    default <E extends Mob> Supplier<SpawnEggItem> makeSpawnEggFor(Supplier<EntityType<E>> entityType, int primaryEggColour, int secondaryEggColour, Item.Properties itemProperties) {
        throw new UnsupportedOperationException("makeSpawnEggFor not supported on this platform");
    }
}
