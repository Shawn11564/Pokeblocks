package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.generator.PokeblocksLoottableGenerator;
import dev.mrshawn.pokeblocks.generator.PokeblocksRecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class PokeblocksDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(PokeblocksRecipeGenerator::new);
		pack.addProvider(PokeblocksLoottableGenerator::new);
	}

}
