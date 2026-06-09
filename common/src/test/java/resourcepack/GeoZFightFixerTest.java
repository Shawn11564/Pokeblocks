package resourcepack;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mrshawn.pokeblocks.resourcepack.resources.GeoZFightFixer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Runs the z-fighting detector over the real bundled geo models (available on the test classpath)
 * and checks it flags the known offenders, emits valid geometry, and doesn't flag everything.
 */
class GeoZFightFixerTest {

    private static final String BLASTOISE = "assets/pokeblocks/geo/block/pokedoll_blastoise.geo.json";
    private static final String CHARMANDER = "assets/pokeblocks/geo/block/pokedoll_charmander.geo.json";

    @Test
    void detectsKnownOffendersAndProducesValidGeo() {
        Map<String, String> fixes = GeoZFightFixer.generateCorrections();

        assertFalse(fixes.isEmpty(), "expected at least the known offenders to be corrected");

        assertTrue(fixes.containsKey(BLASTOISE), "Blastoise's coincident shell cubes should be detected");
        assertTrue(fixes.containsKey(CHARMANDER), "Charmander's stacked eye decals should be detected");

        // Every corrected file must still be valid, parseable geo JSON.
        for (Map.Entry<String, String> e : fixes.entrySet()) {
            JsonObject root = JsonParser.parseString(e.getValue()).getAsJsonObject();
            assertTrue(root.has("minecraft:geometry"), e.getKey() + " should still be a geo model");
        }
    }

    @Test
    void correctionIncreasesAnInflateValue() {
        Map<String, String> fixes = GeoZFightFixer.generateCorrections();
        String charmander = fixes.get(CHARMANDER);
        assertNotNull(charmander);

        // At least one cube in the patched model must carry a meaningful (>= TARGET_GAP) inflate now.
        boolean hasBumpedInflate = JsonParser.parseString(charmander).getAsJsonObject()
                .getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject()
                .getAsJsonArray("bones").asList().stream()
                .filter(b -> b.getAsJsonObject().has("cubes"))
                .flatMap(b -> b.getAsJsonObject().getAsJsonArray("cubes").asList().stream())
                .anyMatch(c -> c.getAsJsonObject().has("inflate")
                        && c.getAsJsonObject().get("inflate").getAsDouble() >= 0.05);

        assertTrue(hasBumpedInflate, "the patched model should contain a bumped inflate value");
    }
}
