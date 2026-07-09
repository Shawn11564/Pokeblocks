package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * A small parchment "chip" button matching the compendium theme (used for the found/missing filter
 * and the figurine hide-box toggle) — a raised nine-slice pad with dark ink text, instead of the
 * vanilla grey button.
 */
public class CompendiumChipButton extends Button {

	private static final ResourceLocation SPRITE =
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium/chip");
	private static final ResourceLocation SPRITE_HIGHLIGHTED =
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium/chip_highlighted");

	public CompendiumChipButton(int x, int y, int width, int height, Component message, OnPress onPress) {
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		guiGraphics.blitSprite(isHoveredOrFocused() ? SPRITE_HIGHLIGHTED : SPRITE,
				getX(), getY(), getWidth(), getHeight());
		Font font = Minecraft.getInstance().font;
		int textX = getX() + (getWidth() - font.width(getMessage())) / 2;
		int textY = getY() + (getHeight() - 8) / 2 + 1;
		guiGraphics.drawString(font, getMessage(), textX, textY, CompendiumScreen.COL_TEXT, false);
	}
}
