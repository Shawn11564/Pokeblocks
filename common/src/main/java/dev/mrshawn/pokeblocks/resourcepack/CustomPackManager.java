package dev.mrshawn.pokeblocks.resourcepack;

import net.minecraft.server.MinecraftServer;

import java.nio.file.Files;
import java.nio.file.Path;

public class CustomPackManager {
    private static Path cachedPack = null;
    private static String cachedSha = null;

    public static void buildAndCache(MinecraftServer server) {
        try {
            Path gameDir = server.getServerDirectory();
            Path pack = CustomPackBuilder.buildResourcePack(gameDir);
            if (pack != null && Files.exists(pack)) {
                cachedPack = pack;
                cachedSha = CustomPackBuilder.computeSHA1(pack);
                System.out.println("[Pokeblocks] Custom resource pack built and cached: " + pack + " sha1=" + cachedSha);
            } else {
                cachedPack = null;
                cachedSha = null;
                System.out.println("[Pokeblocks] No custom resources found, skipping pack build.");
            }
        } catch (Exception e) {
            cachedPack = null;
            cachedSha = null;
            System.err.println("[Pokeblocks] Failed to build custom resource pack: " + e);
        }
    }

    public static Path getCachedPack() {
        return cachedPack;
    }

    public static String getCachedSha() {
        return cachedSha;
    }

    public static boolean hasPack() {
        return cachedPack != null && Files.exists(cachedPack);
    }
}