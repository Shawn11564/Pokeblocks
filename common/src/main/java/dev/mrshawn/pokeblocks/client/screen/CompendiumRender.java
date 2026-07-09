package dev.mrshawn.pokeblocks.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.mrshawn.pokeblocks.client.renderer.item.FigurineItemRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/** Shared helper for drawing a compendium entry enlarged (and optionally silhouetted) in a GUI. */
public final class CompendiumRender {

	// Z position of an item's centre inside GuiGraphics#renderItem (it translates by 150 before
	// scaling). Pivoting about this keeps the entry spinning in place instead of orbiting.
	private static final float ITEM_CENTER_Z = 150f;

	private CompendiumRender() {}

	/** As {@link #render(GuiGraphics, CompendiumType, ItemStack, float, float, float, boolean, float, float, boolean)} with no pitch and the figurine box shown. */
	public static void render(GuiGraphics guiGraphics, CompendiumType type, ItemStack stack,
							  float centerX, float centerY, float scale, boolean silhouette, float yaw) {
		render(guiGraphics, type, stack, centerX, centerY, scale, silhouette, yaw, 0f, false);
	}

	/**
	 * Renders {@code stack} centred on ({@code centerX}, {@code centerY}), scaled up from the 16px
	 * inventory size, spun {@code yaw} degrees about its vertical axis and tilted {@code pitch}
	 * degrees about the screen's horizontal axis (drag-to-inspect). When {@code silhouette} is true
	 * the entry draws as a solid black shape via the type's shared item renderer; {@code hideBox}
	 * additionally skips a figurine's display case.
	 */
	public static void render(GuiGraphics guiGraphics, CompendiumType type, ItemStack stack,
							  float centerX, float centerY, float scale, boolean silhouette,
							  float yaw, float pitch, boolean hideBox) {
		float rendered = 16f * scale;
		PoseStack pose = guiGraphics.pose();
		pose.pushPose();
		pose.translate(centerX - rendered / 2f, centerY - rendered / 2f, 0);
		pose.scale(scale, scale, scale);

		// Pivot about the entry's centre so it turns on the spot. Pitch is applied outermost
		// (screen space) so dragging up/down always tilts toward the viewer, whatever the yaw.
		pose.translate(8f, 8f, ITEM_CENTER_Z);
		pose.mulPose(Axis.XP.rotationDegrees(pitch));
		pose.mulPose(Axis.YP.rotationDegrees(yaw));
		pose.translate(-8f, -8f, -ITEM_CENTER_Z);

		type.setSilhouette(silhouette);
		FigurineItemRenderer.HIDE_BOX = hideBox;
		try {
			guiGraphics.renderItem(stack, 0, 0);
		} finally {
			FigurineItemRenderer.HIDE_BOX = false;
			type.setSilhouette(false);
			pose.popPose();
		}
	}
}
