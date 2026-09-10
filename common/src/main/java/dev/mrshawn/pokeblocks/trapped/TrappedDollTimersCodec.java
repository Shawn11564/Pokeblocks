package dev.mrshawn.pokeblocks.trapped;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The wire format of the trapped-doll timer broadcast: plain compressed NBT, kept free of
 * client/server-only classes so both sides (and unit tests) share the exact same round-trip —
 * same contract as {@code PhonePayloadCodec}.
 * <p>
 * Snapshot: {@code {timers: [{u: <uuid>, at: <long game time>}...]}} — one entry per player with a
 * running countdown, carrying the soonest detonation deadline. Each broadcast fully replaces the
 * previous one; an empty list clears every overhead timer.
 */
public final class TrappedDollTimersCodec {

	private static final String TAG_TIMERS = "timers";
	private static final String TAG_UUID = "u";
	private static final String TAG_DETONATE_AT = "at";

	private static final long MAX_DECODE_BYTES = 64 * 1024;

	private TrappedDollTimersCodec() {}

	public static byte[] encodeTimers(Map<UUID, Long> timers) {
		ListTag list = new ListTag();
		timers.forEach((playerId, detonateAt) -> {
			CompoundTag entry = new CompoundTag();
			entry.putUUID(TAG_UUID, playerId);
			entry.putLong(TAG_DETONATE_AT, detonateAt);
			list.add(entry);
		});
		CompoundTag tag = new CompoundTag();
		tag.put(TAG_TIMERS, list);
		return toBytes(tag);
	}

	public static Map<UUID, Long> decodeTimers(byte[] data) {
		CompoundTag tag = fromBytes(data);
		ListTag list = tag.getList(TAG_TIMERS, Tag.TAG_COMPOUND);
		Map<UUID, Long> timers = new HashMap<>();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag entry = list.getCompound(i);
			if (entry.hasUUID(TAG_UUID)) {
				timers.put(entry.getUUID(TAG_UUID), entry.getLong(TAG_DETONATE_AT));
			}
		}
		return Map.copyOf(timers);
	}

	private static byte[] toBytes(CompoundTag tag) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			NbtIo.writeCompressed(tag, out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static CompoundTag fromBytes(byte[] data) {
		try {
			return NbtIo.readCompressed(new ByteArrayInputStream(data), NbtAccounter.create(MAX_DECODE_BYTES));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
