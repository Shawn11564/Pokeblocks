package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.pokemon.FigurineFlag;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;
import java.util.Set;

/**
 * The memorial marker a pokedoll gains when a tamed {@link dev.mrshawn.pokeblocks.entity.custom.FigurineEntity}
 * dies: the doll its owner originally gifted it comes back with a lore epitaph
 * ({@code <name>'s favorite doll}) and enough data to remember who it belonged to. Placing the
 * memorial doll spawns that figurine again, sitting beside the doll as a memorial (see
 * {@code PokedollBlock#setPlacedBy}).
 * <p>
 * Like {@link ThrowableDolls}, the marker lives in the vanilla {@code minecraft:custom_data}
 * component, <b>not</b> the canonical {@code BLOCK_ENTITY_DATA} identity tag — the doll still reads
 * as its species + flags everywhere, and because the block-entity round trip (place → break)
 * rebuilds a canonical stack, the memorial charge is naturally spent on placement: breaking the
 * placed doll returns a plain one.
 */
public final class MemorialDolls {

	/** The remembered figurine's base id, inside {@code minecraft:custom_data}. */
	public static final String KEY_FIGURINE = "pokeblocks_memorial_figurine";
	/** The remembered figurine's flag set, in {@link FigurineFlag#encodeSet} form. */
	public static final String KEY_FLAGS = "pokeblocks_memorial_flags";
	/** The remembered figurine's display name at the time it died. */
	public static final String KEY_NAME = "pokeblocks_memorial_name";

	private MemorialDolls() {}

	/** True when the stack carries the memorial marker. */
	public static boolean isMemorial(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().contains(KEY_FIGURINE);
	}

	/**
	 * A copy (count 1) of the gifted doll turned into the fallen figurine's memorial: marker data
	 * plus the {@code <name>'s favorite doll} lore line.
	 */
	public static ItemStack makeMemorial(ItemStack giftedDoll, String figurine, Set<FigurineFlag> flags, String figurineName) {
		ItemStack result = giftedDoll.copyWithCount(1);
		CustomData.update(DataComponents.CUSTOM_DATA, result, tag -> {
			tag.putString(KEY_FIGURINE, figurine);
			tag.putString(KEY_FLAGS, FigurineFlag.encodeSet(flags));
			tag.putString(KEY_NAME, figurineName);
		});
		result.set(DataComponents.LORE, new ItemLore(List.of(
				Component.literal(figurineName + "'s favorite doll").withStyle(ChatFormatting.GOLD))));
		return result;
	}

	/** The remembered figurine id, or {@code ""} when the stack is no memorial. */
	public static String figurineOf(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? "" : data.copyTag().getString(KEY_FIGURINE);
	}

	/** The remembered figurine's flags (empty when the stack is no memorial). */
	public static Set<FigurineFlag> flagsOf(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return FigurineFlag.decodeSet(data == null ? "" : data.copyTag().getString(KEY_FLAGS));
	}

	/** The remembered figurine's display name, or {@code ""} when the stack is no memorial. */
	public static String nameOf(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null ? "" : data.copyTag().getString(KEY_NAME);
	}
}
