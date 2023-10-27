package dev.mrshawn.pokeblocks.item.custom.impl;

import dev.mrshawn.pokeblocks.item.client.renderer.impl.PokedollCalyrexBlockItemRenderer;
import dev.mrshawn.pokeblocks.item.custom.PokeBlockItem;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;

public class PokedollCalyrexPokeBlockItem extends PokeBlockItem {

	public PokedollCalyrexPokeBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	protected BuiltinModelItemRenderer getItemRenderer() {
		return new PokedollCalyrexBlockItemRenderer();
	}
}
