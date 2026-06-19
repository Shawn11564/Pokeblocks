package dev.mrshawn.pokeblocks.client.model.item;

import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.custom.CustomDecorationItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

/**
 * Item GeoModel for the generic custom-decoration block item. Mirrors {@code FigurineItemModel}:
 * resolves the decoration id from the current stack and delegates geo/texture/animation resolution to
 * {@link PokeblocksAssetResolver}.
 */
public class CustomDecorationItemModel extends GeoModel<CustomDecorationItem> {
	private ItemStack currentStack = ItemStack.EMPTY;

	public void setCurrentItemStack(ItemStack stack) {
		this.currentStack = stack;
	}

	/** The validated decoration id for the current stack. */
	private String decoration(ResourceManager rm) {
		String id = currentStack.isEmpty() ? ModSettings.DEFAULT_DECORATION : CustomDecorationItem.getDecorationFromStack(currentStack);
		return PokeblocksAssetResolver.validatedDecoration(rm, id);
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		try {
			return super.getBakedModel(location);
		} catch (RuntimeException e) {
			return super.getBakedModel(PokeblocksAssetResolver.customDecorationModel(ModSettings.DEFAULT_DECORATION));
		}
	}

	@Override
	public ResourceLocation getModelResource(CustomDecorationItem animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		return PokeblocksAssetResolver.customDecorationModel(decoration(rm));
	}

	@Override
	public ResourceLocation getTextureResource(CustomDecorationItem animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		return PokeblocksAssetResolver.customDecorationTexture(rm, decoration(rm));
	}

	@Override
	public ResourceLocation getAnimationResource(CustomDecorationItem animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		return PokeblocksAssetResolver.customDecorationAnimation(rm, decoration(rm));
	}

	@Override
	public RenderType getRenderType(CustomDecorationItem animatable, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
}
