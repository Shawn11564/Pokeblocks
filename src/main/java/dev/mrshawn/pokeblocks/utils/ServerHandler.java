package dev.mrshawn.pokeblocks.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ServerHandler {

	private static MinecraftServer server;

	public static void register() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> dev.mrshawn.pokeblocks.utils.ServerHandler.server = server);
	}

	public static void broadcast(String message) {
		if (server == null) return;
		server.getPlayerManager().broadcast(Text.of(message), false);
	}

	public static void broadcast(String message, Formatting formatting) {
		if (server == null) return;
		server.getPlayerManager().broadcast(Text.of(formatting + message), false);
	}

}
