package dev.mrshawn.pokeblocks.resourcepack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CustomPackBuilder {

	private static final String PACK_NAME = "pokeblocks_custom_pack.zip";

	public static Path findCustomDir(Path gameDir) {
		List<Path> candidates = List.of(
				gameDir.resolve("config").resolve("Pokeblocks").resolve("custom"),
				gameDir.resolve("config").resolve("pokeblocks").resolve("custom"),
				gameDir.resolve("config").resolve("Pokeblocks").resolve("pokeblocks").resolve("custom"),
				gameDir.resolve("pokeblocks").resolve("custom")
		);

		for (Path p : candidates) {
			if (Files.exists(p)) {
				try (var walk = Files.walk(p)) {
					if (walk.anyMatch(Files::isRegularFile)) return p;
				} catch (IOException ignored) {
				}
			}
		}

		for (Path p : candidates) {
			if (Files.exists(p)) return p;
		}
		return null;
	}

	public static Path buildResourcePack(Path gameDir) throws IOException {
		Path customDir = findCustomDir(gameDir);
		if (customDir == null) return null;

		Path finalZip = gameDir.resolve(PACK_NAME);
		Path tempZip = gameDir.resolve(PACK_NAME + ".tmp");

		Map<String, Path> entries = new TreeMap<>();
		int modelCount = 0;
		int textureCount = 0;

		// Add textures
		Path texturesDir = customDir.resolve("textures");
		if (Files.exists(texturesDir)) {
			try (var stream = Files.walk(texturesDir)) {
				var files = stream.filter(Files::isRegularFile).toList();
				for (Path path : files) {
					String rel = texturesDir.relativize(path).toString().replace("\\", "/");
					entries.put("assets/pokeblocks/textures/block/" + rel, path);
					textureCount++;
				}
			}
		}

		// Add models
		Path modelsDir = customDir.resolve("models");
		if (Files.exists(modelsDir)) {
			try (var stream = Files.walk(modelsDir)) {
				var files = stream.filter(Files::isRegularFile).toList();
				for (Path path : files) {
					String rel = modelsDir.relativize(path).toString().replace("\\", "/");
					entries.put("assets/pokeblocks/geo/block/" + rel, path);
					modelCount++;
				}
			}
		}

		// Assets folder (raw asset paths)
		Path assetsDir = customDir.resolve("assets");
		if (Files.exists(assetsDir)) {
			try (var stream = Files.walk(assetsDir)) {
				var files = stream.filter(Files::isRegularFile).toList();
				for (Path path : files) {
					String rel = assetsDir.relativize(path).toString().replace("\\", "/");
					entries.put("assets/" + rel, path);
				}
			}
		}

		// Direct files in custom folder
		if (Files.exists(customDir)) {
			try (var stream = Files.list(customDir)) {
				var files = stream.filter(Files::isRegularFile).toList();
				for (Path path : files) {
					String name = path.getFileName().toString();
					String lower = name.toLowerCase();
					String zipPath;
					if (lower.endsWith(".png")) {
						zipPath = "assets/pokeblocks/textures/block/" + name;
						textureCount++;
					} else if (lower.endsWith(".geo.json") || lower.endsWith(".json")) {
						zipPath = "assets/pokeblocks/geo/block/" + name;
						modelCount++;
					} else {
						zipPath = "assets/pokeblocks/extra/" + name;
					}
					entries.putIfAbsent(zipPath, path);
				}
			}
		}

		if (entries.isEmpty()) return null;

		System.out.println("[Pokeblocks] Custom resource pack: " + modelCount + " model(s), " + textureCount + " texture(s)");

		// Optional pack icon
		Path packIcon = customDir.resolve("pack.png");
		boolean hasPackIcon = Files.exists(packIcon);

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

			for (Map.Entry<String, Path> e : entries.entrySet()) {
				String zipName = e.getKey().replaceAll("^/+", "");
				ZipEntry entry = new ZipEntry(zipName);
				entry.setTime(0L);
				zip.putNextEntry(entry);
				try (InputStream in = Files.newInputStream(e.getValue())) {
					in.transferTo(zip);
				}
				zip.closeEntry();
			}
		}

		Files.move(tempZip, finalZip, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		return finalZip;
	}

	public static String computeSHA1(Path file) {
		try (InputStream in = Files.newInputStream(file)) {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			byte[] buf = new byte[8192];
			int len;
			while ((len = in.read(buf)) != -1) digest.update(buf, 0, len);
			StringBuilder sb = new StringBuilder();
			for (byte b : digest.digest()) sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (Exception e) {
			System.err.println("[Pokeblocks] Failed to compute SHA-1 for " + file + ": " + e);
			return "";
		}
	}
}
