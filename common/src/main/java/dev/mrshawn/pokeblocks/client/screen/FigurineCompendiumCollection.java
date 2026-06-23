package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Collection source for the figurine compendium: a figurine counts as "collected" if the player is
 * currently carrying one. Mirrors {@link CompendiumCollection} for dolls.
 * <p>
 * This is the cheap, no-persistence path — it reflects what's in the inventory the moment the book is
 * opened. A real compendium would record obtained figurines into per-player saved data instead so the
 * entry stays filled after the figurine is placed or traded away.
 */
public final class FigurineCompendiumCollection {

    private FigurineCompendiumCollection() {}

    /** Figurine ids (lower-case) of every figurine currently in the player's inventory. */
    public static Set<String> collectedFigurines(Player player) {
        Set<String> collected = new HashSet<>();
        if (player == null) return collected;
        for (ItemStack stack : player.getInventory().items) {
            addIfFigurine(collected, stack);
        }
        for (ItemStack stack : player.getInventory().offhand) {
            addIfFigurine(collected, stack);
        }
        return collected;
    }

    private static void addIfFigurine(Set<String> collected, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof FigurineItem) {
            collected.add(FigurineItem.getFigurineFromStack(stack).toLowerCase());
        }
    }
}
