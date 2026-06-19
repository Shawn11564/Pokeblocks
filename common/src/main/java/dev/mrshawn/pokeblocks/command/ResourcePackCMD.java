package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackBuilder;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.resourcepack.ResourcePackServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * {@code /pokeblocks resourcepack rebuild|saveexample} — admin tools for the custom resource pack.
 * <ul>
 *   <li>{@code rebuild} rebuilds the served pack from {@code config/Pokeblocks/resourcepack/} and
 *       re-pushes it to connected clients.</li>
 *   <li>{@code saveexample} writes a fully-worked example sub-pack (every pack feature, plus a README)
 *       into {@code config/Pokeblocks/resourcepack/example/}.</li>
 * </ul>
 */
public class ResourcePackCMD {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("pokeblocks").then(
						Commands.literal("resourcepack")
								.requires(src -> src.hasPermission(2))
								.then(Commands.literal("rebuild").executes(ResourcePackCMD::rebuild))
								.then(Commands.literal("saveexample").executes(ResourcePackCMD::saveExample))
				));
	}

	private static int rebuild(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack src = ctx.getSource();
		MinecraftServer server = src.getServer();
		Path gameDir = server.getServerDirectory();

		Path customDirCandidate = gameDir.resolve("config").resolve("Pokeblocks").resolve("resourcepack").resolve("custom");

		try {
			src.sendSuccess(() -> Component.literal((Files.exists(customDirCandidate) ? "[FOUND] " : "[MISS]  ") + customDirCandidate), false);

			Path customDir = CustomPackBuilder.findCustomDir(gameDir);
			if (customDir != null) {
				try (var stream = Files.list(customDir)) {
					List<String> children = stream.map(Path::getFileName).map(Object::toString).collect(Collectors.toList());
					src.sendSuccess(() -> Component.literal("Custom dir: " + customDir + " (contains: " + String.join(", ", children) + ")"), false);
				}
			}

			// Rebuild and cache the pack from every sub-pack under resourcepack/.
			CustomPackManager.buildAndCache(server);

			if (!CustomPackManager.hasPack()) {
				src.sendFailure(Component.literal("No custom resources found; nothing to build."));
				return 0;
			}

			Path zip = CustomPackManager.getCachedPack();
			String sha = CustomPackManager.getCachedSha();
			src.sendSuccess(() -> Component.literal("Built resource pack: " + zip), false);
			src.sendSuccess(() -> Component.literal("SHA1: " + sha), false);

			String url = ResourcePackServer.start(server, zip);
			src.sendSuccess(() -> Component.literal("Serving resource pack at: " + url), false);

			UUID uuid = ResourcePackServer.packUuid(sha);
			ClientboundResourcePackPushPacket pkt = new ClientboundResourcePackPushPacket(uuid, url, sha, PokeblocksConfig.isKickOnDecline(), Optional.empty());
			server.getConnection().getConnections().forEach(conn -> conn.send(pkt));

			return 1;
		} catch (Exception e) {
			src.sendFailure(Component.literal("Failed to build resource pack: " + e.getMessage()));
			return 0;
		}
	}

	private static int saveExample(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack src = ctx.getSource();
		Path gameDir = src.getServer().getServerDirectory();
		Path pack = gameDir.resolve("config").resolve("Pokeblocks").resolve("resourcepack").resolve("example");

		try {
			int copied = 0;
			// Dolls — model + base texture + shiny flag-variant texture + animation (typed layout).
			copied += copyAsset("geo/block/pokedoll_applin.geo.json",              pack.resolve("assets/dolls/models/exampledoll.geo.json"));
			copied += copyAsset("textures/block/pokedoll_applin_texture.png",       pack.resolve("assets/dolls/textures/exampledoll.png"));
			copied += copyAsset("textures/block/pokedoll_applin_shiny_texture.png", pack.resolve("assets/dolls/textures/exampledoll_shiny.png"));
			copied += copyAsset("animations/block/pokedoll_applin.animation.json",  pack.resolve("assets/dolls/animations/exampledoll.animation.json"));
			// Figurine — model + texture.
			copied += copyAsset("geo/block/doncheadle_figurine.geo.json",           pack.resolve("assets/figurines/models/examplefig.geo.json"));
			copied += copyAsset("textures/block/doncheadle_figurine_texture.png",   pack.resolve("assets/figurines/textures/examplefig.png"));
			// Decoration (generic data-driven block) — model + texture.
			copied += copyAsset("geo/block/pokemon_trophy.geo.json",                pack.resolve("assets/decorations/models/exampledeco.geo.json"));
			copied += copyAsset("textures/block/pokemon_trophy_texture.png",        pack.resolve("assets/decorations/textures/exampledeco.png"));

			// Per-id setting overrides (#49). Each affects only the example ids, except rarity_weights
			// which is global and is left at its default value here so it has no balance impact.
			writeText(pack.resolve("pokeblocks/config/doll_rarity.json"),                 "[\n  \"exampledoll rare\"\n]\n");
			writeText(pack.resolve("pokeblocks/config/ignored_rarity_flags.json"),        "[\n  \"exampledoll shiny\"\n]\n");
			writeText(pack.resolve("pokeblocks/config/rarity_acquisition_divisors.json"), "[\n  \"exampledoll 2\"\n]\n");
			writeText(pack.resolve("pokeblocks/config/figurine_names.json"),              "[\n  \"examplefig Example Figurine\"\n]\n");
			writeText(pack.resolve("pokeblocks/config/figurine_tags.json"),              "[\n  \"examplefig cobblemon_team\"\n]\n");
			writeText(pack.resolve("pokeblocks/config/rarity_weights.json"),             "{\n  \"rare\": 150\n}\n");

			writeText(pack.resolve("README.txt"), EXAMPLE_README);

			final int assets = copied;
			src.sendSuccess(() -> Component.literal("Saved example resource pack to " + pack), false);
			src.sendSuccess(() -> Component.literal("Wrote " + assets + " asset file(s), 6 override file(s) and a README.txt."), false);
			src.sendSuccess(() -> Component.literal("Run \"/pokeblocks resourcepack rebuild\" (or rejoin) to load it."), false);
			PokeblocksLog.LOGGER.info("Saved example resource pack to {} ({} assets)", pack, assets);
			return 1;
		} catch (Exception e) {
			src.sendFailure(Component.literal("Failed to save example pack: " + e.getMessage()));
			PokeblocksLog.LOGGER.error("Failed to save example resource pack", e);
			return 0;
		}
	}

	/** Copies a bundled asset from {@code /assets/pokeblocks/<rel>} to {@code dest}. Returns 1 if copied. */
	private static int copyAsset(String rel, Path dest) throws IOException {
		try (InputStream is = ResourcePackCMD.class.getResourceAsStream("/assets/pokeblocks/" + rel)) {
			if (is == null) {
				PokeblocksLog.LOGGER.warn("Example pack: bundled asset not found, skipping: {}", rel);
				return 0;
			}
			Files.createDirectories(dest.getParent());
			Files.copy(is, dest, StandardCopyOption.REPLACE_EXISTING);
			return 1;
		}
	}

	private static void writeText(Path dest, String content) throws IOException {
		Files.createDirectories(dest.getParent());
		Files.writeString(dest, content);
	}

	private static final String EXAMPLE_README = """
			Pokeblocks example resource pack (generated by /pokeblocks resourcepack saveexample)
			====================================================================================
			This sub-pack demonstrates every feature available to Pokeblocks resource packs. It lives
			in config/Pokeblocks/resourcepack/example/ and is loaded automatically when the custom pack
			is (re)built. Delete this folder to remove it.

			TYPED LAYOUT:  assets/<type>/<kind>/<bare-id>.<ext>
			  type = dolls | figurines | decorations
			  kind = models | textures | animations
			File names are the BARE id; the type folder supplies the internal naming on build:
			  dolls/        ->  pokedoll_<id>
			  figurines/    ->  <id>_figurine
			  decorations/  ->  <id>_decoration
			(textures also get a _texture suffix). Dolls keep flag suffixes, e.g.
			  dolls/textures/exampledoll_shiny.png  ->  pokedoll_exampledoll_shiny_texture.png
			The older flat layout (assets/models|textures|animations/<full_name>) still works as a
			fallback, but the typed layout above is preferred.

			WHAT THIS PACK ADDS (all copied from existing assets so they render — replace with your own):
			  dolls/exampledoll       : a doll with model + texture + shiny texture + animation
			  figurines/examplefig    : a figurine with model + texture
			  decorations/exampledeco : a generic data-driven decoration with model + texture

			PER-ID SETTING OVERRIDES (pokeblocks/config/*.json)
			These are merged into the server's config by id, letting a pack ship the same settings the
			built-in content uses. Only the listed ids are affected; everything else keeps its default.
			  doll_rarity.json                  ["exampledoll rare"]            set a doll's rarity
			  ignored_rarity_flags.json         ["exampledoll shiny"]          ignore a flag for rarity calc
			  rarity_acquisition_divisors.json  ["exampledoll 2"]              extra rarity divisor
			  figurine_names.json               ["examplefig Example Figurine"] figurine display name
			  figurine_tags.json                ["examplefig cobblemon_team"]  figurine tag
			  rarity_weights.json               { "rare": 150 }                GLOBAL rarity->loot weight
			                                                                   (150 = default; change to rebalance)

			PACK PRECEDENCE
			  When more than one sub-pack provides the same file, the custom/ folder wins; otherwise the
			  last pack alphabetically wins. Conflicts are logged on (re)build, naming the file and packs.

			APPLYING / TESTING
			  - Run /pokeblocks resourcepack rebuild (or rejoin the world) to build + serve the pack.
			  - exampledoll / examplefig / exampledeco then appear in the Creative menu and render in-world.
			  - This is a SERVER-side admin pack. Generating or changing it re-hashes the served pack, so
			    clients download it once and then reuse their cached copy on reconnect.
			""";
}
