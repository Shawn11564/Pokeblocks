package dev.mrshawn.pokeblocks.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.ItemRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A crafting ingredient that can match either a vanilla item/tag (via {@link Ingredient})
 * or a specific Pokeblocks doll variant.
 * <p>
 * Dolls are not distinct registered items — every doll is the single {@code pokeblocks:pokedoll}
 * item with its species and variant stored in data components. A plain vanilla ingredient therefore
 * cannot tell an "Applin Doll" apart from a "Shiny Applin Doll". This class adds a {@code doll}
 * matcher that compares the species and the exact flag set stored on the stack.
 * <p>
 * JSON forms:
 * <pre>
 *   { "item": "minecraft:pink_wool" }      // a specific item
 *   { "tag": "minecraft:planks" }          // any item in a tag
 *   { "doll": "applin" }                   // an Applin doll with no flags
 *   { "doll": "applin", "flags": ["shiny"] } // a Shiny Applin doll
 * </pre>
 */
public final class PokeblocksIngredient {

	/** Shared codec mapping a flag's tag name (e.g. {@code "shiny"}) to a {@link ModelFlag}. */
	public static final Codec<ModelFlag> FLAG_CODEC = Codec.STRING.comapFlatMap(
			name -> {
				ModelFlag flag = ModelFlag.fromTagName(name);
				return flag != null
						? DataResult.success(flag)
						: DataResult.error(() -> "Unknown doll flag '" + name + "'. Valid flags: " + ModelFlag.allTagNames());
			},
			ModelFlag::getTagName
	);

	// Exactly one of these is set: a vanilla ingredient, or a doll species (+ required flags).
	private final Ingredient vanilla;       // nullable
	private final String dollPokemon;       // nullable
	private final Set<ModelFlag> dollFlags; // non-null only when dollPokemon != null

	private PokeblocksIngredient(Ingredient vanilla, String dollPokemon, Set<ModelFlag> dollFlags) {
		this.vanilla = vanilla;
		this.dollPokemon = dollPokemon;
		this.dollFlags = dollFlags;
	}

	public static PokeblocksIngredient vanilla(Ingredient ingredient) {
		return new PokeblocksIngredient(ingredient, null, null);
	}

	public static PokeblocksIngredient doll(String pokemon, Set<ModelFlag> flags) {
		Set<ModelFlag> copy = EnumSet.noneOf(ModelFlag.class);
		copy.addAll(flags);
		return new PokeblocksIngredient(null, pokemon.toLowerCase(Locale.ROOT), copy);
	}

	public boolean isDoll() {
		return dollPokemon != null;
	}

	/**
	 * Every stack this ingredient accepts, for display in recipe viewers (JEI cycles through them).
	 * A doll ingredient yields exactly one stack — the species with its exact flag set, i.e. the same
	 * stack {@link #test} matches; a vanilla ingredient yields its {@link Ingredient#getItems()}.
	 */
	public List<ItemStack> displayStacks() {
		if (isDoll()) {
			return List.of(PokedollItem.createPokedoll(dollPokemon, dollFlags.toArray(new ModelFlag[0])));
		}
		return List.of(vanilla.getItems());
	}

	/** Tests whether the given stack satisfies this ingredient. */
	public boolean test(ItemStack stack) {
		if (isDoll()) {
			if (stack.isEmpty() || !stack.is(ItemRegistry.POKEDOLL_ITEM.get())) return false;
			if (!PokedollItem.getPokemonFromStack(stack).equalsIgnoreCase(dollPokemon)) return false;
			// Exact flag match so "Applin Doll" ({}) and "Shiny Applin Doll" ({shiny}) stay distinct.
			return PokedollItem.getFlagsFromStack(stack).equals(dollFlags);
		}
		return vanilla.test(stack);
	}

	// --- Codecs ---

	private static final Codec<PokeblocksIngredient> DOLL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("doll").forGetter(ingredient -> ingredient.dollPokemon),
			FLAG_CODEC.listOf().optionalFieldOf("flags", List.of()).forGetter(ingredient -> new ArrayList<>(ingredient.dollFlags))
	).apply(instance, (pokemon, flags) -> {
		Set<ModelFlag> set = EnumSet.noneOf(ModelFlag.class);
		set.addAll(flags);
		return doll(pokemon, set);
	}));

	/** Tries the doll form first; anything else is parsed as a vanilla item/tag ingredient. */
	public static final Codec<PokeblocksIngredient> CODEC = Codec.either(DOLL_CODEC, Ingredient.CODEC).xmap(
			either -> either.map(doll -> doll, PokeblocksIngredient::vanilla),
			ingredient -> ingredient.isDoll() ? Either.left(ingredient) : Either.right(ingredient.vanilla)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, PokeblocksIngredient> STREAM_CODEC =
			StreamCodec.of(PokeblocksIngredient::encode, PokeblocksIngredient::decode);

	private static void encode(RegistryFriendlyByteBuf buf, PokeblocksIngredient ingredient) {
		buf.writeBoolean(ingredient.isDoll());
		if (ingredient.isDoll()) {
			buf.writeUtf(ingredient.dollPokemon);
			buf.writeVarInt(ingredient.dollFlags.size());
			for (ModelFlag flag : ingredient.dollFlags) {
				buf.writeVarInt(flag.ordinal());
			}
		} else {
			Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient.vanilla);
		}
	}

	private static PokeblocksIngredient decode(RegistryFriendlyByteBuf buf) {
		if (buf.readBoolean()) {
			String pokemon = buf.readUtf();
			int count = buf.readVarInt();
			Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
			for (int i = 0; i < count; i++) {
				flags.add(ModelFlag.values()[buf.readVarInt()]);
			}
			return doll(pokemon, flags);
		}
		return vanilla(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
	}
}
