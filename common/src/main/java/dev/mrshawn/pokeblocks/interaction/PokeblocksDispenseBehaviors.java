package dev.mrshawn.pokeblocks.interaction;

import dev.mrshawn.pokeblocks.item.ThrowableDolls;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * Registers the pokedoll dispenser behavior: a <b>throwable</b> doll (see {@link ThrowableDolls}) is
 * launched exactly like a snowball — vanilla {@link ProjectileDispenseBehavior} over
 * {@code PokedollItem#asProjectile}, with impact handling (place / drop / head-equip) in
 * {@link dev.mrshawn.pokeblocks.entity.custom.ThrownPokedollEntity} — while a plain doll keeps the
 * vanilla default and is simply ejected. Gating per stack here is why {@code PokedollItem} can't just
 * be registered via {@code DispenserBlock.registerProjectileBehavior}: that would launch every doll.
 *
 * <p>Called once during each loader's common setup: directly from {@code onInitialize} on Fabric, and
 * inside {@code FMLCommonSetupEvent.enqueueWork} on Forge/NeoForge — the dispenser registry is a plain
 * (non-thread-safe) map, and the deferred item supplier only resolves after the registry events.
 */
public final class PokeblocksDispenseBehaviors {

	private PokeblocksDispenseBehaviors() {}

	public static void register() {
		DispenseItemBehavior launch = new ProjectileDispenseBehavior(ItemRegistry.POKEDOLL_ITEM.get());
		DispenseItemBehavior eject = new DefaultDispenseItemBehavior();
		DispenserBlock.registerBehavior(ItemRegistry.POKEDOLL_ITEM.get(), (source, stack) ->
				(ThrowableDolls.isThrowable(stack) ? launch : eject).dispense(source, stack));
	}
}
