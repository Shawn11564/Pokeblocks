package dev.mrshawn.pokeblocks.block.custom;

import dev.mrshawn.pokeblocks.block.entity.custom.DigSiteBlockEntity;
import dev.mrshawn.pokeblocks.phone.DigQuestManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * A phone-call dig site: a mound of disturbed soil that appears in the AIR cell above diggable
 * ground (so no existing block is ever replaced) and is excavated by right-clicking — each scoop
 * advances {@link #STAGE} until the final one resolves into the buried doll or junk (see
 * {@link DigQuestManager#tryDig}). Left alone, a scooped mound slowly refills itself stage by
 * stage, like a vanilla suspicious-sand block un-brushing (scheduled ticks kicked off by the
 * manager on each scoop; see {@link #tick}). Unbreakable and piston-proof so the quest can't be
 * mined or pushed apart; it only leaves the world through the dig flow, quest cleanup, or losing
 * its supporting block (an external removal the manager is told about via {@link #onRemove}).
 * <p>
 * No item form: the mound exists only as part of an active quest.
 */
public class DigSiteBlock extends Block implements EntityBlock {

	/** How far the hole has been dug; {@link #MAX_STAGE} is one scoop away from resolving. */
	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
	public static final int MAX_STAGE = 3;

	/** How long a scooped mound sits untouched before soil starts sliding back in. */
	public static final int COVER_GRACE_TICKS = 100;
	/** Once refilling has started, one stage re-covers every this many ticks. */
	private static final int COVER_INTERVAL_TICKS = 60;

	// The mound flattens as the hole deepens; the top face sits at surfaceHeight(stage).
	private static final VoxelShape[] SHAPES = new VoxelShape[MAX_STAGE + 1];

	static {
		for (int stage = 0; stage <= MAX_STAGE; stage++) {
			SHAPES[stage] = Block.box(2, 0, 2, 14, 16 * surfaceHeight(stage), 14);
		}
	}

	/** Height of the mound's top face at a dig stage — the soil surface the reveal item pokes out of. */
	public static float surfaceHeight(int stage) {
		return (6 - stage) / 16f;
	}

	public DigSiteBlock() {
		super(Properties.of()
				.strength(-1.0F, 3600000.0F)
				.noOcclusion()
				.noLootTable()
				.sound(SoundType.ROOTED_DIRT)
				.pushReaction(PushReaction.BLOCK));
		this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STAGE);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(STAGE)];
	}

	/**
	 * The mound carries a {@link DigSiteBlockEntity} holding the item it's about to yield, so the
	 * client can render it slowly emerging as the hole is scooped (the render shape stays the normal
	 * MODEL — the block entity only drives the reveal item, like a vanilla brushable block).
	 */
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DigSiteBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
			return DigQuestManager.tryDig(serverPlayer, serverLevel, pos, state);
		}
		return InteractionResult.PASS;
	}

	/**
	 * The vanilla-archaeology "cover back up": once {@link #COVER_GRACE_TICKS} have passed since the
	 * owner's last scoop (the block entity remembers it), one stage of soil slides back in every
	 * {@link #COVER_INTERVAL_TICKS} until the mound is whole again — the reveal item sinks back under
	 * with it. A scoop during the grace window just pushes the refill out; the manager schedules the
	 * first tick on each dig ({@link DigQuestManager#tryDig}).
	 */
	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int stage = state.getValue(STAGE);
		if (stage <= 0) return;
		if (level.getBlockEntity(pos) instanceof DigSiteBlockEntity be) {
			long wait = be.getLastDigTime() + COVER_GRACE_TICKS - level.getGameTime();
			if (wait > 0) {
				level.scheduleTick(pos, this, (int) wait);
				return;
			}
		}
		level.setBlock(pos, state.setValue(STAGE, stage - 1), Block.UPDATE_ALL);
		coverEffects(level, pos);
		if (stage - 1 > 0) {
			level.scheduleTick(pos, this, COVER_INTERVAL_TICKS);
		}
	}

	private static void coverEffects(ServerLevel level, BlockPos pos) {
		level.playSound(null, pos, SoundEvents.ROOTED_DIRT_PLACE, SoundSource.BLOCKS,
				0.5f, 0.7f + level.getRandom().nextFloat() * 0.3f);
		level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DIRT.defaultBlockState()),
				pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5, 8, 0.25, 0.1, 0.25, 0.03);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos below = pos.below();
		return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
									 LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.DOWN && !state.canSurvive(level, pos)) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		// External removals (support loss, /setblock, ...) must update the owning quest; the
		// manager ignores this callback for its own placements/removals.
		if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
			DigQuestManager.onSiteBlockRemoved(serverLevel, pos);
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}
}
