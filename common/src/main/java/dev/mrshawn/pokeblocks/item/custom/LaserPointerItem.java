package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.entity.custom.LaserDotEntity;
import dev.mrshawn.pokeblocks.item.IncompleteFeatureItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Hand-held laser pointer. While right-click is held it projects a coloured beam from the player to
 * whatever they're aiming at, and dolls near the dot turn to look at it (see {@code PokedollBlockRenderer}).
 * <p>
 * The visible effect is driven entirely by a server-spawned {@link LaserDotEntity} at the aim point, which
 * vanilla entity tracking syncs to every nearby client — so the beam and the doll-turning are visible to
 * everyone with no custom networking. The beam colour lives in the vanilla {@link DyedItemColor} component
 * and is changed by combining the pointer with a dye in a crafting table (see {@code LaserPointerDyeRecipe}).
 * <p>
 * Flagged {@link IncompleteFeatureItem}: hidden from the creative menu unless {@code [creative]
 * show_incomplete_items} is enabled, and always carries the "incomplete" tooltip.
 */
public class LaserPointerItem extends Item implements IncompleteFeatureItem {

	/** Beam colour (red) used when the pointer has not been dyed. */
	public static final int DEFAULT_COLOR = 0xFF3030;

	public LaserPointerItem(Properties properties) {
		super(properties);
	}

	/** The beam colour for a stack as a packed 0xRRGGBB int: its dyed colour, or {@link #DEFAULT_COLOR}. */
	public static int getColor(ItemStack stack) {
		return DyedItemColor.getOrDefault(stack, DEFAULT_COLOR) & 0xFFFFFF;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		player.startUsingItem(hand);
		if (level instanceof ServerLevel serverLevel) {
			LaserDotEntity.spawnFor(serverLevel, player, getColor(stack));
		}
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
		// The aim dot self-updates from the owner's look each tick; nothing to do here. Overridden only so
		// the pointer remains an actively-used item for the whole duration.
	}

	@Override
	public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
		// Remove the beam promptly on release; the dot would also self-discard next tick as a safety net.
		if (level instanceof ServerLevel serverLevel && entity instanceof Player player) {
			LaserDotEntity.clearFor(serverLevel, player);
		}
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000; // effectively "until released", like a fully-drawn bow
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		IncompleteFeatureItem.appendIncompleteTooltip(tooltip);
	}
}
