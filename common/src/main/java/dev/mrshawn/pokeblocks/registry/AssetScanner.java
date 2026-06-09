package dev.mrshawn.pokeblocks.registry;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Shared file-discovery helpers for the asset-scanning registries
 * ({@link PokemonRegistry}, {@link FigurineRegistry}). Centralizes the classpath/jar walking and the
 * resource-pack listing that those registries used to each carry their own near-identical copy of.
 */
public final class AssetScanner {
	// Use a standalone logger (via the compile-time-constant MOD_ID) rather than PokeblocksCommon.LOGGER:
	// the asset-scanning registries reference these helpers during static init, which runs in unit tests
	// that don't bootstrap a platform — touching PokeblocksCommon would trip its ServiceLoader lookup.
	private static final Logger LOGGER = LoggerFactory.getLogger(PokeblocksCommon.MOD_ID);

	private AssetScanner() {}

	/**
	 * Walks a single classpath directory (one level deep) and returns the bare file names of regular
	 * files whose lowercased name satisfies {@code nameFilter}. Handles both exploded (dev) and jar
	 * ({@code jar:} URI) classpaths. Returns an empty set if the directory is absent or unreadable.
	 */
	public static Set<String> scanClasspath(String resourceDir, Predicate<String> nameFilter) {
		Set<String> output = new TreeSet<>();
		try {
			URL dirUrl = AssetScanner.class.getClassLoader().getResource(resourceDir);
			if (dirUrl == null) return output;

			URI uri = dirUrl.toURI();
			Path dirPath;

			if (uri.getScheme().equals("jar")) {
				FileSystem fs;
				try {
					fs = FileSystems.getFileSystem(uri);
				} catch (FileSystemNotFoundException e) {
					fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
				}
				dirPath = fs.getPath(resourceDir);
			} else {
				dirPath = Paths.get(uri);
			}

			try (Stream<Path> walk = Files.walk(dirPath, 1)) {
				walk.filter(Files::isRegularFile)
						.map(p -> p.getFileName().toString())
						.filter(name -> nameFilter.test(name.toLowerCase()))
						.forEach(output::add);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to scan classpath directory '{}'", resourceDir, e);
		}
		return output;
	}

	/**
	 * Lists every {@code pokeblocks}-namespaced resource under {@code dir} ending in {@code extension},
	 * returning the bare file names whose name passes {@code nameFilter}.
	 */
	static Set<String> listResourceFilenames(ResourceManager resourceManager, String dir, String extension, Predicate<String> nameFilter) {
		Set<String> output = new TreeSet<>();
		resourceManager.listResources(dir, loc -> loc.getPath().endsWith(extension))
				.keySet().stream()
				.filter(loc -> loc.getNamespace().equals(PokeblocksCommon.MOD_ID))
				.forEach(loc -> {
					String filename = loc.getPath().substring(loc.getPath().lastIndexOf('/') + 1);
					if (nameFilter.test(filename)) output.add(filename);
				});
		return output;
	}

	/**
	 * Partitions raw scan results into pokedoll and figurine buckets by the {@code _figurine} marker.
	 * Animations belong only to pokedolls.
	 */
	public static SplitAssets split(Set<String> models, Set<String> textures, Set<String> animations) {
		Set<String> pokedollModels = new TreeSet<>();
		Set<String> pokedollTextures = new TreeSet<>();
		Set<String> figurineModels = new TreeSet<>();
		Set<String> figurineTextures = new TreeSet<>();

		for (String f : models) {
			if (f.contains("_figurine")) figurineModels.add(f);
			else pokedollModels.add(f);
		}
		for (String f : textures) {
			if (f.contains("_figurine")) figurineTextures.add(f);
			else pokedollTextures.add(f);
		}

		return new SplitAssets(pokedollModels, pokedollTextures, new TreeSet<>(animations), figurineModels, figurineTextures);
	}

	/** Raw scan results bucketed into pokedoll vs figurine assets. */
	public record SplitAssets(
			Set<String> pokedollModels,
			Set<String> pokedollTextures,
			Set<String> pokedollAnimations,
			Set<String> figurineModels,
			Set<String> figurineTextures
	) {}
}
