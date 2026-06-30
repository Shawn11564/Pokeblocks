package dev.mrshawn.pokeblocks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Marker for items whose feature is not yet finished.
 * <p>
 * Two effects, both driven by {@code [creative] show_incomplete_items} (default {@code false}):
 * <ul>
 *   <li><b>Creative menu</b> — incomplete items are hidden from the Pokeblocks creative tabs unless the
 *       config flag is enabled (see {@code ItemGroupRegistry}). This keeps unfinished content out of
 *       normal play while still letting it be toggled on for testing.</li>
 *   <li><b>Tooltip</b> — incomplete items always carry a short warning lore line (see
 *       {@link #appendIncompleteTooltip}) so it's obvious in-hand that the feature may be rough.</li>
 * </ul>
 * Implement this empty marker on any {@link net.minecraft.world.item.Item} to flag it. No methods are
 * required; the gating and tooltip are applied generically by the call sites.
 */
public interface IncompleteFeatureItem {

	/**
	 * Appends the standard "incomplete feature" warning lines to a tooltip. Call this from the item's
	 * {@code appendHoverText} so every flagged item presents the same notice.
	 */
	static void appendIncompleteTooltip(List<Component> tooltip) {
		tooltip.add(Component.empty());
		tooltip.add(Component.translatable("tooltip.pokeblocks.incomplete").withStyle(ChatFormatting.GOLD));
		tooltip.add(Component.translatable("tooltip.pokeblocks.incomplete.detail")
				.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
	}
}
