package dev.mrshawn.pokeblocks.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.item.custom.FigurineItem;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.DecorativeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The output of a {@link PokeblocksShapedRecipe}.
 * <p>
 * Because dolls, figurines and decorations are component-based items rather than plain
 * registered items, the result can't always be expressed as a vanilla item id. This class
 * supports four mutually exclusive output kinds:
 * <pre>
 *   { "item": "minecraft:stick", "count": 2 }              // a vanilla item
 *   { "decorative": "applin_basket", "flags": ["shiny"] }  // a decoration (e.g. basket / cushion)
 *   { "doll": "applin", "flags": ["shiny"] }               // a pokedoll
 *   { "figurine": "doncheadle" }                           // a figurine
 * </pre>
 * {@code flags} and {@code count} are optional ({@code count} defaults to 1).
 */
public final class PokeblocksRecipeResult {

	public enum Kind {
		ITEM, DECORATIVE, DOLL, FIGURINE
	}

	private final Kind kind;
	private final String id;
	private final Set<ModelFlag> flags;
	private final int count;

	private PokeblocksRecipeResult(Kind kind, String id, Set<ModelFlag> flags, int count) {
		this.kind = kind;
		this.id = id;
		this.flags = flags;
		this.count = count;
	}

	/** Builds the output stack. Returns {@link ItemStack#EMPTY} if the target no longer exists. */
	public ItemStack toStack() {
		ItemStack stack = switch (kind) {
			case ITEM -> {
				Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
				yield new ItemStack(item, count);
			}
			case DECORATIVE -> {
				DecorativeRegistry.DecorativeEntry entry = DecorativeRegistry.getById(id);
				if (entry == null) yield ItemStack.EMPTY;
				ItemStack s = DecorativeItem.createStack(entry.item().get(), PokeblocksItemData.blockEntityId(id), flags);
				s.setCount(count);
				yield s;
			}
			case DOLL -> {
				Map<ModelFlag, Boolean> flagMap = new EnumMap<>(ModelFlag.class);
				for (ModelFlag flag : flags) flagMap.put(flag, true);
				ItemStack s = PokedollItem.createPokedoll(id, flagMap);
				s.setCount(count);
				yield s;
			}
			case FIGURINE -> {
				ItemStack s = FigurineItem.createFigurine(id);
				s.setCount(count);
				yield s;
			}
		};
		return stack;
	}

	// --- Codec ---

	public static final Codec<PokeblocksRecipeResult> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.optionalFieldOf("item").forGetter(result -> result.kind == Kind.ITEM ? Optional.of(result.id) : Optional.empty()),
			Codec.STRING.optionalFieldOf("decorative").forGetter(result -> result.kind == Kind.DECORATIVE ? Optional.of(result.id) : Optional.empty()),
			Codec.STRING.optionalFieldOf("doll").forGetter(result -> result.kind == Kind.DOLL ? Optional.of(result.id) : Optional.empty()),
			Codec.STRING.optionalFieldOf("figurine").forGetter(result -> result.kind == Kind.FIGURINE ? Optional.of(result.id) : Optional.empty()),
			PokeblocksIngredient.FLAG_CODEC.listOf().optionalFieldOf("flags", List.of()).forGetter(result -> new ArrayList<>(result.flags)),
			Codec.INT.optionalFieldOf("count", 1).forGetter(result -> result.count)
	).apply(instance, PokeblocksRecipeResult::fromFields));

	private static PokeblocksRecipeResult fromFields(Optional<String> item, Optional<String> decorative, Optional<String> doll, Optional<String> figurine, List<ModelFlag> flags, int count) {
		Set<ModelFlag> flagSet = EnumSet.noneOf(ModelFlag.class);
		flagSet.addAll(flags);
		if (item.isPresent()) return new PokeblocksRecipeResult(Kind.ITEM, item.get(), flagSet, count);
		if (decorative.isPresent()) return new PokeblocksRecipeResult(Kind.DECORATIVE, decorative.get(), flagSet, count);
		if (doll.isPresent()) return new PokeblocksRecipeResult(Kind.DOLL, doll.get(), flagSet, count);
		if (figurine.isPresent()) return new PokeblocksRecipeResult(Kind.FIGURINE, figurine.get(), flagSet, count);
		throw new IllegalArgumentException("A Pokeblocks recipe result must specify exactly one of: item, decorative, doll, figurine");
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, PokeblocksRecipeResult> STREAM_CODEC =
			StreamCodec.of(PokeblocksRecipeResult::encode, PokeblocksRecipeResult::decode);

	private static void encode(RegistryFriendlyByteBuf buf, PokeblocksRecipeResult result) {
		buf.writeEnum(result.kind);
		buf.writeUtf(result.id);
		buf.writeVarInt(result.flags.size());
		for (ModelFlag flag : result.flags) {
			buf.writeVarInt(flag.ordinal());
		}
		buf.writeVarInt(result.count);
	}

	private static PokeblocksRecipeResult decode(RegistryFriendlyByteBuf buf) {
		Kind kind = buf.readEnum(Kind.class);
		String id = buf.readUtf();
		int flagCount = buf.readVarInt();
		Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
		for (int i = 0; i < flagCount; i++) {
			flags.add(ModelFlag.values()[buf.readVarInt()]);
		}
		int count = buf.readVarInt();
		return new PokeblocksRecipeResult(kind, id, flags, count);
	}
}
