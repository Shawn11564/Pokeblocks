package dev.mrshawn.pokeblocks.block.entity.headpile;

import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class HeadPileBlockEntity extends PokedollBlockEntity {

	private int currentTextureIndex = 0;

	public HeadPileBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
		super(blockEntityType, pos, state);
	}

	// Override this method to create sync packet
	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	// This is crucial - it tells the game what data to sync
	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		NbtCompound nbt = new NbtCompound();
		this.writeNbt(nbt, registryLookup);
		return nbt;
	}

	@Override
	public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		nbt.putInt("TextureIndex", currentTextureIndex);
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		if (nbt.contains("TextureIndex")) {
			currentTextureIndex = nbt.getInt("TextureIndex");
			// Make sure index is in bounds
			currentTextureIndex = Math.min(currentTextureIndex, getTexturePaths().length - 1);
		}

		// This forces a visual refresh on the client
		if (world != null && world.isClient()) {
			world.updateListeners(pos, getCachedState(), getCachedState(), 3);
		}
	}

	public boolean cycleModelAndTexture() {
		// Check if we should move to next model or texture
		if (currentTextureIndex == getTexturePaths().length - 1) {
			return false;
		}
		currentTextureIndex = (currentTextureIndex + 1) % getTexturePaths().length;

		// Mark for sync with client
		markDirty();

		// If we're on the server, need to explicitly sync with clients
		if (world != null && !world.isClient()) {
			world.updateListeners(pos, getCachedState(), getCachedState(), 3);
		}
		return true;
	}

	public String getModelPath() {
		return getModelPaths()[currentTextureIndex]; // Get directly from array for safety
	}

	public String getTexturePath() {
		return getTexturePaths()[currentTextureIndex]; // Get directly from array for safety
	}

	public int getTextureIndex() {
		return this.currentTextureIndex;
	}

	abstract public String[] getModelPaths();
	abstract public String[] getTexturePaths();

}
