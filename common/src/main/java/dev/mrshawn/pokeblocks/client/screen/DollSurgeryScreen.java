package dev.mrshawn.pokeblocks.client.screen;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.registry.SoundRegistry;
import dev.mrshawn.pokeblocks.trapped.surgery.DollSurgery;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame.Event;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame.Fluff;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame.Stage;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryPayloads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * The doll-surgery minigame screen: an Operation-style board where the player opens a pokedoll,
 * swaps its stuffing for an explosive and sews it back up without touching the sides (see
 * {@link SurgeryGame} for the rules, {@link DollSurgery} for what the server does with the result).
 * <p>
 * Opened over the crafting screen (whose menu stays open); finishing returns to it, a rip closes it
 * and lets the server's blast go off. Everything is drawn procedurally — fabric, cavity, stitches,
 * stuffing, tools — so it needs no textures beyond the compendium panel.
 */
public class DollSurgeryScreen extends Screen {

	private static final ResourceLocation SPRITE_PANEL =
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "compendium/panel");

	private static final int PANEL_W = 300;
	private static final int PANEL_H = 216;
	private static final int BOARD_OFFSET_X = 72;
	private static final int BOARD_OFFSET_Y = 24;

	// Palette: pink plush, plum insides, cream stuffing, steel instruments.
	private static final int COL_AIR = 0xFFE4F2EE;
	private static final int COL_FABRIC = 0xFFF4B3C9;
	private static final int COL_FABRIC_DOT = 0xFFF9CFDD;
	private static final int COL_FABRIC_EDGE = 0xFFC9708F;
	private static final int COL_CAVITY = 0xFF4E2A40;
	private static final int COL_CAVITY_DEEP = 0xFF3A1E30;
	private static final int COL_RIM = 0xFFFFE0EA;
	private static final int COL_RIM_HURT = 0xFFFF3B3B;
	private static final int COL_THREAD = 0xFF5B3FA0;
	private static final int COL_THREAD_LIGHT = 0xFF8F74D6;
	private static final int COL_HOLE = 0xFF6E3550;
	private static final int COL_FLUFF = 0xFFFFF8EA;
	private static final int COL_FLUFF_SHADE = 0xFFE3D8C2;
	private static final int COL_TRAY = 0xFFB9C4CE;
	private static final int COL_TRAY_EDGE = 0xFF7D8A96;
	private static final int COL_BOWL = 0xFFDCE4EA;
	private static final int COL_STEEL = 0xFF8C99A6;
	private static final int COL_STEEL_LIGHT = 0xFFE4EAF0;
	private static final int COL_GUIDE = 0xFFFFD84A;

	private static final float RIP_SEQUENCE_SECONDS = 1.1f;
	private static final float SUCCESS_SEQUENCE_SECONDS = 1.6f;
	private static final float ABANDON_CONFIRM_SECONDS = 2.5f;

	private final int token;
	private final ItemStack doll;
	private final ItemStack explosive;
	@Nullable
	private final Screen previous;
	private final SurgeryGame game;
	private final FrameClock clock = new FrameClock();
	private final DollSpin spin = new DollSpin();
	private final RandomSource random = RandomSource.create();
	private final Component explosiveName;

	private final List<int[]> cavitySpans = new ArrayList<>();
	private final List<int[]> rimPixels = new ArrayList<>();
	private final List<int[]> fabricDots = new ArrayList<>();
	private final List<Particle> particles = new ArrayList<>();
	private final List<Popup> popups = new ArrayList<>();

	private int panelLeft;
	private int panelTop;
	private int boardLeft;
	private int boardTop;
	private float now;
	private float endedAt = -1f;
	private float abandonArmedUntil = -1f;
	private float nextHeartbeat;
	private boolean finished;
	private boolean cursorHidden;

	// Autopilot for the in-game regression check (DollSurgeryHarness): steers the pointer at a
	// careful, fixed speed instead of reading the mouse. Never set in normal play.
	private static final float AUTOPILOT_SPEED = 60f;
	@Nullable
	private Supplier<float[]> autopilotTarget;
	private float autoX = Float.NaN, autoY;

	public DollSurgeryScreen(SurgeryPayloads.StartPayload start, @Nullable Screen previous) {
		super(Component.translatable("screen.pokeblocks.surgery.title"));
		this.token = start.token();
		this.doll = start.doll();
		this.explosive = start.explosive();
		this.explosiveName = explosive.getHoverName();
		this.previous = previous;
		this.game = new SurgeryGame(random.nextLong());
		bakeGeometry();
	}

	/** Opens the surgery over whatever screen is showing (normally the crafting table). */
	public static void open(SurgeryPayloads.StartPayload start) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player == null) return;
		Screen current = minecraft.screen instanceof DollSurgeryScreen surgery ? surgery.previous : minecraft.screen;
		minecraft.setScreen(new DollSurgeryScreen(start, current));
	}

	// --- Setup -----------------------------------------------------------------------

	/** The cavity shape never changes, so rasterize it (and the fabric's polka dots) once. */
	private void bakeGeometry() {
		int x0 = (int) SurgeryGame.FABRIC_X0, x1 = (int) SurgeryGame.FABRIC_X1;
		int y0 = (int) SurgeryGame.FABRIC_Y0, y1 = (int) SurgeryGame.FABRIC_Y1;
		boolean[][] inside = new boolean[y1 - y0][x1 - x0];
		for (int y = y0; y < y1; y++) {
			int spanStart = -1;
			for (int x = x0; x <= x1; x++) {
				boolean in = x < x1 && game.cavityPixel(x + 0.5f, y + 0.5f);
				if (x < x1) inside[y - y0][x - x0] = in;
				if (in && spanStart < 0) spanStart = x;
				if (!in && spanStart >= 0) {
					cavitySpans.add(new int[]{y, spanStart, x});
					spanStart = -1;
				}
			}
		}
		for (int y = y0; y < y1; y++) {
			for (int x = x0; x < x1; x++) {
				if (!inside[y - y0][x - x0]) continue;
				boolean edge = x == x0 || x == x1 - 1 || y == y1 - 1
						|| (y > y0 && !inside[y - y0 - 1][x - x0]) || (y < y1 - 1 && !inside[y - y0 + 1][x - x0])
						|| !inside[y - y0][x - x0 - 1] || !inside[y - y0][x - x0 + 1];
				if (edge && y > y0) rimPixels.add(new int[]{x, y});
			}
		}
		for (int y = y0 + 6; y < y1 - 3; y += 12) {
			for (int x = x0 + 6 + ((y / 12) % 2) * 6; x < x1 - 3; x += 12) {
				fabricDots.add(new int[]{x, y});
			}
		}
	}

	@Override
	protected void init() {
		panelLeft = (width - PANEL_W) / 2;
		panelTop = (height - PANEL_H) / 2;
		boardLeft = panelLeft + BOARD_OFFSET_X;
		boardTop = panelTop + BOARD_OFFSET_Y;
	}

	// --- Frame -----------------------------------------------------------------------

	@Override
	public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		super.render(g, mouseX, mouseY, partialTick);
		clock.tick();
		float dt = clock.dt();
		now += dt;
		spin.update(dt);

		double mx = minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth();
		double my = minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight();
		if (autopilotTarget != null) {
			float[] target = autopilotTarget.get();
			if (Float.isNaN(autoX)) {
				autoX = game.pointerX();
				autoY = game.pointerY();
			}
			float dx = target[0] - autoX, dy = target[1] - autoY;
			float len = (float) Math.sqrt(dx * dx + dy * dy);
			float step = AUTOPILOT_SPEED * dt;
			if (len <= step) {
				autoX = target[0];
				autoY = target[1];
			} else {
				autoX += dx / len * step;
				autoY += dy / len * step;
			}
			game.pointer(autoX, autoY);
		} else {
			game.pointer((float) (mx - boardLeft), (float) (my - boardTop));
		}
		game.tick(dt);
		handleEvents();
		updateCursor(mx, my);
		tickSequences();

		// Flinch: the whole board shakes for a moment after every hurt.
		float flinch = Math.max(0f, 1f - game.sinceHurt() / 0.25f);
		if (game.stage() == Stage.RIPPED) flinch = Math.max(flinch, 1f - (now - endedAt) / RIP_SEQUENCE_SECONDS);
		int shakeX = Math.round((random.nextFloat() - 0.5f) * 5f * flinch);
		int shakeY = Math.round((random.nextFloat() - 0.5f) * 5f * flinch);
		int bx = boardLeft + shakeX;
		int by = boardTop + shakeY;

		drawBoard(g, bx, by);
		drawSidebar(g);
		drawStepText(g);

		g.pose().pushPose();
		g.pose().translate(0, 0, 400);
		if (!game.isOver()) drawTool(g, bx, by);
		drawParticles(g, dt);
		drawPopups(g, dt);
		drawEndOverlay(g);
		g.pose().popPose();
	}

	@Override
	public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
		super.renderBackground(g, mouseX, mouseY, partialTick);
		g.blitSprite(SPRITE_PANEL, panelLeft, panelTop, PANEL_W, PANEL_H);
	}

	private void drawBoard(GuiGraphics g, int bx, int by) {
		int w = (int) SurgeryGame.BOARD_W, h = (int) SurgeryGame.BOARD_H;
		g.fill(bx - 1, by - 1, bx + w + 1, by + h + 1, COL_TRAY_EDGE);
		g.fill(bx, by, bx + w, by + h, COL_AIR);

		// The plush body.
		int fx0 = bx + (int) SurgeryGame.FABRIC_X0, fy0 = by + (int) SurgeryGame.FABRIC_Y0;
		int fx1 = bx + (int) SurgeryGame.FABRIC_X1, fy1 = by + (int) SurgeryGame.FABRIC_Y1;
		g.fill(fx0 + 2, fy0 - 1, fx1 - 2, fy1 + 1, COL_FABRIC_EDGE);
		g.fill(fx0 - 1, fy0 + 2, fx1 + 1, fy1 - 2, COL_FABRIC_EDGE);
		g.fill(fx0 + 1, fy0, fx1 - 1, fy1, COL_FABRIC);
		g.fill(fx0, fy0 + 1, fx1, fy1 - 1, COL_FABRIC);
		for (int[] dot : fabricDots) {
			fillCircle(g, bx + dot[0], by + dot[1], 2.2f, COL_FABRIC_DOT);
		}

		// The insides, once the seam is open.
		if (game.seamOpen()) {
			for (int[] span : cavitySpans) {
				int depthColor = span[0] > game.chamberY() - 6 ? COL_CAVITY_DEEP : COL_CAVITY;
				g.fill(bx + span[1], by + span[0], bx + span[2], by + span[0] + 1, depthColor);
			}
			boolean hurtFlash = (game.isTouching() || game.sinceHurt() < 0.15f) && !game.isOver();
			int rim = hurtFlash ? COL_RIM_HURT : COL_RIM;
			for (int[] p : rimPixels) {
				g.fill(bx + p[0], by + p[1], bx + p[0] + 1, by + p[1] + 1, rim);
			}
		}

		drawSeam(g, bx, by);

		// Instrument tray + stuffing bowl.
		int tx0 = bx + (int) SurgeryGame.TRAY_X0, ty0 = by + (int) SurgeryGame.TRAY_Y0;
		int tx1 = bx + (int) SurgeryGame.TRAY_X1, ty1 = by + (int) SurgeryGame.TRAY_Y1;
		g.fill(tx0 - 1, ty0 - 1, tx1 + 1, ty1 + 1, COL_TRAY_EDGE);
		g.fill(tx0, ty0, tx1, ty1, COL_TRAY);
		fillCircle(g, bx + SurgeryGame.BOWL_X, by + SurgeryGame.BOWL_Y, SurgeryGame.BOWL_R + 1, COL_TRAY_EDGE);
		fillCircle(g, bx + SurgeryGame.BOWL_X, by + SurgeryGame.BOWL_Y, SurgeryGame.BOWL_R, COL_BOWL);

		// Stuffing (the carried tuft last, so it floats above the rest).
		Fluff carried = game.carriedFluff();
		for (Fluff fluff : game.fluffs()) {
			if (fluff != carried) drawFluff(g, bx + fluff.x, by + fluff.y);
		}

		// The explosive.
		g.pose().pushPose();
		float size = SurgeryGame.EXPLOSIVE_R * 2f;
		float jitter = game.isJostling() ? (random.nextFloat() - 0.5f) * 2f : 0f;
		g.pose().translate(bx + game.explosiveX() - size / 2f + jitter, by + game.explosiveY() - size / 2f, 0);
		g.pose().scale(size / 16f, size / 16f, 1f);
		g.renderFakeItem(explosive, 0, 0);
		g.pose().popPose();

		if (carried != null) {
			g.pose().pushPose();
			g.pose().translate(0, 0, 300);
			drawFluff(g, bx + carried.x, by + carried.y);
			g.pose().popPose();
		}

		// A red wash while something scrapes a wall; amber while the explosive is being rushed.
		if (!game.isOver() && (game.isTouching() || game.isJostling())) {
			g.pose().pushPose();
			g.pose().translate(0, 0, 320);
			g.fill(bx, by, bx + w, by + h, game.isTouching() ? 0x38FF2020 : 0x30FFA000);
			g.pose().popPose();
		}
	}

	private void drawSeam(GuiGraphics g, int bx, int by) {
		Stage stage = game.stage();
		for (int i = 0; i < SurgeryGame.SEAM_HOLES; i++) {
			int hx = Math.round(bx + SurgeryGame.holeX(i)), hy = Math.round(by + SurgeryGame.holeY(i));
			g.fill(hx - 1, hy - 1, hx + 1, hy + 1, COL_HOLE);
		}
		for (int i = 0; i < SurgeryGame.SEAM_HOLES - 1; i++) {
			float ax = bx + SurgeryGame.holeX(i), ay = by + SurgeryGame.holeY(i);
			float cx = bx + SurgeryGame.holeX(i + 1), cy = by + SurgeryGame.holeY(i + 1);
			if (stage == Stage.UNSTITCH) {
				float sx = bx + game.stitchX(i), sy = by + game.stitchY(i);
				if (game.snipped(i)) {
					// Frayed ends left behind.
					drawLine(g, ax, ay, ax + 3, sy - 1, COL_THREAD_LIGHT, 1);
					drawLine(g, cx, cy, cx - 3, sy + 1, COL_THREAD_LIGHT, 1);
				} else {
					drawLine(g, ax, ay, sx, sy, COL_THREAD, 2);
					drawLine(g, sx, sy, cx, cy, COL_THREAD, 2);
					g.fill(Math.round(sx) - 1, Math.round(sy) - 1, Math.round(sx) + 1, Math.round(sy) + 1, COL_THREAD_LIGHT);
				}
			} else if (stage == Stage.SEW || stage == Stage.DONE || (stage == Stage.RIPPED && i < game.sewn())) {
				if (i < game.sewn()) {
					drawLine(g, ax, ay, cx, cy, COL_THREAD, 2);
				} else if (i == game.sewn() && stage == Stage.SEW) {
					// Dotted guide to the next hole, which pulses.
					int dots = 6;
					for (int d = 0; d <= dots; d++) {
						float t = d / (float) dots;
						int px = Math.round(Mth.lerp(t, ax, cx)), py = Math.round(Mth.lerp(t, ay, cy));
						g.fill(px, py, px + 1, py + 1, COL_GUIDE);
					}
					float pulse = 1.5f + (float) Math.sin(now * 8) * 0.8f;
					fillCircle(g, cx, cy, pulse + 1f, COL_GUIDE);
				}
			}
		}
	}

	private void drawFluff(GuiGraphics g, float x, float y) {
		fillCircle(g, x - 2f, y + 1.5f, 3.4f, COL_FLUFF_SHADE);
		fillCircle(g, x + 2f, y + 1.5f, 3.4f, COL_FLUFF_SHADE);
		fillCircle(g, x - 2f, y + 1f, 3.1f, COL_FLUFF);
		fillCircle(g, x + 2f, y + 1f, 3.1f, COL_FLUFF);
		fillCircle(g, x, y - 1.5f, 3.4f, COL_FLUFF);
	}

	private void drawTool(GuiGraphics g, int bx, int by) {
		float tx = bx + game.tipX(), ty = by + game.tipY();
		switch (game.stage()) {
			case UNSTITCH -> {
				// Seam ripper: steel shaft, red handle, tiny red safety ball by the hook.
				drawLine(g, tx + 2, ty - 2, tx + 15, ty - 15, COL_STEEL, 1);
				drawLine(g, tx + 15, ty - 15, tx + 25, ty - 25, 0xFFD94B5B, 3);
				fillCircle(g, tx + 2.5f, ty - 0.5f, 1.2f, 0xFFE0383E);
			}
			case SEW -> {
				drawLine(g, tx, ty, tx + 13, ty - 13, COL_STEEL_LIGHT, 1);
				g.fill(Math.round(tx + 12), Math.round(ty - 14), Math.round(tx + 15), Math.round(ty - 11), COL_STEEL);
				float hx = bx + SurgeryGame.holeX(game.sewn()), hy = by + SurgeryGame.holeY(game.sewn());
				drawLine(g, hx, hy, tx + 13, ty - 13, COL_THREAD_LIGHT, 1);
			}
			default -> {
				// Tweezers, pinched shut while the button is held.
				float gap = game.isPressed() ? 1f : 4f;
				drawLine(g, tx - gap / 2f, ty, tx + 12, ty - 22, COL_STEEL, 2);
				drawLine(g, tx + gap / 2f, ty, tx + 18, ty - 18, COL_STEEL, 2);
				drawLine(g, tx + 12, ty - 22, tx + 18, ty - 18, COL_STEEL_LIGHT, 2);
			}
		}
		// The exact working point, so the player can see their own tremor.
		int px = Math.round(tx), py = Math.round(ty);
		g.fill(px - 1, py - 1, px + 2, py + 2, 0xFF202020);
		g.fill(px, py, px + 1, py + 1, game.isTouching() ? COL_RIM_HURT : 0xFFFFFFFF);
	}

	private void drawSidebar(GuiGraphics g) {
		int cx = panelLeft + 38;
		g.drawCenteredString(font, title, panelLeft + PANEL_W / 2, panelTop + 9, CompendiumScreen.COL_TEXT);

		// The patient, flinching when hurt and hopping for joy when it's over.
		float flinch = Math.max(0f, 1f - game.sinceHurt() / 0.3f);
		float jx = (random.nextFloat() - 0.5f) * 4f * flinch;
		float hop = game.stage() == Stage.DONE ? -Math.abs((float) Math.sin((now - endedAt) * 9f)) * 6f : 0f;
		g.drawCenteredString(font, Component.translatable("screen.pokeblocks.surgery.patient"), cx, panelTop + 26,
				CompendiumScreen.COL_TEXT_FADED);
		if (game.stage() != Stage.RIPPED) {
			CompendiumRender.render(g, CompendiumType.DOLLS, doll, cx + jx, panelTop + 62 + hop, 3.2f, false, spin.yaw());
		}

		// Stitch integrity.
		int barX = panelLeft + 12, barY = panelTop + 104, barW = 52, barH = 7;
		g.drawString(font, Component.translatable("screen.pokeblocks.surgery.stitches"), barX, barY - 11,
				CompendiumScreen.COL_TEXT, false);
		float integrity = game.integrity();
		int fill = Math.round(barW * integrity);
		int color = integrity > 0.6f ? 0xFF5BC062 : integrity > 0.3f ? 0xFFE6B83C : 0xFFE0473C;
		if (integrity <= 0.3f && !game.isOver() && (int) (now * 6) % 2 == 0) color = 0xFFFF8070;
		g.fill(barX - 1, barY - 1, barX + barW + 1, barY + barH + 1, 0xFF6B4F2A);
		g.fill(barX, barY, barX + barW, barY + barH, 0xFFC8B08A);
		g.fill(barX, barY, barX + fill, barY + barH, color);
		for (int x = barX + 4; x < barX + barW; x += 6) {
			g.fill(x, barY + 3, x + 3, barY + 4, 0x60FFFFFF); // little stitch marks
		}
		g.drawString(font, Math.round(integrity * 100) + "%", barX, barY + 10, CompendiumScreen.COL_TEXT_FADED, false);

		// The explosive waiting to go in.
		g.drawString(font, Component.translatable("screen.pokeblocks.surgery.filling"), barX, barY + 26,
				CompendiumScreen.COL_TEXT, false);
		g.renderFakeItem(explosive, barX, barY + 37);
		List<FormattedCharSequence> name = font.split(explosiveName, 56);
		if (!name.isEmpty()) {
			g.pose().pushPose();
			g.pose().translate(barX + 19, barY + 41, 0);
			g.pose().scale(0.75f, 0.75f, 1f);
			g.drawString(font, name.get(0), 0, 0, CompendiumScreen.COL_TEXT_FADED, false);
			g.pose().popPose();
		}
	}

	private void drawStepText(GuiGraphics g) {
		Component text;
		int color = CompendiumScreen.COL_TEXT;
		if (abandonArmedUntil > now && !game.isOver()) {
			text = Component.translatable("screen.pokeblocks.surgery.abandon_warning");
			color = 0xFFC02A2A;
		} else {
			text = switch (game.stage()) {
				case UNSTITCH -> Component.translatable("screen.pokeblocks.surgery.step.unstitch");
				case REMOVE_STUFFING -> Component.translatable("screen.pokeblocks.surgery.step.remove",
						game.fluffs().size() - game.countRemoved());
				case INSERT_EXPLOSIVE -> Component.translatable("screen.pokeblocks.surgery.step.insert", explosiveName);
				case RESTUFF -> Component.translatable("screen.pokeblocks.surgery.step.restuff",
						game.fluffs().size() - game.countRestuffed());
				case SEW -> Component.translatable("screen.pokeblocks.surgery.step.sew");
				case DONE -> Component.translatable("screen.pokeblocks.surgery.success");
				case RIPPED -> Component.translatable("screen.pokeblocks.surgery.ripped");
			};
		}
		int y = boardTop + (int) SurgeryGame.BOARD_H + 6;
		for (FormattedCharSequence line : font.split(text, PANEL_W - 24)) {
			if (y > panelTop + PANEL_H - 10) break;
			g.drawString(font, line, panelLeft + 12, y, color, false);
			y += 10;
		}
	}

	private void drawEndOverlay(GuiGraphics g) {
		if (endedAt < 0) return;
		float t = now - endedAt;
		int cx = boardLeft + (int) SurgeryGame.BOARD_W / 2;
		int cy = boardTop + (int) SurgeryGame.BOARD_H / 2;
		if (game.stage() == Stage.RIPPED) {
			if (t > RIP_SEQUENCE_SECONDS * 0.6f) {
				int alpha = (int) (Mth.clamp(1f - (t - RIP_SEQUENCE_SECONDS * 0.6f) * 2f, 0f, 1f) * 200);
				g.fill(boardLeft, boardTop, boardLeft + (int) SurgeryGame.BOARD_W, boardTop + (int) SurgeryGame.BOARD_H,
						(alpha << 24) | 0xFFF4D0);
				bigText(g, Component.translatable("screen.pokeblocks.surgery.kaboom"), cx, cy, 3f, 0xFFE0473C);
			} else {
				float wobble = (float) Math.sin(t * 40) * 2f;
				bigText(g, Component.translatable("screen.pokeblocks.surgery.rip"), cx + wobble, cy, 2.5f, 0xFFE0473C);
			}
		} else if (game.stage() == Stage.DONE) {
			float scale = 1.2f + Math.min(1f, t * 4f) * 0.3f; // fits the 220px board
			bigText(g, Component.translatable("screen.pokeblocks.surgery.success"), cx, cy - 20, scale, 0xFF3DAA4F);
		}
	}

	private void bigText(GuiGraphics g, Component text, float cx, float cy, float scale, int color) {
		g.pose().pushPose();
		g.pose().translate(cx, cy, 50);
		g.pose().scale(scale, scale, 1f);
		g.drawCenteredString(font, text, 0, -4, color);
		g.pose().popPose();
	}

	// --- Events, effects, sounds ---------------------------------------------------

	private void handleEvents() {
		for (Event event : game.drainEvents()) {
			float tx = boardLeft + game.tipX(), ty = boardTop + game.tipY();
			switch (event) {
				case SNIP -> {
					sound(SoundEvents.SHEEP_SHEAR, 1.4f + random.nextFloat() * 0.3f, 0.8f);
					burst(tx, ty, 4, COL_THREAD_LIGHT, 1.5f);
				}
				case POKE -> {
					sound(SoundRegistry.POKEDOLL_SQUEAK.get(), 1.7f, 0.7f);
					popup(Component.translatable("screen.pokeblocks.surgery.eep"), tx, ty);
				}
				case TOUCH -> {
					sound(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), 0.5f, 1.0f);
					sound(SoundRegistry.POKEDOLL_SQUEAK.get(), 1.3f + random.nextFloat() * 0.3f, 0.8f);
					popup(Component.translatable("screen.pokeblocks.surgery.ouch." + random.nextInt(4)), tx, ty);
				}
				case PICK_UP -> sound(SoundEvents.WOOL_HIT, 1.5f, 0.6f);
				case DROP -> sound(SoundEvents.WOOL_PLACE, 1.3f, 0.5f);
				case FLUFF_OUT -> {
					sound(SoundEvents.WOOL_BREAK, 1.3f, 0.7f);
					sound(SoundEvents.ITEM_PICKUP, 1.2f, 0.4f);
					burst(tx, ty, 6, COL_FLUFF, 1.2f);
				}
				case EXPLOSIVE_IN -> {
					sound(SoundEvents.NOTE_BLOCK_BASS.value(), 0.7f, 1.0f);
					popup(Component.translatable("screen.pokeblocks.surgery.phew"), tx, ty);
				}
				case FLUFF_IN -> sound(SoundEvents.WOOL_PLACE, 1.0f, 0.8f);
				case STITCH -> {
					sound(SoundEvents.NOTE_BLOCK_PLING.value(), 0.8f + game.sewn() * 0.15f, 0.7f);
					burst(tx, ty, 3, COL_THREAD_LIGHT, 1f);
				}
				case JOSTLE -> {
					sound(SoundEvents.TNT_PRIMED, 2.0f, 0.35f);
					popup(Component.translatable("screen.pokeblocks.surgery.easy"), tx, ty);
				}
				case STAGE_CLEAR -> sound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1.2f, 0.5f);
				case WARNING -> sound(SoundEvents.NOTE_BLOCK_BIT.value(), 0.6f, 1.0f);
				case RIPPED -> {
					endedAt = now;
					sound(SoundEvents.WOOL_BREAK, 0.6f, 1.0f);
					sound(SoundEvents.TNT_PRIMED, 1.0f, 1.0f);
					for (int i = 0; i < 70; i++) {
						burst(boardLeft + game.chamberX(), boardTop + game.chamberY(), 1, i % 4 == 0 ? COL_FABRIC : COL_FLUFF, 4f);
					}
				}
				case SUCCESS -> {
					endedAt = now;
					sound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.2f, 1.0f);
					sound(SoundEvents.NOTE_BLOCK_CHIME.value(), 1.5f, 0.8f);
					int[] confetti = {0xFFE0473C, 0xFF5BC062, 0xFF4A90E2, 0xFFFFD84A, 0xFFB86BE0};
					for (int i = 0; i < 60; i++) {
						burst(boardLeft + SurgeryGame.OPEN_X, boardTop + SurgeryGame.SEAM_Y, 1, confetti[i % confetti.length], 3.5f);
					}
				}
			}
		}
		// Nervous heartbeat once the stitches are close to giving way.
		if (!game.isOver() && game.integrity() <= 0.3f && now >= nextHeartbeat) {
			nextHeartbeat = now + 0.55f;
			sound(SoundEvents.NOTE_BLOCK_BASEDRUM.value(), 0.8f, 0.6f);
		}
	}

	private void tickSequences() {
		if (finished || endedAt < 0) return;
		float t = now - endedAt;
		if (game.stage() == Stage.RIPPED && t >= RIP_SEQUENCE_SECONDS) {
			finish(DollSurgery.OUTCOME_RIPPED);
		} else if (game.stage() == Stage.DONE && t >= SUCCESS_SEQUENCE_SECONDS) {
			finish(DollSurgery.OUTCOME_SUCCESS);
		}
	}

	private void sound(SoundEvent event, float pitch, float volume) {
		minecraft.getSoundManager().play(SimpleSoundInstance.forUI(event, pitch, volume));
	}

	private void burst(float x, float y, int count, int color, float speed) {
		for (int i = 0; i < count; i++) {
			float angle = random.nextFloat() * Mth.TWO_PI;
			float v = (0.4f + random.nextFloat()) * speed * 30f;
			particles.add(new Particle(x, y, Mth.cos(angle) * v, Mth.sin(angle) * v - 20f, 0.6f + random.nextFloat() * 0.6f,
					color, 1 + random.nextInt(2)));
		}
	}

	private void popup(Component text, float x, float y) {
		popups.add(new Popup(text, x + 6, y - 10, 0.9f));
	}

	private void drawParticles(GuiGraphics g, float dt) {
		for (Iterator<Particle> it = particles.iterator(); it.hasNext(); ) {
			Particle p = it.next();
			p.life -= dt;
			if (p.life <= 0) {
				it.remove();
				continue;
			}
			p.vy += 160f * dt;
			p.x += p.vx * dt;
			p.y += p.vy * dt;
			int x = Math.round(p.x), y = Math.round(p.y);
			g.fill(x, y, x + p.size + 1, y + p.size + 1, p.color);
		}
	}

	private void drawPopups(GuiGraphics g, float dt) {
		for (Iterator<Popup> it = popups.iterator(); it.hasNext(); ) {
			Popup p = it.next();
			p.life -= dt;
			if (p.life <= 0) {
				it.remove();
				continue;
			}
			p.y -= 18f * dt;
			int alpha = (int) (Mth.clamp(p.life / 0.4f, 0f, 1f) * 255) << 24;
			if ((alpha >>> 24) < 8) continue;
			g.drawString(font, p.text, Math.round(p.x), Math.round(p.y), alpha | 0xD02020, true);
		}
	}

	/** The OS cursor hides over the board so only the trembling tool tip shows. */
	private void updateCursor(double mx, double my) {
		boolean over = !game.isOver() && mx >= boardLeft && my >= boardTop
				&& mx < boardLeft + SurgeryGame.BOARD_W && my < boardTop + SurgeryGame.BOARD_H;
		if (over != cursorHidden) {
			cursorHidden = over;
			GLFW.glfwSetInputMode(minecraft.getWindow().getWindow(), GLFW.GLFW_CURSOR,
					over ? GLFW.GLFW_CURSOR_HIDDEN : GLFW.GLFW_CURSOR_NORMAL);
		}
	}

	// --- Input ---------------------------------------------------------------------

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && !finished) {
			game.press();
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0) {
			game.release();
			return true;
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
			onClose();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	/**
	 * ESC. Before the explosive is in, leaving is free (the doll gets patched up, nothing is used).
	 * Once it's inside, the first press warns and a second press within a moment abandons the
	 * operation — which rips the doll.
	 */
	@Override
	public void onClose() {
		if (finished) return;
		if (game.stage() == Stage.RIPPED) {
			finish(DollSurgery.OUTCOME_RIPPED);
		} else if (game.stage() == Stage.DONE) {
			finish(DollSurgery.OUTCOME_SUCCESS);
		} else if (game.explosiveInside()) {
			if (abandonArmedUntil > now) {
				game.abandon();
			} else {
				abandonArmedUntil = now + ABANDON_CONFIRM_SECONDS;
				sound(SoundEvents.NOTE_BLOCK_BIT.value(), 0.5f, 1.0f);
			}
		} else {
			finish(DollSurgery.OUTCOME_CANCEL);
		}
	}

	/** Reports the outcome exactly once and leaves: back to the crafting table, or (after a rip) out entirely. */
	private void finish(int outcome) {
		if (finished) return;
		finished = true;
		DollSurgery.sendResult(token, outcome);
		var player = minecraft.player;
		if (player == null) {
			minecraft.setScreen(null);
			return;
		}
		boolean menuStillOpen = previous instanceof AbstractContainerScreen<?> containerScreen
				&& containerScreen.getMenu() == player.containerMenu;
		if (outcome != DollSurgery.OUTCOME_RIPPED && menuStillOpen) {
			minecraft.setScreen(previous);
		} else if (player.containerMenu != player.inventoryMenu) {
			player.closeContainer(); // sent after the result, so the server handles the rip first
		} else {
			minecraft.setScreen(null);
		}
	}

	@Override
	public void removed() {
		if (cursorHidden) {
			cursorHidden = false;
			GLFW.glfwSetInputMode(minecraft.getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
		}
		// Closed from outside (table broken, server closed the menu, disconnect): nothing happened.
		if (!finished) {
			finished = true;
			DollSurgery.sendResult(token, DollSurgery.OUTCOME_CANCEL);
		}
		super.removed();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	// --- Test hooks (DollSurgeryHarness) ------------------------------------------------

	public SurgeryGame game() {
		return game;
	}

	/** Steers the pointer toward {@code target} (board units, re-read every frame) at a steady speed. */
	public void autopilot(Supplier<float[]> target) {
		autopilotTarget = target;
	}

	/** True once the autopilot pointer sits on its current target. */
	public boolean autopilotArrived() {
		if (autopilotTarget == null || Float.isNaN(autoX)) return false;
		float[] target = autopilotTarget.get();
		return Math.abs(target[0] - autoX) < 0.01f && Math.abs(target[1] - autoY) < 0.01f;
	}

	// --- Drawing helpers -----------------------------------------------------------

	private static void fillCircle(GuiGraphics g, float cx, float cy, float r, int color) {
		int top = Math.round(cy - r), bottom = Math.round(cy + r);
		for (int y = top; y <= bottom; y++) {
			float dy = y + 0.5f - cy;
			float half = r * r - dy * dy;
			if (half <= 0) continue;
			float w = (float) Math.sqrt(half);
			g.fill(Math.round(cx - w), y, Math.round(cx + w), y + 1, color);
		}
	}

	private static void drawLine(GuiGraphics g, float x0, float y0, float x1, float y1, int color, int thickness) {
		float dx = x1 - x0, dy = y1 - y0;
		int steps = Math.max(1, (int) Math.ceil(Math.max(Math.abs(dx), Math.abs(dy))));
		int half = thickness / 2;
		for (int i = 0; i <= steps; i++) {
			int x = Math.round(x0 + dx * i / steps) - half, y = Math.round(y0 + dy * i / steps) - half;
			g.fill(x, y, x + thickness, y + thickness, color);
		}
	}

	private static final class Particle {
		float x, y, vx, vy, life;
		final int color, size;

		Particle(float x, float y, float vx, float vy, float life, int color, int size) {
			this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.life = life; this.color = color; this.size = size;
		}
	}

	private static final class Popup {
		final Component text;
		float x, y, life;

		Popup(Component text, float x, float y, float life) {
			this.text = text; this.x = x; this.y = y; this.life = life;
		}
	}
}
