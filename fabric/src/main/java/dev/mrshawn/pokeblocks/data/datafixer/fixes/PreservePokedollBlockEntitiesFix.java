package dev.mrshawn.pokeblocks.data.datafixer.fixes;

import dev.mrshawn.pokeblocks.data.TempPokedollTypes;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.References;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;

public class PreservePokedollBlockEntitiesFix extends DataFix {
    private final String name;

    public PreservePokedollBlockEntitiesFix(Schema outputSchema, String name) {
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

                Matcher matcher = TempPokedollTypes.PATTERN.matcher(id.get());
                if (!matcher.find()) return dynamic;

                boolean gigantic = matcher.group(1) != null;
                boolean shiny = matcher.group(2) != null;
                String pokemon = Objects.requireNonNull(matcher.group(3));
                boolean posed = matcher.group(4) != null;
                boolean animated = matcher.group(5) != null;

                dynamic = dynamic
                    .set("pokemon", dynamic.createString(pokemon))
                    .set("animated", dynamic.createBoolean(animated))
                    .set("gigantic", dynamic.createBoolean(gigantic))
                    .set("shiny", dynamic.createBoolean(shiny))
                    .set("posed", dynamic.createBoolean(posed));

                return dynamic;
            })
        );
    }
}