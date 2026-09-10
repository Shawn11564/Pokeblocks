package dev.mrshawn.pokeblocks.client.model.block;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.block.FigurinePose;
import dev.mrshawn.pokeblocks.block.entity.custom.FigurineBlockEntity;
import dev.mrshawn.pokeblocks.client.model.PokeblocksAssetResolver;
import dev.mrshawn.pokeblocks.client.model.entity.FigurineEntityModel;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class FigurineModel extends DefaultedBlockGeoModel<FigurineBlockEntity> {

    // Frozen boxless-doll pose angles (radians) for models with REAL sided limb bones — the
    // bone-level counterpart of FigurineBlockRenderer's cube-level pose (which only covers rigid
    // one-bone figures). Values sample FigurineEntityModel's live amplitudes at a natural point.
    private static final float POSE_SIT_LEG_FOLD = 1.4f;
    private static final float POSE_SIT_SINK_LEGGED_PIXELS = 2.0f;
    private static final float POSE_WALK_LEG = 0.6f;
    private static final float POSE_WALK_ARM = 0.5f;
    private static final float POSE_STRIKE_ARM = 1.0f;
    private static final float POSE_STRIKE_LEG = 0.5f;

    public FigurineModel() {
        super(ResourceLocation.fromNamespaceAndPath(PokeblocksCommon.MOD_ID, ModSettings.DEFAULT_FIGURINE + "_figurine"));
    }

    @Override
    public BakedGeoModel getBakedModel(ResourceLocation location) {
        try {
            return super.getBakedModel(location);
        } catch (RuntimeException e) {
            return super.getBakedModel(PokeblocksAssetResolver.figurineModel(ModSettings.DEFAULT_FIGURINE));
        }
    }

    @Override
    public ResourceLocation getModelResource(FigurineBlockEntity animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        String figurine = PokeblocksAssetResolver.validatedFigurine(rm, animatable.getFigurine());
        return PokeblocksAssetResolver.figurineModel(rm, figurine, animatable.getFigurineFlags());
    }

    @Override
    public ResourceLocation getTextureResource(FigurineBlockEntity animatable) {
        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        String figurine = PokeblocksAssetResolver.validatedFigurine(rm, animatable.getFigurine());
        return PokeblocksAssetResolver.figurineTexture(rm, figurine, animatable.getFigurineFlags());
    }

    @Override
    public ResourceLocation getAnimationResource(FigurineBlockEntity animatable) {
        return null;
    }

    @Override
    public RenderType getRenderType(FigurineBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    /**
     * Poses a boxless figurine doll's REAL limb bones for its frozen {@link FigurinePose} — sided
     * legs fold to sit (the figure sinking a leg's length), freeze mid-stride, or kick/punch for
     * the strike. Rigid one-bone figures are posed at the cube level by {@code FigurineBlockRenderer}
     * instead; boxed figurines (and standing dolls) write nothing. Whole-figure tilt (sit lean,
     * strike lunge) is the renderer's job for every boxless doll, so it isn't duplicated here.
     * Bone writes follow the entity model's conventions: initial-snapshot + delta, only the
     * outermost limb bone of a chain, markers cleared afterwards (the baked model is shared with
     * the item/entity render paths).
     */
    @Override
    public void setCustomAnimations(FigurineBlockEntity animatable, long instanceId, AnimationState<FigurineBlockEntity> state) {
        if (animatable == null || !animatable.isBoxless() || animatable.getPose() == FigurinePose.STANDING) {
            return;
        }
        FigurinePose pose = animatable.getPose();
        boolean wroteAny = false;
        boolean hasSidedLegBones = false;

        for (GeoBone bone : getAnimationProcessor().getRegisteredBones()) {
            String name = bone.getName();
            if (ModSettings.FIGURINE_BOX_BONE.equals(name)) continue;
            if (!FigurineEntityModel.isLimb(name) || !FigurineEntityModel.hasSide(name)) continue;
            if (hasSidedLimbAncestor(bone)) continue;

            boolean left = FigurineEntityModel.isLeft(name);
            boolean arm = FigurineEntityModel.isArm(name);
            if (!arm) {
                hasSidedLegBones = true;
            }
            float delta = switch (pose) {
                case STANDING -> 0.0f;
                case SITTING -> arm ? 0.0f : POSE_SIT_LEG_FOLD;
                case WALKING -> arm
                        ? (left ? -POSE_WALK_ARM : POSE_WALK_ARM)
                        : (left ? POSE_WALK_LEG : -POSE_WALK_LEG);
                case STRIKING -> arm ? POSE_STRIKE_ARM : POSE_STRIKE_LEG;
            };
            if (delta != 0.0f) {
                bone.setRotX(bone.getInitialSnapshot().getRotX() + delta);
                wroteAny = true;
            }
        }

        // Sitting on real folded legs sinks the figure; cube-legged models sink in the renderer.
        if (pose == FigurinePose.SITTING && hasSidedLegBones) {
            for (GeoBone bone : getAnimationProcessor().getRegisteredBones()) {
                if (bone.getParent() == null && !ModSettings.FIGURINE_BOX_BONE.equals(bone.getName())) {
                    bone.setPosY(-POSE_SIT_SINK_LEGGED_PIXELS);
                    wroteAny = true;
                }
            }
        }

        if (wroteAny) {
            // Same marker hygiene as FigurineEntityModel#clearSharedBoneMarkers: the baked model is
            // shared with the figurine item and entity renderers — a left-set marker would make
            // their reset-to-default pass skip the bone and inherit this doll's pose.
            for (GeoBone bone : getAnimationProcessor().getRegisteredBones()) {
                bone.resetStateChanges();
            }
        }
    }

    private static boolean hasSidedLimbAncestor(GeoBone bone) {
        for (GeoBone parent = bone.getParent(); parent != null; parent = parent.getParent()) {
            if (FigurineEntityModel.isLimb(parent.getName()) && FigurineEntityModel.hasSide(parent.getName())) {
                return true;
            }
        }
        return false;
    }
}