package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

/**
 * The compendium index: a paged grid of every registered entry of one {@link CompendiumType},
 * drawn on a wood-framed parchment panel. Entries the player has collected render in full colour;
 * the rest are dark 3D silhouettes (shape only, name hidden). Supports searching collected entries,
 * page-turn arrows, mouse-wheel and arrow-key paging, and clicking an entry for its detail page.
 */
public class CompendiumScreen extends Screen {

	private static final int CELL = 44;
	private static final float ITEM_SCALE = 2.5f; // 16px item -> 40px

	// GUI sprites (assets/pokeblocks/textures/gui/sprites/compendium/).
	private static final ResourceLocation SPRITE_PANEL = sprite("compendium/panel");
	private static final ResourceLocation SPRITE_SLOT = sprite("compendium/slot");
	private static final ResourceLocation SPRITE_SEARCH = sprite("compendium/search_field");
	private static final WidgetSprites SPRITES_PAGE_BACKWARD = new WidgetSprites(
			sprite("compendium/page_backward"), sprite("compendium/page_backward_highlighted"));
	private static final WidgetSprites SPRITES_PAGE_FORWARD = new WidgetSprites(
			sprite("compendium/page_forward"), sprite("compendium/page_forward_highlighted"));
	static final WidgetSprites SPRITES_CLOSE = new WidgetSprites(
			sprite("compendium/close"), sprite("compendium/close_highlighted"));

	private static final int ARROW_W = 18;
	private static final int ARROW_H = 11;
	private static final int SEARCH_W = 84;
	private static final int FILTER_W = 54;

	// Parchment-ink palette, shared with the detail screen.
	static final int COL_TEXT = 0xFF4A3620;
	static final int COL_TEXT_FADED = 0xFF8A7350;
	private static final int COL_BAR_TRACK = 0xFFC8B08A;
	private static final int COL_BAR_BORDER = 0xFF6B4F2A;
	private static final int COL_BAR_FILL = 0xFFD9A334;
	private static final int COL_BAR_FILL_LIGHT = 0xFFF2CD60;
	private static final int COL_HOVER = 0x30FFFFFF;

	/** How often the collected set (inventory + synced progress) is re-read while the screen is open. */
	private static final float COLLECTED_REFRESH_SECONDS = 1f;

	final CompendiumType type;

	/** Base stacks, one per entry, in stable display order; built once. */
	private final List<ItemStack> entries;
	/** Spin state, parallel to {@link #entries}. */
	private final List<DollSpin> spins = new ArrayList<>();
	private final FrameClock clock = new FrameClock();

	/** Which slice of the collection the grid shows; cycled by the footer chip. */
	private enum CollectedFilter {
		ALL, FOUND, MISSING;

		Component label() {
			return Component.translatable("screen.pokeblocks.compendium.filter." + name().toLowerCase(Locale.ROOT));
		}
	}

	private Set<String> collected = Set.of();
	/** Indices into {@link #entries} that survive the current search + found/missing filter. */
	private List<Integer> visible = List.of();
	private String query = "";
	private CollectedFilter filter = CollectedFilter.ALL;
	private float refreshTimer;

	private int page = 0;
	private int columns = 7;
	private int rows = 4;
	private int panelLeft;
	private int panelTop;
	private int panelWidth;
	private int panelHeight;
	private int gridOriginX;
	private int gridOriginY;
	private int footerY;

	private ImageButton prevButton;
	private ImageButton nextButton;
	private CompendiumChipButton filterButton;
	private EditBox searchBox;

	public CompendiumScreen(CompendiumType type) {
		super(type.title());
		this.type = type;
		this.entries = type.entries();
		for (int i = 0; i < entries.size(); i++) {
			spins.add(new DollSpin());
		}
	}

	private static ResourceLocation sprite(String path) {
		return ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, path);
	}

	@Override
	protected void init() {
		collected = type.collectedIds(this.minecraft != null ? this.minecraft.player : null);
		refreshTimer = 0f;

		// The grid adapts to the window so the panel never overflows small GUI-scale setups:
		// ~71px of vertical chrome (frame, header, footer, margins) and 24px horizontal.
		columns = Mth.clamp((this.width - 24) / CELL, 4, 7);
		rows = Mth.clamp((this.height - 71) / CELL, 2, 4);

		// Vertical anatomy (relative to panelTop): 8px wood frame, header (title on its own row,
		// then progress bar left + search box right at +21..35), grid at +36, footer, frame again.
		panelWidth = columns * CELL + 20;
		panelHeight = rows * CELL + 63;
		panelLeft = (this.width - panelWidth) / 2;
		panelTop = (this.height - panelHeight) / 2;
		gridOriginX = panelLeft + 10;
		gridOriginY = panelTop + 36;
		footerY = gridOriginY + rows * CELL + 3;

		prevButton = addRenderableWidget(new ImageButton(
				gridOriginX, footerY + 2, ARROW_W, ARROW_H, SPRITES_PAGE_BACKWARD, b -> changePage(-1)));
		nextButton = addRenderableWidget(new ImageButton(
				panelLeft + panelWidth - 10 - ARROW_W, footerY + 2, ARROW_W, ARROW_H, SPRITES_PAGE_FORWARD, b -> changePage(1)));

		// Themed close pad in the title row's top-right corner (E and ESC close too).
		addRenderableWidget(new ImageButton(
				panelLeft + panelWidth - 10 - 12, panelTop + 9, 12, 12, SPRITES_CLOSE, b -> onClose()));

		// Found/missing filter chip, tucked beside the back arrow in the footer.
		filterButton = addRenderableWidget(new CompendiumChipButton(
				gridOriginX + ARROW_W + 6, footerY, FILTER_W, 14, filter.label(), b -> cycleFilter()));

		searchBox = new EditBox(this.font, panelLeft + panelWidth - 8 - SEARCH_W + 5, panelTop + 24,
				SEARCH_W - 10, 10, Component.translatable("screen.pokeblocks.compendium.search"));
		searchBox.setBordered(false);
		searchBox.setMaxLength(40);
		searchBox.setTextColor(COL_TEXT);
		searchBox.setHint(Component.translatable("screen.pokeblocks.compendium.search")
				.withStyle(ChatFormatting.ITALIC).withColor(COL_TEXT_FADED & 0xFFFFFF));
		searchBox.setValue(query);
		searchBox.setResponder(text -> {
			if (!text.equals(query)) {
				query = text;
				applyFilter();
			}
		});
		addRenderableWidget(searchBox);

		applyFilter();
	}

	private void cycleFilter() {
		filter = CollectedFilter.values()[(filter.ordinal() + 1) % CollectedFilter.values().length];
		filterButton.setMessage(filter.label());
		applyFilter();
	}

	/** Rebuilds {@link #visible} from the query + found/missing filter; never leaks undiscovered names. */
	private void applyFilter() {
		String q = query.trim().toLowerCase(Locale.ROOT);
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < entries.size(); i++) {
			ItemStack stack = entries.get(i);
			boolean found = collected.contains(type.idOf(stack));
			if (filter == CollectedFilter.FOUND && !found) continue;
			if (filter == CollectedFilter.MISSING && found) continue;
			if (q.isEmpty() || matchesQuery(stack, found, q)) {
				result.add(i);
			}
		}
		visible = result;
		page = Mth.clamp(page, 0, pageCount() - 1);
		updateNavState();
	}

	private boolean matchesQuery(ItemStack stack, boolean found, String query) {
		// Undiscovered entries never match — searching must not reveal hidden names.
		if (!found) return false;
		return stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(query);
	}

	private int perPage() {
		return columns * rows;
	}

	private int pageCount() {
		return Math.max(1, (visible.size() + perPage() - 1) / perPage());
	}

	private void changePage(int delta) {
		int target = Mth.clamp(page + delta, 0, pageCount() - 1);
		if (target != page) {
			page = target;
			playPageSound();
			updateNavState();
		}
	}

	private void updateNavState() {
		if (prevButton != null) prevButton.visible = page > 0;
		if (nextButton != null) nextButton.visible = page < pageCount() - 1;
	}

	private void playPageSound() {
		if (this.minecraft != null) {
			this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1f));
		}
	}

	/** Re-reads the collected set once a second so pickups and sync updates fill in live. */
	private void refreshCollected() {
		Set<String> now = type.collectedIds(this.minecraft != null ? this.minecraft.player : null);
		if (!now.equals(collected)) {
			collected = now;
			if (!query.isEmpty() || filter != CollectedFilter.ALL) {
				applyFilter();
			}
		}
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		// Panel + static chrome go under the widgets (search text, arrows) drawn by super.render.
		guiGraphics.blitSprite(SPRITE_PANEL, panelLeft, panelTop, panelWidth, panelHeight);
		guiGraphics.blitSprite(SPRITE_SEARCH, panelLeft + panelWidth - 8 - SEARCH_W, panelTop + 21, SEARCH_W, 14);
		for (int slot = 0; slot < perPage(); slot++) {
			int cellX = gridOriginX + (slot % columns) * CELL;
			int cellY = gridOriginY + (slot / columns) * CELL;
			guiGraphics.blitSprite(SPRITE_SLOT, cellX, cellY, CELL, CELL);
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		clock.tick();
		refreshTimer += clock.dt();
		if (refreshTimer >= COLLECTED_REFRESH_SECONDS) {
			refreshTimer = 0f;
			refreshCollected();
		}

		// Header: title, then the collection progress bar under it.
		guiGraphics.drawString(this.font, this.title, panelLeft + 10, panelTop + 12, COL_TEXT, false);
		drawProgressBar(guiGraphics, panelLeft + 10, panelTop + 24, 90, 9);

		// Footer: page indicator between the filter chip and the forward arrow (shadow-free —
		// it sits on parchment). Centered when there's room, else pushed right of the chip.
		Component pageLabel = Component.translatable("screen.pokeblocks.compendium.page", page + 1, pageCount());
		int labelX = Math.max(panelLeft + (panelWidth - this.font.width(pageLabel)) / 2,
				gridOriginX + ARROW_W + 6 + FILTER_W + 6);
		guiGraphics.drawString(this.font, pageLabel, labelX, footerY + 3, COL_TEXT, false);

		ItemStack hovered = null;
		boolean hoveredCollected = false;

		int start = page * perPage();
		int end = Math.min(start + perPage(), visible.size());
		for (int i = start; i < end; i++) {
			int slot = i - start;
			int cellX = gridOriginX + (slot % columns) * CELL;
			int cellY = gridOriginY + (slot / columns) * CELL;
			boolean hover = mouseX >= cellX && mouseX < cellX + CELL && mouseY >= cellY && mouseY < cellY + CELL;
			if (hover) {
				guiGraphics.fill(cellX + 3, cellY + 3, cellX + CELL - 3, cellY + CELL - 3, COL_HOVER);
			}

			int index = visible.get(i);
			ItemStack stack = entries.get(index);
			boolean isCollected = collected.contains(type.idOf(stack));

			DollSpin spin = spins.get(index);
			spin.update(clock.dt());
			CompendiumRender.render(guiGraphics, type, stack,
					cellX + CELL / 2f, cellY + CELL / 2f, ITEM_SCALE, !isCollected, spin.yaw());

			if (hover) {
				hovered = stack;
				hoveredCollected = isCollected;
			}
		}

		if (hovered != null) {
			Component name = hoveredCollected
					? hovered.getHoverName()
					: Component.translatable("screen.pokeblocks.compendium.unknown").withStyle(ChatFormatting.DARK_GRAY);
			guiGraphics.renderTooltip(this.font, name, mouseX, mouseY);
		}
	}

	private void drawProgressBar(GuiGraphics guiGraphics, int x, int y, int width, int height) {
		int collectedCount = countCollected();
		int total = entries.size();
		float fraction = total == 0 ? 0f : (float) collectedCount / total;
		int fillWidth = Math.round((width - 2) * fraction);

		guiGraphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, COL_BAR_BORDER);
		guiGraphics.fill(x, y, x + width, y + height, COL_BAR_TRACK);
		if (fillWidth > 0) {
			guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + height - 1, COL_BAR_FILL);
			guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + 2, COL_BAR_FILL_LIGHT);
		}
		String label = collectedCount + "/" + total;
		guiGraphics.drawString(this.font, label, x + (width - this.font.width(label)) / 2, y + 1, COL_TEXT, false);
	}

	private int countCollected() {
		int count = 0;
		for (ItemStack stack : entries) {
			if (collected.contains(type.idOf(stack))) count++;
		}
		return count;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) return true;
		if (button == 0) {
			int i = cellIndexAt(mouseX, mouseY);
			if (i >= 0 && this.minecraft != null) {
				List<ItemStack> order = new ArrayList<>(visible.size());
				for (int index : visible) {
					order.add(entries.get(index));
				}
				playPageSound();
				this.minecraft.setScreen(new CompendiumDetailScreen(this, type, order, i));
				return true;
			}
		}
		return false;
	}

	/** Index into {@link #visible} of the populated cell under the cursor on the current page, or -1. */
	private int cellIndexAt(double mouseX, double mouseY) {
		int start = page * perPage();
		int end = Math.min(start + perPage(), visible.size());
		for (int i = start; i < end; i++) {
			int slot = i - start;
			int cellX = gridOriginX + (slot % columns) * CELL;
			int cellY = gridOriginY + (slot / columns) * CELL;
			if (mouseX >= cellX && mouseX < cellX + CELL && mouseY >= cellY && mouseY < cellY + CELL) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
		if (scrollY != 0) {
			changePage(scrollY < 0 ? 1 : -1);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// Super first: a focused search box consumes arrows/typing before they can flip pages.
		if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
		// The inventory key closes the book like a vanilla container — unless it's being typed
		// into the search box.
		if (!searchBox.isFocused() && this.minecraft != null
				&& this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
			onClose();
			return true;
		}
		switch (keyCode) {
			case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_PAGE_UP -> {
				changePage(-1);
				return true;
			}
			case GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_PAGE_DOWN -> {
				changePage(1);
				return true;
			}
		}
		return false;
	}

	/**
	 * True while the doll compendium (index or detail page) is the active screen. Used by world
	 * doll rendering to ease placed dolls into facing the camera.
	 */
	public static boolean isOpen() {
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof CompendiumScreen index) return index.type == CompendiumType.DOLLS;
		if (screen instanceof CompendiumDetailScreen detail) return detail.type() == CompendiumType.DOLLS;
		return false;
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
