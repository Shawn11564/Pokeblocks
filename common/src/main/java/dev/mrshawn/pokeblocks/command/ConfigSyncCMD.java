package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.config.ConfigSync;
import dev.mrshawn.pokeblocks.config.ConfigUpdateMode;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarityAcquisitionDivisors;
import dev.mrshawn.pokeblocks.item.DollRarityIgnoredFlags;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.FigurineNameOverrides;
import dev.mrshawn.pokeblocks.item.FigurineTagOverrides;
import dev.mrshawn.pokeblocks.item.RarityWeightConfig;
import dev.mrshawn.pokeblocks.item.loot.LootGroupConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

/**
 * {@code /pokeblocks config status|sync} — preview or apply a {@link ConfigSync} pass on demand.
 * <p>
 * Both subcommands use {@code force = true} so they work even when {@code auto_update_configs} is
 * off: that is the intended workflow for admins who keep auto-update disabled but occasionally want
 * to pull in new content. Frozen files are still respected.
 */
public class ConfigSyncCMD {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("pokeblocks")
						.then(Commands.literal("config")
								.requires(src -> src.hasPermission(2))
								.then(Commands.literal("status").executes(ConfigSyncCMD::status))
								.then(Commands.literal("sync").executes(ConfigSyncCMD::sync))
						)
		);
	}

	private static int status(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		ConfigSync.SyncReport report = ConfigSync.sync(source.getServer().getServerDirectory(), true, true);

		ConfigUpdateMode mode = PokeblocksConfig.getConfigUpdateMode();
		source.sendSuccess(() -> Component.literal("Config auto-update mode: " + mode.token())
				.withStyle(mode == ConfigUpdateMode.OFF ? ChatFormatting.YELLOW : ChatFormatting.GREEN), false);

		if (!report.anyChanges()) {
			source.sendSuccess(() -> Component.literal("All config files are up to date.").withStyle(ChatFormatting.GRAY), false);
		} else {
			source.sendSuccess(() -> Component.literal(summary("Pending", report)).withStyle(ChatFormatting.AQUA), false);
			sendDetails(source, report);
			source.sendSuccess(() -> Component.literal("Run /pokeblocks config sync to apply.").withStyle(ChatFormatting.GRAY), false);
		}
		sendSkipped(source, report);
		return 1;
	}

	private static int sync(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		ConfigSync.SyncReport report = ConfigSync.sync(source.getServer().getServerDirectory(), false, true);

		if (!report.anyChanges()) {
			source.sendSuccess(() -> Component.literal("Config files already up to date — nothing to apply.")
					.withStyle(ChatFormatting.GRAY), true);
			sendSkipped(source, report);
			return 1;
		}

		reloadAll();

		source.sendSuccess(() -> Component.literal(summary("Applied", report)).withStyle(ChatFormatting.GREEN), true);
		sendDetails(source, report);
		sendSkipped(source, report);
		return 1;
	}

	/** Reload every config-backed cache so an applied sync takes effect without a restart. */
	private static void reloadAll() {
		DollRarityOverrides.reload();
		DollRarityIgnoredFlags.reload();
		DollRarityAcquisitionDivisors.reload();
		FigurineNameOverrides.reload();
		FigurineTagOverrides.reload();
		RarityWeightConfig.reload();
		LootGroupConfig.reload();
		// Rarity/weight/loot-group changes all feed variant weights and the loot map.
		PokeblocksCommon.invalidateLootMap();
	}

	private static String summary(String verb, ConfigSync.SyncReport report) {
		String base = verb + " config updates: +" + report.totalAdded() + " new, ~" + report.totalUpdated() + " changed";
		if (!report.overwritten.isEmpty()) base += ", " + report.overwritten.size() + " overwritten";
		return base + ".";
	}

	private static void sendDetails(CommandSourceStack source, ConfigSync.SyncReport report) {
		for (Map.Entry<String, List<String>> e : report.added.entrySet()) {
			String file = e.getKey();
			List<String> added = e.getValue();
			List<String> updated = report.updated.getOrDefault(file, List.of());
			if (added.isEmpty() && updated.isEmpty()) continue;
			source.sendSuccess(() -> Component.literal("  " + file + ": +" + added.size() + " new, ~" + updated.size() + " changed")
					.withStyle(ChatFormatting.DARK_AQUA), false);
		}
		for (String file : report.overwritten) {
			source.sendSuccess(() -> Component.literal("  " + file + ": overwritten with bundled default")
					.withStyle(ChatFormatting.GOLD), false);
		}
	}

	private static void sendSkipped(CommandSourceStack source, ConfigSync.SyncReport report) {
		for (String s : report.skipped) {
			source.sendSuccess(() -> Component.literal("  skipped " + s).withStyle(ChatFormatting.GRAY), false);
		}
	}
}
