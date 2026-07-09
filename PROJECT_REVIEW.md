# Pokeblocks Multiloader — Project Review (Card #55)

## Executive summary

The multiloader rewrite is structurally sound and broadly faithful to pre-rewrite behavior, but it carries two recurring themes worth addressing before the port is declared complete. First, **loader parity**: the data-migration subsystem that converts legacy worlds exists only on Fabric, so a player upgrading a pre-rewrite save on Forge or NeoForge gets no migration at all — the single high-impact behavioral divergence in the codebase. Second, **copy-paste duplication**: the figurine feature set (added in commit `904777a`) was introduced largely by cloning the doll feature set — five near-byte-identical compendium classes, paired block-entities/renderers, and seven config loaders that all repeat the same pack-override plumbing. Beyond those themes there is one genuine data-asset bug (a duplicate `pokemon_trophy` rarity key silently flips an intended-excluded item into the legendary loot pool) and a cluster of low-severity nits (a latent `/0.5f` base-scale landmine in the item renderers, a non-volatile static cache raced across threads, dead code, and a couple of incomplete/malformed asset entries). No crashes, security issues, or correctness regressions block release; the rewrite is healthy, and the work remaining is parity completion plus DRY consolidation.

## Summary by severity and category

| Severity | Count |
| --- | --- |
| High | 3 |
| Medium | 3 |
| Low | 13 |
| **Total** | **19** |

| Category | Count |
| --- | --- |
| reuse | 7 |
| multiloader-duplication | 3 |
| data/assets | 2 |
| regression-vs-prerewrite | 2 |
| correctness | 2 |
| structure | 2 |
| thread-safety | 1 |

---

## Multiloader duplication / loader parity

### [HIGH] DataFixers / world migration is Fabric-only — Forge & NeoForge get no save migration
- **File:** [`fabric/src/main/java/dev/mrshawn/pokeblocks/PokeblocksFabric.java:24`](fabric/src/main/java/dev/mrshawn/pokeblocks/PokeblocksFabric.java)
- **What's wrong:** The entire data-migration subsystem lives only in the fabric module and is wired only from `PokeblocksFabric.onInitialize()` via `PokeblocksDataFixers.register()`. It migrates legacy worlds: legacy block-entity/item/block id renames, derived-variant NBT preservation, and the pokedoll `facing`→16-step `rotation` conversion (`fabric/.../data/PokeblocksDataFixers.java:36-74`, schemas V1/V2/V3, fixes `PreserveLegacyBlockEntitiesFix` / `PreserveLegacyItemsFix` / `PokedollFacingToRotationFix`). `PokeblocksForge.java:38-55` and `PokeblocksNeoForge.java:37-52` never call any equivalent — there is no `data` package and no datafixer registration in those modules. `PokeblocksCommon.DATA_FIXER_VERSION = 3` (`PokeblocksCommon.java:29`) is referenced by all loaders but only Fabric acts on it (read via the Fabric-only `MixinChunkStorage.java:25`). A player who upgrades a world that used the pre-rewrite (legacy-id) mod on Forge or NeoForge gets **no migration** — legacy block entities/items/blocks and old pokedoll facing are left unconverted (broken/disappearing content). The mechanism is genuinely loader-specific: the registration path (`data/datafixerapi/*`) is backed by the Fabric-only mixins `MixinChunkStorage` / `MixinDataFixTypes` / `NbtUtilsMixin` because Minecraft exposes no public mod-datafixer hook, so each Forge-family loader needs a parallel implementation rather than a simple move into common.
- **Recommended fix:** Either (a) implement equivalent datafixer registration for Forge (its own SPI mechanism) and NeoForge, gated behind the same `DATA_FIXER_VERSION`, so all three loaders migrate legacy saves identically; or (b) if Forge/NeoForge are not yet expected to support legacy-world upgrades, document this explicitly and emit a startup warning on those loaders so the gap is intentional and visible. Track the loader-neutral pieces (`LegacyIdMigrator`, schemas, fixes) so they can move to common once a platform abstraction for datafixer registration exists.

### [MEDIUM] `reload()` pack-override scaffolding duplicated across 7 config-loader classes
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/item/FigurineNameOverrides.java:34-54`](common/src/main/java/dev/mrshawn/pokeblocks/item/FigurineNameOverrides.java)
- **What's wrong:** `DollRarityOverrides`, `DollRarityIgnoredFlags`, `DollRarityAcquisitionDivisors`, `FigurineNameOverrides`, `FigurineDescriptionOverrides`, `FigurineTagOverrides` and `RarityWeightConfig` all repeat the same `reload()` skeleton verbatim: clear the map, `PokeblocksConfigFiles.readConfigContent(configPath, FILE)`, log "No/Loaded …", `applyContent(content)`, then `PokeblocksConfigFiles.collectPackOverrides(parent, FILE)`, loop `applyContent` over each override, and a final "Applied N pack override(s)" debug log. Only the per-class `applyContent` body and the "loaded" message differ. `PokeblocksConfigFiles` was created to centralize this plumbing but the reload/pack-override loop was left copy-pasted.
- **Recommended fix:** Add a shared helper `static void loadWithPackOverrides(Path configPath, String fileName, Consumer<String> applyContent)` to `PokeblocksConfigFiles` that runs read → apply → collectPackOverrides → apply-each (and standard logs). Each `reload()` becomes `map.clear(); PokeblocksConfigFiles.loadWithPackOverrides(configPath, FILE, X::applyContent);`. Note `RarityWeightConfig` needs a base-vs-override asymmetry (`loadDefaults` + distinct `applyOverride`) and `DollRarityIgnoredFlags` clears two maps, so those two refactor less cleanly and would need slight generalization.

### [LOW] Forge `mods.toml` omits the `[[mixins]]` block that NeoForge declares
- **File:** [`forge/src/main/resources/META-INF/mods.toml:1-32`](forge/src/main/resources/META-INF/mods.toml)
- **What's wrong:** NeoForge declares the common mixin config explicitly (`neoforge.mods.toml:5-6`, `[[mixins]] config = "pokeblocks.mixins.json"`). Forge's `mods.toml` has no `[[mixins]]` block; the config is instead picked up via the jar manifest attribute in `forge/build.gradle:17` (`MixinConfigs`) plus the spongepowered mixin gradle plugin, with common's resources bundled via `processResources`. The common mixins **do** load on Forge — this is not a functional bug — but the two Forge-family loaders declare the same config through two different mechanisms, which is a maintenance hazard (remove the manifest attribute and Forge silently loses all common mixins with no `mods.toml` trace).
- **Recommended fix:** Add a `[[mixins]] config = "pokeblocks.mixins.json"` block to `forge/src/main/resources/META-INF/mods.toml` to match `neoforge.mods.toml`, so both Forge-family loaders declare the config the same way.

---

## Reuse / duplication

### [HIGH] ✅ RESOLVED — Figurine compendium screen package is a near-verbatim copy of the doll compendium package
> **Resolved** during compendium finalization: the five `FigurineCompendium*` classes were deleted and both
> collections now share one `CompendiumScreen`/`CompendiumDetailScreen`/`CompendiumRender`/`CompendiumCollection`
> stack parameterized by the `CompendiumType` enum (DOLLS / FIGURINES).
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/client/screen/FigurineCompendiumScreen.java:22-188`](common/src/main/java/dev/mrshawn/pokeblocks/client/screen/FigurineCompendiumScreen.java)
- **What's wrong:** `FigurineCompendiumScreen`, `FigurineCompendiumDetailScreen`, `FigurineCompendiumRender`, `FigurineCompendiumCollection` and `FigurineCompendiumClientHooks` are wholesale copies of the doll `Compendium*` package (added as a copy in commit `904777a`). `FigurineCompendiumScreen` vs `CompendiumScreen` are ~95% identical: same `COLUMNS`/`ROWS`/`PER_PAGE`/`CELL`/`ITEM_SCALE`/`CELL_BG`/`CELL_BG_HOVER`/`CELL_BORDER` constants, identical `init()` grid+nav layout, identical `render()` loop, identical `mouseClicked()`/`cellIndexAt()`/`changePage()`/`updateNavState()`/`pageCount()`, identical `renderBlurredBackground()`/`isPauseScreen()`. The two `*Render` helpers (`renderDoll` vs `renderFigurine`) are byte-identical except which renderer's `SILHOUETTE` flag they toggle; the two `*Collection` classes differ only by the `instanceof` type and the key-extractor call. Real differences are only the backing list (`PokedollItem` species vs `FigurineItem` ids), the silhouette helper, and the detail screen's body text (figurine adds a genuine `FigurineDescriptionOverrides` lookup + default).
- **Recommended fix:** Extract a generic paged "collectible compendium" screen/detail/render/collection abstraction parameterized over (a) the base `ItemStack` list, (b) the silhouette-flag toggle, (c) the title, (d) the detail body-text provider; doll and figurine become thin configurations. At minimum, merge the two identical `*Render` helpers and `*Collection` classes.

### [MEDIUM] `FigurineNameOverrides` and `FigurineDescriptionOverrides` are nearly byte-identical
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/item/FigurineDescriptionOverrides.java:21-96`](common/src/main/java/dev/mrshawn/pokeblocks/item/FigurineDescriptionOverrides.java)
- **What's wrong:** `FigurineDescriptionOverrides` is a structural copy of `FigurineNameOverrides`: same fields (`OVERRIDES_FILE`, `GSON`, `overrides Map<String,String>`, `configPath`), same `initialize`/`reload`, and an `applyContent` that splits each entry on the first whitespace (`trimmed.split("\\s+", 2)`) storing the remainder under the lowercased id, plus an identical `getOverride(String)`. The only differences are the filename (`figurine_names.json` vs `figurine_descriptions.json`) and a few log words ("name" vs "description").
- **Recommended fix:** Collapse into one reusable "whitespace-split string-override map" loader (instantiated with filename + log label), or have both delegate to a shared parse helper, removing the second copy of the split-on-first-whitespace parsing.

### [LOW] Unused platform abstraction methods and dead Fabric-only helper
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/platform/PokeblocksPlatform.java:35-41`](common/src/main/java/dev/mrshawn/pokeblocks/platform/PokeblocksPlatform.java)
- **What's wrong:** `registerArmorMaterial` (lines 35-37) and `makeSpawnEggFor` (lines 39-41) are default interface methods that throw `UnsupportedOperationException` and are never called from any module — speculative API surface. Relatedly, `PokeblocksFabricPlatform.registerHolder` (`fabric/src/main/java/dev/mrshawn/pokeblocks/PokeblocksFabricPlatform.java:74-76`) is a private method never referenced — dead code present in one loader only.
- **Recommended fix:** Remove the two unused default interface methods (or implement them where needed) and delete the unused private `registerHolder` helper.

### [LOW] `FigurineBlockRenderer` and `CustomDecorationBlockRenderer` are identical except for the generic type
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/block/CustomDecorationBlockRenderer.java:11-23`](common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/block/CustomDecorationBlockRenderer.java)
- **What's wrong:** Both extend `GeoBlockRenderer<T>`, construct with their model, and override `scaleModelForRender` to apply `ModSettings.GIGANTIC_SCALE` when `animatable != null && animatable.isGigantic()` — byte-for-byte identical bodies. Only the class name, type parameter (`FigurineBlockEntity` vs `CustomDecorationBlockEntity`) and model differ. `PokedollBlockRenderer` legitimately differs (camera-facing easing, squish).
- **Recommended fix:** Introduce a small generic base, e.g. `abstract class GiganticGeoBlockRenderer<T extends BlockEntity & GeoAnimatable & {isGigantic}>` implementing the gigantic-scale override once; the two simple renderers extend it and only pass their model.

### [LOW] `FigurineBlockEntity` and `CustomDecorationBlockEntity` differ only by one field name and NBT key
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/block/entity/custom/CustomDecorationBlockEntity.java:29-118`](common/src/main/java/dev/mrshawn/pokeblocks/block/entity/custom/CustomDecorationBlockEntity.java)
- **What's wrong:** `CustomDecorationBlockEntity` is a near-exact copy of `FigurineBlockEntity`: identical cache field, a single String id field (`figurine` vs `decoration`) defaulting to a `ModSettings` constant, a boolean `gigantic`, identical `registerControllers` (STOP controller), identical `setX`/`getX`/`setGigantic`/`isGigantic` with the same `sendBlockUpdated` guard, identical `saveToItem`, and identical `saveAdditional`/`loadAdditional`/`getUpdateTag`/`getUpdatePacket` aside from the NBT key string. A copy-paste pair that will drift.
- **Recommended fix:** Extract `abstract class IdGiganticBlockEntity` holding the id string + gigantic boolean, the sync helpers, and the save/load/update-tag plumbing parameterized by NBT key and the `PokeblocksItemData` tag-builder; concretes supply only `BlockEntityType`, default id, and tag-builder.

### [LOW] Four `GeoItemRenderer` subclasses repeat the same model-field + `renderByItem` boilerplate
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/CustomDecorationItemRenderer.java:12-25`](common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/CustomDecorationItemRenderer.java)
- **What's wrong:** `PokedollItemRenderer`, `FigurineItemRenderer`, `CustomDecorationItemRenderer` and `DecorativeItemRenderer` all hold `private final XModel model;`, cast `getGeoModel()` to it in the constructor, and override `renderByItem` solely to call `model.setCurrentItemStack(stack)` before super. `CustomDecorationItemRenderer` and `DecorativeItemRenderer` are essentially identical aside from the model type/constructor arg. All four `*ItemModel` classes already expose `setCurrentItemStack(ItemStack)`.
- **Recommended fix:** Add `abstract class StackAwareGeoItemRenderer<T> extends GeoItemRenderer<T>` that overrides `renderByItem` to call a `setCurrentItemStack` hook (via a shared interface) then super; subclasses pass only their model. Pokedoll/Figurine still add `getRenderColor`/silhouette/scale on top of the same base.

### [LOW] Item display-name capitalization logic duplicated between `PokedollItem` and `FigurineItem`
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/item/custom/FigurineItem.java:75`](common/src/main/java/dev/mrshawn/pokeblocks/item/custom/FigurineItem.java)
- **What's wrong:** `FigurineItem.buildDisplayName` formats an id with `id.substring(0,1).toUpperCase() + id.substring(1).replace("_"," ")`, while `PokedollItem.capitalize` (`PokedollItem.java:222-230`) performs a per-word capitalize-and-replace-underscores transform. Both turn a lowercase, underscore-joined id into a display string. **Note:** the two are not strictly equivalent — `FigurineItem` capitalizes only the first character (`pikachu_libre` → `Pikachu libre`) while `PokedollItem` capitalizes every word (`Pikachu Libre`), so consolidation must deliberately choose the canonical behavior.
- **Recommended fix:** Move id→display-name capitalization into a shared static text helper and have both items call it, deciding the desired per-word vs first-letter behavior explicitly.

---

## Data / assets

### [HIGH] Duplicate conflicting `pokemon_trophy` entry in `doll_rarity.json` silently overrides NONE with LEGENDARY
- **File:** [`common/src/main/resources/assets/pokeblocks/doll_rarity.json:34` and `:125`](common/src/main/resources/assets/pokeblocks/doll_rarity.json)
- **What's wrong:** `doll_rarity.json` has two flagless entries for the same key `pokemon_trophy`: line 34 `"pokemon_trophy none"` and line 125 `"pokemon_trophy legendary"`. `DollRarityOverrides.applyContent` (`DollRarityOverrides.java:82-83`) builds the key via `buildKey(pokemon, flags)` and calls `overrides.put(key, rarity)` with no duplicate detection, so the later-parsed LEGENDARY entry (appended in commit `3a7f6d2`) silently overwrites the NONE entry. `pokemon_trophy` is a registered decoration (`DecorativeRegistry.java:47`, `NOTHING` flags); `LootTableItemMap.build:84-85` calls `DecorativeItem.resolveRarity("pokemon_trophy", {})`, which returns the override first (`DecorativeItem.java:83-86`) → LEGENDARY, so the `rarity != NONE` guard does **not** skip it and the item is injected into loot at legendary weight rather than excluded. The stale line 34 masks the author's intent.
- **Recommended fix:** Remove the stale line 34 `"pokemon_trophy none"` (keeping LEGENDARY), or if NONE was intended, remove line 125 instead. Optionally add a load-time guard in `applyContent` that logs a warning when `overrides.put` overwrites an existing key.

### [LOW] Malformed/empty figurine description for `damorgo` is silently dropped
- **File:** [`common/src/main/resources/assets/pokeblocks/figurine_descriptions.json:3`](common/src/main/resources/assets/pokeblocks/figurine_descriptions.json)
- **What's wrong:** Line 3 is `"damorgo "` (id with trailing space, no description). `FigurineDescriptionOverrides.applyContent` (`FigurineDescriptionOverrides.java:69-77`) trims to `"damorgo"`, splits on whitespace with limit 2, gets a length-1 array, logs `LOGGER.error("Invalid figurine_description entry…")` and skips it. The `damorgo` figurine is real (`damorgo_figurine.geo.json`/texture exist, name override `"DaMorgo"` in `figurine_names.json`) but ends up with the generic fallback blurb, and the file emits an ERROR on every load. The other two entries (`doncheadle`, `airuhsea`) have descriptions; `damorgo` appears to be an incomplete placeholder.
- **Recommended fix:** Fill in a real description for `damorgo` (e.g. `"damorgo <text>"`) or remove the line 3 placeholder so it stops logging an error on load.

---

## Regression vs pre-rewrite

### [MEDIUM] Pokedoll item base scale dropped from 0.5 to 1.0; gigantic math assumes a 0.5 base that no longer exists
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/PokedollItemRenderer.java:31-32,62-74`](common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/PokedollItemRenderer.java)
- **What's wrong:** Pre-rewrite pokedoll item models rendered the doll at scale 0.5 and gigantic at 0.75 held / 0.7 gui. The rewrite has no doll item model JSON, and `PokedollItemRenderer` applies a scale only when GIGANTIC; regular dolls therefore render at GeckoLib's default 1.0 (twice pre-rewrite). The gigantic branch computes `scale = GIGANTIC_INVENTORY_SCALE/0.5f` (=1.4) or `GIGANTIC_HELD_SCALE/0.5f` (=1.5), which only yields the correct absolute gigantic size if the base were really 0.5. Because the actual base is 1.0 (no `withScale` is ever called — confirmed across all client renderers), the gigantic:regular ratio (1.4–1.5×) is preserved (so #59 still reads correctly) but the `/0.5f` constant is a latent landmine and the absolute sizing is a probable regression. The same absent base-scale convention affects `FigurineItemRenderer`/`DecorativeItemRenderer`.
- **Recommended fix:** Decide the intended base. If dolls should match pre-rewrite size, call `withScale(0.5f)` on `PokedollItemRenderer` (and `FigurineItemRenderer`) so the base is 0.5, then drop the `/0.5f` division (the `GIGANTIC_*_SCALE` constants become absolute again). If the doubled size is intentional, replace the magic `0.5f` with a named `BASE_SCALE` constant and document it. Align all `GeoItem` item renders on one base-scale convention.

### [LOW] Wool-block secondary properties dropped by switching `copy(WHITE_WOOL)` to bare `Properties.of()`
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/block/custom/PokedollBlock.java:75`](common/src/main/java/dev/mrshawn/pokeblocks/block/custom/PokedollBlock.java)
- **What's wrong:** Pre-rewrite, doll/figurine/decorative blocks used `FabricBlockSettings.copy(Blocks.WHITE_WOOL).strength(0.4f)`, inheriting wool's full property set (flammable/`ignitedByLavaOrFire`, wool map color, GUITAR note-block instrument). The rewrite's `Properties.of().sound(WOOL).strength(0.4f).noOcclusion()` reproduces only sound, hardness and occlusion, so these blocks are no longer flammable, lose their wool map color, and no longer act as a guitar note-block base. Consistent across all four block classes, so internally coherent, and the card's stated scope was sound+strength+occlusion. Flagged because flammability is a behavioral (not purely cosmetic) change a player could notice. **Note:** the `pushReaction` claim does not apply — wool uses the default `NORMAL`, so nothing changed there.
- **Recommended fix:** If parity with pre-rewrite plush-block behavior is desired, add `.ignitedByLavaOrFire()` / `.instrument(NoteBlockInstrument.GUITAR)` / `.mapColor(...)`, or define a shared helper mirroring `WHITE_WOOL` with `strength(0.4f)`. Otherwise document that the loss is intentional.

---

## Correctness

### [LOW] Bare-pokemon loot exclusion check is not lowercased (inconsistent with `LootGroup`)
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/config/PokeblocksConfig.java:710-715`](common/src/main/java/dev/mrshawn/pokeblocks/config/PokeblocksConfig.java)
- **What's wrong:** `isDollExcludedFromLoot()` does the bare-pokemon match with `excludedLootDolls.contains(pokemon)` without lowercasing (line 712), while `LootGroup.containsDoll()` (`LootGroup.java:53`) correctly does `dollKeys.contains(pokemon.toLowerCase())`. The set is populated from canonical lowercase keys (`DollRarityOverrides.buildKey` lowercases). The main loot-build path passes already-lowercase ids so it is not triggered there, but mixed-case input is reachable via the mc-test state provider (`mctest/PokeblocksStateProvider.java:121-122` passes raw `requireStr(args,"pokemon")`), where it would silently fail the bare-name match. Latent inconsistency.
- **Recommended fix:** Lowercase the argument before the bare-name check: `if (excludedLootDolls.contains(pokemon.toLowerCase(Locale.ROOT))) return true;`, mirroring `LootGroup.containsDoll`.

### [LOW] Card #59 confirmed satisfied (informational)
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/PokedollItemRenderer.java:31-32,66-71`](common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/PokedollItemRenderer.java)
- **What's confirmed:** Gigantic items render larger than regular — `PokedollItemRenderer` scales GIGANTIC dolls by `0.75/0.5 = 1.5×` (held) and `0.7/0.5 = 1.4×` (GUI) and translates up by half the increase so the model grows upward. The absolute values 0.75 held / 0.7 gui match the pre-rewrite gigantic item display scales (vs regular 0.5). On the block side, `DecorativeBlockRenderer`/`PokedollBlockRenderer` apply `ModSettings.GIGANTIC_SCALE = 2.0f`, matching pre-rewrite `PokeblocksClient.SCALE = 2.0f`. The block-2.0× vs item-1.5× asymmetry is inherited directly from pre-rewrite data — a pre-existing intentional choice, not a rewrite regression.
- **Recommended fix:** No change required for #59. If the block-vs-item ratio asymmetry is undesired, it is a design decision predating the rewrite and should be raised separately.

---

## Thread safety

### [LOW] `cachedTotalWeight` static cache is read/written from render and server threads without synchronization
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/item/RarityScoreCalculator.java:69,193-213`](common/src/main/java/dev/mrshawn/pokeblocks/item/RarityScoreCalculator.java)
- **What's wrong:** `cachedTotalWeight` (line 69) is a plain non-volatile mutable static double, lazily populated inside `computeChance()` (reached from `PokedollItem.appendHoverText` on the client render thread) and reset via `invalidateTotalWeightCache()` from `PokeblocksCommon.invalidateLootMap()` (line 111) on the server thread (reload commands / lifecycle). A reload that resets the cache to `-1` concurrently with a tooltip render can cause a redundant recompute or a briefly stale total. The recompute is idempotent and the consumer is a cosmetic tooltip percentage, so the practical worst case is a momentarily stale displayed value (a non-volatile double write is not JLS-atomic, so a torn read is theoretically possible — which mildly strengthens the case for `volatile`).
- **Recommended fix:** Mark `cachedTotalWeight` `volatile` (and ideally compute it into a local before the comparison), or guard the compute/invalidate pair behind the same concurrent structure used for the loot cache.

---

## Structure

### [LOW] `findCustomDir` has dead/redundant existence logic
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/resourcepack/CustomPackBuilder.java:48-60`](common/src/main/java/dev/mrshawn/pokeblocks/resourcepack/CustomPackBuilder.java)
- **What's wrong:** `findCustomDir()` first walks `customDir` and returns it if it contains any regular file, then immediately does `if (Files.exists(customDir)) return customDir;` — which returns the directory whenever it exists regardless of content. The first `Files.walk()/anyMatch` block is therefore dead; the effective behavior is just "return `customDir` if it exists, else null". Not a correctness bug (downstream callers handle an empty dir gracefully) but the walk is wasted I/O and the apparent intent (treat the dir as present only when it has content) is silently not honored.
- **Recommended fix:** Either drop the redundant second `if` and keep the content check (return null for an empty custom dir), or remove the walk entirely if returning an existing-but-empty dir is intended.

### [LOW] GIGANTIC scale "constants" in `PokedollItemRenderer` are non-static instance fields
- **File:** [`common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/PokedollItemRenderer.java:31-32`](common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/PokedollItemRenderer.java)
- **What's wrong:** `GIGANTIC_HELD_SCALE` (0.75f) and `GIGANTIC_INVENTORY_SCALE` (0.7f) are declared `private final float` instance fields with SCREAMING_CASE constant naming, never depend on instance state, and sit alongside a correct `private static final Color SILHOUETTE_COLOR` in the same class — an internal inconsistency. Their only use divides each by a magic `0.5f` literal.
- **Recommended fix:** Make both `private static final float`, and ideally hoist the magic `0.5f` base scale into a named constant too.

---

## Content cards status (#58 / #59 / #61)

### #58 — Figurine compendium content
The figurine compendium feature ships and is wired (commit `904777a`): screens, detail view, render helpers, collection, and client hooks all exist under `common/.../client/screen/`, backed by real figurine assets (e.g. `damorgo_figurine.geo.json` + texture, name overrides in `figurine_names.json`). The figurine detail screen additionally carries a genuine `FigurineDescriptionOverrides` description-lookup system that the doll variant lacks. **Outstanding:** the figurine description data is incomplete — `figurine_descriptions.json:3` has a malformed empty `damorgo` placeholder that logs an ERROR on every load and falls back to the generic blurb (see Data/assets above). A **human visual pass** is still needed to confirm grid layout, paging, silhouette rendering, and detail-page text render correctly in-game, since the screens were produced by cloning the doll compendium and have not been independently rendered. The heavy structural duplication (five near-identical classes) is a maintainability concern, not a functional blocker.

### #59 — Gigantic items render larger than regular
**Confirmed satisfied.** Gigantic dolls render at `1.5×` held and `1.4×` GUI relative to base (absolute 0.75/0.7, matching pre-rewrite), and gigantic blocks at `2.0×` (`ModSettings.GIGANTIC_SCALE`, matching pre-rewrite `PokeblocksClient.SCALE`). So gigantic content is unambiguously bigger than regular. **Block-vs-item ratio note:** blocks scale by `2.0×` while held items scale by only `1.4–1.5×` relative to base; this asymmetry is inherited directly from pre-rewrite data (block 2.0 vs item 0.75/0.5 = 1.5) and is therefore a pre-existing intentional design choice, **not** a rewrite regression. No change is required for #59 itself. One latent caveat tied to this area: the item renderers' `/0.5f` divisor assumes a 0.5 base scale that no longer exists (regular items now render at GeckoLib's default 1.0), so while the gigantic:regular *ratio* is correct, the *absolute* item sizes are likely doubled vs pre-rewrite — tracked as the MEDIUM regression finding above, separate from #59's pass/fail.

### #61 — Figurine / decoration / decorative WOOL + 0.4 strength
**Confirmed correct and complete.** All four block classes — `PokedollBlock.java:75`, `FigurineBlock.java:33`, `CustomDecorationBlock.java:38`, `DecorativeBlock.java:47` — use `Properties.of().sound(SoundType.WOOL).strength(0.4f).noOcclusion()`, applying the wool sound type and 0.4 strength uniformly and matching the card's stated scope of sound + strength + occlusion. `PokedollBlock` (the #61 reference) is consistent with the figurine/decorative classes, which are new in the rewrite and internally coherent with that reference. The only side note is that switching from `copy(WHITE_WOOL)` to bare `Properties.of()` drops wool's secondary properties (flammability, map color, GUITAR instrument); this is consistent across all four classes and outside #61's stated sound+strength+occlusion scope, so #61 is delivered correctly — the property-parity question is logged separately as a LOW regression observation.

---

## Recommended action order

1. **[HIGH] Forge/NeoForge save migration** (`PokeblocksFabric.java:24` subsystem) — close the loader-parity gap or, at minimum, emit a documented startup warning on Forge/NeoForge so the missing migration is intentional and visible. Highest real-world risk (content loss on legacy-save upgrades).
2. **[HIGH] Duplicate `pokemon_trophy` rarity key** (`doll_rarity.json:34` / `:125`) — a one-line data fix that removes the silent flip of an intended-excluded item into the legendary loot pool; add the optional overwrite-warning guard in `DollRarityOverrides.applyContent`.
3. **[MEDIUM] Item base-scale landmine** (`PokedollItemRenderer.java`) — decide the intended base, add `withScale(0.5f)` (or a named `BASE_SCALE`) and remove/justify the `/0.5f` divisor across the doll/figurine/decorative renderers so they share one convention.
4. **[LOW data] `damorgo` figurine description** (`figurine_descriptions.json:3`) — fill in or remove the placeholder to stop the per-load ERROR, as part of the #58 content pass.
5. **[HIGH-reuse / MEDIUM] DRY consolidation** — extract the generic paged-compendium abstraction (figurine vs doll), the shared `loadWithPackOverrides` config helper, and the merged `FigurineName`/`FigurineDescription` loader. These remove the bulk of the rewrite's duplication and shrink future maintenance surface.
6. **[LOW] Correctness/thread-safety hardening** — lowercase the bare-name loot exclusion check (`PokeblocksConfig.java:712`) and mark `cachedTotalWeight` `volatile` (`RarityScoreCalculator.java:69`).
7. **[LOW] Cleanup pass** — collapse the identical block-entity / block-renderer / item-renderer pairs, add the `[[mixins]]` block to Forge `mods.toml`, remove the dead platform methods and `findCustomDir` walk, and make the GIGANTIC scale fields `static final`. Batch these as a single low-risk tidy-up.
8. **#59 / #61** — no code action required; treat as verified-complete and capture the block-vs-item ratio note and the WOOL secondary-property question as documented intentional decisions.
