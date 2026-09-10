package dev.mrshawn.pokeblocks.client.trapped;

import dev.mrshawn.pokeblocks.item.TrappedDolls;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

/**
 * The wearer's own detonation countdown: while a ticking trapped doll (see {@link TrappedDolls})
 * sits on the local player's head, a big red timer counts down in the upper middle of the screen —
 * hundredths rolling every frame, shaking harder (and flashing white) as the deadline closes in.
 * <p>
 * Reads the deadline straight off the synced head stack, so it needs no networking and stays exact
 * even if a broadcast is missed. Rendered from {@code TrappedDollHudMixin} at the tail of
 * {@code Gui#render} — one hook that fires on all three loaders (Forge 52 ships no HUD render
 * event at all, and no longer replaces the vanilla {@code Gui}).
 */
public final class TrappedDollHudRenderer {

	private static final int RED = 0xFF3030;
	private static final int FLASH_WHITE = 0xFFFFFF;
	private static final float SCALE = 3.0f;
	/** Flash the timer white on alternating intervals once this close to zero. */
	private static final int FLASH_BELOW_TICKS = 60;

	private TrappedDollHudRenderer() {}

	public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		if (player == null || minecraft.level == null || minecraft.options.hideGui) {
			return;
		}
		ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
		if (!TrappedDolls.isTicking(head)) {
			return;
		}

		float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
		long gameTime = minecraft.level.getGameTime();
		float remainingTicks = Math.max(0.0f, TrappedDolls.detonateAt(head) - gameTime - partialTick);
		String text = String.format(Locale.ROOT, "%.2f", remainingTicks / 20.0f);

		// Shake: high-frequency sines sampled per frame alias into a jitter that ramps from a
		// nervous wobble to a violent rattle as the fuse runs down.
		float urgency = 1.0f - Mth.clamp(remainingTicks / TrappedDolls.DETONATION_TICKS, 0.0f, 1.0f);
		float amplitude = 1.0f + urgency * 6.0f;
		float time = (gameTime % 100_000) + partialTick;
		float shakeX = Mth.sin(time * 37.7f) * amplitude;
		float shakeY = Mth.cos(time * 41.3f) * amplitude;

		boolean flash = remainingTicks < FLASH_BELOW_TICKS && (gameTime / 4) % 2 == 0;

		graphics.pose().pushPose();
		graphics.pose().translate(graphics.guiWidth() / 2.0f + shakeX, graphics.guiHeight() * 0.28f + shakeY, 0.0f);
		graphics.pose().scale(SCALE, SCALE, 1.0f);
		graphics.drawCenteredString(minecraft.font, text, 0, 0, flash ? FLASH_WHITE : RED);
		graphics.pose().popPose();
	}
}
