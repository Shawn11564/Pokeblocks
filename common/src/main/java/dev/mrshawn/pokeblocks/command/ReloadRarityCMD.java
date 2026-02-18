package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadRarityCMD {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("pokeblocks-reload-rarity")
                .requires(src -> src.hasPermission(2))
                .executes(ReloadRarityCMD::run)
        );
    }

    private static int run(CommandContext<CommandSourceStack> ctx) {
        DollRarityOverrides.reload();
        ctx.getSource().sendSuccess(() -> Component.literal("Reloaded doll rarity overrides"), true);
        return 1;
    }
}