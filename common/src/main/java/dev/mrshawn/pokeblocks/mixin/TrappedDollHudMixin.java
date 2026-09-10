package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.client.trapped.TrappedDollHudRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the wearer's trapped-doll countdown over the HUD (see {@link TrappedDollHudRenderer}).
 * A mixin rather than per-loader HUD events because Forge 52 (1.21.1) ships neither a GUI render
 * event nor its old {@code ForgeGui} subclass — vanilla {@code Gui#render} runs verbatim there,
 * and it is also the method Fabric's and NeoForge's own HUD hooks wrap, so one tail injection
 * covers all three loaders exactly once.
 */
@Mixin(Gui.class)
public abstract class TrappedDollHudMixin {

	@Inject(method = "render", at = @At("TAIL"))
	private void pokeblocks$renderTrappedCountdown(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		TrappedDollHudRenderer.render(guiGraphics, deltaTracker);
	}
}
