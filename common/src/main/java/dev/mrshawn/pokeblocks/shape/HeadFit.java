package dev.mrshawn.pokeblocks.shape;

import org.jetbrains.annotations.Nullable;

/**
 * Pure math for seating a worn pokedoll on a player's head, shared by the item renderer and unit
 * tests. Everything here works in vanilla's <b>head display space</b> — the space
 * {@code CustomHeadLayer.translateToHead} leaves the {@code PoseStack} in for
 * {@code ItemDisplayContext.HEAD} (1.21.1: {@code translate(0, -0.25, 0)}, {@code rotY(180°)},
 * {@code scale(0.625, -0.625, -0.625)}):
 * <ul>
 *   <li>origin on the head's vertical axis, at mid-head height (4px above the neck);</li>
 *   <li>+Y up (the negative Y scale un-flips entity-model space);</li>
 *   <li>1 unit = 0.625 blocks = 10 model pixels, so the 8px skull cube spans ±0.4 horizontally,
 *       its top face sits at +{@link #HEAD_TOP_Y} and the hat/hair overlay (inflated 0.5px) at
 *       +{@link #HAT_TOP_Y}.</li>
 * </ul>
 * A doll's geo model has its origin at the model's bottom-center, so with no correction the doll
 * hangs from mid-head downward through the player's face. The fit maps the model's outer bounds
 * ({@link GeoGeometry#outerBounds}) so the doll rests on the head instead:
 * <ul>
 *   <li><b>Seat:</b> the model's visual bottom lands on the {@link #SEAT_Y} plane. A model authored
 *       floating above y=0 keeps its hover (anchor {@code min(minY, 0)} preserves artist intent,
 *       matching how the doll floats when placed as a block) — but nothing may hang below the seat.</li>
 *   <li><b>Center:</b> the model keeps its authored x/z origin on the head axis — the same origin
 *       placed dolls spin around, which artists center the body on. The bounds' horizontal center is
 *       deliberately <i>not</i> used, so asymmetric details (tails, fins) stick out naturally instead
 *       of shifting the body.</li>
 *   <li><b>Size:</b> regular dolls keep the scale they already rendered at on heads
 *       ({@link #BASE_SCALE}), gigantic dolls get the same ×1.5 bump the held contexts use, and both
 *       are capped ({@link #MAX_FOOTPRINT}/{@link #MAX_HEIGHT}) so oversized models stay a wearable
 *       hat rather than a building.</li>
 * </ul>
 */
public final class HeadFit {

	/** Top of the 8px skull cube: (0.5 − 0.25) / 0.625 blocks, in display units. */
	public static final float HEAD_TOP_Y = 0.4f;
	/** Top of the hat/hair skin overlay (skull cube inflated by 0.5px): (0.53125 − 0.25) / 0.625. */
	public static final float HAT_TOP_Y = 0.45f;
	/**
	 * Where the doll's bottom rests: 0.02 units into the hair overlay so it reads as sitting in the
	 * hair on skins that have one, while the 0.03-unit (0.3px) float above a bare skull is invisible.
	 */
	public static final float SEAT_Y = 0.43f;

	/**
	 * Display units per geo unit for a regular doll. 1.0 keeps the size dolls have always rendered
	 * at on heads (no head entry in the item JSON = identity display transform) — only the seating
	 * changes.
	 */
	public static final float BASE_SCALE = 1.0f;
	/** Extra scale for gigantic dolls — the held contexts' ratio (0.75 gigantic / 0.5 base). */
	public static final float GIGANTIC_MULTIPLIER = 1.5f;
	/** Max horizontal extent (display units; 2.0 = 1.25 blocks = 2.5 head widths) before capping. */
	public static final float MAX_FOOTPRINT = 2.0f;
	/** Max height (display units; 2.4 = 1.5 blocks) before capping. */
	public static final float MAX_HEIGHT = 2.4f;

	/**
	 * Bounds used when a doll's geo model can't be resolved — the legacy fixed hitbox
	 * ({@code DollShapes.DEFAULT_SHAPE}) translated into model space.
	 */
	private static final double[] FALLBACK_BOUNDS = {-0.25, 0, -0.25, 0.25, 0.75, 0.25};

	/** Vanilla {@code ItemRenderer.render}'s {@code translate(-0.5, -0.5, -0.5)} before the BEWLR. */
	private static final float VANILLA_ITEM_OFFSET = 0.5f;
	/** GeckoLib {@code GeoItemRenderer.preRender}'s {@code translate(0.5, 0.51, 0.5)} (4.8.x). */
	private static final float GECKOLIB_OFFSET_XZ = 0.5f;
	private static final float GECKOLIB_OFFSET_Y = 0.51f;

	private HeadFit() {}

	/** The uniform scale and vertical anchor for a doll's outer bounds (null = model unresolved). */
	public record Fit(float scale, float anchorY) {}

	public static Fit fit(double @Nullable [] bounds, boolean gigantic) {
		double[] box = bounds != null ? bounds : FALLBACK_BOUNDS;
		float scale = BASE_SCALE * (gigantic ? GIGANTIC_MULTIPLIER : 1f);

		double footprint = Math.max(box[3] - box[0], box[5] - box[2]);
		double height = box[4] - box[1];
		if (footprint > 1e-4) {
			scale = (float) Math.min(scale, MAX_FOOTPRINT / footprint);
		}
		if (height > 1e-4) {
			scale = (float) Math.min(scale, MAX_HEIGHT / height);
		}

		// Seat the visual bottom, but let deliberately-floating models (minY > 0) keep their hover.
		float anchorY = (float) Math.min(box[1], 0);
		return new Fit(scale, anchorY);
	}

	/**
	 * The pose operations the item renderer applies for {@code ItemDisplayContext.HEAD}, as
	 * {@code [preX, preY, preZ, scale, postX, postY, postZ]}: {@code translate(pre)} →
	 * {@code scale(scale)} → {@code translate(post)}.
	 * <p>
	 * These run inside {@code scaleModelForRender}, i.e. between vanilla {@code ItemRenderer}'s
	 * {@code translate(-0.5, -0.5, -0.5)} (already on the stack) and GeckoLib's
	 * {@code translate(0.5, 0.51, 0.5)} (applied just after), and are composed so those two cancel
	 * exactly and the net display-space map is
	 * {@code translate(0, SEAT_Y, 0) · scale(s) · translate(0, -anchorY, 0)}: the model keeps its
	 * authored x/z origin on the head axis, is uniformly scaled by {@code s}, and its bottom rests
	 * on the {@link #SEAT_Y} plane.
	 */
	public static float[] headPoseOps(double @Nullable [] bounds, boolean gigantic) {
		Fit fit = fit(bounds, gigantic);
		// X = T₋⁻¹ · target · T₊⁻¹, with T₋ vanilla's pre-translate and T₊ GeckoLib's post-translate:
		// the pre undoes T₋ and lifts to the seat, the post pre-cancels T₊ and drops to the anchor.
		return new float[]{
				VANILLA_ITEM_OFFSET, VANILLA_ITEM_OFFSET + SEAT_Y, VANILLA_ITEM_OFFSET,
				fit.scale(),
				-GECKOLIB_OFFSET_XZ, -fit.anchorY() - GECKOLIB_OFFSET_Y, -GECKOLIB_OFFSET_XZ};
	}
}
