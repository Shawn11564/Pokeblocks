package dev.mrshawn.pokeblocks.integration.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.mrshawn.pokeblocks.client.renderer.accessory.HeadAccessoryRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Draws a pokedoll worn in a Curios <b>head</b> slot. Curios invokes this from its own render layer
 * with the PoseStack already at the wearer's model root (exactly like a vanilla armor layer), so all
 * this has to do is defer to {@link HeadAccessoryRenderer}, which reproduces vanilla's head display
 * space and reuses the doll's existing {@code HeadFit} seating — a Curios doll ends up sitting
 * identically to one worn in the vanilla helmet slot.
 * <p>
 * This class references Curios API types, so it must only ever be classloaded when the {@code curios}
 * mod is present (see {@link CuriosIntegration}).
 */
public class PokedollCurioRenderer implements ICurioRenderer {

	@Override
	public <T extends LivingEntity, M extends EntityModel<T>> void render(
			ItemStack stack, SlotContext slotContext, PoseStack matrixStack,
			RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
			int light, float limbSwing, float limbSwingAmount, float partialTicks,
			float ageInTicks, float netHeadYaw, float headPitch) {
		HeadAccessoryRenderer.render(slotContext.entity(), stack, renderLayerParent.getModel(),
				matrixStack, renderTypeBuffer, light);
	}
}
