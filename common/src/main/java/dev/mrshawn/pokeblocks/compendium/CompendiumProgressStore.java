package dev.mrshawn.pokeblocks.compendium;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * World-saved record of every player's compendium discoveries, one file per world
 * ({@code data/pokeblocks_compendium.dat} on the overworld's storage). An entry is recorded the
 * first time a player carries the doll/figurine and never removed, so compendium progress survives
 * placing, trading or losing the item — the screens only ask "has this player ever had it".
 * <p>
 * Mutated on the server thread only. The NBT round-trip lives in the static
 * {@link #saveInto}/{@link #loadFrom} helpers so it can be unit-tested without a server.
 */
public class CompendiumProgressStore extends SavedData {

	private static final String STORAGE_ID = "pokeblocks_compendium";

	private static final String TAG_PLAYERS = "players";
	private static final String TAG_UUID = "uuid";
	private static final String TAG_DOLLS = "dolls";
	private static final String TAG_FIGURINES = "figurines";

	// Command-storage data-fix semantics: free-form mod NBT with no vanilla structure to migrate.
	private static final Factory<CompendiumProgressStore> FACTORY = new Factory<>(
			CompendiumProgressStore::new,
			(tag, registries) -> loadFrom(tag),
			DataFixTypes.SAVED_DATA_COMMAND_STORAGE);

	/** Discovered ids per player uuid and kind. */
	private final Map<UUID, Map<CompendiumKind, Set<String>>> players = new HashMap<>();

	public static CompendiumProgressStore get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(FACTORY, STORAGE_ID);
	}

	/**
	 * Records {@code id} (normalised to lower-case) as discovered by {@code player}.
	 *
	 * @return true when the id was newly added (the caller should re-sync that player)
	 */
	public boolean record(UUID player, CompendiumKind kind, String id) {
		Set<String> ids = players
				.computeIfAbsent(player, uuid -> new HashMap<>())
				.computeIfAbsent(kind, k -> new HashSet<>());
		boolean added = ids.add(id.toLowerCase(Locale.ROOT));
		if (added) {
			setDirty();
		}
		return added;
	}

	/** An immutable snapshot of one player's discoveries, ready to encode for the sync payload. */
	public CompendiumProgress snapshot(UUID player) {
		Map<CompendiumKind, Set<String>> progress = players.get(player);
		if (progress == null) {
			return CompendiumProgress.EMPTY;
		}
		return new CompendiumProgress(
				progress.getOrDefault(CompendiumKind.DOLL, Set.of()),
				progress.getOrDefault(CompendiumKind.FIGURINE, Set.of()));
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		saveInto(tag, players);
		return tag;
	}

	/** Visible for tests: the pure NBT → store half of the round-trip. */
	public static CompendiumProgressStore loadFrom(CompoundTag tag) {
		CompendiumProgressStore store = new CompendiumProgressStore();
		for (Tag element : tag.getList(TAG_PLAYERS, Tag.TAG_COMPOUND)) {
			CompoundTag playerTag = (CompoundTag) element;
			if (!playerTag.hasUUID(TAG_UUID)) continue;
			Map<CompendiumKind, Set<String>> progress = new HashMap<>();
			readIds(playerTag, TAG_DOLLS).ifPresent(ids -> progress.put(CompendiumKind.DOLL, ids));
			readIds(playerTag, TAG_FIGURINES).ifPresent(ids -> progress.put(CompendiumKind.FIGURINE, ids));
			store.players.put(playerTag.getUUID(TAG_UUID), progress);
		}
		return store;
	}

	/** Visible for tests: the pure store → NBT half of the round-trip. */
	public static void saveInto(CompoundTag tag, Map<UUID, Map<CompendiumKind, Set<String>>> players) {
		ListTag list = new ListTag();
		for (Map.Entry<UUID, Map<CompendiumKind, Set<String>>> entry : players.entrySet()) {
			CompoundTag playerTag = new CompoundTag();
			playerTag.putUUID(TAG_UUID, entry.getKey());
			playerTag.put(TAG_DOLLS, idList(entry.getValue().get(CompendiumKind.DOLL)));
			playerTag.put(TAG_FIGURINES, idList(entry.getValue().get(CompendiumKind.FIGURINE)));
			list.add(playerTag);
		}
		tag.put(TAG_PLAYERS, list);
	}

	private static ListTag idList(Set<String> ids) {
		ListTag list = new ListTag();
		if (ids != null) {
			// Sorted so identical progress writes identical NBT.
			ids.stream().sorted().forEach(id -> list.add(StringTag.valueOf(id)));
		}
		return list;
	}

	private static java.util.Optional<Set<String>> readIds(CompoundTag playerTag, String key) {
		ListTag list = playerTag.getList(key, Tag.TAG_STRING);
		if (list.isEmpty()) {
			return java.util.Optional.empty();
		}
		Set<String> ids = new HashSet<>();
		for (int i = 0; i < list.size(); i++) {
			ids.add(list.getString(i));
		}
		return java.util.Optional.of(ids);
	}
}
