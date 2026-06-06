package dev.mrshawn.pokeblocks.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Pure (no I/O) three-way merge implementations used by {@link ConfigSync}.
 * <p>
 * Every merge takes three inputs:
 * <ul>
 *   <li><b>base</b> — the bundled default as it was the last time we synced (may be {@code null}
 *       on first adoption, which is treated as "empty" → additive backfill only).</li>
 *   <li><b>mine</b> — the admin's current live file.</li>
 *   <li><b>theirs</b> — the bundled default shipped in the current jar.</li>
 * </ul>
 * The result keeps every admin entry, adds entries that are new in {@code theirs} versus
 * {@code base}, and updates entries whose default changed upstream <em>only</em> when the admin
 * left them at the previous default. Entries the admin deleted (present in {@code base} but not
 * {@code mine}) are never re-added.
 */
public final class ConfigMergers {

    private ConfigMergers() {}

    private static final Gson PRETTY = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    /** Outcome of a single merge. {@code content} is only meaningful when {@code changed} is true. */
    public record Outcome(boolean changed, String content, List<String> added, List<String> updated) {
        public static Outcome unchanged() {
            return new Outcome(false, null, List.of(), List.of());
        }
    }

    // ------------------------------------------------------------------
    // Key functions for the string-array configs
    // ------------------------------------------------------------------

    /** Whitespace-collapsed, lower-cased form used for stable comparison. */
    public static String normalize(String s) {
        return s.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /** Key = first token (e.g. {@code "zackghast cobblemon_team"} → {@code "zackghast"}). */
    public static String keyHead(String entry) {
        String n = normalize(entry);
        int sp = n.indexOf(' ');
        return sp < 0 ? n : n.substring(0, sp);
    }

    /**
     * Key = everything except the last token (e.g. {@code "calyrex animated legendary"} →
     * {@code "calyrex animated"}). Used where the trailing token is the value (rarity, divisor).
     */
    public static String keyAllButLast(String entry) {
        String n = normalize(entry);
        int sp = n.lastIndexOf(' ');
        return sp < 0 ? n : n.substring(0, sp);
    }

    /** Key = the whole entry. Used where each entry is an indivisible rule. */
    public static String keyWhole(String entry) {
        return normalize(entry);
    }

    // ------------------------------------------------------------------
    // Strategy: JSON array of strings, keyed
    // ------------------------------------------------------------------

    public static Outcome mergeStringArray(String baseJson, String mineJson, String theirsJson,
                                           Function<String, String> keyFn) {
        List<String> mine = parseArray(mineJson);
        List<String> theirs = parseArray(theirsJson);
        List<String> base = parseArray(baseJson);

        Map<String, String> baseNorm = normByKey(base, keyFn);
        Map<String, String> theirsNorm = normByKey(theirs, keyFn);
        Map<String, String> theirsRaw = rawByKey(theirs, keyFn);

        // Start from the admin's file verbatim so custom entries and ordering survive.
        List<String> result = new ArrayList<>(mine);
        Map<String, Integer> mineIndex = new HashMap<>();
        for (int i = 0; i < result.size(); i++) {
            String k = keyFn.apply(result.get(i));
            if (k != null && !k.isEmpty()) mineIndex.putIfAbsent(k, i);
        }

        List<String> added = new ArrayList<>();
        List<String> updated = new ArrayList<>();

        // Updates: entry is in mine, base and theirs; the admin left it at the old default
        // (mine == base) and the default changed upstream (theirs != base) → adopt the new default.
        for (Map.Entry<String, Integer> e : mineIndex.entrySet()) {
            String key = e.getKey();
            String baseValue = baseNorm.get(key);
            String theirsValue = theirsNorm.get(key);
            if (baseValue == null || theirsValue == null) continue;

            String mineValue = normalize(result.get(e.getValue()));
            if (mineValue.equals(baseValue) && !theirsValue.equals(baseValue)) {
                String replacement = theirsRaw.get(key);
                result.set(e.getValue(), replacement);
                updated.add(replacement);
            }
        }

        // Additions: new in theirs versus base, and the admin doesn't already have it.
        for (String entry : theirs) {
            String key = keyFn.apply(entry);
            if (key == null || key.isEmpty()) continue;
            if (!baseNorm.containsKey(key) && !mineIndex.containsKey(key)) {
                result.add(entry);
                mineIndex.put(key, result.size() - 1);
                added.add(entry);
            }
        }

        if (added.isEmpty() && updated.isEmpty()) return Outcome.unchanged();

        JsonArray out = new JsonArray();
        for (String entry : result) out.add(entry);
        return new Outcome(true, PRETTY.toJson(out), added, updated);
    }

    // ------------------------------------------------------------------
    // Strategy: flat JSON object (property → value), e.g. rarity_weights.json
    // ------------------------------------------------------------------

    public static Outcome mergeJsonObject(String baseJson, String mineJson, String theirsJson) {
        JsonObject theirs = parseObject(theirsJson);
        if (theirs == null) return Outcome.unchanged();
        JsonObject base = parseObject(baseJson);
        JsonObject mine = parseObject(mineJson);
        if (mine == null) mine = new JsonObject();

        List<String> added = new ArrayList<>();
        List<String> updated = new ArrayList<>();

        for (Map.Entry<String, JsonElement> e : theirs.entrySet()) {
            String key = e.getKey();
            JsonElement theirsValue = e.getValue();
            boolean inBase = base != null && base.has(key);
            boolean inMine = mine.has(key);

            if (!inMine && !inBase) {
                mine.add(key, theirsValue);
                added.add(key + " = " + theirsValue);
            } else if (inMine && inBase) {
                JsonElement mineValue = mine.get(key);
                JsonElement baseValue = base.get(key);
                if (mineValue.equals(baseValue) && !theirsValue.equals(baseValue)) {
                    mine.add(key, theirsValue);
                    updated.add(key + " = " + theirsValue);
                }
            }
        }

        if (added.isEmpty() && updated.isEmpty()) return Outcome.unchanged();
        return new Outcome(true, PRETTY.toJson(mine), added, updated);
    }

    // ------------------------------------------------------------------
    // Strategy: loot_groups.json — merge at the top-level group granularity
    // ------------------------------------------------------------------

    public static Outcome mergeLootGroups(String baseJson, String mineJson, String theirsJson) {
        JsonObject theirs = parseObject(theirsJson);
        if (theirs == null || !isObject(theirs, "groups")) return Outcome.unchanged();

        JsonObject mine = parseObject(mineJson);
        if (mine == null) mine = new JsonObject();
        JsonObject base = parseObject(baseJson);

        JsonObject theirsGroups = theirs.getAsJsonObject("groups");
        JsonObject mineGroups = isObject(mine, "groups") ? mine.getAsJsonObject("groups") : new JsonObject();
        JsonObject baseGroups = (base != null && isObject(base, "groups")) ? base.getAsJsonObject("groups") : new JsonObject();

        List<String> added = new ArrayList<>();
        List<String> updated = new ArrayList<>();

        for (Map.Entry<String, JsonElement> e : theirsGroups.entrySet()) {
            String name = e.getKey();
            JsonElement theirsValue = e.getValue();
            boolean inMine = mineGroups.has(name);
            boolean inBase = baseGroups.has(name);

            if (!inMine && !inBase) {
                mineGroups.add(name, theirsValue);
                added.add(name);
            } else if (inMine && inBase) {
                JsonElement mineValue = mineGroups.get(name);
                JsonElement baseValue = baseGroups.get(name);
                if (mineValue.equals(baseValue) && !theirsValue.equals(baseValue)) {
                    mineGroups.add(name, theirsValue);
                    updated.add(name);
                }
            }
        }

        if (added.isEmpty() && updated.isEmpty()) return Outcome.unchanged();
        mine.add("groups", mineGroups);
        return new Outcome(true, PRETTY.toJson(mine), added, updated);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static boolean isObject(JsonObject obj, String key) {
        return obj.has(key) && obj.get(key).isJsonObject();
    }

    private static Map<String, String> normByKey(List<String> entries, Function<String, String> keyFn) {
        Map<String, String> map = new HashMap<>();
        for (String entry : entries) {
            String key = keyFn.apply(entry);
            if (key != null && !key.isEmpty()) map.putIfAbsent(key, normalize(entry));
        }
        return map;
    }

    private static Map<String, String> rawByKey(List<String> entries, Function<String, String> keyFn) {
        Map<String, String> map = new HashMap<>();
        for (String entry : entries) {
            String key = keyFn.apply(entry);
            if (key != null && !key.isEmpty()) map.putIfAbsent(key, entry);
        }
        return map;
    }

    private static List<String> parseArray(String json) {
        List<String> out = new ArrayList<>();
        if (json == null || json.isBlank()) return out;
        try {
            JsonElement el = JsonParser.parseString(json);
            if (el != null && el.isJsonArray()) {
                for (JsonElement e : el.getAsJsonArray()) {
                    if (e.isJsonPrimitive()) {
                        String s = e.getAsString().trim();
                        if (!s.isEmpty()) out.add(s);
                    }
                }
            }
        } catch (JsonSyntaxException ignored) {
            // Caller validates JSON beforehand; treat unparseable input as empty here.
        }
        return out;
    }

    private static JsonObject parseObject(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            JsonElement el = JsonParser.parseString(json);
            return (el != null && el.isJsonObject()) ? el.getAsJsonObject() : null;
        } catch (JsonSyntaxException e) {
            return null;
        }
    }
}
