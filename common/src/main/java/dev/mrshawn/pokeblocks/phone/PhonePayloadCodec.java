package dev.mrshawn.pokeblocks.phone;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The wire format of the phone payloads: plain compressed NBT, kept free of client/server-only
 * classes so both sides (and unit tests) share the exact same round-trip.
 * <p>
 * Dig sites: {@code {dim: "<dimension id>", sites: [long...]}} with positions as
 * {@link BlockPos#asLong()}. Call response: {@code {accept: 0|1}}.
 */
public final class PhonePayloadCodec {

	private static final String TAG_DIMENSION = "dim";
	private static final String TAG_SITES = "sites";
	private static final String TAG_ACCEPT = "accept";

	private static final long MAX_DECODE_BYTES = 64 * 1024;

	private PhonePayloadCodec() {}

	public record DigSites(String dimension, List<BlockPos> sites) {
		public static final DigSites EMPTY = new DigSites("", List.of());
	}

	public static byte[] encodeSites(String dimension, List<BlockPos> sites) {
		CompoundTag tag = new CompoundTag();
		tag.putString(TAG_DIMENSION, dimension);
		long[] packed = new long[sites.size()];
		for (int i = 0; i < sites.size(); i++) {
			packed[i] = sites.get(i).asLong();
		}
		tag.putLongArray(TAG_SITES, packed);
		return toBytes(tag);
	}

	public static DigSites decodeSites(byte[] data) {
		CompoundTag tag = fromBytes(data);
		List<BlockPos> sites = new ArrayList<>();
		for (long packed : tag.getLongArray(TAG_SITES)) {
			sites.add(BlockPos.of(packed));
		}
		return new DigSites(tag.getString(TAG_DIMENSION), List.copyOf(sites));
	}

	public static byte[] encodeCallResponse(boolean accept) {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean(TAG_ACCEPT, accept);
		return toBytes(tag);
	}

	public static boolean decodeCallResponse(byte[] data) {
		return fromBytes(data).getBoolean(TAG_ACCEPT);
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
