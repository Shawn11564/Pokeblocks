package dev.mrshawn.pokeblocks.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.JsonOps;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.item.loot.LootTableItemMap;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
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
	 * Suggests loot table IDs from the reloadable server registries, filtered by what has
	 * been typed so far. Uses a simple {@link String#contains} check on the full ID so that
	 * partial path segments work from any position — e.g. typing {@code "simple_dungeon"}
	 * matches {@code "minecraft:chests/simple_dungeon"} even though it doesn't start the ID.
	 */
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_LOOT_TABLES = (context, builder) -> {
		MinecraftServer server = context.getSource().getServer();
		if (server == null) return builder.buildFuture();
		String remaining = builder.getRemaining().toLowerCase();
		server.reloadableRegistries()
				.getKeys(Registries.LOOT_TABLE)
				.stream()
				.filter(loc -> remaining.isEmpty() || loc.toString().toLowerCase().contains(remaining))
				.forEach(loc -> builder.suggest(loc.toString()));
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

		List<Component> matches = searchLootTable(table, tableId, searchString, server);

		if (matches.isEmpty()) {
			source.sendSuccess(() -> Component.literal("No items found in '" + tableId + "' containing '" + searchString + "'"), false);
			return 0;
		}

		source.sendSuccess(() -> Component.literal("§6Found " + matches.size() + " item(s) in '" + tableId + "' containing '" + searchString + "'"), false);
		for (Component match : matches) {
			source.sendSuccess(() -> match, false);
		}
		return matches.size();
	}

	/**
	 * Searches a loot table for entries whose serialized JSON or Pokeblocks live entry list
	 * contains the search string. Returns a list of clickable chat components.
	 * <p>
	 * The Pokeblocks injected pool is not reliably reproduced when the table is re-encoded
	 * via {@code LootTable.DIRECT_CODEC}, so those entries are searched directly from
	 * {@link PokeblocksCommon#getLootEntries()} when the queried table is configured for injection.
	 */
	private static List<Component> searchLootTable(LootTable table, ResourceLocation tableId, String searchString, MinecraftServer server) {
		List<Component> results = new ArrayList<>();
		Set<String> seen = new HashSet<>();

		// Search the codec-serialized loot table JSON (covers vanilla and datapack entries)
		var ops = server.reloadableRegistries().get().createSerializationContext(JsonOps.INSTANCE);
		JsonElement encoded = LootTable.DIRECT_CODEC.encodeStart(ops, table).result().orElse(null);
		if (encoded != null && encoded.isJsonObject()) {
			JsonArray pools = encoded.getAsJsonObject().getAsJsonArray("pools");
			if (pools != null) {
				for (JsonElement poolEl : pools) {
					if (!poolEl.isJsonObject()) continue;
					JsonArray entries = poolEl.getAsJsonObject().getAsJsonArray("entries");
					if (entries == null) continue;

					for (JsonElement entryEl : entries) {
						if (!entryEl.isJsonObject()) continue;
						JsonObject entry = entryEl.getAsJsonObject();

						if (entry.toString().toLowerCase().contains(searchString)) {
							String name = entry.has("name") ? entry.get("name").getAsString() : entry.toString();
							if (seen.add(name)) {
								results.add(buildVanillaResult(name));
							}
						}
					}
				}
			}
		}

		// Directly search Pokeblocks loot entries (bypasses codec re-encoding)
		if (LootInjector.shouldInject(tableId)) {
			for (LootTableItemMap.LootEntry pbEntry : PokeblocksCommon.getLootEntries()) {
				ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(pbEntry.stack().getItem());
				String itemId = itemKey != null ? itemKey.toString() : "";

				String searchable = itemId;
				var blockEntityData = pbEntry.stack().get(DataComponents.BLOCK_ENTITY_DATA);
				if (blockEntityData != null) {
					searchable = buildSearchableString(itemId, blockEntityData.copyTag());
				}

				if (searchable.contains(searchString)) {
					String giveCommand = buildGiveCommand(pbEntry);
					if (giveCommand != null && seen.add(giveCommand)) {
						results.add(buildPokeblockResult(pbEntry, giveCommand));
					}
				}
			}
		}

		return results;
	}

	/**
	 * Builds a searchable string from an item's registry ID and its block entity data tag.
	 * <p>
	 * String values are included verbatim. Boolean (byte) values contribute their <em>key name</em>
	 * only when the value is {@code true} — this prevents false-positives such as matching
	 * "gigantic" on items that store {@code gigantic:0b} in their tag but are not actually gigantic.
	 */
	private static String buildSearchableString(String itemId, CompoundTag tag) {
		StringBuilder sb = new StringBuilder(itemId);
		for (String key : tag.getAllKeys()) {
			if (tag.contains(key, Tag.TAG_STRING)) {
				sb.append(" ").append(tag.getString(key));
			} else if (tag.contains(key, Tag.TAG_BYTE)) {
				if (tag.getBoolean(key)) {
					sb.append(" ").append(key);
				}
			}
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * Builds a clickable chat component for a vanilla loot entry.
	 * Uses SUGGEST_COMMAND so the player can fill in their own name before running.
	 */
	private static Component buildVanillaResult(String itemName) {
		return Component.literal("§7- " + itemName)
				.withStyle(style -> style
						.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/give @s " + itemName))
						.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								Component.literal("Click to fill give command in chat")))
				);
	}

	/**
	 * Builds a clickable chat component for a Pokeblocks loot entry.
	 * Shows the item's full display name and runs the give command immediately on click.
	 */
	private static Component buildPokeblockResult(LootTableItemMap.LootEntry entry, String giveCommand) {
		String displayName = entry.stack().getHoverName().getString();
		MutableComponent text = Component.literal("§7- §f" + displayName);
		return text.withStyle(style -> style
				.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, giveCommand))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
						Component.literal("Click to give to yourself\n§7" + giveCommand)))
		);
	}

	/**
	 * Constructs the appropriate Pokeblocks give command for the given loot entry.
	 * <ul>
	 *   <li>Figurines  → {@code /pokeblocks figurinegive @s <name>}</li>
	 *   <li>Pokedolls  → {@code /pokeblocks pokegive @s <pokemon> [flags...]}</li>
	 *   <li>Decoratives → {@code /pokeblocks decorativegive @s <id> [flags...]}</li>
	 * </ul>
	 */
	private static String buildGiveCommand(LootTableItemMap.LootEntry entry) {
		ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(entry.stack().getItem());
		if (itemKey == null) return null;

		var blockEntityData = entry.stack().get(DataComponents.BLOCK_ENTITY_DATA);
		if (blockEntityData == null) return null;

		CompoundTag tag = blockEntityData.copyTag();
		String itemId = itemKey.toString();

		if ("pokeblocks:figurine".equals(itemId)) {
			String figurine = tag.getString("figurine");
			return "/pokeblocks figurinegive @s " + figurine;

		} else if ("pokeblocks:pokedoll".equals(itemId)) {
			String pokemon = tag.getString("pokemon");
			StringBuilder cmd = new StringBuilder("/pokeblocks pokegive @s ").append(pokemon);
			for (ModelFlag flag : ModelFlag.values()) {
				if (tag.contains(flag.getTagName()) && tag.getBoolean(flag.getTagName())) {
					cmd.append(" ").append(flag.getTagName());
				}
			}
			return cmd.toString();

		} else {
			// Decorative item — extract local ID from block entity "id" field
			// e.g. "pokeblocks:applin_basket" → "applin_basket"
			String blockEntityId = tag.getString("id");
			String localId = blockEntityId.contains(":")
					? blockEntityId.substring(blockEntityId.indexOf(':') + 1)
					: blockEntityId;
			StringBuilder cmd = new StringBuilder("/pokeblocks decorativegive @s ").append(localId);
			for (ModelFlag flag : ModelFlag.values()) {
				if (tag.contains(flag.getTagName()) && tag.getBoolean(flag.getTagName())) {
					cmd.append(" ").append(flag.getTagName());
				}
			}
			return cmd.toString();
		}
	}
}
