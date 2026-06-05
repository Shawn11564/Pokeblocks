package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * POC compendium screen: a paged grid of every registered doll. Dolls the player owns render in
 * full colour; the rest render as dark 3D silhouettes (shape only, name hidden).
 */
public class CompendiumScreen extends Screen {

    private static final int COLUMNS = 7;
    private static final int ROWS = 4;
    private static final int PER_PAGE = COLUMNS * ROWS;
    private static final int CELL = 44;
    private static final float ITEM_SCALE = 2.5f; // 16px item -> 40px

    private static final int CELL_BG = 0xFF2B2B2B;
    private static final int CELL_BG_HOVER = 0xFF4A4A4A;
    private static final int CELL_BORDER = 0xFF000000;

    /** Base doll stacks, one per species, in stable alphabetical order. */
    private final List<ItemStack> dolls = new ArrayList<>();
    /** Spin state, parallel to {@link #dolls}. */
    private final List<DollSpin> spins = new ArrayList<>();
    private final FrameClock clock = new FrameClock();
    private Set<String> collected = Set.of();

    private int page = 0;
    private int gridOriginX;
    private int gridOriginY;

    private Button prevButton;
    private Button nextButton;

    public CompendiumScreen() {
        super(Component.literal("Doll Compendium"));
    }

    @Override
    protected void init() {
        dolls.clear();
        spins.clear();
        // Alphabetical species order keeps page contents stable between opens.
        for (String species : new TreeSet<>(PokemonRegistry.ALL_POKEMON.keySet())) {
            dolls.add(PokedollItem.createPokedoll(species, new EnumMap<>(ModelFlag.class)));
            spins.add(new DollSpin());
        }
        collected = CompendiumCollection.collectedSpecies(this.minecraft != null ? this.minecraft.player : null);

        int gridWidth = COLUMNS * CELL;
        int gridHeight = ROWS * CELL;
        gridOriginX = (this.width - gridWidth) / 2;
        gridOriginY = (this.height - gridHeight) / 2 - 4;

        int navY = gridOriginY + gridHeight + 8;
        prevButton = Button.builder(Component.literal("< Prev"), b -> changePage(-1))
                .bounds(gridOriginX, navY, 60, 20).build();
        nextButton = Button.builder(Component.literal("Next >"), b -> changePage(1))
                .bounds(gridOriginX + gridWidth - 60, navY, 60, 20).build();
        addRenderableWidget(prevButton);
        addRenderableWidget(nextButton);
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds((this.width - 60) / 2, navY, 60, 20).build());

        updateNavState();
    }

    private int pageCount() {
        return Math.max(1, (dolls.size() + PER_PAGE - 1) / PER_PAGE);
    }

    private void changePage(int delta) {
        page = Math.max(0, Math.min(pageCount() - 1, page + delta));
        updateNavState();
    }

    private void updateNavState() {
        prevButton.active = page > 0;
        nextButton.active = page < pageCount() - 1;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Vanilla menu background (dim) + widgets, but no blur — see renderBlurredBackground.
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        clock.tick();

        // Header: title + collection progress.
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, gridOriginY - 28, 0xFFFFFF);
        Component progress = Component.literal(collected.size() + " / " + dolls.size() + " collected")
                .withStyle(ChatFormatting.GRAY);
        guiGraphics.drawCenteredString(this.font, progress, this.width / 2, gridOriginY - 16, 0xFFFFFF);

        ItemStack hovered = null;
        boolean hoveredCollected = false;

        int start = page * PER_PAGE;
        int end = Math.min(start + PER_PAGE, dolls.size());
        for (int i = start; i < end; i++) {
            int slot = i - start;
            int col = slot % COLUMNS;
            int row = slot / COLUMNS;
            int cellX = gridOriginX + col * CELL;
            int cellY = gridOriginY + row * CELL;

            boolean hover = mouseX >= cellX && mouseX < cellX + CELL && mouseY >= cellY && mouseY < cellY + CELL;

            // Slot background so dark silhouettes stay visible against the dim screen.
            guiGraphics.fill(cellX, cellY, cellX + CELL, cellY + CELL, CELL_BORDER);
            guiGraphics.fill(cellX + 1, cellY + 1, cellX + CELL - 1, cellY + CELL - 1, hover ? CELL_BG_HOVER : CELL_BG);

            ItemStack doll = dolls.get(i);
            boolean isCollected = collected.contains(PokedollItem.getPokemonFromStack(doll).toLowerCase());

            float cx = cellX + CELL / 2f;
            float cy = cellY + CELL / 2f;
            DollSpin spin = spins.get(i);
            spin.update(clock.dt());
            CompendiumRender.renderDoll(guiGraphics, doll, cx, cy, ITEM_SCALE, !isCollected, spin.yaw());

            if (hover) {
                hovered = doll;
                hoveredCollected = isCollected;
            }
        }

        if (hovered != null) {
            Component name = hoveredCollected
                    ? hovered.getHoverName()
                    : Component.literal("???").withStyle(ChatFormatting.DARK_GRAY);
            guiGraphics.renderTooltip(this.font, name, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (button == 0) {
            int idx = cellIndexAt(mouseX, mouseY);
            if (idx >= 0 && this.minecraft != null) {
                ItemStack doll = dolls.get(idx);
                boolean isCollected = collected.contains(PokedollItem.getPokemonFromStack(doll).toLowerCase());
                this.minecraft.setScreen(new CompendiumDetailScreen(this, doll, isCollected));
                return true;
            }
        }
        return false;
    }

    /** Index into {@link #dolls} of the cell under the cursor on the current page, or -1. */
    private int cellIndexAt(double mouseX, double mouseY) {
        int start = page * PER_PAGE;
        int end = Math.min(start + PER_PAGE, dolls.size());
        for (int i = start; i < end; i++) {
            int slot = i - start;
            int cellX = gridOriginX + (slot % COLUMNS) * CELL;
            int cellY = gridOriginY + (slot / COLUMNS) * CELL;
            if (mouseX >= cellX && mouseX < cellX + CELL && mouseY >= cellY && mouseY < cellY + CELL) {
                return i;
            }
        }
        return -1;
    }

    /** True while either compendium screen is the active screen. Used by world doll rendering. */
    public static boolean isOpen() {
        Screen screen = Minecraft.getInstance().screen;
        return screen instanceof CompendiumScreen || screen instanceof CompendiumDetailScreen;
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
        // Intentionally empty: suppresses the vanilla menu blur.
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
