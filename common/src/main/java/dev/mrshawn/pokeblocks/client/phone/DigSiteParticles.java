package dev.mrshawn.pokeblocks.client.phone;

import dev.mrshawn.pokeblocks.phone.ClientDigSites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

import java.util.List;

/**
 * Client-side guiding particles over the player's active dig sites (positions synced via
 * {@link ClientDigSites}): a soft golden shimmer at ground level plus an occasional rising
 * end-rod mote that is visible from a distance. Ticked from the {@code PhoneClientTickMixin}
 * on the client tick — purely cosmetic, nothing here is authoritative.
 */
public final class DigSiteParticles {

	private static final Vector3f GOLD = new Vector3f(1.0f, 0.83f, 0.3f);
	private static final DustParticleOptions GOLD_DUST = new DustParticleOptions(GOLD, 0.9f);

	/** Sites farther than this from the camera skip particles entirely. */
	private static final double MAX_RANGE_SQR = 96 * 96;

	private static final int SHIMMER_INTERVAL_TICKS = 3;
	private static final int BEACON_INTERVAL_TICKS = 40;

	private static int tickCounter;

	private DigSiteParticles() {}

	public static void tick(Minecraft minecraft) {
		ClientLevel level = minecraft.level;
		if (level == null) {
			// Left the world — drop any remembered sites so they can't leak into the next one.
			ClientDigSites.clear();
			return;
		}
		if (minecraft.isPaused()) return;

		List<BlockPos> sites = ClientDigSites.sites();
		if (sites.isEmpty()) return;
		if (!level.dimension().location().toString().equals(ClientDigSites.dimension())) return;

		Entity camera = minecraft.getCameraEntity();
		if (camera == null) return;

		tickCounter++;
		RandomSource random = level.random;

		for (BlockPos site : sites) {
			if (camera.distanceToSqr(site.getX() + 0.5, site.getY() + 0.5, site.getZ() + 0.5) > MAX_RANGE_SQR) {
				continue;
			}
			if (tickCounter % SHIMMER_INTERVAL_TICKS == 0) {
				for (int i = 0; i < 2; i++) {
					level.addParticle(GOLD_DUST,
							site.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.9,
							site.getY() + 0.3 + random.nextDouble() * 0.3,
							site.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.9,
							0.0, 0.02, 0.0);
				}
			}
			if (tickCounter % BEACON_INTERVAL_TICKS == 0) {
				level.addParticle(ParticleTypes.END_ROD,
						site.getX() + 0.5, site.getY() + 0.4, site.getZ() + 0.5,
						0.0, 0.07, 0.0);
			}
		}
	}
}
