package dev.mrshawn.pokeblocks.generator;

import dev.mrshawn.pokeblocks.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class PokeblocksLoottableGenerator extends FabricBlockLootTableProvider {

	public PokeblocksLoottableGenerator(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generate() {
		ModBlocks.getAllDollBlocks(true).forEach(this::addDrop);
	}

}
