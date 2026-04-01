package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.FigurineNameOverrides;
import dev.mrshawn.pokeblocks.item.RarityWeightConfig;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;

public final class PokeblocksServerLifecycle {

    public static void onServerAboutToStart(MinecraftServer server) {
//        PokeblocksConfig.initialize(server.getServerDirectory());
//        PokeblocksCommon.invalidateLootMap();
    }

    public static void onServerStarted(MinecraftServer server) {
        try {
            Path serverDir = server.getServerDirectory();
            PokeblocksConfig.initialize(serverDir);

            Path customDir = serverDir.resolve("config")
                    .resolve("Pokeblocks")
                    .resolve("resourcepack")
                    .resolve("custom")
                    .resolve("assets");

            Files.createDirectories(customDir.resolve("models"));
            Files.createDirectories(customDir.resolve("textures"));
            Files.createDirectories(customDir.resolve("animations"));

            DollRarityOverrides.initialize(serverDir);
            FigurineNameOverrides.initialize(serverDir);
            RarityWeightConfig.initialize(serverDir);
            CustomPackManager.buildAndCache(server);
        } catch (Exception e) {
            System.err.println("[Pokeblocks] Failed during server startup: " + e);
        }
    }
}