package dev.mrshawn.pokeblocks.client.renderer.atlas;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntBinaryOperator;

/**
 * The pure (GL-free, testable) half of {@link DollTextureAtlas}: how a texture file maps to its sprite id
 * in the atlas, and how a stitched image is classified as needing real translucency.
 */
public final class DollAtlasNaming {

	private static final String TEXTURE_PREFIX = "textures/";
	private static final String TEXTURE_SUFFIX = ".png";

	private DollAtlasNaming() {}

	/**
	 * The sprite id the atlas's {@code directory} source gives a texture file, or {@code null} when the
	 * location isn't a stitchable texture path. {@code pokeblocks:textures/block/pokedoll_x_texture.png}
	 * becomes {@code pokeblocks:block/pokedoll_x_texture} (the source lists {@code textures/block} with the
	 * {@code block/} prefix, and vanilla drops the {@code .png}).
	 */
	@Nullable
	public static ResourceLocation spriteId(ResourceLocation texture) {
		String path = texture.getPath();
		if (!path.startsWith(TEXTURE_PREFIX) || !path.endsWith(TEXTURE_SUFFIX)) return null;
		if (path.length() <= TEXTURE_PREFIX.length() + TEXTURE_SUFFIX.length()) return null;
		return ResourceLocation.fromNamespaceAndPath(texture.getNamespace(),
				path.substring(TEXTURE_PREFIX.length(), path.length() - TEXTURE_SUFFIX.length()));
	}

	/**
	 * Whether any pixel of a {@code width}x{@code height} image has an alpha strictly between fully
	 * transparent and fully opaque. Such a texture must keep blending (the translucent render type);
	 * everything else renders identically on the cheaper cutout type, since both shaders discard the
	 * fully transparent pixels and draw the opaque ones as-is.
	 *
	 * @param pixelABGR {@code (x, y) -> packed ABGR pixel}, alpha in the top byte (NativeImage's layout)
	 */
	public static boolean hasPartialAlpha(int width, int height, IntBinaryOperator pixelABGR) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int alpha = pixelABGR.applyAsInt(x, y) >>> 24;
				if (alpha != 0 && alpha != 0xFF) return true;
			}
		}
		return false;
	}
}
