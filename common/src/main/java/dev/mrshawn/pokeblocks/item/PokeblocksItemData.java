package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Single source of truth for the canonical, minimal {@code BLOCK_ENTITY_DATA} written onto
 * Pokeblocks item stacks (pokedolls, figurines and decoratives).
 * <p>
 * Every path that mints a stack — give commands, the creative tabs, loot tables, recipe outputs,
 * doll-break drops and the block entities' own {@link net.minecraft.world.level.block.entity.BlockEntity#saveToItem}
 * (pick-block) — routes through here, so two stacks with the same identity are always byte-identical
 * and therefore stack together.
 *
 * <h2>Canonical format</h2>
 * <ul>
 *   <li><b>{@code id}</b> — always the block-entity type's registry id (e.g. {@code pokeblocks:pokedoll}).
 *       Vanilla's pick-block path ({@code BlockEntity.saveToItem} → {@code BlockItem.setBlockEntityData})
 *       always rewrites this key from the registered type, so our {@code create*} stacks must carry the
 *       same value or a picked doll won't stack with a given one.</li>
 *   <li><b>content key</b> — {@code pokemon} for dolls, {@code figurine} for figurines; decoratives carry
 *       no content key (their identity is the item/block itself).</li>
 *   <li><b>flags</b> — only flags that are {@code true} are written; a {@code false} flag is simply absent
 *       (readers default it to {@code false}).</li>
 *   <li><b>custom nbt</b> (decoratives) — only values that differ from their variant default are written
 *       (readers fall back to the default when absent).</li>
 * </ul>
 * Nothing else is written: no {@code false} flags, no default-valued variants, no transient block-entity
 * state. This keeps the stored tag as small as possible and is the format the data fixers target.
 */
public final class PokeblocksItemData {

	public static final String KEY_ID = "id";
	public static final String KEY_POKEMON = "pokemon";
	public static final String KEY_FIGURINE = "figurine";

	private PokeblocksItemData() {}

	/** The {@code id} tag value for a block-entity type, e.g. {@code blockEntityId("pokedoll")} → {@code "pokeblocks:pokedoll"}. */
	public static String blockEntityId(String localId) {
		return PokeblocksCommon.MOD_ID + ":" + localId;
	}

	// --- Writers -------------------------------------------------------------

	/** Builds the canonical doll tag: {@code {id, pokemon, <true flags>}}. */
	public static CompoundTag pokedollTag(String pokemon, Collection<ModelFlag> activeFlags) {
		CompoundTag tag = base(blockEntityId(ModSettings.DOLL_ID));
		tag.putString(KEY_POKEMON, pokemon == null || pokemon.isEmpty() ? ModSettings.DEFAULT_POKEMON : pokemon);
		writeFlags(tag, activeFlags);
		return tag;
	}

	/** Builds the canonical figurine tag: {@code {id, figurine, <true flags>}}. */
	public static CompoundTag figurineTag(String figurine, Collection<ModelFlag> activeFlags) {
		CompoundTag tag = base(blockEntityId(ModSettings.FIGURINE_ID));
		tag.putString(KEY_FIGURINE, figurine == null || figurine.isEmpty() ? ModSettings.DEFAULT_FIGURINE : figurine);
		writeFlags(tag, activeFlags);
		return tag;
	}

	/**
	 * Builds the canonical decorative tag: {@code {id, <true flags>, <non-default nbt>}}.
	 * <p>
	 * Custom-nbt values equal to their variant default are omitted so that, e.g., an eiscue head pile
	 * created with the default {@code headCount=1} stacks with one whose key was never written at all.
	 *
	 * @param definition the decorative's definition, used to look up variant defaults; may be {@code null}
	 *                   to write {@code customNbt} verbatim
	 */
	public static CompoundTag decorativeTag(String blockEntityId, Collection<ModelFlag> activeFlags,
											Map<String, String> customNbt, DecorativeDefinition definition) {
		CompoundTag tag = base(blockEntityId);
		writeFlags(tag, activeFlags);
		if (customNbt != null && !customNbt.isEmpty()) {
			Map<String, String> defaults = new HashMap<>();
			if (definition != null) {
				for (DecorativeDefinition.NbtVariant variant : definition.nbtVariants()) {
					defaults.put(variant.nbtKey(), variant.defaultValue());
				}
			}
			customNbt.forEach((key, value) -> {
				if (value != null && !value.equals(defaults.get(key))) {
					tag.putString(key, value);
				}
			});
		}
		return tag;
	}

	/** Stores the given tag as the stack's {@code BLOCK_ENTITY_DATA} component. */
	public static void apply(ItemStack stack, CompoundTag tag) {
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
	}

	private static CompoundTag base(String blockEntityId) {
		CompoundTag tag = new CompoundTag();
		tag.putString(KEY_ID, blockEntityId);
		return tag;
	}

	private static void writeFlags(CompoundTag tag, Collection<ModelFlag> activeFlags) {
		if (activeFlags == null) return;
		for (ModelFlag flag : activeFlags) {
			tag.putBoolean(flag.getTagName(), true);
		}
	}

	// --- Readers -------------------------------------------------------------

	/** Reads a string key from the stack's {@code BLOCK_ENTITY_DATA}, or {@code fallback} if absent/empty. */
	public static String readString(ItemStack stack, String key, String fallback) {
		CompoundTag tag = tagOf(stack);
		if (tag != null && tag.contains(key)) {
			String value = tag.getString(key);
			if (!value.isEmpty()) return value;
		}
		return fallback;
	}

	/** Reads the set of flags stored as {@code true} on the stack. */
	public static Set<ModelFlag> readActiveFlags(ItemStack stack) {
		EnumSet<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		CompoundTag tag = tagOf(stack);
		if (tag != null) {
			for (ModelFlag flag : ModelFlag.values()) {
				if (tag.contains(flag.getTagName()) && tag.getBoolean(flag.getTagName())) {
					flags.add(flag);
				}
			}
		}
		return flags;
	}

	/**
	 * Reads every flag key present on the stack into a map of flag → stored value. Flags that are absent
	 * are omitted (callers should treat a missing flag as {@code false}).
	 */
	public static Map<ModelFlag, Boolean> readFlagsMap(ItemStack stack) {
		Map<ModelFlag, Boolean> flags = new EnumMap<>(ModelFlag.class);
		CompoundTag tag = tagOf(stack);
		if (tag != null) {
			for (ModelFlag flag : ModelFlag.values()) {
				if (tag.contains(flag.getTagName())) {
					flags.put(flag, tag.getBoolean(flag.getTagName()));
				}
			}
		}
		return flags;
	}

	private static CompoundTag tagOf(ItemStack stack) {
		CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
		return data == null ? null : data.copyTag();
	}
}
