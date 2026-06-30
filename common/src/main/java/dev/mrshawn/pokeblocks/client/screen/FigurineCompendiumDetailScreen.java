package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.item.FigurineDescriptionOverrides;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * "Open book" detail page for a single figurine. Left page shows a large render of the figurine
 * (silhouette if undiscovered); right page shows its name and a description — a generic default, or a
 * custom blurb from {@code figurine_descriptions.json} when one is set for that figurine. Sibling of
 * {@link CompendiumDetailScreen} for dolls.
 */
public class FigurineCompendiumDetailScreen extends Screen {

    private static final int BOOK_W = 256;
    private static final int BOOK_H = 180;

    private static final int COL_BORDER = 0xFF3A2A12;
    private static final int COL_PAGE = 0xFFEAD9B0;
    private static final int COL_SPINE = 0xFF6B4F2A;
    private static final int COL_TEXT = 0xFF4A3620;

    private static final float FIGURINE_SCALE = 6f;

    /** Shown for a discovered figurine that has no custom description configured. */
    private static final String DEFAULT_DESCRIPTION = "A collectible figurine.";

    private final Screen parent;
    private final ItemStack figurine;
    private final boolean discovered;
    private final DollSpin spin = new DollSpin();
    private final FrameClock clock = new FrameClock();

    private int left;
    private int top;

    public FigurineCompendiumDetailScreen(Screen parent, ItemStack figurine, boolean discovered) {
        super(Component.literal("Figurine Compendium"));
        this.parent = parent;
        this.figurine = figurine;
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

        // Left page: large figurine render, centred.
        float leftCenterX = left + BOOK_W / 4f;
        float figurineCenterY = top + BOOK_H / 2f + 6f;
        spin.update(clock.dt());
        FigurineCompendiumRender.renderFigurine(guiGraphics, figurine, leftCenterX, figurineCenterY, FIGURINE_SCALE, !discovered, spin.yaw());

        // Right page: title + wrapped body text.
        int rightLeft = spineX + 14;
        int rightRight = left + BOOK_W - 14;
        int rightCenterX = (spineX + 2 + left + BOOK_W) / 2;

        Component title = discovered
                ? figurine.getHoverName()
                : Component.literal("???");
        guiGraphics.drawCenteredString(this.font, title, rightCenterX, top + 22, COL_TEXT);
        // Underline-ish divider under the title.
        guiGraphics.fill(rightLeft, top + 36, rightRight, top + 37, COL_SPINE);

        Component body = Component.literal(bodyText());
        List<FormattedCharSequence> lines = this.font.split(body, rightRight - rightLeft);
        int textY = top + 48;
        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(this.font, line, rightLeft, textY, COL_TEXT, false);
            textY += this.font.lineHeight + 2;
        }
    }

    /**
     * The right-page blurb: the undiscovered hint, else the figurine's custom description from
     * {@code figurine_descriptions.json}, else the generic default.
     */
    private String bodyText() {
        if (!discovered) {
            return "You haven't found this figurine yet.";
        }
        String id = FigurineItem.getFigurineFromStack(figurine);
        String custom = FigurineDescriptionOverrides.getOverride(id);
        return custom != null ? custom : DEFAULT_DESCRIPTION;
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
