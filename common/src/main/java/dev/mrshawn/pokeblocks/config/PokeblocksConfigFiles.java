package dev.mrshawn.pokeblocks.config;

import dev.mrshawn.pokeblocks.PokeblocksLog;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

            // #68: only write a file into the folder if it is shown. Hidden files are intentionally
            // left out to reduce clutter; their bundled defaults still apply via readConfigContent.
            if (!Files.exists(configPath) && ConfigSync.isFileShown(fileName)) {
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

    /**
     * Returns the content of a managed config file: the admin's on-disk file if it exists, otherwise
     * the bundled default from {@code /assets/pokeblocks/<fileName>}. This is how hidden files (#68) —
     * intentionally never written to the config folder — still contribute their defaults at load time,
     * so hiding a file declutters the folder without changing behavior. Returns {@code null} only if
     * neither the on-disk file nor a bundled default exists.
     *
     * @param configPath the resolved on-disk path (may be {@code null}, e.g. before initialization)
     * @param fileName   the bundled asset file name used for the jar fallback
     */
    public static String readConfigContent(Path configPath, String fileName) {
        if (configPath != null && Files.exists(configPath)) {
            try {
                return Files.readString(configPath);
            } catch (Exception e) {
                PokeblocksLog.LOGGER.error("Failed to read {}", configPath, e);
                return null;
            }
        }
        try (InputStream is = PokeblocksConfigFiles.class.getResourceAsStream("/assets/pokeblocks/" + fileName)) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.error("Failed to read bundled default for {}", fileName, e);
        }
        return null;
    }

    /** Relative path, within a sub-pack, of the per-id setting override file for a config. */
    private static final String PACK_CONFIG_PREFIX = "pokeblocks/config/";

    /**
     * Collects per-id setting overrides that admin resource packs supply for a given config file.
     * Looks under {@code <modConfigDir>/resourcepack/} for sub-packs (folders and .zip files, plus the
     * {@code custom/} folder loaded LAST) and returns the UTF-8 content of each pack's
     * {@code pokeblocks/config/<fileName>} entry, in load order (later entries take precedence).
     * Empty list if none or if modConfigDir is null.
     * <p>
     * Pack enumeration mirrors {@code resourcepack/CustomPackBuilder}: top-level folders and {@code .zip}
     * files directly under {@code resourcepack/} are processed first (the {@code custom} folder is skipped
     * here), then the {@code custom/} folder is processed last so it overrides the others. Never throws;
     * per-pack errors are logged at warn and skipped.
     */
    public static List<String> collectPackOverrides(Path modConfigDir, String fileName) {
        List<String> overrides = new ArrayList<>();
        if (modConfigDir == null) {
            return overrides;
        }

        Path resourcePackDir = modConfigDir.resolve("resourcepack");
        if (!Files.exists(resourcePackDir)) {
            return overrides;
        }

        // Top-level sub-packs (folders + .zip), skipping the special "custom" folder which loads last.
        try (var stream = Files.list(resourcePackDir)) {
            var packs = stream.filter(p -> {
                String name = p.getFileName().toString();
                if (name.equals("custom")) return false;
                return Files.isDirectory(p) || name.toLowerCase(Locale.ROOT).endsWith(".zip");
            }).sorted().toList();

            for (Path pack : packs) {
                String content = Files.isDirectory(pack)
                        ? readOverrideFromFolder(pack, fileName)
                        : readOverrideFromZip(pack, fileName);
                if (content != null) {
                    overrides.add(content);
                }
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.warn("Failed to enumerate resource packs for {} overrides", fileName, e);
        }

        // The custom/ folder is processed last so its entries take precedence.
        Path customDir = resourcePackDir.resolve("custom");
        if (Files.isDirectory(customDir)) {
            String content = readOverrideFromFolder(customDir, fileName);
            if (content != null) {
                overrides.add(content);
            }
        }

        return overrides;
    }

    /** Reads {@code <pack>/pokeblocks/config/<fileName>} as UTF-8, or {@code null} if absent/unreadable. */
    private static String readOverrideFromFolder(Path packDir, String fileName) {
        Path overrideFile = packDir.resolve("pokeblocks").resolve("config").resolve(fileName);
        if (!Files.exists(overrideFile)) {
            return null;
        }
        try {
            return Files.readString(overrideFile);
        } catch (Exception e) {
            PokeblocksLog.LOGGER.warn("Failed to read pack override {}", overrideFile, e);
            return null;
        }
    }

    /** Reads the zip entry {@code pokeblocks/config/<fileName>} as UTF-8, or {@code null} if absent/unreadable. */
    private static String readOverrideFromZip(Path zipPath, String fileName) {
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            ZipEntry entry = zipFile.getEntry(PACK_CONFIG_PREFIX + fileName);
            if (entry == null || entry.isDirectory()) {
                return null;
            }
            try (InputStream in = zipFile.getInputStream(entry)) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            PokeblocksLog.LOGGER.warn("Failed to read pack override {} from {}", PACK_CONFIG_PREFIX + fileName, zipPath, e);
            return null;
        }
    }
}
