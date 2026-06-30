package dev.mrshawn.pokeblocks.client.laser;

import dev.mrshawn.pokeblocks.entity.custom.LaserDotEntity;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side, per-frame cache of active {@link LaserDotEntity} positions, used by the doll renderer to
 * decide which dolls should turn to look at a laser dot.
 * <p>
 * {@code PokedollBlockRenderer.rotateBlock} runs once per doll per frame, so scanning the world for dot
 * entities there directly would be wasteful. Instead the scan runs at most once every
 * {@link #REFRESH_INTERVAL_MS}; every doll that frame reads the same small cached list. The scan walks
 * {@link ClientLevel#entitiesForRendering()} — the exact set the renderer draws from — so any dot whose
 * beam is visible is guaranteed to be considered (a spatial {@code getEntitiesOfClass} query could miss
 * an entity we reposition client-side each tick). Read on the render thread only, so plain statics suffice.
 */
public final class ActiveLaserDots {

	private ActiveLaserDots() {}

	private static final long REFRESH_INTERVAL_MS = 30L;

	/** {@link Long#MIN_VALUE} marks "never refreshed"; guarded explicitly to avoid subtraction overflow. */
	private static long lastRefreshMs = Long.MIN_VALUE;
	private static List<Vec3> dots = List.of();

	/** The nearest laser dot within {@code radius} of {@code center}, or {@code null} if none. */
	public static Vec3 nearestWithin(Vec3 center, double radius) {
		refresh();
		if (dots.isEmpty()) return null;

		double bestSq = radius * radius;
		Vec3 best = null;
		for (Vec3 dot : dots) {
			double distSq = dot.distanceToSqr(center);
			if (distSq <= bestSq) {
				bestSq = distSq;
				best = dot;
			}
		}
		return best;
	}

	private static void refresh() {
		long now = Util.getMillis();
		// Note the explicit "never refreshed" guard: comparing (now - Long.MIN_VALUE) overflows, which would
		// otherwise wedge the throttle permanently and the cache would never populate.
		if (lastRefreshMs != Long.MIN_VALUE && now - lastRefreshMs < REFRESH_INTERVAL_MS) return;
		lastRefreshMs = now;

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null) {
			dots = List.of();
			return;
		}

		List<Vec3> positions = null;
		for (Entity entity : level.entitiesForRendering()) {
			if (entity instanceof LaserDotEntity dot) {
				if (positions == null) positions = new ArrayList<>();
				positions.add(dot.position());
			}
		}
		dots = positions == null ? List.of() : positions;
	}
}
