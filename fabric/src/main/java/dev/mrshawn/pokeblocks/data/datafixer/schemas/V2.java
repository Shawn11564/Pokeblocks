package dev.mrshawn.pokeblocks.data.datafixer.schemas;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.data.LegacyPokeblocks;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

/**
 * The new, unified registration system: the per-variant legacy block-entity ids are gone, replaced by
 * the handful of dynamic block entities ({@code pokedoll}, {@code figurine} and the decoratives) whose
 * variant is driven by NBT.
 */
public class V2 extends NamespacedSchema {
    public V2(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> blockEntities = super.registerBlockEntities(schema);

        for (String id : LegacyPokeblocks.LEGACY_BLOCK_ENTITY_IDS) {
            blockEntities.remove(PokeblocksCommon.MOD_ID + ":" + id);
        }
        for (String id : LegacyPokeblocks.NEW_BLOCK_ENTITY_IDS) {
            registerSimple(blockEntities, PokeblocksCommon.MOD_ID + ":" + id);
        }

        return blockEntities;
    }
}
