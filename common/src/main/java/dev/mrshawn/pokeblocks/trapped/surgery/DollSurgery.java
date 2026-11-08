package dev.mrshawn.pokeblocks.trapped.surgery;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.TrappedDolls;
import dev.mrshawn.pokeblocks.mixin.ResultSlotAccessor;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import dev.mrshawn.pokeblocks.registry.SoundRegistry;
import dev.mrshawn.pokeblocks.trapped.TrappedExplosives;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Server side of the doll-surgery minigame: crafting a trapped doll (doll + explosive) no longer
 * hands the doll straight out of the result slot. Clicking that result instead opens the
 * {@link SurgeryGame} on the client; the crafting menu stays open underneath, untouched, until the
 * client reports how it went:
 * <ul>
 *   <li>{@link #OUTCOME_SUCCESS} — one doll + one explosive leave the grid, the trapped doll goes to
 *       the inventory.</li>
 *   <li>{@link #OUTCOME_RIPPED} — the stitches gave way: doll and explosive are consumed and the
 *       explosive really goes off at the player, exactly as a popped trapped doll would
 *       ({@link TrappedExplosives#detonate}: a modded explosive as itself, TNT as a block-breaking
 *       TNT blast).</li>
 *   <li>{@link #OUTCOME_CANCEL} — nothing changes (left before the explosive went in).</li>
 * </ul>
 * Nothing is taken from the grid until the result arrives, so a disconnect or a closed table simply
 * leaves the ingredients where vanilla puts them. The client is trusted with the minigame itself (it
 * is a single-player skill game), but a success faster than {@link #MIN_SUCCESS_TICKS} is refused.
 * <p>
 * Optional per player: without the {@link SurgeryPayloads.StartPayload} channel (or with
 * {@code [trapped] surgery_minigame = false}) trapped dolls craft the old way.
 * <p>
 * Regression checks: {@code SurgeryGameTest} (rules, every layout winnable) and
 * {@code tools/surgery_check.py} (real client: click interception, cancel, full operation, rip, Crafter).
 */
public final class DollSurgery {

	public static final int OUTCOME_CANCEL = 0;
	public static final int OUTCOME_SUCCESS = 1;
	public static final int OUTCOME_RIPPED = 2;

	/** No human finishes all five steps this fast; a quicker "success" is a forged packet. */
	static final int MIN_SUCCESS_TICKS = 100;
	/** Sessions nobody answered (crashed client, lost packet) are forgotten after 15 minutes. */
	private static final int SESSION_TIMEOUT_TICKS = 20 * 60 * 15;

	private record Session(int token, int containerId, int resultSlot, ItemStack doll, ItemStack explosive, long startedAt) {}

	private static final Map<UUID, Session> SESSIONS = new ConcurrentHashMap<>();
	private static int nextToken = 1;

	private static Predicate<ServerPlayer> canSend = player -> false;
	private static BiConsumer<ServerPlayer, SurgeryPayloads.StartPayload> sender = (player, payload) -> {};
	/** Client-side opener, installed by the client bootstrap; a no-op on dedicated servers. */
	private static Consumer<SurgeryPayloads.StartPayload> clientOpener = payload -> {};
	private static Consumer<SurgeryPayloads.ResultPayload> resultSender = payload -> {};

	private DollSurgery() {}

	// --- Wiring --------------------------------------------------------------------

	public static void setNetworkBridge(Predicate<ServerPlayer> canSendStart, BiConsumer<ServerPlayer, SurgeryPayloads.StartPayload> sendStart) {
		canSend = canSendStart;
		sender = sendStart;
	}

	public static void setResultSender(Consumer<SurgeryPayloads.ResultPayload> send) {
		resultSender = send;
	}

	public static void setClientOpener(Consumer<SurgeryPayloads.StartPayload> opener) {
		clientOpener = opener;
	}

	/** Clientbound entry point for the loader payload handlers. */
	public static void handleStartOnClient(SurgeryPayloads.StartPayload payload) {
		clientOpener.accept(payload);
	}

	/** Client → server: report how the surgery ended. */
	public static void sendResult(int token, int outcome) {
		resultSender.accept(new SurgeryPayloads.ResultPayload(token, outcome));
	}

	// --- Starting ------------------------------------------------------------------

	/**
	 * Called at the head of every server-side container click. Returns true (cancel the click) when
	 * the click hit a trapped-doll crafting result and a surgery was started instead.
	 */
	public static boolean interceptCraft(AbstractContainerMenu menu, int slotIndex, ServerPlayer player) {
		if (!PokeblocksConfig.isSurgeryMinigame()) return false;
		if (slotIndex < 0 || slotIndex >= menu.slots.size()) return false;
		Slot slot = menu.slots.get(slotIndex);
		if (!(slot instanceof ResultSlot)) return false;
		ItemStack result = slot.getItem();
		if (!result.is(ItemRegistry.POKEDOLL_ITEM.get()) || !TrappedDolls.isTrapped(result)) return false;
		CraftingContainer grid = ((ResultSlotAccessor) slot).pokeblocks$craftSlots();
		int[] found = findIngredients(grid);
		if (found == null) return false;
		if (!canSend.test(player)) return false; // client can't play: old-style craft

		int token = nextToken++;
		ItemStack doll = grid.getItem(found[0]).copyWithCount(1);
		ItemStack explosive = grid.getItem(found[1]).copyWithCount(1);
		SESSIONS.put(player.getUUID(), new Session(token, menu.containerId, slotIndex, doll, explosive,
				player.level().getGameTime()));
		sender.accept(player, new SurgeryPayloads.StartPayload(token, doll, explosive));
		return true;
	}

	/** Grid indices {doll, explosive} when the grid holds exactly one plain doll and one explosive. */
	@Nullable
	static int[] findIngredients(CraftingContainer grid) {
		int doll = -1;
		int explosive = -1;
		for (int i = 0; i < grid.getContainerSize(); i++) {
			ItemStack stack = grid.getItem(i);
			if (stack.isEmpty()) continue;
			if (stack.is(ItemRegistry.POKEDOLL_ITEM.get()) && doll < 0 && !TrappedDolls.isTrapped(stack)) {
				doll = i;
			} else if (TrappedExplosives.isExplosive(stack) && explosive < 0) {
				explosive = i;
			} else {
				return null;
			}
		}
		return doll >= 0 && explosive >= 0 ? new int[]{doll, explosive} : null;
	}

	// --- Finishing -----------------------------------------------------------------

	/** Serverbound entry point for the loader payload handlers. */
	public static void handleResult(ServerPlayer player, int token, int outcome) {
		Session session = SESSIONS.get(player.getUUID());
		if (session == null || session.token() != token) return;
		SESSIONS.remove(player.getUUID());

		AbstractContainerMenu menu = player.containerMenu;
		try {
			if (outcome == OUTCOME_CANCEL || menu.containerId != session.containerId()) return;
			if (session.resultSlot() >= menu.slots.size()
					|| !(menu.slots.get(session.resultSlot()) instanceof ResultSlot slot)) return;
			CraftingContainer grid = ((ResultSlotAccessor) slot).pokeblocks$craftSlots();
			int[] found = findIngredients(grid);
			// The grid must still hold the very doll and explosive the surgery started with.
			if (found == null
					|| !ItemStack.isSameItemSameComponents(grid.getItem(found[0]), session.doll())
					|| !ItemStack.isSameItemSameComponents(grid.getItem(found[1]), session.explosive())) {
				return;
			}

			if (outcome == OUTCOME_SUCCESS) {
				long elapsed = player.level().getGameTime() - session.startedAt();
				if (elapsed < MIN_SUCCESS_TICKS) {
					PokeblocksLog.LOGGER.warn("Ignoring a {}-tick doll surgery from {} (too fast to be real)",
							elapsed, player.getGameProfile().getName());
					return;
				}
				ItemStack trapped = TrappedDolls.makeTrapped(session.doll(), TrappedExplosives.idFor(session.explosive()));
				grid.removeItem(found[0], 1);
				grid.removeItem(found[1], 1);
				menu.slotsChanged(grid);
				trapped.onCraftedBy(player.level(), player, 1);
				player.getInventory().placeItemBackInInventory(trapped);
				player.displayClientMessage(Component.translatable("message.pokeblocks.surgery.success",
						session.doll().getHoverName()), true);
				player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6f, 1.4f);
			} else if (outcome == OUTCOME_RIPPED) {
				grid.removeItem(found[0], 1);
				grid.removeItem(found[1], 1);
				menu.slotsChanged(grid);
				ripBlast(player, session.explosive());
			}
		} finally {
			// The client's predicted click (and its screen swap) desynced the menu; resend all of it.
			menu.sendAllDataToRemote();
		}
	}

	/**
	 * The surgery went wrong: stuffing everywhere, and the explosive goes off at the player's feet
	 * the same way a popped trapped doll's does — its own behaviour for a modded explosive, a
	 * block-breaking TNT blast for vanilla TNT (or a dud). No chat line: the bang says it all.
	 */
	private static void ripBlast(ServerPlayer player, ItemStack explosive) {
		ServerLevel level = player.serverLevel();
		double x = player.getX(), y = player.getY() + 0.8, z = player.getZ();
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.WHITE_WOOL.defaultBlockState()),
				x, y, z, 60, 0.4, 0.4, 0.4, 0.2);
		level.playSound(null, x, y, z, SoundRegistry.POKEDOLL_SQUEAK.get(), SoundSource.PLAYERS, 1.0f, 0.6f);
		TrappedExplosives.detonate(level, player.blockPosition(), TrappedExplosives.idFor(explosive));
	}

	/** Forgets sessions of players who left or never answered. Cheap: runs every 5 seconds. */
	public static void serverTick(MinecraftServer server) {
		if (SESSIONS.isEmpty() || server.getTickCount() % 100 != 0) return;
		SESSIONS.entrySet().removeIf(entry -> {
			ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
			return player == null
					|| player.containerMenu.containerId != entry.getValue().containerId()
					|| player.level().getGameTime() - entry.getValue().startedAt() > SESSION_TIMEOUT_TICKS;
		});
	}
}
