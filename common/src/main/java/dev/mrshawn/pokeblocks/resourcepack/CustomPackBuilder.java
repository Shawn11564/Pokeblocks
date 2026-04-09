package dev.mrshawn.pokeblocks.resourcepack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class CustomPackBuilder {

	private static final String PACK_NAME = "pokeblocks_custom_pack.zip";

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

	private static void scanFolderFileNames(Path packDir, Set<String> modelFileNames,
											Set<String> textureFileNames, Set<String> animationFileNames) throws IOException {
		Path assetsDir = packDir.resolve("assets");
		if (!Files.exists(assetsDir)) return;

		for (String folder : new String[]{"textures", "models", "animations"}) {
			Path dir = assetsDir.resolve(folder);
			if (!Files.exists(dir)) continue;

			try (var stream = Files.list(dir)) {
				stream.filter(Files::isRegularFile).map(p -> p.getFileName().toString()).forEach(name -> {
					switch (folder) {
						case "textures" -> textureFileNames.add(name);
						case "models" -> modelFileNames.add(name);
						case "animations" -> animationFileNames.add(name);
					}
				});
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
				if (parts.length != 3) continue;
				switch (parts[1]) {
					case "textures" -> textureFileNames.add(parts[2]);
					case "models" -> modelFileNames.add(parts[2]);
					case "animations" -> animationFileNames.add(parts[2]);
				}
			}
		}
	}

	public static PackBuildResult buildResourcePack(Path gameDir) throws IOException {
		Path resourcePackDir = gameDir.resolve("config").resolve("Pokeblocks").resolve("resourcepack");
		Path customDir = findCustomDir(gameDir);

		Map<String, byte[]> zipEntries = new TreeMap<>();
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
					// Skip the "custom" folder
					if (name.equals("custom")) return false;
					// Include directories and zip files
					return Files.isDirectory(p) || name.toLowerCase().endsWith(".zip");
				}).toList();

				for (Path pack : packs) {
					if (Files.isDirectory(pack)) {
						loadResourcePackFromFolder(pack, zipEntries, modelFileNames, textureFileNames, animationFileNames);
					} else {
						loadResourcePackFromZip(pack, zipEntries, modelFileNames, textureFileNames, animationFileNames);
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
			loadResourcePackFromFolder(customDir, zipEntries, modelFileNames, textureFileNames, animationFileNames);
		}

		if (zipEntries.isEmpty()) return null;

		System.out.println("[Pokeblocks] Custom resource pack: "
				+ modelFileNames.size() + " model(s), "
				+ textureFileNames.size() + " texture(s), "
				+ animationFileNames.size() + " animation(s)");

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

	private static void loadResourcePackFromFolder(Path packDir, Map<String, byte[]> zipEntries,
												   Set<String> modelFileNames,
												   Set<String> textureFileNames,
												   Set<String> animationFileNames) throws IOException {
		System.out.println("[Pokeblocks] Loading resource pack from folder: " + packDir);
		Path assetsDir = packDir.resolve("assets");
		if (!Files.exists(assetsDir)) return;

		for (String folder : new String[]{"textures", "models", "animations"}) {
			Path dir = assetsDir.resolve(folder);
			if (!Files.exists(dir)) continue;

			try (var stream = Files.list(dir)) {
				var files = stream.filter(Files::isRegularFile).toList();
				for (Path path : files) {
					String name = path.getFileName().toString();
					String packPath = remapToPackPath(folder, name, modelFileNames, textureFileNames, animationFileNames);
					if (packPath != null) {
						zipEntries.putIfAbsent(packPath, Files.readAllBytes(path));
					}
				}
			}
		}
	}

	private static void loadResourcePackFromZip(Path zipPath, Map<String, byte[]> zipEntries,
												Set<String> modelFileNames,
												Set<String> textureFileNames,
												Set<String> animationFileNames) throws IOException {
		System.out.println("[Pokeblocks] Loading resource pack from zip: " + zipPath);
		try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
			var entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) continue;

				String name = entry.getName();
				if (!name.startsWith("assets/")) continue;

				// Expected format: assets/<textures|models|animations>/filename
				String[] parts = name.split("/");
				if (parts.length != 3) continue;

				String folder = parts[1];
				String fileName = parts[2];

				String packPath = remapToPackPath(folder, fileName, modelFileNames, textureFileNames, animationFileNames);
				if (packPath == null) continue;

				try (InputStream in = zipFile.getInputStream(entry)) {
					zipEntries.putIfAbsent(packPath, in.readAllBytes());
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
			System.err.println("[Pokeblocks] Failed to compute SHA-1 for " + file + ": " + e);
			return "";
		}
	}
}