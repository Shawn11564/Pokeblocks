package dev.mrshawn.pokeblocks.config;

import java.util.Locale;

/**
 * How {@link ConfigSync} reconciles the mod's bundled default config/override files with the
 * admin's files in {@code config/Pokeblocks/} when the mod updates. Selected by the top-level
 * {@code auto_update_configs} setting in {@code config.toml}.
 */
public enum ConfigUpdateMode {

    /**
     * Non-destructive three-way merge (the default): new and changed defaults are pulled in,
     * but the admin's custom entries and deliberate deletions are preserved.
     */
    MERGE,

    /**
     * Replace each managed file with the bundled default on every startup, discarding admin
     * customizations. A timestamped backup is taken first when {@code backup_before_update} is on.
     */
    OVERWRITE,

    /** Never touch the admin's files automatically; updates are pulled manually with the command. */
    OFF;

    /**
     * Parses a config value into a mode, tolerating the legacy boolean form
     * ({@code true} → {@link #MERGE}, {@code false} → {@link #OFF}).
     *
     * @param value        the raw config value (may be null)
     * @param defaultValue the mode to use when {@code value} is null or unrecognized
     */
    public static ConfigUpdateMode parse(String value, ConfigUpdateMode defaultValue) {
        if (value == null) return defaultValue;
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "merge", "true" -> MERGE;
            case "overwrite", "replace" -> OVERWRITE;
            case "off", "false", "none", "frozen" -> OFF;
            default -> defaultValue;
        };
    }

    /** The lowercase token written to config.toml for this mode. */
    public String token() {
        return name().toLowerCase(Locale.ROOT);
    }
}
