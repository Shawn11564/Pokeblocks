package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.client.screen.CompendiumClientHooks;
import dev.mrshawn.pokeblocks.item.IncompleteFeatureItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * POC "Doll Compendium" book. Right-clicking opens a client-side screen that renders every
 * registered doll as a 3D silhouette, filling in the ones the player currently owns.
 */
public class CompendiumItem extends Item implements IncompleteFeatureItem {

    public CompendiumItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (level.isClientSide) {
            // Guarded so the client-only screen class is never loaded on a dedicated server.
            CompendiumClientHooks.open();
        }
        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        IncompleteFeatureItem.appendIncompleteTooltip(tooltip);
    }
}
