package dev.mrshawn.pokeblocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared SLF4J logger for the mod, kept in a side-effect-free holder.
 * <p>
 * {@link PokeblocksCommon} also exposes a logger, but its static initializer performs a
 * platform {@link java.util.ServiceLoader} lookup that throws when no loader-specific
 * implementation is present (e.g. in headless unit tests). Config loaders and other
 * low-level utilities log through this holder instead, so that simply emitting a log line
 * never forces platform initialization. Both loggers share the same {@code "pokeblocks"}
 * name and therefore the same underlying logger instance.
 */
public final class PokeblocksLog {

    public static final Logger LOGGER = LoggerFactory.getLogger("pokeblocks");

    private PokeblocksLog() {}
}
