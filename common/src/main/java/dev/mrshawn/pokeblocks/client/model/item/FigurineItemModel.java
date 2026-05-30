package dev.mrshawn.pokeblocks.client.model.item;

import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class FigurineItemModel extends GeoModel<FigurineItem> {
	private ItemStack currentStack = ItemStack.EMPTY;

	public void setCurrentItemStack(ItemStack stack) {
		this.currentStack = stack;
	}

	/** The validated figurine id for the current stack. */
	private String figurine(ResourceManager rm) {
		String id = currentStack.isEmpty() ? ModSettings.DEFAULT_FIGURINE : FigurineItem.getFigurineFromStack(currentStack);
		return PokeblocksAssetResolver.validatedFigurine(rm, id);
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		try {
			return super.getBakedModel(location);
		} catch (RuntimeException e) {
			return super.getBakedModel(PokeblocksAssetResolver.figurineModel(ModSettings.DEFAULT_FIGURINE));
		}
	}

	@Override
	public ResourceLocation getModelResource(FigurineItem animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		return PokeblocksAssetResolver.figurineModel(figurine(rm));
	}

	@Override
	public ResourceLocation getTextureResource(FigurineItem animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		return PokeblocksAssetResolver.figurineTexture(rm, figurine(rm));
	}

	@Override
	public ResourceLocation getAnimationResource(FigurineItem animatable) {
		return null;
	}

	@Override
	public RenderType getRenderType(FigurineItem animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}