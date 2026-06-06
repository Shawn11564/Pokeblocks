package dev.mrshawn.pokeblocks.config;

import dev.mrshawn.pokeblocks.PokeblocksLog;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared helper for the mod's per-server JSON config files, which live under
 * {@code <serverDir>/config/Pokeblocks/}.
 * <p>
 * Every config loader needs the same first-run plumbing: resolve the path, create the
 * directory, and copy the default bundled in {@code assets/pokeblocks/} if the file does
 * not yet exist. Centralizing it here keeps that logic identical across all loaders.
 */
public final class PokeblocksConfigFiles {

    private PokeblocksConfigFiles() {}

    /**
     * Resolves {@code config/Pokeblocks/<fileName>}, creating the parent directory and
     * copying the bundled default from {@code /assets/pokeblocks/<fileName>} when the file
     * does not yet exist. Always returns the resolved path, even if extraction failed (the
     * caller's own "file missing" handling then applies).
     */
    public static Path ensureExtracted(Path serverDir, String fileName) {
        Path configPath = serverDir.resolve("config").resolve("Pokeblocks").resolve(fileName);

        try {
            Files.createDirectories(configPath.getParent());

            if (!Files.exists(configPath)) {
                try (InputStream is = PokeblocksConfigFiles.class.getResourceAsStream("/assets/pokeblocks/" + fileName)) {
                    if (is != null) {
                        Files.copy(is, configPath);
                        PokeblocksLog.LOGGER.info("Copied default {} to {}", fileName, configPath);
                    }
                }
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to copy {} to config", fileName, e);
        }

        return configPath;
    }
}
