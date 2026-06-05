package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * POC collection source: a doll counts as "collected" if the player is currently carrying one.
 * <p>
 * This is the cheap, no-persistence path — it reflects what's in the inventory the moment the book
 * is opened. A real compendium would record obtained dolls into per-player saved data instead so
 * the entry stays filled after the doll is placed or traded away.
 */
public final class CompendiumCollection {

    private CompendiumCollection() {}

    /** Base species names (lower-case, flags ignored) of every doll currently in the player's inventory. */
    public static Set<String> collectedSpecies(Player player) {
        Set<String> collected = new HashSet<>();
        if (player == null) return collected;
        for (ItemStack stack : player.getInventory().items) {
            addIfDoll(collected, stack);
        }
        for (ItemStack stack : player.getInventory().offhand) {
            addIfDoll(collected, stack);
        }
        return collected;
    }

    private static void addIfDoll(Set<String> collected, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof PokedollItem) {
            collected.add(PokedollItem.getPokemonFromStack(stack).toLowerCase());
        }
    }
}
