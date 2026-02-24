package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.RarityWeightConfig;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;

public final class PokeblocksServerLifecycle {

    public static void onServerStarted(MinecraftServer server) {
        try {
            Path serverDir = server.getServerDirectory();
            Path customDir = serverDir.resolve("config")
                    .resolve("Pokeblocks")
                    .resolve("resourcepack")
                    .resolve("custom")
                    .resolve("assets");

            Files.createDirectories(customDir.resolve("models"));
            Files.createDirectories(customDir.resolve("textures"));
            Files.createDirectories(customDir.resolve("animations"));

            DollRarityOverrides.initialize(serverDir);
            RarityWeightConfig.initialize(serverDir);
            CustomPackManager.buildAndCache(server);
        } catch (Exception e) {
            System.err.println("[Pokeblocks] Failed during server startup: " + e);
        }
    }
}