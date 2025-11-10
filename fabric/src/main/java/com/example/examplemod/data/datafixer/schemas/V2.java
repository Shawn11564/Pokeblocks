package com.example.examplemod.data.datafixer.schemas;

import com.example.examplemod.ExampleModCommon;
import com.example.examplemod.data.TempPokedollTypes;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

public class V2 extends NamespacedSchema {
    public V2(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
        Map<String, Supplier<TypeTemplate>> blockEntities = super.registerBlockEntities(schema);

        TempPokedollTypes.forAllFullTypes(t -> blockEntities.remove("pokeblocks:" + t));
        registerSimple(blockEntities, ExampleModCommon.MOD_ID + ":pokedoll");

        return blockEntities;
    }
}
