package dev.mrshawn.pokeblocks.block.custom;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.BlockEntityRegistry;
import dev.mrshawn.pokeblocks.registry.SoundRegistry;
import dev.mrshawn.pokeblocks.utils.ColorFactory;
import dev.mrshawn.pokeblocks.utils.WoolColorMatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Simple pokedoll block. Mostly a wrapper to provide a BlockEntity and render as an entity.
 */
public class PokedollBlock extends BaseEntityBlock implements EntityBlock, SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	/**
	 * Color of wax particles (warm honey/amber).
	 */
	private static final Vector3f WAX_PARTICLE_COLOR = new Vector3f(0.95f, 0.75f, 0.2f);

	public PokedollBlock() {
		super(Properties.of().sound(SoundType.WOOL).strength(0.4f).noOcclusion());
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
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
		builder.add(FACING, WATERLOGGED);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
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

	// --- Interaction: honeycomb waxing ---

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
											  Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		if (stack.is(Items.HONEYCOMB) && level.getBlockEntity(pos) instanceof PokedollBlockEntity pokedoll) {
			if (pokedoll.isWaxed()) {
				// Already waxed
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			}

			// Wax the doll
			pokedoll.setWaxed(true);

			if (!player.isCreative()) {
				stack.shrink(1);
			}

			// Play wax on sound (same as copper waxing)
			level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0f, 1.0f);

			// Spawn wax particles
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

	private List<Block> getWoolColorsForDoll(PokedollBlockEntity pokedoll) {
		try {
			ResourceLocation textureLoc = getTextureForDoll(pokedoll);
			if (textureLoc != null) {
				Vector3f avgColor = ColorFactory.sampleAverageColor(textureLoc, new Vector3f(0.5f, 0.5f, 0.5f));
				return WoolColorMatcher.getClosestWools(avgColor.x(), avgColor.y(), avgColor.z(), 2);
			}
		} catch (Exception ignored) {
		}
		return List.of(Blocks.WHITE_WOOL, Blocks.LIGHT_GRAY_WOOL);
	}

	@Nullable
	private ResourceLocation getTextureForDoll(PokedollBlockEntity pokedoll) {
		String pokemon = pokedoll.getPokemon();
		if (pokemon == null || pokemon.isEmpty()) pokemon = ModSettings.DEFAULT_POKEMON;

		List<ModelFlag> activeFlags = new ArrayList<>();
		for (ModelFlag flag : ModelFlag.values()) {
			if (pokedoll.getFlag(flag) && !flag.getTextureSuffix().isEmpty()) {
				activeFlags.add(flag);
			}
		}
		activeFlags.sort(Comparator.comparingInt(ModelFlag::getSortOrder));

		StringBuilder texSuffix = new StringBuilder();
		for (ModelFlag flag : activeFlags) {
			texSuffix.append(flag.getTextureSuffix());
		}

		List<String> suffixesToTry = new ArrayList<>();
		suffixesToTry.add(texSuffix.toString());

		for (int i = activeFlags.size() - 1; i >= 0; i--) {
			StringBuilder sub = new StringBuilder();
			for (int j = 0; j < activeFlags.size(); j++) {
				if (j != i) sub.append(activeFlags.get(j).getTextureSuffix());
			}
			String s = sub.toString();
			if (!suffixesToTry.contains(s)) suffixesToTry.add(s);
		}
		if (!suffixesToTry.contains("")) suffixesToTry.add("");

		try {
			var rm = Minecraft.getInstance().getResourceManager();
			for (String suffix : suffixesToTry) {
				String[] paths = {
						"textures/block/pokedoll_" + pokemon + suffix + "_texture.png",
						"textures/block/pokedoll_" + pokemon + suffix + ".png"
				};
				for (String path : paths) {
					ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, path);
					if (rm.getResource(loc).isPresent()) return loc;
				}
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	private static Vector3f sampleAverageColor(ResourceLocation textureLoc) {
		try {
			Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(textureLoc);
			if (resource.isPresent()) {
				try (var is = resource.get().open();
					 NativeImage image = NativeImage.read(is)) {
					long r = 0, g = 0, b = 0;
					int count = 0;
					for (int x = 0; x < image.getWidth(); x++) {
						for (int y = 0; y < image.getHeight(); y++) {
							int pixel = image.getPixelRGBA(x, y);
							int a = (pixel >> 24) & 0xFF;
							if (a < 128) continue;
							r += pixel & 0xFF;
							g += (pixel >> 8) & 0xFF;
							b += (pixel >> 16) & 0xFF;
							count++;
						}
					}
					if (count > 0) {
						return new Vector3f(
								(r / (float) count) / 255f,
								(g / (float) count) / 255f,
								(b / (float) count) / 255f
						);
					}
				}
			}
		} catch (Exception ignored) {
		}
		return new Vector3f(0.9f, 0.9f, 0.9f);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case NORTH -> Block.box(4, 0, 4, 12, 12, 12);
			case SOUTH -> Block.box(4, 0, 4, 12, 12, 12);
			case WEST -> Block.box(4, 0, 4, 12, 12, 12);
			default -> Block.box(4, 0, 4, 12, 12, 12);
		};
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}
}
