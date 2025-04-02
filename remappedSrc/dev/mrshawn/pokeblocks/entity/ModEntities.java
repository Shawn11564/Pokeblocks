package dev.mrshawn.pokeblocks.entity;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.entity.custom.SittableEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

	public static final EntityType<SittableEntity> SITTABLE = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier(Pokeblocks.MOD_ID, "sittable"),
			FabricEntityTypeBuilder.<SittableEntity>create(SpawnGroup.MISC, SittableEntity::new)
					.dimensions(EntityDimensions.fixed(0.5F, 0.5F))
					.trackRangeBlocks(0)
					.trackedUpdateRate(Integer.MAX_VALUE)
					.forceTrackedVelocityUpdates(false)
					.build()
	);

	public static void registerAllEntities() {
		Pokeblocks.LOGGER.info("Registering mod entities for " + Pokeblocks.MOD_ID);
	}

}
