package dev.mrshawn.pokeblocks.resourcepack;

import java.nio.file.Path;
import java.util.Set;

public record PackBuildResult(Path zipFile, Set<String> modelFiles, Set<String> textureFiles) {}
