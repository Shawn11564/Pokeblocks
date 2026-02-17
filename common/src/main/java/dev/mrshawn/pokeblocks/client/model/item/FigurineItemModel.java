package dev.mrshawn.pokeblocks.client.model.item;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.FigurineItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.HashSet;
import java.util.Set;

public class FigurineItemModel extends GeoModel<FigurineItem> {
	private static final Set<String> validatedFigurines = new HashSet<>();
	private ItemStack currentStack = ItemStack.EMPTY;

	public FigurineItemModel() {
		validatedFigurines.add(ModSettings.DEFAULT_FIGURINE);
	}

	public void setCurrentItemStack(ItemStack stack) {
		this.currentStack = stack;
	}

	private String getValidatedFigurine(String figurine) {
		if (figurine == null || figurine.isEmpty()) return ModSettings.DEFAULT_FIGURINE;
		if (validatedFigurines.contains(figurine)) return figurine;

		ResourceLocation modelPath = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"geo/block/" + figurine + "_figurine.geo.json"
		);

		try {
			if (Minecraft.getInstance().getResourceManager().getResource(modelPath).isPresent()) {
				validatedFigurines.add(figurine);
				return figurine;
			}
		} catch (Exception e) {}

		return ModSettings.DEFAULT_FIGURINE;
	}

	private String getFigurineFromCurrent() {
		if (!currentStack.isEmpty()) {
			return getValidatedFigurine(FigurineItem.getFigurineFromStack(currentStack));
		}
		return ModSettings.DEFAULT_FIGURINE;
	}

	@Override
	public BakedGeoModel getBakedModel(ResourceLocation location) {
		try {
			return super.getBakedModel(location);
		} catch (RuntimeException e) {
			ResourceLocation fallback = ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"geo/block/" + ModSettings.DEFAULT_FIGURINE + "_figurine.geo.json"
			);
			return super.getBakedModel(fallback);
		}
	}

	@Override
	public ResourceLocation getModelResource(FigurineItem animatable) {
		String figurine = getFigurineFromCurrent();
		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"geo/block/" + figurine + "_figurine.geo.json"
		);
	}

	@Override
	public ResourceLocation getTextureResource(FigurineItem animatable) {
		String figurine = getFigurineFromCurrent();
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

		ResourceLocation withTexture = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/" + figurine + "_figurine_texture.png"
		);
		try {
			if (resourceManager.getResource(withTexture).isPresent()) return withTexture;
		} catch (Exception e) {}

		ResourceLocation plain = ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/" + figurine + "_figurine.png"
		);
		try {
			if (resourceManager.getResource(plain).isPresent()) return plain;
		} catch (Exception e) {}

		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				"textures/block/" + ModSettings.DEFAULT_FIGURINE + "_figurine_texture.png"
		);
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