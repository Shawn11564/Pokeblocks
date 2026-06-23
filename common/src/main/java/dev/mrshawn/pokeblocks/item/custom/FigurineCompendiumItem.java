package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.client.screen.FigurineCompendiumClientHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * "Figurine Compendium" book. Right-clicking opens a client-side screen that renders every
 * registered figurine as a 3D silhouette, filling in the ones the player currently owns. Sibling
 * of {@link CompendiumItem}, but scoped to figurines and with per-entry descriptions.
 */
public class FigurineCompendiumItem extends Item {

    public FigurineCompendiumItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (level.isClientSide) {
            // Guarded so the client-only screen class is never loaded on a dedicated server.
            FigurineCompendiumClientHooks.open();
        }
        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }
}
