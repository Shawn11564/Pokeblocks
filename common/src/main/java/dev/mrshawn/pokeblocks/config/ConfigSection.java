package dev.mrshawn.pokeblocks.config;

import java.util.List;

/**
 * A {@code config.toml} section contributed by an add-on (see
 * {@link dev.mrshawn.pokeblocks.addon.PokeblocksAddon#configSections}). {@link PokeblocksConfig} writes
 * and patches its keys like its own, and on every reload calls {@link #reset} and then {@link #set} for
 * each key found in the section.
 */
public interface ConfigSection {

	/** One key: its comment lines (each starting with {@code #}) and the default written for it. */
	record Key(String key, String comment, String defaultValue) {}

	/** The section name, without brackets. */
	String category();

	/** The section's keys, in the order they are written. */
	List<Key> keys();

	/** Restores every value to its default (before a reload reads the file). */
	void reset();

	/**
	 * Applies one {@code key = value} line from the section. The key is lower-cased and the value
	 * trimmed with any trailing comment removed; parse it with the {@code PokeblocksConfig.parse*} helpers.
	 */
	void set(String key, String value);
}
