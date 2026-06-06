package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class ModCommands {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        DollInfoCMD.register(dispatcher);
		PokeGiveCMD.register(dispatcher);
		FigurineGiveCMD.register(dispatcher);
		DecorativeGiveCMD.register(dispatcher);
		RebuildPackCMD.register(dispatcher);
		ReloadRarityCMD.register(dispatcher);
		ReloadWeightsCMD.register(dispatcher);
		ReloadLootGroupsCMD.register(dispatcher);
		IntableSearchCMD.register(dispatcher);
		ConfigSyncCMD.register(dispatcher);
    }
}