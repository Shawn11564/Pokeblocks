package dev.mrshawn.pokeblocks.resourcepack;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.server.MinecraftServer;

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
		if (mcServer.isDedicatedServer()) {
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

	public static synchronized UUID serveAndPush(MinecraftServer mcServer, Path file, boolean required) throws IOException {
		String url = start(mcServer, file);
		String sha = CustomPackManager.getCachedSha();
		if (sha == null || sha.isEmpty()) sha = CustomPackBuilder.computeSHA1(file);
		UUID uuid = packUuid(sha);
		ClientboundResourcePackPushPacket pkt = new ClientboundResourcePackPushPacket(uuid, url, sha, required, Optional.empty());
		mcServer.getConnection().getConnections().forEach(conn -> conn.send(pkt));
		return uuid;
	}
}
