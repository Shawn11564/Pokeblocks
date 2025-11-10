package dev.mrshawn.pokeblocks.data;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.data.datafixer.fixes.PreservePokedollBlockEntitiesFix;
import dev.mrshawn.pokeblocks.data.datafixer.fixes.PreservePokedollItemsFix;
import dev.mrshawn.pokeblocks.data.datafixer.schemas.V1;
import dev.mrshawn.pokeblocks.data.datafixer.schemas.V2;
import dev.mrshawn.pokeblocks.data.datafixerapi.DataFixesInternals;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.BlockEntityRenameFix;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.ItemRenameFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;

import static dev.mrshawn.pokeblocks.data.datafixerapi.DataFixesInternals.BASE_SCHEMA;

public class PokeblocksDataFixers {
    private static final BiFunction<Integer, Schema, Schema> SAME = Schema::new;
    private static final BiFunction<Integer, Schema, Schema> SAME_NAMESPACED = NamespacedSchema::new;

    public static void register() {
        PokeblocksCommon.LOGGER.info("Registering data fixers");

        DataFixesInternals api = DataFixesInternals.get();

        DataFixerBuilder builder = new DataFixerBuilder(PokeblocksCommon.DATA_FIXER_VERSION);
        addFixers(builder);

        ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Pokeblocks Datafixer Bootstrap").setDaemon(true).setPriority(1).build());
        api.registerFixer(PokeblocksCommon.DATA_FIXER_VERSION, builder.build().fixer());
    }

    private static void addFixers(DataFixerBuilder builder) {
        // Register a schema, and then the fixes to get *to* that schema

        // vanilla latest: do not add fixers to this, the system assumes pre-datafixer worlds are on mod schema 1
        builder.addSchema(0, BASE_SCHEMA);

        // the mod's base: add anything to this that existed pre-datafixers
        Schema schemaV1 = builder.addSchema(1, V1::new);
        builder.addFixer(new AddNewChoices(schemaV1, "Added Pokedolls", References.BLOCK_ENTITY));

        // For v2, need to upgrade pokeblocks:<old_doll_ids> to pokeblocks:pokedoll[pokemon=<old_doll_id>]
        Schema schemaV2 = builder.addSchema(2, V2::new);
        builder.addFixer(new PreservePokedollBlockEntitiesFix(schemaV2, "Convert pokeblocks:pokedoll_(?<type>.*) into pokeblocks:pokedoll_(?<type>.*){pokemon=<type>,...}"));
        builder.addFixer(new PreservePokedollItemsFix(schemaV2, "Convert pokeblocks:pokedoll_(?<type>.*) into pokeblocks:pokedoll_(?<type>.*){BlockEntityData={pokemon=<type>,...}}"));
        UnaryOperator<String> dollRenamer = oldName -> {
            oldName = NamespacedSchema.ensureNamespaced(oldName);
            Matcher matcher = TempPokedollTypes.PATTERN.matcher(oldName);
            if (matcher.find()) {
                return PokeblocksCommon.MOD_ID+":pokedoll";
            }
            return oldName;
        };
        builder.addFixer(BlockEntityRenameFix.create(schemaV2, "Convert pokeblocks:pokedoll_.* into pokeblocks:pokedoll", dollRenamer));
        builder.addFixer(BlockRenameFix.create(schemaV2, "Convert pokeblocks:pokedoll_.* into pokeblocks:pokedoll", dollRenamer));
        builder.addFixer(ItemRenameFix.create(schemaV2, "Convert pokeblocks:pokedoll_.* into pokeblocks:pokedoll", dollRenamer));

        // the below is to ensure we don't get out of sync with ExampleModCommon.DATA_FIXER_VERSION
        //noinspection ConstantValue
        assert 2 == PokeblocksCommon.DATA_FIXER_VERSION : "DATA_FIXER_VERSION does not match the latest schema version!";
    }
}
