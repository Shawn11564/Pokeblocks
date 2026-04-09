package dev.mrshawn.pokeblocks.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntableSearchCMD {

	/**
	 * Suggests loot table IDs from the reloadable server registries.
	 */
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT_TABLES = (context, builder) -> {
		MinecraftServer server = context.getSource().getServer();
		if (server != null) {
			server.reloadableRegistries()
					.getKeys(Registries.LOOT_TABLE)
					.forEach(loc -> builder.suggest(loc.toString()));
		}
		return builder.buildFuture();
	};

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("pokeblocks")
						.then(
								Commands.literal("intable")
										.requires(src -> src.hasPermission(2))
										.then(Commands.argument("loot_table", ResourceLocationArgument.id())
												.suggests(SUGGEST_LOOT_TABLES)
												.then(Commands.argument("search_string", StringArgumentType.greedyString())
														.executes(IntableSearchCMD::execute)
												)
										)
						)
		);
	}

	private static int execute(CommandContext<CommandSourceStack> context) {
		ResourceLocation tableId = ResourceLocationArgument.getId(context, "loot_table");
		String searchString = StringArgumentType.getString(context, "search_string").toLowerCase();
		CommandSourceStack source = context.getSource();
		MinecraftServer server = source.getServer();

		if (server == null) {
			source.sendFailure(Component.literal("Server is not available"));
			return 0;
		}

		ResourceKey<LootTable> tableKey = ResourceKey.create(Registries.LOOT_TABLE, tableId);
		LootTable table = server.reloadableRegistries().getLootTable(tableKey);
		if (table == LootTable.EMPTY) {
			source.sendFailure(Component.literal("Loot table '" + tableId + "' not found"));
			return 0;
		}

		List<String> matches = searchLootTable(table, searchString, server);

		if (matches.isEmpty()) {
			source.sendSuccess(() -> Component.literal("No items found in '" + tableId + "' containing '" + searchString + "'"), false);
			return 0;
		}

		source.sendSuccess(() -> Component.literal("§6Found " + matches.size() + " item(s) in '" + tableId + "' containing '" + searchString + "'"), false);
		for (String match : matches) {
			source.sendSuccess(() -> Component.literal("§7- " + match), false);
		}
		return matches.size();
	}

	/**
	 * Serializes the loot table to JSON via LootTable.DIRECT_CODEC and walks all
	 * pool entries looking for items whose serialized JSON contains the search string.
	 */
	private static List<String> searchLootTable(LootTable table, String searchString, MinecraftServer server) {
		List<String> results = new ArrayList<>();
		Set<String> seen = new HashSet<>();

		var ops = server.reloadableRegistries().get().createSerializationContext(JsonOps.INSTANCE);
		JsonElement encoded = LootTable.DIRECT_CODEC.encodeStart(ops, table).result().orElse(null);
		if (encoded == null || !encoded.isJsonObject()) return results;

		JsonArray pools = encoded.getAsJsonObject().getAsJsonArray("pools");
		if (pools == null) return results;

		for (JsonElement poolEl : pools) {
			if (!poolEl.isJsonObject()) continue;
			JsonArray entries = poolEl.getAsJsonObject().getAsJsonArray("entries");
			if (entries == null) continue;

			for (JsonElement entryEl : entries) {
				if (!entryEl.isJsonObject()) continue;
				JsonObject entry = entryEl.getAsJsonObject();

				// Search the full serialized entry (catches name, functions, components, etc.)
				if (entry.toString().toLowerCase().contains(searchString)) {
					String name = entry.has("name") ? entry.get("name").getAsString() : entry.toString();
					if (seen.add(name)) {
						results.add(name);
					}
				}
			}
		}

		return results;
	}
}
