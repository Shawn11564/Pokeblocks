package dev.mrshawn.pokeblocks.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.ParticleSourceBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.interaction.DollInteractionRegistry;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import dev.mrshawn.pokeblocks.registry.SoundRegistry;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import dev.mrshawn.pokeblocks.utils.ColorFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple pokedoll block. Mostly a wrapper to provide a BlockEntity and render as an entity.
 */
public class PokedollBlock extends BaseEntityBlock implements EntityBlock, SimpleWaterloggedBlock, ParticleSourceBlock {
	/**
	 * Fine-grained placement rotation (0-15), matching vanilla standing signs/banners.
	 * The doll's GeckoLib model is rotated by {@link dev.mrshawn.pokeblocks.client.renderer.block.PokedollBlockRenderer}.
	 */
	public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	/**
	 * Color of wax particles (warm honey/amber).
	 */
	private static final Vector3f WAX_PARTICLE_COLOR = new Vector3f(0.95f, 0.75f, 0.2f);

	public PokedollBlock() {
		// noLootTable: drops are built from the block entity in getDrops (NBT-preserving), not a json table.
		// dynamicShape: the hitbox depends on the block entity's pokemon/flags (see getShape), so vanilla
		// must not precompute per-state shape caches from a BE-less EmptyBlockGetter.
		super(Properties.of().sound(SoundType.WOOL).strength(0.4f).noOcclusion().noLootTable().dynamicShape());
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(ROTATION, 0)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(ROTATION, WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState()
				// Face the placer. (No +180 offset; the model's authored front already faces the player.)
				.setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation()))
				.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), 16));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), 16));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
								  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return BlockEntityRegistry.POKEDOLL_BLOCK_ENTITY.get().create(blockPos, blockState);
	}

	// --- Pick block (middle-click in creative) ---

	/**
	 * Returns a copy of the doll with its pokemon and flags intact, so middle-clicking
	 * in creative gives the exact variant rather than the default substitute.
	 */
	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof PokedollBlockEntity pokedoll) {
			Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) {
				flagMap.put(flag, pokedoll.getFlag(flag));
			}
			return PokedollItem.createPokedoll(pokedoll.getPokemon(), flagMap);
		}
		return super.getCloneItemStack(level, pos, state);
	}

	// --- Drops ---

	/**
	 * Mining the block drops the doll itself with its pokemon + flags intact (the data-driven block has no
	 * loot-table json, so the drop is built from the block entity here — mirrors {@link #getCloneItemStack}).
	 * The spam-click "pop" path ({@link #breakDoll}) breaks via {@code destroyBlock(pos, false)}, which never
	 * calls {@code getDrops}, so it keeps dropping wool/substitute instead.
	 */
	@Override
	protected java.util.List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof PokedollBlockEntity pokedoll) {
			Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) {
				flagMap.put(flag, pokedoll.getFlag(flag));
			}
			return java.util.List.of(PokedollItem.createPokedoll(pokedoll.getPokemon(), flagMap));
		}
		return super.getDrops(state, params);
	}

	// --- Interaction: custom handlers + honeycomb waxing ---

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
											  Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (!(level.getBlockEntity(pos) instanceof PokedollBlockEntity pokedoll)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		// Pokemon-specific interactions registered via DollInteractionRegistry (e.g. shearing eiscue).
		ItemInteractionResult custom = DollInteractionRegistry.dispatch(
				pokedoll.getPokemon(), stack, state, level, pos, player, hand, pokedoll);
		if (custom != null) return custom;

		// Honeycomb waxing
		if (stack.is(Items.HONEYCOMB)) {
			if (pokedoll.isWaxed()) {
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}
			pokedoll.setWaxed(true);
			if (!player.isCreative()) {
				stack.shrink(1);
			}
			level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);
			spawnWaxParticles(level, pos, 10);
			return ItemInteractionResult.SUCCESS;
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	// --- Interaction: squish / break ---

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide()) return InteractionResult.SUCCESS;

		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof PokedollBlockEntity pokedoll) {
			// Check for break (only non-waxed dolls)
			if (pokedoll.recordClick()) {
				breakDoll(level, pos, pokedoll);
				return InteractionResult.SUCCESS;
			}

			// For waxed dolls, track clicks for particle effects
			if (pokedoll.isWaxed()) {
				pokedoll.recordClickForParticles();

				// Show wax particles occasionally when spam-clicking a waxed doll
				int clicks = pokedoll.getRapidClickCount();
				if (clicks >= 3 && clicks % 2 == 0) {
					spawnWaxParticles(level, pos, 3 + clicks / 2);
				}
			}

			// Normal squish
			pokedoll.triggerSquish();

			level.playSound(
					null,
					pos,
					SoundRegistry.POKEDOLL_SQUEAK.get(),
					SoundSource.BLOCKS,
					0.8f,
					0.9f + level.getRandom().nextFloat() * 0.2f
			);
		}

		return InteractionResult.SUCCESS;
	}

	// --- Break logic ---

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!state.is(newState.getBlock())) {
			// Spawn wax particles on break if the doll was waxed
			if (level.getBlockEntity(pos) instanceof PokedollBlockEntity pokedoll && pokedoll.isWaxed()) {
				spawnWaxParticles(level, pos, 15);
			}
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	/**
	 * Breaks the doll from spam clicking. Has a 1-in-{@link ModSettings#SUBSTITUTE_POP_CHANCE}
	 * chance of dropping a substitute doll (shiny if the original was shiny) instead of wool.
	 */
	private void breakDoll(Level level, BlockPos pos, PokedollBlockEntity pokedoll) {
		// Play wool break sound
		level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 1.0f, 0.8f);

		// 1-in-SUBSTITUTE_POP_CHANCE: pop into a substitute doll instead of wool.
		// Read shiny status before destroying the block entity.
		if (level.getRandom().nextInt(ModSettings.SUBSTITUTE_POP_CHANCE) == 0) {
			boolean isShiny = pokedoll.getFlag(ModelFlag.SHINY);

			// Destroy the block (triggers onRemove for standard break particles)
			level.destroyBlock(pos, false);

			Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
			for (ModelFlag flag : ModelFlag.values()) flagMap.put(flag, false);
			flagMap.put(ModelFlag.SHINY, isShiny);

			popResource(level, pos, PokedollItem.createPokedoll(ModSettings.DEFAULT_POKEMON, flagMap));
			return;
		}

		// Normal pop: determine wool colors from texture before destroying the block
		List<Block> woolColors = getWoolColorsForDoll(pokedoll);

		// Destroy the block (triggers onRemove for standard break particles)
		level.destroyBlock(pos, false);

		// Drop string (2-4 pieces)
		int stringCount = 2 + level.getRandom().nextInt(3);
		popResource(level, pos, new ItemStack(Items.STRING, stringCount));

		// Drop 1-2 color-matched wool blocks
		for (Block wool : woolColors) {
			popResource(level, pos, new ItemStack(wool.asItem(), 1));
		}
	}

	// --- Wax particles ---

	/**
	 * Spawns warm honey-colored wax particles around the block position.
	 * Works from both server and client side.
	 */
	private static void spawnWaxParticles(Level level, BlockPos pos, int count) {
		if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
			for (int i = 0; i < count; i++) {
				double px = pos.getX() + 0.3 + level.getRandom().nextDouble() * 0.4;
				double py = pos.getY() + 0.3 + level.getRandom().nextDouble() * 0.4;
				double pz = pos.getZ() + 0.3 + level.getRandom().nextDouble() * 0.4;

				serverLevel.sendParticles(
						new DustParticleOptions(WAX_PARTICLE_COLOR, 0.8f),
						px, py, pz,
						1,  // particle count
						0.05, 0.05, 0.05,  // spread
						0.0  // speed
				);
			}
		} else {
			// Client-side fallback
			for (int i = 0; i < count; i++) {
				double px = pos.getX() + 0.3 + level.getRandom().nextDouble() * 0.4;
				double py = pos.getY() + 0.3 + level.getRandom().nextDouble() * 0.4;
				double pz = pos.getZ() + 0.3 + level.getRandom().nextDouble() * 0.4;

				level.addParticle(
						new DustParticleOptions(WAX_PARTICLE_COLOR, 0.8f),
						px, py, pz,
						(level.getRandom().nextDouble() - 0.5) * 0.05,
						level.getRandom().nextDouble() * 0.05,
						(level.getRandom().nextDouble() - 0.5) * 0.05
				);
			}
		}
	}

	// --- Texture color sampling for wool drops ---

	/** Wool dropped when a doll's texture can't be sampled. */
	private static final List<Block> FALLBACK_WOOLS = List.of(Blocks.WHITE_WOOL, Blocks.LIGHT_GRAY_WOOL);

	private List<Block> getWoolColorsForDoll(PokedollBlockEntity pokedoll) {
		try {
			ResourceLocation textureLoc = getTextureForDoll(pokedoll);
			if (textureLoc != null) {
				// Saturation-weighted dominant colors of the texture (cached per texture), so the drops
				// reflect the doll's signature colors rather than the muddy average of all its pixels.
				return ColorFactory.sampleDominantWools(textureLoc, 2, FALLBACK_WOOLS);
			}
		} catch (Exception ignored) {
		}
		return FALLBACK_WOOLS;
	}

	@Nullable
	private ResourceLocation getTextureForDoll(PokedollBlockEntity pokedoll) {
		String pokemon = pokedoll.getPokemon();
		if (pokemon == null || pokemon.isEmpty()) pokemon = ModSettings.DEFAULT_POKEMON;

		Set<ModelFlag> activeFlags = EnumSet.noneOf(ModelFlag.class);
		for (ModelFlag flag : ModelFlag.values()) {
			if (pokedoll.getFlag(flag)) activeFlags.add(flag);
		}

		try {
			// Order-independent lookup shared with the render models (see PokeblocksAssetResolver), so the
			// wool colors sampled on break come from the same texture the doll actually renders with.
			return PokeblocksAssetResolver.pokedollTextureOrNull(Minecraft.getInstance().getResourceManager(), pokemon, activeFlags);
		} catch (Exception ignored) {
			return null;
		}
	}

	/**
	 * Hitbox derived from the doll's own {@code .geo.json} (cached per model + rotation + gigantic),
	 * so each pokemon is targeted/collided at its rendered silhouette instead of a generic box. Falls
	 * back to the legacy centered box whenever the block entity or its model isn't available.
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (world.getBlockEntity(pos) instanceof PokedollBlockEntity pokedoll) {
			return DollShapes.pokedoll(pokedoll, state.getValue(ROTATION));
		}
		return DollShapes.DEFAULT_SHAPE;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}
}
