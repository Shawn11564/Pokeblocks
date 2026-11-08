package trapped;

import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame.Capsule;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame.Event;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame.Fluff;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame.Stage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The doll-surgery minigame model: every generated layout is winnable by a careful hand (with and
 * without tremor) without a single wall touch, and sloppy play rips the doll.
 */
class SurgeryGameTest {

	private static final float DT = 1f / 60f;
	/** Board px per frame for a careful hand: 1.5 px at 60 fps = 90 px/s, under the jostle limit. */
	private static final float STEP = 1.5f;

	@Test
	void everyLayoutStaysInsideTheFabric() {
		for (long seed = 0; seed < 2000; seed++) {
			SurgeryGame game = new SurgeryGame(seed, false);
			for (Capsule c : game.cavity()) {
				if (c.ay() < SurgeryGame.FABRIC_Y0) continue; // the mouth segment pokes out of the top on purpose
				float minX = Math.min(c.ax(), c.bx()) - c.r();
				float maxX = Math.max(c.ax(), c.bx()) + c.r();
				float maxY = Math.max(c.ay(), c.by()) + c.r();
				assertTrue(minX >= SurgeryGame.FABRIC_X0 + 3, "seed " + seed + " cavity leaks left");
				assertTrue(maxX <= SurgeryGame.FABRIC_X1 - 3, "seed " + seed + " cavity leaks right");
				assertTrue(maxY <= SurgeryGame.FABRIC_Y1 - 3, "seed " + seed + " cavity leaks bottom");
			}
		}
	}

	@Test
	void carefulHandWinsEveryLayoutWithoutATouch() {
		for (long seed = 0; seed < 300; seed++) {
			for (boolean tremor : new boolean[]{false, true}) {
				SurgeryGame game = new SurgeryGame(seed, tremor);
				List<Event> events = solve(game);
				assertEquals(Stage.DONE, game.stage(), "seed " + seed + " tremor " + tremor);
				assertEquals(1f, game.integrity(), 1e-6, "seed " + seed + " tremor " + tremor + " got hurt");
				assertFalse(events.contains(Event.TOUCH), "seed " + seed);
				assertFalse(events.contains(Event.JOSTLE), "seed " + seed);
				assertTrue(events.contains(Event.SUCCESS));
			}
		}
	}

	@Test
	void draggingStuffingThroughTheWallHurts() {
		SurgeryGame game = new SurgeryGame(7, false);
		unstitch(game);
		Fluff fluff = game.fluffs().get(0);
		moveTo(game, fluff.x, fluff.y);
		game.press();
		walk(game, fluff.x, 0f); // straight up through the fabric
		game.release();
		assertTrue(game.integrity() < 0.8f, "integrity " + game.integrity());
		assertTrue(game.drainEvents().contains(Event.TOUCH));
	}

	@Test
	void holdingAgainstTheWallRipsTheDoll() {
		SurgeryGame game = new SurgeryGame(3, false);
		unstitch(game);
		// Empty tweezers pressed into the fabric beside the cavity.
		moveTo(game, SurgeryGame.FABRIC_X0 + 2, SurgeryGame.FABRIC_Y1 - 2);
		game.press();
		for (int i = 0; i < 60 * 4 && !game.isOver(); i++) game.tick(DT);
		assertEquals(Stage.RIPPED, game.stage());
		List<Event> events = game.drainEvents();
		assertTrue(events.contains(Event.WARNING));
		assertTrue(events.contains(Event.RIPPED));
	}

	@Test
	void rushingTheExplosiveJostlesIt() {
		SurgeryGame game = new SurgeryGame(11, false);
		unstitch(game);
		removeStuffing(game);
		assertEquals(Stage.INSERT_EXPLOSIVE, game.stage());
		moveTo(game, game.explosiveX(), game.explosiveY());
		game.press();
		// 6 px per frame = 360 px/s, well over the limit, in open air above the tray.
		for (int i = 0; i < 15; i++) {
			game.pointer(SurgeryGame.EXPLOSIVE_HOME_X, SurgeryGame.EXPLOSIVE_HOME_Y - 6f * (i + 1));
			game.tick(DT);
		}
		assertTrue(game.drainEvents().contains(Event.JOSTLE));
		assertTrue(game.integrity() < 1f);
	}

	@Test
	void abandoningRips() {
		SurgeryGame game = new SurgeryGame(1, false);
		game.abandon();
		assertEquals(Stage.RIPPED, game.stage());
	}

	// --- A scripted careful surgeon ------------------------------------------------

	private static List<Event> solve(SurgeryGame game) {
		List<Event> events = new ArrayList<>();
		unstitch(game);
		events.addAll(game.drainEvents());
		removeStuffing(game);
		events.addAll(game.drainEvents());
		insertExplosive(game);
		events.addAll(game.drainEvents());
		restuff(game);
		events.addAll(game.drainEvents());
		sew(game);
		events.addAll(game.drainEvents());
		return events;
	}

	private static void unstitch(SurgeryGame game) {
		for (int i = 0; i < SurgeryGame.SEAM_HOLES - 1; i++) {
			game.tick(DT);
			moveTo(game, game.stitchX(i), game.stitchY(i));
			game.press();
			// A real click holds the button for a few frames (the last snip changes the step mid-hold).
			for (int f = 0; f < 4; f++) game.tick(DT);
			game.release();
		}
		assertEquals(Stage.REMOVE_STUFFING, game.stage());
	}

	private static void removeStuffing(SurgeryGame game) {
		for (Fluff fluff : game.fluffs()) {
			moveTo(game, fluff.x, fluff.y);
			game.press();
			walk(game, game.chamberX(), game.chamberY());
			for (int p = game.neckPoints() - 2; p >= 0; p--) {
				walk(game, game.neckX(p), game.neckY(p));
			}
			walk(game, game.neckX(0), -4f);
			game.release();
			assertTrue(fluff.removed);
		}
	}

	private static void insertExplosive(SurgeryGame game) {
		moveTo(game, game.explosiveX(), game.explosiveY());
		game.press();
		walk(game, SurgeryGame.EXPLOSIVE_HOME_X, 2f);
		walk(game, game.neckX(0), 2f);
		for (int p = 0; p < game.neckPoints() && !game.explosiveInside(); p++) {
			walk(game, game.neckX(p), game.neckY(p));
		}
		game.release();
		assertTrue(game.explosiveInside());
		assertEquals(Stage.RESTUFF, game.stage());
	}

	private static void restuff(SurgeryGame game) {
		for (Fluff fluff : game.fluffs()) {
			moveTo(game, fluff.x, fluff.y);
			game.press();
			walk(game, fluff.x, 2f);
			walk(game, game.neckX(0), 2f);
			for (int p = 0; p < game.neckPoints() && !fluff.restuffed; p++) {
				walk(game, game.neckX(p), game.neckY(p));
			}
			game.release();
			assertTrue(fluff.restuffed);
		}
		assertEquals(Stage.SEW, game.stage());
	}

	private static void sew(SurgeryGame game) {
		moveTo(game, SurgeryGame.holeX(0), SurgeryGame.holeY(0));
		game.press();
		for (int i = 1; i < SurgeryGame.SEAM_HOLES && !game.isOver(); i++) {
			walk(game, SurgeryGame.holeX(i), SurgeryGame.holeY(i));
		}
		game.release();
	}

	/** Teleports the (unpressed) pointer and lets one frame pass. */
	private static void moveTo(SurgeryGame game, float x, float y) {
		game.pointer(x, y);
		game.tick(DT);
	}

	/** Steadily walks the pointer to (x, y), one careful step per frame. Stops early once the step's goal was reached. */
	private static void walk(SurgeryGame game, float x, float y) {
		// Track the raw pointer rather than the trembling tip.
		float px = game.pointerX(), py = game.pointerY();
		float dx = x - px, dy = y - py;
		float len = (float) Math.sqrt(dx * dx + dy * dy);
		int steps = Math.max(1, (int) Math.ceil(len / STEP));
		for (int i = 1; i <= steps && !game.isOver(); i++) {
			game.pointer(px + dx * i / steps, py + dy * i / steps);
			game.tick(DT);
		}
	}
}
