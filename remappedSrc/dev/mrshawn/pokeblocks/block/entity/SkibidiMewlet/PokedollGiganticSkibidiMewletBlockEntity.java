package dev.mrshawn.pokeblocks.block.entity.SkibidiMewlet;

import dev.mrshawn.pokeblocks.block.entity.BlockEntityTypeRegistry;
import dev.mrshawn.pokeblocks.block.entity.PokedollBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PokedollGiganticSkibidiMewletBlockEntity extends PokedollBlockEntity {
	public PokedollGiganticSkibidiMewletBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityTypeRegistry.get(PokedollGiganticSkibidiMewletBlockEntity.class), pos, state);
	}
}
