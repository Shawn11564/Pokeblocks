package dev.mrshawn.pokeblocks.trapped;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Watches what another mod's explosive block does when {@link TrappedExplosives} sets it off, so
 * the trap can tell "it went off" from "nothing happened" and only then fall back to a plain blast.
 * <p>
 * While {@link #record} runs an action, three mixins report into the current {@link Record}:
 * entities spawned ({@code ExplosiveActivityServerLevelMixin}, {@code ServerLevel#addFreshEntity}
 * — a primed-TNT style entity), explosions started ({@code ExplosiveActivityExplosionMixin},
 * {@code Explosion#explode} — an instant blast) and non-air blocks placed
 * ({@code ExplosiveActivityLevelMixin}, {@code Level#setBlock} — a "lit" block variant). Anything
 * at all counts as the explosive having gone off.
 * <p>
 * Recording is synchronous and happens only on the thread that started it (the server thread),
 * so the per-call cost in those hot methods is one null check when idle.
 */
public final class ExplosiveActivity {

	private static Record current;
	private static Thread owner;

	private ExplosiveActivity() {}

	/** Runs {@code action}, returning everything it did. Exceptions from mod code are logged and swallowed. */
	public static Record record(String what, Runnable action) {
		Record outer = current;
		Thread outerOwner = owner;
		Record record = new Record();
		current = record;
		owner = Thread.currentThread();
		try {
			action.run();
		} catch (Exception | LinkageError e) {
			PokeblocksLog.LOGGER.warn("Trapped doll: setting off {} threw; treating it as a dud", what, e);
		} finally {
			current = outer;
			owner = outerOwner;
		}
		return record;
	}

	private static Record active() {
		Record record = current;
		return record != null && Thread.currentThread() == owner ? record : null;
	}

	public static void onEntityAdded(Entity entity) {
		Record record = active();
		if (record != null) record.entities.add(entity);
	}

	public static void onExplosion() {
		Record record = active();
		if (record != null) record.explosions++;
	}

	public static void onBlockSet(BlockState state) {
		Record record = active();
		if (record != null && !state.isAir()) record.blocksPlaced++;
	}

	/** What one recorded action did. */
	public static final class Record {
		final List<Entity> entities = new ArrayList<>();
		int explosions;
		int blocksPlaced;

		/** True when the action did nothing observable: the explosive is a dud. */
		public boolean isEmpty() {
			return entities.isEmpty() && explosions == 0 && blocksPlaced == 0;
		}

		/**
		 * True when the only thing that happened is plain vanilla primed TNT spawning: the signature
		 * of a modded {@code TntBlock} that inherited vanilla's chain-reaction hook instead of
		 * spawning its own entity.
		 */
		public boolean onlyVanillaTnt() {
			if (entities.isEmpty() || explosions != 0 || blocksPlaced != 0) return false;
			for (Entity entity : entities) {
				if (entity.getType() != EntityType.TNT) return false;
			}
			return true;
		}

		/** Removes every entity this action spawned (they haven't ticked yet). */
		public void discardEntities() {
			for (Entity entity : entities) {
				entity.discard();
			}
		}

		public List<Entity> entities() {
			return entities;
		}
	}
}
