package dev.mrshawn.pokeblocks.config;

import java.util.Locale;

/**
 * How the server hands the custom resource pack to clients (the {@code [resourcepack] distribution} key).
 * <ul>
 *   <li>{@link #SELF_HOST} — (default) the mod serves the locally-built zip over its own small HTTP server
 *       and advertises that URL. Works out of the box; for public servers set {@code self_host_address} to a
 *       reachable host/IP.</li>
 *   <li>{@link #REMOTE_URL} — the mod advertises an admin-provided {@code remote_url} instead of self-hosting.
 *       The SHA-1 comes from {@code remote_sha1} if set, otherwise from the locally-built pack (in which case
 *       the admin must upload that exact zip to the URL).</li>
 * </ul>
 */
public enum PackDistribution {
    SELF_HOST("self_host"),
    REMOTE_URL("remote_url");

    private final String token;

    PackDistribution(String token) {
        this.token = token;
    }

    /** The canonical config token for this mode (e.g. {@code "self_host"}). */
    public String token() {
        return token;
    }

    /** Parses a config value into a mode, tolerating a few aliases, falling back to {@code fallback}. */
    public static PackDistribution parse(String value, PackDistribution fallback) {
        if (value == null) return fallback;
        String v = value.trim().toLowerCase(Locale.ROOT);
        for (PackDistribution d : values()) {
            if (d.token.equals(v)) return d;
        }
        return switch (v) {
            case "remote", "url", "remoteurl" -> REMOTE_URL;
            case "self", "host", "selfhost", "self-host" -> SELF_HOST;
            default -> fallback;
        };
    }
}
