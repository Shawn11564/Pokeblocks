package dev.mrshawn.pokeblocks.block.entity.headpile;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.constants.ResourceConstants;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class EiscueHeadpileBlockEntity extends HeadPileBlockEntity {

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

	public EiscueHeadpileBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(EiscueHeadpileBlockEntity.class), pos, state);
	}


	@Override
	public String[] getModelPaths() {
		return MODEL_PATHS;
	}

	@Override
	public String[] getTexturePaths() {
		return TEXTURE_PATHS;
	}
}
