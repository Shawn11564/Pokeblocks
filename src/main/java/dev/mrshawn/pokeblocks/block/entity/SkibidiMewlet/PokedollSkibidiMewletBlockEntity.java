package dev.mrshawn.pokeblocks.block.entity.SkibidiMewlet;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollSkibidiMewletBlockEntity extends PokedollBlockEntity {
	public PokedollSkibidiMewletBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollSkibidiMewletBlockEntity.class), pos, state);
	}
}
