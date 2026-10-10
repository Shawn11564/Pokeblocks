package dev.mrshawn.pokeblocks.client.renderer.atlas;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.SequencedMap;

/**
 * The doll block renderers' own vertex buffers, drawn once per frame.
 * <p>
 * Vanilla hands block-entity renderers one shared buffer that it flushes (upload + draw) every time two
 * consecutive block entities use different render types. Even with every doll on the shared atlas type
 * (see {@link DollTextureAtlas}), anything else interleaved in the section order (skulls, chests, a
 * translucent doll) ends the doll batch again and again; a profile of a doll wall next to some player
 * heads still showed one flush per doll. So atlas dolls don't write into vanilla's buffer at all: they
 * write into these dedicated buffers, which nothing else touches, and each loader's post-block-entity
 * render hook calls {@link #flush()} exactly once, after all block entities and before translucent
 * terrain. Draw order relative to other block entities is depth-tested anyway, so nothing visible changes.
 * <p>
 * Render thread only.
 */
public final class DollRenderBatch {

	/** Roughly 3,000 cubes' worth; the builder grows on demand. */
	private static final int CUTOUT_INITIAL_BYTES = 1 << 20;
	private static final int TRANSLUCENT_INITIAL_BYTES = 1 << 18;
	/** Only anything a third-party render layer might add with another type; tiny. */
	private static final int SHARED_INITIAL_BYTES = 1 << 12;

	@Nullable
	private static MultiBufferSource.BufferSource source;

	private DollRenderBatch() {}

	/** The buffer source atlas dolls render into. Created on first use (render thread). */
	public static MultiBufferSource.BufferSource source() {
		if (source == null) {
			SequencedMap<RenderType, ByteBufferBuilder> fixed = new LinkedHashMap<>();
			fixed.put(DollTextureAtlas.cutoutRenderType(), new ByteBufferBuilder(CUTOUT_INITIAL_BYTES));
			fixed.put(DollTextureAtlas.translucentRenderType(), new ByteBufferBuilder(TRANSLUCENT_INITIAL_BYTES));
			source = MultiBufferSource.immediateWithBuffers(fixed, new ByteBufferBuilder(SHARED_INITIAL_BYTES));
		}
		return source;
	}

	/** Uploads and draws everything queued this frame. Called from each loader's after-block-entities hook. */
	public static void flush() {
		if (source != null) source.endBatch();
	}
}
