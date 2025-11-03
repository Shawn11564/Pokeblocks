package com.example.examplemod.registry;

import com.example.examplemod.ExampleModCommon;
import com.example.examplemod.item.PokedollItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public final class ItemRegistry {
	public static void init() {}

	public static final Supplier<BlockItem> POKEDOLL_ITEM = registerItem("pokedoll", () -> new PokedollItem(BlockRegistry.POKEDOLL_BLOCK.get(), new Item.Properties()));
	public static final Supplier<BlockItem> TESTBLOCK_ITEM = registerItem("testblock", () -> new BlockItem(BlockRegistry.TESTBLOCK.get(), new Item.Properties()));

	private static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
		return ExampleModCommon.COMMON_PLATFORM.registerItem(id, item);
	}

	public static final Supplier<CreativeModeTab> EXAMPLEMOD_TAB = ExampleModCommon.COMMON_PLATFORM.registerCreativeModeTab("examplemod_items", () -> ExampleModCommon.COMMON_PLATFORM.newCreativeTabBuilder()
			.title(Component.translatable("itemGroup." + ExampleModCommon.MODID + ".examplemod_items"))
			.icon(() -> new ItemStack(ItemRegistry.POKEDOLL_ITEM.get()))
			.displayItems((enabledFeatures, entries) -> {
				entries.accept(PokedollItem.createPokedoll("charmander", false));
				entries.accept(PokedollItem.createPokedoll("absol", false));
			})
			.build());

}
