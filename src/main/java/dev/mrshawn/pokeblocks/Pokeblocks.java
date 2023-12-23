package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.block.entity.ModBlockEntities;
import dev.mrshawn.pokeblocks.item.ModItemGroups;
import dev.mrshawn.pokeblocks.item.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;

public class Pokeblocks implements ModInitializer {

	public static final String MOD_ID = "pokeblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerAllBlockEntities();

		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POKEDOLL_MAREEP, RenderLayer.getCutoutMipped());

		GeckoLib.initialize();
	}
}