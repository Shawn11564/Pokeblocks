package dev.mrshawn.pokeblocks.trapped.surgery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The "doll surgery" minigame behind trapping a pokedoll — an Operation-style precision game the
 * crafting table opens instead of handing out the trapped doll directly (see {@link DollSurgery}).
 * <p>
 * The doll is shown as a cross-section of plush fabric with a hidden cavity inside. The player:
 * <ol>
 *   <li>{@link Stage#UNSTITCH} — snips the bobbing belly stitches open with a seam ripper,</li>
 *   <li>{@link Stage#REMOVE_STUFFING} — tweezers every fluff of stuffing out of the cavity's side
 *       pockets and up the winding neck,</li>
 *   <li>{@link Stage#INSERT_EXPLOSIVE} — carries the (bigger, twitchy) explosive from the tray down
 *       the same neck into the central chamber, slowly,</li>
 *   <li>{@link Stage#RESTUFF} — packs the stuffing back into the chamber around it,</li>
 *   <li>{@link Stage#SEW} — sews the seam shut by tracing a zig-zag thread line hole to hole.</li>
 * </ol>
 * Every time the tool (or what it carries) touches the cavity wall, pokes the fabric, wanders off
 * the sewing line or jostles the explosive, the doll's {@linkplain #integrity() stitch integrity}
 * drops; at zero the stitches rip ({@link Stage#RIPPED}) and the explosive goes off.
 * <p>
 * Pure Java on purpose (no Minecraft classes): the screen feeds it pointer moves, presses and frame
 * deltas and drains its {@link Event}s for sounds/effects; unit tests play it by script. All
 * coordinates are "board" units (one GUI pixel each) on a {@link #BOARD_W}×{@link #BOARD_H} board.
 */
public final class SurgeryGame {

	public enum Stage { UNSTITCH, REMOVE_STUFFING, INSERT_EXPLOSIVE, RESTUFF, SEW, DONE, RIPPED }

	/** Things that happened since the last {@link #drainEvents()}, for sounds and effects. */
	public enum Event { SNIP, POKE, TOUCH, PICK_UP, DROP, FLUFF_OUT, EXPLOSIVE_IN, FLUFF_IN, STITCH, JOSTLE,
		STAGE_CLEAR, WARNING, RIPPED, SUCCESS }

	// --- Board layout ------------------------------------------------------------

	public static final float BOARD_W = 220f;
	public static final float BOARD_H = 160f;
	/** The plush body (everything inside it that isn't cavity is fabric you must not touch). */
	public static final float FABRIC_X0 = 6f, FABRIC_Y0 = 20f, FABRIC_X1 = 160f, FABRIC_Y1 = 154f;
	/** The instrument tray to the right of the doll: stuffing bowl on top, explosive below. */
	public static final float TRAY_X0 = 168f, TRAY_Y0 = 20f, TRAY_X1 = 214f, TRAY_Y1 = 154f;
	public static final float BOWL_X = 191f, BOWL_Y = 52f, BOWL_R = 19f;
	public static final float EXPLOSIVE_HOME_X = 191f, EXPLOSIVE_HOME_Y = 122f;

	public static final float OPEN_X = 83f;
	public static final float SEAM_Y = 36f;
	public static final int SEAM_HOLES = 6;
	public static final float SEAM_X0 = OPEN_X - 30f;
	public static final float SEAM_SPACING = 12f;
	public static final float SEAM_ZIGZAG = 3f;

	public static final float NECK_R = 11.5f;
	public static final float CHAMBER_R = 25f;
	public static final float CHANNEL_R = 8.5f;
	public static final float POCKET_R = 10f;
	public static final float FLUFF_R = 5f;
	public static final float EXPLOSIVE_R = 7f;
	public static final float TIP_R = 1.5f;

	// --- Tuning ------------------------------------------------------------------

	/** Integrity lost on the first frame of a new wall touch, plus per second while it lasts. */
	static final float TOUCH_HIT = 0.12f;
	static final float TOUCH_DRAIN_PER_SEC = 0.45f;
	/** A stab at the fabric beside the stitches (or beside the sewing line). */
	static final float POKE_HIT = 0.08f;
	/** Moving the explosive faster than this (board px/s, smoothed) jostles it. */
	static final float JOSTLE_SPEED = 160f;
	static final float JOSTLE_DRAIN_PER_SEC = 0.35f;
	static final float SNIP_TOLERANCE = 3f;
	static final float POKE_NEAR_SEAM = 10f;
	static final float SEW_TOLERANCE = 3.5f;
	static final float WARNING_LEVEL = 0.3f;

	// --- Geometry ----------------------------------------------------------------

	/** A capsule (segment swept by a radius); the cavity is the union of these. */
	public record Capsule(float ax, float ay, float bx, float by, float r) {}

	/** A tuft of stuffing. {@code home*} is where it sits when dropped in a bad spot. */
	public static final class Fluff {
		public float x, y;
		float homeX, homeY;
		public boolean removed;
		public boolean restuffed;

		Fluff(float x, float y) {
			this.x = this.homeX = x;
			this.y = this.homeY = y;
		}
	}

	private final List<Capsule> cavity = new ArrayList<>();
	private final float[] neckX = new float[4];
	private final float[] neckY = new float[4];
	private final float chamberX, chamberY;
	private final List<float[]> pockets = new ArrayList<>();
	private final List<Fluff> fluffs = new ArrayList<>();
	private final boolean[] snipped = new boolean[SEAM_HOLES - 1];
	private int sewn;

	private float explosiveX = EXPLOSIVE_HOME_X, explosiveY = EXPLOSIVE_HOME_Y;
	private float explosiveHomeX = EXPLOSIVE_HOME_X, explosiveHomeY = EXPLOSIVE_HOME_Y;
	private boolean explosiveInside;

	// --- Live state --------------------------------------------------------------

	private final boolean tremorEnabled;
	private final float tremorPhase;
	private Stage stage = Stage.UNSTITCH;
	private float integrity = 1f;
	private float time;
	private float pointerX, pointerY;
	private float tipX, tipY;
	private boolean pressed;
	/** The step the current grip began in: a hold carried over from a finished step never scrapes. */
	private Stage pressStage;
	private Fluff carriedFluff;
	private boolean carryingExplosive;
	private boolean sewing;
	private float grabDX, grabDY;
	private float lastCarryX, lastCarryY;
	private float smoothedSpeed;
	private boolean touching;
	private boolean jostling;
	private boolean warned;
	private float lastHurtTime = -10f;
	private final List<Event> events = new ArrayList<>();

	public SurgeryGame(long seed) {
		this(seed, true);
	}

	/** @param tremor false for a perfectly steady hand (tests). */
	public SurgeryGame(long seed, boolean tremor) {
		Random random = new Random(seed);
		this.tremorEnabled = tremor;
		this.tremorPhase = random.nextFloat() * 100f;

		// Winding neck from just above the fabric down to the central chamber.
		float p1 = OPEN_X + (random.nextFloat() * 44f - 22f);
		float p2;
		do {
			p2 = OPEN_X + (random.nextFloat() * 44f - 22f);
		} while (Math.abs(p2 - p1) < 14f);
		chamberX = OPEN_X + (random.nextFloat() * 20f - 10f);
		chamberY = 110f;
		neckX[0] = OPEN_X; neckY[0] = FABRIC_Y0 - 12f;
		neckX[1] = p1; neckY[1] = 58f;
		neckX[2] = p2; neckY[2] = 80f;
		neckX[3] = chamberX; neckY[3] = chamberY;
		for (int i = 0; i < 3; i++) {
			cavity.add(new Capsule(neckX[i], neckY[i], neckX[i + 1], neckY[i + 1], NECK_R));
		}
		cavity.add(new Capsule(chamberX, chamberY, chamberX, chamberY, CHAMBER_R));

		// Three side pockets (out of right / left / lower-right / lower-left), each holding a fluff.
		List<Float> angles = new ArrayList<>(List.of(0f, 180f, 45f, 135f));
		Collections.shuffle(angles, random);
		for (int i = 0; i < 3; i++) {
			float base = angles.get(i);
			float deg = base + (random.nextFloat() * 24f - 12f);
			float dist = (base == 0f || base == 180f) ? 38f : 36f;
			double rad = Math.toRadians(deg);
			float qx = chamberX + (float) Math.cos(rad) * dist;
			float qy = chamberY + (float) Math.sin(rad) * dist;
			cavity.add(new Capsule(chamberX, chamberY, qx, qy, CHANNEL_R));
			cavity.add(new Capsule(qx, qy, qx, qy, POCKET_R));
			pockets.add(new float[]{qx, qy});
			fluffs.add(new Fluff(qx, qy));
		}
		fluffs.add(new Fluff(chamberX + 8f, chamberY + 6f));
	}

	// --- Input -------------------------------------------------------------------

	/** The raw pointer position (board units). The tool tip trails it with a little hand tremor. */
	public void pointer(float x, float y) {
		pointerX = x;
		pointerY = y;
	}

	/** Mouse button down at the current tool tip. */
	public void press() {
		if (isOver()) return;
		pressed = true;
		pressStage = stage;
		switch (stage) {
			case UNSTITCH -> pressUnstitch();
			case REMOVE_STUFFING, RESTUFF -> {
				Fluff grab = fluffAt(tipX, tipY);
				if (grab != null) {
					carriedFluff = grab;
					grabDX = grab.x - tipX;
					grabDY = grab.y - tipY;
					lastCarryX = grab.x;
					lastCarryY = grab.y;
					events.add(Event.PICK_UP);
				} else {
					pokeIfOnFabric();
				}
			}
			case INSERT_EXPLOSIVE -> {
				if (dist(tipX, tipY, explosiveX, explosiveY) <= EXPLOSIVE_R + 2f) {
					carryingExplosive = true;
					grabDX = explosiveX - tipX;
					grabDY = explosiveY - tipY;
					lastCarryX = explosiveX;
					lastCarryY = explosiveY;
					smoothedSpeed = 0f;
					events.add(Event.PICK_UP);
				} else {
					pokeIfOnFabric();
				}
			}
			case SEW -> {
				if (sewBandDistance(tipX, tipY) <= SEW_TOLERANCE) {
					sewing = true;
				} else if (dist(tipX, tipY, OPEN_X, SEAM_Y) < 40f) {
					hurt(POKE_HIT);
					events.add(Event.POKE);
				}
			}
			default -> {}
		}
	}

	/** Mouse button up: whatever the tweezers held is set down. */
	public void release() {
		pressed = false;
		sewing = false;
		if (carriedFluff != null) {
			Fluff fluff = carriedFluff;
			carriedFluff = null;
			if (inContact(fluff.x, fluff.y, FLUFF_R)) {
				fluff.x = fluff.homeX;
				fluff.y = fluff.homeY;
			} else {
				fluff.homeX = fluff.x;
				fluff.homeY = fluff.y;
			}
			events.add(Event.DROP);
		}
		if (carryingExplosive) {
			carryingExplosive = false;
			if (inContact(explosiveX, explosiveY, EXPLOSIVE_R)) {
				explosiveX = explosiveHomeX;
				explosiveY = explosiveHomeY;
			} else {
				explosiveHomeX = explosiveX;
				explosiveHomeY = explosiveY;
			}
			events.add(Event.DROP);
		}
		touching = false;
		jostling = false;
	}

	/** Advances the game by {@code dt} seconds. */
	public void tick(float dt) {
		if (dt <= 0f) return;
		time += dt;
		updateTip();
		if (isOver()) return;

		boolean contact = false;
		boolean jostle = false;

		if (carriedFluff != null) {
			carriedFluff.x = tipX + grabDX;
			carriedFluff.y = tipY + grabDY;
			contact = inContact(carriedFluff.x, carriedFluff.y, FLUFF_R);
			if (!contact) {
				carriedFluff.homeX = carriedFluff.x;
				carriedFluff.homeY = carriedFluff.y;
			}
			if (stage == Stage.REMOVE_STUFFING && !overlapsFabric(carriedFluff.x, carriedFluff.y, FLUFF_R)) {
				carriedFluff.removed = true;
				int index = countRemoved() - 1;
				carriedFluff.x = BOWL_X + (index % 2 == 0 ? -7f : 7f);
				carriedFluff.y = BOWL_Y + (index < 2 ? -5f : 6f);
				carriedFluff.homeX = carriedFluff.x;
				carriedFluff.homeY = carriedFluff.y;
				carriedFluff = null;
				contact = false;
				events.add(Event.FLUFF_OUT);
				if (countRemoved() == fluffs.size()) advance(Stage.INSERT_EXPLOSIVE);
			} else if (stage == Stage.RESTUFF && !carriedFluff.restuffed
					&& dist(carriedFluff.x, carriedFluff.y, chamberX, chamberY) <= CHAMBER_R - FLUFF_R) {
				int index = countRestuffed();
				double rad = Math.toRadians(45 + 90 * index);
				carriedFluff.restuffed = true;
				carriedFluff.x = chamberX + (float) Math.cos(rad) * 15f;
				carriedFluff.y = chamberY + (float) Math.sin(rad) * 15f;
				carriedFluff = null;
				contact = false;
				events.add(Event.FLUFF_IN);
				if (countRestuffed() == fluffs.size()) advance(Stage.SEW);
			}
		} else if (carryingExplosive) {
			explosiveX = tipX + grabDX;
			explosiveY = tipY + grabDY;
			float speed = dist(explosiveX, explosiveY, lastCarryX, lastCarryY) / dt;
			lastCarryX = explosiveX;
			lastCarryY = explosiveY;
			smoothedSpeed += (speed - smoothedSpeed) * Math.min(1f, dt * 10f);
			contact = inContact(explosiveX, explosiveY, EXPLOSIVE_R);
			jostle = smoothedSpeed > JOSTLE_SPEED;
			if (!contact) {
				explosiveHomeX = explosiveX;
				explosiveHomeY = explosiveY;
			}
			if (dist(explosiveX, explosiveY, chamberX, chamberY) <= 6f) {
				explosiveX = chamberX;
				explosiveY = chamberY;
				explosiveInside = true;
				carryingExplosive = false;
				contact = false;
				jostle = false;
				events.add(Event.EXPLOSIVE_IN);
				for (Fluff fluff : fluffs) {
					fluff.removed = false;
				}
				advance(Stage.RESTUFF);
			}
		} else if (sewing) {
			if (sewBandDistance(tipX, tipY) > SEW_TOLERANCE) {
				contact = true;
			} else if (dist(tipX, tipY, holeX(sewn + 1), holeY(sewn + 1)) <= SEW_TOLERANCE) {
				sewn++;
				events.add(Event.STITCH);
				if (sewn == SEAM_HOLES - 1) {
					sewing = false;
					stage = Stage.DONE;
					events.add(Event.SUCCESS);
					return;
				}
			}
		} else if (pressed && pressStage == stage && (stage == Stage.REMOVE_STUFFING || stage == Stage.INSERT_EXPLOSIVE || stage == Stage.RESTUFF)) {
			// Empty tweezers held down still scrape whatever they touch.
			contact = inContact(tipX, tipY, TIP_R);
		}

		if (contact) {
			if (!touching) {
				hurt(TOUCH_HIT);
				events.add(Event.TOUCH);
			}
			hurt(TOUCH_DRAIN_PER_SEC * dt);
		}
		if (jostle) {
			if (!jostling) events.add(Event.JOSTLE);
			hurt(JOSTLE_DRAIN_PER_SEC * dt);
		}
		touching = contact;
		jostling = jostle;
	}

	public List<Event> drainEvents() {
		List<Event> out = List.copyOf(events);
		events.clear();
		return out;
	}

	// --- Stage logic -------------------------------------------------------------

	private void pressUnstitch() {
		for (int i = 0; i < snipped.length; i++) {
			if (!snipped[i] && dist(tipX, tipY, stitchX(i), stitchY(i)) <= SNIP_TOLERANCE) {
				snipped[i] = true;
				events.add(Event.SNIP);
				boolean all = true;
				for (boolean s : snipped) all &= s;
				if (all) advance(Stage.REMOVE_STUFFING);
				return;
			}
		}
		if (tipX >= SEAM_X0 - POKE_NEAR_SEAM && tipX <= holeX(SEAM_HOLES - 1) + POKE_NEAR_SEAM
				&& Math.abs(tipY - SEAM_Y) <= POKE_NEAR_SEAM) {
			hurt(POKE_HIT);
			events.add(Event.POKE);
		}
	}

	private void pokeIfOnFabric() {
		if (inContact(tipX, tipY, TIP_R)) {
			hurt(POKE_HIT);
			events.add(Event.POKE);
			touching = true; // the drain picks up next tick without a second hit
		}
	}

	private void advance(Stage next) {
		stage = next;
		events.add(Event.STAGE_CLEAR);
	}

	private void hurt(float amount) {
		if (isOver()) return;
		integrity -= amount;
		lastHurtTime = time;
		if (!warned && integrity <= WARNING_LEVEL) {
			warned = true;
			events.add(Event.WARNING);
		}
		if (integrity <= 0f) {
			integrity = 0f;
			stage = Stage.RIPPED;
			carriedFluff = null;
			carryingExplosive = false;
			sewing = false;
			events.add(Event.RIPPED);
		}
	}

	/** Gives up mid-operation with the explosive already inside: counts as a rip. */
	public void abandon() {
		if (!isOver()) hurt(integrity + 1f);
	}

	private void updateTip() {
		float amp = 0f;
		if (tremorEnabled) {
			amp = 0.6f + (carryingExplosive ? 1.0f : 0f) + (1f - integrity) * 1.2f;
		}
		float t = time + tremorPhase;
		tipX = pointerX + amp * (float) (Math.sin(t * 7.3) + 0.6 * Math.sin(t * 13.1 + 1.7)) * 0.65f;
		tipY = pointerY + amp * (float) (Math.sin(t * 6.1 + 0.4) + 0.6 * Math.sin(t * 11.7 + 2.9)) * 0.65f;
	}

	// --- Geometry queries ----------------------------------------------------------

	/** True when a circle sits entirely inside one cavity capsule. */
	public boolean insideCavity(float x, float y, float r) {
		for (Capsule c : cavity) {
			if (segmentDistance(x, y, c.ax, c.ay, c.bx, c.by) <= c.r - r + 0.01f) return true;
		}
		return false;
	}

	/** True when a circle overlaps the plush body at all. */
	public static boolean overlapsFabric(float x, float y, float r) {
		float cx = Math.max(FABRIC_X0, Math.min(x, FABRIC_X1));
		float cy = Math.max(FABRIC_Y0, Math.min(y, FABRIC_Y1));
		return dist(x, y, cx, cy) < r;
	}

	/** Touching fabric that isn't cavity — a wall bump. */
	public boolean inContact(float x, float y, float r) {
		return overlapsFabric(x, y, r) && !insideCavity(x, y, r);
	}

	/** Cavity membership of a single board pixel (for drawing). */
	public boolean cavityPixel(float x, float y) {
		return insideCavity(x, y, 0f);
	}

	private float sewBandDistance(float x, float y) {
		int from = Math.min(sewn, SEAM_HOLES - 2);
		return segmentDistance(x, y, holeX(from), holeY(from), holeX(from + 1), holeY(from + 1));
	}

	private Fluff fluffAt(float x, float y) {
		for (Fluff fluff : fluffs) {
			if (stage == Stage.REMOVE_STUFFING && fluff.removed) continue;
			if (stage == Stage.RESTUFF && fluff.restuffed) continue;
			if (dist(x, y, fluff.x, fluff.y) <= FLUFF_R + 2f) return fluff;
		}
		return null;
	}

	static float segmentDistance(float px, float py, float ax, float ay, float bx, float by) {
		float dx = bx - ax, dy = by - ay;
		float len2 = dx * dx + dy * dy;
		float t = len2 == 0f ? 0f : Math.max(0f, Math.min(1f, ((px - ax) * dx + (py - ay) * dy) / len2));
		return dist(px, py, ax + t * dx, ay + t * dy);
	}

	static float dist(float ax, float ay, float bx, float by) {
		float dx = ax - bx, dy = ay - by;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	// --- Read-only view for the screen and tests -----------------------------------

	public Stage stage() { return stage; }
	public boolean isOver() { return stage == Stage.DONE || stage == Stage.RIPPED; }
	public float integrity() { return integrity; }
	public float time() { return time; }
	public float pointerX() { return pointerX; }
	public float pointerY() { return pointerY; }
	public float tipX() { return tipX; }
	public float tipY() { return tipY; }
	public boolean isPressed() { return pressed; }
	public boolean isTouching() { return touching; }
	public boolean isJostling() { return jostling; }
	/** Seconds since the doll last got hurt (for flinch/flash effects). */
	public float sinceHurt() { return time - lastHurtTime; }
	public boolean isCarryingFluff() { return carriedFluff != null; }
	/** The tuft in the tweezers, or null. */
	public Fluff carriedFluff() { return carriedFluff; }
	public boolean isCarryingExplosive() { return carryingExplosive; }
	public boolean isSewing() { return sewing; }
	public List<Capsule> cavity() { return Collections.unmodifiableList(cavity); }
	public List<Fluff> fluffs() { return Collections.unmodifiableList(fluffs); }
	public float chamberX() { return chamberX; }
	public float chamberY() { return chamberY; }
	/** Neck path points, mouth (above the fabric) first, chamber centre last. */
	public float neckX(int i) { return neckX[i]; }
	public float neckY(int i) { return neckY[i]; }
	public int neckPoints() { return neckX.length; }
	public float[] pocket(int i) { return pockets.get(i).clone(); }
	public int pocketCount() { return pockets.size(); }
	public float explosiveX() { return explosiveX; }
	public float explosiveY() { return explosiveY; }
	public boolean explosiveInside() { return explosiveInside; }
	public boolean snipped(int stitch) { return snipped[stitch]; }
	public int sewn() { return sewn; }
	public boolean seamOpen() { return stage != Stage.UNSTITCH; }
	public int countRemoved() { int n = 0; for (Fluff f : fluffs) if (f.removed) n++; return n; }
	public int countRestuffed() { int n = 0; for (Fluff f : fluffs) if (f.restuffed) n++; return n; }

	public static float holeX(int i) { return SEAM_X0 + SEAM_SPACING * i; }
	public static float holeY(int i) { return SEAM_Y + (i % 2 == 0 ? -SEAM_ZIGZAG : SEAM_ZIGZAG); }

	/** Centre of stitch {@code i} (between holes i and i+1), bobbing gently as the doll "breathes". */
	public float stitchX(int i) { return (holeX(i) + holeX(i + 1)) / 2f; }
	public float stitchY(int i) { return SEAM_Y + (float) Math.sin(time * 2.4 + i * 1.3) * 1.2f; }
}
