package dev.mrshawn.pokeblocks.generator;

import dev.mrshawn.pokeblocks.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class PokeblocksLoottableGenerator extends FabricBlockLootTableProvider {

	public PokeblocksLoottableGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
		super(dataOutput, registryLookup);
	}

	@Override
	public void generate() {
		ModBlocks.getAllDollBlocks(true).forEach(this::addDrop);
	}

}
