package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.entity.custom.SeatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public final class EntityRegistry {
    public static void init() {}

    public static final Supplier<EntityType<SeatEntity>> SEAT_ENTITY = PokeblocksCommon.COMMON_PLATFORM.registerEntity(
            "seat",
            () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
                    .sized(0.001f, 0.001f)
                    .noSummon()
                    .noSave()
                    .fireImmune()
                    .build("seat")
    );
}