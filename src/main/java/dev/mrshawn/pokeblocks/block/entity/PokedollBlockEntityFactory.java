package dev.mrshawn.pokeblocks.block.entity;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.client.ScalablePokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PokedollBlockEntityFactory {

	// For regular Pokédolls
	public static BlockEntityType<PokedollBlockEntity> create(
			String id, Block block,
			String modelPath, String texturePath,
			String animationPath, String animationName) {

		return Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				Identifier.of(Pokeblocks.MOD_ID, id),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new PokedollBlockEntity(
								Registries.BLOCK_ENTITY_TYPE.get(Identifier.of(Pokeblocks.MOD_ID, id)),
								pos, state, modelPath, texturePath, animationPath, animationName
						),
						block
				).build()
		);
	}

	public static BlockEntityType<PokedollBlockEntity> createDefault(
			String id, Block block,
			String modelPath, String texturePath) {
		return create(id, block, modelPath, texturePath,
				ResourceConstants.GENERIC_ANIMATION_PATH,
				ResourceConstants.GENERIC_ANIMATION);
	}

	// For scalable Pokédolls
	public static BlockEntityType<ScalablePokedollBlockEntity> createScalable(
			String id, Block block,
			String modelPath, String texturePath,
			String animationPath, String animationName,
			float scaleWidth, float scaleHeight) {

		return Registry.register(
				Registries.BLOCK_ENTITY_TYPE,
				Identifier.of(Pokeblocks.MOD_ID, id),
				FabricBlockEntityTypeBuilder.create(
						(pos, state) -> new ScalablePokedollBlockEntity(
								Registries.BLOCK_ENTITY_TYPE.get(Identifier.of(Pokeblocks.MOD_ID, id)),
								pos, state, modelPath, texturePath, animationPath, animationName,
								scaleWidth, scaleHeight
						),
						block
				).build()
		);
	}

	public static BlockEntityType<ScalablePokedollBlockEntity> createScalableDefault(
			String id, Block block,
			String modelPath, String texturePath,
			float scaleWidth, float scaleHeight) {
		return createScalable(id, block, modelPath, texturePath,
				ResourceConstants.GENERIC_ANIMATION_PATH,
				ResourceConstants.GENERIC_ANIMATION,
				scaleWidth, scaleHeight);
	}

}