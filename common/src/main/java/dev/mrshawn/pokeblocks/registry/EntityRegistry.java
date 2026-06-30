package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.entity.custom.LaserDotEntity;
import dev.mrshawn.pokeblocks.entity.custom.SeatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public final class EntityRegistry {
    private EntityRegistry() {}

    public static void init() {}

    private static final String SEAT_ID = "seat";
    private static final String LASER_DOT_ID = "laser_dot";

    public static final Supplier<EntityType<SeatEntity>> SEAT_ENTITY = PokeblocksCommon.COMMON_PLATFORM.registerEntity(
            SEAT_ID,
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.001f, 0.001f)
                    .noSummon()
                    .noSave()
                    .fireImmune()
                    .build(SEAT_ID)
    );

    /**
     * Transient marker spawned at a laser pointer's aim point. Tracked frequently and from far away so
     * the beam stays responsive and visible across the room; never summonable or saved.
     */
    public static final Supplier<EntityType<LaserDotEntity>> LASER_DOT_ENTITY = PokeblocksCommon.COMMON_PLATFORM.registerEntity(
            LASER_DOT_ID,
            () -> EntityType.Builder.<LaserDotEntity>of(LaserDotEntity::new, MobCategory.MISC)
                    .sized(0.05f, 0.05f)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .noSummon()
                    .noSave()
                    .fireImmune()
                    .build(LASER_DOT_ID)
    );
}