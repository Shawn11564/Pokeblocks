package dev.mrshawn.pokeblocks.client.addon;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * An extra section of a compendium book, contributed by an add-on (see
 * {@link PokeblocksClientAddon#compendiumSections}). The book's index page shows each unlocked
 * section as a bookmark tab down its right edge; clicking the tab opens the section's own screen.
 */
public interface CompendiumSection {

	/** The section's name, shown when the tab is hovered. */
	Component title();

	/** The item drawn on the tab. */
	ItemStack icon();

	/** Whether the player has unlocked the section; locked sections show no tab at all. Re-checked while the book is open. */
	boolean isUnlocked(Player player);

	/** The section's screen. Closing it should return to {@code parent}, the book's index page. */
	Screen open(Screen parent);
}
