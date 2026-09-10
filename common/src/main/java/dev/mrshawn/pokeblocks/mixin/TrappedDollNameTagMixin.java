package dev.mrshawn.pokeblocks.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mrshawn.pokeblocks.client.trapped.TrappedDollOverheadRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the trapped-doll countdown above other players' heads (see
 * {@link TrappedDollOverheadRenderer}). Hooked at the tail of {@code PlayerRenderer#render} rather
 * than {@code renderNameTag} so the timer shows even when the name tag itself doesn't (the
 * renderer applies its own vanilla-parity distance gate).
 */
@Mixin(PlayerRenderer.class)
public abstract class TrappedDollNameTagMixin {

	@Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At("TAIL"))
	private void pokeblocks$renderTrappedCountdown(AbstractClientPlayer player, float entityYaw, float partialTick,
												   PoseStack poseStack, MultiBufferSource buffer, int packedLight,
												   CallbackInfo ci) {
		TrappedDollOverheadRenderer.render(player, partialTick, poseStack, buffer, packedLight);
	}
}
