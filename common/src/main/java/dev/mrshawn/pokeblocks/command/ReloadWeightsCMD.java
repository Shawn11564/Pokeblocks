package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.mrshawn.pokeblocks.item.RarityWeightConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadWeightsCMD {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pokeblocks")
                .then(Commands.literal("reload_weights")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> {
                            return reloadWeights(ctx.getSource());
                        }))
                .then(Commands.literal("recreate_weights")
                        .requires(source -> source.hasPermission(2))
                        .executes(ctx -> {
                            return recreateWeights(ctx.getSource());
                        }))
        );
    }

    private static int reloadWeights(CommandSourceStack source) {
        try {
            RarityWeightConfig.reload();
            source.sendSuccess(() -> Component.literal("Rarity weights reloaded from config file"), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to reload rarity weights: " + e.getMessage()));
            return 0;
        }
    }

    private static int recreateWeights(CommandSourceStack source) {
        try {
            RarityWeightConfig.forceRecreate(source.getServer().getServerDirectory());
            source.sendSuccess(() -> Component.literal("Rarity weights config file recreated with defaults"), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to recreate rarity weights config: " + e.getMessage()));
            return 0;
        }
    }
}