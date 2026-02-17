package dev.mrshawn.pokeblocks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.mrshawn.pokeblocks.pokemon.PokemonRegistry;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackBuilder;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.resourcepack.ResourcePackServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.server.MinecraftServer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RebuildPackCMD {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("pokeblocks-pack-rebuild").requires(src -> src.hasPermission(2))
                .executes(RebuildPackCMD::run));
    }

    private static int run(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        MinecraftServer server = src.getServer();
        Path gameDir = server.getServerDirectory();

        List<Path> candidates = List.of(
                gameDir.resolve("config").resolve("Pokeblocks").resolve("pokeblocks").resolve("custom"),
                gameDir.resolve("config").resolve("pokeblocks").resolve("custom"),
                gameDir.resolve("pokeblocks").resolve("custom"),
                gameDir.resolve("config").resolve("Pokeblocks").resolve("custom")
        );

        try {
            for (Path p : candidates) {
                src.sendSuccess(() -> Component.literal((Files.exists(p) ? "[FOUND] " : "[MISS]  ") + p), false);
            }

            Path customDir = CustomPackBuilder.findCustomDir(gameDir);
            if (customDir == null) {
                src.sendFailure(Component.literal("No pokeblocks/custom directory found among candidates."));
                return 0;
            }

            try (var stream = Files.list(customDir)) {
                List<String> children = stream.map(Path::getFileName).map(Object::toString).collect(Collectors.toList());
                src.sendSuccess(() -> Component.literal("Using custom dir: " + customDir + " (contains: " + String.join(", ", children) + ")"), false);
            }

            // Reload custom dolls into the registry
            PokemonRegistry.loadCustomFrom(gameDir);

            // Rebuild and cache the pack
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

            UUID uuid = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
            ClientboundResourcePackPushPacket pkt = new ClientboundResourcePackPushPacket(uuid, url, sha, true, Optional.empty());
            server.getConnection().getConnections().forEach(conn -> conn.send(pkt));

            return 1;
        } catch (Exception e) {
            src.sendFailure(Component.literal("Failed to build resource pack: " + e.getMessage()));
            return 0;
        }
    }
}
