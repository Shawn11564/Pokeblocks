package dev.mrshawn.pokeblocks;

import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.FigurineNameOverrides;
import dev.mrshawn.pokeblocks.item.FigurineTagOverrides;
import dev.mrshawn.pokeblocks.item.RarityWeightConfig;
import dev.mrshawn.pokeblocks.item.loot.LootGroupConfig;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;

public final class PokeblocksServerLifecycle {

    public static void onServerAboutToStart(MinecraftServer server) {
        try {
            Path serverDir = server.getServerDirectory();

            // Clear any loot pool cached from a previous server session so it
            // gets rebuilt fresh below with the correct set of pokemon.
            PokeblocksCommon.invalidateLootMap();

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
            FigurineTagOverrides.initialize(serverDir);
            RarityWeightConfig.initialize(serverDir);
            LootGroupConfig.initialize(serverDir);

            // MUST run before resource loading so that custom-pack pokemon
            // (e.g. "alien") are in PokemonRegistry when LootTableLoadEvent fires.
            CustomPackManager.buildAndCache(server);
        } catch (Exception e) {
            System.err.println("[Pokeblocks] Failed during server pre-start: " + e);
        }
    }

    public static void onServerStarted(MinecraftServer server) {
    }
}