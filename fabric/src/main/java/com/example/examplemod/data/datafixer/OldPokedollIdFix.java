package com.example.examplemod.data.datafixer;

import com.example.examplemod.ExampleModCommon;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.fixes.References;

import java.util.Optional;

public class OldPokedollIdFix extends DataFix {

    private final String name;

    public OldPokedollIdFix(Schema outputSchema, String name) {
        super(outputSchema, false);
        this.name = name;
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(References.BLOCK_STATE), typed -> typed.update(DSL.remainderFinder(), dynamic -> {
            Optional<String> optional = dynamic.get("Name").asString().result();
//			ExampleModCommon.LOGGER.info("OldPokedollIdFix checking Name: {}", optional.orElse("none"));
			if (optional.isPresent() && optional.get().equals("pokeblocks:pokedoll_bulbasaur")) {
				ExampleModCommon.LOGGER.info("Got hit: {}", optional.get());
                dynamic = dynamic.set("Name", dynamic.createString(ExampleModCommon.MOD_ID + ":pokedoll"));

                Dynamic<?> properties = dynamic.get("Properties").orElseEmptyMap()
                    .set("pokemon", dynamic.createString("bulbasaur"));

                dynamic = dynamic.set("Properties", properties);
                return dynamic;
            }

            return dynamic;
        }));
    }
}