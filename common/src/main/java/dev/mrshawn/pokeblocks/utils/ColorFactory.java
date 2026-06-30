package dev.mrshawn.pokeblocks.utils;

import com.mojang.blaze3d.platform.NativeImage;
import dev.mrshawn.pokeblocks.item.DollRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.joml.Vector3f;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ColorFactory {

	private ColorFactory() {}

	/**
	 * Cache of the saturation-ranked wool list per texture. Sampling reads and iterates a PNG, and the
	 * result is fixed for a given texture, so we compute it once. Cleared on client resource reload
	 * (see {@link dev.mrshawn.pokeblocks.client.PokeblocksClient#reloadPokeblocksAssets}) since a
	 * resource pack can swap textures under the same id.
	 */
	private static final Map<ResourceLocation, List<Block>> DOMINANT_WOOL_CACHE = new ConcurrentHashMap<>();

	/**
	 * Cache of the average color per texture, shared by the break/sprint particle mixins. Same lifecycle
	 * as {@link #DOMINANT_WOOL_CACHE}: computed once per texture, cleared on client resource reload.
	 */
	private static final Map<ResourceLocation, Vector3f> AVERAGE_COLOR_CACHE = new ConcurrentHashMap<>();

	/**
	 * Per-pixel weight floor. The dominant-color vote weights each pixel by {@code BASE + saturation²}
	 * so a doll's vivid signature colors outrank large neutral/shaded areas, while this small floor keeps
	 * predominantly-neutral dolls from being hijacked by a few highly-saturated accent pixels.
	 */
	private static final double WOOL_WEIGHT_BASE = 0.1;

	public static ChatFormatting getFormattingFor(ItemStack stack) {
		return DollRarity.getRarity(stack).getFormatting();
	}

	public static Vector3f sampleAverageColor(ResourceLocation textureLoc, Vector3f fallBack) {
		try {
			Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(textureLoc);
			if (resource.isPresent()) {
				try (InputStream is = resource.get().open();
					 NativeImage image = NativeImage.read(is)) {
					long r = 0, g = 0, b = 0;
					int count = 0;
					for (int x = 0; x < image.getWidth(); x++) {
						for (int y = 0; y < image.getHeight(); y++) {
							int pixel = image.getPixelRGBA(x, y);
							int a = (pixel >> 24) & 0xFF;
							if (a < 128) continue;
							r += pixel & 0xFF;
							g += (pixel >> 8) & 0xFF;
							b += (pixel >> 16) & 0xFF;
							count++;
						}
					}
					if (count > 0) {
						return new Vector3f(
								(r / (float) count) / 255f,
								(g / (float) count) / 255f,
								(b / (float) count) / 255f
						);
					}
				}
			}
		} catch (Exception e) {}
		return fallBack;
	}

	/**
	 * Cached variant of {@link #sampleAverageColor}: samples each texture once and reuses the result.
	 * Used for the particle tint on doll break/sprint; the cache is cleared on resource reload.
	 */
	public static Vector3f sampleAverageColorCached(ResourceLocation textureLoc, Vector3f fallBack) {
		Vector3f cached = AVERAGE_COLOR_CACHE.get(textureLoc);
		if (cached != null) return cached;
		Vector3f color = sampleAverageColor(textureLoc, fallBack);
		AVERAGE_COLOR_CACHE.put(textureLoc, color);
		return color;
	}

	/**
	 * Returns the dominant wool colors of a texture, ordered by saturation-weighted prevalence so a doll's
	 * signature colors lead rather than the muddy average of all its pixels. Each opaque pixel votes for its
	 * closest wool weighted by {@code BASE + saturation²} (see {@link #WOOL_WEIGHT_BASE}); the top {@code count}
	 * swatches are returned. Results are cached per texture. Falls back to {@code fallback} if the texture
	 * can't be read or has no opaque pixels.
	 */
	public static List<Block> sampleDominantWools(ResourceLocation textureLoc, int count, List<Block> fallback) {
		List<Block> ranked = DOMINANT_WOOL_CACHE.get(textureLoc);
		if (ranked == null) {
			ranked = computeRankedWools(textureLoc);
			DOMINANT_WOOL_CACHE.put(textureLoc, ranked);
		}
		if (ranked.isEmpty()) return fallback;
		return new ArrayList<>(ranked.subList(0, Math.min(count, ranked.size())));
	}

	/** Clears all texture-derived color caches; call on client resource reload since textures may have changed. */
	public static void clearCaches() {
		DOMINANT_WOOL_CACHE.clear();
		AVERAGE_COLOR_CACHE.clear();
	}

	/**
	 * Reads the texture and ranks every represented wool by saturation-weighted prevalence.
	 * Returns an empty list (cached as a negative result) if the texture can't be read or is fully transparent.
	 */
	private static List<Block> computeRankedWools(ResourceLocation textureLoc) {
		try {
			Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(textureLoc);
			if (resource.isPresent()) {
				try (InputStream is = resource.get().open();
					 NativeImage image = NativeImage.read(is)) {
					double[] weights = new double[WoolColorMatcher.paletteSize()];
					boolean any = false;
					for (int x = 0; x < image.getWidth(); x++) {
						for (int y = 0; y < image.getHeight(); y++) {
							int pixel = image.getPixelRGBA(x, y);
							if (((pixel >> 24) & 0xFF) < 128) continue; // skip (semi-)transparent pixels
							int r = pixel & 0xFF;
							int g = (pixel >> 8) & 0xFF;
							int b = (pixel >> 16) & 0xFF;
							weights[WoolColorMatcher.closestIndex(r, g, b)] += pixelWeight(r, g, b);
							any = true;
						}
					}
					if (any) return WoolColorMatcher.topWeighted(weights, weights.length);
				}
			}
		} catch (Exception ignored) {
		}
		return List.of();
	}

	/**
	 * Vote weight for a single pixel: {@code BASE + saturation²} (HSV saturation), so vivid pixels
	 * dominate the ranking while large neutral areas still register.
	 */
	private static double pixelWeight(int r, int g, int b) {
		int max = Math.max(r, Math.max(g, b));
		int min = Math.min(r, Math.min(g, b));
		double sat = max == 0 ? 0.0 : (max - min) / (double) max;
		return WOOL_WEIGHT_BASE + sat * sat;
	}

}
