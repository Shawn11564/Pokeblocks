package dev.mrshawn.pokeblocks.phone;

import dev.mrshawn.pokeblocks.block.custom.DigSiteBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.DigSiteBlockEntity;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.registry.BlockRegistry;
import dev.mrshawn.pokeblocks.registry.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The dig-site quest an accepted phone call starts: places the site mounds, resolves each dig into
 * junk or the buried doll, and guarantees the doll within the configured attempt cap (the winning
 * dig index is rolled up-front and stored on the quest, so WHICH sites the player picks never
 * matters). Sites only ever occupy an AIR block above diggable ground — existing blocks are never
 * replaced — and completing/failing the quest removes every leftover mound.
 */
public final class DigQuestManager {

	private DigQuestManager() {}

	/** Ground the phone considers "diggable": the classic soil blocks a doll could be buried in. */
	private static final Set<Block> DIGGABLE_GROUND = Set.of(
			Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.COARSE_DIRT,
			Blocks.PODZOL, Blocks.MYCELIUM, Blocks.ROOTED_DIRT);

	/** The junk a dud site coughs up instead of the doll. */
	private static final List<Item> JUNK = List.of(
			Items.BONE, Items.STRING, Items.FLINT, Items.CLAY_BALL,
			Items.ROTTEN_FLESH, Items.STICK, Items.POISONOUS_POTATO, Items.LEATHER);

	/** Minimum spacing between two sites (blocks). */
	private static final double MIN_SITE_SPACING_SQR = 5 * 5;

	/**
	 * Placement attempts per REQUESTED site (with a generous floor). Scaled rather than fixed so a
	 * high-rarity phone's bigger site count (dig_sites + tier × dig_sites_per_rarity) doesn't silently
	 * under-place on rough terrain — the whole point of the rarity scaling is more sites.
	 */
	private static final int PLACEMENT_ATTEMPTS_PER_SITE = 40;
	private static final int MIN_PLACEMENT_ATTEMPTS = 250;

	// True while the manager itself is placing/removing site blocks, so the block's onRemove
	// hook only reacts to EXTERNAL removals (/setblock, creative pistons, support loss, ...).
	private static boolean internalMutation = false;

	// ------------------------------------------------------------------
	// Quest lifecycle
	// ------------------------------------------------------------------

	public static boolean hasQuest(ServerPlayer player) {
		return getQuest(player) != null;
	}

	/** The player's active quest, or null. */
	public static DigQuestStore.Quest getQuest(ServerPlayer player) {
		MinecraftServer server = player.getServer();
		return server == null ? null : DigQuestStore.get(server).get(player.getUUID());
	}

	/**
	 * Starts the dig quest for an accepted call: finds up to the configured number of valid spots,
	 * buries a rarity-weighted doll (drawn to MATCH the caller's announced lost-doll descriptor) at a
	 * pre-rolled attempt index, places the mounds and syncs the site markers. False when no valid ground
	 * exists nearby (the caller apologises instead).
	 * <p>
	 * Both the dig-site count and the guarantee scale with the phone's rarity: each tier of the caller
	 * (its attuned doll) adds {@code dig_sites_per_rarity} sites and {@code guaranteed_attempts_per_rarity}
	 * guaranteed attempts, so a rarer phone runs a bigger hunt.
	 *
	 * @param lost the descriptor announced when the phone rang; a defensive null re-rolls one from the caller.
	 */
	public static boolean startQuest(ServerPlayer player, String callerKey, PhoneCalls.LostDollTarget lost) {
		MinecraftServer server = player.getServer();
		if (server == null) return false;
		ServerLevel level = player.serverLevel();
		RandomSource random = level.getRandom();

		// The phone's rarity = its attuned doll = the caller. Each tier above Common adds the configured
		// per-rarity bonus to both the site count and the guarantee.
		PhoneCalls.Variant callerVariant = PhoneCalls.parseVariantKey(callerKey);
		DollRarity callerRarity = RarityScoreCalculator.resolvedRarity(callerVariant.species(), callerVariant.flags());
		int tierSteps = PhoneCalls.rarityTierBonusSteps(callerRarity);
		int digSites = PokeblocksConfig.getPhoneDigSites() + tierSteps * PokeblocksConfig.getPhoneDigSitesPerRarity();
		int guaranteedAttempts = PokeblocksConfig.getPhoneGuaranteedAttempts() + tierSteps * PokeblocksConfig.getPhoneGuaranteedAttemptsPerRarity();

		List<BlockPos> sites = generateSites(level, player.blockPosition(),
				digSites, PokeblocksConfig.getPhoneSiteRadius(), random);
		if (sites.isEmpty()) return false;

		// Bury a doll that MATCHES what the caller announced they lost (its rarity tier or percent
		// window). A null descriptor (a call predating this data) is re-rolled from the caller.
		PhoneCalls.LostDollTarget target = lost != null ? lost : PhoneCalls.pickLostDoll(callerKey, random);
		String buriedKey = PhoneCalls.pickBuriedDollKey(random, target);
		if (buriedKey == null) return false;

		int cap = Math.min(guaranteedAttempts, sites.size());
		int targetAttempt = 1 + random.nextInt(Math.max(1, cap));

		internalMutation = true;
		try {
			for (BlockPos pos : sites) {
				level.setBlockAndUpdate(pos, BlockRegistry.DIG_SITE_BLOCK.get().defaultBlockState());
			}
		} finally {
			internalMutation = false;
		}

		DigQuestStore.get(server).put(player.getUUID(), new DigQuestStore.Quest(
				dimensionId(level), callerKey, buriedKey, targetAttempt, 0, sites));
		syncTo(player);

		level.playSound(null, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.7f, 1.3f);
		player.displayClientMessage(Component.translatable("message.pokeblocks.phone.quest_started",
				PhoneCalls.dollName(callerKey), PhoneCalls.describeLostDoll(target), sites.size()), false);
		return true;
	}

	/**
	 * One right-click on a site: advances the dig stage with soil-scatter feedback, and on the
	 * final scoop resolves the site into the buried doll or junk. Doll found → every other site
	 * vanishes and the quest completes.
	 */
	public static InteractionResult tryDig(ServerPlayer player, ServerLevel level, BlockPos pos, BlockState state) {
		MinecraftServer server = level.getServer();
		DigQuestStore store = DigQuestStore.get(server);

		Map.Entry<UUID, DigQuestStore.Quest> entry = store.findQuestAt(dimensionId(level), pos);
		if (entry == null) {
			// Orphaned mound (its quest is gone) — clean it up quietly.
			removeSiteBlock(level, pos);
			return InteractionResult.CONSUME;
		}
		if (!entry.getKey().equals(player.getUUID())) {
			player.displayClientMessage(Component.translatable("message.pokeblocks.phone.not_your_site"), true);
			return InteractionResult.CONSUME;
		}

		int stage = state.getValue(DigSiteBlock.STAGE);
		digEffects(level, pos);
		if (stage < DigSiteBlock.MAX_STAGE) {
			// Reveal what this site would yield so the item slowly surfaces from the mound as it's
			// scooped, like brushing a vanilla suspicious-sand block. This is a live preview — the
			// authoritative resolve below runs the same rule, so it always matches what pops out.
			setRevealItem(level, pos, previewReward(entry.getValue(), pos));
			level.setBlock(pos, state.setValue(DigSiteBlock.STAGE, stage + 1), Block.UPDATE_ALL);
			// Left alone, the scooped mound slowly refills itself (DigSiteBlock#tick) — record the
			// scoop so the countdown restarts, and start the tick chain if one isn't already running.
			if (level.getBlockEntity(pos) instanceof DigSiteBlockEntity be) {
				be.markDug(level.getGameTime());
			}
			if (!level.getBlockTicks().hasScheduledTick(pos, state.getBlock())) {
				level.scheduleTick(pos, state.getBlock(), DigSiteBlock.COVER_GRACE_TICKS);
			}
			return InteractionResult.CONSUME;
		}

		// Final scoop — the site resolves into the very item it had been revealing.
		DigQuestStore.Quest quest = entry.getValue();
		int attempts = quest.attempts() + 1;
		List<BlockPos> remaining = new ArrayList<>(quest.sites());
		remaining.remove(pos);
		boolean foundDoll = DigQuestRules.shouldFindDoll(attempts, quest.targetAttempt(), remaining.size());
		removeSiteBlock(level, pos);

		if (foundDoll) {
			completeQuest(player, level, pos, quest, remaining);
		} else {
			spawnReward(level, pos, new ItemStack(JUNK.get(junkIndex(pos))));
			store.put(player.getUUID(), quest.withAttemptsAndSites(attempts, remaining));
			syncTo(player);
			player.displayClientMessage(Component.translatable("message.pokeblocks.phone.dug_junk", remaining.size()), true);
		}
		return InteractionResult.CONSUME;
	}

	private static void completeQuest(ServerPlayer player, ServerLevel level, BlockPos dugPos,
									  DigQuestStore.Quest quest, List<BlockPos> remaining) {
		ItemStack doll = PhoneCalls.createDoll(quest.buriedKey());
		spawnReward(level, dugPos, doll);

		// The other sites vanish the moment the doll is found.
		internalMutation = true;
		try {
			for (BlockPos other : remaining) {
				if (level.getBlockState(other).is(BlockRegistry.DIG_SITE_BLOCK.get())) {
					level.removeBlock(other, false);
				}
			}
		} finally {
			internalMutation = false;
		}

		DigQuestStore.get(level.getServer()).remove(player.getUUID());
		syncTo(player);

		level.playSound(null, dugPos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.7f, 1.3f);
		level.playSound(null, dugPos, SoundRegistry.POKEDOLL_SQUEAK.get(), SoundSource.PLAYERS, 0.9f, 1.0f);
		level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
				dugPos.getX() + 0.5, dugPos.getY() + 0.6, dugPos.getZ() + 0.5, 16, 0.5, 0.4, 0.5, 0.1);
		player.displayClientMessage(Component.translatable("message.pokeblocks.phone.dug_doll",
				PhoneCalls.dollName(quest.buriedKey()), PhoneCalls.dollName(quest.callerKey())), false);
	}

	/**
	 * Reacts to a site block disappearing OUTSIDE the dig flow (support broken, /setblock, ...):
	 * the site silently leaves the quest, and losing the last one fails the quest. No-op for the
	 * manager's own mutations.
	 */
	public static void onSiteBlockRemoved(ServerLevel level, BlockPos pos) {
		if (internalMutation) return;
		MinecraftServer server = level.getServer();
		DigQuestStore store = DigQuestStore.get(server);
		Map.Entry<UUID, DigQuestStore.Quest> entry = store.findQuestAt(dimensionId(level), pos);
		if (entry == null) return;

		DigQuestStore.Quest quest = entry.getValue();
		List<BlockPos> remaining = new ArrayList<>(quest.sites());
		remaining.remove(pos);
		ServerPlayer owner = server.getPlayerList().getPlayer(entry.getKey());

		if (remaining.isEmpty()) {
			store.remove(entry.getKey());
			if (owner != null) {
				owner.displayClientMessage(Component.translatable("message.pokeblocks.phone.quest_failed"), false);
			}
		} else {
			store.put(entry.getKey(), quest.withAttemptsAndSites(quest.attempts(), remaining));
		}
		if (owner != null) {
			syncTo(owner);
		}
	}

	/** Sends the player's current sites (or an empty clear) — join sync and every quest change. */
	public static void syncTo(ServerPlayer player) {
		if (!PhoneCalls.canSendTo(player)) return;
		MinecraftServer server = player.getServer();
		if (server == null) return;
		DigQuestStore.Quest quest = DigQuestStore.get(server).get(player.getUUID());
		byte[] data = quest == null
				? PhonePayloadCodec.encodeSites("", List.of())
				: PhonePayloadCodec.encodeSites(quest.dimension(), quest.sites());
		PhoneCalls.sendDigSites(player, data);
	}

	// ------------------------------------------------------------------
	// Site placement
	// ------------------------------------------------------------------

	/**
	 * Picks up to {@code count} spots on a ring around the player: surface position from the
	 * heightmap, ground below must be a diggable soil block, the mound cell itself must be AIR
	 * (never replacing an existing block), spaced apart from each other.
	 */
	private static List<BlockPos> generateSites(ServerLevel level, BlockPos center, int count, int radius,
												RandomSource random) {
		List<BlockPos> chosen = new ArrayList<>();
		int minRadius = Math.min(8, Math.max(1, radius - 1));
		int maxAttempts = Math.max(MIN_PLACEMENT_ATTEMPTS, count * PLACEMENT_ATTEMPTS_PER_SITE);

		for (int attempt = 0; attempt < maxAttempts && chosen.size() < count; attempt++) {
			double angle = random.nextDouble() * Math.PI * 2;
			double dist = minRadius + random.nextDouble() * Math.max(1, radius - minRadius);
			int x = center.getX() + (int) Math.round(Math.cos(angle) * dist);
			int z = center.getZ() + (int) Math.round(Math.sin(angle) * dist);
			int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
			BlockPos mound = new BlockPos(x, surfaceY, z);

			if (!level.isLoaded(mound)) continue;
			if (!level.getWorldBorder().isWithinBounds(mound)) continue;
			if (!level.getBlockState(mound).isAir()) continue;
			if (!DIGGABLE_GROUND.contains(level.getBlockState(mound.below()).getBlock())) continue;

			boolean tooClose = false;
			for (BlockPos existing : chosen) {
				if (existing.distSqr(mound) < MIN_SITE_SPACING_SQR) {
					tooClose = true;
					break;
				}
			}
			if (tooClose) continue;

			chosen.add(mound);
		}
		return chosen;
	}

	// ------------------------------------------------------------------
	// Effects and helpers
	// ------------------------------------------------------------------

	/**
	 * The item this site would yield if completed right now: the buried doll on the guarantee
	 * attempt, otherwise a junk item chosen deterministically from the position — so the preview
	 * never flickers and matches whatever {@link #tryDig}'s final scoop spawns (which reruns the
	 * same {@link DigQuestRules#shouldFindDoll} rule and {@link #junkIndex}).
	 */
	private static ItemStack previewReward(DigQuestStore.Quest quest, BlockPos pos) {
		int attemptsAfter = quest.attempts() + 1;
		int remainingAfter = quest.sites().size() - 1;
		if (DigQuestRules.shouldFindDoll(attemptsAfter, quest.targetAttempt(), remainingAfter)) {
			return PhoneCalls.createDoll(quest.buriedKey());
		}
		return new ItemStack(JUNK.get(junkIndex(pos)));
	}

	/** Stable junk pick for a site, so the reveal preview and the final spawned item agree. */
	private static int junkIndex(BlockPos pos) {
		return Math.floorMod(pos.getX() * 31 + pos.getY() * 17 + pos.getZ(), JUNK.size());
	}

	/** Records the item a mound is revealing on its block entity, for the client to render emerging. */
	private static void setRevealItem(ServerLevel level, BlockPos pos, ItemStack stack) {
		if (level.getBlockEntity(pos) instanceof DigSiteBlockEntity be) {
			be.setRevealItem(stack);
		}
	}

	private static void digEffects(ServerLevel level, BlockPos pos) {
		level.playSound(null, pos, SoundEvents.ROOTED_DIRT_BREAK, SoundSource.BLOCKS,
				0.7f, 0.8f + level.getRandom().nextFloat() * 0.4f);
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
				pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 12, 0.25, 0.15, 0.25, 0.05);
	}

	private static void spawnReward(ServerLevel level, BlockPos pos, ItemStack stack) {
		ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, stack);
		item.setDeltaMovement(0, 0.28, 0);
		item.setDefaultPickUpDelay();
		level.addFreshEntity(item);
	}

	private static void removeSiteBlock(ServerLevel level, BlockPos pos) {
		internalMutation = true;
		try {
			if (level.getBlockState(pos).is(BlockRegistry.DIG_SITE_BLOCK.get())) {
				level.removeBlock(pos, false);
			}
		} finally {
			internalMutation = false;
		}
	}

	private static String dimensionId(ServerLevel level) {
		return level.dimension().location().toString();
	}
}
