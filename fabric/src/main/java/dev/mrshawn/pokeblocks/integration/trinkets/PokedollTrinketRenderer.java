package dev.mrshawn.pokeblocks.integration.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.mrshawn.pokeblocks.client.renderer.accessory.HeadAccessoryRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Draws a pokedoll worn in the Trinkets <b>head/hat</b> slot. Trinkets invokes this from its render
 * layer with the PoseStack already at the wearer's model root (like a vanilla armor layer), so it
 * simply defers to {@link HeadAccessoryRenderer}, which reproduces vanilla's head display space and
 * reuses the doll's existing {@code HeadFit} seating — a Trinkets doll sits identically to one in the
 * vanilla helmet slot.
 * <p>
 * Types here come from the Trinkets API (mapped to Mojang names by Loom at build time), so this class
 * must only be classloaded when the {@code trinkets} mod is present (see {@link TrinketsIntegration}).
 */
public class PokedollTrinketRenderer implements TrinketRenderer {

	@Override
	public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel,
	                   PoseStack matrices, MultiBufferSource vertexConsumers, int light, LivingEntity entity,
	                   float limbAngle, float limbDistance, float tickDelta, float animationProgress,
	                   float headYaw, float headPitch) {
		HeadAccessoryRenderer.render(entity, stack, contextModel, matrices, vertexConsumers, light);
	}
}
