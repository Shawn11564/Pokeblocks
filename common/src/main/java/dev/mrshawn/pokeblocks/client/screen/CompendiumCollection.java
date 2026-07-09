package dev.mrshawn.pokeblocks.client.screen;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Inventory half of "what has the player collected": the progress keys of every matching item the
 * player is carrying right now (main inventory, offhand, and armor — dolls are head-equipable).
 * Persistent discoveries come from the server-synced {@code CompendiumProgress}; see
 * {@link CompendiumType#collectedKeys}.
 */
public final class CompendiumCollection {

	private CompendiumCollection() {}

	/** Progress keys (variant-exact) of every stack of {@code type} currently on the player. */
	public static Set<String> fromInventory(Player player, CompendiumType type) {
		Set<String> collected = new HashSet<>();
		if (player == null) return collected;
		addAll(collected, player.getInventory().items, type);
		addAll(collected, player.getInventory().offhand, type);
		addAll(collected, player.getInventory().armor, type);
		return collected;
	}

	private static void addAll(Set<String> collected, Iterable<ItemStack> stacks, CompendiumType type) {
		for (ItemStack stack : stacks) {
			if (!stack.isEmpty() && type.matches(stack)) {
				collected.add(type.progressKeyOf(stack));
			}
		}
	}
}
