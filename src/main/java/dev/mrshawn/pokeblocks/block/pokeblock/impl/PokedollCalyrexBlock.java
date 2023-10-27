package dev.mrshawn.pokeblocks.block.pokeblock.impl;

import dev.mrshawn.pokeblocks.Pokeblocks;
import dev.mrshawn.pokeblocks.block.entity.PokeBlockEntity;
import dev.mrshawn.pokeblocks.block.entity.impl.PokedollCalyrexBlockEntity;
import dev.mrshawn.pokeblocks.block.pokeblock.PokeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PokedollCalyrexBlock extends PokeBlock {

	public PokedollCalyrexBlock() {
		super("pokedoll_calyrex");
	}

	@Override
	protected Identifier getPokeModelResource(PokeBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "geo/pokedoll_calyrex.geo.json");
	}

	@Override
	protected Identifier getPokeTextureResource(PokeBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "textures/block/pokedoll_calyrex_texture.png");
	}

	@Override
	protected Identifier getPokeAnimationResource(PokeBlockEntity animatable) {
		return new Identifier(Pokeblocks.MOD_ID, "animations/generic.animation.json");
	}

	@Override
	protected BlockEntity createPokeBlockEntity(BlockPos pos, BlockState state) {
		return new PokedollCalyrexBlockEntity(pos, state);
	}

}
