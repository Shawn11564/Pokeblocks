package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.DollRarityAcquisitionDivisors;
import dev.mrshawn.pokeblocks.item.DollRarityIgnoredFlags;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ReloadRarityCMD {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("pokeblocks")
						.then(Commands.literal("reload_rarity")
								.requires(src -> src.hasPermission(2))
								.executes(ReloadRarityCMD::run)
						)
		);
	}

	private static int run(CommandContext<CommandSourceStack> ctx) {
		DollRarityOverrides.reload();
		DollRarityIgnoredFlags.reload();
		DollRarityAcquisitionDivisors.reload();
		// Rarity overrides, ignored flags and acquisition divisors all feed variant weights,
		// so drop the cached loot map and tooltip weight total to force a rebuild with the new
		// values (otherwise drop chances and tooltips stay stale until a restart).
		PokeblocksCommon.invalidateLootMap();
		ctx.getSource().sendSuccess(() -> Component.literal("Reloaded doll rarity overrides"), true);
		return 1;
	}
}