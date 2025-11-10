package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

public class PokeGiveCMD {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

	}

	private static int execute(CommandContext<CommandSourceStack> context) {
		return 0;
	}
}
