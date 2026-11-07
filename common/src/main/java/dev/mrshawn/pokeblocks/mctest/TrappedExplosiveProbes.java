package dev.mrshawn.pokeblocks.mctest;

import dev.mrshawn.pokeblocks.PokeblocksServerLifecycle;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.TrappedDolls;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.recipe.TrappedDollRecipe;
import dev.mrshawn.pokeblocks.registry.BlockRegistry;
import dev.mrshawn.pokeblocks.trapped.TrappedExplosives;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.StringJoiner;

/**
 * The {@code trapped.*} server-truth probes behind {@code mctests/pokeblocks.trapped.mctest.yml}:
 * the doll + explosive recipe, the explosive id surviving the block entity, and the real
 * detonation engine ({@link TrappedExplosives}) on a live server.
 * <p>
 * Unlike the rest of {@link PokeblocksStateProvider}, the two world probes do touch the world, as
 * little as possible: they work in the empty sky above spawn (near the build limit, so a blast
 * there has no blocks to break), place and pop one doll, and discard every entity they spawned.
 */
final class TrappedExplosiveProbes {

	private TrappedExplosiveProbes() {}

	/** A doll + explosive grid; {@code explosiveId} "none" leaves the second slot empty. */
	static CraftingInput grid(ItemStack doll, String explosiveId) {
		ItemStack explosive = "none".equals(explosiveId) ? ItemStack.EMPTY
				: new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(explosiveId)));
		return CraftingInput.of(2, 1, List.of(doll, explosive));
	}

	static boolean recipeMatches(ItemStack doll, String explosiveId) {
		return new TrappedDollRecipe().matches(grid(doll, explosiveId), null);
	}

	/** The explosive id the crafted doll remembers ("" for vanilla TNT / none). */
	static String assembledExplosive(ItemStack doll, String explosiveId) {
		ItemStack result = new TrappedDollRecipe().assemble(grid(doll, explosiveId), server().registryAccess());
		if (!TrappedDolls.isTrapped(result)) return "not trapped";
		ResourceLocation id = TrappedDolls.explosiveOf(result);
		return id == null ? "" : id.toString();
	}

	/** Saves and reloads a trapped doll block entity, returning the explosive it still remembers. */
	static String blockEntityRoundTrip(String explosiveId) {
		MinecraftServer server = server();
		var state = BlockRegistry.POKEDOLL_BLOCK.get().defaultBlockState();
		PokedollBlockEntity saved = new PokedollBlockEntity(BlockPos.ZERO, state);
		saved.setTrapped(true, ResourceLocation.parse(explosiveId));
		CompoundTag tag = saved.saveWithoutMetadata(server.registryAccess());
		PokedollBlockEntity loaded = new PokedollBlockEntity(BlockPos.ZERO, state);
		loaded.loadWithComponents(tag, server.registryAccess());
		if (!loaded.isTrapped()) return "not trapped";
		ResourceLocation id = loaded.getTrappedExplosive();
		return id == null ? "" : id.toString();
	}

	/**
	 * Runs the activation engine with {@code blockId}'s block in the sky and reports what it
	 * spawned: comma-joined entity type ids, "none" when it did nothing. Spawned entities are discarded.
	 */
	static String activation(String blockId) {
		ServerLevel level = server().overworld();
		BlockPos pos = skyPos(level);
		Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
		List<Entity> before = level.getEntities((Entity) null, new AABB(pos).inflate(4));
		boolean activated = TrappedExplosives.activate(level, pos, block.defaultBlockState());
		StringJoiner spawned = new StringJoiner(",");
		for (Entity entity : level.getEntities((Entity) null, new AABB(pos).inflate(4))) {
			if (!before.contains(entity)) {
				spawned.add(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
				entity.discard();
			}
		}
		String result = spawned.length() == 0 ? "none" : spawned.toString();
		return activated ? result : "dud:" + result;
	}

	/**
	 * Places a doll trapped with {@code explosiveId} in the sky, pops it through the same call
	 * {@code PokedollBlock} makes, and returns the {@link TrappedExplosives.Outcome} name, with
	 * "+doll left behind" appended if the doll block survived.
	 */
	static String detonate(String explosiveId) {
		ServerLevel level = server().overworld();
		BlockPos pos = skyPos(level);
		level.setBlock(pos, BlockRegistry.POKEDOLL_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
		if (!(level.getBlockEntity(pos) instanceof PokedollBlockEntity doll)) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
			return "no block entity";
		}
		ItemStack stack = TrappedDolls.makeTrapped(PokedollItem.createPokedoll(ModSettings.DEFAULT_POKEMON),
				"minecraft:tnt".equals(explosiveId) ? null : ResourceLocation.parse(explosiveId));
		doll.setTrapped(true, TrappedDolls.explosiveOf(stack));
		List<Entity> before = level.getEntities((Entity) null, new AABB(pos).inflate(8));
		TrappedExplosives.Outcome outcome = TrappedDolls.detonateBlock(level, pos, doll.getTrappedExplosive());
		for (Entity entity : level.getEntities((Entity) null, new AABB(pos).inflate(8))) {
			if (!before.contains(entity)) entity.discard();
		}
		boolean dollLeft = level.getBlockState(pos).is(BlockRegistry.POKEDOLL_BLOCK.get());
		if (dollLeft) level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
		return outcome.name() + (dollLeft ? "+doll left behind" : "");
	}

	/** Air near the build limit above world spawn: loaded (spawn chunks), nothing to blow up. */
	private static BlockPos skyPos(ServerLevel level) {
		BlockPos spawn = level.getSharedSpawnPos();
		return new BlockPos(spawn.getX(), level.getMaxBuildHeight() - 10, spawn.getZ());
	}

	private static MinecraftServer server() {
		MinecraftServer server = PokeblocksServerLifecycle.currentServer();
		if (server == null) throw new IllegalStateException("server not started");
		return server;
	}
}
