package dev.mrshawn.pokeblocks.client.screen;

import net.minecraft.client.Minecraft;

/**
 * Tiny client-only indirection so {@link dev.mrshawn.pokeblocks.item.custom.FigurineCompendiumItem} can
 * open the screen without statically referencing {@link Minecraft} on the server class path. Only invoked
 * from the item's {@code isClientSide} branch, so it never loads server-side.
 */
public final class FigurineCompendiumClientHooks {

    private FigurineCompendiumClientHooks() {}

    public static void open() {
        Minecraft.getInstance().setScreen(new FigurineCompendiumScreen());
    }
}
