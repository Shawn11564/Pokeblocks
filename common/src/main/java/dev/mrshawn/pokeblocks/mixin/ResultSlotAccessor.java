package dev.mrshawn.pokeblocks.mixin;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Reads the crafting grid behind a result slot (doll surgery checks and consumes its ingredients). */
@Mixin(ResultSlot.class)
public interface ResultSlotAccessor {

	@Accessor("craftSlots")
	CraftingContainer pokeblocks$craftSlots();
}
