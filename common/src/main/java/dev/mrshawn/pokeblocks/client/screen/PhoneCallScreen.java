package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.phone.ClientDigSites;
import dev.mrshawn.pokeblocks.phone.PhoneCalls;
import dev.mrshawn.pokeblocks.phone.PhoneCalls.LostDollTarget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * The incoming-call screen a ringing Pokedoll Phone opens: the calling doll spins inside a
 * pokeball-ring portrait next to its plea ("I buried my favorite doll nearby — find it!"), with
 * Accept / Hang Up choices. Exactly one answer is ever sent: pressing a button, closing with ESC
 * or the inventory key all resolve the call (close = hang up), and the server clears the phone's
 * call state either way. Reuses the compendium's parchment panel and chip buttons plus the shared
 * {@link CompendiumRender}/{@link DollSpin} doll rendering.
 */
public class PhoneCallScreen extends Screen {

	private static final ResourceLocation SPRITE_PANEL =
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium/panel");
	private static final ResourceLocation TEX_PORTRAIT_RING =
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "textures/gui/phone/portrait_ring.png");

	private static final int PANEL_W = 252;
	private static final int PANEL_H = 112;
	private static final int RING_SIZE = 64;
	private static final int BUTTON_W = 64;
	private static final int BUTTON_H = 16;
	private static final float PORTRAIT_SCALE = 2.2f;

	private final ItemStack callerStack;
	private final Component callerName;
	private final Component callText;
	private final DollSpin spin = new DollSpin();
	private final FrameClock clock = new FrameClock();

	private int panelLeft;
	private int panelTop;
	private boolean responded;

	public PhoneCallScreen(String callerKey, LostDollTarget lost) {
		super(Component.translatable("screen.pokeblocks.phone.title"));
		this.callerStack = PhoneCalls.createDoll(callerKey == null ? "" : callerKey);
		// The caller's name is the doll's display name without the trailing "Pokedoll" word, read
		// from the same stack the portrait renders, so the name always matches the doll shown.
		this.callerName = PokedollItem.displayName(callerStack, false);
		// The plea names what the caller lost, tinted with that rarity's colour (a rarity tier, or a
		// compact min–max rarity-percent window). The two percent numbers keep the line short.
		Component descriptor = lost == null
				? Component.translatable("screen.pokeblocks.phone.lost_unknown")
				: PhoneCalls.describeLostDoll(lost);
		this.callText = Component.translatable("screen.pokeblocks.phone.call_text", descriptor);
	}

	@Override
	protected void init() {
		panelLeft = (width - PANEL_W) / 2;
		panelTop = (height - PANEL_H) / 2;

		int buttonY = panelTop + PANEL_H - BUTTON_H - 12;
		int textX = panelLeft + RING_SIZE + 28;
		addRenderableWidget(new CompendiumChipButton(textX, buttonY, BUTTON_W, BUTTON_H,
				Component.translatable("screen.pokeblocks.phone.accept"), button -> respond(true)));
		addRenderableWidget(new CompendiumChipButton(textX + BUTTON_W + 10, buttonY, BUTTON_W, BUTTON_H,
				Component.translatable("screen.pokeblocks.phone.hangup"), button -> respond(false)));
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.blitSprite(SPRITE_PANEL, panelLeft, panelTop, PANEL_W, PANEL_H);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		clock.tick();
		spin.update(clock.dt());

		// Title strip.
		guiGraphics.drawCenteredString(font, title, panelLeft + PANEL_W / 2, panelTop + 10, CompendiumScreen.COL_TEXT);

		// Caller portrait: pokeball ring with the doll spinning inside it.
		int ringX = panelLeft + 16;
		int ringY = panelTop + 30;
		guiGraphics.blit(TEX_PORTRAIT_RING, ringX, ringY, 0, 0, RING_SIZE, RING_SIZE, RING_SIZE, RING_SIZE);
		CompendiumRender.render(guiGraphics, CompendiumType.DOLLS, callerStack,
				ringX + RING_SIZE / 2f, ringY + RING_SIZE / 2f + 2f, PORTRAIT_SCALE, false, spin.yaw());

		// Caller name + plea.
		int textX = panelLeft + RING_SIZE + 28;
		int textWidth = panelLeft + PANEL_W - 14 - textX;
		guiGraphics.drawString(font, callerName, textX, panelTop + 26, CompendiumScreen.COL_TEXT, false);

		List<FormattedCharSequence> lines = font.split(callText, textWidth);
		int lineY = panelTop + 40;
		for (FormattedCharSequence line : lines) {
			if (lineY > panelTop + PANEL_H - BUTTON_H - 24) break;
			guiGraphics.drawString(font, line, textX, lineY, CompendiumScreen.COL_TEXT_FADED, false);
			lineY += 10;
		}
	}

	/** Sends exactly one answer, then closes. */
	private void respond(boolean accept) {
		if (!responded) {
			responded = true;
			ClientDigSites.sendCallResponse(accept);
		}
		onClose();
	}

	@Override
	public void onClose() {
		// Closing without choosing (ESC, inventory key) hangs up.
		if (!responded) {
			responded = true;
			ClientDigSites.sendCallResponse(false);
		}
		super.onClose();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		// The inventory key closes the call like a container screen (matches the compendium).
		if (minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
			onClose();
			return true;
		}
		return false;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
