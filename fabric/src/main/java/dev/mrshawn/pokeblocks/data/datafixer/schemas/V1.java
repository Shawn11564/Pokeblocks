package dev.mrshawn.pokeblocks.data.datafixer.schemas;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.data.LegacyPokeblocks;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

/**
 * The mod's pre-datafixer baseline: every legacy block-entity id that existed in the old, unique-id
 * registration system. Teaching the DFU type system about these ids lets the v2 fixes read and rename
 * them.
 */
public class V1 extends NamespacedSchema {
    public V1(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> blockEntities = super.registerBlockEntities(schema);

        for (String id : LegacyPokeblocks.LEGACY_BLOCK_ENTITY_IDS) {
            registerSimple(blockEntities, PokeblocksCommon.MOD_ID + ":" + id);
        }

        return blockEntities;
    }
}
