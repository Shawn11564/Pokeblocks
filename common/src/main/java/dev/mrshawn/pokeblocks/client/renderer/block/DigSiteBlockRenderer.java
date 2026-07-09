package dev.mrshawn.pokeblocks.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.mrshawn.pokeblocks.block.custom.DigSiteBlock;
import dev.mrshawn.pokeblocks.block.entity.custom.DigSiteBlockEntity;
import dev.mrshawn.pokeblocks.client.model.item.PokedollItemModel;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import dev.mrshawn.pokeblocks.shape.HeadFit;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Draws the item a dig site is about to yield, climbing out of the mound as it is scooped — the same
 * reveal the vanilla suspicious-sand brush gives (see {@code BrushableBlockRenderer}). Both the item
 * and its emergence come straight from the {@link DigSiteBlockEntity} and the block's
 * {@link DigSiteBlock#STAGE}; nothing here is authoritative. The block still renders its normal mound
 * model — this only adds the emerging item on top.
 * <p>
 * The item sits on the block's center axis at a stable pseudo-random yaw (buried things don't face
 * the grid), seated so {@link #EMERGE_FRACTION} of its own visual height shows above the mound
 * surface at each stage: dolls measure themselves via {@link DollShapes#modelBounds} (the same
 * idle-pose bounds the head fit uses), junk is a flat 1x1 sprite. Placement math relies on vanilla
 * {@code ItemRenderer}'s {@code translate(-0.5, -0.5, -0.5)} before the custom renderer and
 * GeckoLib's {@code translate(0.5, 0.51, 0.5)} after it cancelling out (see {@link HeadFit}), so a
 * doll's geo origin — its bottom-center — lands exactly on the pose position, and a junk sprite
 * renders centred on it. Stage changes ease over a few ticks ({@link DigSiteBlockEntity#easeRenderY})
 * so each scoop reads as the item rising from the soil — and the mound refilling as it sinking back
 * under.
 */
public class DigSiteBlockRenderer implements BlockEntityRenderer<DigSiteBlockEntity> {

	private static final float ITEM_SCALE = 0.5f;
	/**
	 * Fraction of the reveal item's own visual height standing proud of the mound surface, per dig
	 * stage: tucked fully under the soil at stage 0 (with margin so an idle animation's bob can't
	 * poke through), just the head peeking after the first scoop, and mostly free — feet still
	 * planted in the crater — one scoop before it pops out.
	 */
	private static final float[] EMERGE_FRACTION = {-0.15f, 0.25f, 0.55f, 0.85f};
	/** Bounds when a doll's geo model can't be resolved: {@code DollShapes.DEFAULT_SHAPE} in model space. */
	private static final double[] FALLBACK_BOUNDS = {-0.25, 0, -0.25, 0.25, 0.75, 0.25};

	private final ItemRenderer itemRenderer;
	// The item model's own per-stack geo/animation resolution, so bounds match what actually renders.
	private final PokedollItemModel dollModel = new PokedollItemModel();

	public DigSiteBlockRenderer(BlockEntityRendererProvider.Context context) {
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(DigSiteBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffer,
					   int packedLight, int packedOverlay) {
		if (be.getLevel() == null) return;
		ItemStack stack = be.getRevealItem();
		if (stack.isEmpty()) return;

		BlockState state = be.getBlockState();
		if (!state.hasProperty(DigSiteBlock.STAGE)) return;
		int stage = state.getValue(DigSiteBlock.STAGE);

		// The item's visual top/bottom relative to the pose position, at render scale.
		float top;
		float bottom;
		if (stack.getItem() instanceof PokedollItem) {
			// Gigantic dolls grow a further 1.5x inside the item renderer (base-anchored, see
			// HeadFit.anchoredScaleOps), so their bounds scale with it while the pose scale stays ours.
			float scale = ITEM_SCALE * (PokedollItem.getFlagsFromStack(stack).contains(ModelFlag.GIGANTIC)
					? HeadFit.GIGANTIC_MULTIPLIER : 1f);
			double[] bounds = dollBounds(stack);
			top = (float) (bounds[4] * scale);
			bottom = (float) (bounds[1] * scale);
		} else {
			top = ITEM_SCALE * 0.5f;
			bottom = -ITEM_SCALE * 0.5f;
		}

		float height = top - bottom;
		float target = DigSiteBlock.surfaceHeight(stage) - top + EMERGE_FRACTION[stage] * height;
		float buried = DigSiteBlock.surfaceHeight(0) - top + EMERGE_FRACTION[0] * height;
		double clock = be.getLevel().getGameTime() + partialTick;
		// A fresh reveal (first scoop) rises out of the soil; a chunk reloaded mid-dig doesn't replay that.
		float y = be.easeRenderY(target, stage <= 1 ? buried : target, clock);

		pose.pushPose();
		pose.translate(0.5, y, 0.5);
		pose.mulPose(Axis.YP.rotationDegrees(buriedYaw(be.getBlockPos())));
		pose.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
		itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
				pose, buffer, be.getLevel(), 0);
		pose.popPose();
	}

	/** The doll's outer render bounds in its idle pose, from the same geo/animation the stack resolves to. */
	private double[] dollBounds(ItemStack stack) {
		dollModel.setCurrentItemStack(stack);
		PokedollItem item = (PokedollItem) stack.getItem();
		double[] bounds = DollShapes.modelBounds(
				dollModel.getModelResource(item).getPath(),
				dollModel.getAnimationResource(item).getPath());
		return bounds != null ? bounds : FALLBACK_BOUNDS;
	}

	/** Stable per-site yaw so buried items surface at a natural random orientation. */
	private static float buriedYaw(BlockPos pos) {
		return Math.floorMod(Mth.getSeed(pos.getX(), pos.getY(), pos.getZ()), 360);
	}
}
