package render;

import dev.mrshawn.pokeblocks.client.renderer.atlas.DollAtlasNaming;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The doll atlas's texture-to-sprite naming and its translucency classification. */
class DollAtlasNamingTest {

	@Test
	void textureFileMapsToDirectorySourceSpriteId() {
		assertEquals(ResourceLocation.fromNamespaceAndPath("pokeblocks", "block/pokedoll_snorunt_shiny_texture"),
				DollAtlasNaming.spriteId(ResourceLocation.fromNamespaceAndPath("pokeblocks", "textures/block/pokedoll_snorunt_shiny_texture.png")));
		assertEquals(ResourceLocation.fromNamespaceAndPath("pokeblocks", "block/amongsans1015_figurine"),
				DollAtlasNaming.spriteId(ResourceLocation.fromNamespaceAndPath("pokeblocks", "textures/block/amongsans1015_figurine.png")));
	}

	@Test
	void nonTexturePathsAreNotStitchable() {
		assertNull(DollAtlasNaming.spriteId(ResourceLocation.fromNamespaceAndPath("pokeblocks", "geo/block/pokedoll_x.geo.json")));
		assertNull(DollAtlasNaming.spriteId(ResourceLocation.fromNamespaceAndPath("pokeblocks", "textures/block/x.png.mcmeta")));
		assertNull(DollAtlasNaming.spriteId(ResourceLocation.fromNamespaceAndPath("pokeblocks", "textures/.png")));
	}

	@Test
	void onlyPartialAlphaNeedsBlending() {
		int opaque = 0xFF_00_00_00;
		int clear = 0x00_FF_FF_FF;
		int half = 0x80_10_20_30;
		int[][] cutout = {{opaque, clear}, {clear, opaque}};
		int[][] translucent = {{opaque, clear}, {half, opaque}};
		assertFalse(DollAtlasNaming.hasPartialAlpha(2, 2, (x, y) -> cutout[y][x]), "opaque + fully transparent pixels only");
		assertTrue(DollAtlasNaming.hasPartialAlpha(2, 2, (x, y) -> translucent[y][x]), "one half-transparent pixel");
		assertFalse(DollAtlasNaming.hasPartialAlpha(0, 0, (x, y) -> half), "empty image");
	}
}
