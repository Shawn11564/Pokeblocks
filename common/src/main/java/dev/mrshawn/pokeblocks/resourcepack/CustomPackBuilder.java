package dev.mrshawn.pokeblocks.resourcepack;

import dev.mrshawn.pokeblocks.PokeblocksLog;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Builds the served custom resource pack from admin sub-packs under
 * {@code config/Pokeblocks/resourcepack/} (folders and {@code .zip} files, plus the {@code custom/}
 * folder which is loaded last so it overrides).
 *
 * <p>Two input layouts are accepted inside a sub-pack's {@code assets/} directory:
 * <ul>
 *   <li><b>Typed (preferred):</b> {@code assets/<type>/<kind>/<id>.<ext>} where {@code <type>} is
 *       {@code dolls}, {@code figurines} or {@code decorations} and {@code <kind>} is {@code models},
 *       {@code textures} or {@code animations}. File names are the bare id (dolls keep flag suffixes,
 *       e.g. {@code orange_shiny.geo.json}); the type folder supplies the internal naming
 *       ({@code dolls/} → {@code pokedoll_<id>}, {@code figurines/} → {@code <id>_figurine},
 *       {@code decorations/} → {@code <id>_decoration}).</li>
 *   <li><b>Flat (legacy):</b> {@code assets/<kind>/<fullname>} where the file name already carries the
 *       marker (e.g. {@code pokedoll_orange.geo.json}). Kept for backwards compatibility.</li>
 * </ul>
 * Both are remapped to the proper pack paths {@code assets/pokeblocks/<geo|textures|animations>/block/}.
 */
public class CustomPackBuilder {

	private static final String PACK_NAME = "pokeblocks_custom_pack.zip";

	/** Type sub-folders in the preferred typed layout; each supplies the internal naming marker. */
	private static final String[] TYPE_FOLDERS = {"dolls", "figurines", "decorations"};
	/** Asset-kind sub-folders (used by both the typed and legacy layouts). */
	private static final String[] KIND_FOLDERS = {"textures", "models", "animations"};

	public static Path findCustomDir(Path gameDir) {
		Path customDir = gameDir.resolve("config").resolve("Pokeblocks").resolve("resourcepack").resolve("custom");

		if (Files.exists(customDir)) {
			try (var walk = Files.walk(customDir)) {
				if (walk.anyMatch(Files::isRegularFile)) return customDir;
			} catch (IOException ignored) {}
		}

		if (Files.exists(customDir)) return customDir;

		return null;
	}

	/**
	 * Scans custom pack directories for asset filenames without writing any zip file.
	 * Returns null if there are no custom assets.
	 */
	public static PackBuildResult scanFiles(Path gameDir) throws IOException {
		Path resourcePackDir = gameDir.resolve("config").resolve("Pokeblocks").resolve("resourcepack");
		Path customDir = findCustomDir(gameDir);

		Set<String> modelFileNames = new TreeSet<>();
		Set<String> textureFileNames = new TreeSet<>();
		Set<String> animationFileNames = new TreeSet<>();

		if (Files.exists(resourcePackDir)) {
			try (var stream = Files.list(resourcePackDir)) {
				var packs = stream.filter(p -> {
					String name = p.getFileName().toString();
					if (name.equals("custom")) return false;
					return Files.isDirectory(p) || name.toLowerCase().endsWith(".zip");
				}).toList();

				for (Path pack : packs) {
					if (Files.isDirectory(pack)) {
						scanFolderFileNames(pack, modelFileNames, textureFileNames, animationFileNames);
					} else {
						scanZipFileNames(pack, modelFileNames, textureFileNames, animationFileNames);
					}
				}
			}
		}

		if (customDir != null) {
			scanFolderFileNames(customDir, modelFileNames, textureFileNames, animationFileNames);
		}

		if (modelFileNames.isEmpty() && textureFileNames.isEmpty() && animationFileNames.isEmpty()) return null;

		return new PackBuildResult(null, modelFileNames, textureFileNames, animationFileNames);
	}

	/**
	 * Applies the internal naming marker for a typed-layout file. {@code type} is one of
	 * {@link #TYPE_FOLDERS}, {@code kind} one of {@link #KIND_FOLDERS}, {@code bareName} the admin's
	 * bare file (e.g. {@code orange.geo.json}). Returns the internal file name
	 * (e.g. {@code pokedoll_orange.geo.json}, {@code customfig_figurine_texture.png}), or {@code null}
	 * if the extension does not match the kind.
	 */
	private static String markBareName(String type, String kind, String bareName) {
		String ext = switch (kind) {
			case "models" -> ".geo.json";
			case "animations" -> ".animation.json";
			case "textures" -> ".png";
			default -> null;
		};
		if (ext == null || !bareName.toLowerCase().endsWith(ext)) return null;

		String base = bareName.substring(0, bareName.length() - ext.length());
		// Tolerate an already-present "_texture" on texture files so both orange.png and
		// orange_texture.png resolve to the same internal name.
		if (kind.equals("textures") && base.toLowerCase().endsWith("_texture")) {
			base = base.substring(0, base.length() - "_texture".length());
		}

		String marked = switch (type) {
			case "dolls" -> "pokedoll_" + base;
			case "figurines" -> base + "_figurine";
			case "decorations" -> base + "_decoration";
			default -> null;
		};
		if (marked == null) return null;

		return switch (kind) {
			case "textures" -> marked + "_texture.png";
			case "models" -> marked + ".geo.json";
			case "animations" -> marked + ".animation.json";
			default -> null;
		};
	}

	private static void addByKind(String kind, String name, Set<String> modelFileNames,
								  Set<String> textureFileNames, Set<String> animationFileNames) {
		switch (kind) {
			case "textures" -> textureFileNames.add(name);
			case "models" -> modelFileNames.add(name);
			case "animations" -> animationFileNames.add(name);
		}
	}

	private static void scanFolderFileNames(Path packDir, Set<String> modelFileNames,
											Set<String> textureFileNames, Set<String> animationFileNames) throws IOException {
		Path assetsDir = packDir.resolve("assets");
		if (!Files.exists(assetsDir)) return;

		// Typed layout: assets/<type>/<kind>/<bare>
		for (String type : TYPE_FOLDERS) {
			Path typeDir = assetsDir.resolve(type);
			if (!Files.exists(typeDir)) continue;
			for (String kind : KIND_FOLDERS) {
				Path dir = typeDir.resolve(kind);
				if (!Files.exists(dir)) continue;
				try (var stream = Files.list(dir)) {
					stream.filter(Files::isRegularFile).forEach(p -> {
						String marked = markBareName(type, kind, p.getFileName().toString());
						if (marked != null) addByKind(kind, marked, modelFileNames, textureFileNames, animationFileNames);
					});
				}
			}
		}

		// Legacy flat layout: assets/<kind>/<fullname>
		for (String kind : KIND_FOLDERS) {
			Path dir = assetsDir.resolve(kind);
			if (!Files.exists(dir)) continue;
			try (var stream = Files.list(dir)) {
				stream.filter(Files::isRegularFile).map(p -> p.getFileName().toString())
						.forEach(name -> addByKind(kind, name, modelFileNames, textureFileNames, animationFileNames));
			}
		}
	}

	private static void scanZipFileNames(Path zipPath, Set<String> modelFileNames,
										 Set<String> textureFileNames, Set<String> animationFileNames) throws IOException {
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
			var entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) continue;
				String name = entry.getName();
				if (!name.startsWith("assets/")) continue;
				String[] parts = name.split("/");
				if (parts.length == 4) {
					// Typed layout: assets/<type>/<kind>/<bare>
					String marked = markBareName(parts[1], parts[2], parts[3]);
					if (marked != null) addByKind(parts[2], marked, modelFileNames, textureFileNames, animationFileNames);
				} else if (parts.length == 3) {
					// Legacy flat layout: assets/<kind>/<fullname>
					addByKind(parts[1], parts[2], modelFileNames, textureFileNames, animationFileNames);
				}
			}
		}
	}

	public static PackBuildResult buildResourcePack(Path gameDir) throws IOException {
		Path resourcePackDir = gameDir.resolve("config").resolve("Pokeblocks").resolve("resourcepack");
		Path customDir = findCustomDir(gameDir);

		Map<String, byte[]> zipEntries = new TreeMap<>();
		// packPath -> the distinct packs (in load order) that provided it, for conflict reporting.
		Map<String, List<String>> providers = new TreeMap<>();
		Set<String> modelFileNames = new TreeSet<>();
		Set<String> textureFileNames = new TreeSet<>();
		Set<String> animationFileNames = new TreeSet<>();

		/* =========================
		   LOAD ADDITIONAL RESOURCE PACKS
		   ========================= */

		if (Files.exists(resourcePackDir)) {
			try (var stream = Files.list(resourcePackDir)) {
				var packs = stream.filter(p -> {
					String name = p.getFileName().toString();
					// Skip the "custom" folder (loaded last below so it has the highest priority)
					if (name.equals("custom")) return false;
					// Include directories and zip files
					return Files.isDirectory(p) || name.toLowerCase().endsWith(".zip");
				}).sorted().toList(); // deterministic order -> stable conflict winner and stable pack hash

				for (Path pack : packs) {
					String packName = pack.getFileName().toString();
					if (Files.isDirectory(pack)) {
						loadResourcePackFromFolder(pack, packName, zipEntries, providers, modelFileNames, textureFileNames, animationFileNames);
					} else {
						loadResourcePackFromZip(pack, packName, zipEntries, providers, modelFileNames, textureFileNames, animationFileNames);
					}
				}
			}
		}

		/* =========================
		   LOAD FROM CUSTOM FOLDER
		   Structure: custom/assets/<textures, models, animations>
		   Files placed flat (no subdirectories)
		   Custom folder is loaded last so its files override others
		   ========================= */

		if (customDir != null) {
			// Loaded last so the custom/ folder overrides any sub-pack that provides the same file.
			loadResourcePackFromFolder(customDir, "custom", zipEntries, providers, modelFileNames, textureFileNames, animationFileNames);
		}

		// Warn about any resource supplied by more than one pack (last loaded wins; custom/ is highest).
		for (Map.Entry<String, List<String>> e : providers.entrySet()) {
			List<String> who = e.getValue();
			if (who.size() > 1) {
				PokeblocksLog.LOGGER.warn("Resource pack conflict: '{}' is provided by multiple packs {} - using the "
						+ "version from '{}' (custom/ overrides sub-packs; otherwise the last in alphabetical order wins).",
						e.getKey(), who, who.get(who.size() - 1));
			}
		}

		if (zipEntries.isEmpty()) return null;

		PokeblocksLog.LOGGER.info("Custom resource pack: {} model(s), {} texture(s), {} animation(s)",
				modelFileNames.size(), textureFileNames.size(), animationFileNames.size());

		Path finalZip = gameDir.resolve(PACK_NAME);
		Path tempZip = gameDir.resolve(PACK_NAME + ".tmp");

		Path packIcon = customDir != null ? customDir.resolve("pack.png") : null;
		boolean hasPackIcon = packIcon != null && Files.exists(packIcon);

		if (Files.exists(finalZip)) Files.delete(finalZip);
		if (Files.exists(tempZip)) Files.delete(tempZip);

		try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(tempZip))) {

			ZipEntry meta = new ZipEntry("pack.mcmeta");
			meta.setTime(0L);
			zip.putNextEntry(meta);
			zip.write("""
            {
              "pack": {
                "pack_format": 26,
                "description": "Pokeblocks Custom Dolls"
              }
            }
            """.getBytes());
			zip.closeEntry();

			if (hasPackIcon) {
				ZipEntry iconEntry = new ZipEntry("pack.png");
				iconEntry.setTime(0L);
				zip.putNextEntry(iconEntry);
				try (InputStream in = Files.newInputStream(packIcon)) {
					in.transferTo(zip);
				}
				zip.closeEntry();
			}

			for (Map.Entry<String, byte[]> e : zipEntries.entrySet()) {
				String zipName = e.getKey().replaceAll("^/+", "");
				ZipEntry entry = new ZipEntry(zipName);
				entry.setTime(0L);
				zip.putNextEntry(entry);
				zip.write(e.getValue());
				zip.closeEntry();
			}
		}

		Files.move(tempZip, finalZip,
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.ATOMIC_MOVE);

		return new PackBuildResult(
				finalZip,
				modelFileNames,
				textureFileNames,
				animationFileNames
		);
	}

	/**
	 * Maps a file from the simplified structure (assets/<textures,models,animations>/filename)
	 * to the proper resource pack path (assets/pokeblocks/<textures|geo|animations>/block/filename).
	 *
	 * @return the remapped path, or null if the file doesn't belong to a recognized folder
	 */
	private static String remapToPackPath(String folder, String fileName,
										  Set<String> modelFileNames, Set<String> textureFileNames,
										  Set<String> animationFileNames) {
		return switch (folder) {
			case "textures" -> {
				textureFileNames.add(fileName);
				yield "assets/pokeblocks/textures/block/" + fileName;
			}
			case "models" -> {
				modelFileNames.add(fileName);
				yield "assets/pokeblocks/geo/block/" + fileName;
			}
			case "animations" -> {
				animationFileNames.add(fileName);
				yield "assets/pokeblocks/animations/block/" + fileName;
			}
			default -> null;
		};
	}

	/**
	 * Records a built pack entry under {@code packPath}. Later (higher-priority) packs overwrite earlier
	 * ones — "last wins" — and every distinct pack that provides a path is tracked, in load order, so
	 * {@link #buildResourcePack} can warn about cross-pack conflicts.
	 */
	private static void addEntry(Map<String, byte[]> zipEntries, Map<String, List<String>> providers,
								 String packPath, byte[] bytes, String packName) {
		zipEntries.put(packPath, bytes); // last wins: later (higher-priority) packs override earlier ones
		List<String> who = providers.computeIfAbsent(packPath, k -> new ArrayList<>());
		if (who.isEmpty() || !who.get(who.size() - 1).equals(packName)) {
			who.add(packName);
		}
	}

	private static void loadResourcePackFromFolder(Path packDir, String packName,
												   Map<String, byte[]> zipEntries, Map<String, List<String>> providers,
												   Set<String> modelFileNames,
												   Set<String> textureFileNames,
												   Set<String> animationFileNames) throws IOException {
		PokeblocksLog.LOGGER.debug("Loading resource pack from folder: {}", packDir);
		Path assetsDir = packDir.resolve("assets");
		if (!Files.exists(assetsDir)) return;

		// Typed layout: assets/<type>/<kind>/<bare>
		for (String type : TYPE_FOLDERS) {
			Path typeDir = assetsDir.resolve(type);
			if (!Files.exists(typeDir)) continue;
			for (String kind : KIND_FOLDERS) {
				Path dir = typeDir.resolve(kind);
				if (!Files.exists(dir)) continue;
				try (var stream = Files.list(dir)) {
					var files = stream.filter(Files::isRegularFile).toList();
					for (Path path : files) {
						String marked = markBareName(type, kind, path.getFileName().toString());
						if (marked == null) continue;
						String packPath = remapToPackPath(kind, marked, modelFileNames, textureFileNames, animationFileNames);
						if (packPath != null) addEntry(zipEntries, providers, packPath, Files.readAllBytes(path), packName);
					}
				}
			}
		}

		// Legacy flat layout: assets/<kind>/<fullname>
		for (String kind : KIND_FOLDERS) {
			Path dir = assetsDir.resolve(kind);
			if (!Files.exists(dir)) continue;
			try (var stream = Files.list(dir)) {
				var files = stream.filter(Files::isRegularFile).toList();
				for (Path path : files) {
					String name = path.getFileName().toString();
					String packPath = remapToPackPath(kind, name, modelFileNames, textureFileNames, animationFileNames);
					if (packPath != null) addEntry(zipEntries, providers, packPath, Files.readAllBytes(path), packName);
				}
			}
		}
	}

	private static void loadResourcePackFromZip(Path zipPath, String packName,
												Map<String, byte[]> zipEntries, Map<String, List<String>> providers,
												Set<String> modelFileNames,
												Set<String> textureFileNames,
												Set<String> animationFileNames) throws IOException {
		PokeblocksLog.LOGGER.debug("Loading resource pack from zip: {}", zipPath);
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
			var entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) continue;

				String name = entry.getName();
				if (!name.startsWith("assets/")) continue;

				// Typed layout: assets/<type>/<kind>/<bare>; legacy flat: assets/<kind>/<fullname>
				String[] parts = name.split("/");
				String kind;
				String fileName;
				if (parts.length == 4) {
					kind = parts[2];
					fileName = markBareName(parts[1], parts[2], parts[3]);
					if (fileName == null) continue;
				} else if (parts.length == 3) {
					kind = parts[1];
					fileName = parts[2];
				} else {
					continue;
				}

				String packPath = remapToPackPath(kind, fileName, modelFileNames, textureFileNames, animationFileNames);
				if (packPath == null) continue;

				try (InputStream in = zipFile.getInputStream(entry)) {
					addEntry(zipEntries, providers, packPath, in.readAllBytes(), packName);
				}
			}
		}
	}

	public static String computeSHA1(Path file) {
		try (InputStream in = Files.newInputStream(file)) {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] buf = new byte[8192];
			int len;
			while ((len = in.read(buf)) != -1)
				digest.update(buf, 0, len);

			StringBuilder sb = new StringBuilder();
			for (byte b : digest.digest())
				sb.append(String.format("%02x", b));

			return sb.toString();
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("Failed to compute SHA-1 for {}", file, e);
			return "";
		}
	}

	/** A cheap fingerprint of the custom-pack inputs (sorted relative path + size + mtime of every file
	 *  under config/Pokeblocks/resourcepack/), so the zip is only rebuilt when something actually changed.
	 *  Returns "" when the directory does not exist. */
	public static String computeInputFingerprint(Path gameDir) {
		Path resourcePackDir = gameDir.resolve("config").resolve("Pokeblocks").resolve("resourcepack");
		if (!Files.exists(resourcePackDir)) return "";

		Set<String> lines = new TreeSet<>();
		try (var walk = Files.walk(resourcePackDir)) {
			for (Path p : (Iterable<Path>) walk.filter(Files::isRegularFile)::iterator) {
				String rel = resourcePackDir.relativize(p).toString().replace('\\', '/');
				lines.add(rel + "|" + Files.size(p) + "|" + Files.getLastModifiedTime(p).toMillis());
			}
		} catch (IOException e) {
			return "";
		}

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(String.join("\n", lines).getBytes());

			StringBuilder sb = new StringBuilder();
			for (byte b : digest.digest())
				sb.append(String.format("%02x", b));

			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}
}