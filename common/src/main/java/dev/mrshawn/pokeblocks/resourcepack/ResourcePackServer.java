package dev.mrshawn.pokeblocks.resourcepack;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import dev.mrshawn.pokeblocks.config.PackDistribution;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;

public final class ResourcePackServer {
	private static HttpServer server;
	private static Path servedFile;
	private static String url;
	/** Last remote-mode log line emitted; remote distribution is re-evaluated on every push (incl. each join),
	 *  so we only log a given guidance/warning message when it changes rather than once per player. */
	private static String lastRemoteLog;

	private ResourcePackServer() {}

	/** Stable pack identity derived from content hash, so the vanilla client reuses its cached
	 *  download across restarts when the pack is unchanged. */
	public static UUID packUuid(String sha) {
		return UUID.nameUUIDFromBytes(("pokeblocks-pack:" + (sha == null ? "" : sha)).getBytes(StandardCharsets.UTF_8));
	}

	public static synchronized String start(MinecraftServer mcServer, Path file) throws IOException {
		if (server != null && Files.exists(servedFile) && servedFile.equals(file) && url != null) {
			return url;
		}

		stop();

		// choose bind address and host
		// bind to all interfaces so clients can reach it; advertise localhost address for URL
		InetAddress bindAddr = InetAddress.getByName("0.0.0.0");

		// pick ephemeral port
		server = HttpServer.create(new InetSocketAddress(bindAddr, 0), 0);
		server.setExecutor(Executors.newSingleThreadExecutor());

		// context path
		String path = "/pokeblocks_custom_pack.zip";
		servedFile = file;

		server.createContext(path, new HttpHandler() {
			@Override
			public void handle(HttpExchange exchange) throws IOException {
				try {
					if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
						exchange.sendResponseHeaders(405, -1);
						return;
					}

					String sha = CustomPackManager.getCachedSha();
					String etag = (sha == null || sha.isEmpty()) ? null : "\"" + sha + "\"";
					if (etag != null && etag.equals(exchange.getRequestHeaders().getFirst("If-None-Match"))) {
						exchange.sendResponseHeaders(304, -1);
						return;
					}

					Headers h = exchange.getResponseHeaders();
					h.add("Content-Type", "application/zip");
					if (etag != null) h.add("ETag", etag);
					h.add("Cache-Control", "max-age=0, must-revalidate");
					h.add("Last-Modified", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(
							java.time.ZonedDateTime.ofInstant(
									java.time.Instant.ofEpochMilli(Files.getLastModifiedTime(servedFile).toMillis()),
									java.time.ZoneOffset.UTC)));
					long size = Files.size(servedFile);
					exchange.sendResponseHeaders(200, size);
					try (OutputStream os = exchange.getResponseBody()) {
						Files.copy(servedFile, os);
					}
				} catch (IOException ex) {
					exchange.sendResponseHeaders(500, -1);
				} finally {
					exchange.close();
				}
			}
		});

		server.start();

		int port = server.getAddress().getPort();

		String hostForUrl;
		String addressOverride = PokeblocksConfig.getSelfHostAddress();
		if (addressOverride != null && !addressOverride.isBlank()) {
			// Admin-provided host/IP (e.g. a public address for an internet-facing server).
			hostForUrl = addressOverride.trim();
		} else if (mcServer.isDedicatedServer()) {
			// For dedicated servers, try to get the server IP from server.properties,
			// fall back to finding a usable network address
			String serverIp = mcServer.getLocalIp();
			if (serverIp != null && !serverIp.isEmpty()) {
				hostForUrl = serverIp;
			} else {
				hostForUrl = getUsableAddress();
			}
		} else {
			hostForUrl = "127.0.0.1";
		}

		url = URI.create("http://" + hostForUrl + ":" + port + path).toString();
		PokeblocksLog.LOGGER.info("Resource pack server started at: {}", url);
		return url;
	}

	private static String getUsableAddress() {
		try {
			var interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				var ni = interfaces.nextElement();
				if (ni.isLoopback() || !ni.isUp()) continue;
				var addrs = ni.getInetAddresses();
				while (addrs.hasMoreElements()) {
					var addr = addrs.nextElement();
					// Prefer IPv4 site-local addresses (192.168.x.x, 10.x.x.x, etc.)
					if (addr instanceof Inet4Address && addr.isSiteLocalAddress()) {
						return addr.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			PokeblocksLog.LOGGER.warn("Failed to enumerate network interfaces", e);
		}
		// Last resort
		return "127.0.0.1";
	}

	public static synchronized void stop() {
		if (server != null) {
			server.stop(0);
			server = null;
			servedFile = null;
			url = null;
		}
	}

	/** The download URL to advertise and the SHA-1 clients verify it against. */
	public record PackPush(String url, String sha) {}

	/**
	 * Resolves how the cached pack should be handed to clients based on {@code [resourcepack] distribution},
	 * starting the built-in HTTP server when self-hosting. Returns {@code null} when there is nothing to send
	 * (no built pack while self-hosting, or remote mode without a usable URL/hash). Safe to call repeatedly —
	 * {@link #start} is idempotent for an unchanged file, and remote mode tears the self-host server down.
	 */
	public static synchronized PackPush prepare(MinecraftServer mcServer) throws IOException {
		PackDistribution mode = PokeblocksConfig.getPackDistribution();

		if (mode == PackDistribution.REMOTE_URL) {
			String remoteUrl = PokeblocksConfig.getRemotePackUrl();
			if (remoteUrl == null || remoteUrl.isBlank()) {
				logRemoteOnce(true, "[ResourcePack] distribution = remote_url but resourcepack.remote_url is blank; no pack "
						+ "will be sent. Set remote_url or switch distribution back to self_host.");
				return null;
			}

			String remoteSha = PokeblocksConfig.getRemotePackSha1();
			String sha;
			if (remoteSha != null && !remoteSha.isBlank()) {
				sha = remoteSha.trim();
			} else if (CustomPackManager.hasPack()) {
				Path file = CustomPackManager.getCachedPack();
				String localSha = CustomPackManager.getCachedSha();
				sha = (localSha != null && !localSha.isEmpty()) ? localSha : CustomPackBuilder.computeSHA1(file);
				logRemoteOnce(false, "[ResourcePack] remote_url is set without remote_sha1; advertising the locally built "
						+ "pack's sha1 (" + sha + "). Upload " + file + " to " + remoteUrl + " so the hash matches, or set remote_sha1.");
			} else {
				logRemoteOnce(true, "[ResourcePack] remote_url is set but remote_sha1 is blank and no pack was built locally "
						+ "to hash; set resourcepack.remote_sha1. No pack will be sent.");
				return null;
			}

			stop(); // pointing clients at a remote host — release any self-host server we started earlier
			return new PackPush(remoteUrl.trim(), sha);
		}

		// Self-host (default): serve the locally-built zip over the built-in HTTP server.
		if (!CustomPackManager.hasPack()) return null;
		Path file = CustomPackManager.getCachedPack();
		String sha = CustomPackManager.getCachedSha();
		if (sha == null || sha.isEmpty()) sha = CustomPackBuilder.computeSHA1(file);
		String url = start(mcServer, file);
		return new PackPush(url, sha);
	}

	/** Builds the push packet, marking it required (and adding a prompt) per {@code kick_on_decline}. */
	private static ClientboundResourcePackPushPacket packet(PackPush push) {
		boolean required = PokeblocksConfig.isKickOnDecline();
		Optional<Component> prompt = required
				? Optional.of(Component.literal("This server requires the Pokeblocks resource pack to play."))
				: Optional.empty();
		return new ClientboundResourcePackPushPacket(packUuid(push.sha()), push.url(), push.sha(), required, prompt);
	}

	/** Logs a remote-mode message only when it differs from the previous one, so it doesn't repeat per join. */
	private static void logRemoteOnce(boolean warn, String msg) {
		if (msg.equals(lastRemoteLog)) return;
		lastRemoteLog = msg;
		if (warn) PokeblocksLog.LOGGER.warn(msg);
		else PokeblocksLog.LOGGER.info(msg);
	}

	/** Resolves distribution and pushes the pack to every connected client. No-op when there is nothing to send. */
	public static void pushToAll(MinecraftServer mcServer) {
		try {
			PackPush push = prepare(mcServer);
			if (push == null) return;
			ClientboundResourcePackPushPacket pkt = packet(push);
			mcServer.getConnection().getConnections().forEach(conn -> conn.send(pkt));
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("[ResourcePack] Failed to push resource pack to clients", e);
		}
	}

	/** Resolves distribution and pushes the pack to a single player (e.g. on join). No-op when nothing to send. */
	public static void pushTo(MinecraftServer mcServer, ServerPlayer player) {
		try {
			PackPush push = prepare(mcServer);
			if (push == null) return;
			player.connection.send(packet(push));
		} catch (Exception e) {
			PokeblocksLog.LOGGER.error("[ResourcePack] Failed to push resource pack to {}", player.getName().getString(), e);
		}
	}
}
