package dev.mrshawn.pokeblocks.mctest;

import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import io.mctest.agent.core.McTestStateProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pokeblocks' implementation of the mc-test server-truth SPI ({@link McTestStateProvider}). It lets
 * mc-test's {@code truth.assertPluginState} step query <em>real, authoritative server-side state</em>
 * of the running mod — registry membership, the asset-scanned Pokémon registry, and the rarity
 * overrides loaded from {@code config/Pokeblocks/doll_rarity.json} — and assert against it.
 *
 * <p><b>Discovery.</b> The matching {@code agent-server-fabric}/{@code agent-server-neoforge} agent
 * resolves this class via {@link java.util.ServiceLoader} (it shares the exact {@code McTestStateProvider}
 * class because it bundles {@code mc-test-agent-core}). Pokeblocks must therefore depend on
 * {@code io.mctest:mc-test-agent-core} as <b>compileOnly</b> (never bundled — bundling would create a
 * second, incompatible copy of the interface and break ServiceLoader class identity), and ship
 * {@code META-INF/services/io.mctest.agent.core.McTestStateProvider} naming this class. When no agent
 * is present (i.e. ordinary play), nothing ever loads this class — it is dormant.
 *
 * <p><b>Threading.</b> The agent invokes {@link #query} on the server thread, so reads of the
 * (post-init, effectively read-only) static registries here are safe. This provider only READS state —
 * it never mutates the world or game — keeping the agent "dumb" per the mc-test Prime Directives.
 *
 * <p><b>Pure values.</b> Every query returns a primitive (boolean / int / String); the agent applies the
 * {@code expect} predicate and the runner owns the verdict.
 */
public final class PokeblocksStateProvider implements McTestStateProvider {

    @Override
    public Object query(String query, Map<String, Object> args) throws Exception {
        return switch (query) {
            // Vanilla registry membership — populated at bootstrap, before the server starts.
            case "item.exists" -> registryContains(BuiltInRegistries.ITEM, requireStr(args, "id"));
            case "block.exists" -> registryContains(BuiltInRegistries.BLOCK, requireStr(args, "id"));
            case "entity.exists" -> registryContains(BuiltInRegistries.ENTITY_TYPE, requireStr(args, "id"));

            // The asset-scanned Pokémon registry (PokemonRegistry populates from the mod jar's
            // geo/texture/animation assets at class-load — present on a dedicated server too).
            case "pokemon.registered" -> PokemonRegistry.isRegistered(requireStr(args, "name"));
            case "pokemon.count" -> PokemonRegistry.ALL_POKEMON.size();

            // The rarity overrides loaded from config/Pokeblocks/doll_rarity.json (loaded on
            // SERVER_STARTING via PokeblocksServerLifecycle, before the agent answers any query).
            case "rarity.override" -> overrideRarity(requireStr(args, "pokemon"), parseFlags(args.get("flags")));

            // A flag's intrinsic rarity tier (pure enum logic — SHINY -> "Shiny", GIGANTIC -> "Gigantic").
            case "rarity.flagTier" -> flagTier(requireStr(args, "flag"));

            default -> throw new IllegalArgumentException("unknown Pokeblocks state query: '" + query + "'");
        };
    }

    /** True iff the registry contains the given {@code namespace:path} id (false for a malformed id). */
    private static boolean registryContains(Registry<?> registry, String id) {
        ResourceLocation rl = ResourceLocation.tryParse(id);
        return rl != null && registry.containsKey(rl);
    }

    /**
     * The effective rarity OVERRIDE for {@code (pokemon, flags)} as loaded from doll_rarity.json, by the
     * exact key the mod builds ({@link DollRarityOverrides#getOverride}). Returns the rarity's display
     * name (e.g. {@code "Legendary"}) or {@code "NONE"} when no override is configured for that key.
     */
    private static String overrideRarity(String pokemon, Set<ModelFlag> flags) {
        DollRarity override = DollRarityOverrides.getOverride(pokemon, flags);
        return override == null ? "NONE" : override.getDisplayName();
    }

    /** The display name of a flag's intrinsic rarity tier (via the canonical {@link ModelFlag} enum). */
    private static String flagTier(String flagTagName) {
        ModelFlag flag = ModelFlag.fromTagName(flagTagName);
        if (flag == null) {
            throw new IllegalArgumentException("unknown ModelFlag tag '" + flagTagName
                    + "' (valid: " + ModelFlag.allTagNames() + ")");
        }
        return flag.getRarity().getDisplayName();
    }

    /** Parses the optional {@code flags} arg (a JSON array of tag names, or a single tag name) into a set. */
    private static Set<ModelFlag> parseFlags(Object raw) {
        Set<ModelFlag> flags = EnumSet.noneOf(ModelFlag.class);
        if (raw == null) {
            return flags;
        }
        List<Object> tokens = new ArrayList<>();
        if (raw instanceof List<?> list) {
            tokens.addAll(list);
        } else {
            tokens.add(raw);
        }
        for (Object token : tokens) {
            if (token == null) {
                continue;
            }
            ModelFlag flag = ModelFlag.fromTagName(token.toString());
            if (flag != null) {
                flags.add(flag);
            }
        }
        return flags;
    }

    private static String requireStr(Map<String, Object> args, String key) {
        Object value = args == null ? null : args.get(key);
        if (value == null || value.toString().isEmpty()) {
            throw new IllegalArgumentException("missing required arg '" + key + "'");
        }
        return value.toString();
    }
}
