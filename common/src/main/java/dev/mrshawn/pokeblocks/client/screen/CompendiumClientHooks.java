package dev.mrshawn.pokeblocks.client.screen;

import net.minecraft.client.Minecraft;

/**
 * Tiny client-only indirection so the compendium items can open their screens without statically
 * referencing {@link Minecraft} on the server class path. Only invoked from the items'
 * {@code isClientSide} branches, so it never loads server-side.
 */
public final class CompendiumClientHooks {

	private CompendiumClientHooks() {}

	public static void openDolls() {
		Minecraft.getInstance().setScreen(new CompendiumScreen(CompendiumType.DOLLS));
	}

	public static void openFigurines() {
		Minecraft.getInstance().setScreen(new CompendiumScreen(CompendiumType.FIGURINES));
	}
}
