package dev.mrshawn.pokeblocks.block.entity.headpile;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EiscueHeadpileBlockEntity extends PokedollBlockEntity {

	private static final String[] MODEL_PATHS = {
			ResourceConstants.EISCUE_HEAD_PILE_1_MODEL,
			ResourceConstants.EISCUE_HEAD_PILE_2_MODEL,
			ResourceConstants.EISCUE_HEAD_PILE_3_MODEL
	};

	private static final String[] TEXTURE_PATHS = {
			ResourceConstants.EISCUE_HEAD_PILE_1_TEXTURE,
			ResourceConstants.EISCUE_HEAD_PILE_2_TEXTURE,
			ResourceConstants.EISCUE_HEAD_PILE_3_TEXTURE
	};

	private int currentTextureIndex = 0;

	public EiscueHeadpileBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(EiscueHeadpileBlockEntity.class), pos, state);
	}

	// Override this method to create sync packet
	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	// This is crucial - it tells the game what data to sync
	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound nbt = new NbtCompound();
		this.writeNbt(nbt);
		return nbt;
	}

	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putInt("TextureIndex", currentTextureIndex);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("TextureIndex")) {
			currentTextureIndex = nbt.getInt("TextureIndex");
			// Make sure index is in bounds
			currentTextureIndex = Math.min(currentTextureIndex, TEXTURE_PATHS.length - 1);
		}

		// This forces a visual refresh on the client
		if (world != null && world.isClient()) {
			world.updateListeners(pos, getCachedState(), getCachedState(), 3);
		}
	}

	public boolean cycleModelAndTexture() {
		// Check if we should move to next model or texture
		if (currentTextureIndex == TEXTURE_PATHS.length - 1) {
			return false;
		}
		currentTextureIndex = (currentTextureIndex + 1) % TEXTURE_PATHS.length;

		// Mark for sync with client
		markDirty();

		// If we're on the server, need to explicitly sync with clients
		if (world != null && !world.isClient()) {
			world.updateListeners(pos, getCachedState(), getCachedState(), 3);
		}
		return true;
	}

	public String getModelPath() {
		return MODEL_PATHS[currentTextureIndex]; // Get directly from array for safety
	}

	public String getTexturePath() {
		return TEXTURE_PATHS[currentTextureIndex]; // Get directly from array for safety
	}
}
