package dev.mrshawn.pokeblocks.block.pokeblock;

import dev.mrshawn.pokeblocks.block.entity.PokeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.client.model.PokeBlockModel;
import dev.mrshawn.pokeblocks.block.entity.client.renderer.PokeBlockRenderer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class PokeBlock extends BlockWithEntity {

	private final String name;
	private final PokeBlockModel pokeBlockModel;
	private final PokeBlockRenderer pokeBlockRenderer;

	public PokeBlock(String name) {
		super(
				FabricBlockSettings.copy(Blocks.WHITE_WOOL)
						.nonOpaque()
		);
		this.name = name;
		pokeBlockModel = new PokeBlockModel() {
			@Override
			public Identifier getModelResource(PokeBlockEntity animatable) {
				return getPokeModelResource(animatable);
			}

			@Override
			public Identifier getTextureResource(PokeBlockEntity animatable) {
				return getPokeTextureResource(animatable);
			}

			@Override
			public Identifier getAnimationResource(PokeBlockEntity animatable) {
				return getPokeAnimationResource(animatable);
			}
		};
		pokeBlockRenderer = new PokeBlockRenderer(pokeBlockModel);
	}

	public PokeBlock(Settings settings, String name, PokeBlockModel pokeBlockModel, PokeBlockRenderer pokeBlockRenderer) {
		super(settings);
		this.name = name;
		this.pokeBlockModel = pokeBlockModel;
		this.pokeBlockRenderer = pokeBlockRenderer;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return createPokeBlockEntity(pos, state);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	protected abstract Identifier getPokeModelResource(PokeBlockEntity animatable);

	protected abstract Identifier getPokeTextureResource(PokeBlockEntity animatable);

	protected abstract Identifier getPokeAnimationResource(PokeBlockEntity animatable);

	protected abstract BlockEntity createPokeBlockEntity(BlockPos pos, BlockState state);

	public String getPokeBlockName() {
		return name;
	}
}
