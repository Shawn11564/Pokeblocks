package com.example.examplemod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class ModCommands {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        DollInfoCMD.register(dispatcher);
		PokeGiveCMD.register(dispatcher);
    }
}