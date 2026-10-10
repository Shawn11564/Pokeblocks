package dev.mrshawn.pokeblocks.client.compat;

/**
 * Render-call counters for compatibility testing, so a run can tell "the renderer was never called"
 * (a rendering mod skipped or culled it) apart from "it was called but drew nothing visible" (the
 * screenshots show that). Plain increments on the render thread; only a test harness reads them, and
 * resetting them is its job.
 */
public final class RenderProbe {

	/** Doll-family block entities rendered (pokedolls, figurines, decorations). */
	public static int blockEntities;
	/** ...of which went into the dolls' own atlas batch ({@code DollRenderBatch}) instead of the passed buffer. */
	public static int batchedBlockEntities;
	/** Pokedoll item renders: GUI slots, hands, item frames, dropped items, worn on heads. */
	public static int dollItems;
	/** Walking figurine entities rendered. */
	public static int figurineEntities;

	private RenderProbe() {}

	public static void reset() {
		blockEntities = 0;
		batchedBlockEntities = 0;
		dollItems = 0;
		figurineEntities = 0;
	}
}
