package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class DecorativeModel extends DefaultedBlockGeoModel<DecorativeBlockEntity> {
	private final DecorativeDefinition definition;

	public DecorativeModel(DecorativeDefinition definition) {
		super(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, definition.modelPrefix()));
		this.definition = definition;
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		try {
			return super.getBakedModel(location);
		} catch (RuntimeException e) {
			ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"geo/block/" + definition.modelPrefix() + ".geo.json"
			);
			return super.getBakedModel(fallback);
		}
	}

	@Override
	public ResourceLocation getModelResource(DecorativeBlockEntity animatable) {
		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				definition.modelPath(animatable.getActiveFlags(), animatable.getNbtLookup())
		);
	}

	@Override
	public ResourceLocation getTextureResource(DecorativeBlockEntity animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String basePath = definition.texturePath(animatable.getActiveFlags(), animatable.getNbtLookup());

		ResourceLocation withTexture = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID, basePath + "_texture.png");
		try {
			if (rm.getResource(withTexture).isPresent()) return withTexture;
		} catch (Exception e) {}

		ResourceLocation plain = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID, basePath + ".png");
		try {
			if (rm.getResource(plain).isPresent()) return plain;
		} catch (Exception e) {}

		// Fallback to base prefix
		ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/" + definition.modelPrefix() + "_texture.png");
		try {
			if (rm.getResource(baseTexture).isPresent()) return baseTexture;
		} catch (Exception e) {}

		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/" + definition.modelPrefix() + ".png");
	}

	@Override
	public ResourceLocation getAnimationResource(DecorativeBlockEntity animatable) {
		if (!definition.hasAnimation()) {
			return ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"animations/block/empty.animation.json"
			);
		}
		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				definition.animationPath(animatable.getNbtLookup())
		);
	}

	@Override
	public RenderType getRenderType(DecorativeBlockEntity animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}