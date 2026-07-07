package dev.mrshawn.pokeblocks.mctest;

import dev.mrshawn.pokeblocks.config.ConfigSync;
import dev.mrshawn.pokeblocks.config.PokeblocksConfig;
import dev.mrshawn.pokeblocks.item.DollRarity;
import dev.mrshawn.pokeblocks.item.DollRarityAcquisitionDivisors;
import dev.mrshawn.pokeblocks.item.DollRarityIgnoredFlags;
import dev.mrshawn.pokeblocks.item.DollRarityOverrides;
import dev.mrshawn.pokeblocks.item.FigurineDescriptionOverrides;
import dev.mrshawn.pokeblocks.item.FigurineNameOverrides;
import dev.mrshawn.pokeblocks.item.FigurineTagOverrides;
import dev.mrshawn.pokeblocks.item.RarityScoreCalculator;
import dev.mrshawn.pokeblocks.item.custom.PokedollItem;
import dev.mrshawn.pokeblocks.item.loot.LootGroup;
import dev.mrshawn.pokeblocks.item.loot.LootGroupConfig;
import dev.mrshawn.pokeblocks.item.loot.LootInjector;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;
import dev.mrshawn.pokeblocks.recipe.PokeblocksIngredient;
import dev.mrshawn.pokeblocks.registry.PokemonRegistry;
import dev.mrshawn.pokeblocks.resourcepack.CustomPackManager;
import dev.mrshawn.pokeblocks.shape.DollShapes;
import io.mctest.agent.core.McTestStateProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

/**
 * Pokeblocks' implementation of the mc-test server-truth SPI ({@link McTestStateProvider}). It lets
 * mc-test's {@code truth.assertPluginState} step query <em>real, authoritative server-side state</em>
 * of the running mod and assert against it: registry membership (items/blocks/entities/recipe
 * serializers), the asset-scanned Pokémon registry, the full rarity engine (override resolution, the
 * effective-weight "score", tier weights, acquisition divisors and ignored flags), loot routing
 * (group assignment, table targeting, exclusions), the doll recipe ingredient matcher, the loaded
 * config defaults, and the figurine name/tag overrides — all loaded server-side by
 * {@link dev.mrshawn.pokeblocks.PokeblocksServerLifecycle} before the agent answers any query.
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

            // ── Rarity engine: the full resolution + score pipeline (deterministic, registry-independent) ──
            // The resolved rarity TIER for a variant (override -> ignored-flag strip -> highest flag -> COMMON).
            // A superset of rarity.override: it exercises the whole resolution chain, not just the raw map.
            case "rarity.resolved" -> RarityScoreCalculator
                    .resolvedRarity(requireStr(args, "pokemon"), parseFlags(args.get("flags"))).getDisplayName();

            // The effective loot weight ("rarity score") of a variant, scaled ×1000 and rounded so the exact
            // fractional value (e.g. a gigantic substitute's 0.625) is assertable as an integer. Folds in the
            // tier weight, per-extra-flag ÷4, the acquisition divisor, and gigantic's ÷4 base recursion.
            case "rarity.weightX1000" -> scaleX1000(RarityScoreCalculator
                    .effectiveWeight(requireStr(args, "pokemon"), parseFlags(args.get("flags"))));

            // A rarity tier's configured weight from rarity_weights.json (via the live DollRarity.getWeight()).
            case "rarity.tierWeight" -> requireRarity(args, "rarity").getWeight();

            // The per-variant acquisition divisor (extra obtain-mechanic penalty) from the divisors config; default 1.
            case "rarity.acquisitionDivisor" -> DollRarityAcquisitionDivisors
                    .getAcquisitionDivisor(requireStr(args, "pokemon"), parseFlags(args.get("flags")));

            // Whether a flag is ignored for rarity for a given pokemon (e.g. eiscue ignores "noice").
            case "rarity.flagIgnored" -> DollRarityIgnoredFlags
                    .getIgnoredFlags(requireStr(args, "pokemon")).contains(requireFlag(args, "flag"));

            // Whether a flag set has a mutual-exclusion conflict (e.g. male+female share the "gender" group).
            case "flag.exclusionConflict" -> ModelFlag.hasExclusionConflict(parseFlags(args.get("flags")));

            // ── Loot routing: which dolls go to which pools/tables, and what is excluded from loot ──
            // The loot group a doll variant is routed to (first match wins), or "NONE" for the default pool.
            case "loot.groupForDoll" -> {
                LootGroup group = LootGroupConfig.groupForDoll(requireStr(args, "pokemon"), parseFlags(args.get("flags")));
                yield group == null ? "NONE" : group.name();
            }
            // Whether the default global pool targets a loot table (exact id or wildcard from [loot] loot_tables).
            case "loot.targetsTable" -> LootInjector.matchesDefaultTables(requireRl(args, "id"));
            // Whether a named loot group injects into the given loot table.
            case "loot.groupMatchesTable" -> findGroup(requireStr(args, "group")).matchesTable(requireRl(args, "id"));
            // A loot group's resolved drop chance, scaled ×1000 (e.g. 0.03 -> 30).
            case "loot.groupDropChanceX1000" -> scaleX1000(findGroup(requireStr(args, "group")).resolveDropChance());
            // The number of compiled loot groups (tiers count individually).
            case "loot.groupCount" -> LootGroupConfig.getGroups().size();
            // Whether a doll variant is barred from all loot ([loot] excluded_dolls).
            case "loot.dollExcluded" -> PokeblocksConfig
                    .isDollExcludedFromLoot(requireStr(args, "pokemon"), parseFlags(args.get("flags")));
            // Whether a flag bars a variant from loot ([loot] excluded_flags).
            case "loot.flagExcluded" -> PokeblocksConfig.getExcludedLootFlags().contains(requireFlag(args, "flag"));

            // ── Geo-derived hitboxes: the server-side shape pipeline really resolves bundled .geo.json
            // files and compiles a single best-fit VoxelShape box from them (DollShapes).
            // The box count of the compiled shape (a single best-fit box by design, so 1 for a real doll).
            case "shape.pokedollBoxes" -> DollShapes
                    .pokedollVariant(requireStr(args, "pokemon"), parseFlags(args.get("flags")), optInt(args, "segment", 0))
                    .toAabbs().size();
            // Whether the compiled shape is the generic fallback box rather than a geo-derived one —
            // the real discriminator now that both real and fallback shapes are a single box.
            case "shape.pokedollIsDefaultBox" -> DollShapes.isFallbackShape(DollShapes
                    .pokedollVariant(requireStr(args, "pokemon"), parseFlags(args.get("flags")), optInt(args, "segment", 0)));
            // The shape's max Y ×1000 (int), e.g. to assert a gigantic doll's hitbox really is scaled up.
            case "shape.pokedollMaxYx1000" -> scaleX1000(DollShapes
                    .pokedollVariant(requireStr(args, "pokemon"), parseFlags(args.get("flags")), optInt(args, "segment", 0))
                    .max(Direction.Axis.Y));

            // ── Recipes ──
            // Whether a recipe serializer id is registered (pokeblocks:crafting_shaped / pokeblocks:gigantic_doll).
            case "recipe.serializerExists" -> registryContains(BuiltInRegistries.RECIPE_SERIALIZER, requireStr(args, "id"));
            // Whether a Pokeblocks doll ingredient accepts a doll stack — EXACT species + flag-set match, the
            // whole reason PokeblocksIngredient exists (an "Applin" ingredient must reject a "Shiny Applin").
            case "recipe.dollIngredientMatches" -> {
                PokeblocksIngredient ingredient = PokeblocksIngredient.doll(
                        requireStr(args, "ingredientPokemon"), parseFlags(args.get("ingredientFlags")));
                ItemStack stack = PokedollItem.createPokedoll(
                        requireStr(args, "stackPokemon"), parseFlags(args.get("stackFlags")).toArray(new ModelFlag[0]));
                yield ingredient.test(stack);
            }

            // ── Served resource pack: built at server pre-start (CustomPackManager.buildAndCache) ──
            // Whether the served pack zip was built and still exists on disk. A clean server builds one
            // even with no admin custom assets, because include_builtin_assets defaults to true.
            case "resourcepack.built" -> CustomPackManager.hasPack();
            // The number of zip entries under a path prefix in the built pack (0 when no pack was built).
            // Entries under assets/pokeblocks/geo/block/ prove the built-in doll assets were bundled —
            // the version-skew guarantee that outdated clients still receive newly added dolls.
            case "resourcepack.entriesUnder" -> countPackEntriesUnder(requireStr(args, "prefix"));

            // ── Config truth (the bundled defaults a clean server boots with) ──
            case "config.dollPopping" -> PokeblocksConfig.isDollPoppingEnabled();
            case "config.lootDropChanceX1000" -> scaleX1000(PokeblocksConfig.getLootDropChance());
            case "config.updateMode" -> PokeblocksConfig.getConfigUpdateMode().name();
            // Whether a managed config file is written to config/Pokeblocks/ (hidden files still load defaults).
            case "config.fileShown" -> ConfigSync.isFileShown(requireStr(args, "file"));

            // ── Figurine overrides (figurine_names.json / figurine_descriptions.json / figurine_tags.json) ──
            case "figurine.nameOverride" -> {
                String name = FigurineNameOverrides.getOverride(requireStr(args, "id"));
                yield name == null ? "NONE" : name;
            }
            case "figurine.descriptionOverride" -> {
                String description = FigurineDescriptionOverrides.getOverride(requireStr(args, "id"));
                yield description == null ? "NONE" : description;
            }
            case "figurine.hasTag" -> FigurineTagOverrides.hasTag(requireStr(args, "id"), requireStr(args, "tag"));

            default -> throw new IllegalArgumentException("unknown Pokeblocks state query: '" + query + "'");
        };
    }

    /** True iff the registry contains the given {@code namespace:path} id (false for a malformed id). */
    private static boolean registryContains(Registry<?> registry, String id) {
        ResourceLocation rl = ResourceLocation.tryParse(id);
        return rl != null && registry.containsKey(rl);
    }

    /** Counts non-directory entries under {@code prefix} in the built served pack (0 when no pack). */
    private static int countPackEntriesUnder(String prefix) throws Exception {
        if (!CustomPackManager.hasPack()) {
            return 0;
        }
        try (ZipFile zip = new ZipFile(CustomPackManager.getCachedPack().toFile())) {
            return (int) zip.stream().filter(e -> !e.isDirectory() && e.getName().startsWith(prefix)).count();
        }
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

    /**
     * Scales a fractional value (weight, drop chance) by 1000 and rounds to an int, so exact
     * non-integer values are assertable without depending on the runner's float comparison.
     */
    private static int scaleX1000(double value) {
        return (int) Math.round(value * 1000.0);
    }

    /** Resolves a rarity tier by name (case-insensitive), e.g. {@code "common"} -> {@link DollRarity#COMMON}. */
    private static DollRarity requireRarity(Map<String, Object> args, String key) {
        String name = requireStr(args, key);
        try {
            return DollRarity.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("unknown rarity '" + name + "'");
        }
    }

    /** Resolves a single {@link ModelFlag} by tag name, or throws if it is not a valid flag. */
    private static ModelFlag requireFlag(Map<String, Object> args, String key) {
        String tag = requireStr(args, key);
        ModelFlag flag = ModelFlag.fromTagName(tag);
        if (flag == null) {
            throw new IllegalArgumentException("unknown ModelFlag tag '" + tag
                    + "' (valid: " + ModelFlag.allTagNames() + ")");
        }
        return flag;
    }

    /** Parses a {@code namespace:path} arg into a {@link ResourceLocation}, or throws if malformed. */
    private static ResourceLocation requireRl(Map<String, Object> args, String key) {
        ResourceLocation rl = ResourceLocation.tryParse(requireStr(args, key));
        if (rl == null) {
            throw new IllegalArgumentException("malformed resource id for arg '" + key + "'");
        }
        return rl;
    }

    /** Finds a compiled loot group by its (cache) name, or throws naming the groups that do exist. */
    private static LootGroup findGroup(String name) {
        for (LootGroup group : LootGroupConfig.getGroups()) {
            if (group.name().equals(name)) {
                return group;
            }
        }
        List<String> names = new ArrayList<>();
        for (LootGroup group : LootGroupConfig.getGroups()) {
            names.add(group.name());
        }
        throw new IllegalArgumentException("unknown loot group '" + name + "' (have: " + names + ")");
    }

    /** Reads an optional integer arg (YAML numbers may arrive as any {@link Number}, or a string). */
    private static int optInt(Map<String, Object> args, String key, int fallback) {
        Object value = args == null ? null : args.get(key);
        if (value == null) return fallback;
        if (value instanceof Number number) return number.intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("arg '" + key + "' is not an integer: '" + value + "'");
        }
    }

    private static String requireStr(Map<String, Object> args, String key) {
        Object value = args == null ? null : args.get(key);
        if (value == null || value.toString().isEmpty()) {
            throw new IllegalArgumentException("missing required arg '" + key + "'");
        }
        return value.toString();
    }
}
