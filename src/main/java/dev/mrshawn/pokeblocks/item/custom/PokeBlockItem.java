package dev.mrshawn.pokeblocks.item.custom;

import dev.mrshawn.pokeblocks.item.DollRarity;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public class PokeBlockItem extends BlockItem {

    private final DollRarity rarity;

    public PokeBlockItem(Block block, DollRarity rarity) {
        super(block, new Item.Settings());
        this.rarity = rarity;
    }

    public DollRarity getRarity() {
        return rarity;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (rarity != DollRarity.NONE) {
            tooltip.add(1, Text.empty());
            tooltip.add(2, Text.literal(rarity.getDisplayName()).formatted(rarity.getColor()));
        }
    }
}
