package dev.mrshawn.pokeblocks.data;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

// NOTE: you'll probably want to redo this once you port more of your existing registration logic
public class TempPokedollTypes {
    public static final Pattern PATTERN = Pattern.compile("pokeblocks:(gigantic_)?pokedoll_(shiny_)?(\\w+?)(?=_posed|_animated|$)(_posed)?(_animated)?");

    private TempPokedollTypes() {}

    // FIXME: add all pokedoll types to this list
    public static final List<String> TYPES = List.of(
        "charmander",
        "bulbasaur"
    );

    public static void forAllFullTypes(Consumer<String> consumer) {
        for (String type : TYPES) {
            for (boolean shiny : new boolean[]{false, true}) {
                for (boolean gigantic : new boolean[]{false, true}) {
                    for (String posedAnimated : new String[]{"", "_posed", "_animated"}) {
                        StringBuilder fullType = new StringBuilder();
                        if (gigantic) {
                            fullType.append("gigantic_");
                        }
                        fullType.append("pokedoll_");
                        if (shiny) {
                            fullType.append("shiny_");
                        }
                        fullType.append(type);
                        fullType.append(posedAnimated);

                        consumer.accept(fullType.toString());
                    }
                }
            }
        }
    }
}
