package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

	public static BlockEntityType<AnimatedBlockEntity> ANIMATED_BLOCK_ENTITY;

	public static void registerAllBlockEntities() {
		ANIMATED_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
				new Identifier(Pokeblocks.MOD_ID, "pokedoll_calyrex"),
				FabricBlockEntityTypeBuilder.create(AnimatedBlockEntity::new,
								ModBlocks.POKEDOLL_CALYREX).build());
	}

}
