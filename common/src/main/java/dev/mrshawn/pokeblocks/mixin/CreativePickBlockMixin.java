package dev.mrshawn.pokeblocks.mixin;

import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.Map;

/**
 * Intercepts the ctrl+middle-click (ClickType.CLONE) in the creative inventory for
 * Pokedoll items and redirects it through {@code Inventory.setPickedItem} — the same
 * path as a regular middle-click — so that an existing stack in the inventory is
 * selected rather than a fresh count-1 item always being placed in a new slot.
 */
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativePickBlockMixin extends Screen {

    protected CreativePickBlockMixin() {
        super(null);
    }

    @Inject(
            method = "slotClicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void pokeblocks$redirectCloneToSetPickedItem(
            Slot slot, int slotId, int mouseButton, ClickType clickType, CallbackInfo ci) {

        if (clickType != ClickType.CLONE) return;
        if (slot == null || !slot.hasItem()) return;
        if (!(slot.getItem().getItem() instanceof PokedollItem)) return;
        if (this.minecraft == null || this.minecraft.player == null) return;

        // Read the block entity state from the level so we get the full pokemon+flags,
        // then build the item the same way getCloneItemStack does.
        ItemStack picked = null;

        if (this.minecraft.level != null && this.minecraft.hitResult instanceof BlockHitResult blockHit) {
            if (this.minecraft.level.getBlockEntity(blockHit.getBlockPos()) instanceof PokedollBlockEntity pokedoll) {
                Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
                for (ModelFlag flag : ModelFlag.values()) {
                    flagMap.put(flag, pokedoll.getFlag(flag));
                }
                picked = PokedollItem.createPokedoll(pokedoll.getPokemon(), flagMap);
            }
        }

        // Fall back to reading pokemon+flags directly from the slot's ItemStack
        // (e.g. when picking from the creative search tab rather than from the world).
        if (picked == null) {
            ItemStack slotStack = slot.getItem();
            String pokemon = PokedollItem.getPokemonFromStack(slotStack);
            Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
            for (ModelFlag flag : ModelFlag.values()) {
                flagMap.put(flag, PokedollItem.getFlagsFromStack(slotStack).contains(flag));
            }
            picked = PokedollItem.createPokedoll(pokemon, flagMap);
        }

        // Route through setPickedItem: searches existing inventory stacks first,
        // and only places a new item if no matching stack is found.
        this.minecraft.player.getInventory().setPickedItem(picked);
        this.minecraft.gameMode.handleCreativeModeItemAdd(
                this.minecraft.player.getInventory().getSelected(),
                (short) (this.minecraft.player.getInventory().selected + 36)
        );

        ci.cancel();
    }
}
