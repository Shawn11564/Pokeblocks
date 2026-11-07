package dev.mrshawn.pokeblocks.addon;

import dev.mrshawn.pokeblocks.block.entity.custom.PokedollBlockEntity;
import dev.mrshawn.pokeblocks.config.ConfigSection;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * A feature compiled into the mod from a local add-on folder (see {@code gradle/addons.gradle}) instead
 * of from this repository. Add-ons are found with {@link java.util.ServiceLoader}: list the class in
 * {@code META-INF/services/dev.mrshawn.pokeblocks.addon.PokeblocksAddon}.
 * <p>
 * Every hook is a no-op by default, so a build without add-ons behaves exactly as if these hooks did
 * not exist. Client-only hooks live in {@link dev.mrshawn.pokeblocks.client.addon.PokeblocksClientAddon};
 * loader-specific ones (networking) in each loader's own add-on interface.
 */
public interface PokeblocksAddon {

	/**
	 * Called at the end of {@code PokeblocksCommon.doRegistrations()}: register entity types, sounds
	 * and the like through {@code PokeblocksCommon.COMMON_PLATFORM} here.
	 */
	default void register() {}

	/** Extra {@code config.toml} sections, written after the mod's own and reloaded with them. */
	default List<ConfigSection> configSections() {
		return List.of();
	}

	/**
	 * Server side: a player right-clicked a placed pokedoll with an empty hand. Runs before the
	 * squeak / pop handling; return a result to consume the click, or {@code null} to let it through.
	 */
	@Nullable
	default InteractionResult useDoll(ServerLevel level, BlockPos pos, BlockState state, PokedollBlockEntity doll, Player player) {
		return null;
	}

	/**
	 * Server side: a player popped a placed pokedoll by spam-clicking it. Runs after the pop's own drops
	 * (or a trapped doll's blast), so anything dropped here survives; the block is already gone.
	 */
	default void onDollPopped(ServerLevel level, BlockPos pos, String pokemon, Player player) {}

	/**
	 * Server side: a pokedoll was placed from {@code stack}, its block entity already filled in.
	 * {@code placer} is {@code null} for placements nobody made (e.g. a thrown doll landing).
	 */
	default void onDollPlaced(ServerLevel level, BlockPos pos, PokedollBlockEntity doll, @Nullable LivingEntity placer, ItemStack stack) {}

	/**
	 * Whether a doll variant must never come from a random source (loot tables, the phone's buried
	 * dolls), on top of the config's own exclusions.
	 */
	default boolean excludesFromLoot(String pokemon, Set<ModelFlag> flags) {
		return false;
	}

	/** Extra tooltip lines for a pokedoll item, added after its throwable/trapped lines and before its rarity. */
	default void appendDollTooltip(String pokemon, ItemStack stack, List<Component> tooltip) {}

	/**
	 * Lines that replace a pokedoll tooltip's rarity tier and rarity score (the blank line before them
	 * stays), or {@code null} to keep the usual ones. Client side, rebuilt every frame the tooltip shows.
	 */
	@Nullable
	default List<Component> dollRarityLines(String pokemon, ItemStack stack) {
		return null;
	}

	/**
	 * How a species' name is spelled in doll item names, or {@code null} to derive it from the id
	 * ({@code mr_mime} becomes "Mr Mime").
	 */
	@Nullable
	default String dollSpeciesName(String pokemon) {
		return null;
	}

	/** A server is about to start, before its config is (re)loaded. */
	default void onServerAboutToStart(MinecraftServer server) {}

	/** A player finished joining a server (after the mod's own join-time syncs). */
	default void onPlayerJoin(ServerPlayer player) {}

	/** End of every server tick. */
	default void onServerTick(MinecraftServer server) {}
}
