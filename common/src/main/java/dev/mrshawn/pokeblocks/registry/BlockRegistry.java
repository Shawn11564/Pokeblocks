package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.custom.CustomDecorationBlock;
import dev.mrshawn.pokeblocks.block.custom.DigSiteBlock;
import dev.mrshawn.pokeblocks.block.custom.FigurineBlock;
import dev.mrshawn.pokeblocks.block.custom.PokedollBlock;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public final class BlockRegistry {
	private BlockRegistry() {}

	public static void init() {}

	public static final Supplier<PokedollBlock> POKEDOLL_BLOCK = registerBlock(ModSettings.DOLL_ID, PokedollBlock::new);
	public static final Supplier<FigurineBlock> FIGURINE_BLOCK = registerBlock(ModSettings.FIGURINE_ID, FigurineBlock::new);
	public static final Supplier<CustomDecorationBlock> CUSTOM_DECORATION_BLOCK = registerBlock(ModSettings.CUSTOM_DECORATION_ID, CustomDecorationBlock::new);

	/** Phone-call dig site mound (no item form — placed/removed only by the dig quest). */
	public static final Supplier<DigSiteBlock> DIG_SITE_BLOCK = registerBlock("dig_site", DigSiteBlock::new);

	private static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
		return PokeblocksCommon.COMMON_PLATFORM.registerBlock(id, block);
	}
}
