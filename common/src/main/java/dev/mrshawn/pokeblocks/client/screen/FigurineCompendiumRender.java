package dev.mrshawn.pokeblocks.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.mrshawn.pokeblocks.client.renderer.item.FigurineItemRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/** Shared helper for drawing a figurine item enlarged (and optionally silhouetted) in a GUI. */
public final class FigurineCompendiumRender {

    // Z position of an item's centre inside GuiGraphics#renderItem (it translates by 150 before
    // scaling). Pivoting about this keeps the figurine spinning in place instead of orbiting.
    private static final float ITEM_CENTER_Z = 150f;

    private FigurineCompendiumRender() {}

    /**
     * Renders {@code figurine} centred on ({@code centerX}, {@code centerY}), scaled up from the 16px
     * inventory size and spun {@code yaw} degrees about its vertical axis. When {@code silhouette} is
     * true the figurine draws as a solid black shape.
     */
    public static void renderFigurine(GuiGraphics guiGraphics, ItemStack figurine, float centerX, float centerY,
                                      float scale, boolean silhouette, float yaw) {
        float rendered = 16f * scale;
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(centerX - rendered / 2f, centerY - rendered / 2f, 0);
        pose.scale(scale, scale, scale);

        // Pivot about the figurine's centre so it spins on the spot.
        pose.translate(8f, 8f, ITEM_CENTER_Z);
        pose.mulPose(Axis.YP.rotationDegrees(yaw));
        pose.translate(-8f, -8f, -ITEM_CENTER_Z);

        FigurineItemRenderer.SILHOUETTE = silhouette;
        try {
            guiGraphics.renderItem(figurine, 0, 0);
        } finally {
            FigurineItemRenderer.SILHOUETTE = false;
            pose.popPose();
        }
    }
}
