package dev.mrshawn.pokeblocks.data;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.data.datafixer.fixes.PokedollFacingToRotationFix;
import dev.mrshawn.pokeblocks.data.datafixer.fixes.PreserveLegacyBlockEntitiesFix;
import dev.mrshawn.pokeblocks.data.datafixer.fixes.PreserveLegacyItemsFix;
import dev.mrshawn.pokeblocks.data.datafixer.schemas.V1;
import dev.mrshawn.pokeblocks.data.datafixer.schemas.V2;
import dev.mrshawn.pokeblocks.data.datafixerapi.DataFixesInternals;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.BlockEntityRenameFix;
import net.minecraft.util.datafix.fixes.BlockRenameFix;
import net.minecraft.util.datafix.fixes.ItemRenameFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.function.UnaryOperator;

import static dev.mrshawn.pokeblocks.data.datafixerapi.DataFixesInternals.BASE_SCHEMA;

public class PokeblocksDataFixers {

    public static void register() {
        PokeblocksCommon.LOGGER.info("Registering data fixers");

        DataFixesInternals api = DataFixesInternals.get();

        DataFixerBuilder builder = new DataFixerBuilder(PokeblocksCommon.DATA_FIXER_VERSION);
        addFixers(builder);

        api.registerFixer(PokeblocksCommon.DATA_FIXER_VERSION, builder.build().fixer());
    }

    private static void addFixers(DataFixerBuilder builder) {
        // Register a schema, and then the fixes to get *to* that schema

        // vanilla latest: do not add fixers to this, the system assumes pre-datafixer worlds are on mod schema 1
        builder.addSchema(0, BASE_SCHEMA);

        // the mod's base: every legacy (unique-id) block entity that existed pre-datafixers
        Schema schemaV1 = builder.addSchema(1, V1::new);
        builder.addFixer(new AddNewChoices(schemaV1, "Added legacy Pokeblocks block entities", References.BLOCK_ENTITY));

        // v2: collapse all legacy doll/figurine/decorative ids into the new dynamic ids + NBT.
        Schema schemaV2 = builder.addSchema(2, V2::new);

        // 1) Inject the derived variant NBT first, while the original (legacy) ids are still readable.
        builder.addFixer(new PreserveLegacyBlockEntitiesFix(schemaV2, "Preserve legacy Pokeblocks block-entity variants"));
        builder.addFixer(new PreserveLegacyItemsFix(schemaV2, "Preserve legacy Pokeblocks item variants"));

        // 2) Then rename the ids themselves to the new unified ids.
        UnaryOperator<String> renamer = oldName -> {
            String ensured = NamespacedSchema.ensureNamespaced(oldName);
            return LegacyIdMigrator.migrate(ensured)
                .map(LegacyIdMigrator.LegacyVariant::newId)
                .orElse(oldName);
        };
        builder.addFixer(BlockEntityRenameFix.create(schemaV2, "Rename legacy Pokeblocks block entities", renamer));
        builder.addFixer(BlockRenameFix.create(schemaV2, "Rename legacy Pokeblocks blocks", renamer));
        builder.addFixer(ItemRenameFix.create(schemaV2, "Rename legacy Pokeblocks items", renamer));

        // v3: the pokedoll block swapped its 4-way `facing` property for a 16-step `rotation` (sign-style
        // placement). Convert the property on already-placed dolls so they keep their orientation. No new
        // types are introduced, so a plain NamespacedSchema suffices. Runs after the v2 rename above, so the
        // doll is already the unified `pokeblocks:pokedoll` block by this point.
        Schema schemaV3 = builder.addSchema(3, NamespacedSchema::new);
        builder.addFixer(new PokedollFacingToRotationFix(schemaV3, "Convert pokedoll facing to 16-step rotation"));

        // the below is to ensure we don't get out of sync with PokeblocksCommon.DATA_FIXER_VERSION
        //noinspection ConstantValue
        assert 3 == PokeblocksCommon.DATA_FIXER_VERSION : "DATA_FIXER_VERSION does not match the latest schema version!";
    }
}
