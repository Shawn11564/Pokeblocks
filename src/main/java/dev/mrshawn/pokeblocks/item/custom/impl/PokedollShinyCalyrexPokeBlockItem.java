package dev.mrshawn.pokeblocks.item.custom.impl;

import dev.mrshawn.pokeblocks.item.client.renderer.impl.PokedollShinyCalyrexBlockItemRenderer;
import dev.mrshawn.pokeblocks.item.custom.PokeBlockItem;
import net.minecraft.block.Block;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;

public class PokedollShinyCalyrexPokeBlockItem extends PokeBlockItem {

	public PokedollShinyCalyrexPokeBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	protected BuiltinModelItemRenderer getItemRenderer() {
		return new PokedollShinyCalyrexBlockItemRenderer();
	}
}
