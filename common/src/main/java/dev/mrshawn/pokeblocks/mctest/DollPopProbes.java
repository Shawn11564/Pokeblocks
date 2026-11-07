package dev.mrshawn.pokeblocks.mctest;

import dev.mrshawn.pokeblocks.PokeblocksServerLifecycle;
import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.TreeSet;

/**
 * mc-test probe for the plain spam-click "pop" of a doll on a real dedicated server: places a doll in the
 * empty sky above spawn, pops it through {@code PokedollBlock#breakDoll} (the exact method the click path
 * calls, reached by reflection because it is private) and reports what dropped. Spawned item entities and
 * any leftover block are removed again.
 *
 * <p>Guards the dedicated-server crash where the pop sampled the doll's texture through client-only classes
 * ({@code Minecraft}, {@code NativeImage}) and threw {@link NoClassDefFoundError}. A dedicated server has no
 * texture sampler, so a wool pop must drop the fallback wools.
 */
final class DollPopProbes {

	/** Pops are retried this many times so a 1-in-12 substitute pop can't hide the wool path. */
	private static final int MAX_POPS = 60;

	private DollPopProbes() {}

	/**
	 * Pops a plain doll until it drops wool (not a substitute) and returns the dropped item ids, sorted and
	 * comma-joined; "threw &lt;Throwable&gt;" if the pop threw, "no wool pop" if every try was a substitute.
	 */
	static String pop() {
		ServerLevel level = server().overworld();
		BlockSkyPos sky = new BlockSkyPos(level);
		Method breakDoll;
		try {
			breakDoll = PokedollBlock.class.getDeclaredMethod("breakDoll", Level.class, BlockPos.class, PokedollBlockEntity.class);
			breakDoll.setAccessible(true);
		} catch (NoSuchMethodException e) {
			return "no breakDoll method";
		}
		for (int attempt = 0; attempt < MAX_POPS; attempt++) {
			BlockPos pos = sky.pos;
			level.setBlock(pos, BlockRegistry.POKEDOLL_BLOCK.get().defaultBlockState(), Block.UPDATE_ALL);
			if (!(level.getBlockEntity(pos) instanceof PokedollBlockEntity doll)) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
				return "no block entity";
			}
			AABB area = new AABB(pos).inflate(4);
			List<Entity> before = level.getEntities((Entity) null, area);
			Throwable thrown = null;
			try {
				breakDoll.invoke(level.getBlockState(pos).getBlock(), level, pos, doll);
			} catch (InvocationTargetException e) {
				thrown = e.getCause();
			} catch (IllegalAccessException e) {
				thrown = e;
			}
			TreeSet<String> dropped = new TreeSet<>();
			for (Entity entity : level.getEntities((Entity) null, area)) {
				if (before.contains(entity)) continue;
				if (entity instanceof ItemEntity item) {
					dropped.add(BuiltInRegistries.ITEM.getKey(item.getItem().getItem()).toString());
				}
				entity.discard();
			}
			if (level.getBlockState(pos).is(BlockRegistry.POKEDOLL_BLOCK.get())) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
			}
			if (thrown != null) return "threw " + thrown;
			if (dropped.contains("minecraft:string")) return String.join(",", dropped);
		}
		return "no wool pop";
	}

	/** Air near the build limit above world spawn: loaded (spawn chunks), nothing around it. */
	private record BlockSkyPos(BlockPos pos) {
		BlockSkyPos(ServerLevel level) {
			this(new BlockPos(level.getSharedSpawnPos().getX(), level.getMaxBuildHeight() - 20, level.getSharedSpawnPos().getZ()));
		}
	}

	private static MinecraftServer server() {
		MinecraftServer server = PokeblocksServerLifecycle.currentServer();
		if (server == null) throw new IllegalStateException("server not started");
		return server;
	}
}
