package dev.mrshawn.pokeblocks.utils;

import com.mojang.blaze3d.platform.NativeImage;
import dev.mrshawn.pokeblocks.item.DollRarity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.io.InputStream;
import java.util.Optional;

public class ColorFactory {

	private ColorFactory() {}

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

}
