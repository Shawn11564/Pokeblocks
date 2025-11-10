package dev.mrshawn.pokeblocks.data.datafixer.schemas;

import dev.mrshawn.pokeblocks.data.TempPokedollTypes;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

public class V1 extends NamespacedSchema {
    public V1(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> blockEntities = super.registerBlockEntities(schema);

        TempPokedollTypes.forAllFullTypes(t -> registerSimple(blockEntities, "pokeblocks:" + t));

        return blockEntities;
    }
}
