package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.FigurineBlock;
import dev.mrshawn.pokeblocks.block.PokedollBlock;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public final class BlockRegistry {
	public static void init() {}

	public static final Supplier<PokedollBlock> POKEDOLL_BLOCK = registerBlock(ModSettings.DOLL_ID, PokedollBlock::new);
	public static final Supplier<FigurineBlock> FIGURINE_BLOCK = registerBlock(ModSettings.FIGURINE_ID, FigurineBlock::new);

	private static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
		return PokeblocksCommon.COMMON_PLATFORM.registerBlock(id, block);
	}
}
