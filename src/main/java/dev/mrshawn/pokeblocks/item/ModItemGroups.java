package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

	public static final ItemGroup POKEBLOCKS = Registry.register(Registries.ITEM_GROUP,
			new Identifier(Pokeblocks.MOD_ID, "pokeblocks"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemgroup.pokeblocks"))
					.icon(() -> new ItemStack(ModItems.POKEDOLL_CALYREX_BLOCK_ITEM))
					.entries(((displayContext, entries) -> {
						entries.add(ModBlocks.POKEDOLL_CALYREX);
						entries.add(ModBlocks.POKEDOLL_SHINY_CALYREX);
						entries.add(ModBlocks.POKEDOLL_CALYREX_ANIMATED);
					}))
					.build()
	);

	public static void registerItemGroups() {
		Pokeblocks.LOGGER.info("Registering ItemGroups for " + Pokeblocks.MOD_ID);
	}

}
