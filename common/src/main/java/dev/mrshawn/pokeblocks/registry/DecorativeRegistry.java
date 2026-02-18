package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeBlock;
import dev.mrshawn.pokeblocks.block.custom.decorative.DecorativeDefinition;
import dev.mrshawn.pokeblocks.block.entity.custom.decorative.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.item.custom.DecorativeItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.*;
import java.util.function.Supplier;

public class DecorativeRegistry {
	public static void init() {}

	public static final List<DecorativeEntry> ALL_ENTRIES = new ArrayList<>();

	private static final Set<ModelFlag> NOTHING = Collections.emptySet();
	private static final Set<ModelFlag> SHINY_ONLY = EnumSet.of(ModelFlag.SHINY);
	private static final Set<ModelFlag> GIGANTIC_ONLY = EnumSet.of(ModelFlag.GIGANTIC);
	private static final Set<ModelFlag> SHINY_AND_GIGANTIC = EnumSet.of(ModelFlag.SHINY, ModelFlag.GIGANTIC);

	public static final DecorativeEntry APPLIN_BASKET = register("applin_basket", "Applin Basket", SHINY_ONLY, false);
	public static final DecorativeEntry EISCUE_HEAD_PILE = register(
			"eiscue_head_pile",
			"Eiscue Head Pile",
			SHINY_AND_GIGANTIC,
			false,
			List.of(new DecorativeDefinition.NbtVariant(
					"headCount",
					"string",
					Map.of(
							"1", "eiscue_head_pile_1",
							"2", "eiscue_head_pile_2",
							"3", "eiscue_head_pile_3"
					),
					"1",
					true
			))
	);
	public static final DecorativeEntry LUVDISC_CUSHION = register("luvdisc_cushion", "Luvdisc Cushion", NOTHING, false, List.of(), true, 0.5);
	public static final DecorativeEntry MAGIKARP_FISHBOWL = register("magikarp_fishbowl", "Magikarp Fishbowl", SHINY_ONLY, true);
	public static final DecorativeEntry POKEMON_TROPHY = register("pokemon_trophy", "Pokemon Trophy", NOTHING, false);

	private static DecorativeEntry register(String id, String displayName, Set<ModelFlag> supportedFlags, boolean hasAnimation) {
		return register(id, displayName, supportedFlags, hasAnimation, List.of(), false, 0.0);
	}

	private static DecorativeEntry register(String id, String displayName, Set<ModelFlag> supportedFlags, boolean hasAnimation, List<DecorativeDefinition.NbtVariant> nbtVariants) {
		return register(id, displayName, supportedFlags, hasAnimation, nbtVariants, false, 0.0);
	}

	private static DecorativeEntry register(String id, String displayName, Set<ModelFlag> supportedFlags, boolean hasAnimation, List<DecorativeDefinition.NbtVariant> nbtVariants, boolean sittable, double seatHeight) {
		DecorativeDefinition definition = new DecorativeDefinition(id, displayName, id, supportedFlags, hasAnimation, nbtVariants, sittable, seatHeight);

		final Supplier<BlockEntityType<DecorativeBlockEntity>>[] beTypeHolder = new Supplier[1];

		Supplier<DecorativeBlock> block = PokeblocksCommon.COMMON_PLATFORM.registerBlock(
				id, () -> new DecorativeBlock(() -> beTypeHolder[0].get())
		);

		Supplier<BlockEntityType<DecorativeBlockEntity>> blockEntityType = PokeblocksCommon.COMMON_PLATFORM.registerBlockEntity(
				id, () -> BlockEntityType.Builder.of(
						(pos, state) -> new DecorativeBlockEntity(beTypeHolder[0].get(), pos, state, definition),
						block.get()
				).build(null)
		);

		beTypeHolder[0] = blockEntityType;

		@SuppressWarnings("unchecked")
		Supplier<DecorativeItem> item = (Supplier<DecorativeItem>) (Supplier<?>) PokeblocksCommon.COMMON_PLATFORM.registerItem(
				id, () -> new DecorativeItem(block.get(), new Item.Properties(), definition)
		);

		DecorativeEntry entry = new DecorativeEntry(definition, block, blockEntityType, item);
		ALL_ENTRIES.add(entry);
		return entry;
	}

	public record DecorativeEntry(
			DecorativeDefinition definition,
			Supplier<DecorativeBlock> block,
			Supplier<BlockEntityType<DecorativeBlockEntity>> blockEntityType,
			Supplier<DecorativeItem> item
	) {}
}