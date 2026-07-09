package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.entity.custom.LaserDotEntity;
import dev.mrshawn.pokeblocks.entity.custom.SeatEntity;
import dev.mrshawn.pokeblocks.entity.custom.ThrownPokedollEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public final class EntityRegistry {
    private EntityRegistry() {}

    public static void init() {}

    private static final String SEAT_ID = "seat";
    private static final String LASER_DOT_ID = "laser_dot";
    private static final String THROWN_POKEDOLL_ID = "thrown_pokedoll";

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

    /**
     * A pokedoll in flight after right-clicking a throwable doll (doll + snowball recipe). Sized and
     * tracked exactly like a vanilla snowball; the doll stack rides the entity's synched item, and
     * impact handling (place / drop / head-equip) lives in {@link ThrownPokedollEntity}.
     */
    public static final Supplier<EntityType<ThrownPokedollEntity>> THROWN_POKEDOLL_ENTITY = PokeblocksCommon.COMMON_PLATFORM.registerEntity(
            THROWN_POKEDOLL_ID,
            () -> EntityType.Builder.<ThrownPokedollEntity>of(ThrownPokedollEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(THROWN_POKEDOLL_ID)
    );
}