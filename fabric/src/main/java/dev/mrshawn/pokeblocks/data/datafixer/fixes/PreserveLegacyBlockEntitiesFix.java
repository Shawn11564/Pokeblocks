package dev.mrshawn.pokeblocks.data.datafixer.fixes;

import dev.mrshawn.pokeblocks.data.LegacyIdMigrator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

/**
 * Injects the new variant NBT (pokemon/figurine + true flags) onto every placed legacy doll, figurine
 * and decorative block-entity, derived from its old id via {@link LegacyIdMigrator}. The id itself is
 * left as-is here and renamed afterwards by {@code BlockEntityRenameFix} (this fix must run first so it
 * can read the original id).
 */
public class PreserveLegacyBlockEntitiesFix extends DataFix {
    private final String name;

    public PreserveLegacyBlockEntitiesFix(Schema outputSchema, String name) {
        super(outputSchema, true);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(
            this.name + " for block_entity",
            this.getInputSchema().findChoiceType(References.BLOCK_ENTITY),
            typed -> typed.update(DSL.remainderFinder(), dynamic -> {
                Optional<String> id = dynamic.get("id").asString().result();
                if (id.isEmpty()) return dynamic;

                Optional<LegacyIdMigrator.LegacyVariant> variant = LegacyIdMigrator.migrate(id.get());
                if (variant.isEmpty()) return dynamic;

                dynamic = LegacyIdMigrator.mergeVariant(dynamic, variant.get());
                dynamic = LegacyIdMigrator.convertHeadPileCount(dynamic, variant.get());
                return dynamic;
            })
        );
    }
}
