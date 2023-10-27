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
					.displayName(Text.translatable("pokeblocks.pokedoll_calyrex"))
					.icon(() -> new ItemStack(ModItems.ANIMATED_BLOCK_ITEM))
					.entries(((displayContext, entries) -> {
						entries.add(ModBlocks.POKEDOLL_CALYREX);
					}))
					.build()
	);

	public static void registerItemGroups() {
		Pokeblocks.LOGGER.info("Registering ItemGroups for " + Pokeblocks.MOD_ID);
	}

}
