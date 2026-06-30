package dev.mrshawn.pokeblocks.item;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PokeblocksConfigFiles;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.nio.file.Path;
import java.util.*;

/**
 * Loads a per-variant acquisition divisor that is applied on top of the
 * standard rarity weight, representing an extra rarity penalty imposed by
 * the mechanic through which that variant is obtained.
 *
 * <p>Example: the substitute doll can only be obtained by popping a pokedoll
 * (1-in-{@link dev.mrshawn.pokeblocks.constants.ModSettings#SUBSTITUTE_POP_CHANCE}).
 * Registering it with a divisor of 12 means its effective loot weight is divided
 * by 12, so the displayed drop chance correctly reflects the pop mechanic.
 *
 * <p>Config format ({@code rarity_acquisition_divisors.json}) — JSON array of strings:
 * <pre>  "pokemon [flag1 flag2 ...] divisor"</pre>
 * Examples:
 * <ul>
 *   <li>{@code "substitute 12"} — plain substitute, effective weight ÷ 12</li>
 *   <li>{@code "substitute shiny 12"} — shiny substitute, effective weight ÷ 12</li>
 * </ul>
 */
public class DollRarityAcquisitionDivisors {

    /** Key → acquisition divisor.  Key format matches {@link DollRarityOverrides#buildKey}. */
    private static final Map<String, Integer> divisorsMap = new HashMap<>();
    private static Path configPath = null;
    private static final Gson GSON = new Gson();

    /**
     * Copies the default {@code rarity_acquisition_divisors.json} from assets to
     * the server config folder if absent, then loads it. Call during server startup.
     */
    public static void initialize(Path serverDir) {
        configPath = PokeblocksConfigFiles.ensureExtracted(serverDir, "rarity_acquisition_divisors.json");
        reload();
    }

    /** Reloads the acquisition divisors map from the config file. */
    public static void reload() {
        divisorsMap.clear();

        String content = PokeblocksConfigFiles.readConfigContent(configPath, "rarity_acquisition_divisors.json");
        if (content == null) {
            PokeblocksLog.LOGGER.info("No rarity_acquisition_divisors.json found, skipping acquisition divisors");
        } else {
            applyContent(content);
            PokeblocksLog.LOGGER.info("Loaded {} acquisition divisor(s)", divisorsMap.size());
        }

        // Resource packs may contribute override entries; later packs win by key.
        List<String> packOverrides = PokeblocksConfigFiles.collectPackOverrides(
                configPath == null ? null : configPath.getParent(), "rarity_acquisition_divisors.json");
        for (String override : packOverrides) {
            applyContent(override);
        }
        if (!packOverrides.isEmpty()) {
            PokeblocksLog.LOGGER.debug("Applied {} pack override(s) for {}", packOverrides.size(), "rarity_acquisition_divisors.json");
        }
    }

    /** Parses one JSON-array content string into {@link #divisorsMap}, merging by key (does not clear). */
    private static void applyContent(String content) {
        try {
            JsonArray array = GSON.fromJson(content, JsonArray.class);

            for (JsonElement element : array) {
                String trimmed = element.getAsString().trim();
                if (trimmed.isEmpty()) continue;

                String[] parts = trimmed.split("\\s+");
                if (parts.length < 2) {
                    PokeblocksLog.LOGGER.error("Invalid rarity_acquisition_divisors entry "
                            + "(need at least pokemon + divisor): {}", trimmed);
                    continue;
                }

                String pokemon = parts[0].toLowerCase();

                // Last token is the divisor; middle tokens (if any) are flags.
                String divisorStr = parts[parts.length - 1];
                int divisor;
                try {
                    divisor = Integer.parseInt(divisorStr);
                } catch (NumberFormatException e) {
                    PokeblocksLog.LOGGER.error("Invalid divisor '{}' in rarity_acquisition_divisors entry: {}", divisorStr, trimmed);
                    continue;
                }

                if (divisor <= 0) {
                    PokeblocksLog.LOGGER.error("Divisor must be > 0 in rarity_acquisition_divisors entry: {}", trimmed);
                    continue;
                }

                Set<ModelFlag> flags = DollFlagParser.parseFlags(parts, 1, parts.length - 1, trimmed);

                String key = DollRarityOverrides.buildKey(pokemon, flags);
                divisorsMap.put(key, divisor);
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to load rarity_acquisition_divisors.json", e);
        }
    }

    /**
     * Returns the acquisition divisor for the given pokemon + flag combination,
     * or {@code 1} (no extra penalty) if no entry is configured.
     */
    public static int getAcquisitionDivisor(String pokemon, Set<ModelFlag> flags) {
        String key = DollRarityOverrides.buildKey(pokemon, flags);
        return divisorsMap.getOrDefault(key, 1);
    }
}
