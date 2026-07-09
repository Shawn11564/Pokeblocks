package dev.mrshawn.pokeblocks.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * The "throwable" marker a pokedoll gains from the doll + snowball recipe
 * ({@link dev.mrshawn.pokeblocks.recipe.ThrowableDollRecipe}) and sheds again when it lands
 * (see {@link dev.mrshawn.pokeblocks.entity.custom.ThrownPokedollEntity}).
 * <p>
 * The marker is a boolean inside the vanilla {@code minecraft:custom_data} component — deliberately
 * <b>not</b> part of the canonical {@code BLOCK_ENTITY_DATA} identity tag ({@link PokeblocksItemData}):
 * the doll's identity (species + flags) is untouched, throwable dolls of the same variant still stack
 * with each other but not with plain ones, and none of the block-entity paths (placement, pick-block,
 * drops) ever see or propagate the marker. One snowball buys one throw; every landing outcome
 * (placed, dropped, worn) yields the plain doll again via {@link #strip}.
 * <p>
 * Command form: {@code /give @s pokeblocks:pokedoll[custom_data={pokeblocks_throwable:true}]}.
 */
public final class ThrowableDolls {

	/** Boolean key inside {@code minecraft:custom_data} marking a doll as throwable. */
	public static final String KEY_THROWABLE = "pokeblocks_throwable";

	private ThrowableDolls() {}

	/** True when the stack carries the throwable marker. */
	public static boolean isThrowable(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().getBoolean(KEY_THROWABLE);
	}

	/** A copy of the doll (count 1) with the throwable marker set. */
	public static ItemStack makeThrowable(ItemStack doll) {
		ItemStack result = doll.copyWithCount(1);
		CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> tag.putBoolean(KEY_THROWABLE, true));
		return result;
	}

	/**
	 * Removes the marker from the stack. {@link CustomData#update} drops the {@code custom_data}
	 * component entirely when the marker was its only content, so the result is byte-identical to a
	 * never-throwable doll and stacks with it. Mutates and returns the given stack.
	 */
	public static ItemStack strip(ItemStack stack) {
		if (stack.has(DataComponents.CUSTOM_DATA)) {
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(KEY_THROWABLE));
		}
		return stack;
	}
}
