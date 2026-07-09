package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.compendium.CompendiumVariantKey;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

/**
 * "Open book" detail page for a single compendium entry. Left page shows a large render that can
 * be dragged to inspect (auto-spin resumes a moment after letting go); right page shows the name,
 * a blurb, the entry's valid variants as clickable mini-slots (each silhouetted until that exact
 * variant has been collected), and the selected variant's collection status. Figurine pages get a
 * chip that hides the display case. The corner arrows flip through the index's current (filtered)
 * entry order without leaving the book; ESC or the corner pad returns to the index, the inventory
 * key closes the book entirely.
 */
public class CompendiumDetailScreen extends Screen {

	private static final int BOOK_W = 256;
	private static final int BOOK_H = 180;

	private static final ResourceLocation BOOK_TEXTURE =
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "textures/gui/compendium/book_spread.png");
	private static final ResourceLocation SPRITE_SLOT =
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium/slot");
	private static final WidgetSprites SPRITES_PAGE_BACKWARD = new WidgetSprites(
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium/page_backward"),
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium/page_backward_highlighted"));
	private static final WidgetSprites SPRITES_PAGE_FORWARD = new WidgetSprites(
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium/page_forward"),
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium/page_forward_highlighted"));

	private static final int ARROW_W = 18;
	private static final int ARROW_H = 11;

	private static final float ENTRY_SCALE = 6f;

	// Left page (drag/inspect zone) and big-render anchor, relative to the book origin.
	private static final int PAGE_LEFT_X0 = 8;
	private static final int PAGE_LEFT_X1 = 124;
	private static final int PAGE_Y0 = 6;
	private static final int PAGE_Y1 = 169;
	private static final float RENDER_CX = 66f;
	private static final float RENDER_CY = 92f;

	// Variant strip geometry (right page).
	private static final int VS_X = 140;
	private static final int VS_Y = 79;
	private static final int VS_CELL = 20;
	private static final int VS_COLS = 5;
	private static final int VS_MAX = 10;

	/** How long after the last drag before the auto-spin takes over again. */
	private static final long RESUME_SPIN_MS = 2500;
	private static final float DRAG_SENSITIVITY = 2f;
	private static final float MAX_PITCH = 80f;

	// Ink colors on the parchment art.
	private static final int COL_TEXT = CompendiumScreen.COL_TEXT;
	private static final int COL_TEXT_FADED = CompendiumScreen.COL_TEXT_FADED;
	private static final int COL_COLLECTED = 0xFF3E6B34;
	private static final int COL_SELECTED = 0xFFD9A334;
	private static final int COL_HOVER = 0x30FFFFFF;

	private final Screen parent;
	private final CompendiumType type;
	/** The index's entry order (its current filter view); the corner arrows walk this list. */
	private final List<ItemStack> order;
	private int index;

	/** Valid variants of the current entry (empty for figurines) and which one is selected. */
	private List<ItemStack> variants = List.of();
	private int selectedVariant = 0;
	private Set<String> collectedKeys = Set.of();
	private boolean hideBox = false;

	private final DollSpin spin = new DollSpin();
	private final FrameClock clock = new FrameClock();
	private float refreshTimer;

	// Manual drag-to-inspect state. While recently touched, the manual yaw/pitch drives the
	// render; afterwards the pitch eases home and the auto-spin resumes from the manual angle.
	private boolean dragging = false;
	private boolean wasManual = false;
	private long lastManualMs = -1_000_000L;
	private float manualYaw = 0f;
	private float manualPitch = 0f;

	private int left;
	private int top;
	private ImageButton prevButton;
	private ImageButton nextButton;
	private CompendiumChipButton boxButton;

	public CompendiumDetailScreen(Screen parent, CompendiumType type, List<ItemStack> order, int index) {
		super(type.title());
		this.parent = parent;
		this.type = type;
		this.order = order;
		this.index = index;
		loadEntry();
	}

	CompendiumType type() {
		return type;
	}

	/** The stack the big render and right page describe: the selected variant, or the entry itself. */
	private ItemStack current() {
		return variants.isEmpty() ? order.get(index) : variants.get(selectedVariant);
	}

	/** Recomputes variants, selection and collected keys for the entry at {@link #index}. */
	private void loadEntry() {
		ItemStack entry = order.get(index);
		variants = type.variantsOf(type.idOf(entry));
		selectedVariant = 0;
		String entryKey = type.progressKeyOf(entry);
		for (int i = 0; i < variants.size(); i++) {
			if (type.progressKeyOf(variants.get(i)).equals(entryKey)) {
				selectedVariant = i;
				break;
			}
		}
		collectedKeys = type.collectedKeys(this.minecraft != null ? this.minecraft.player : null);
	}

	private boolean variantCollected(ItemStack stack) {
		return collectedKeys.contains(type.progressKeyOf(stack));
	}

	/** Whether ANY variant of the current entry is collected (drives name/description visibility). */
	private boolean entryDiscovered() {
		String species = type.idOf(order.get(index));
		for (String key : collectedKeys) {
			if (CompendiumVariantKey.speciesOf(key).equals(species)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void init() {
		left = (this.width - BOOK_W) / 2;
		top = (this.height - BOOK_H) / 2 - 4;

		// The constructor ran before this.minecraft was attached, so the first key read may have
		// missed the inventory scan; re-read now (without resetting the variant selection).
		collectedKeys = type.collectedKeys(this.minecraft != null ? this.minecraft.player : null);

		// Page-corner arrows, tucked into the bottom outer corners of the two pages.
		prevButton = addRenderableWidget(new ImageButton(
				left + 18, top + BOOK_H - 26, ARROW_W, ARROW_H, SPRITES_PAGE_BACKWARD, b -> flip(-1)));
		nextButton = addRenderableWidget(new ImageButton(
				left + BOOK_W - 18 - ARROW_W, top + BOOK_H - 26, ARROW_W, ARROW_H, SPRITES_PAGE_FORWARD, b -> flip(1)));

		// Close pad on the cover corner: back to the index (ESC does the same; E closes fully).
		addRenderableWidget(new ImageButton(
				left + BOOK_W - 13, top + 3, 12, 12, CompendiumScreen.SPRITES_CLOSE, b -> onClose()));

		// Figurine pages: toggle the display case around the figure.
		boxButton = addRenderableWidget(new CompendiumChipButton(
				left + 158, top + 118, 64, 14, boxLabel(), b -> {
			hideBox = !hideBox;
			boxButton.setMessage(boxLabel());
		}));

		refreshWidgetState();
	}

	private Component boxLabel() {
		return Component.translatable(hideBox
				? "screen.pokeblocks.compendium.show_box"
				: "screen.pokeblocks.compendium.hide_box");
	}

	private void refreshWidgetState() {
		if (prevButton != null) prevButton.visible = index > 0;
		if (nextButton != null) nextButton.visible = index < order.size() - 1;
		if (boxButton != null) {
			boxButton.visible = type.hasHideableBox() && variantCollected(current());
		}
	}

	/** Steps to the neighbouring entry in the index's order, refreshing its state. */
	private void flip(int delta) {
		int target = Mth.clamp(index + delta, 0, order.size() - 1);
		if (target != index) {
			index = target;
			loadEntry();
			playPageSound();
			refreshWidgetState();
		}
	}

	private void playPageSound() {
		if (this.minecraft != null) {
			this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1f));
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		clock.tick();
		refreshTimer += clock.dt();
		if (refreshTimer >= 1f) {
			refreshTimer = 0f;
			collectedKeys = type.collectedKeys(this.minecraft != null ? this.minecraft.player : null);
			refreshWidgetState();
		}

		ItemStack shown = current();
		boolean entryDiscovered = entryDiscovered();
		boolean shownCollected = variantCollected(shown);

		// Left page: large render, manually steered while recently dragged, else auto-spinning.
		float yaw;
		float pitch;
		if (dragging || Util.getMillis() - lastManualMs < RESUME_SPIN_MS) {
			wasManual = true;
			yaw = manualYaw;
			pitch = manualPitch;
		} else {
			if (wasManual) {
				// Hand the manual angle back to the auto-spin so there's no visible jump.
				spin.snapTo(manualYaw);
				wasManual = false;
			}
			// Ease any leftover tilt home while spinning.
			manualPitch += (0f - manualPitch) * (1f - (float) Math.exp(-clock.dt() * 5f));
			spin.update(clock.dt());
			manualYaw = spin.yaw();
			yaw = manualYaw;
			pitch = manualPitch;
		}
		CompendiumRender.render(guiGraphics, type, shown, left + RENDER_CX, top + RENDER_CY, ENTRY_SCALE,
				!shownCollected, yaw, pitch, hideBox && boxButton != null && boxButton.visible);

		// Right page: title over the baked divider, wrapped body text, variants, status line.
		int textLeft = left + 140;
		int textRight = left + 240;
		int pageCenterX = (textLeft + textRight) / 2;

		Component title = entryDiscovered
				? shown.getHoverName()
				: Component.translatable("screen.pokeblocks.compendium.unknown");
		guiGraphics.drawString(this.font, title,
				pageCenterX - this.font.width(title) / 2, top + 26, COL_TEXT, false);

		Component body = entryDiscovered
				? type.descriptionFor(type.idOf(shown))
				: type.lockedText();
		// The blurb yields to whatever sits below it: the variant strip, the hide-box chip, or
		// (with neither) the status rule.
		int bodyLimitY = top + 132;
		if (variants.size() > 1) {
			bodyLimitY = top + VS_Y - 10;
		} else if (boxButton != null && boxButton.visible) {
			bodyLimitY = top + 114;
		}
		int textY = top + 48;
		for (FormattedCharSequence line : this.font.split(body, textRight - textLeft)) {
			if (textY + this.font.lineHeight > bodyLimitY) break;
			guiGraphics.drawString(this.font, line, textLeft, textY, COL_TEXT, false);
			textY += this.font.lineHeight + 2;
		}

		renderVariantStrip(guiGraphics, mouseX, mouseY);

		Component status = shownCollected
				? Component.translatable("screen.pokeblocks.compendium.collected")
				: Component.translatable("screen.pokeblocks.compendium.undiscovered")
						.withStyle(ChatFormatting.ITALIC);
		guiGraphics.drawString(this.font, status,
				pageCenterX - this.font.width(status) / 2, top + BOOK_H - 38,
				shownCollected ? COL_COLLECTED : COL_TEXT_FADED, false);
	}

	/** The clickable per-variant mini-slots (only for entries that actually have variants). */
	private void renderVariantStrip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		if (variants.size() <= 1) return;

		guiGraphics.drawString(this.font, Component.translatable("screen.pokeblocks.compendium.variants"),
				left + VS_X, top + VS_Y - 10, COL_TEXT_FADED, false);

		int shown = Math.min(variants.size(), VS_MAX);
		ItemStack hovered = null;
		boolean hoveredCollected = false;
		for (int i = 0; i < shown; i++) {
			int cellX = left + VS_X + (i % VS_COLS) * VS_CELL;
			int cellY = top + VS_Y + (i / VS_COLS) * VS_CELL;
			guiGraphics.blitSprite(SPRITE_SLOT, cellX, cellY, VS_CELL, VS_CELL);
			if (i == selectedVariant) {
				// Gold selection ring.
				guiGraphics.fill(cellX, cellY, cellX + VS_CELL, cellY + 1, COL_SELECTED);
				guiGraphics.fill(cellX, cellY + VS_CELL - 1, cellX + VS_CELL, cellY + VS_CELL, COL_SELECTED);
				guiGraphics.fill(cellX, cellY, cellX + 1, cellY + VS_CELL, COL_SELECTED);
				guiGraphics.fill(cellX + VS_CELL - 1, cellY, cellX + VS_CELL, cellY + VS_CELL, COL_SELECTED);
			}

			boolean hover = mouseX >= cellX && mouseX < cellX + VS_CELL && mouseY >= cellY && mouseY < cellY + VS_CELL;
			if (hover) {
				guiGraphics.fill(cellX + 1, cellY + 1, cellX + VS_CELL - 1, cellY + VS_CELL - 1, COL_HOVER);
			}

			ItemStack variant = variants.get(i);
			boolean isCollected = variantCollected(variant);
			// Static three-quarter angle: readable depth without ten spinning thumbnails.
			CompendiumRender.render(guiGraphics, type, variant,
					cellX + VS_CELL / 2f, cellY + VS_CELL / 2f, 1f, !isCollected, -30f);

			if (hover) {
				hovered = variant;
				hoveredCollected = isCollected;
			}
		}

		if (variants.size() > VS_MAX) {
			guiGraphics.drawString(this.font, "+" + (variants.size() - VS_MAX),
					left + VS_X + VS_COLS * VS_CELL + 3, top + VS_Y + VS_CELL + 6, COL_TEXT_FADED, false);
		}

		if (hovered != null) {
			Component name = hoveredCollected
					? hovered.getHoverName()
					: Component.translatable("screen.pokeblocks.compendium.unknown").withStyle(ChatFormatting.DARK_GRAY);
			guiGraphics.renderTooltip(this.font, name, mouseX, mouseY);
		}
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.blit(BOOK_TEXTURE, left, top, 0, 0, BOOK_W, BOOK_H, BOOK_W, BOOK_H);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (super.mouseClicked(mouseX, mouseY, button)) return true;
		if (button != 0) return false;

		int variant = variantIndexAt(mouseX, mouseY);
		if (variant >= 0) {
			if (variant != selectedVariant) {
				selectedVariant = variant;
				refreshWidgetState();
				if (this.minecraft != null) {
					this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
				}
			}
			return true;
		}

		// Grab the render to inspect it by hand.
		if (isInLeftPage(mouseX, mouseY)) {
			dragging = true;
			if (!wasManual) {
				manualYaw = spin.yaw();
			}
			lastManualMs = Util.getMillis();
			return true;
		}
		return false;
	}

	private boolean isInLeftPage(double mouseX, double mouseY) {
		return mouseX >= left + PAGE_LEFT_X0 && mouseX < left + PAGE_LEFT_X1
				&& mouseY >= top + PAGE_Y0 && mouseY < top + PAGE_Y1;
	}

	/** Index of the variant mini-slot under the cursor, or -1. */
	private int variantIndexAt(double mouseX, double mouseY) {
		if (variants.size() <= 1) return -1;
		int shown = Math.min(variants.size(), VS_MAX);
		for (int i = 0; i < shown; i++) {
			int cellX = left + VS_X + (i % VS_COLS) * VS_CELL;
			int cellY = top + VS_Y + (i / VS_COLS) * VS_CELL;
			if (mouseX >= cellX && mouseX < cellX + VS_CELL && mouseY >= cellY && mouseY < cellY + VS_CELL) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (dragging && button == 0) {
			manualYaw = Mth.wrapDegrees(manualYaw + (float) dragX * DRAG_SENSITIVITY);
			manualPitch = Mth.clamp(manualPitch + (float) dragY * DRAG_SENSITIVITY, -MAX_PITCH, MAX_PITCH);
			lastManualMs = Util.getMillis();
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (dragging && button == 0) {
			dragging = false;
			lastManualMs = Util.getMillis();
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
		if (scrollY != 0) {
			flip(scrollY < 0 ? 1 : -1);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
		// The inventory key drops the whole book, vanilla-container style; ESC steps back instead.
		if (this.minecraft != null && this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
			playPageSound();
			this.minecraft.setScreen(null);
			return true;
		}
		switch (keyCode) {
			case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_PAGE_UP -> {
				flip(-1);
				return true;
			}
			case GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_PAGE_DOWN -> {
				flip(1);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void renderBlurredBackground(float partialTick) {
		// Intentionally empty: suppresses the vanilla menu blur.
	}

	@Override
	public void onClose() {
		// Return to the index rather than closing the whole UI — flip back a page.
		playPageSound();
		if (this.minecraft != null) {
			this.minecraft.setScreen(parent);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
