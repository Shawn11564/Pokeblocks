package dev.mrshawn.pokeblocks.data;

import com.mojang.serialization.Dynamic;
import dev.mrshawn.pokeblocks.PokeblocksCommon;
import dev.mrshawn.pokeblocks.constants.ModSettings;
import dev.mrshawn.pokeblocks.item.PokeblocksItemData;
import dev.mrshawn.pokeblocks.pokemon.ModelFlag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Parses a single legacy Pokeblocks id (e.g. {@code pokeblocks:gigantic_pokedoll_shiny_charmander})
 * into the new id it should become plus the minimal NBT that identifies its variant.
 * <p>
 * This is the single source of truth shared by every data fixer (the two {@code Preserve*} fixes that
 * inject NBT and the three rename fixes that rewrite ids), so a doll, figurine or decorative migrates
 * identically whether it is encountered as a placed block, a chunk block-entity, or an item stack.
 *
 * <h2>Output format</h2>
 * The {@link LegacyVariant#strings} + true-only {@link LegacyVariant#trueFlags} are exactly the
 * canonical, minimal tag that {@code PokeblocksItemData} writes for freshly-minted content: a content
 * key ({@code pokemon} for dolls, {@code figurine} for figurines, none for decoratives) and only the
 * flags that are {@code true}. Producing the same bytes is what lets a migrated item stack stack with a
 * {@code /give}-n one. The {@code id} key is intentionally <b>not</b> included — the rename fixes own the id.
 *
 * <h2>Legacy id grammar</h2>
 * <ul>
 *   <li><b>Dolls:</b> {@code [gigantic_]pokedoll_[shiny_][netherite_]<pokemon>[_posed|_animated|_zenith|_noice|_family]}
 *       → {@code pokeblocks:pokedoll}, {@code pokemon=<pokemon>}, flags from the tokens.
 *       {@code <pokemon>} may itself contain underscores (e.g. {@code skibidi_mewlet}, {@code washing_machine}).</li>
 *   <li><b>Figurines:</b> {@code <name>_figurine} → {@code pokeblocks:figurine}, {@code figurine=<name>}.</li>
 *   <li><b>Decoratives:</b> {@code [gigantic_][shiny_]<base>} where {@code <base>} is one of
 *       {@code applin_basket, eiscue_head_pile, luvdisc_cushion, magikarp_fishbowl, pokemon_trophy}
 *       → {@code pokeblocks:<base>}, flags from the prefixes.</li>
 * </ul>
 * The legacy {@code pokeblock_*} cube blocks and the misc items (poke coin, vouchers, …) need no entry
 * here: they were re-added natively under their original ids ({@code PokeBlockRegistry},
 * {@code ItemRegistry.MISC_ITEMS}), so their ids are unchanged and load directly — {@link #migrate}
 * returns empty for them and the rename fixes leave them alone.
 */
public final class LegacyIdMigrator {

    private LegacyIdMigrator() {}

    /** The new id (namespaced) plus the minimal variant NBT to merge. {@code strings}/{@code trueFlags} never include the {@code id} key. */
    public record LegacyVariant(String newId, Map<String, String> strings, List<String> trueFlags) {}

    private static final String DOLL_PREFIX = "pokedoll_";
    private static final String GIGANTIC_PREFIX = "gigantic_";
    private static final String SHINY_PREFIX = "shiny_";
    private static final String NETHERITE_PREFIX = "netherite_";
    private static final String FIGURINE_SUFFIX = "_figurine";

    /** Decorative base ids that keep their own block/block-entity/item id in the new mod. */
    private static final Set<String> DECORATIVE_IDS = Set.of(
            "applin_basket",
            "eiscue_head_pile",
            "luvdisc_cushion",
            "magikarp_fishbowl",
            "pokemon_trophy"
    );

    /** Trailing variant suffix → flag, in the order they are stripped (old ids carry at most one). */
    private static final Map<String, ModelFlag> SUFFIX_FLAGS = new LinkedHashMap<>();
    static {
        SUFFIX_FLAGS.put("_posed", ModelFlag.POSED);
        SUFFIX_FLAGS.put("_animated", ModelFlag.ANIMATED);
        SUFFIX_FLAGS.put("_zenith", ModelFlag.ZENITH);
        SUFFIX_FLAGS.put("_noice", ModelFlag.NOICE);
        SUFFIX_FLAGS.put("_family", ModelFlag.FAMILY);
    }

    /**
     * Maps a legacy id (with or without the {@code pokeblocks:} namespace) to its new id + variant NBT,
     * or {@link Optional#empty()} if the id is not a recognized legacy Pokeblocks id that needs migrating
     * (ids in other namespaces, and the natively re-added {@code pokeblock_*} / misc items whose ids are
     * unchanged, are left untouched).
     */
    public static Optional<LegacyVariant> migrate(String legacyId) {
        if (legacyId == null) return Optional.empty();

        String id = stripNamespace(legacyId);
        if (id == null) return Optional.empty();

        Optional<LegacyVariant> result = tryFigurine(id);
        if (result.isPresent()) return result;

        result = tryDoll(id);
        if (result.isPresent()) return result;

        return tryDecorative(id);
    }

    private static Optional<LegacyVariant> tryFigurine(String id) {
        if (!id.endsWith(FIGURINE_SUFFIX) || id.length() <= FIGURINE_SUFFIX.length()) {
            return Optional.empty();
        }
        String name = id.substring(0, id.length() - FIGURINE_SUFFIX.length());
        return Optional.of(new LegacyVariant(newId(ModSettings.FIGURINE_ID),
                Map.of(PokeblocksItemData.KEY_FIGURINE, name), List.of()));
    }

    private static Optional<LegacyVariant> tryDoll(String id) {
        String core = id;
        List<String> flags = new ArrayList<>();

        if (core.startsWith(GIGANTIC_PREFIX)) {
            flags.add(ModelFlag.GIGANTIC.getTagName());
            core = core.substring(GIGANTIC_PREFIX.length());
        }
        if (!core.startsWith(DOLL_PREFIX)) {
            return Optional.empty();
        }
        core = core.substring(DOLL_PREFIX.length());

        if (core.startsWith(SHINY_PREFIX)) {
            flags.add(ModelFlag.SHINY.getTagName());
            core = core.substring(SHINY_PREFIX.length());
        }
        if (core.startsWith(NETHERITE_PREFIX)) {
            flags.add(ModelFlag.NETHERITE.getTagName());
            core = core.substring(NETHERITE_PREFIX.length());
        }

        // Strip trailing variant suffixes (e.g. _posed, _animated). Loop for robustness even though
        // legacy ids only ever carry a single trailing suffix.
        boolean stripped = true;
        while (stripped) {
            stripped = false;
            for (Map.Entry<String, ModelFlag> entry : SUFFIX_FLAGS.entrySet()) {
                String suffix = entry.getKey();
                if (core.endsWith(suffix) && core.length() > suffix.length()) {
                    flags.add(entry.getValue().getTagName());
                    core = core.substring(0, core.length() - suffix.length());
                    stripped = true;
                    break;
                }
            }
        }

        if (core.isEmpty()) {
            return Optional.empty();
        }

        // The new mod only ships an animated "family" model (e.g. pokedoll_snorunt_family_animated.geo.json),
        // so a legacy family doll must also carry the animated flag or the resolver can't find its model and
        // falls back to the plain pokemon.
        if (flags.contains(ModelFlag.FAMILY.getTagName()) && !flags.contains(ModelFlag.ANIMATED.getTagName())) {
            flags.add(ModelFlag.ANIMATED.getTagName());
        }

        return Optional.of(new LegacyVariant(newId(ModSettings.DOLL_ID),
                Map.of(PokeblocksItemData.KEY_POKEMON, core), flags));
    }

    private static Optional<LegacyVariant> tryDecorative(String id) {
        String core = id;
        List<String> flags = new ArrayList<>();

        if (core.startsWith(GIGANTIC_PREFIX)) {
            flags.add(ModelFlag.GIGANTIC.getTagName());
            core = core.substring(GIGANTIC_PREFIX.length());
        }
        if (core.startsWith(SHINY_PREFIX)) {
            flags.add(ModelFlag.SHINY.getTagName());
            core = core.substring(SHINY_PREFIX.length());
        }

        if (DECORATIVE_IDS.contains(core)) {
            return Optional.of(new LegacyVariant(newId(core), Map.of(), flags));
        }
        return Optional.empty();
    }

    /** Strips the {@code pokeblocks:} namespace; returns {@code null} for any other namespace. */
    private static String stripNamespace(String id) {
        int colon = id.indexOf(':');
        if (colon < 0) return id;
        if (!id.substring(0, colon).equals(PokeblocksCommon.MOD_ID)) return null;
        return id.substring(colon + 1);
    }

    private static String newId(String localId) {
        return PokeblocksCommon.MOD_ID + ":" + localId;
    }

    /**
     * Merges a variant's content keys and true-only flags into {@code dynamic} (a block-entity compound
     * or a {@code block_entity_data} sub-compound). Does not touch the {@code id} key. Returns the updated
     * dynamic (the DFU {@link Dynamic} API is immutable).
     */
    public static <T> Dynamic<T> mergeVariant(Dynamic<T> dynamic, LegacyVariant variant) {
        for (Map.Entry<String, String> entry : variant.strings().entrySet()) {
            dynamic = dynamic.set(entry.getKey(), dynamic.createString(entry.getValue()));
        }
        for (String flag : variant.trueFlags()) {
            dynamic = dynamic.set(flag, dynamic.createBoolean(true));
        }
        return dynamic;
    }

    /** New id of the eiscue head pile decorative. */
    private static final String EISCUE_HEAD_PILE_ID = "eiscue_head_pile";
    /** Legacy head-pile count was a 0-based int {@code TextureIndex}; the new decorative uses the string key below. */
    private static final String LEGACY_HEAD_PILE_INDEX_KEY = "TextureIndex";
    /** The new {@code headCount} nbt-variant key (see {@code DecorativeRegistry.EISCUE_HEAD_PILE}); values "1"/"2"/"3", default "1". */
    private static final String HEAD_PILE_COUNT_KEY = "headCount";
    private static final int MAX_HEAD_PILE_COUNT = 3;

    /**
     * Migrates the eiscue head pile's pile size: the legacy block entity stored a 0-based
     * {@code TextureIndex} (0,1,2), the new decorative stores a {@code headCount} string ("1","2","3").
     * Reads {@code TextureIndex} off {@code dynamic} (a block-entity compound or item
     * {@code block_entity_data}), drops it, and writes {@code headCount} when it differs from the default.
     * A no-op for any other variant or when no index is present (legacy items never stored one).
     */
    public static <T> Dynamic<T> convertHeadPileCount(Dynamic<T> dynamic, LegacyVariant variant) {
        if (!variant.newId().equals(newId(EISCUE_HEAD_PILE_ID))) {
            return dynamic;
        }
        boolean hasIndex = dynamic.get(LEGACY_HEAD_PILE_INDEX_KEY).result().isPresent();
        int index = dynamic.get(LEGACY_HEAD_PILE_INDEX_KEY).asInt(0);
        dynamic = dynamic.remove(LEGACY_HEAD_PILE_INDEX_KEY);
        if (!hasIndex) {
            return dynamic;
        }
        int headCount = Math.max(1, Math.min(index + 1, MAX_HEAD_PILE_COUNT));
        if (headCount <= 1) {
            return dynamic; // default — omit to keep the tag minimal (matches PokeblocksItemData)
        }
        return dynamic.set(HEAD_PILE_COUNT_KEY, dynamic.createString(Integer.toString(headCount)));
    }
}
