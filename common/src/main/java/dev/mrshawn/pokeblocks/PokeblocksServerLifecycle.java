package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;

public final class PokeblocksServerLifecycle {
    public static void onServerStarted(MinecraftServer server) {
        try {
            Path serverDir = server.getServerDirectory();
            Path customDir = serverDir.resolve("config").resolve("Pokeblocks").resolve("custom");

            Files.createDirectories(customDir.resolve("models"));
            Files.createDirectories(customDir.resolve("textures"));

            CustomPackManager.buildAndCache(server);
        } catch (Exception e) {
            System.err.println("[Pokeblocks] Failed during server startup: " + e);
        }
    }
}