package config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.mrshawn.pokeblocks.config.ConfigMergers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Locks in the three-way merge semantics ConfigSync relies on: new defaults flow in, admin
 * customizations and deletions survive, and upstream changes only override untouched entries.
 */
class ConfigMergersTest {

    private static List<String> entries(String json) {
        List<String> out = new ArrayList<>();
        for (var el : JsonParser.parseString(json).getAsJsonArray()) out.add(el.getAsString());
        return out;
    }

    // ---- string-array (doll_rarity style: key = all but last token) ----

    @Test
    void addsNewDefaultEntries() {
        String base = "[\"bulbasaur common\"]";
        String mine = "[\"bulbasaur common\"]";
        String theirs = "[\"bulbasaur common\", \"gible rare\"]";

        ConfigMergers.Outcome out = ConfigMergers.mergeStringArray(base, mine, theirs, ConfigMergers::keyAllButLast);

        assertTrue(out.changed());
        List<String> result = entries(out.content());
        assertTrue(result.contains("gible rare"), "new default should be added");
        assertEquals(1, out.added().size());
    }

    @Test
    void keepsAdminCustomEntries() {
        String base = "[\"bulbasaur common\"]";
        String mine = "[\"bulbasaur common\", \"mypokemon legendary\"]"; // admin custom
        String theirs = "[\"bulbasaur common\", \"gible rare\"]";

        ConfigMergers.Outcome out = ConfigMergers.mergeStringArray(base, mine, theirs, ConfigMergers::keyAllButLast);

        List<String> result = entries(out.content());
        assertTrue(result.contains("mypokemon legendary"), "admin custom entry must survive");
        assertTrue(result.contains("gible rare"), "new default still added");
    }

    @Test
    void respectsAdminDeletionOfDefault() {
        String base = "[\"bulbasaur common\", \"squirtle common\"]";
        String mine = "[\"bulbasaur common\"]"; // admin deleted squirtle
        String theirs = "[\"bulbasaur common\", \"squirtle common\"]"; // still a default

        ConfigMergers.Outcome out = ConfigMergers.mergeStringArray(base, mine, theirs, ConfigMergers::keyAllButLast);

        assertFalse(out.changed(), "a deletion the admin made (already in base) must not be re-added");
    }

    @Test
    void updatesUntouchedEntryWhenDefaultChanges() {
        String base = "[\"bulbasaur common\"]";
        String mine = "[\"bulbasaur common\"]";        // admin left it alone
        String theirs = "[\"bulbasaur uncommon\"]";    // rebalanced upstream

        ConfigMergers.Outcome out = ConfigMergers.mergeStringArray(base, mine, theirs, ConfigMergers::keyAllButLast);

        assertTrue(out.changed());
        assertEquals(List.of("bulbasaur uncommon"), entries(out.content()));
        assertEquals(1, out.updated().size());
    }

    @Test
    void doesNotOverrideAdminCustomizedValue() {
        String base = "[\"bulbasaur common\"]";
        String mine = "[\"bulbasaur legendary\"]";   // admin changed the rarity
        String theirs = "[\"bulbasaur uncommon\"]";  // upstream changed it differently

        ConfigMergers.Outcome out = ConfigMergers.mergeStringArray(base, mine, theirs, ConfigMergers::keyAllButLast);

        assertFalse(out.changed(), "admin's customized value wins over an upstream change");
    }

    @Test
    void firstRunWithoutBaselineBackfillsMissingDefaults() {
        // base == null simulates first adoption (no snapshot yet) → additive backfill, no deletes.
        String mine = "[\"bulbasaur common\"]";
        String theirs = "[\"bulbasaur common\", \"gible rare\", \"cubone uncommon\"]";

        ConfigMergers.Outcome out = ConfigMergers.mergeStringArray(null, mine, theirs, ConfigMergers::keyAllButLast);

        assertTrue(out.changed());
        assertEquals(2, out.added().size());
    }

    @Test
    void invalidValuesNotDuplicatedWhenAdminAlreadyHasKey() {
        // Admin already has the doll under a custom rarity; the new default for the same key
        // must not be appended as a duplicate.
        String base = "[]";
        String mine = "[\"gible epic\"]";
        String theirs = "[\"gible rare\"]";

        ConfigMergers.Outcome out = ConfigMergers.mergeStringArray(base, mine, theirs, ConfigMergers::keyAllButLast);

        assertFalse(out.changed(), "same key already present → no addition, no override");
    }

    // ---- flat JSON object (rarity_weights style) ----

    @Test
    void jsonObjectAddsNewKeyAndKeepsCustomValue() {
        String base = "{\"common\": 100}";
        String mine = "{\"common\": 250}";                 // admin tuned common
        String theirs = "{\"common\": 100, \"mythic\": 1}"; // new rarity added upstream

        ConfigMergers.Outcome out = ConfigMergers.mergeJsonObject(base, mine, theirs);

        assertTrue(out.changed());
        JsonObject result = JsonParser.parseString(out.content()).getAsJsonObject();
        assertEquals(250, result.get("common").getAsInt(), "admin value preserved");
        assertEquals(1, result.get("mythic").getAsInt(), "new key added");
    }

    // ---- loot groups (nested object, group granularity) ----

    @Test
    void lootGroupsAddNewGroupKeepExisting() {
        String base = "{\"groups\": {\"archaeology\": {\"a\": 1}}}";
        String mine = "{\"groups\": {\"archaeology\": {\"a\": 1}, \"custom\": {\"x\": 9}}}";
        String theirs = "{\"groups\": {\"archaeology\": {\"a\": 1}, \"fishing\": {\"b\": 2}}}";

        ConfigMergers.Outcome out = ConfigMergers.mergeLootGroups(base, mine, theirs);

        assertTrue(out.changed());
        JsonObject groups = JsonParser.parseString(out.content()).getAsJsonObject().getAsJsonObject("groups");
        assertTrue(groups.has("fishing"), "new default group added");
        assertTrue(groups.has("custom"), "admin custom group preserved");
        assertTrue(groups.has("archaeology"));
    }
}
