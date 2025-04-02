package dev.mrshawn.pokeblocks.item;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.item.custom.PokedollBlockItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ModItemGroups {

	public static final ItemGroup POKEBLOCKS = Registry.register(Registries.ITEM_GROUP,
			Identifier.of(Pokeblocks.MOD_ID, "pokeblocks"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemgroup.pokeblocks"))
					.icon(() -> new ItemStack(ModItems.POKEDOLL_CALYREX_BLOCK_ITEM))
					.entries(((displayContext, entries) -> {
						// Get all non-gigantic dolls
						List<PokedollBlockItem> dolls = ModItems.getAllDolls(true);

						// Sort dolls by Pokedex number
						dolls.sort(Comparator.comparingInt(PokedollBlockItem::getDexNumber));

						// Add all sorted dolls to the item group
						for (PokedollBlockItem doll : dolls) {
							if (doll.getDexNumber() == 0) continue;
							entries.add(doll);
						}
					}))
					.build()
	);

	public static final ItemGroup GIGANTICS = Registry.register(Registries.ITEM_GROUP,
			Identifier.of(Pokeblocks.MOD_ID, "pokeblocks_gigantics"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemgroup.pokeblocks_gigantics"))
					.icon(() -> new ItemStack(ModItems.GIGANTIC_POKEDOLL_CALYREX_BLOCK_ITEM))
					.entries(((displayContext, entries) -> {
						// Get all non-gigantic dolls
						List<PokedollBlockItem> dolls = ModItems.getAllGiganticDolls();

						// Sort dolls by Pokedex number
						dolls.sort(Comparator.comparingInt(PokedollBlockItem::getDexNumber));

						// Add all sorted dolls to the item group
						for (PokedollBlockItem doll : dolls) {
							if (doll.getDexNumber() == 0) continue;
							entries.add(doll);
						}
					}))
					.build()
	);

	public static final ItemGroup MISC = Registry.register(Registries.ITEM_GROUP,
			Identifier.of(Pokeblocks.MOD_ID, "pokeblocks_misc"),
			FabricItemGroup.builder()
					.displayName(Text.translatable("itemgroup.pokeblocks_misc"))
					.icon(() -> new ItemStack(ModItems.POKEDOLL_DONCHEADLE_FIGURINE_BLOCK_ITEM))
					.entries(((displayContext, entries) -> {
						// Get all non-gigantic dolls
						List<ItemConvertible> items = ModItems.getAllMiscItems();
						for (ItemConvertible item : items) {
							entries.add(item);
						}
					}))
					.build()
	);

	public static void registerItemGroups() {
		Pokeblocks.LOGGER.info("Registering ItemGroups for " + Pokeblocks.MOD_ID);

		HashMap<String, Integer> counts = new HashMap<>();
		POKEBLOCKS.getDisplayStacks().forEach(stack -> {
			if (counts.containsKey(stack.getName().getString())) {
				counts.put(stack.getName().getString(), counts.get(stack.getName().getString()) + 1);
			} else {
				counts.put(stack.getName().getString(), 1);
			}
		});
		counts.forEach((key, value) -> Pokeblocks.LOGGER.info("Registered " + value + "x " + key));
	}

}
