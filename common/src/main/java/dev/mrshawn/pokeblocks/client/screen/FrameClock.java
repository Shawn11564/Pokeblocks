package dev.mrshawn.pokeblocks.client.screen;

import net.minecraft.Util;

/** Tracks the per-frame time delta (seconds). Tick once per frame before updating spins. */
public final class FrameClock {

    private static final float MAX_DT = 0.1f; // clamp to avoid a jump after a stall

    private long lastMs = -1L;
    private float dt;

    public void tick() {
        long now = Util.getMillis();
        if (lastMs < 0L) {
            lastMs = now;
        }
        dt = Math.min((now - lastMs) / 1000f, MAX_DT);
        lastMs = now;
    }

    public float dt() {
        return dt;
    }
}
