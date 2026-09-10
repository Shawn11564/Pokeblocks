package dev.mrshawn.pokeblocks.trapped;

import dev.mrshawn.pokeblocks.item.TrappedDolls;
import net.minecraft.core.NonNullList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Server side of the trapped-doll detonation countdown (see {@link TrappedDolls}). Every tick
 * (from {@code ServerTickMixin}) each online player's inventory — main, armor and offhand — is
 * scanned for ticking dolls; the scan is a cheap component-presence check per stack, so it is
 * effectively free while nothing ticks.
 * <p>
 * A doll whose deadline passed is removed and detonates <b>on the player</b>: the TNT-sized
 * entities-only blast goes off at their chest, and — because the doll was in their inventory —
 * the wearer is then killed outright with the trapped-doll damage type, whose death message
 * credits the doll. (A doll ticking on the <i>ground</i> detonates from
 * {@code TrappedDollItemEntityMixin} with the blast alone — no guaranteed kill.)
 * <p>
 * Every {@link #BROADCAST_INTERVAL_TICKS} the soonest deadline per ticking player is broadcast to
 * all clients with the optional {@link TrappedDollPayloads.TimersPayload} channel, driving the
 * countdown other players see above the victim's head; one final empty snapshot clears it. The
 * per-loader send/presence hooks arrive via {@link #setNetworkBridge}, mirroring
 * {@code PhoneCalls}.
 */
public final class TrappedDollCountdown {

	private static final int BROADCAST_INTERVAL_TICKS = 10;

	// Injected per loader; no-op defaults so an un-wired loader fails safe (no broadcast).
	private static Predicate<ServerPlayer> channelCheck = player -> false;
	private static BiConsumer<ServerPlayer, byte[]> timersSender = (player, data) -> {};

	/** Whether the previous broadcast carried any timers — one empty snapshot is sent to clear. */
	private static boolean lastBroadcastHadTimers = false;

	private TrappedDollCountdown() {}

	/** Wires the loader-specific channel presence check and S2C sender. */
	public static void setNetworkBridge(Predicate<ServerPlayer> hasChannel, BiConsumer<ServerPlayer, byte[]> send) {
		channelCheck = hasChannel;
		timersSender = send;
	}

	/** Called once per server tick. */
	public static void serverTick(MinecraftServer server) {
		Map<UUID, Long> active = new HashMap<>();
		// Copy: a detonation can kill players mid-iteration and death handling may touch the list.
		for (ServerPlayer player : List.copyOf(server.getPlayerList().getPlayers())) {
			long soonest = tickPlayer(player);
			if (soonest >= 0) {
				active.put(player.getUUID(), soonest);
			}
		}

		if (server.getTickCount() % BROADCAST_INTERVAL_TICKS != 0) {
			return;
		}
		boolean hasTimers = !active.isEmpty();
		if (!hasTimers && !lastBroadcastHadTimers) {
			return;
		}
		byte[] data = TrappedDollTimersCodec.encodeTimers(active);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (channelCheck.test(player)) {
				timersSender.accept(player, data);
			}
		}
		lastBroadcastHadTimers = hasTimers;
	}

	/**
	 * Scans one player's inventory for ticking dolls, detonating any whose deadline passed.
	 * Returns the soonest still-running deadline, or -1 when nothing ticks.
	 */
	private static long tickPlayer(ServerPlayer player) {
		Inventory inventory = player.getInventory();
		long gameTime = player.level().getGameTime();
		long soonest = -1;
		boolean detonated = false;

		for (NonNullList<ItemStack> compartment : List.of(inventory.items, inventory.armor, inventory.offhand)) {
			for (int slot = 0; slot < compartment.size(); slot++) {
				ItemStack stack = compartment.get(slot);
				if (!TrappedDolls.isTicking(stack)) {
					continue;
				}
				long detonateAt = TrappedDolls.detonateAt(stack);
				if (gameTime >= detonateAt) {
					// Remove before any damage so the doll never re-drops with the death loot.
					compartment.set(slot, ItemStack.EMPTY);
					detonated = true;
				} else if (soonest < 0 || detonateAt < soonest) {
					soonest = detonateAt;
				}
			}
		}

		if (detonated) {
			detonateOnPlayer(player);
		}
		return soonest;
	}

	private static void detonateOnPlayer(ServerPlayer player) {
		Level level = player.level();
		Vec3 center = player.position().add(0.0, player.getBbHeight() / 2.0, 0.0);
		// Blast first (bystanders + knockback), finisher second: the explosion damage alone may not
		// kill through armor, and the order keeps the death drops out of the blast.
		TrappedDolls.explodeEntityOnly(level, center);
		if (player.isAlive()) {
			player.hurt(TrappedDolls.damageSource(level), Float.MAX_VALUE);
		}
	}
}
