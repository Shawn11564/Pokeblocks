package dev.mrshawn.pokeblocks.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * POC "open book" detail page for a single doll. Left page shows a large render of the doll
 * (silhouette if undiscovered); right page shows its name and a blurb.
 */
public class CompendiumDetailScreen extends Screen {

    private static final int BOOK_W = 256;
    private static final int BOOK_H = 180;

    private static final int COL_BORDER = 0xFF3A2A12;
    private static final int COL_PAGE = 0xFFEAD9B0;
    private static final int COL_SPINE = 0xFF6B4F2A;
    private static final int COL_TEXT = 0xFF4A3620;

    private static final float DOLL_SCALE = 6f;

    private final Screen parent;
    private final ItemStack doll;
    private final boolean discovered;
    private final DollSpin spin = new DollSpin();
    private final FrameClock clock = new FrameClock();

    private int left;
    private int top;

    public CompendiumDetailScreen(Screen parent, ItemStack doll, boolean discovered) {
        super(Component.literal("Doll Compendium"));
        this.parent = parent;
        this.doll = doll;
        this.discovered = discovered;
    }

    @Override
    protected void init() {
        left = (this.width - BOOK_W) / 2;
        top = (this.height - BOOK_H) / 2;

        addRenderableWidget(Button.builder(Component.literal("Back"), b -> onClose())
                .bounds((this.width - 80) / 2, top + BOOK_H + 8, 80, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Vanilla menu background (dim) + widgets, but no blur — see renderBlurredBackground.
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        clock.tick();

        int spineX = left + BOOK_W / 2;

        // Open-book panel: outer border, two parchment pages, a darker binding down the middle.
        guiGraphics.fill(left - 3, top - 3, left + BOOK_W + 3, top + BOOK_H + 3, COL_BORDER);
        guiGraphics.fill(left, top, spineX - 2, top + BOOK_H, COL_PAGE);
        guiGraphics.fill(spineX + 2, top, left + BOOK_W, top + BOOK_H, COL_PAGE);
        guiGraphics.fill(spineX - 2, top, spineX + 2, top + BOOK_H, COL_SPINE);

        // Left page: large doll render, centred.
        float leftCenterX = left + BOOK_W / 4f;
        float dollCenterY = top + BOOK_H / 2f + 6f;
        spin.update(clock.dt());
        CompendiumRender.renderDoll(guiGraphics, doll, leftCenterX, dollCenterY, DOLL_SCALE, !discovered, spin.yaw());

        // Right page: title + wrapped body text.
        int rightLeft = spineX + 14;
        int rightRight = left + BOOK_W - 14;
        int rightCenterX = (spineX + 2 + left + BOOK_W) / 2;

        Component title = discovered
                ? doll.getHoverName()
                : Component.literal("???");
        guiGraphics.drawCenteredString(this.font, title, rightCenterX, top + 22, COL_TEXT);
        // Underline-ish divider under the title.
        guiGraphics.fill(rightLeft, top + 36, rightRight, top + 37, COL_SPINE);

        Component body = Component.literal(discovered
                ? "Not much is known."
                : "You haven't found this doll yet.");
        List<FormattedCharSequence> lines = this.font.split(body, rightRight - rightLeft);
        int textY = top + 48;
        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(this.font, line, rightLeft, textY, COL_TEXT, false);
            textY += this.font.lineHeight + 2;
        }
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
        // Intentionally empty: suppresses the vanilla menu blur.
    }

    @Override
    public void onClose() {
        // Return to the index rather than closing the whole UI — flip back a page.
        if (this.minecraft != null) {
            this.minecraft.setScreen(parent);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
