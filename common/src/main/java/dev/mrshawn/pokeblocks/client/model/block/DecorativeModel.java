package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.client.renderer.PokeblocksRenderTypes;
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
		return PokeblocksAssetResolver.decorativeTexture(rm, basePath, definition.modelPrefix());
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
		return PokeblocksRenderTypes.forModel(definition.modelPrefix(), texture);
	}
}