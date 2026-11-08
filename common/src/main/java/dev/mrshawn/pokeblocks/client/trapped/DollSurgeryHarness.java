package dev.mrshawn.pokeblocks.client.trapped;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.client.screen.DollSurgeryScreen;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.TrappedDolls;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame.Fluff;
import dev.mrshawn.pokeblocks.trapped.surgery.SurgeryGame.Stage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * In-game regression check for the doll-surgery minigame (trapped-doll crafting), covering what the
 * {@code SurgeryGameTest} unit tests can't: the real crafting-result click being intercepted on the
 * server, the start payload opening the screen, the result payload, returning to the crafting table,
 * and what the server does with each outcome.
 * <p>
 * Completely dormant unless {@code pokeblocks-surgery-check.json} exists in the game directory
 * ({@code tools/surgery_check.py} writes it and launches a dev Fabric client into a throwaway world).
 * It then, in survival: (1) clicks a doll + TNT result and cancels with ESC — nothing used;
 * (2) plays the whole operation by autopilot — trapped doll in the inventory, grid consumed;
 * (3) rips it on purpose — doll and TNT gone, a real block-breaking TNT blast at the player (who may
 * die), no chat line; and (4) checks the
 * Crafter can't trap dolls. Every check logs {@code [Surgery check] PASS|FAIL ...}, then
 * {@code RESULT PASS|FAIL}; screenshots are named {@code surgery_*.png}.
 */
public final class DollSurgeryHarness {

	private static final String CONFIG_FILE = "pokeblocks-surgery-check.json";

	private static boolean checked;
	private static JsonObject config;
	private static final Deque<Step> STEPS = new ArrayDeque<>();
	private static Step current;
	private static int passes;
	private static int failures;
	private static BlockPos tablePos;
	private static BlockPos standPos;
	private static int exitTicks = -1;
	private static float finalIntegrity = -1f;

	private interface Step {
		boolean tick(Minecraft mc, IntegratedServer server);
	}

	private DollSurgeryHarness() {}

	public static void tick(Minecraft mc) {
		if (!checked) {
			checked = true;
			load(mc);
		}
		if (config == null || mc.level == null || mc.player == null) return;
		IntegratedServer server = mc.getSingleplayerServer();
		if (server == null) return;
		if (mc.screen != null && mc.screen.isPauseScreen()) mc.setScreen(null);
		if (mc.isPaused()) return;
		if (exitTicks >= 0) {
			if (++exitTicks > 40 && config.has("exit_when_done") && config.get("exit_when_done").getAsBoolean()) mc.stop();
			return;
		}
		if (current == null) {
			current = STEPS.poll();
			if (current == null) {
				log("RESULT " + (failures == 0 ? "PASS" : "FAIL") + " (" + passes + " passed, " + failures + " failed)");
				exitTicks = 0;
				return;
			}
		}
		if (current.tick(mc, server)) current = null;
	}

	private static void load(Minecraft mc) {
		Path file = mc.gameDirectory.toPath().resolve(CONFIG_FILE);
		if (!Files.isRegularFile(file)) return;
		try {
			config = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("[Surgery check] bad {}", CONFIG_FILE, e);
			return;
		}
		mc.options.pauseOnLostFocus = false;
		script();
		log("harness armed: " + STEPS.size() + " steps");
	}

	// --- The script ------------------------------------------------------------------------------

	private static void script() {
		waitTicks(100);
		onServer("set up", DollSurgeryHarness::setUp);
		waitTicks(40);

		// 1. Cancel before the explosive goes in: nothing is used, back at the table.
		onServer("open the crafting table with a doll + TNT", player -> {
			openCrafting(player);
			fillGrid(player);
		});
		waitTicks(10);
		check("the grid shows a trapped doll", player -> TrappedDolls.isTrapped(resultOf(player)));
		clickResult();
		awaitClient("the surgery screen opens on the result click", 60, mc -> mc.screen instanceof DollSurgeryScreen);
		waitTicks(10);
		screenshot("open");
		check("the click did not hand out the doll", player -> !hasTrappedDoll(player) && player.containerMenu.getCarried().isEmpty());
		check("the grid still holds the doll and TNT", DollSurgeryHarness::gridFull);
		onClient("press ESC", mc -> mc.screen.onClose());
		awaitClient("ESC goes back to the crafting table", 40, mc -> mc.screen instanceof CraftingScreen);
		waitTicks(10);
		check("cancelling used nothing", player -> gridFull(player) && !hasTrappedDoll(player));
		checkClient("the client grid is resynced", () -> {
			var menu = Minecraft.getInstance().player.containerMenu;
			return menu instanceof CraftingMenu && TrappedDolls.isTrapped(menu.getSlot(0).getItem())
					&& menu.getSlot(1).hasItem() && menu.getSlot(2).hasItem();
		});

		// 2. A careful operation, played by autopilot.
		clickResult();
		awaitClient("the surgery screen opens again", 60, mc -> mc.screen instanceof DollSurgeryScreen);
		playUnstitch();
		screenshot("seam_open");
		playRemoveStuffing();
		playInsertExplosive();
		playRestuff();
		playSew();
		awaitClient("the operation succeeds", 200, mc -> {
			if (mc.screen instanceof DollSurgeryScreen surgery && surgery.game().stage() == Stage.DONE) {
				finalIntegrity = surgery.game().integrity();
				return true;
			}
			return false;
		});
		waitTicks(8);
		screenshot("success");
		checkClient("a careful hand never touched the sides", () -> finalIntegrity == 1f);
		awaitClient("success goes back to the crafting table", 80, mc -> mc.screen instanceof CraftingScreen);
		waitTicks(10);
		check("the trapped doll is in the inventory", DollSurgeryHarness::hasTrappedDoll);
		check("the doll and TNT left the grid", player -> resultOf(player).isEmpty()
				&& player.containerMenu.getSlot(1).getItem().isEmpty() && player.containerMenu.getSlot(2).getItem().isEmpty());
		screenshot("back_at_table");

		// 3. A rip: tweezers held against the fabric until the stitches give.
		onServer("refill the grid, full health", player -> {
			fillGrid(player);
			player.setHealth(player.getMaxHealth());
		});
		waitTicks(10);
		clickResult();
		awaitClient("the surgery screen opens a third time", 60, mc -> mc.screen instanceof DollSurgeryScreen);
		playUnstitch();
		glide(game -> new float[]{SurgeryGame.FABRIC_X0 + 3f, SurgeryGame.FABRIC_Y1 - 3f});
		onClient("press into the fabric", mc -> mc.screen.mouseClicked(0, 0, 0));
		waitTicks(10);
		screenshot("scrape");
		awaitClient("the stitches rip", 200, mc -> mc.screen instanceof DollSurgeryScreen s && s.game().stage() == Stage.RIPPED);
		waitTicks(6);
		screenshot("rip");
		// A real TNT at your feet can kill you: the death screen counts as "closed" too.
		awaitClient("the rip closes the table", 80, mc -> !(mc.screen instanceof DollSurgeryScreen) && !(mc.screen instanceof CraftingScreen));
		waitTicks(20);
		check("the ripped doll and its TNT are gone", player -> countPlainDolls(player) == 0
				&& !player.getInventory().contains(new ItemStack(Items.TNT))
				&& player.serverLevel().getEntitiesOfClass(ItemEntity.class, new AABB(tablePos).inflate(12), item ->
						item.getItem().is(Items.TNT) || (item.getItem().is(ItemRegistry.POKEDOLL_ITEM.get())
								&& !TrappedDolls.isTrapped(item.getItem()))).isEmpty());
		check("the blast hurt the player", player -> player.isDeadOrDying() || player.getHealth() < player.getMaxHealth());
		check("the blast broke blocks where the player stood", player -> player.level().getBlockState(standPos.below()).isAir());
		checkClient("no chat/action-bar line about the rip", () -> !clientShowsSurgeryMessage(Minecraft.getInstance()));
		onClient("respawn if the blast was fatal", mc -> {
			if (mc.player.isDeadOrDying()) {
				mc.player.respawn();
				mc.setScreen(null);
			}
		});
		waitTicks(20);

		// 4. Automation can't do surgery.
		check("the Crafter finds no recipe for doll + TNT", player -> CrafterBlock.getPotentialResults(player.level(),
				CraftingInput.of(2, 1, List.of(PokedollItem.createPokedoll(ModSettings.DEFAULT_POKEMON), new ItemStack(Items.TNT)))).isEmpty());
	}

	// --- Playing the game by autopilot ------------------------------------------------------------

	private static void playUnstitch() {
		for (int i = 0; i < SurgeryGame.SEAM_HOLES - 1; i++) {
			int stitch = i;
			glide(game -> new float[]{game.stitchX(stitch), game.stitchY(stitch)});
			click();
		}
		awaitGame("all stitches snipped", 20, game -> game.stage() == Stage.REMOVE_STUFFING);
	}

	private static void playRemoveStuffing() {
		for (int f = 0; f < 4; f++) {
			int index = f;
			glide(game -> new float[]{game.fluffs().get(index).x, game.fluffs().get(index).y});
			press();
			glide(game -> new float[]{game.chamberX(), game.chamberY()});
			if (f == 0) screenshot("carrying_stuffing");
			for (int p = 2; p >= 0; p--) {
				int point = p;
				glide(game -> new float[]{game.neckX(point), game.neckY(point)});
			}
			glide(game -> new float[]{game.neckX(0), -4f});
			release();
		}
		awaitGame("all stuffing out", 20, game -> game.stage() == Stage.INSERT_EXPLOSIVE);
	}

	private static void playInsertExplosive() {
		glide(game -> new float[]{game.explosiveX(), game.explosiveY()});
		press();
		glide(game -> new float[]{SurgeryGame.EXPLOSIVE_HOME_X, 2f});
		glide(game -> new float[]{game.neckX(0), 2f});
		glide(game -> new float[]{game.neckX(1), game.neckY(1)});
		screenshot("carrying_explosive");
		glide(game -> new float[]{game.neckX(2), game.neckY(2)});
		glide(game -> game.explosiveInside() ? new float[]{game.pointerX(), game.pointerY()} : new float[]{game.chamberX(), game.chamberY()});
		release();
		awaitGame("the explosive is in", 20, game -> game.stage() == Stage.RESTUFF);
	}

	private static void playRestuff() {
		for (int f = 0; f < 4; f++) {
			int index = f;
			float[] start = new float[2];
			STEPS.add((mc, server) -> {
				if (mc.screen instanceof DollSurgeryScreen s) {
					start[0] = s.game().fluffs().get(index).x;
					start[1] = s.game().fluffs().get(index).y;
				}
				return true;
			});
			glide(game -> start);
			press();
			glide(game -> new float[]{start[0], 2f});
			glide(game -> new float[]{game.neckX(0), 2f});
			for (int p = 0; p < 4; p++) {
				int point = p;
				glide(game -> game.fluffs().get(index).restuffed ? new float[]{game.pointerX(), game.pointerY()}
						: new float[]{game.neckX(point), game.neckY(point)});
			}
			release();
		}
		awaitGame("all stuffing back in", 20, game -> game.stage() == Stage.SEW);
	}

	private static void playSew() {
		glide(game -> new float[]{SurgeryGame.holeX(0), SurgeryGame.holeY(0)});
		press();
		for (int i = 1; i < SurgeryGame.SEAM_HOLES; i++) {
			int hole = i;
			glide(game -> game.isOver() ? new float[]{game.pointerX(), game.pointerY()}
					: new float[]{SurgeryGame.holeX(hole), SurgeryGame.holeY(hole)});
			if (i == 3) screenshot("sewing");
		}
		release();
	}

	/** Steers the surgery pointer to a moving target and waits until it's there. */
	private static void glide(java.util.function.Function<SurgeryGame, float[]> target) {
		boolean[] started = {false};
		int[] ticks = {0};
		STEPS.add((mc, server) -> {
			if (!(mc.screen instanceof DollSurgeryScreen surgery)) {
				fail("glide: the surgery screen is gone");
				return true;
			}
			if (!started[0]) {
				started[0] = true;
				surgery.autopilot(() -> target.apply(surgery.game()));
			}
			if (++ticks[0] > 400) {
				fail("glide: never arrived");
				return true;
			}
			return surgery.autopilotArrived() || surgery.game().isOver();
		});
		waitTicks(1);
	}

	private static void press() {
		onClient("press", mc -> mc.screen.mouseClicked(0, 0, 0));
		waitTicks(1);
	}

	private static void release() {
		onClient("release", mc -> mc.screen.mouseReleased(0, 0, 0));
		waitTicks(1);
	}

	private static void click() {
		press();
		release();
	}

	private static void awaitGame(String what, int ticks, Predicate<SurgeryGame> condition) {
		awaitClient(what, ticks, mc -> mc.screen instanceof DollSurgeryScreen s && condition.test(s.game()));
	}

	/** The real click path: the client's container click packet on the result slot. */
	private static void clickResult() {
		onClient("click the crafting result", mc ->
				mc.gameMode.handleInventoryMouseClick(mc.player.containerMenu.containerId, 0, 0, ClickType.PICKUP, mc.player));
	}

	// --- Steps -----------------------------------------------------------------------------------

	private static void waitTicks(int ticks) {
		int[] left = {ticks};
		STEPS.add((mc, server) -> --left[0] <= 0);
	}

	private static void onClient(String what, Consumer<Minecraft> action) {
		STEPS.add((mc, server) -> {
			action.accept(mc);
			return true;
		});
	}

	private static void awaitClient(String what, int ticks, Predicate<Minecraft> condition) {
		int[] waited = {0};
		STEPS.add((mc, server) -> {
			if (condition.test(mc)) {
				report(what, true);
				return true;
			}
			if (++waited[0] >= ticks) {
				report(what + " (screen=" + (mc.screen == null ? "none" : mc.screen.getClass().getSimpleName()) + ")", false);
				return true;
			}
			return false;
		});
	}

	private static void onServer(String what, Consumer<ServerPlayer> action) {
		AtomicReference<CompletableFuture<Void>> pending = new AtomicReference<>();
		STEPS.add((mc, server) -> {
			if (pending.get() == null) {
				pending.set(server.submit(() -> {
					ServerPlayer player = server.getPlayerList().getPlayer(mc.player.getUUID());
					if (player != null) action.accept(player);
				}));
			}
			CompletableFuture<Void> future = pending.get();
			if (!future.isDone()) return false;
			if (future.isCompletedExceptionally()) fail(what + " threw " + future.handle((v, e) -> e).join());
			return true;
		});
	}

	private static void check(String what, Predicate<ServerPlayer> condition) {
		AtomicReference<CompletableFuture<Boolean>> pending = new AtomicReference<>();
		STEPS.add((mc, server) -> {
			if (pending.get() == null) {
				pending.set(server.submit(() -> {
					ServerPlayer player = server.getPlayerList().getPlayer(mc.player.getUUID());
					return player != null && condition.test(player);
				}));
			}
			CompletableFuture<Boolean> future = pending.get();
			if (!future.isDone()) return false;
			report(what, !future.isCompletedExceptionally() && future.join());
			return true;
		});
	}

	private static void checkClient(String what, BooleanSupplier condition) {
		STEPS.add((mc, server) -> {
			report(what, condition.getAsBoolean());
			return true;
		});
	}

	private static void screenshot(String name) {
		STEPS.add((mc, server) -> {
			String file = "surgery_" + name + ".png";
			Screenshot.grab(mc.gameDirectory, file, mc.getMainRenderTarget(), message -> {});
			log("shot " + file);
			return true;
		});
	}

	// --- Server-side helpers ---------------------------------------------------------------------

	private static void setUp(ServerPlayer player) {
		ServerLevel level = player.serverLevel();
		player.setGameMode(GameType.SURVIVAL);
		player.getInventory().clearContent();
		player.setHealth(player.getMaxHealth());
		// Explosions (like vanilla TNT) do no damage to players on Peaceful; the test world may be.
		player.server.setDifficulty(net.minecraft.world.Difficulty.NORMAL, true);
		level.setDayTime(6000L);
		level.setWeatherParameters(12000, 0, false, false);
		level.getGameRules().getRule(net.minecraft.world.level.GameRules.RULE_DAYLIGHT).set(false, player.server);

		int offset = config.has("area_offset") ? config.get("area_offset").getAsInt() : 300;
		BlockPos at = player.blockPosition().offset(offset, 0, 0);
		level.getChunk(at);
		int ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, at.getX(), at.getZ());
		tablePos = new BlockPos(at.getX(), ground, at.getZ());
		level.setBlock(tablePos.below(), Blocks.STONE.defaultBlockState(), 3);
		level.setBlock(tablePos, Blocks.CRAFTING_TABLE.defaultBlockState(), 3);
		standPos = tablePos.south(2);
		player.teleportTo(level, standPos.getX() + 0.5, standPos.getY(), standPos.getZ() + 0.5, Set.of(), 180f, 35f);
		log("table at " + tablePos);
	}

	private static void openCrafting(ServerPlayer player) {
		player.openMenu(new SimpleMenuProvider((id, inventory, p) ->
				new CraftingMenu(id, inventory, ContainerLevelAccess.create(player.level(), tablePos)),
				Component.translatable("container.crafting")));
	}

	private static void fillGrid(ServerPlayer player) {
		if (!(player.containerMenu instanceof CraftingMenu menu)) return;
		menu.getSlot(1).set(PokedollItem.createPokedoll(ModSettings.DEFAULT_POKEMON));
		menu.getSlot(2).set(new ItemStack(Items.TNT));
	}

	private static boolean gridFull(ServerPlayer player) {
		return player.containerMenu instanceof CraftingMenu menu
				&& menu.getSlot(1).getItem().is(ItemRegistry.POKEDOLL_ITEM.get()) && menu.getSlot(2).getItem().is(Items.TNT);
	}

	private static ItemStack resultOf(ServerPlayer player) {
		return player.containerMenu instanceof CraftingMenu menu ? menu.getSlot(0).getItem() : ItemStack.EMPTY;
	}

	private static boolean hasTrappedDoll(ServerPlayer player) {
		for (ItemStack stack : player.getInventory().items) {
			if (stack.is(ItemRegistry.POKEDOLL_ITEM.get()) && TrappedDolls.isTrapped(stack)) return true;
		}
		return false;
	}

	private static int countPlainDolls(ServerPlayer player) {
		int count = 0;
		for (ItemStack stack : player.getInventory().items) {
			if (stack.is(ItemRegistry.POKEDOLL_ITEM.get()) && !TrappedDolls.isTrapped(stack)) count += stack.getCount();
		}
		return count;
	}

	/**
	 * True when the action bar or chat currently holds a {@code message.pokeblocks.surgery.*} line
	 * other than the success one. Reads the (private) Gui/ChatComponent fields by type — dev client only.
	 */
	private static boolean clientShowsSurgeryMessage(Minecraft mc) {
		List<Object> shown = new java.util.ArrayList<>();
		try {
			for (java.lang.reflect.Field field : net.minecraft.client.gui.Gui.class.getDeclaredFields()) {
				if (Component.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					shown.add(field.get(mc.gui));
				}
			}
			for (java.lang.reflect.Field field : net.minecraft.client.gui.components.ChatComponent.class.getDeclaredFields()) {
				if (List.class.isAssignableFrom(field.getType())) {
					field.setAccessible(true);
					for (Object line : (List<?>) field.get(mc.gui.getChat())) {
						if (line instanceof net.minecraft.client.GuiMessage message) shown.add(message.content());
					}
				}
			}
		} catch (ReflectiveOperationException e) {
			fail("could not read the HUD: " + e);
			return false;
		}
		for (Object value : shown) {
			if (value instanceof Component component && mentionsRipKey(component)) return true;
		}
		return false;
	}

	private static boolean mentionsRipKey(Component component) {
		if (component.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents translatable
				&& translatable.getKey().startsWith("message.pokeblocks.surgery.")
				&& !translatable.getKey().equals("message.pokeblocks.surgery.success")) {
			return true;
		}
		for (Component sibling : component.getSiblings()) {
			if (mentionsRipKey(sibling)) return true;
		}
		return false;
	}

	private static void report(String what, boolean ok) {
		if (ok) {
			passes++;
			log("PASS " + what);
		} else {
			fail(what);
		}
	}

	private static void fail(String what) {
		failures++;
		log("FAIL " + what);
	}

	private static void log(String message) {
		PokeblocksLog.LOGGER.info("[Surgery check] {}", message);
	}
}
