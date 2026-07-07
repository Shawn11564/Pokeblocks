package item;

import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityAcquisitionDivisors;
import dev.mrshawn.pokeblocks.item.DollRarityIgnoredFlags;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.FigurineDescriptionOverrides;
import dev.mrshawn.pokeblocks.item.FigurineNameOverrides;
import dev.mrshawn.pokeblocks.item.FigurineTagOverrides;
import dev.mrshawn.pokeblocks.item.RarityWeightConfig;
import dev.mrshawn.pokeblocks.item.ServerOverrides;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The pack-borne server override snapshot: while a snapshot is applied, every override holder
 * answers from the SERVER's values (remote replaces local — including absence); clearing it
 * restores local behavior. Also proves the server-side export → client-side parse roundtrip using
 * the real bundled config defaults.
 */
class ServerOverridesTest {

	@TempDir
	Path serverDir;

	@AfterEach
	void tearDown() {
		ServerOverrides.clear();
	}

	@Test
	void appliedSnapshotReplacesLocalAnswers() {
		String json = """
				{
				  "format": 1,
				  "doll_rarity": ["testmon legendary", "othermon shiny rare"],
				  "rarity_acquisition_divisors": ["testmon 4"],
				  "ignored_rarity_flags": ["noice", "testmon shiny"],
				  "rarity_weights": {"common": 123, "legendary": 7},
				  "figurine_names": ["somefig Fancy Fig Name"],
				  "figurine_descriptions": ["somefig A description with spaces"],
				  "figurine_tags": ["somefig cobblemon_team vip"]
				}
				""";
		ServerOverrides.applyJson(json);
		assertNotNull(ServerOverrides.current());

		assertEquals(DollRarity.LEGENDARY, DollRarityOverrides.getOverride("testmon", EnumSet.noneOf(ModelFlag.class)));
		assertEquals(DollRarity.RARE, DollRarityOverrides.getOverride("othermon", EnumSet.of(ModelFlag.SHINY)));
		assertNull(DollRarityOverrides.getOverride("unlisted", EnumSet.noneOf(ModelFlag.class)),
				"a key absent on the server must resolve as absent, whatever local files say");

		assertEquals(4, DollRarityAcquisitionDivisors.getAcquisitionDivisor("testmon", EnumSet.noneOf(ModelFlag.class)));
		assertEquals(1, DollRarityAcquisitionDivisors.getAcquisitionDivisor("othermon", EnumSet.noneOf(ModelFlag.class)));

		Set<ModelFlag> ignored = DollRarityIgnoredFlags.getIgnoredFlags("testmon");
		assertTrue(ignored.contains(ModelFlag.NOICE), "blanket rule applies to every pokemon");
		assertTrue(ignored.contains(ModelFlag.SHINY), "pokemon-specific rule applies");
		assertEquals(Set.of(ModelFlag.NOICE), DollRarityIgnoredFlags.getIgnoredFlags("othermon"));

		assertEquals(123, RarityWeightConfig.getWeight(DollRarity.COMMON));
		assertEquals(123, DollRarity.COMMON.getWeight(), "DollRarity.getWeight must honor the snapshot even uninitialized");
		assertEquals(7, DollRarity.LEGENDARY.getWeight());
		assertEquals(DollRarity.EPIC.getDefaultWeight(), DollRarity.EPIC.getWeight(),
				"weights absent from the snapshot fall back to defaults");

		assertEquals("Fancy Fig Name", FigurineNameOverrides.getOverride("somefig"));
		assertEquals("A description with spaces", FigurineDescriptionOverrides.getOverride("somefig"));
		assertTrue(FigurineTagOverrides.hasTag("somefig", "cobblemon_team"));
		assertTrue(FigurineTagOverrides.hasTag("somefig", "vip"));
		assertFalse(FigurineTagOverrides.hasTag("otherfig", "cobblemon_team"));
	}

	@Test
	void clearRestoresLocalBehavior() {
		ServerOverrides.applyJson("{\"format\":1,\"doll_rarity\":[\"testmon legendary\"]}");
		assertEquals(DollRarity.LEGENDARY, DollRarityOverrides.getOverride("testmon", EnumSet.noneOf(ModelFlag.class)));

		ServerOverrides.clear();
		assertNull(ServerOverrides.current());
		assertNull(DollRarityOverrides.getOverride("testmon", EnumSet.noneOf(ModelFlag.class)));
	}

	@Test
	void malformedJsonKeepsPreviousState() {
		ServerOverrides.applyJson("{\"format\":1,\"doll_rarity\":[\"testmon legendary\"]}");
		ServerOverrides.applyJson("this is not json {{{");
		assertNotNull(ServerOverrides.current(), "a malformed document must not wipe the active snapshot");
		assertEquals(DollRarity.LEGENDARY, DollRarityOverrides.getOverride("testmon", EnumSet.noneOf(ModelFlag.class)));
	}

	@Test
	void exportRoundtripsThroughApply() {
		// Load the real bundled defaults into the holders (extracted into a throwaway server dir),
		// export them the way the pack builder does, then parse the export like a client would.
		DollRarityOverrides.initialize(serverDir);
		DollRarityIgnoredFlags.initialize(serverDir);
		DollRarityAcquisitionDivisors.initialize(serverDir);
		FigurineNameOverrides.initialize(serverDir);
		FigurineDescriptionOverrides.initialize(serverDir);
		FigurineTagOverrides.initialize(serverDir);
		RarityWeightConfig.initialize(serverDir);

		// Snapshot a couple of LOCAL answers before applying the export as remote.
		DollRarity localVenusaur = DollRarityOverrides.getOverride("venusaur", EnumSet.noneOf(ModelFlag.class));
		assertNotNull(localVenusaur, "bundled doll_rarity.json is expected to configure venusaur");
		int localCommonWeight = RarityWeightConfig.getWeight(DollRarity.COMMON);

		ServerOverrides.applyJson(ServerOverrides.buildJson());
		assertNotNull(ServerOverrides.current());

		assertEquals(localVenusaur, DollRarityOverrides.getOverride("venusaur", EnumSet.noneOf(ModelFlag.class)),
				"export → parse must preserve rarity override entries");
		assertEquals(localCommonWeight, RarityWeightConfig.getWeight(DollRarity.COMMON),
				"export → parse must preserve rarity weights");
	}
}
