package dev.mrshawn.pokeblocks.client.screen;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * Per-doll spin state. The doll turns continuously at a base speed, and rarely kicks into a fast
 * burst that eases back down. Framerate-independent: all motion scales by the frame delta (seconds).
 */
public final class DollSpin {

    private static final float BASE_SPEED = 22f;             // degrees/second, normal spin
    private static final float BURST_SPEED = 540f;           // degrees/second, burst peak
    private static final float SPEED_DECAY = 2.5f;           // how quickly a burst eases back
    private static final float BURST_CHANCE_PER_SEC = 0.01f; // ~once every 100s per doll

    private static final RandomSource RANDOM = RandomSource.create();

    private float yaw = RANDOM.nextFloat() * 360f - 180f;    // random start so dolls aren't in sync
    private float speed = BASE_SPEED;

    public void update(float dt) {
        // Rarely trigger a fast burst (probability scaled by frame time so it's framerate-independent).
        if (RANDOM.nextFloat() < BURST_CHANCE_PER_SEC * dt) {
            speed = BURST_SPEED;
        }
        // Ease the spin speed back toward the base, then advance the angle.
        float ease = 1f - (float) Math.exp(-dt * SPEED_DECAY);
        speed += (BASE_SPEED - speed) * ease;
        yaw = Mth.wrapDegrees(yaw + speed * dt);
    }

    public float yaw() {
        return yaw;
    }

    /** Adopts an externally-controlled angle (e.g. after a manual drag) so the spin resumes from it. */
    public void snapTo(float yaw) {
        this.yaw = Mth.wrapDegrees(yaw);
        this.speed = BASE_SPEED;
    }
}
