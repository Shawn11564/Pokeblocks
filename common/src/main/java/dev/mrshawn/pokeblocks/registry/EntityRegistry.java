package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.entity.custom.SeatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public final class EntityRegistry {
    private EntityRegistry() {}

    public static void init() {}

    private static final String SEAT_ID = "seat";

    public static final Supplier<EntityType<SeatEntity>> SEAT_ENTITY = PokeblocksCommon.COMMON_PLATFORM.registerEntity(
            SEAT_ID,
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.001f, 0.001f)
                    .noSummon()
                    .noSave()
                    .fireImmune()
                    .build(SEAT_ID)
    );
}