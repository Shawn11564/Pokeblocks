package dev.mrshawn.pokeblocks.resourcepack.resources;

import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.PokeblocksLog;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PokedollsInMemoryPack implements PackResources {

    private static final String PACK_ID = "pokeblocks_generated";
    /**
     * The client-resources pack format for the running game version (34 on 1.21.1), read from the game
     * itself rather than hardcoded so the generated pack is never flagged as made for another version and
     * stays correct across Minecraft updates. {@link net.minecraft.DetectedVersion} is the source of truth.
     */
    private static final int PACK_FORMAT =
            SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES);
    private final Map<String, byte[]> resources = new HashMap<>();
    private final PackLocationInfo locationInfo;

    public PokedollsInMemoryPack() {
        this.locationInfo = new PackLocationInfo(
                PACK_ID,
                Component.literal("Pokeblocks Generated Resources"),
                PackSource.BUILT_IN,
                Optional.empty()
        );

        Map<String, String> generated = GeneratedResourcePack.generateAll();
        for (Map.Entry<String, String> entry : generated.entrySet()) {
            resources.put(entry.getKey(), entry.getValue().getBytes(StandardCharsets.UTF_8));
        }

        PokeblocksLog.LOGGER.debug("Generated {} resource files", generated.size());
    }

    @Override
    public PackLocationInfo location() {
        return locationInfo;
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... paths) {
        return null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type != PackType.CLIENT_RESOURCES) return null;
        String path = "assets/" + location.getNamespace() + "/" + location.getPath();
        byte[] data = resources.get(path);
        if (data != null) {
            return () -> new ByteArrayInputStream(data);
        }
        return null;
    }

    @Override
    public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
        if (type != PackType.CLIENT_RESOURCES) return;
        String prefix = "assets/" + namespace + "/" + path;
        for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                String remaining = key.substring(("assets/" + namespace + "/").length());
                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(namespace, remaining);
                output.accept(loc, () -> new ByteArrayInputStream(entry.getValue()));
            }
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        if (type == PackType.CLIENT_RESOURCES) {
            return Set.of(PokeblocksCommon.MOD_ID);
        }
        return Set.of();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
        if (serializer == PackMetadataSection.TYPE) {
            return (T) new PackMetadataSection(
                    Component.literal("Pokeblocks auto-generated block models and states"),
                    PACK_FORMAT,
                    Optional.empty()
            );
        }
        return null;
    }

    @Override
    public String packId() {
        return PACK_ID;
    }

    @Override
    public void close() {}

    public static Pack createPack() {
        final PokedollsInMemoryPack instance = new PokedollsInMemoryPack();

        return Pack.readMetaAndCreate(
                new PackLocationInfo(
                        PACK_ID,
                        Component.literal("Pokeblocks Generated Resources"),
                        PackSource.BUILT_IN,
                        Optional.empty()
                ),
                new Pack.ResourcesSupplier() {
                    @Override
                    public PackResources openPrimary(PackLocationInfo info) {
                        return instance;
                    }

                    @Override
                    public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                        return instance;
                    }
                },
                PackType.CLIENT_RESOURCES,
                new PackSelectionConfig(true, Pack.Position.TOP, false)
        );
    }
}