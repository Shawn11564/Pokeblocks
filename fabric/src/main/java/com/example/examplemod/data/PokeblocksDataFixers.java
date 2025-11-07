package com.example.examplemod.data;

import com.example.examplemod.ExampleModCommon;
import com.example.examplemod.data.datafixer.OldPokedollIdFix;
import com.example.examplemod.data.datafixerapi.DataFixesInternals;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

import static com.example.examplemod.data.datafixerapi.DataFixesInternals.BASE_SCHEMA;

public class PokeblocksDataFixers {

	private static final BiFunction<Integer, Schema, Schema> SAME = Schema::new;
	private static final BiFunction<Integer, Schema, Schema> SAME_NAMESPACED = NamespacedSchema::new;

	public static void register() {
		ExampleModCommon.LOGGER.info("Registering data fixers");

		DataFixesInternals api = DataFixesInternals.get();

		DataFixerBuilder builder = new DataFixerBuilder(ExampleModCommon.DATA_FIXER_VERSION);
		addFixers(builder);

		ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Pokeblocks Datafixer Bootstrap").setDaemon(true).setPriority(1).build());
		api.registerFixer(ExampleModCommon.DATA_FIXER_VERSION, builder.build().fixer());
	}

	private static void addFixers(DataFixerBuilder builder) {
		builder.addSchema(0, BASE_SCHEMA);

		// Register a schema, and then the fixes to get *to* that schema

		// For v1, need to upgrade pokeblocks:<old_doll_ids> to pokeblocks:pokedoll[pokemon=<old_doll_id>]
		Schema schemaV1 = builder.addSchema(1, SAME_NAMESPACED);
		builder.addFixer(new OldPokedollIdFix(schemaV1, "Convert pokeblocks:<old_doll_ids> into pokeblocks:pokedoll[pokemon=\"<old_doll_id>\"]"));
		ExampleModCommon.LOGGER.info("Added schema: {}", schemaV1);
	}

}
