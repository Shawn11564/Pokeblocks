package dev.mrshawn.pokeblocks.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SeatEntity extends Entity {

	public SeatEntity(EntityType<?> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide() && this.getPassengers().isEmpty()) {
			this.discard();
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {}

	@Override
	protected boolean canRide(Entity vehicle) {
		return true;
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isNoGravity() {
		return true;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return false;
	}

}
