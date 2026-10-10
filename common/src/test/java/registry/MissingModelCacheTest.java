package registry;

import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * An id whose model doesn't exist must be probed once, not once per frame: the renderer hooks validate the
 * id on every render, and each probe is a filesystem {@code exists} on the render thread. The negative
 * result is cached until the next resource reload, when the model may have appeared.
 */
class MissingModelCacheTest {

	/** A resource manager with nothing in it that counts how often it is asked. */
	private static final class EmptyCountingResourceManager implements ResourceManager {
		int probes;

		@Override
		public Optional<Resource> getResource(ResourceLocation location) {
			this.probes++;
			return Optional.empty();
		}

		@Override
		public Set<String> getNamespaces() {
			return Set.of();
		}

		@Override
		public List<Resource> getResourceStack(ResourceLocation location) {
			return List.of();
		}

		@Override
		public Map<ResourceLocation, Resource> listResources(String path, Predicate<ResourceLocation> filter) {
			return Map.of();
		}

		@Override
		public Map<ResourceLocation, List<Resource>> listResourceStacks(String path, Predicate<ResourceLocation> filter) {
			return Map.of();
		}

		@Override
		public Stream<PackResources> listPacks() {
			return Stream.empty();
		}
	}

	@Test
	void missingFigurineIsProbedOncePerReload() {
		EmptyCountingResourceManager rm = new EmptyCountingResourceManager();
		PokeblocksAssetResolver.clearFigurineCache();

		assertEquals(ModSettings.DEFAULT_FIGURINE, PokeblocksAssetResolver.validatedFigurine(rm, "ghost_figurine_a"));
		assertEquals(ModSettings.DEFAULT_FIGURINE, PokeblocksAssetResolver.validatedFigurine(rm, "ghost_figurine_a"));
		assertEquals(ModSettings.DEFAULT_FIGURINE, PokeblocksAssetResolver.validatedFigurine(rm, "ghost_figurine_a"));
		assertEquals(1, rm.probes, "one disk probe for a missing figurine, however often it renders");

		PokeblocksAssetResolver.clearFigurineCache();
		PokeblocksAssetResolver.validatedFigurine(rm, "ghost_figurine_a");
		assertEquals(2, rm.probes, "re-probed after a resource reload");
	}

	@Test
	void missingDecorationIsProbedOncePerReload() {
		EmptyCountingResourceManager rm = new EmptyCountingResourceManager();
		PokeblocksAssetResolver.clearDecorationCache();

		PokeblocksAssetResolver.validatedDecoration(rm, "ghost_decoration_a");
		PokeblocksAssetResolver.validatedDecoration(rm, "ghost_decoration_a");
		assertEquals(1, rm.probes);

		PokeblocksAssetResolver.clearDecorationCache();
		PokeblocksAssetResolver.validatedDecoration(rm, "ghost_decoration_a");
		assertEquals(2, rm.probes);
	}

	@Test
	void missingPokemonIsProbedOncePerReload() {
		EmptyCountingResourceManager rm = new EmptyCountingResourceManager();
		PokeblocksAssetResolver.clearPokemonCache();

		assertEquals(ModSettings.DEFAULT_POKEMON, PokeblocksAssetResolver.validatedPokemon(rm, "ghost_pokemon_a"));
		assertEquals(ModSettings.DEFAULT_POKEMON, PokeblocksAssetResolver.validatedPokemon(rm, "ghost_pokemon_a"));
		assertEquals(1, rm.probes, "an unregistered pokemon has no variant models to probe either");

		PokeblocksAssetResolver.clearPokemonCache();
		PokeblocksAssetResolver.validatedPokemon(rm, "ghost_pokemon_a");
		assertEquals(2, rm.probes);
	}
}
