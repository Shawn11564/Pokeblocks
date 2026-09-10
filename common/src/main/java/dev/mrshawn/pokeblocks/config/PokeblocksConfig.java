package dev.mrshawn.pokeblocks.config;

import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class PokeblocksConfig {

	// [eastereggs]
	private static boolean dollPoppingEnabled = true;

	// [creative] — controls visibility of unfinished content in the creative menu.
	private static boolean showIncompleteItems = false;

	// [resourcepack]
	private static boolean kickOnDecline = true;
	private static boolean includeBuiltinAssets = true;
	private static boolean deltaServing = true;
	// How the custom pack reaches clients, and the knobs each distribution mode needs.
	private static PackDistribution packDistribution = PackDistribution.SELF_HOST;
	private static String remotePackUrl = "";
	private static String remotePackSha1 = "";
	private static String selfHostAddress = "";

	// [phone] — the Pokedoll Phone's ring cadence and dig-site quest shape.
	private static boolean phoneEnabled = true;
	private static int phoneAverageCallIntervalMinutes = 15;
	private static int phoneRingSeconds = 30;
	private static int phoneDigSites = 6;
	private static int phoneSiteRadius = 32;
	private static int phoneGuaranteedAttempts = 3;
	private static int phoneDurability = 4;
	private static int phoneDigSitesPerRarity = 1;
	private static int phoneGuaranteedAttemptsPerRarity = 1;
	private static float phoneLootDropChance = 0.03f;

	// [figurine] — the walking figurine companion.
	private static int figurineMemorialWanderRadius = 16;

	// [loot]
	private static float lootDropChance = 0.33f;
	private static final Set<ResourceLocation> lootTables = new HashSet<>();
	private static final List<Pattern> lootTableWildcards = new ArrayList<>();
	private static final Set<ModelFlag> excludedLootFlags = EnumSet.noneOf(ModelFlag.class);
	private static final Set<String> excludedLootDolls = new LinkedHashSet<>();

	// [config_sync] — controls how the bundled default JSON configs are updated into the server's
	// files on update, and which of them are written to the config folder at all (see ConfigSync).
	private static ConfigUpdateMode configUpdateMode = ConfigUpdateMode.MERGE;
	private static boolean configBackupBeforeUpdate = true;
	private static final Set<String> frozenConfigFiles = new LinkedHashSet<>();
	// #68 — per-file visibility overrides. show = force-create a hidden-by-default file so it can be
	// edited; hide = keep a shown-by-default file out of the folder (its bundled default still applies).
	private static final Set<String> shownConfigFiles = new LinkedHashSet<>();
	private static final Set<String> hiddenConfigFiles = new LinkedHashSet<>();

	private static Path configPath;

	public static boolean isDollPoppingEnabled() {
		return dollPoppingEnabled;
	}

	/**
	 * Whether items flagged as incomplete (see {@link dev.mrshawn.pokeblocks.item.IncompleteFeatureItem})
	 * are shown in the creative menu. Defaults to {@code false} so unfinished features stay hidden.
	 */
	public static boolean isShowIncompleteItems() {
		return showIncompleteItems;
	}

	public static boolean isKickOnDecline() {
		return kickOnDecline;
	}

	/**
	 * Whether the served pack also bundles the mod's own built-in doll/figurine/decoration assets, so
	 * clients running an older Pokeblocks than the server still receive (and can render) dolls that
	 * were added to the mod after their version. See the {@code include_builtin_assets} config comment.
	 */
	public static boolean isIncludeBuiltinAssets() {
		return includeBuiltinAssets;
	}

	/**
	 * Whether joins negotiate a per-client DELTA pack (only the entries the client can't resolve
	 * locally) instead of always pushing the full pack. Requires self-host distribution and a
	 * delta-capable client; everything else falls back to the full pack automatically.
	 */
	public static boolean isDeltaServing() {
		return deltaServing;
	}

	/** How the custom resource pack is distributed to clients: self-hosted (default) or an admin remote URL. */
	public static PackDistribution getPackDistribution() {
		return packDistribution;
	}

	/** The admin-provided pack URL used when {@link #getPackDistribution()} is {@link PackDistribution#REMOTE_URL}. */
	public static String getRemotePackUrl() {
		return remotePackUrl;
	}

	/** Optional SHA-1 of the remote pack; when blank the locally-built pack's hash is used instead. */
	public static String getRemotePackSha1() {
		return remotePackSha1;
	}

	/** Optional host/IP to advertise in the self-hosted pack URL (e.g. a public address); blank = auto-detect. */
	public static String getSelfHostAddress() {
		return selfHostAddress;
	}

	/** Whether Pokedoll Phones ring at all (existing quests still resolve when off). */
	public static boolean isPhoneEnabled() {
		return phoneEnabled;
	}

	/** Average minutes between calls while a phone sits in an inventory. */
	public static int getPhoneAverageCallIntervalMinutes() {
		return phoneAverageCallIntervalMinutes;
	}

	/** How long a call rings before it counts as missed. */
	public static int getPhoneRingSeconds() {
		return phoneRingSeconds;
	}

	/** How many dig sites an accepted call scatters around the player. */
	public static int getPhoneDigSites() {
		return phoneDigSites;
	}

	/** Radius (blocks) around the player the dig sites spawn in. */
	public static int getPhoneSiteRadius() {
		return phoneSiteRadius;
	}

	/** The buried doll is guaranteed within this many fully-dug sites. */
	public static int getPhoneGuaranteedAttempts() {
		return phoneGuaranteedAttempts;
	}

	/**
	 * How many calls a Pokedoll Phone can answer before it wears out: each accepted call costs one
	 * durability point and the phone breaks when it hits zero. Applied to phones as they are crafted;
	 * a value of 1 or more (defaults to 4).
	 */
	public static int getPhoneDurability() {
		return phoneDurability;
	}

	/**
	 * Chance (0.0–1.0) that an unattuned Pokedoll Phone shows up in a configured loot chest (the same
	 * tables as the {@code [loot] loot_tables} list). 0 removes the phone from loot entirely.
	 */
	public static float getPhoneLootDropChance() {
		return phoneLootDropChance;
	}

	/**
	 * How far (blocks) a memorial-revived figurine wanders from its memorial doll. A figurine brought
	 * back by placing its memorial doll stays tamed but no longer follows its owner — it roams around
	 * the doll within this radius instead.
	 */
	public static int getFigurineMemorialWanderRadius() {
		return figurineMemorialWanderRadius;
	}

	/**
	 * Extra dig sites added per rarity tier of the phone's attuned doll (Common +0, Uncommon +1×, …
	 * Gigantic +6×). 0 disables the scaling. Added on top of {@link #getPhoneDigSites()}.
	 */
	public static int getPhoneDigSitesPerRarity() {
		return phoneDigSitesPerRarity;
	}

	/**
	 * Extra guaranteed attempts added per rarity tier of the phone's attuned doll (same tier scale as
	 * {@link #getPhoneDigSitesPerRarity()}). 0 disables the scaling. Added on top of
	 * {@link #getPhoneGuaranteedAttempts()}.
	 */
	public static int getPhoneGuaranteedAttemptsPerRarity() {
		return phoneGuaranteedAttemptsPerRarity;
	}

	public static float getLootDropChance() {
		return lootDropChance;
	}

	public static Set<ResourceLocation> getLootTables() {
		return lootTables;
	}

	public static List<Pattern> getLootTableWildcards() {
		return lootTableWildcards;
	}

	public static Set<ModelFlag> getExcludedLootFlags() {
		return excludedLootFlags;
	}

	public static Set<String> getExcludedLootDolls() {
		return excludedLootDolls;
	}

	/** How bundled default configs are applied on startup: {@code merge}, {@code overwrite}, or {@code off}. */
	public static ConfigUpdateMode getConfigUpdateMode() {
		return configUpdateMode;
	}

	/**
	 * Convenience: whether any automatic updating happens at all (i.e. the mode is not
	 * {@link ConfigUpdateMode#OFF}). Retained for callers that only care about on/off.
	 */
	public static boolean isConfigAutoUpdate() {
		return configUpdateMode != ConfigUpdateMode.OFF;
	}

	/** Whether a timestamped backup is taken before a config file is rewritten by a sync. */
	public static boolean isConfigBackupBeforeUpdate() {
		return configBackupBeforeUpdate;
	}

	/** File names (e.g. {@code "doll_rarity.json"}) that should never be auto-updated. */
	public static Set<String> getFrozenConfigFiles() {
		return frozenConfigFiles;
	}

	/** Files hidden by default that the admin has opted to have written to the config folder (#68). */
	public static Set<String> getShownConfigFiles() {
		return shownConfigFiles;
	}

	/** Files shown by default that the admin has opted to keep out of the config folder (#68). */
	public static Set<String> getHiddenConfigFiles() {
		return hiddenConfigFiles;
	}

	public static void initialize(Path serverDir) {
		configPath = serverDir.resolve("config").resolve("Pokeblocks").resolve("config.toml");

		try {
			Files.createDirectories(configPath.getParent());

			if (!Files.exists(configPath)) {
				writeDefaults();
				PokeblocksLog.LOGGER.info("Created default config.toml at {}", configPath);
			} else {
				patchMissingKeys();
			}
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("Failed to create config.toml", e);
		}

		reload();
	}

	public static void reload() {
		// Reset to defaults
		dollPoppingEnabled = true;
		showIncompleteItems = false;
		kickOnDecline = true;
		includeBuiltinAssets = true;
		deltaServing = true;
		packDistribution = PackDistribution.SELF_HOST;
		remotePackUrl = "";
		remotePackSha1 = "";
		selfHostAddress = "";
		phoneEnabled = true;
		phoneAverageCallIntervalMinutes = 15;
		phoneRingSeconds = 30;
		phoneDigSites = 6;
		phoneSiteRadius = 32;
		phoneGuaranteedAttempts = 3;
		phoneDurability = 4;
		phoneDigSitesPerRarity = 1;
		phoneGuaranteedAttemptsPerRarity = 1;
		phoneLootDropChance = 0.03f;
		figurineMemorialWanderRadius = 16;
		lootDropChance = 0.33f;
		lootTables.clear();
		lootTableWildcards.clear();
		excludedLootFlags.clear();
		excludedLootFlags.add(ModelFlag.GIGANTIC);
		excludedLootDolls.clear();
		configUpdateMode = ConfigUpdateMode.MERGE;
		configBackupBeforeUpdate = true;
		frozenConfigFiles.clear();
		shownConfigFiles.clear();
		hiddenConfigFiles.clear();

		if (configPath == null || !Files.exists(configPath)) return;

		try {
			String currentCategory = "";
			// Tracks whether the new `auto_update_configs` key set the mode, so a legacy
			// `auto_update` boolean elsewhere in the file cannot override it.
			boolean modeSetByNewKey = false;
			List<String> lines = Files.readAllLines(configPath);

			for (int i = 0; i < lines.size(); i++) {
				String rawLine = lines.get(i);
				String line = rawLine.trim();

				if (line.isEmpty() || line.startsWith("#")) continue;

				if (line.startsWith("[") && line.endsWith("]")) {
					currentCategory = line.substring(1, line.length() - 1).trim().toLowerCase();
					continue;
				}

				int eqIndex = line.indexOf('=');
				if (eqIndex < 0) continue;

				String key = line.substring(0, eqIndex).trim().toLowerCase();
				String value = line.substring(eqIndex + 1).trim();

				int commentIndex = value.indexOf('#');
				if (commentIndex >= 0) {
					value = value.substring(0, commentIndex).trim();
				}

				switch (currentCategory) {
					case "eastereggs" -> {
						if (key.equals("doll_popping_enabled")) {
							dollPoppingEnabled = parseBoolean(value, true);
						}
					}
					case "creative" -> {
						if (key.equals("show_incomplete_items")) {
							showIncompleteItems = parseBoolean(value, false);
						}
					}
					case "resourcepack" -> {
						switch (key) {
							case "kick_on_decline" -> kickOnDecline = parseBoolean(value, true);
							case "include_builtin_assets" -> includeBuiltinAssets = parseBoolean(value, true);
							case "delta_serving" -> deltaServing = parseBoolean(value, true);
							case "distribution" -> packDistribution = PackDistribution.parse(unquote(value), PackDistribution.SELF_HOST);
							case "remote_url" -> remotePackUrl = unquote(value);
							case "remote_sha1" -> remotePackSha1 = unquote(value);
							case "self_host_address" -> selfHostAddress = unquote(value);
						}
					}
					case "figurine" -> {
						if (key.equals("memorial_wander_radius")) {
							figurineMemorialWanderRadius = parsePositiveInt(value, 16);
						}
					}
					case "phone" -> {
						switch (key) {
							case "enabled" -> phoneEnabled = parseBoolean(value, true);
							case "average_call_interval_minutes" -> phoneAverageCallIntervalMinutes = parsePositiveInt(value, 15);
							case "ring_seconds" -> phoneRingSeconds = parsePositiveInt(value, 30);
							case "dig_sites" -> phoneDigSites = parsePositiveInt(value, 6);
							case "site_radius" -> phoneSiteRadius = parsePositiveInt(value, 32);
							case "guaranteed_attempts" -> phoneGuaranteedAttempts = parsePositiveInt(value, 3);
							case "durability" -> phoneDurability = parsePositiveInt(value, 4);
							case "dig_sites_per_rarity" -> phoneDigSitesPerRarity = parseNonNegativeInt(value, 1);
							case "guaranteed_attempts_per_rarity" -> phoneGuaranteedAttemptsPerRarity = parseNonNegativeInt(value, 1);
							case "loot_drop_chance" -> phoneLootDropChance = parseFloat(value, 0.03f);
						}
					}
					case "loot" -> {
						switch (key) {
							case "loot_tables" -> i = parseLootTableList(lines, i, value);
							case "excluded_flags" -> i = parseExcludedFlagsList(lines, i, value);
							case "excluded_dolls" -> i = parseExcludedDollsList(lines, i, value);
							case "drop_chance" -> lootDropChance = parseFloat(value, 0.15f);
						}
					}
					case "config_sync" -> {
						switch (key) {
							case "auto_update_configs" -> {
								configUpdateMode = ConfigUpdateMode.parse(value, ConfigUpdateMode.MERGE);
								modeSetByNewKey = true;
							}
							// Legacy boolean key — honored only if the new mode key is absent from the file.
							case "auto_update" -> {
								if (!modeSetByNewKey) {
									configUpdateMode = parseBoolean(value, true) ? ConfigUpdateMode.MERGE : ConfigUpdateMode.OFF;
								}
							}
							case "backup_before_update" -> configBackupBeforeUpdate = parseBoolean(value, true);
							case "frozen_files" -> i = parseStringFileList(lines, i, value, frozenConfigFiles);
							case "show_files" -> i = parseStringFileList(lines, i, value, shownConfigFiles);
							case "hide_files" -> i = parseStringFileList(lines, i, value, hiddenConfigFiles);
						}
					}
				}
			}

			PokeblocksLog.LOGGER.debug("Loaded config: doll_popping_enabled={}, kick_on_decline={}, include_builtin_assets={}, "
					+ "pack_distribution={}, remote_url_set={}, self_host_address={}, drop_chance={}, "
					+ "loot_tables={}, loot_table_wildcards={}, excluded_flags={}, excluded_dolls={}, "
					+ "config_update_mode={}, config_backup={}, frozen_config_files={}, shown_config_files={}, hidden_config_files={}",
					dollPoppingEnabled, kickOnDecline, includeBuiltinAssets, packDistribution.token(), !remotePackUrl.isBlank(),
					selfHostAddress.isBlank() ? "(auto)" : selfHostAddress, lootDropChance, lootTables, lootTableWildcards.size(),
					excludedLootFlags, excludedLootDolls, configUpdateMode, configBackupBeforeUpdate, frozenConfigFiles,
					shownConfigFiles, hiddenConfigFiles);

		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("Failed to load config.toml", e);
		}
	}

	/**
	 * Defines every config key, its category, comment, and default value.
	 * Order matters — keys are appended in this order within their category.
	 */
	private record KeyDef(String category, String key, String comment, String defaultValue) {}

	private static final List<KeyDef> ALL_KEYS = List.of(
			new KeyDef("config_sync", "auto_update_configs",
					"""
					# How Pokeblocks updates its bundled config & override files (doll_rarity.json,
					# rarity_weights.json, loot_groups.json, etc.) when the mod itself updates:
					#   merge     = (default, recommended) pull in new and changed defaults, but KEEP your
					#               own custom entries and your deletions. Non-destructive.
					#   overwrite = replace these files with the mod's defaults on startup, DISCARDING your
					#               edits. A timestamped backup is taken first (see backup_before_update).
					#   off       = never touch your files automatically; pull updates yourself afterwards
					#               with "/pokeblocks config sync".""",
					"merge"),
			new KeyDef("config_sync", "backup_before_update",
					"# Take a timestamped backup (in config/Pokeblocks/.sync/backups/) before a merge or overwrite rewrites a file.",
					"true"),
			new KeyDef("config_sync", "frozen_files",
					"""
					# Individual config files to exclude from auto-update (merge or overwrite) while the rest keep updating.
					# Use this for a file you have heavily customized. Example: ["doll_rarity.json"].
					# Run "/pokeblocks config status" to preview pending changes, "/pokeblocks config sync" to apply.""",
					"""
					[
					]"""),
			new KeyDef("config_sync", "show_files",
					"""
					# Config files that are hidden by default but that you want written into this folder so you
					# can edit them. To reduce clutter, Pokeblocks keeps niche files tied to hardcoded values out
					# of the folder by default (e.g. rarity_acquisition_divisors.json, ignored_rarity_flags.json) —
					# their bundled defaults still apply. List a file here to have it created for editing.
					# Example: ["rarity_acquisition_divisors.json"].""",
					"""
					[
					]"""),
			new KeyDef("config_sync", "hide_files",
					"""
					# Config files to keep OUT of this folder even though they are shown by default. Their bundled
					# defaults still apply; this only removes clutter. An already-existing file is left in place.
					# Example: ["loot_groups.json"].""",
					"""
					[
					]"""),
			new KeyDef("eastereggs", "doll_popping_enabled",
					"# Whether dolls can \"pop\" (break into wool and string) when right-clicked too many times quickly.",
					"true"),
			new KeyDef("creative", "show_incomplete_items",
					"""
					# Whether items whose feature is still unfinished (the laser pointer, the doll compendium and
					# the figurine compendium) appear in the creative menu. When false (default) they are hidden.
					# When true they show up but carry a tooltip warning that the feature may not be fully working.""",
					"false"),
			new KeyDef("phone", "enabled",
					"""
					# Whether Pokedoll Phones ring at all. When false a phone never starts a new call;
					# an already-active dig quest still finishes normally.""",
					"true"),
			new KeyDef("phone", "average_call_interval_minutes",
					"""
					# Roughly how many minutes pass, on average, between calls while a Pokedoll Phone sits
					# in someone's inventory. Calls arrive randomly around this average.""",
					"15"),
			new KeyDef("phone", "ring_seconds",
					"# How long an incoming call rings (and the phone buzzes) before it counts as missed.",
					"30"),
			new KeyDef("phone", "dig_sites",
					"# How many dig sites an accepted call scatters around the player.",
					"6"),
			new KeyDef("phone", "site_radius",
					"""
					# Radius in blocks around the player that dig sites can appear in. Sites only spawn in
					# open air above grass/dirt-type ground and never replace existing blocks.""",
					"32"),
			new KeyDef("phone", "guaranteed_attempts",
					"""
					# The buried doll is guaranteed to turn up within this many fully-dug sites (the winning
					# site index is rolled when the quest starts). The other sites yield junk.""",
					"3"),
			new KeyDef("phone", "durability",
					"""
					# How many calls a Pokedoll Phone can answer before it wears out. Each accepted call
					# costs one durability point and the phone breaks when it runs out. Applied as phones
					# are crafted (existing phones keep the durability they were made with).""",
					"4"),
			new KeyDef("phone", "dig_sites_per_rarity",
					"""
					# Extra dig sites added per rarity tier of the phone's attuned doll (Common +0,
					# Uncommon +1, Rare +2, Epic +3, Legendary +4, Shiny +5, Gigantic +6, times this
					# number), on top of dig_sites. So a Rare phone with the default scatters 6 + 2 = 8
					# sites. Set to 0 to disable rarity scaling of dig sites.""",
					"1"),
			new KeyDef("phone", "guaranteed_attempts_per_rarity",
					"""
					# Extra guaranteed attempts added per rarity tier of the phone's attuned doll (same
					# tier scale as dig_sites_per_rarity, times this number), on top of guaranteed_attempts.
					# So a Rare phone with the default guarantees the doll within 3 + 2 = 5 digs. Set to 0
					# to disable rarity scaling of the guarantee.""",
					"1"),
			new KeyDef("phone", "loot_drop_chance",
					"""
					# Chance (0.0 to 1.0) that an unattuned Pokedoll Phone appears in a configured loot chest
					# (the same tables as [loot] loot_tables). Unattuned phones ring with random doll callers.
					# Set to 0 to keep phones out of loot entirely.""",
					"0.03"),
			new KeyDef("figurine", "memorial_wander_radius",
					"""
					# How far (in blocks) a figurine revived by placing its memorial doll wanders from the
					# doll. Memorial figurines stay tamed (sit/stand on right-click) but no longer follow
					# their owner — they roam around the memorial spot within this radius instead.""",
					"16"),
			new KeyDef("resourcepack", "kick_on_decline",
					"# Whether to kick players who decline the custom Pokeblocks resource pack.",
					"true"),
			new KeyDef("resourcepack", "include_builtin_assets",
					"""
					# Whether the served pack also bundles the doll/figurine/decoration assets that ship inside
					# the Pokeblocks mod itself, in addition to any admin-added custom assets. With this on
					# (default), players whose Pokeblocks is OLDER than the server's still see dolls added by a
					# newer mod update - the server supplies the missing models/textures via the pack, so client
					# updates are optional for releases that only add dolls. (Updates that add new blocks, items
					# or features still require a matching client update.) With this off, only admin custom
					# assets are served, and players need the server's mod version to see newly added dolls.""",
					"true"),
			new KeyDef("resourcepack", "delta_serving",
					"""
					# Whether joining players with a delta-capable Pokeblocks negotiate a SMALL per-player pack
					# holding only what their install is missing (new dolls, admin custom assets, the server's
					# rarity/name overrides) instead of downloading the full pack. Players whose game can't
					# negotiate (older mod versions) automatically get the full pack instead. Only applies when
					# distribution = self_host; remote_url always advertises the full remote zip.""",
					"true"),
			new KeyDef("resourcepack", "distribution",
					"""
					# How the custom resource pack is delivered to players:
					#   self_host  = (default) Pokeblocks serves the pack from a small built-in web server and
					#                tells clients where to download it. Works out of the box on LAN / singleplayer.
					#                For a PUBLIC server, also set self_host_address below to an address players
					#                can actually reach (the auto-detected one is usually a LAN-only IP).
					#   remote_url = advertise your own remote_url instead of self-hosting (e.g. a CDN or web host).
					#                Set remote_url (and ideally remote_sha1) below.""",
					"self_host"),
			new KeyDef("resourcepack", "self_host_address",
					"""
					# Host or IP advertised in the self-hosted download URL. Leave blank to auto-detect
					# (server-ip from server.properties, else a LAN address). Set this to your server's public
					# address/domain so off-LAN players can download the pack. Only used when distribution = self_host.""",
					"\"\""),
			new KeyDef("resourcepack", "remote_url",
					"""
					# Direct download URL of the pack .zip when distribution = remote_url
					# (e.g. "https://cdn.example.com/pokeblocks_pack.zip"). Ignored when self-hosting.""",
					"\"\""),
			new KeyDef("resourcepack", "remote_sha1",
					"""
					# SHA-1 hash of the file at remote_url. Strongly recommended so clients can verify and cache
					# the download. If left blank, Pokeblocks uses the hash of the pack it built locally — in which
					# case you MUST upload that exact built zip to remote_url or clients will reject the download.""",
					"\"\""),
			new KeyDef("loot", "drop_chance",
					"# Chance (0.0 to 1.0) that a Pokedoll appears in a configured loot chest.",
					"0.33f"),
			new KeyDef("loot", "loot_tables",
					"""
					# Loot tables that Pokeblocks items can be injected into.
					# Use the full namespaced loot table ID (e.g. "minecraft:chests/simple_dungeon").""",
					"""
					[
					  "minecraft:chests/end_city_treasure",
					  "minecraft:chests/simple_dungeon",
					  "minecraft:chests/village/village_weaponsmith",
					  "minecraft:chests/village/village_toolsmith",
					  "minecraft:chests/village/village_armorer",
					  "minecraft:chests/village/village_cartographer",
					  "minecraft:chests/village/village_mason",
					  "minecraft:chests/village/village_shepherd",
					  "minecraft:chests/village/village_butcher",
					  "minecraft:chests/village/village_fletcher",
					  "minecraft:chests/village/village_fisher",
					  "minecraft:chests/village/village_tannery",
					  "minecraft:chests/village/village_temple",
					  "minecraft:chests/village/village_desert_house",
					  "minecraft:chests/village/village_plains_house",
					  "minecraft:chests/village/village_taiga_house",
					  "minecraft:chests/village/village_snowy_house",
					  "minecraft:chests/village/village_savanna_house",
					  "minecraft:chests/abandoned_mineshaft",
					  "minecraft:chests/nether_bridge",
					  "minecraft:chests/stronghold_library",
					  "minecraft:chests/stronghold_crossing",
					  "minecraft:chests/stronghold_corridor",
					  "minecraft:chests/desert_pyramid",
					  "minecraft:chests/jungle_temple",
					  "minecraft:chests/igloo_chest",
					  "minecraft:chests/woodland_mansion",
					  "minecraft:chests/pillager_outpost",
					  "minecraft:chests/bastion_treasure",
					  "minecraft:chests/bastion_other",
					  "minecraft:chests/bastion_bridge",
					  "minecraft:chests/bastion_hoglin_stable",
					  "minecraft:chests/ancient_city",
					  "minecraft:chests/ancient_city_ice_box"
					]"""),
			new KeyDef("loot", "excluded_flags",
					"""
					# Model flags to exclude from loot table drops.
					# Dolls with any of these flags will never appear in loot.""",
					"""
					[
					  "gigantic",
					  "noice"
					]"""),
			new KeyDef("loot", "excluded_dolls",
					"""
					# Specific doll IDs to exclude from loot table drops, regardless of their rarity.
					# Format: "pokemon" to exclude all variants, or "pokemon flag1 flag2" for a specific variant.
					# Example: "substitute" excludes all substitute variants; "substitute shiny" excludes only the shiny one.""",
					"""
					[
					  "substitute"
					]""")
	);

	/**
	 * Reads the existing config, detects missing keys and categories,
	 * and appends them with their default values and comments.
	 */
	private static void patchMissingKeys() {
		try {
			List<String> lines = new ArrayList<>(Files.readAllLines(configPath));

			// Collect existing keys per category from the file
			Map<String, Set<String>> existingKeys = new HashMap<>();
			String currentCategory = "";

			for (String rawLine : lines) {
				String line = rawLine.trim();
				if (line.startsWith("[") && line.endsWith("]")) {
					currentCategory = line.substring(1, line.length() - 1).trim().toLowerCase();
					existingKeys.computeIfAbsent(currentCategory, k -> new HashSet<>());
					continue;
				}
				int eqIndex = line.indexOf('=');
				if (eqIndex > 0 && !line.startsWith("#")) {
					String key = line.substring(0, eqIndex).trim().toLowerCase();
					existingKeys.computeIfAbsent(currentCategory, k -> new HashSet<>()).add(key);
				}
			}

			boolean modified = false;

			for (KeyDef def : ALL_KEYS) {
				Set<String> keys = existingKeys.get(def.category());
				if (keys != null && keys.contains(def.key())) continue;

				// Category might not exist yet
				if (!existingKeys.containsKey(def.category())) {
					lines.add("");
					lines.add("[" + def.category() + "]");
					existingKeys.put(def.category(), new HashSet<>());
				}

				// Find the end of the category section to insert the new key
				int insertIndex = findCategoryEnd(lines, def.category());

				List<String> toInsert = new ArrayList<>();
				toInsert.add(""); // blank line before comment
				for (String commentLine : def.comment().split("\n")) {
					toInsert.add(commentLine);
				}
				toInsert.add(def.key() + " = " + resolveDefaultValue(def, lines));

				lines.addAll(insertIndex, toInsert);
				existingKeys.get(def.category()).add(def.key());
				modified = true;

				PokeblocksLog.LOGGER.info("Added missing config key: [{}] {}", def.category(), def.key());
			}

			if (modified) {
				Files.write(configPath, lines);
			}

		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("Failed to patch config.toml", e);
		}
	}

	/**
	 * The default value to write when inserting a missing key. Normally {@link KeyDef#defaultValue()},
	 * but the new {@code auto_update_configs} key is seeded from a legacy {@code [config_sync] auto_update}
	 * boolean so an admin's earlier on/off choice survives the upgrade.
	 */
	private static String resolveDefaultValue(KeyDef def, List<String> lines) {
		if (def.category().equals("config_sync") && def.key().equals("auto_update_configs")) {
			Boolean legacy = findLegacyAutoUpdate(lines);
			if (legacy != null) {
				String mode = legacy ? "merge" : "off";
				PokeblocksLog.LOGGER.info("Migrated legacy [config_sync] auto_update={} to auto_update_configs={}; "
						+ "the old auto_update line is now ignored and can be removed.", legacy, mode);
				return mode;
			}
		}
		return def.defaultValue();
	}

	/** Reads a legacy {@code [config_sync] auto_update} boolean from the file, or {@code null} if absent. */
	private static Boolean findLegacyAutoUpdate(List<String> lines) {
		boolean inConfigSync = false;
		for (String rawLine : lines) {
			String line = rawLine.trim();
			if (line.startsWith("[") && line.endsWith("]")) {
				inConfigSync = line.substring(1, line.length() - 1).trim().equalsIgnoreCase("config_sync");
				continue;
			}
			if (!inConfigSync || line.startsWith("#")) continue;
			int eqIndex = line.indexOf('=');
			if (eqIndex <= 0) continue;
			if (!line.substring(0, eqIndex).trim().equalsIgnoreCase("auto_update")) continue;
			String value = line.substring(eqIndex + 1).trim();
			int commentIndex = value.indexOf('#');
			if (commentIndex >= 0) value = value.substring(0, commentIndex).trim();
			if (value.equalsIgnoreCase("false")) return false;
			if (value.equalsIgnoreCase("true")) return true;
		}
		return null;
	}

	/**
	 * Finds the line index where new keys should be inserted for a category.
	 * This is the line before the next category header, or the end of the file.
	 */
	private static int findCategoryEnd(List<String> lines, String category) {
		boolean inCategory = false;

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i).trim();

			if (line.startsWith("[") && line.endsWith("]")) {
				String cat = line.substring(1, line.length() - 1).trim().toLowerCase();
				if (cat.equals(category)) {
					inCategory = true;
					continue;
				} else if (inCategory) {
					// Found the next category — insert before it
					return i;
				}
			}
		}

		// Category is the last one (or only one), insert at end
		return lines.size();
	}

	private static int parseLootTableList(List<String> lines, int startIndex, String firstLineValue) {
		StringBuilder builder = new StringBuilder(firstLineValue);

		int i = startIndex;

		while (!builder.toString().contains("]") && i + 1 < lines.size()) {
			i++;
			builder.append(lines.get(i).trim());
		}

		String full = builder.toString();

		int start = full.indexOf('[');
		int end = full.lastIndexOf(']');

		if (start < 0 || end < 0 || end <= start) return i;

		String inner = full.substring(start + 1, end);
		String[] entries = inner.split(",");

		for (String entry : entries) {
			String cleaned = entry.trim();

			if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
				cleaned = cleaned.substring(1, cleaned.length() - 1);
			}

			if (!cleaned.isEmpty()) {
				if (cleaned.contains("*")) {
					lootTableWildcards.add(globToPattern(cleaned));
				} else {
					ResourceLocation id = ResourceLocation.tryParse(cleaned);
					if (id != null) {
						lootTables.add(id);
					} else {
						PokeblocksLog.LOGGER.warn("Invalid loot table id: {}", cleaned);
					}
				}
			}
		}

		return i;
	}

	private static int parseExcludedFlagsList(List<String> lines, int startIndex, String firstLineValue) {
		StringBuilder builder = new StringBuilder(firstLineValue);

		int i = startIndex;

		while (!builder.toString().contains("]") && i + 1 < lines.size()) {
			i++;
			builder.append(lines.get(i).trim());
		}

		String full = builder.toString();

		int start = full.indexOf('[');
		int end = full.lastIndexOf(']');

		if (start < 0 || end < 0 || end <= start) return i;

		String inner = full.substring(start + 1, end);
		String[] entries = inner.split(",");

		excludedLootFlags.clear();

		for (String entry : entries) {
			String cleaned = entry.trim();

			if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
				cleaned = cleaned.substring(1, cleaned.length() - 1);
			}

			if (!cleaned.isEmpty()) {
				ModelFlag flag = ModelFlag.fromTagName(cleaned);
				if (flag != null) {
					excludedLootFlags.add(flag);
				} else {
					PokeblocksLog.LOGGER.warn("Invalid excluded flag: '{}'. Valid flags: {}", cleaned, ModelFlag.allTagNames());
				}
			}
		}

		return i;
	}

	private static int parseExcludedDollsList(List<String> lines, int startIndex, String firstLineValue) {
		StringBuilder builder = new StringBuilder(firstLineValue);

		int i = startIndex;

		while (!builder.toString().contains("]") && i + 1 < lines.size()) {
			i++;
			builder.append(lines.get(i).trim());
		}

		String full = builder.toString();

		int start = full.indexOf('[');
		int end = full.lastIndexOf(']');

		if (start < 0 || end < 0 || end <= start) return i;

		String inner = full.substring(start + 1, end);
		String[] entries = inner.split(",");

		excludedLootDolls.clear();

		for (String entry : entries) {
			String cleaned = entry.trim();

			if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
				cleaned = cleaned.substring(1, cleaned.length() - 1);
			}

			cleaned = cleaned.trim().toLowerCase();
			if (cleaned.isEmpty()) continue;

			// Normalize to canonical key: first word is pokemon, remaining words are flag tag names
			String[] words = cleaned.split("\\s+");
			String pokemon = words[0];
			Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
			for (int j = 1; j < words.length; j++) {
				ModelFlag flag = ModelFlag.fromTagName(words[j]);
				if (flag != null) {
					flags.add(flag);
				} else {
					PokeblocksLog.LOGGER.warn("Unknown flag '{}' in excluded_dolls entry: {}", words[j], cleaned);
				}
			}
			excludedLootDolls.add(DollRarityOverrides.buildKey(pokemon, flags));
		}

		return i;
	}

	/** Parses a (possibly multi-line) JSON string array into {@code target}, one file name per entry. */
	private static int parseStringFileList(List<String> lines, int startIndex, String firstLineValue, Set<String> target) {
		StringBuilder builder = new StringBuilder(firstLineValue);

		int i = startIndex;

		while (!builder.toString().contains("]") && i + 1 < lines.size()) {
			i++;
			builder.append(lines.get(i).trim());
		}

		String full = builder.toString();

		int start = full.indexOf('[');
		int end = full.lastIndexOf(']');

		if (start < 0 || end < 0 || end <= start) return i;

		String inner = full.substring(start + 1, end);
		String[] entries = inner.split(",");

		target.clear();

		for (String entry : entries) {
			String cleaned = entry.trim();

			if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
				cleaned = cleaned.substring(1, cleaned.length() - 1);
			}

			cleaned = cleaned.trim();
			if (!cleaned.isEmpty()) {
				target.add(cleaned);
			}
		}

		return i;
	}

	/**
	 * Returns true if this doll variant should be excluded from loot tables.
	 * A bare pokemon name (e.g. "substitute") matches all variants of that pokemon;
	 * a name with flags (e.g. "substitute shiny") matches only that exact variant.
	 */
	public static boolean isDollExcludedFromLoot(String pokemon, Set<ModelFlag> flags) {
		// Bare pokemon name in the set means all variants of that pokemon are excluded.
		// Lowercase to match the canonical keys (buildKey lowercases) and LootGroup.containsDoll.
		if (excludedLootDolls.contains(pokemon.toLowerCase())) return true;
		// Check for an exact variant match using the canonical key
		return excludedLootDolls.contains(DollRarityOverrides.buildKey(pokemon, flags));
	}

	public static Pattern globToPattern(String glob) {
		StringBuilder regex = new StringBuilder("^");
		for (int i = 0; i < glob.length(); i++) {
			char c = glob.charAt(i);
			if (c == '*') {
				regex.append(".*");
			} else {
				regex.append(Pattern.quote(String.valueOf(c)));
			}
		}
		regex.append("$");
		return Pattern.compile(regex.toString());
	}

	private static void writeDefaults() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("# Pokeblocks Configuration\n");

		String lastCategory = "";
		for (KeyDef def : ALL_KEYS) {
			if (!def.category().equals(lastCategory)) {
				sb.append("\n[").append(def.category()).append("]\n");
				lastCategory = def.category();
			}
			for (String commentLine : def.comment().split("\n")) {
				sb.append(commentLine).append("\n");
			}
			sb.append(def.key()).append(" = ").append(def.defaultValue()).append("\n");
		}

		Files.writeString(configPath, sb.toString());
	}

	/** Strips a single pair of surrounding single or double quotes from a TOML scalar string value. */
	private static String unquote(String value) {
		if (value == null) return "";
		String v = value.trim();
		if (v.length() >= 2) {
			char first = v.charAt(0);
			char last = v.charAt(v.length() - 1);
			if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
				return v.substring(1, v.length() - 1);
			}
		}
		return v;
	}

	private static boolean parseBoolean(String value, boolean defaultValue) {
		value = value.toLowerCase();
		if (value.equals("true")) return true;
		if (value.equals("false")) return false;
		return defaultValue;
	}

	private static float parseFloat(String value, float defaultValue) {
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/** Parses a strictly-positive int, falling back to the default for junk or non-positive values. */
	private static int parsePositiveInt(String value, int defaultValue) {
		try {
			int parsed = Integer.parseInt(value.trim());
			return parsed > 0 ? parsed : defaultValue;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/** Parses a non-negative int (0 allowed, e.g. to disable scaling), else the default. */
	private static int parseNonNegativeInt(String value, int defaultValue) {
		try {
			int parsed = Integer.parseInt(value.trim());
			return parsed >= 0 ? parsed : defaultValue;
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}