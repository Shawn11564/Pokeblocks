package com.example.examplemod.mixin;

import com.example.examplemod.ExampleModCommon;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NbtUtils.class)
public class NbtUtilsMixin {
	// moved general updating to MixinDataFixTypes.java

	@Inject(method = "readBlockState", at = @At("HEAD"))
	private static void railways$upgradeBlocks(HolderGetter<Block> blockGetter, CompoundTag tag, CallbackInfoReturnable<BlockState> cir) {
		if (tag.contains("Name", Tag.TAG_STRING)) {
			/*
			 * MonoBogey Fixer
			 */
			if (tag.getString("Name").equals("pokeblocks:pokedoll_bulbasaur")) {
				ExampleModCommon.LOGGER.info("Got hit: {}", tag.getString("Name"));
				tag.putString("Name", "examplemod:pokedoll");
				CompoundTag properties = tag.getCompound("Properties");
				properties.putString("pokemon", "bulbasaur");
				tag.put("Properties", properties);
			}

			/*
			 * Compat Cherry Track Fixer
			 */
//			if (CompatCherryTrackFix.standardCherryOld.contains(tag.getString("Name"))) {
//				railways$fixCherryTrackData(tag, "railways:track_cherry");
//			} else if (CompatCherryTrackFix.wideCherryOld.contains(tag.getString("Name"))) {
//				railways$fixCherryTrackData(tag, "railways:track_cherry_wide");
//			} else if (CompatCherryTrackFix.narrowCherryOld.contains(tag.getString("Name"))) {
//				railways$fixCherryTrackData(tag, "railways:track_cherry_narrow");
//			}
		}
	}

	@Unique
	private static void railways$fixCherryTrackData(CompoundTag tag, String name) {
		tag.putString("Name", name);
		CompoundTag properties = tag.getCompound("Properties");
		tag.put("Properties", properties);
	}
}