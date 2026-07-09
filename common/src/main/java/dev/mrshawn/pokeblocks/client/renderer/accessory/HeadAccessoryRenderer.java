package dev.mrshawn.pokeblocks.client.renderer.accessory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Loader-agnostic seating for a pokedoll worn in an <em>accessory</em> head slot (Curios on
 * NeoForge/Forge, Trinkets/Accessories on Fabric) rather than the vanilla helmet slot.
 * <p>
 * The doll's whole on-head appearance — the {@link dev.mrshawn.pokeblocks.shape.HeadFit} seating,
 * the pose-aware bounds, the gigantic scale — is authored for the exact PoseStack space that vanilla
 * {@code CustomHeadLayer} leaves behind for {@link ItemDisplayContext#HEAD} (see {@code HeadFit}'s
 * class javadoc). An accessory slot bypasses {@code CustomHeadLayer} entirely, so the only thing an
 * accessory renderer has to do is <b>reproduce that space</b> and then hand the stack to the same
 * item renderer under the same {@code HEAD} context. Doing exactly that here means Curios/Trinkets
 * dolls sit identically to helmet-slot dolls with zero duplicated seating math, and any future tweak
 * to {@code HeadFit} flows to both automatically.
 * <p>
 * This mirrors the final branch of {@code CustomHeadLayer.render} in MC 1.21.1:
 * {@code head.translateAndRotate(pose)} → {@code CustomHeadLayer.translateToHead(pose, villager)} →
 * {@code itemInHandRenderer.renderItem(entity, stack, HEAD, false, pose, buffer, light)}. The loader
 * shim supplies the entity's model, PoseStack, buffer and packed light from the accessory library's
 * render callback.
 */
public final class HeadAccessoryRenderer {

	private HeadAccessoryRenderer() {}

	/**
	 * Seats {@code stack} on {@code entity}'s head. The caller must have already applied the entity's
	 * body/limb pose to {@code poseStack} (both Curios and Trinkets do this before invoking the item's
	 * renderer), so this only needs the local head-bone transform plus the vanilla head display map.
	 *
	 * @param entity       the wearer (any {@link LivingEntity}; players and armor stands both work)
	 * @param stack        the doll stack to render (no-op if empty)
	 * @param contextModel the wearer's entity model, used to locate the head bone; a non-headed model
	 *                     falls back to seating at the model origin
	 * @param poseStack    the render matrix stack, already at the entity's model space
	 * @param buffer       the render buffer source
	 * @param packedLight  the packed light coordinates for this render
	 */
	public static void render(LivingEntity entity, ItemStack stack, EntityModel<?> contextModel,
	                          PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		if (stack.isEmpty()) return;

		poseStack.pushPose();

		// Align to the head bone exactly as CustomHeadLayer does, so the doll tracks head pitch/yaw
		// and rides the correct joint on non-standard models (baby scaling, custom rigs, etc.).
		ModelPart head = headBone(contextModel);
		if (head != null) head.translateAndRotate(poseStack);

		// Reproduce the vanilla HEAD display space that HeadFit is authored against. The villager
		// nudge is part of vanilla's contract; dolls on players/stands pass false.
		boolean villager = entity instanceof Villager || entity instanceof ZombieVillager;
		CustomHeadLayer.translateToHead(poseStack, villager);

		// Same call vanilla makes from the head layer: routes through the doll's GeckoLib item
		// renderer under ItemDisplayContext.HEAD, whose scaleModelForRender applies the HeadFit seating.
		Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer()
				.renderItem(entity, stack, ItemDisplayContext.HEAD, false, poseStack, buffer, packedLight);

		poseStack.popPose();
	}

	private static ModelPart headBone(EntityModel<?> model) {
		return model instanceof HeadedModel headed ? headed.getHead() : null;
	}
}
