package dev.mrshawn.pokeblocks.interaction;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.DecorativeRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/** Registers all concrete doll interaction handlers. */
public final class DollInteractions {

    public static void init() {
        registerEiscueShear();
    }

    // --- Eiscue shearing ---

    /**
     * Right-clicking an eiscue doll with shears transforms it to its noice variant and
     * drops an eiscue head pile. All variant flags (shiny, gigantic, etc.) are preserved
     * on the transformed doll; shiny/gigantic are mirrored onto the dropped head pile.
     * Wax state carries over automatically since the block entity is modified in-place.
     */
    private static void registerEiscueShear() {
        DollInteractionRegistry.register("eiscue", (stack, state, level, pos, player, hand, doll) -> {
            if (!stack.is(Items.SHEARS)) return null;
            if (doll.getFlag(ModelFlag.NOICE)) return null; // already noice

            boolean isShiny = doll.getFlag(ModelFlag.SHINY);
            boolean isGigantic = doll.getFlag(ModelFlag.GIGANTIC);

            // Transform: add the NOICE flag — all other flags and wax state are untouched.
            doll.setFlag(ModelFlag.NOICE, true);

            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0f, 1.0f);

            stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

            // Drop a head pile whose flags match the doll that was sheared.
            Set<ModelFlag> headFlags = EnumSet.noneOf(ModelFlag.class);
            if (isShiny) headFlags.add(ModelFlag.SHINY);
            if (isGigantic) headFlags.add(ModelFlag.GIGANTIC);
            Block.popResource(level, pos, createEiscueHeadPile(headFlags));

            return ItemInteractionResult.SUCCESS;
        });
    }

    private static ItemStack createEiscueHeadPile(Set<ModelFlag> flags) {
        DecorativeRegistry.DecorativeEntry entry = DecorativeRegistry.EISCUE_HEAD_PILE;
        String blockEntityId = PokeblocksCommon.MOD_ID + ":" + entry.definition().id();
        return DecorativeItem.createStack(entry.item().get(), blockEntityId, flags, Map.of("headCount", "1"));
    }

    private DollInteractions() {}
}
