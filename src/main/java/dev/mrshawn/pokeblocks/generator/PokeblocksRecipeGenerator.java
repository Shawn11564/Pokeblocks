package dev.mrshawn.pokeblocks.generator;

import dev.mrshawn.pokeblocks.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public class PokeblocksRecipeGenerator extends FabricRecipeProvider {

	private final List<String> blacklist = List.of(
			"APPLIN",
			"FISHBOWL",
			"TROPHY",
			"FIGURINE"
	);

	public PokeblocksRecipeGenerator(FabricDataOutput generator) {
		super(generator);
	}

	@Override
	public void generate(Consumer<RecipeJsonProvider> exporter) {
		// Loop through all fields in ModBlocks
		for (var field : ModBlocks.class.getDeclaredFields()) {
			try {
				// Check if the field is a regular Pokedoll block
				if (field.getName().startsWith("POKEDOLL_") && !field.getName().contains("GIGANTIC") && blacklist.stream().noneMatch(field.getName()::contains)) {
					Block block = (Block) field.get(null);

					String pokemonName = block.getRegistryEntry().getKey().get().getValue().getPath()
							.replace("pokedoll_", "")
							.replace(" Pokedoll", "")
							.replace(" ", "_")
							.toLowerCase();

					System.out.println("Generating recipe for " + pokemonName);

					// Create a shaped recipe for the gigantic Pokedoll
					ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.getGiganticPokedollBlock(pokemonName))
							.pattern(" d ")
							.pattern("dwd")
							.pattern(" d ")
							.input('d', block)
							.input('w', ItemTags.WOOL)
							.criterion(FabricRecipeProvider.hasItem(block),
									FabricRecipeProvider.conditionsFromItem(block))
							.offerTo(exporter, new Identifier("pokeblocks", "gigantic_pokedoll_" + pokemonName));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
