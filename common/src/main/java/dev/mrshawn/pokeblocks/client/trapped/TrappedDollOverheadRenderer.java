package dev.mrshawn.pokeblocks.client.trapped;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mrshawn.pokeblocks.item.TrappedDolls;
import dev.mrshawn.pokeblocks.trapped.ClientTrappedDollTimers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Locale;

/**
 * The countdown other players see: while a player carries a ticking trapped doll — on their head
 * <i>or anywhere in their inventory</i> — a red timer floats above their name tag, fed by the
 * server's {@link ClientTrappedDollTimers timer broadcast} (inventory contents aren't synced to
 * other clients, so the head stack alone wouldn't cover the pocketed case; it still serves as a
 * fallback when the broadcast channel is absent). Rendered from {@code TrappedDollNameTagMixin} at
 * the tail of {@code PlayerRenderer#render}, deliberately independent of the vanilla name-tag
 * path so hidden or out-of-range name tags can't hide a live bomb.
 * <p>
 * The transform replicates vanilla {@code EntityRenderer#renderNameTag} (attachment point,
 * camera-facing billboard, 0.025 scale, see-through + normal passes), lifted 0.35 blocks above
 * the name line.
 */
public final class TrappedDollOverheadRenderer {

	/** Vanilla name tags stop rendering past 64 blocks; the countdown matches. */
	private static final double MAX_DISTANCE_SQR = 4096.0;
	/** World-space lift above the vanilla name-tag baseline (which sits at attachment + 0.5). */
	private static final float ABOVE_NAME = 0.35f;
	private static final int RED_OPAQUE = 0xFFFF3030;
	private static final int RED_SEE_THROUGH = 0x20FF3030;

	private TrappedDollOverheadRenderer() {}

	public static void render(AbstractClientPlayer player, float partialTick, PoseStack poseStack,
							  MultiBufferSource buffer, int packedLight) {
		Minecraft minecraft = Minecraft.getInstance();

		Long detonateAt = ClientTrappedDollTimers.detonateAtFor(player.getUUID());
		if (detonateAt == null) {
			// Broadcast-less fallback: the head slot is synced to every watcher regardless.
			ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
			if (!TrappedDolls.isTicking(head)) {
				return;
			}
			detonateAt = TrappedDolls.detonateAt(head);
		}

		if (minecraft.getEntityRenderDispatcher().distanceToSqr(player) > MAX_DISTANCE_SQR) {
			return;
		}
		Vec3 attachment = player.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, player.getViewYRot(partialTick));
		if (attachment == null) {
			return;
		}

		float remainingTicks = Math.max(0.0f, detonateAt - player.level().getGameTime() - partialTick);
		Component text = Component.literal(String.format(Locale.ROOT, "%.1f", remainingTicks / 20.0f));

		poseStack.pushPose();
		poseStack.translate(attachment.x, attachment.y + 0.5 + ABOVE_NAME, attachment.z);
		poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
		poseStack.scale(0.025f, -0.025f, 0.025f);
		Matrix4f pose = poseStack.last().pose();

		Font font = minecraft.font;
		float x = -font.width(text) / 2.0f;
		int background = (int) (minecraft.options.getBackgroundOpacity(0.25f) * 255.0f) << 24;
		// Same two passes as a vanilla name tag: dim through walls, bright in direct view. A bomb
		// timer ignores sneaking — crouching shouldn't hide that you're about to go off.
		font.drawInBatch(text, x, 0.0f, RED_SEE_THROUGH, false, pose, buffer, Font.DisplayMode.SEE_THROUGH, background, packedLight);
		font.drawInBatch(text, x, 0.0f, RED_OPAQUE, false, pose, buffer, Font.DisplayMode.NORMAL, 0, packedLight);
		poseStack.popPose();
	}
}
