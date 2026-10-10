package dev.mrshawn.pokeblocks.client.renderer.atlas;

import com.mojang.blaze3d.platform.NativeImage;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * One texture atlas holding every {@code pokeblocks:textures/block/*.png} (all doll, figurine and decoration
 * skins, including pack-added and server-pushed ones), so every placed doll can render through a single
 * {@link RenderType} instead of one per texture.
 * <p>
 * <b>Why:</b> vanilla's block-entity buffer flushes a draw call every time consecutive block entities use a
 * different render type, and GeckoLib's default {@code entityTranslucent(texture)} is a different type per
 * texture. A mixed doll collection therefore cost one upload + one CPU quad sort + one draw <em>per doll</em>
 * (a profiled 37% of the render thread). Sharing one atlas-backed type lets vanilla consolidate all dolls
 * in view into one batch.
 * <p>
 * <b>Fidelity:</b> the atlas is stitched at mip level 0 with nearest filtering, exactly like the standalone
 * textures GeckoLib binds, so sampling is unchanged. Textures with partial alpha (a handful: the fishbowl,
 * rabsca, the mareep line) keep the translucent type; the rest use cutout, whose shader discards the same
 * fully-transparent pixels and draws the same opaque ones, so nothing visible changes: see-through gaps
 * in a texture stay see-through.
 * <p>
 * Registered as a client resource-reload listener on every loader (see the loader client classes), so it
 * rebuilds whenever packs change. Renderers ask {@link #lookup} per frame; a texture the atlas doesn't hold
 * (or one asked for before the first stitch) returns {@code null} and the renderer falls back to GeckoLib's
 * per-texture path, so nothing ever fails to draw. Render thread only.
 */
public final class DollTextureAtlas implements PreparableReloadListener {

	/** The atlas texture, as registered with the {@link net.minecraft.client.renderer.texture.TextureManager}. */
	public static final ResourceLocation ATLAS_LOCATION =
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "textures/atlas/dolls.png");
	/** {@code assets/pokeblocks/atlases/dolls.json}: the sprite sources. */
	private static final ResourceLocation ATLAS_INFO =
			ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, "dolls");

	private static final DollTextureAtlas INSTANCE = new DollTextureAtlas();

	/** A stitched texture: its atlas region and the (shared) render type to draw it with. */
	public record Entry(TextureAtlasSprite sprite, RenderType renderType) {}

	/** A finished stitch plus the sprites found to need blending, handed from the prepare stage to apply. */
	private record Prepared(SpriteLoader.Preparations preparations, Set<ResourceLocation> translucent) {}

	@Nullable
	private TextureAtlas atlas;
	private Set<ResourceLocation> translucentSprites = Set.of();
	/** Texture location to its entry; an empty value records "not in the atlas" so misses are cheap too. */
	private final Map<ResourceLocation, Optional<Entry>> lookups = new HashMap<>();
	@Nullable
	private static RenderType cutoutType;
	@Nullable
	private static RenderType translucentType;

	private DollTextureAtlas() {}

	public static DollTextureAtlas get() {
		return INSTANCE;
	}

	/**
	 * The atlas entry for a texture file location, or {@code null} when the atlas doesn't hold it
	 * (not a {@code textures/...png} path, not under {@code textures/block}, missing, or not stitched yet).
	 */
	@Nullable
	public static Entry lookup(ResourceLocation texture) {
		return INSTANCE.lookupEntry(texture);
	}

	@Nullable
	private Entry lookupEntry(ResourceLocation texture) {
		Optional<Entry> cached = this.lookups.get(texture);
		if (cached != null) return cached.orElse(null);
		Entry entry = resolve(texture);
		this.lookups.put(texture, Optional.ofNullable(entry));
		return entry;
	}

	@Nullable
	private Entry resolve(ResourceLocation texture) {
		if (this.atlas == null) return null;
		ResourceLocation spriteId = DollAtlasNaming.spriteId(texture);
		if (spriteId == null) return null;
		TextureAtlasSprite sprite = this.atlas.getSprite(spriteId);
		if (sprite == null || MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) return null;
		return new Entry(sprite, this.translucentSprites.contains(spriteId) ? translucentRenderType() : cutoutRenderType());
	}

	/** The shared type for atlas textures without partial alpha (no blending, no per-frame quad sort). */
	public static RenderType cutoutRenderType() {
		if (cutoutType == null) cutoutType = RenderType.entityCutoutNoCull(ATLAS_LOCATION);
		return cutoutType;
	}

	/** The shared type for the few atlas textures that do have partial alpha. */
	public static RenderType translucentRenderType() {
		if (translucentType == null) translucentType = RenderType.entityTranslucent(ATLAS_LOCATION);
		return translucentType;
	}

	/** Creates and registers the atlas texture on first use (on the game thread, once the texture manager exists). */
	private TextureAtlas atlas() {
		if (this.atlas == null) {
			this.atlas = new TextureAtlas(ATLAS_LOCATION);
			Minecraft.getInstance().getTextureManager().register(ATLAS_LOCATION, this.atlas);
		}
		return this.atlas;
	}

	/**
	 * Same flow as vanilla's {@code TextureAtlasHolder} (whose reload is final and apply private), plus a
	 * pass over the stitched images in the background stage to find the ones that need blending.
	 */
	@Override
	public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager,
										  ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler,
										  Executor backgroundExecutor, Executor gameExecutor) {
		TextureAtlas target = atlas();
		return SpriteLoader.create(target)
				.loadAndStitch(resourceManager, ATLAS_INFO, 0, backgroundExecutor)
				.thenApply(preparations -> new Prepared(preparations, findTranslucent(resourceManager, preparations)))
				.thenCompose(prepared -> prepared.preparations().waitForUpload().thenApply(ignored -> prepared))
				.thenCompose(barrier::wait)
				.thenAcceptAsync(prepared -> apply(target, prepared, reloadProfiler), gameExecutor);
	}

	private void apply(TextureAtlas target, Prepared prepared, ProfilerFiller profiler) {
		profiler.startTick();
		profiler.push("upload");
		target.upload(prepared.preparations());
		this.translucentSprites = prepared.translucent();
		this.lookups.clear();
		profiler.pop();
		profiler.endTick();
		PokeblocksLog.LOGGER.info("Stitched {} doll textures into {} ({} translucent)",
				prepared.preparations().regions().size(), ATLAS_LOCATION, prepared.translucent().size());
	}

	/**
	 * Sprite ids whose image has partial alpha. Runs in the background stage, decoding each stitched
	 * texture's file once more (vanilla keeps the stitched pixels private without a loader-specific
	 * accessor; a few hundred 64x64 PNGs are a few milliseconds). A texture that can't be read is
	 * treated as translucent, which is the safe (previous) behaviour.
	 */
	private static Set<ResourceLocation> findTranslucent(ResourceManager resourceManager, SpriteLoader.Preparations preparations) {
		Set<ResourceLocation> translucent = new HashSet<>();
		for (ResourceLocation spriteId : preparations.regions().keySet()) {
			if (needsBlending(resourceManager, spriteId)) translucent.add(spriteId);
		}
		return Set.copyOf(translucent);
	}

	private static boolean needsBlending(ResourceManager resourceManager, ResourceLocation spriteId) {
		if (MissingTextureAtlasSprite.getLocation().equals(spriteId)) return false;
		ResourceLocation file = ResourceLocation.fromNamespaceAndPath(spriteId.getNamespace(), "textures/" + spriteId.getPath() + ".png");
		Optional<Resource> resource = resourceManager.getResource(file);
		if (resource.isEmpty()) return true;
		try (InputStream in = resource.get().open(); NativeImage image = NativeImage.read(in)) {
			return DollAtlasNaming.hasPartialAlpha(image.getWidth(), image.getHeight(), image::getPixelRGBA);
		} catch (IOException | RuntimeException e) {
			return true;
		}
	}
}
