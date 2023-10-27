package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.block.ModBlocks;
import dev.mrshawn.pokeblocks.item.ModItemGroups;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pokeblocks implements ModInitializer {

	public static final String MOD_ID = "pokeblocks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
	}
}