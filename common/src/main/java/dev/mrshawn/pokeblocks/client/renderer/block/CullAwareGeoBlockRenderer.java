package dev.mrshawn.pokeblocks.client.renderer.block;

import dev.mrshawn.pokeblocks.block.entity.custom.CustomDecorationBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.DecorativeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Base renderer for all doll block families, fixing render culling for models that extend past
 * their block — most visibly gigantic dolls (2x scale, up to ~3 blocks tall), which vanished while
 * still on-screen as soon as the camera looked slightly away from their base block.
 * <p>
 * Each loader culls block-entity renderers differently, so this fixes both mechanisms:
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
 */
public class CullAwareGeoBlockRenderer<T extends BlockEntity & GeoAnimatable> extends GeoBlockRenderer<T> {

	/**
	 * Extra blocks around the geo hitbox for culling: covers the strict box's conservative
	 * undershoot, the squish animation, and compendium/laser turning of asymmetric models
	 * (worst case, a gigantic doll's footprint diagonal swings ~0.95 blocks past its box).
	 */
	private static final double CULL_MARGIN = 1.0;

	public CullAwareGeoBlockRenderer(GeoModel<T> model) {
		super(model);
	}

	/**
	 * Keeps gigantic dolls rendering while their base block's chunk section is out of the frustum
	 * but the model itself is still on-screen. Evaluated at section compile; the gigantic flag
	 * syncs via {@code sendBlockUpdated}, which triggers a rebuild, so flips are picked up.
	 */
	@Override
	public boolean shouldRenderOffScreen(T blockEntity) {
		return isGigantic(blockEntity);
	}

	/**
	 * The box (Neo)Forge frustum-culls this renderer with: the geo-derived outline shape (already
	 * variant-, pose-, rotation- and gigantic-aware, cached in {@code DollShapes}) plus a margin.
	 * <p>
	 * Deliberately <b>no {@code @Override}</b>: the method only exists on NeoForge's patched
	 * {@code BlockEntityRenderer}. The loader modules recompile the common sources, so this becomes
	 * a real override there and stays an unused plain method on the other loaders.
	 */
	public AABB getRenderBoundingBox(T blockEntity) {
		BlockPos pos = blockEntity.getBlockPos();
		Level level = blockEntity.getLevel();
		if (level != null) {
			VoxelShape shape = blockEntity.getBlockState().getShape(level, pos);
			if (!shape.isEmpty()) {
				return shape.bounds().move(pos).inflate(CULL_MARGIN);
			}
		}
		// No level/shape to derive from: cover the largest possible doll instead of under-culling.
		return new AABB(pos).inflate(3.0);
	}

	private static boolean isGigantic(BlockEntity blockEntity) {
		return blockEntity instanceof PokedollBlockEntity doll ? doll.isGigantic()
				: blockEntity instanceof FigurineBlockEntity figurine ? figurine.isGigantic()
				: blockEntity instanceof DecorativeBlockEntity decorative ? decorative.isGigantic()
				: blockEntity instanceof CustomDecorationBlockEntity custom && custom.isGigantic();
	}
}
