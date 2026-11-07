package dev.mrshawn.pokeblocks.trapped;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.TrappedDolls;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Which items can trap a doll, and how a popped trap sets off the explosive it was packed with.
 * <p>
 * <b>Accepted explosives</b> ({@link #isExplosive}): anything in the {@code pokeblocks:trap_explosives}
 * item tag ({@code data/pokeblocks/tags/item/trap_explosives.json}; entries for other mods go in as
 * {@code {"id": "somemod:nuke", "required": false}} so the tag still loads without that mod), plus
 * every block item whose block extends vanilla {@link TntBlock}, which picks up most TNT mods with
 * no list at all.
 * <p>
 * <b>Detonation</b> ({@link #detonate}): vanilla TNT, and dolls trapped before explosives were
 * remembered, keep the classic single-TNT blast. Any other explosive is set off as itself: its
 * block's own "caught in an explosion" hook ({@link Block#wasExploded}, the chain-reaction path
 * every TNT-like block implements on every loader) runs at the doll's position, so whatever it
 * really does (its own primed entity, a nuke, fire, a lit block) happens there. The block is never
 * placed, so nothing can drop or duplicate. If that hook does nothing, Forge/NeoForge also try the
 * block's ignition hook ({@code onCaughtFire}, what flint and steel calls). If the explosive still
 * did nothing (not a block, its mod was removed, or a block with no explosion behaviour) the
 * classic blast goes off instead, so a trapped doll always goes bang.
 */
public final class TrappedExplosives {

	/** Extra items that can trap a doll, beyond auto-detected {@link TntBlock} subclasses. */
	public static final TagKey<Item> TAG = TagKey.create(Registries.ITEM,
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "trap_explosives"));

	/** How a trapped doll went off. */
	public enum Outcome {
		/** Vanilla TNT (or no remembered explosive): the classic single-TNT blast. */
		CLASSIC,
		/** The packed explosive went off with its own behaviour. */
		ACTIVATED,
		/** The packed explosive did nothing, so the classic blast went off instead. */
		FALLBACK
	}

	private TrappedExplosives() {}

	/** Whether this stack can be crafted into a doll to trap it. */
	public static boolean isExplosive(ItemStack stack) {
		if (stack.isEmpty() || stack.is(ItemRegistry.POKEDOLL_ITEM.get())) return false;
		if (stack.is(TAG)) return true;
		return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock;
	}

	/**
	 * The id a doll trapped with this explosive remembers, or null for vanilla TNT. Vanilla TNT
	 * stores nothing so those dolls stay identical to (and stack with) dolls trapped before
	 * explosives were remembered.
	 */
	@Nullable
	public static ResourceLocation idFor(ItemStack explosive) {
		if (explosive.is(Items.TNT)) return null;
		return BuiltInRegistries.ITEM.getKey(explosive.getItem());
	}

	/** The item for a remembered explosive id, if it is still registered. */
	public static Optional<Item> itemFor(@Nullable ResourceLocation explosiveId) {
		return explosiveId == null ? Optional.empty() : BuiltInRegistries.ITEM.getOptional(explosiveId);
	}

	/**
	 * Sets off the doll's explosive at {@code pos}, which must already be cleared (the doll block
	 * destroyed). See the class docs for the order of attempts.
	 */
	public static Outcome detonate(ServerLevel level, BlockPos pos, @Nullable ResourceLocation explosiveId) {
		if (explosiveId == null || explosiveId.equals(BuiltInRegistries.ITEM.getKey(Items.TNT))) {
			TrappedDolls.classicBlast(level, pos);
			return Outcome.CLASSIC;
		}
		Optional<Item> item = itemFor(explosiveId);
		if (item.isPresent() && item.get() instanceof BlockItem blockItem && blockItem.getBlock() != Blocks.TNT
				&& activate(level, pos, blockItem.getBlock().defaultBlockState())) {
			return Outcome.ACTIVATED;
		}
		TrappedDolls.classicBlast(level, pos);
		return Outcome.FALLBACK;
	}

	/**
	 * Makes {@code state}'s block go off at {@code pos} as if it had been there and been caught in a
	 * blast. Returns false when nothing observable happened (see {@link ExplosiveActivity}).
	 */
	public static boolean activate(ServerLevel level, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		String what = BuiltInRegistries.BLOCK.getKey(block).toString();
		Explosion trigger = new Explosion(level, null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				TrappedDolls.EXPLOSION_POWER, false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);

		ExplosiveActivity.Record exploded = ExplosiveActivity.record(what, () -> block.wasExploded(level, pos, trigger));
		if (exploded.isEmpty()) {
			return !ignite(level, pos, state, what).isEmpty();
		}
		if (exploded.onlyVanillaTnt() && block instanceof TntBlock) {
			// A modded TntBlock that only spawned plain primed TNT inherited vanilla's chain-reaction
			// hook. On Forge/NeoForge its ignition hook is the one such mods override (flint and
			// steel calls it), so prefer that if it does something of its own.
			ExplosiveActivity.Record lit = ignite(level, pos, state, what);
			if (!lit.isEmpty() && !lit.onlyVanillaTnt()) {
				exploded.discardEntities();
			} else {
				lit.discardEntities();
			}
		}
		return true;
	}

	private static ExplosiveActivity.Record ignite(ServerLevel level, BlockPos pos, BlockState state, String what) {
		return ExplosiveActivity.record(what, () -> PokeblocksCommon.COMMON_PLATFORM.igniteBlock(state, level, pos));
	}
}
