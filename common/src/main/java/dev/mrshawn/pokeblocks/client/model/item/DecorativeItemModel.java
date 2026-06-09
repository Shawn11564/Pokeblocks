package dev.mrshawn.pokeblocks.client.model.item;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.client.renderer.PokeblocksRenderTypes;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

public class DecorativeItemModel extends GeoModel<DecorativeItem> {
	private final DecorativeDefinition definition;
	private ItemStack currentStack = ItemStack.EMPTY;

	public DecorativeItemModel(DecorativeDefinition definition) {
		this.definition = definition;
	}

	public void setCurrentItemStack(ItemStack stack) {
		this.currentStack = stack;
	}

	private Set<ModelFlag> getActiveFlags() {
		if (currentStack == null || currentStack.isEmpty()) return EnumSet.noneOf(ModelFlag.class);
		return DecorativeItem.getFlagsFromStack(currentStack);
	}

	private Function<String, String> getNbtLookup() {
		if (currentStack == null || currentStack.isEmpty()) return k -> null;
		CustomData data = currentStack.get(DataComponents.BLOCK_ENTITY_DATA);
		if (data == null) return k -> null;
		CompoundTag tag = data.copyTag();
		return key -> tag.contains(key) ? tag.getString(key) : null;
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
	public ResourceLocation getModelResource(DecorativeItem animatable) {
		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				definition.modelPath(getActiveFlags(), getNbtLookup())
		);
	}

	@Override
	public ResourceLocation getTextureResource(DecorativeItem animatable) {
		ResourceManager rm = Minecraft.getInstance().getResourceManager();
		String basePath = definition.texturePath(getActiveFlags(), getNbtLookup());
		return PokeblocksAssetResolver.decorativeTexture(rm, basePath, definition.modelPrefix());
	}

	@Override
	public ResourceLocation getAnimationResource(DecorativeItem animatable) {
		if (!definition.hasAnimation()) {
			return ResourceLocation.fromNamespaceAndPath(
					PokeblocksCommon.MOD_ID,
					"animations/block/empty.animation.json"
			);
		}
		return ResourceLocation.fromNamespaceAndPath(
				PokeblocksCommon.MOD_ID,
				definition.animationPath(getNbtLookup())
		);
	}

	@Override
	public RenderType getRenderType(DecorativeItem animatable, ResourceLocation texture) {
		return PokeblocksRenderTypes.forModel(definition.modelPrefix(), texture);
	}
}