package dev.mrshawn.pokeblocks.entity.custom;

import dev.mrshawn.pokeblocks.block.custom.SittablePokedollBlock;
import dev.mrshawn.pokeblocks.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SittableEntity extends Entity {

	public SittableEntity(EntityType<?> type, World world) {
		super(type, world);
		this.noClip = true;
		this.setInvisible(true);
	}

	public SittableEntity(World world, BlockPos pos, double yOffset) {
		this(ModEntities.SITTABLE, world);
		this.setPosition(pos.getX() + 0.5, pos.getY() + yOffset, pos.getZ() + 0.5);
	}

	@Override
	protected void initDataTracker() {
		// No data to track
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		// No custom data to read
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		// No custom data to write
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.getWorld().isClient) return;

		// If no passengers or the block is gone, remove this entity
		if (this.getPassengerList().isEmpty() ||
				!(this.getWorld().getBlockState(new BlockPos((int) this.getX(), (int) (this.getY() - 0.5), (int) this.getZ()))
						.getBlock() instanceof SittablePokedollBlock)) {
			this.discard();
		}
	}

	@Override
	public void removePassenger(Entity passenger) {
		super.removePassenger(passenger);

		// Position the dismounting entity in front of the block
		if (passenger instanceof PlayerEntity) {
			BlockPos blockPos = this.getBlockPos().down();
			Vec3d direction = passenger.getRotationVector().multiply(0.5);
			passenger.setPosition(
					blockPos.getX() + 0.5 + direction.x,
					blockPos.getY() + 1.0,
					blockPos.getZ() + 0.5 + direction.z
			);
		}
	}

	@Override
	public double getMountedHeightOffset() {
		return 0.0; // Adjust based on visual needs
	}

	// This is crucial for preventing movement
	@Override
	public boolean isPushable() {
		return false;
	}

	// Prevent movement completely
	@Override
	public void move(MovementType movementType, Vec3d movement) {
		// Do nothing - prevents any movement
	}

}
