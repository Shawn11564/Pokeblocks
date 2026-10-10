package dev.mrshawn.pokeblocks.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.mrshawn.pokeblocks.block.entity.DollBlockEntity;
import dev.mrshawn.pokeblocks.client.compat.RenderProbe;
import dev.mrshawn.pokeblocks.client.renderer.atlas.DollRenderBatch;
import dev.mrshawn.pokeblocks.client.renderer.atlas.DollTextureAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Base renderer for all doll block families. Two jobs:
 *
 * <h2>Render culling</h2>
 * Fixes culling for models that extend past their block — most visibly gigantic dolls (2x scale, up to
 * ~3 blocks tall), which vanished while still on-screen as soon as the camera looked slightly away from
 * their base block. Each loader culls block-entity renderers differently, so this fixes both mechanisms:
 * <ul>
 *   <li><b>NeoForge</b> frustum-culls every block entity against the renderer's
 *       {@code getRenderBoundingBox}, whose default is the <b>unit cube</b> at the block pos —
 *       the aggressive culling. Overridden here with the real silhouette.</li>
 *   <li><b>Vanilla/Fabric</b> has no per-block-entity culling but only renders block entities of
 *       chunk sections still in the frustum; a gigantic doll poking out of its 16³ section could
 *       vanish while visible. {@link #shouldRenderOffScreen} makes gigantic dolls "global" block
 *       entities, which skip that check. (Neo)Forge still frustum-culls global block entities with
 *       their render bounding box, so going global costs nothing there.</li>
 *   <li><b>Forge</b> culls (both lists) against the block entity's default render box, which is
 *       already the block's collision shape = the geo-derived hitbox — correct without help.</li>
 * </ul>
 *
 * <h2>Per-frame cost</h2>
 * A doll-heavy scene profiled at 82% of the render thread inside block-entity rendering, so the three
 * biggest per-doll costs are taken out here, with identical output:
 * <ul>
 *   <li>Every doll draws through {@link DollTextureAtlas}'s single render type instead of one per texture,
 *       into the dolls' own {@link DollRenderBatch} buffers that are flushed once per frame, so all dolls
 *       in view become one draw instead of one per doll.</li>
 *   <li>Cube vertices are emitted from a {@link BakedBoneMesh} (one matrix per vertex) instead of
 *       GeckoLib's per-cube pose-stack replay.</li>
 *   <li>The NeoForge culling box is cached per block entity instead of walking the geo-derived shape
 *       lookup every frame.</li>
 * </ul>
 */
public class CullAwareGeoBlockRenderer<T extends BlockEntity & GeoAnimatable & DollBlockEntity> extends GeoBlockRenderer<T> {

	/**
	 * Extra blocks around the geo hitbox for culling: covers the strict box's conservative
	 * undershoot, the squish animation, and compendium/laser turning of asymmetric models
	 * (worst case, a gigantic doll's footprint diagonal swings ~0.95 blocks past its box).
	 */
	private static final double CULL_MARGIN = 1.0;

	/**
	 * Bumped on resource reload: a box cached while a doll's geo hadn't loaded yet (fallback shape)
	 * must be re-derived once the real shape is available, even though the block entity itself is unchanged.
	 */
	private static int reloadGeneration;

	/** A culling box and the inputs it was derived from. */
	private record CachedRenderBox(BlockState state, int dataVersion, int generation, AABB box) {}

	/** Per block entity (identity, weak so unloaded ones drop out). Render thread only. */
	private final Map<T, CachedRenderBox> renderBoxes = new WeakHashMap<>();

	/** The atlas region of the doll being rendered, or {@code null} when its texture isn't in the atlas. */
	@Nullable
	private TextureAtlasSprite sprite;

	/** Scratch for the (rare) per-cube path; render thread only. */
	private static final Vector3f SCRATCH_POSITION = new Vector3f();

	public CullAwareGeoBlockRenderer(GeoModel<T> model) {
		super(model);
	}

	/** Call on resource reload: cached culling boxes may have been derived from fallback shapes. */
	public static void invalidateRenderBoxes() {
		reloadGeneration++;
	}

	/**
	 * Keeps gigantic dolls rendering while their base block's chunk section is out of the frustum
	 * but the model itself is still on-screen. Evaluated at section compile; the gigantic flag
	 * syncs via {@code sendBlockUpdated}, which triggers a rebuild, so flips are picked up.
	 */
	@Override
	public boolean shouldRenderOffScreen(T blockEntity) {
		return blockEntity.isGigantic();
	}

	/**
	 * The box (Neo)Forge frustum-culls this renderer with: the geo-derived outline shape (already
	 * variant-, pose-, rotation- and gigantic-aware, cached in {@code DollShapes}) plus a margin.
	 * Cached per block entity and re-derived only when its block state or synced data changes; the
	 * shape lookup itself is cached but still allocated flag sets, keys and a regex match per call,
	 * which added up to 3.5% of the render thread when asked for every doll every frame.
	 * <p>
	 * Deliberately <b>no {@code @Override}</b>: the method only exists on NeoForge's patched
	 * {@code BlockEntityRenderer}. The loader modules recompile the common sources, so this becomes
	 * a real override there and stays an unused plain method on the other loaders.
	 */
	public AABB getRenderBoundingBox(T blockEntity) {
		BlockState state = blockEntity.getBlockState();
		int dataVersion = blockEntity.renderDataVersion();
		int generation = reloadGeneration;
		CachedRenderBox cached = this.renderBoxes.get(blockEntity);
		if (cached != null && cached.state() == state && cached.dataVersion() == dataVersion && cached.generation() == generation) {
			return cached.box();
		}
		AABB box = computeRenderBoundingBox(blockEntity, state);
		this.renderBoxes.put(blockEntity, new CachedRenderBox(state, dataVersion, generation, box));
		return box;
	}

	private static AABB computeRenderBoundingBox(BlockEntity blockEntity, BlockState state) {
		BlockPos pos = blockEntity.getBlockPos();
		Level level = blockEntity.getLevel();
		if (level != null) {
			VoxelShape shape = state.getShape(level, pos);
			if (!shape.isEmpty()) {
				return shape.bounds().move(pos).inflate(CULL_MARGIN);
			}
		}
		// No level/shape to derive from: cover the largest possible doll instead of under-culling.
		return new AABB(pos).inflate(3.0);
	}

	// --- Atlas render type -------------------------------------------------------------------------

	/**
	 * A doll whose texture is in the atlas renders into {@link DollRenderBatch}'s buffers rather than the
	 * shared one vanilla passes in, so nothing interleaved in the section order can end its batch; the
	 * loader hook draws the whole batch after all block entities.
	 * <p>
	 * Only when handed the level renderer's own main buffer source, though. Anything else must be honoured
	 * as given: vanilla wraps the source in a crumbling-overlay consumer for the block being mined, shader
	 * packs render block entities again into their own sources for shadow passes, and a GUI could render
	 * a block entity outside the level pass altogether. Those still get the atlas render type, just with
	 * vanilla's per-block-entity flushing, so they look the same as before.
	 */
	@Override
	public void render(T animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		MultiBufferSource target = bufferSource;
		if (bufferSource == Minecraft.getInstance().renderBuffers().bufferSource()
				&& DollTextureAtlas.lookup(getTextureLocation(animatable)) != null) {
			target = DollRenderBatch.source();
			RenderProbe.batchedBlockEntities++;
		}
		RenderProbe.blockEntities++;
		super.render(animatable, partialTick, poseStack, target, packedLight, packedOverlay);
	}

	/**
	 * Draws through the shared doll atlas whenever the texture has been stitched into it; otherwise the
	 * model's own (per-texture) render type, exactly as before.
	 */
	@Override
	public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
		DollTextureAtlas.Entry entry = DollTextureAtlas.lookup(texture);
		if (entry == null) {
			this.sprite = null;
			return super.getRenderType(animatable, texture, bufferSource, partialTick);
		}
		this.sprite = entry.sprite();
		return entry.renderType();
	}

	/**
	 * GeckoLib's hook for {@code .mcmeta}-animated standalone textures (none of the doll textures are one).
	 * It re-resolves the texture and binds it directly, which is wasted work and, with the atlas, would
	 * also load every standalone doll texture into VRAM a second time. The atlas animates its own sprites.
	 */
	@Override
	public void updateAnimatedTextureFrame(T animatable) {
		if (this.sprite == null) super.updateAnimatedTextureFrame(animatable);
	}

	@Override
	public void doPostRenderCleanup() {
		super.doPostRenderCleanup();
		this.sprite = null;
	}

	// --- Baked cube emission ------------------------------------------------------------------------

	/** Emits the bone's cubes from their baked mesh; hidden bones are skipped as GeckoLib skips them. */
	@Override
	public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
		if (bone.isHidden()) return;
		BakedBoneMesh.of(bone).emit(poseStack.last(), buffer, this.sprite, packedLight, packedOverlay, colour);
	}

	/**
	 * The per-cube path is still taken by subclasses that pose individual cubes (the figurine renderer's
	 * limb swings call {@code renderCube} directly); this keeps that path allocation-free and atlas-aware.
	 */
	@Override
	public void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
		TextureAtlasSprite atlasSprite = this.sprite;
		Vector3f position = SCRATCH_POSITION;
		for (GeoVertex vertex : quad.vertices()) {
			poseState.transformPosition(vertex.position(), position);
			float u = vertex.texU();
			float v = vertex.texV();
			if (atlasSprite != null) {
				u = atlasSprite.getU(u);
				v = atlasSprite.getV(v);
			}
			buffer.addVertex(position.x, position.y, position.z, colour, u, v, packedOverlay, packedLight,
					normal.x(), normal.y(), normal.z());
		}
	}
}
