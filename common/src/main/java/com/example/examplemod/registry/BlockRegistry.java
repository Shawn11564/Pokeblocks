package com.example.examplemod.registry;

import com.example.examplemod.ExampleModCommon;
import com.example.examplemod.block.PokedollBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public final class BlockRegistry {
	public static void init() {}

	public static final Supplier<PokedollBlock> POKEDOLL_BLOCK = registerBlock("pokedoll", PokedollBlock::new);
	public static final Supplier<Block> TESTBLOCK = registerBlock("testblock", () ->
			new Block(BlockBehaviour.Properties.of().strength(1.5f))
	);

	private static <T extends Block> Supplier<T> registerBlock(String id, Supplier<T> block) {
		return ExampleModCommon.COMMON_PLATFORM.registerBlock(id, block);
	}
}
