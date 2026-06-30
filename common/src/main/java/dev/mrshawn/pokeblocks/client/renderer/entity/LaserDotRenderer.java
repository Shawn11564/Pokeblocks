package dev.mrshawn.pokeblocks.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mrshawn.pokeblocks.entity.custom.LaserDotEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * Renders a {@link LaserDotEntity} as a glowing coloured beam from its owner's hand to the dot, plus a
 * small flare at the dot itself. The dot's position and colour arrive via vanilla entity tracking, so
 * this draws identically on every client — the holder and bystanders alike.
 * <p>
 * The beam is a camera-billboarded quad drawn with {@link RenderType#lightning()} (additive, unlit), so
 * it reads as light rather than a solid bar. All geometry is built in entity-local space (the pose stack
 * is already at the dot), with the owner's hand and the camera converted into that space.
 */
public class LaserDotRenderer extends EntityRenderer<LaserDotEntity> {

	private static final float BEAM_HALF_WIDTH = 0.035f;
	private static final float CORE_HALF_WIDTH = 0.012f;
	private static final float FLARE_HALF_SIZE = 0.07f;

	public LaserDotRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(LaserDotEntity dot, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource buffer, int packedLight) {
		Player owner = resolveOwner(dot);
		if (owner != null) {
			renderBeam(dot, owner, partialTick, poseStack, buffer);
		}
		super.render(dot, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	private void renderBeam(LaserDotEntity dot, Player owner, float partialTick, PoseStack poseStack,
							MultiBufferSource buffer) {
		int color = dot.getColor();
		float r = ((color >> 16) & 0xFF) / 255f;
		float g = ((color >> 8) & 0xFF) / 255f;
		float b = (color & 0xFF) / 255f;

		// World position of the dot (this entity), interpolated to match the pose stack origin.
		Vec3 dotWorld = new Vec3(
				Mth.lerp(partialTick, dot.xOld, dot.getX()),
				Mth.lerp(partialTick, dot.yOld, dot.getY()),
				Mth.lerp(partialTick, dot.zOld, dot.getZ()));

		// Approximate the owner's hand: forward, to the right and slightly below the eyes.
		Vec3 eye = owner.getEyePosition(partialTick);
		Vec3 look = owner.getViewVector(partialTick);
		Vec3 right = look.cross(new Vec3(0, 1, 0));
		right = right.lengthSqr() < 1.0e-4 ? new Vec3(1, 0, 0) : right.normalize();
		Vec3 handWorld = eye.add(look.scale(0.4)).add(right.scale(0.25)).subtract(0, 0.25, 0);

		Vec3 camWorld = this.entityRenderDispatcher.camera.getPosition();

		// Everything into entity-local space (the dot sits at the local origin).
		Vec3 from = handWorld.subtract(dotWorld);
		Vec3 to = Vec3.ZERO;
		Vec3 cam = camWorld.subtract(dotWorld);

		Matrix4f mat = poseStack.last().pose();
		VertexConsumer vc = buffer.getBuffer(RenderType.lightning());

		// Outer glow then a near-white hot core down the middle.
		drawBeamQuad(vc, mat, from, to, cam, BEAM_HALF_WIDTH, r, g, b, 0.5f);
		drawBeamQuad(vc, mat, from, to, cam,
				CORE_HALF_WIDTH, lighten(r), lighten(g), lighten(b), 0.95f);

		// A small camera-facing flare at the dot.
		drawFlare(vc, mat, to, cam, FLARE_HALF_SIZE, r, g, b, 0.85f);
	}

	/** Emits a camera-billboarded quad of the given half-width running from {@code from} to {@code to}. */
	private static void drawBeamQuad(VertexConsumer vc, Matrix4f mat, Vec3 from, Vec3 to, Vec3 cam,
									 float halfWidth, float r, float g, float b, float a) {
		Vec3 seg = to.subtract(from);
		Vec3 toCam = cam.subtract(from.add(seg.scale(0.5)));
		Vec3 w = perpendicular(seg, toCam).scale(halfWidth);

		quad(vc, mat,
				from.add(w), from.subtract(w), to.subtract(w), to.add(w),
				r, g, b, a);
	}

	/** Emits a flat camera-facing square centred on {@code center}. */
	private static void drawFlare(VertexConsumer vc, Matrix4f mat, Vec3 center, Vec3 cam,
								  float halfSize, float r, float g, float b, float a) {
		Vec3 viewDir = center.subtract(cam);
		viewDir = viewDir.lengthSqr() < 1.0e-4 ? new Vec3(0, 0, 1) : viewDir.normalize();
		Vec3 right = perpendicular(viewDir, new Vec3(0, 1, 0)).scale(halfSize);
		Vec3 up = perpendicular(viewDir, right).scale(halfSize);

		quad(vc, mat,
				center.add(right).add(up), center.add(right).subtract(up),
				center.subtract(right).subtract(up), center.subtract(right).add(up),
				r, g, b, a);
	}

	/** Unit vector perpendicular to {@code a}, in the plane spanned with {@code hint}; robust to degeneracy. */
	private static Vec3 perpendicular(Vec3 a, Vec3 hint) {
		Vec3 c = a.cross(hint);
		if (c.lengthSqr() < 1.0e-4) {
			c = a.cross(new Vec3(1, 0, 0));
			if (c.lengthSqr() < 1.0e-4) c = a.cross(new Vec3(0, 0, 1));
		}
		return c.normalize();
	}

	private static void quad(VertexConsumer vc, Matrix4f mat, Vec3 a, Vec3 b, Vec3 c, Vec3 d,
							 float red, float green, float blue, float alpha) {
		vertex(vc, mat, a, red, green, blue, alpha);
		vertex(vc, mat, b, red, green, blue, alpha);
		vertex(vc, mat, c, red, green, blue, alpha);
		vertex(vc, mat, d, red, green, blue, alpha);
	}

	private static void vertex(VertexConsumer vc, Matrix4f mat, Vec3 p,
							   float r, float g, float b, float a) {
		vc.addVertex(mat, (float) p.x, (float) p.y, (float) p.z).setColor(r, g, b, a);
	}

	private static float lighten(float channel) {
		return channel + (1f - channel) * 0.6f;
	}

	private static Player resolveOwner(LaserDotEntity dot) {
		return dot.getOwnerUUID().map(dot.level()::getPlayerByUUID).orElse(null);
	}

	@Override
	public ResourceLocation getTextureLocation(LaserDotEntity entity) {
		// The beam is untextured (POSITION_COLOR via RenderType.lightning); no texture is sampled.
		return null;
	}
}
