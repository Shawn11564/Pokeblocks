package com.example.examplemod.data.datafixer.fixes;

import com.example.examplemod.ExampleModCommon;
import com.example.examplemod.data.TempPokedollTypes;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.References;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;

public class PreservePokedollItemsFix extends DataFix {
    private final String name;

    public PreservePokedollItemsFix(Schema outputSchema, String name) {
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

                Matcher matcher = TempPokedollTypes.PATTERN.matcher(id.get());
                if (!matcher.find()) return dynamic;

                boolean gigantic = matcher.group(1) != null;
                boolean shiny = matcher.group(2) != null;
                String pokemon = Objects.requireNonNull(matcher.group(3));
                boolean posed = matcher.group(4) != null;
                boolean animated = matcher.group(5) != null;

                var components = dynamic.get("components")
                    .orElseEmptyMap();

                var blockEntityData = components.get("minecraft:block_entity_data")
                    .orElseEmptyMap();

                blockEntityData = blockEntityData
                    .set("id", dynamic.createString(ExampleModCommon.MOD_ID+":pokedoll"))
                    .set("pokemon", dynamic.createString(pokemon))
                    .set("animated", dynamic.createBoolean(animated))
                    .set("gigantic", dynamic.createBoolean(gigantic))
                    .set("shiny", dynamic.createBoolean(shiny))
                    .set("posed", dynamic.createBoolean(posed));

                components = components.set("minecraft:block_entity_data", blockEntityData);
                dynamic = dynamic.set("components", components);

                return dynamic;
            })
        );
    }
}