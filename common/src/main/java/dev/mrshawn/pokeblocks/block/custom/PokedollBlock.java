package dev.mrshawn.pokeblocks.block.custom;

import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.ParticleSourceBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.addon.PokeblocksAddon;
import dev.mrshawn.pokeblocks.addon.PokeblocksAddons;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.entity.custom.FigurineEntity;
import dev.mrshawn.pokeblocks.interaction.DollInteractionRegistry;
import dev.mrshawn.pokeblocks.item.MemorialDolls;
import dev.mrshawn.pokeblocks.item.TrappedDolls;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import dev.mrshawn.pokeblocks.registry.EntityRegistry;
import dev.mrshawn.pokeblocks.registry.SoundRegistry;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
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

	/**
	 * Placing a memorial doll (see {@link MemorialDolls} — the doll a fallen tamed figurine gave
	 * back) brings that figurine back: it spawns beside the doll, already tamed to the placer,
	 * sitting and facing its favorite doll. The marker rides {@code custom_data}, which the
	 * block-entity round trip never copies, so breaking the placed doll returns a plain one —
	 * one memorial, one revival.
	 */
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		// A trapped doll (see TrappedDolls) stays armed when placed. Like the memorial marker, the
		// trapped marker rides custom_data, which the block-entity data transfer never copies — so
		// it is handed over explicitly here and handed back in getDrops/getCloneItemStack.
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PokedollBlockEntity pokedoll
				&& TrappedDolls.isTrapped(stack)) {
			pokedoll.setTrapped(true, TrappedDolls.explosiveOf(stack));
		}
		if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof PokedollBlockEntity pokedoll) {
			for (PokeblocksAddon addon : PokeblocksAddons.all()) {
				addon.onDollPlaced(serverLevel, pos, pokedoll, placer, stack);
			}
		}
		if (!(level instanceof ServerLevel serverLevel) || !MemorialDolls.isMemorial(stack)) {
			return;
		}

		FigurineEntity figurine = EntityRegistry.FIGURINE_ENTITY.get().create(serverLevel);
		if (figurine == null) {
			return;
		}
		figurine.setFigurine(MemorialDolls.figurineOf(stack), MemorialDolls.flagsOf(stack));
		String name = MemorialDolls.nameOf(stack);
		if (!name.isEmpty()) {
			figurine.setCustomName(Component.literal(name));
		}
		if (placer instanceof Player player) {
			// setTame instead of tame(): rejoining a memorial shouldn't re-fire the tame-an-animal
			// advancement. Sitting only sticks on tamed mobs (SitWhenOrderedToGoal), so the sit
			// order is tied to having an owner; a placer-less placement spawns it standing.
			figurine.setTame(true, true);
			figurine.setOwnerUUID(player.getUUID());
			figurine.setOrderedToSit(true);
			figurine.setInSittingPose(true);
		}
		// A revived figurine is bound to its memorial: it stays tamed (sit toggle and all) but never
		// follows its owner again — it wanders around the doll within the configured radius instead.
		figurine.setMemorialAnchor(pos);

		// First free side wins, the figurine facing back toward its doll; on top as a last resort.
		boolean placed = false;
		for (Direction side : Direction.Plane.HORIZONTAL) {
			Vec3 spot = Vec3.atBottomCenterOf(pos.relative(side));
			figurine.moveTo(spot.x, spot.y, spot.z, side.getOpposite().toYRot(), 0.0f);
			if (serverLevel.noCollision(figurine)) {
				placed = true;
				break;
			}
		}
		if (!placed) {
			Vec3 top = Vec3.atBottomCenterOf(pos.above());
			figurine.moveTo(top.x, top.y, top.z, state.getValue(ROTATION) * 22.5f, 0.0f);
		}
		serverLevel.addFreshEntity(figurine);
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
			ItemStack copy = PokedollItem.createPokedoll(pokedoll.getPokemon(), flagMap);
			// isTrapped is only known server-side (integrated server / dedicated pick path);
			// a client-side pick of someone else's trap yields the plain doll, which is the point.
			return pokedoll.isTrapped() ? TrappedDolls.makeTrapped(copy, pokedoll.getTrappedExplosive()) : copy;
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
			ItemStack drop = PokedollItem.createPokedoll(pokedoll.getPokemon(), flagMap);
			// Mining a trapped doll returns it still armed — popping is the only way it goes off.
			return java.util.List.of(pokedoll.isTrapped() ? TrappedDolls.makeTrapped(drop, pokedoll.getTrappedExplosive()) : drop);
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
			if (level instanceof ServerLevel serverLevel) {
				for (PokeblocksAddon addon : PokeblocksAddons.all()) {
					InteractionResult result = addon.useDoll(serverLevel, pos, state, pokedoll, player);
					if (result != null) return result;
				}
			}

			// Check for break (only non-waxed dolls)
			if (pokedoll.recordClick()) {
				String pokemon = pokedoll.getPokemon();
				if (pokedoll.isTrapped() && level instanceof ServerLevel trapLevel) {
					// The trap springs: no wool, no substitute — the packed explosive goes off
					// (vanilla TNT: a single-TNT blast with block damage).
					TrappedDolls.detonateBlock(trapLevel, pos, pokedoll.getTrappedExplosive());
				} else {
					breakDoll(level, pos, pokedoll);
				}
				if (level instanceof ServerLevel serverLevel) {
					for (PokeblocksAddon addon : PokeblocksAddons.all()) {
						addon.onDollPopped(serverLevel, pos, pokemon, player);
					}
				}
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

	/** Wool dropped when a doll's texture can't be sampled — always the case on a dedicated server. */
	private static final List<Block> FALLBACK_WOOLS = List.of(Blocks.WHITE_WOOL, Blocks.LIGHT_GRAY_WOOL);

	/**
	 * Samples a doll's dominant wool-drop colors from its texture. Reading the texture PNG needs the client
	 * resource manager and blaze3d's {@code NativeImage} — classes that DO NOT EXIST on a dedicated server —
	 * so the implementation lives on the client and is installed via {@link #setClientWoolSampler}. Returns
	 * {@code null} when the texture can't be resolved or sampled.
	 */
	@FunctionalInterface
	public interface WoolSampler {
		@Nullable List<Block> sample(String pokemon, Set<ModelFlag> activeFlags);
	}

	/**
	 * Client-installed texture sampler, or {@code null} on a dedicated server.
	 * <p>
	 * The break path ({@link #breakDoll}) runs on the logical server. Sampling touches
	 * {@code net.minecraft.client.Minecraft} and {@code com.mojang.blaze3d.platform.NativeImage}, which are
	 * absent on a dedicated server, so calling it directly threw {@link NoClassDefFoundError} — an
	 * {@link Error} that slips past {@code catch (Exception)} and could crash the server on the first doll pop.
	 * Routing through this hook keeps those classes off the server-reachable path: a dedicated server never
	 * installs it and drops {@link #FALLBACK_WOOLS}, while an integrated server (singleplayer/LAN) shares the
	 * client's resource manager and keeps drops color-matched.
	 * <p>
	 * Regression test: {@code mctests/pokeblocks.serverpop.mctest.yml} pops a doll on a real dedicated server
	 * ({@code DollPopProbes}).
	 */
	@Nullable
	private static volatile WoolSampler clientWoolSampler = null;

	/** Installs the client-side wool sampler. Called once from the client bootstrap; never on a dedicated server. */
	public static void setClientWoolSampler(@Nullable WoolSampler sampler) {
		clientWoolSampler = sampler;
	}

	private List<Block> getWoolColorsForDoll(PokedollBlockEntity pokedoll) {
		WoolSampler sampler = clientWoolSampler;
		if (sampler != null) {
			// Throwable, not Exception: a cosmetic wool sample must never break the drop or the server tick.
			try {
				String pokemon = pokedoll.getPokemon();
				if (pokemon == null || pokemon.isEmpty()) pokemon = ModSettings.DEFAULT_POKEMON;

				Set<ModelFlag> activeFlags = EnumSet.noneOf(ModelFlag.class);
				for (ModelFlag flag : ModelFlag.values()) {
					if (pokedoll.getFlag(flag)) activeFlags.add(flag);
				}

				List<Block> sampled = sampler.sample(pokemon, activeFlags);
				if (sampled != null && !sampled.isEmpty()) return sampled;
			} catch (Throwable ignored) {
			}
		}
		return FALLBACK_WOOLS;
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
