package dev.mrshawn.pokeblocks.data.datafixer.fixes;

import dev.mrshawn.pokeblocks.data.LegacyIdMigrator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

/**
 * Writes the new variant NBT onto every legacy doll, figurine and decorative item stack as the
 * {@code minecraft:block_entity_data} component, so the picked/placed result matches the canonical
 * minimal tag from {@code PokeblocksItemData} and converted stacks stack with freshly-given ones.
 * The item id itself is renamed afterwards by {@code ItemRenameFix} (this fix runs first to read the
 * original id). The written {@code block_entity_data.id} is the new block-entity id from the migrator.
 */
public class PreserveLegacyItemsFix extends DataFix {
    private final String name;

    public PreserveLegacyItemsFix(Schema outputSchema, String name) {
        super(outputSchema, true);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(
            this.name + " for item_stack",
            this.getInputSchema().getType(References.ITEM_STACK),
            typed -> typed.update(DSL.remainderFinder(), dynamic -> {
                Optional<String> id = dynamic.get("id").asString().result();
                if (id.isEmpty()) return dynamic;

                Optional<LegacyIdMigrator.LegacyVariant> variant = LegacyIdMigrator.migrate(id.get());
                if (variant.isEmpty()) return dynamic;
                LegacyIdMigrator.LegacyVariant v = variant.get();

                Dynamic<?> components = dynamic.get("components").orElseEmptyMap();
                Dynamic<?> blockEntityData = components.get("minecraft:block_entity_data").orElseEmptyMap();

                blockEntityData = blockEntityData.set("id", blockEntityData.createString(v.newId()));
                blockEntityData = LegacyIdMigrator.mergeVariant(blockEntityData, v);
                blockEntityData = LegacyIdMigrator.convertHeadPileCount(blockEntityData, v);

                components = components.set("minecraft:block_entity_data", blockEntityData);
                return dynamic.set("components", components);
            })
        );
    }
}
