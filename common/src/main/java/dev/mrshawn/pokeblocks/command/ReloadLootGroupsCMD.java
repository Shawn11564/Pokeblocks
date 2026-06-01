package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.loot.LootGroupConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadLootGroupsCMD {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("pokeblocks")
						.then(Commands.literal("reload_loot_groups")
								.requires(src -> src.hasPermission(2))
								.executes(ReloadLootGroupsCMD::run)
						)
		);
	}

	private static int run(CommandContext<CommandSourceStack> ctx) {
		LootGroupConfig.reload();
		// Rebuild the partitioned loot pools so the new group assignments take effect immediately.
		PokeblocksCommon.invalidateLootMap();
		ctx.getSource().sendSuccess(() -> Component.literal(
				"Reloaded loot groups (" + LootGroupConfig.getGroups().size() + " group(s))"), true);
		return 1;
	}
}
