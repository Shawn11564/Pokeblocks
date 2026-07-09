package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.client.screen.CompendiumClientHooks;
import net.minecraft.ChatFormatting;
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
 * The Figurine Compendium book. Right-clicking opens a client-side screen that renders every
 * registered figurine as a 3D silhouette, filling in the ones the player has collected. Sibling of
 * {@link CompendiumItem}, scoped to figurines and with per-entry descriptions from
 * {@code figurine_descriptions.json}.
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
			CompendiumClientHooks.openFigurines();
		}
		return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.pokeblocks.compendium.figurine")
				.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}
}
