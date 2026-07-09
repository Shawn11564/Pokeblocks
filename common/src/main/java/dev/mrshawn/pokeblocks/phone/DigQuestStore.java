package dev.mrshawn.pokeblocks.phone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * World-saved record of every player's active phone dig quest, one file per world
 * ({@code data/pokeblocks_phone_digs.dat} on the overworld's storage) so a quest survives
 * relogs/restarts — the dig-site blocks are real blocks in the world and must keep resolving.
 * At most one quest per player.
 * <p>
 * Mutated on the server thread only. The NBT round-trip lives in the static
 * {@link #saveInto}/{@link #loadFrom} helpers so it can be unit-tested without a server.
 */
public class DigQuestStore extends SavedData {

	private static final String STORAGE_ID = "pokeblocks_phone_digs";

	private static final String TAG_QUESTS = "quests";
	private static final String TAG_UUID = "uuid";
	private static final String TAG_DIMENSION = "dim";
	private static final String TAG_CALLER = "caller";
	private static final String TAG_BURIED = "buried";
	private static final String TAG_TARGET = "target";
	private static final String TAG_ATTEMPTS = "attempts";
	private static final String TAG_SITES = "sites";

	// Command-storage data-fix semantics: free-form mod NBT with no vanilla structure to migrate.
	private static final Factory<DigQuestStore> FACTORY = new Factory<>(
			DigQuestStore::new,
			(tag, registries) -> loadFrom(tag),
			DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

	/**
	 * One player's quest: where the sites are, who called, which doll is buried, and the dig on
	 * which it turns up. {@code targetAttempt} is 1-based — the doll surfaces on that fully-dug
	 * site, guaranteeing a find within the configured attempt cap.
	 */
	public record Quest(String dimension, String callerKey, String buriedKey,
						int targetAttempt, int attempts, List<BlockPos> sites) {

		public Quest {
			sites = List.copyOf(sites);
		}

		public Quest withAttemptsAndSites(int attempts, List<BlockPos> sites) {
			return new Quest(dimension, callerKey, buriedKey, targetAttempt, attempts, sites);
		}
	}

	private final Map<UUID, Quest> quests = new HashMap<>();

	public static DigQuestStore get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(FACTORY, STORAGE_ID);
	}

	public Quest get(UUID player) {
		return quests.get(player);
	}

	public void put(UUID player, Quest quest) {
		quests.put(player, quest);
		setDirty();
	}

	public void remove(UUID player) {
		if (quests.remove(player) != null) {
			setDirty();
		}
	}

	/** The player whose quest contains {@code pos} in {@code dimension}, or null. */
	public Map.Entry<UUID, Quest> findQuestAt(String dimension, BlockPos pos) {
		for (Map.Entry<UUID, Quest> entry : quests.entrySet()) {
			Quest quest = entry.getValue();
			if (quest.dimension().equals(dimension) && quest.sites().contains(pos)) {
				return entry;
			}
		}
		return null;
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		saveInto(tag, quests);
		return tag;
	}

	/** Visible for tests: the pure NBT → store half of the round-trip. */
	public static DigQuestStore loadFrom(CompoundTag tag) {
		DigQuestStore store = new DigQuestStore();
		for (Tag element : tag.getList(TAG_QUESTS, Tag.TAG_COMPOUND)) {
			CompoundTag questTag = (CompoundTag) element;
			if (!questTag.hasUUID(TAG_UUID)) continue;
			List<BlockPos> sites = new ArrayList<>();
			for (long packed : questTag.getLongArray(TAG_SITES)) {
				sites.add(BlockPos.of(packed));
			}
			store.quests.put(questTag.getUUID(TAG_UUID), new Quest(
					questTag.getString(TAG_DIMENSION),
					questTag.getString(TAG_CALLER),
					questTag.getString(TAG_BURIED),
					Math.max(1, questTag.getInt(TAG_TARGET)),
					Math.max(0, questTag.getInt(TAG_ATTEMPTS)),
					sites));
		}
		return store;
	}

	/** Visible for tests: the pure store → NBT half of the round-trip. */
	public static void saveInto(CompoundTag tag, Map<UUID, Quest> quests) {
		ListTag list = new ListTag();
		for (Map.Entry<UUID, Quest> entry : quests.entrySet()) {
			Quest quest = entry.getValue();
			CompoundTag questTag = new CompoundTag();
			questTag.putUUID(TAG_UUID, entry.getKey());
			questTag.putString(TAG_DIMENSION, quest.dimension());
			questTag.putString(TAG_CALLER, quest.callerKey());
			questTag.putString(TAG_BURIED, quest.buriedKey());
			questTag.putInt(TAG_TARGET, quest.targetAttempt());
			questTag.putInt(TAG_ATTEMPTS, quest.attempts());
			long[] packed = new long[quest.sites().size()];
			for (int i = 0; i < quest.sites().size(); i++) {
				packed[i] = quest.sites().get(i).asLong();
			}
			questTag.putLongArray(TAG_SITES, packed);
			list.add(questTag);
		}
		tag.put(TAG_QUESTS, list);
	}

	/** Visible for tests: an unmodifiable view of the quest map. */
	public Map<UUID, Quest> quests() {
		return Map.copyOf(quests);
	}
}
