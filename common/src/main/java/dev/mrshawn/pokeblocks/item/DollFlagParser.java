package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.util.EnumSet;

/**
 * Shared parsing for the {@code "pokemon flag1 flag2 ..."} doll-variant specs used by the
 * whitespace-delimited rarity config files ({@code doll_rarity.json},
 * {@code ignored_rarity_flags.json}, {@code rarity_acquisition_divisors.json}).
 * <p>
 * Centralizing the flag-token lookup here guarantees every config interprets flag names
 * (and reports unknown ones) identically. The canonical map key for a parsed
 * {@code pokemon + flags} pair is built with {@link DollRarityOverrides#buildKey}.
 */
public final class DollFlagParser {

    private DollFlagParser() {}

    /**
     * Parses the flag tokens in {@code parts[fromInclusive..toExclusive)} into a set,
     * logging a warning for any token that is not a recognized {@link ModelFlag}.
     *
     * @param parts         the whitespace-split entry tokens
     * @param fromInclusive index of the first flag token (inclusive)
     * @param toExclusive   index just past the last flag token (exclusive)
     * @param rawEntry      the original entry text, used only for log messages
     * @return the set of recognized flags (possibly empty)
     */
    public static EnumSet<ModelFlag> parseFlags(String[] parts, int fromInclusive, int toExclusive, String rawEntry) {
        EnumSet<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
        for (int i = fromInclusive; i < toExclusive; i++) {
            ModelFlag flag = ModelFlag.fromTagName(parts[i]);
            if (flag == null) {
                PokeblocksLog.LOGGER.warn("Unknown flag '{}' in entry: {}", parts[i], rawEntry);
            } else {
                flags.add(flag);
            }
        }
        return flags;
    }
}
