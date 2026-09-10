# Pokeblocks Upcoming Update — Assumptions & Decisions Log

> Companion to [UPCOMING_UPDATE_TODO.md](UPCOMING_UPDATE_TODO.md).
> Records every place where completing a remaining card required an **assumption** or a **judgment call** that
> couldn't be settled purely mechanically. Each entry gives the **exact location**, the **value/decision set**, and
> the **reasoning** (grounded against the pre-rewrite branch `origin/1.21.1/10.22.2025`, the already-correct
> `PokedollBlock`, Minecraft source, and GeckoLib behavior).
>
> Branch: `multiloader/initial` · Generated 2026-06-26 · Phases 4–7 pass.

---

## How to read this

| Field | Meaning |
|-------|---------|
| **Location** | File + symbol the change/decision lives at. |
| **Value set** | The concrete value chosen. |
| **Reasoning** | Why this value is defensible, and what it was grounded against. |
| **Confidence** | How safe the choice is without an in-game pass. |
| **Needs human** | What a person still has to confirm, if anything. |

---

## Card #61 — Block break speeds / tools / sounds  ✅ changed

The pre-rewrite mod built **every** doll/figurine/decoration block from
`FabricBlockSettings.copy(Blocks.WHITE_WOOL).strength(0.4f).nonOpaque()` (see
`origin/1.21.1/10.22.2025:src/main/java/dev/mrshawn/pokeblocks/block/ModBlocks.java` and
`.../block/custom/PokedollBlock.java`). The multiloader rewrite carried that onto `PokedollBlock`
(`Properties.of().sound(SoundType.WOOL).strength(0.4f).noOcclusion()`) **but not** onto the figurine,
custom-decoration, or generic-decorative blocks, which were left at the bare default
`Properties.of().noOcclusion()` → **STONE break sound + `0.0` hardness (instant break) + `0.0` resistance**.
That is a behavior regression, so it was restored.

### Assumption 1 — Figurine / decoration block material = wool, hardness 0.4

- **Location:**
  - `common/src/main/java/dev/mrshawn/pokeblocks/block/custom/FigurineBlock.java` — `FigurineBlock()` constructor
  - `common/src/main/java/dev/mrshawn/pokeblocks/block/custom/CustomDecorationBlock.java` — `CustomDecorationBlock()` constructor
  - `common/src/main/java/dev/mrshawn/pokeblocks/block/custom/decorative/DecorativeBlock.java` — `DecorativeBlock(...)` constructor
- **Value set:** `Properties.of().sound(SoundType.WOOL).strength(0.4f).noOcclusion()`
  (was `Properties.of().noOcclusion()`). `strength(0.4f)` sets **both** destroy-time and blast-resistance to `0.4`.
- **Reasoning:** Exact parity with (a) the already-merged, correct `PokedollBlock`, and (b) the pre-rewrite settings
  where these blocks copied `WHITE_WOOL` (→ `SoundType.WOOL`) and overrode `strength(0.4f)`. Plush/figurine blocks
  reading as soft wool that pops quickly is the established game feel.
- **Confidence:** **High** — directly restores documented prior behavior; compiles clean (`:common:compileJava` exit 0).
- **Needs human:** Only a "feel" play-test (the card's 🟡 portion) — mining speed/sound. No further code expected.

### Assumption 2 — No per-doll material variants in the data-driven block

- **Decision:** Use **one** universal `WOOL`/`0.4` material for all dolls/figurines/decorations.
- **Reasoning:** The pre-rewrite mod had a *handful* of per-doll material exceptions —
  `applin_basket` copied `OAK_PLANKS`, `magikarp_fishbowl` copied `GLASS`
  (`origin/1.21.1/10.22.2025:.../block/ModBlocks.java`). The rewrite intentionally collapsed all dolls into a
  **single** data-driven `PokedollBlock` (+ one `FigurineBlock`, one `CustomDecorationBlock`, one shared
  `DecorativeBlock`), so a per-id sound/hardness is no longer expressible without re-introducing per-id block
  classes. `WHITE_WOOL`/`0.4` was the universal default for the vast majority pre-rewrite, so it is the correct
  single value. The two glass/wood outliers lose their distinct break sound — a conscious, documented trade-off of
  the rewrite's architecture, **not** an oversight.
- **Confidence:** **High** for the common case; the 2 outliers are a known, minor cosmetic loss.
- **Needs human:** Decide (later, if desired) whether `magikarp_fishbowl`/`applin_basket` warrant a per-definition
  `SoundType` field on `DecorativeDefinition`. Out of scope for #61.

### Assumption 3 — Did not replicate the old `.solidBlock(false)` call

- **Decision:** The restored properties **omit** an equivalent of the pre-rewrite `.solidBlock((s,w,p) -> false)`.
- **Reasoning:** The reference `PokedollBlock` in the current rewrite already omits it and is the merged, accepted
  baseline; matching it keeps all four block types consistent. `noOcclusion()` already covers the rendering side.
- **Confidence:** **High** — parity with the accepted reference block.

### Blocks confirmed already correct (no change)

- `PokeBlock` (the `pokeblock_*` full-cube blocks) — registered in `PokeBlockRegistry` with
  `Properties.of().strength(2.0f)`, which **exactly matches** the pre-rewrite
  `AbstractBlock.Settings.create().strength(2.0f)` (default STONE sound for an opaque cube). Left as-is.
- `PokedollBlock` — already `sound(WOOL).strength(0.4f).noOcclusion()`. Left as-is.

---

## Card #59 — Gigantics item model bigger than regular dolls  ✅ verified, no change

The mechanism already exists; the values were verified against the pre-rewrite mod rather than guessed.

### Assumption 4 — Gigantic **block** scale = 2.0×

- **Location:** `common/src/main/java/dev/mrshawn/pokeblocks/constants/ModSettings.java` → `GIGANTIC_SCALE = 2.0f`
  (applied in every `*BlockRenderer.scaleModelForRender`).
- **Value set:** `2.0f` (unchanged).
- **Reasoning:** Matches the pre-rewrite `private static final float SCALE = 2.0f` used by
  `registerScaledBlockEntityRenderer(...)` in
  `origin/1.21.1/10.22.2025:.../PokeblocksClient.java`. Exact carry-over.
- **Confidence:** **High.**

### Assumption 5 — Gigantic **item** scale = 1.5× held / 1.4× GUI (smaller than the block's 2.0×)

- **Location:** `common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/PokedollItemRenderer.java`
  → `GIGANTIC_HELD_SCALE = 0.75f`, `GIGANTIC_INVENTORY_SCALE = 0.7f` (the `/0.5f` base means **1.5×** in hand,
  **1.4×** in the inventory slot relative to a regular doll item).
- **Value set:** Unchanged. The card's literal requirement ("bigger than the regular dolls") is **already satisfied** —
  gigantic items render 40–50 % larger than regular ones.
- **Reasoning / why item ≠ block (2.0×):** An item must fit inside a 16×16 GUI slot and the first-person hand; a full
  2.0× item overflows the slot and clips the hand. A reduced 1.4–1.5× keeps gigantics visibly larger while staying
  inside item bounds. The pre-rewrite mod scaled only the *block* (block-entity renderer), not the item, so there is
  no prior item value to copy — 1.4–1.5× is the conservative "clearly bigger but still fits" choice.
- **Confidence:** **Medium-High** — code-correct and clearly bigger; the exact magnitude is a taste call.
- **Needs human:** The card's 🟡 portion — eyeball whether gigantic items should be even larger. Tunable purely via
  the two constants above; no structural change required.

---

## Card #58 — Weird item models (Luvdisc cushion)  ⚠️ diagnosed, deliberately NOT auto-fixed

### Decision 6 — Did not edit the Luvdisc geometry; flagged a safe fix location instead

- **Location of the symptom:**
  - `common/src/main/resources/assets/pokeblocks/geo/block/luvdisc_cushion.geo.json` — the cushion geometry is built
    **off-origin**: its cubes are centred around `z ≈ -5` with 45° rotations (visible-bounds offset `[0, 0.75, 0]`),
    so the model's visual centre is **not** at the model origin.
  - `common/src/main/java/dev/mrshawn/pokeblocks/client/renderer/item/DecorativeItemRenderer.java` — extends
    `GeoItemRenderer` with **no** `scaleModelForRender` / translate override (contrast `PokedollItemRenderer`, which
    does override it). So the decoration item is drawn at the model's raw origin.
- **Why the block looks fine but the item looks "weird":** the in-world block uses `DecorativeBlockRenderer extends
  GeoBlockRenderer`, and GeckoLib's `GeoBlockRenderer` auto-translates to the block centre + applies facing rotation.
  The **item** renderer does none of that, so an off-centre model like the Luvdisc cushion renders visibly shifted /
  oddly oriented in the inventory and hand.
- **Why I did not "just fix it":**
  1. The `.geo.json` is **shared** by the block model (`DecorativeModel`) and the item model (`DecorativeItemModel`).
     Editing the geometry to centre the *item* would shift the already-correct *in-world block*.
  2. The exact target (how far to translate, what rotation looks right) is a **visual** judgment that the card itself
     assigns to a human ("visually identify what's wrong … apply the JSON/transform fixes once the defect is
     described"). Guessing a transform blind could make it worse.
- **Recommended safe fix (for the human pass):** add an **item-renderer-only** transform in `DecorativeItemRenderer`
  (override `scaleModelForRender` / `renderByItem` to translate the model so its visual centre sits at the slot
  centre, optionally per-`DecorativeDefinition`), **leaving the shared `.geo.json` untouched**. This mirrors how
  `PokedollItemRenderer` already special-cases its own item transform.
- **Confidence:** **High** on the diagnosis (root cause is the missing item-side transform); **deliberately deferred**
  on the fix value pending a rendered look.
- **Needs human:** Render the cushion as an item, confirm the off-centre/rotation defect, and pick the translate/scale
  (or confirm whether other decoratives need the same). The project review (`PROJECT_REVIEW.md`) re-derives this
  independently.

---

## Deliverables produced for the documentation / review cards

| Card | Deliverable | Location | Human step remaining |
|------|-------------|----------|----------------------|
| **#55** Project review | Full multi-agent, adversarially-verified review | `PROJECT_REVIEW.md` | Triage & action the findings |
| **#22** Wiki pages | Draft admin/pack-maker wiki | `wiki/Home.md`, `wiki/Configuration.md`, `wiki/Resource-Packs.md`, `wiki/Custom-Content-Packs.md`, `wiki/Commands.md` | Publish to GitHub wiki + add screenshots |

---

## Cards that genuinely still need a human (could not be completed in-repo)

| Card | Why it can't be code-completed | What was done to assist |
|------|-------------------------------|-------------------------|
| **#60** Review new models (Cutiefly legs clip, etc.) | Geometry/rig fixes in Blockbench — outside code. | Listed in `PROJECT_REVIEW.md` content section if any asset/transform regression is detectable; otherwise pure modeling. |
| **#25** Profiler pass | Requires launching Minecraft + capturing a Spark/JFR recording — I can't run the client. | JFR/flamegraph tooling is available to analyze a dump **once a human captures one**. No dump exists yet. |
| **#8 / #14** Merge final update to master | Release decision + branch merge is a person's call. | Branch can be prepped / PR opened on request; not auto-merged. |

---

## Project-review (#55) findings — what I acted on vs. deferred

Card #55 explicitly assigns the *decision* to a human ("I produce the findings; you decide what to action"). The full,
adversarially-verified list (19 findings: 3 high / 3 medium / 13 low) lives in [PROJECT_REVIEW.md](PROJECT_REVIEW.md).
I applied **only** the handful of fixes that are unambiguous, behavior-preserving on the live game path, and that no
reviewer would debate. Everything that touches game balance, visuals, architecture, or needs a design call was left in
the report for Shawn.

### Applied (safe, zero-behavior-risk)

- **Thread-safety — `cachedTotalWeight` → `volatile`**
  - **Location:** `common/.../item/RarityScoreCalculator.java` — the static `cachedTotalWeight` field.
  - **Value set:** added `volatile`.
  - **Reasoning:** It is written from the server thread (cache invalidation on reload/lifecycle) and read from the
    client render thread (tooltip %). `volatile` publishes writes and avoids a torn read of the non-atomic `double`.
    No behavior change; pure hardening.
- **Correctness — lowercase the bare-name loot-exclusion check**
  - **Location:** `common/.../config/PokeblocksConfig.java` → `isDollExcludedFromLoot(...)`.
  - **Value set:** `excludedLootDolls.contains(pokemon)` → `excludedLootDolls.contains(pokemon.toLowerCase())`.
  - **Reasoning:** `excludedLootDolls` is populated from `DollRarityOverrides.buildKey(...)` keys, which lowercase;
    the sibling `LootGroup.containsDoll` already lowercases the same bare check. The live loot path passes
    already-lowercase ids (so `.toLowerCase()` is a no-op there), but a mixed-case caller (e.g. the mc-test state
    provider) previously slipped past the exclusion. Matches existing convention exactly; no live-path change.
- **Hygiene — gigantic item scale fields `static final`**
  - **Location:** `common/.../client/renderer/item/PokedollItemRenderer.java` → `GIGANTIC_HELD_SCALE`,
    `GIGANTIC_INVENTORY_SCALE`.
  - **Value set:** `private final` → `private static final` (values unchanged: `0.75f`, `0.7f`).
  - **Reasoning:** They never depend on instance state and sit beside an already-`static final` constant; values are
    untouched, so #59 behavior is identical.

### Deferred to Shawn (judgment / balance / design / scale) — see PROJECT_REVIEW.md

| Finding | Why I did **not** auto-apply |
|---------|------------------------------|
| **[HIGH]** Forge/NeoForge get no legacy-world datafixer migration (Fabric-only) | Needs a product decision (do Forge/NeoForge support legacy-save upgrades?) + a non-trivial per-loader SPI implementation. |
| **[HIGH]** Duplicate `pokemon_trophy` key in `doll_rarity.json` (NONE vs LEGENDARY) | The *intended* rarity is genuinely ambiguous. Last-wins currently → LEGENDARY, which matches the pre-rewrite data **and** the mc-test assertion, so I did **not** silently pick the other value — it's a balance call for Shawn. |
| **[HIGH-reuse]** Figurine compendium / block-entity / renderer duplication; `loadWithPackOverrides` helper; merged Figurine name/description loaders | Large structural refactors; should be batched & reviewed deliberately. |
| **[MEDIUM]** Item base-scale landmine (`/0.5f` assumes a 0.5 base that is now 1.0) | Changing the base scale alters the on-screen size of **regular** doll/figurine items — a visual decision that needs an in-game look (related to my #58/#59 notes above). |
| **[LOW]** `damorgo` figurine description placeholder logs an ERROR each load | Content gap — the figurine wants a *real* description (human content pass), not silent deletion. |
| **[LOW]** Wool secondary properties (flammability / map color / note-block instrument) lost vs `copy(WHITE_WOOL)` | Outside #61's stated sound+strength+occlusion scope; whether plush blocks should burn is a design choice. |
| Other LOW cleanups (Forge `[[mixins]]`, dead platform methods, `findCustomDir` walk) | Low-risk tidy-ups best batched; deferred to avoid expanding the pre-merge diff without Shawn's call. |

---

## Verification performed

- `./gradlew :common:compileJava` → **exit 0** after the #61 edits.
- `./gradlew :fabric:compileJava :forge:compileJava :neoforge:compileJava` → **exit 0** (all loaders re-source
  common; only pre-existing forge-deprecation / mixin-mapping / `unchecked` notes, none from these changes).
- `./gradlew :common:compileJava` → re-verified **exit 0** after the three safe review fixes above.

---

## Compendium finalization pass (2026-07-08)

Both compendium books were taken from POC to shippable: server-side per-player collection persistence
(+ optional sync payload), the two near-verbatim screen stacks merged behind `CompendiumType`, textured
GUI (panel / slots / arrows / book spread) and original 16x16 item icons, recipes, localization, and the
`IncompleteFeatureItem` gate removed from both books (laser pointer stays gated). The `[HIGH-reuse]`
figurine-compendium duplication finding in PROJECT_REVIEW.md is resolved by this pass.

### Assumption — "collected" = has ever carried the item (per player, per world)

- **Location:** `common/.../compendium/CompendiumProgressStore.java`, recorded from
  `PokedollItem#inventoryTick` / `FigurineItem#inventoryTick` (1s cadence), stored as a `SavedData`
  file `data/pokeblocks_compendium.dat` on the overworld, keyed by player UUID.
- **Value set:** discovery latches on first carry and never un-records; dolls collect **species**
  (variant flags ignored), figurines collect ids. Client shows synced-progress ∪ current inventory.
- **Reasoning:** the POC's own comments called inventory-scanning "the cheap, no-persistence path" and
  named per-player saved data as the real design. Species-level (not per-variant) matches the POC's
  `getPokemonFromStack(...).toLowerCase()` keying. The inventory-scan union keeps the book working
  against servers too old to send the payload (same graceful-degradation stance as pack sync).
- **Confidence:** high for the mechanism (unit-tested codec/NBT/latch); the *policy* (should shinies be
  separate entries?) is a design call Shawn may want to revisit.
- **Needs human:** in-game pass on all 3 loaders; decide whether shiny/gigantic variants deserve their
  own compendium entries later.

### Assumption — recipes: book+wool (doll) / book+quartz (figurine)

- **Location:** `common/src/main/resources/data/pokeblocks/recipe/compendium.json`,
  `figurine_compendium.json` (vanilla `crafting_shapeless`).
- **Value set:** Doll Compendium = `minecraft:book` + `#minecraft:wool`; Figurine Compendium =
  `minecraft:book` + `minecraft:quartz`.
- **Reasoning:** no prior recipes existed (items were creative-only behind `show_incomplete_items`).
  Wool = plush theme; quartz = display/ornament theme. Cheap on purpose — the books are trackers, not
  rewards. Trivial to rebalance by editing the JSON.
- **Confidence:** medium (pure taste). **Needs human:** confirm ingredients/cost.

### Assumption — Forge sync channel is separate from `pack_sync`

- **Location:** `forge/.../PokeblocksForge.java` → `compendium_sync` optional channel, v1.
- **Value set:** new optional channel rather than a third payload on the existing `pack_sync` channel.
- **Reasoning:** old-client/new-server joins are an explicitly supported flow (doll-only update skew);
  adding payload types to an existing Forge channel risks member-list mismatches against older builds,
  while a whole missing channel is exactly what `.optional()` tolerates. Fabric/NeoForge register the
  payload individually (inherently optional per-payload) so nothing changes for them.
- **Confidence:** high. **Needs human:** cross-version join smoke test alongside the pack handshake.

### Judgment call — index screen adapts rows/columns to the window

- **Location:** `common/.../client/screen/CompendiumScreen.java` → `init()`.
- **Value set:** columns `clamp((width-24)/44, 4..7)`, rows `clamp((height-93)/44, 2..4)` (the POC was a
  fixed 7×4 that overflowed 320×240 GUI-scale setups).
- **Reasoning:** the textured panel + Done button need ~93px of vertical chrome; at the common 480×270
  logical size this still yields the POC's 7×4. Verified against a pixel-exact PIL mock of the layout.
- **Confidence:** high, but it is new layout code. **Needs human:** eyeball at GUI scale 1–4.

### Play-test feedback round (2026-07-08, same day)

Shawn's first pass surfaced seven changes, all applied:

- **Combee render fix + representative variants** — the index/detail no longer builds bare no-flag
  stacks; each species is represented by its least-flagged **valid** variant from
  `PokedollItem.getAllMutations` (Combee has no base texture — gender is mandatory — so it now shows
  its male form). Ordering rule: non-gigantic before gigantic, non-shiny before shiny, fewer flags
  first, canonical key as the final tiebreak.
- **Per-variant tracking** — progress keys upgraded from bare species to
  `CompendiumVariantKey` (`"species flag1 flag2"`, flags lower-case + alphabetical; bare species ==
  the no-flag variant, so day-old playtest data reads as "base variant found" and self-heals on the
  next carry). Doll detail pages list every valid variant as clickable mini-slots, silhouetted until
  that exact variant is carried; the selected variant drives the big render + status line.
- **Found/Missing filter** — cycling chip (All → Found → Missing) in the index footer.
- **Vanilla-grey buttons removed** — Done/Back replaced by a themed leather close pad (panel
  top-right / book cover corner); chips + pads come from the same generator script.
- **`E` closes** like vanilla containers (index: unless the search box is focused; detail: closes
  the whole book, while `ESC` steps back to the index).
- **Drag-to-inspect** — dragging the left page steers yaw/pitch (clamped ±80°); ~2.5s after release
  the pitch eases home and the auto-spin resumes from the manual angle (`DollSpin.snapTo`).
  **Needs human:** the pitch drag *direction* is a sign guess (GUI y-flip) — flip
  `DRAG_SENSITIVITY` on the pitch axis if dragging down tilts the wrong way.
- **Figurine "Hide Box"** — every figurine geo names its display case bone `box`;
  `FigurineItemRenderer.HIDE_BOX` + a `renderRecursively` override skip that subtree (stateless — no
  bone mutation), toggled by a chip on discovered figurine pages only.

Verification: all 4 modules compile; `:common:test` green (11 compendium tests). Still needs an
in-game pass (drag feel/pitch sign, variant strip with >10 variants, box-hide on every figurine).

---

## Pokedoll Phone feature (2026-07-08)

New, fully-realized feature (not a card): a craftable **Pokedoll Phone** that occasionally rings
(buzzing sound + lit inventory texture), opens an accept/hang-up call screen when picked up, and on
accept scatters dig sites around the player that resolve into junk or a rarity-rolled buried doll
(guaranteed within 3 digs). New `phone` package (payloads/codec/calls/quest store/manager/rules),
`DigSiteBlock` (+4 stage models over vanilla dirt/mud textures), `PokedollPhoneItem`, call screen
reusing the compendium panel/chips/doll-render stack, PIL-generated textures
(`tools/gen_phone_assets.py`) and an ffmpeg-synthesized `phone_buzz.ogg`.

### Assumption — lit texture rides vanilla `custom_model_data`, not a modded predicate

- **Location:** `models/item/pokedoll_phone.json` (override) + `PokedollPhoneItem` (sets/clears
  `DataComponents.CUSTOM_MODEL_DATA` on ring start/end).
- **Value set:** `custom_model_data == 1` selects `pokedoll_phone_on`.
- **Reasoning:** `ItemProperties.register` is **private** in vanilla 1.21.1; calling it from common
  would lean on loader-specific AWs/ATs at runtime. The server already mutates the stack at ring
  transitions, so the vanilla generic predicate needs zero client registration on any loader.
- **Confidence:** high (compiles + wire-format identical everywhere). Quirk: a phone dropped into a
  chest mid-ring keeps its lit texture until next carried (no inventoryTick to expire it).
- **Needs human:** in-game check that the texture flips in inventory within ~1s of ring start/end.

### Assumption — ring cadence / quest shape defaults (all configurable under `[phone]`)

- **Value set:** `enabled=true`, `average_call_interval_minutes=15`, `ring_seconds=30` (spec),
  `dig_sites=6` (spec), `site_radius=32`, `guaranteed_attempts=3` (spec); plus hardcoded 1-min
  initial grace, 2-min post-call cooldown, 60s accept/hang-up decision window.
- **Reasoning:** spec fixed 30s/6 sites/3 attempts; the interval/radius are taste values placed in
  config so Shawn can retune without code. One ring roll per second (`1/(avg*60)` per check) gives a
  memoryless average matching the configured interval.
- **Confidence:** medium (feel values). **Needs human:** pacing play-test.

### Assumption — guarantee = pre-rolled winning dig index stored on the quest

- **Location:** `DigQuestManager.startQuest` (`targetAttempt = 1 + rand(min(3, siteCount))`),
  `DigQuestRules.shouldFindDoll` (attempts ≥ target OR last remaining site).
- **Reasoning:** rolling the winning *attempt number* up front makes the guarantee unconditional on
  WHICH mounds the player digs, survives restarts inside the SavedData, and stays unit-testable. The
  "last remaining site" clause covers externally destroyed sites.
- **Confidence:** high (unit-tested).

### Assumption — dig sites are unbreakable air-cell mounds; external removal shrinks the quest

- **Location:** `DigSiteBlock` (`strength(-1, 3.6e6)`, `pushReaction(BLOCK)`, no BlockItem, no loot),
  placed only into an AIR cell above `{grass_block, dirt, coarse_dirt, podzol, mycelium, rooted_dirt}`
  (heightmap surface, world-border + 5-block spacing checks), `onRemove` → quest update.
- **Reasoning:** the spec demands existing blocks are never destroyed/overwritten, so the mound
  occupies air and the soil below is untouched. Unbreakable + piston-proof keeps the minigame from
  being mined apart; /setblock-or-support-loss removals shrink the quest (last one fails it) instead
  of leaving orphans, and a mound whose quest is gone self-removes on interaction.
- **Confidence:** high for placement safety; medium for the "right-click to dig" interaction reading
  as intuitive (vs. left-click mining). **Needs human:** dig feel; whether stages should need more
  clicks or a shovel.

### Assumption — buried doll roll reuses the LOOT pipeline's exclusions; caller is uniform species

- **Location:** `PhoneCalls.pickBuriedDollKey` (weighted over
  `RarityScoreCalculator.computeAllVariants(getExcludedLootFlags())`, skipping NONE-rarity,
  zero-weight and `excluded_dolls` entries), `PhoneCalls.pickCallerKey` (uniform species,
  least-flagged VALID representative — the compendium's Combee rule).
- **Reasoning:** "already established doll rarity" == the loot weighting; reusing its exclusions
  keeps gigantic/noice/substitute policy in one place. The caller is cosmetic, so uniform species
  gives variety; the least-flagged-valid representative avoids broken bare stacks (Combee).
- **Confidence:** high. **Needs human:** confirm gigantic dolls should stay un-buriable (they are,
  via the default `excluded_flags`).

### Assumption — Forge gets a third optional channel `phone_sync`

- **Location:** `forge/.../PokeblocksForge.java`.
- **Value set:** own `.optional()` channel (S2C dig sites, C2S call response) — mirroring the
  documented `compendium_sync` reasoning: never append payloads to an existing Forge channel that
  old clients negotiate by member list. Fabric/NeoForge register the payloads individually.
  Phones deliberately never ring for players whose client lacks the payloads (`canSendTo` gate).
- **Confidence:** high. **Needs human:** cross-version join smoke test.

### Judgment call — client tick via a common client mixin, not per-loader events

- **Location:** `mixin/PhoneClientTickMixin` (`Minecraft#tick` TAIL → `DigSiteParticles.tick`),
  registered in the mixin config's `client` array.
- **Reasoning:** the repo's established pattern (`ServerTickMixin`, `PlayerJoinMixin`) — one hook for
  all three loaders instead of three ClientTickEvent registrations. Particles: gold dust shimmer
  every 3 ticks + END_ROD beacon every 40, ≤96 blocks, purely cosmetic.
- **Confidence:** high.

### Verification performed (this pass)

- `./gradlew :common:compileJava` / `:fabric:compileJava` / `:forge:compileJava` /
  `:neoforge:compileJava` → **exit 0** (only pre-existing warnings).
- `./gradlew :common:test` → green, including 15 new phone tests (store NBT round-trip, payload
  codec round-trip, variant-key round-trip, weighted-pick bands, dig guarantee incl. forced-last).
- mc-test registry assertions added (`pokedoll_phone` item, `dig_site` block, `dig_site` item
  negative control). **NOT play-tested in-game.**

## Pokedoll Phone play-test round 1 (2026-07-08)

Play-test feedback: the reveal doll rendered in the mound's corner and stayed mostly buried at the
final stage; sites should re-cover themselves like vanilla archaeology; chat messages should not
call dolls "... Pokedoll".

### Fix — reveal item centred and seated by its own model bounds

- **Location:** `DigSiteBlockRenderer` (rewritten), `DigSiteBlock.surfaceHeight`,
  `DigSiteBlockEntity.easeRenderY`.
- **Reasoning:** the old renderer compensated for GeckoLib's `(0.5, 0.51, 0.5)` post-translate but
  missed vanilla `ItemRenderer`'s `(-0.5, -0.5, -0.5)` pre-translate that cancels it (the exact
  cancellation `HeadFit.headPoseOps` documents) — so the doll's origin landed at 0.25/0.25, the
  corner. Now: centred at 0.5/0.5, stable position-hashed yaw, and seated so a per-stage fraction
  of the item's OWN idle-pose height (`DollShapes.modelBounds`, gigantic ×1.5 accounted) shows
  above the mound surface: `EMERGE_FRACTION = {-0.15, 0.25, 0.55, 0.85}` (tucked under → head
  peeking → mostly out). Height changes ease over ~1s (client-only state on the block entity) so
  scoops and refills read as rising/sinking, not teleporting.
- **Confidence:** high on the centring math (mirrors HeadFit's documented offsets); medium on the
  emerge fractions + ease rate reading well. **Needs human:** re-play-test the reveal.

### Fix — mounds slowly re-cover themselves (vanilla brushable reset)

- **Location:** `DigSiteBlock.tick` + `COVER_GRACE_TICKS=100`, `COVER_INTERVAL_TICKS=60`;
  scheduled from `DigQuestManager.tryDig` (guarded by `hasScheduledTick`), last-scoop time on
  `DigSiteBlockEntity` (transient, like vanilla `BrushableBlockEntity`'s cooldowns).
- **Reasoning:** vanilla-archaeology parity via scheduled ticks (zero idle cost): 5s untouched →
  one stage refills every 3s with dirt place-sound + particles, fully buried again ~14s after
  abandonment. The reveal item is deliberately NOT cleared at stage 0 so the sink animation plays
  out; every scoop refreshes it anyway. Decay only lowers STAGE — quest resolution still happens
  solely on the final scoop, and a pending tick on a removed mound no-ops (block mismatch).
- **Confidence:** high mechanically; timing values are taste. **Needs human:** pacing feel.

### Fix — phone messages drop the "Pokedoll" word

- **Location:** `PhoneCalls.dollName` now uses `PokedollItem.displayName(stack, false)` (the
  call screen's existing suffix-free path) instead of `getHoverName()`.
- **Confidence:** high.

### Verification performed (this pass)

- `./gradlew :common:compileJava :fabric:compileJava :forge:compileJava :neoforge:compileJava
  :common:test` → **exit 0**, tests green (only pre-existing warnings). **NOT play-tested yet.**

## Pokedoll Phone — attunement / lost-doll rarity / durability (2026-07-09)

Spec: the recipe should attune the phone to the centre doll (only that doll calls it); a call should
announce the **rarity** (or a rarity **percent range**) of the doll the caller lost, coloured by that
rarity — picked as same-rarity / one-below / a scaling percent window / rarely one-above; and phones
should have configurable durability (default 8) spent per accepted call.

### Assumption — attunement rides the recipe, unattuned phones fall back to random

- **Location:** `PokedollPhoneRecipe` (new serializer `pokeblocks:pokedoll_phone`), custom_data key
  `phone_attuned`; `PokedollPhoneItem.inventoryTick` uses it, else `PhoneCalls.pickCallerKey`.
- **Reasoning:** a vanilla shaped recipe can't copy the doll variant onto the result, so the recipe is
  code-driven like `GiganticDollRecipe` (fixed 3×3 indices; `CraftingInput` trims to bounding box).
  Creative/`/give` phones have no attunement, so keeping the random-caller fallback leaves them usable.
- **Confidence:** high (compiles all loaders). **Needs human:** confirm a phone should attune to the
  doll's *exact* variant incl. flags (assumed yes) rather than the base species.

### Assumption — lost-doll rarity option weights + percent-window scaling

- **Location:** `PhoneCalls.pickLostDoll` / `percentRange` / `stepRarity`; constants
  `LOST_WEIGHT_SAME=45`, `LOST_WEIGHT_BELOW=30`, `LOST_WEIGHT_PERCENT=30`, `LOST_WEIGHT_ABOVE=6`,
  `LOST_PERCENT_RANGE_FRACTION=0.35`.
- **Reasoning:** spec ordered the four possibilities with "above" explicitly rare, so above gets a
  small weight and the other three are comparable. The percent window is **proportional** to the
  caller's own rarity percent (`callerPercent × (1 ± 0.35)`), which is the simplest reading of "scaling
  such that rarer dolls have a smaller range" — a rarer caller (smaller %) gets a narrower absolute
  window. Only options with a non-empty MATCHING pool are kept, so the announcement is always
  fulfillable; the buried doll (`pickBuriedDollKey`) is then drawn to match the descriptor, falling
  back to the full pool if somehow empty. The window shows as a compact `min% – max%` pair (two numbers)
  on the call page, tinted with the caller's rarity colour.
- **Confidence:** medium (weights + 0.35 are taste values, easy to retune; the pure math is unit-tested).
  **Needs human:** whether the split "feels" right in play, and whether the percent-window colour should
  be the caller's rarity (chosen) vs. a tier derived from the midpoint.

### Assumption — "the book" = the incoming-call popup page (not the compendium)

- **Location:** `PhoneCallScreen` (`screen.pokeblocks.phone.call_text` now takes the coloured
  descriptor); the same descriptor also appears in the incoming chat line and `quest_started`.
- **Reasoning:** the percent range is a per-call property, so it belongs on the call popup (which
  reuses the compendium's parchment "book" panel), not the doll-centric compendium. The two numbers
  keep the line short as requested.
- **Confidence:** medium-high (wording is unambiguous in context). **Needs human:** confirm placement.

### Assumption — durability spent only when an accepted call starts a dig

- **Location:** `PokedollPhoneItem.createAttuned` (MAX_DAMAGE from `[phone] durability`, default 8;
  item baseline `.durability(DEFAULT_DURABILITY=8)`), `spendDurability` = `ItemStack.hurtAndBreak(1,…)`
  charged in `PhoneCalls.handleCallResponse` after `startQuest` succeeds.
- **Reasoning:** spec says "each time a phone call is accepted"; charging only when a quest actually
  starts means hanging up, a call that already ended, or a fizzle for want of digging ground costs
  nothing — the fairer reading. `hurtAndBreak` handles the break (shrink + `ITEM_BREAK` sound +
  `message…broke`) and no-ops in creative. Durability set at CRAFT time so admins can retune without a
  restart affecting existing phones.
- **Confidence:** high mechanically. **Needs human:** confirm charge-on-quest-start vs. charge on any
  Accept press.

### Verification performed (this pass)

- `./gradlew :common:test` → green (12 phone tests, incl. new `stepRarity` / `percentRange` /
  `formatChance` coverage); `:fabric:compileJava :forge:compileJava :neoforge:compileJava` → **exit 0**.
- **NOT play-tested in-game.**

### Assumption — phone rarity scales dig sites & guarantee, +1 per tier by default (2026-07-09)

- **Location:** `DigQuestManager.startQuest` (scales `getPhoneDigSites()` / `getPhoneGuaranteedAttempts()`
  by `PhoneCalls.rarityTierBonusSteps(callerRarity) ×` the new `[phone] dig_sites_per_rarity` /
  `guaranteed_attempts_per_rarity`, both default 1); `PhoneCalls.rarityTierBonusSteps` (Common=0 …
  Gigantic=6, 0 off-ladder).
- **Reasoning:** spec = "each rarity tier of the crafted phone adds 1 to the dig sites and 1 to the
  guaranteed count, configurable." The phone's rarity IS its attuned doll = the caller, so the bonus is
  derived from the caller's resolved rarity in `startQuest` (no new plumbing). Common is the baseline
  (+0); each step up the COMMON..GIGANTIC ladder adds the per-tier amount. `0` disables scaling
  (parsed with a new `parseNonNegativeInt` since 0 is meaningful here, unlike the strictly-positive
  base counts).
- **Confidence:** high mechanically (compiles, tier math unit-tested). **Assumption flagged:** Common
  adds +0 (tiers *above* Common), not +1; and for an admin `/pokeblocks phonering` with a forced caller
  the bonus follows that caller, not the phone's attunement (matches attuned phones in normal play).
  **Needs human:** confirm the +0-at-Common reading and that a bigger hunt for rarer phones feels right.

### Fix — unattuned phones no longer ring with a random caller (2026-07-09)

- **Symptom:** a phone "attuned to furret" rang with random dolls. Diagnosed by scanning the running
  NeoForge dev world's `playerdata/*.dat` (gzipped NBT): the player's phones had **no** `phone_attuned`
  / `custom_data` / `max_damage` — i.e. plain phones from the pre-attunement shaped recipe, which were
  hitting the random-caller fallback I'd added for unattuned phones.
- **Location:** `PokedollPhoneItem.inventoryTick` — removed the `pickCallerKey` fallback; a phone with
  no `phone_attuned` now simply never starts a call. Unattuned tooltip line (`...unattuned`, red) added.
  `PhoneRingCMD` no-arg now prefers the phone's attunement before random. `pickCallerKey` retained
  (only the force-ring command uses it).
- **Reasoning:** the feature is "get called ONLY by the attuned doll"; a random fallback contradicts
  that and is what surprised the user. Freshly-crafted phones on the current build ARE attuned (verified
  the recipe/`createAttuned` write both `phone_attuned` and `max_damage`), so they ring only their doll;
  legacy/creative phones stay silent until (re)crafted.
- **Confidence:** high (root cause confirmed from live save NBT; compiles + tests green). **Needs human:**
  re-craft a phone on the current build and confirm it now only ever rings its own doll.
- **⚠ Superseded 2026-07-09 (same day, later pass):** the loot-drop request below makes unattuned phones
  obtainable as chest loot, and a phone that can never ring is a dead drop — the random-caller fallback is
  **deliberately reinstated**, now clearly labelled ("Not attuned — strange dolls may call…", grey not red).
  The original symptom (a phone *believed* attuned ringing randomly) can't recur for newly-crafted phones —
  the recipe attunes them — but pre-attunement legacy phones will start ringing randomly again. Judged
  acceptable: the tooltip now says so, and re-crafting fixes it.

---

## Phone rebalance + loot drop + figurine entity expansion (2026-07-09, second pass)

Spec (Shawn): lower the phone's rarity payout to roughly ONE same-or-higher-rarity doll per phone with
durability lowered to 4; add the unattuned phone to loot tables as a rare drop (with config); review the
dig-sites-per-rarity scaling; phrase the percent window as "<range> rarity". Figurines: memorial revivals
wander a configurable radius instead of following; honeycomb turns a figurine entity into a placeable,
poseable boxless "figurine doll"; tamed figurines visibly hold their taming doll; owners can equip them
with weapons/armor (real stats, weapon-only strike animation, doll moves to the off-hand side).

### Assumption — "likely 1 same-or-higher doll per phone" ⇒ P(same-or-higher) ≈ 25% per call

- **Location:** `PhoneCalls` — `LOST_WEIGHT_SAME=12`, `LOST_WEIGHT_BELOW=75`, `LOST_WEIGHT_PERCENT=10`,
  `LOST_WEIGHT_ABOVE=3` (was 45/30/30/6); `PokedollPhoneItem.DEFAULT_DURABILITY=4`; config
  `[phone] durability` default/reset/fallback all 8→4.
- **Reasoning:** the percent window centres on the caller's own percent, so it counts as "same-ish";
  SAME+PERCENT+ABOVE = 25/100 ⇒ expected same-or-higher payouts over a 4-call phone = **1.0** exactly
  (P(at least one) ≈ 68%). "Below" becomes the everyday outcome, which is the requested "lower the rarity
  capabilities". Old configs with an explicit `durability = 8` keep 8 (only the default changed).
- **Confidence:** medium (weights are taste); the arithmetic is deliberate. **Needs human:** payout feel.

### Assumption — phone loot drop = its own pool at `[phone] loot_drop_chance` (default 0.03)

- **Location:** `LootInjector.buildPhonePool` (+ `poolsFor` adds it beside the default doll pool),
  `PokeblocksCommon.getPhoneLootPool` (cached, invalidated with the loot map), new config key
  `[phone] loot_drop_chance` (0 disables). The pool sets `MAX_DAMAGE` from `[phone] durability` so loot
  phones match crafted ones even if the config changed after registration.
- **Reasoning:** a separate single-entry pool (not an entry inside the doll pool) makes "rare drop"
  directly tunable as chest-chance, targets the same `[loot] loot_tables` list, and rolls independently
  of the 33% doll pool. 3% ≈ one phone per ~33 opened chests — "rare" without being mythical.
- **Confidence:** high mechanically. **Needs human:** drop-rate taste; see the reinstated-fallback note above.

### Review — dig-sites-per-rarity scaling verified correct; placement attempts now scale

- **Findings:** the tier math (`rarityTierBonusSteps`, Common=0 … Gigantic=6 × `dig_sites_per_rarity` /
  `guaranteed_attempts_per_rarity`) is correct and unit-tested; the ladder matches `DollRarity` sort
  order. Two real defects fixed: (1) `generateSites` capped at a fixed 250 placement attempts, so a
  high-tier phone's larger site count could silently under-place on rough terrain — now
  `max(250, 40×count)`; (2) config parse fallbacks disagreed with the reload defaults
  (`site_radius` 64→32, `guaranteed_attempts` 5→3) — aligned.
- **Confidence:** high.

### Percent window now reads "<min>%–<max>% rarity" (doll-lore phrasing)

- **Location:** `PhoneCalls.describeLostDoll` PERCENT branch (+ 2 unit tests). Flows into the call
  popup, the incoming chat line and `quest_started` unchanged (they all take the descriptor arg).

### Assumption — memorial figurines wander via vanilla home restriction

- **Location:** `FigurineEntity.setMemorialAnchor` (`restrictTo(pos, [figurine] memorial_wander_radius)`,
  default 16, saved as `memorial_anchor` and re-applied on load so config changes take effect);
  `FollowOwnerGoal` subclass gated on `!isMemorial()`; new `MoveTowardsRestrictionGoal` walks strays
  back; anchor set in `PokedollBlock.setPlacedBy` for memorial placements.
- **Reasoning:** vanilla stroll goals already honour `restrictTo` (`GoalUtils.isRestricted`), so the
  radius needs no custom AI; the sit toggle is untouched (still tamed/owned). The restriction is
  re-derived from the anchor on load because vanilla never persists `restrictTo`.
- **Confidence:** high mechanically. **Needs human:** whether combat should also be leashed to the
  radius (currently a fight can drag it out; it walks back after).

### Assumption — honeycomb ⇒ boxless figurine doll reuses the EXISTING figurine block/item

- **Location:** `FigurineEntity.convertToBoxlessDoll` (returns gear + gifted doll first, keeps the
  custom name, WAX_ON sound/particles); `PokeblocksItemData.KEY_BOXLESS` (+`figurineTag` overload);
  `FigurineBlockEntity` gains `boxless` / `pose` / `rotation16` (synced, canonical-minimal NBT;
  boxless survives break→place, pose deliberately resets); `FigurineBlock.setPlacedBy` stores the
  placer-yaw 16-segment; `useWithoutItem` sneak-cycles `FigurinePose` (STANDING→SITTING→WALKING→
  STRIKING), plain click squeaks; `DollShapes.figurineBoxless` (figure-only square-footprint box, so
  one shape serves all 16 rotations); renderers skip the `box` bone and apply the pose — cube-level in
  `FigurineBlockRenderer` (rigid figures, via the extracted `FigureSwingPlans`), bone-level in
  `FigurineModel` (real limb bones), item-side skip in `FigurineItemRenderer`.
- **Reasoning:** a NEW block/BE/item would need per-loader renderer registration, datafixer surface and
  registry churn; storing the fine rotation on the BE (not a blockstate property) leaves existing
  worlds' FACING untouched. Pose cycling on sneak+empty-hand is the only reachable sneak-use path
  (vanilla skips block interaction when sneaking WITH an item). Shears still free the figure —
  honeycomb↔shears mirrors vanilla wax-on/wax-off. Poses mirror the entity's four procedural states
  since figurines ship no animation files ("any of its animation states" = these).
- **Confidence:** medium-high (compiles everywhere; render-transform signs are the usual play-test
  risk). **Needs human:** pose look per model family, hitbox feel, shears-frees-it confirmation.

### Assumption — held doll rendered anchored to the hitbox, not bones

- **Location:** `FigurineEntity`: synced `DATA_GIFTED_DOLL`; `FigurineEntityRenderer.renderHeldDoll`
  draws it cradled at a hitbox-derived hand anchor (front-right edge, ~38% height), rolled 55° across
  the chest + leaned back 20° so it sits IN the grip instead of standing upright beside the figure.
- **Reasoning:** hitbox anchors are the only universal attachment on rigid one-bone figures; the
  rotations pivot at the anchor so the doll's base stays in the "hand" while its top lies across the
  arm. Anchor fractions / angles / 0.4 scale are declared play-test knobs at the top of the renderer.
- **Confidence:** medium on the exact angles (signs reasoned, not seen). **Needs human:** in-game look.

### ⚠ Removed after play-test — owner-given weapons/armor (2026-07-09, same day)

- **What was removed:** the entire equip-on-right-click feature from the pass above (armor to slots,
  damage-items to main hand, stat attributes, weapon-only swing, helmet/chest/legs/boots trinket
  rendering). Shawn's verdict after seeing it: "it looks wrong."
- **Location:** `FigurineEntity` (equip branch + `equipSlotFor`/`addsAttackDamage`/`equipFromOwner`/
  `isArmed` deleted; `convertToBoxlessDoll` no longer strips gear), `FigurineEntityModel` /
  `FigurineEntityRenderer` (armed gating reverted, weapon+armor rendering deleted). The strike
  animation is back to the always-on whole-body pose.
- **Note:** figurines never had a way to acquire equipment besides this feature, so no live-world
  cleanup path is needed; a dev-world figurine equipped during testing would still hold NBT equipment
  but nothing renders or drops it beyond vanilla Mob behavior.

### Verification performed (this pass)

- `./gradlew :common:compileJava :common:test :fabric:compileJava :forge:compileJava
  :neoforge:compileJava` → **exit 0**, tests green (incl. 2 new descriptor tests).
- **NOT play-tested in-game.**

---

## Trapped (explosive) dolls + strawberr1shake gag (2026-07-10)

Spec (Shawn): doll + TNT ⇒ "trapped" doll — places normally but explodes (TNT-sized, WITH block
damage) when *popped*; throwable dolls craftable with TNT too; a thrown explosive doll that hits a
player replaces their helmet (helmet must fit an open inventory slot, else the doll falls to the
ground) and starts a 10-second countdown; the wearer sees a rapid red shaking on-screen timer;
other players see the countdown above the carrier's head (inventory or head); walking over a
counting-down ground doll re-attempts the helmet swap; at zero → entities-only TNT-sized blast, and
if it was in a player's inventory it kills them with a custom death message. Separately: shearing
the strawberry figurine free shows it for 3 s, then it explodes (damaging nothing) and dies.

### Assumption — "popped" = the spam-click break; waxing disarms; mining returns the armed doll

- **Location:** `PokedollBlock.useWithoutItem` (the existing `recordClick()` break path branches to
  `TrappedDolls.detonateBlock` — `destroyBlock(pos, false)` + `explode(…, 4.0f, TNT)`);
  `PokedollBlockEntity.trapped` (saved, server-only); `PokedollBlock.setPlacedBy/getDrops/
  getCloneItemStack` (marker → BE on place, BE → marker on drop/pick).
- **Value set:** power `4.0f` = one vanilla TNT; `ExplosionInteraction.TNT` (block damage, tnt drop
  decay); popping is the ONLY block-side trigger. Mining drops the doll still trapped; a **waxed**
  trapped doll can never pop (recordClick refuses) so honeycomb effectively disarms the block.
- **Reasoning:** "popped" is this codebase's established term for the 9-rapid-clicks break
  (`BREAK_CLICK_THRESHOLD`, `SUBSTITUTE_POP_CHANCE` "pop" naming, `[dolls] popping` config). The
  wax interaction falls out of the existing guard and reads as a sensible counter-play rather than
  a bug. Explosions do NOT chain-detonate other placed trapped dolls (they just drop, armed).
- **Confidence:** high on the trigger reading. **Needs human:** whether wax-disarm + no chain
  reaction match the intent.

### Assumption — trapped marker mirrors ThrowableDolls (custom_data), explicitly bridged through the BE

- **Location:** `item/TrappedDolls` (`pokeblocks_trapped` bool + `pokeblocks_detonate_at` long in
  `minecraft:custom_data`); `recipe/TrappedDollRecipe` (+ serializer registration + recipe json);
  name prefix "Trapped", red tooltip "Pops with a bang" (`PokedollItem`).
- **Reasoning:** exact parity with the throwable marker keeps doll identity (species/flags/rarity/
  compendium key) untouched and the two recipes compose in either order (both `copyWithCount`
  everything). Because the BE round trip never copies custom_data (the memorial-doll precedent),
  `setPlacedBy` hands the flag to the BE and `getDrops` hands it back. The BE flag is deliberately
  absent from `getUpdateTag`/`saveToItem`, so clients can't sniff placed traps and ctrl+pick copies
  are unarmed. The "Trapped" name/tooltip DOES reveal a trapped item in hand — consistent with
  "Throwable", and judged better than enabling scam trades.
- **Confidence:** high (mirrors two shipped markers). **Needs human:** whether trapped dolls should
  be visually indistinguishable in inventory instead.

### Assumption — countdown state lives on the stack; server scan + ItemEntity mixin cover every home

- **Location:** `TrappedDolls.hitPlayer/equipOnHead` (called from `ThrownPokedollEntity.onHitEntity`
  for players — force-replaces the helmet via `Inventory.getFreeSlot()`, else drops the doll ticking);
  `trapped/TrappedDollCountdown.serverTick` (from the existing `ServerTickMixin`: scans items/armor/
  offhand of every online player per tick — component-presence check per stack, ~free when idle);
  `mixin/TrappedDollItemEntityMixin` (ground tick + walk-over forced head-equip; vanilla pickup of a
  ticking doll is always cancelled); `PokedollItem.useOn` FAIL for ticking stacks (placing would
  consume the countdown = one-click defuse).
- **Value set:** deadline = absolute game time (`gameTime + 200`); at zero → `explode(…, 4.0f,
  NONE)` at the carrier's chest with the custom damage source, then — inventory case only — a
  `Float.MAX_VALUE` finisher with the same source (blast first so death drops aren't vaporized;
  finisher because armor/protection could otherwise survive the blast). Ground detonation = blast
  only, per spec. Totems can still save the wearer (left as legit counter-play).
- **Known escape hatches (accepted, documented):** stashing the ticking doll in a container/ender
  chest or holding it on the inventory cursor pauses the scan — but the deadline is absolute, so it
  detonates the instant it re-enters a scanned slot or the ground; hoppers can swallow the ground
  ItemEntity (frozen until extracted); Curios/Trinkets accessory slots are not scanned.
- **Confidence:** high mechanically. **Needs human:** whether the stash-pause loopholes need
  closing (e.g. also scanning open containers), and the kill-through-totem question.

### Assumption — timer UX: HUD from the synced head stack, overhead via a broadcast payload

- **Location:** `client/trapped/TrappedDollHudRenderer` (red `%.2f`s at ~28% screen height, scale 3,
  sine-alias shake ramping 1→7 px, white flash under 3 s) hooked by `mixin/TrappedDollHudMixin` at
  the TAIL of `Gui#render`; `TrappedDollOverheadRenderer` (vanilla-parity nametag billboard +0.35
  above the name, see-through + normal passes, 64-block gate) hooked by `TrappedDollNameTagMixin`
  at the TAIL of `PlayerRenderer#render`; `trapped/TrappedDollPayloads.TimersPayload`
  ("trapped_doll_timers", byte-blob NBT via `TrappedDollTimersCodec`, full snapshot every 10 ticks
  + one clearing empty, 3 s client staleness) handled by the server-safe `ClientTrappedDollTimers`.
- **Reasoning:** the wearer's HUD needs no networking (armor syncs to the wearer); the overhead case
  DOES (inventory contents never sync to other clients), hence the broadcast — optional-channel on
  all three loaders like the phone/compendium payloads (Forge gets its own `trapped_sync` channel;
  member lists of shipped channels must not change). The HUD hook is a **common Gui mixin instead of
  per-loader events** because Forge 52/1.21.1 ships NO gui-render event and no `ForgeGui` any more
  (verified against forge-1.21.1-52.0.24: `net.minecraftforge.client.event` has no RenderGuiEvent;
  vanilla `Gui.class` is used directly) — one TAIL injection fires exactly once on all loaders.
  Overhead rendering hooks `render`, not `renderNameTag`, so a hidden name tag can't hide a bomb;
  it also ignores sneaking for the same reason.
- **Confidence:** high for Fabric/NeoForge, medium-high for the Forge Gui-mixin claim (grounded in
  the jar listing, not a run). **Needs human:** HUD position/scale taste; shake feel; whether the
  own-player overhead in F5 should be hidden.

### Assumption — custom death message via a datapack damage type; blast itself uses the same source

- **Location:** `data/pokeblocks/damage_type/trapped_doll.json` (`message_id
  pokeblocks.trapped_doll`, scaling `always`, exhaustion 0.1); lang
  `death.attack.pokeblocks.trapped_doll` = "%1$s hugged a trapped Pokedoll a moment too long";
  `TrappedDolls.damageSource` (falls back to the plain explosion source if the datapack entry is
  missing, so detonation can never crash).
- **Reasoning:** damage types are data-driven in 1.21.1 — no code registration needed on any loader.
  Passing the same source INTO the explosion means bystanders killed by the blast get the doll
  death message too (judged a feature); no attacker credit is tracked (the thrower is not carried
  on the stack), so the base message key always applies.
- **Confidence:** high. **Needs human:** message wording.

### Assumption — strawberr1shake gag: cosmetic-only bang, normal death (item DROPS back)

- **Location:** `FigurineBlock.useItemOn` shear path (`figurine.startExplosionFuse(60)` when the
  freed id equals `ModSettings.EXPLODING_FIGURINE = "strawberr1shake"`); `FigurineEntity`
  (`explosionFuse` int, TNT_PRIMED on ignite, smoke every 5 ticks, at zero EXPLOSION_EMITTER
  particle + GENERIC_EXPLODE sound and `kill()`; fuse persisted as `explosion_fuse`).
- **Reasoning:** "the strawberry figurine" resolves to `strawberr1shake` (the only strawberry-named
  figurine in `figurine_names.json`). "Shouldn't damage any blocks or any other entities" ⇒ NO real
  `Explosion` at all — sound + particle only. "Die" is read as the entity's normal death, and for
  this mod a figurine's death *returns its figurine item* (`dropCustomDeathLoot`) — chosen over
  silently destroying an epic-rarity item on an unannounced first shear; the gag is the jump-scare,
  not the loss. Applies to every shear-release (boxed or boxless), but not to command/memorial
  spawns.
- **Confidence:** high on the id; medium on drop-vs-destroy. **Needs human:** confirm the item
  should survive the gag (deleting it instead is a one-line change: `discard()` for `kill()`).

### Verification performed (this pass)

- `./gradlew :common:test :fabric:compileJava :forge:compileJava :neoforge:compileJava` →
  **BUILD SUCCESSFUL**, tests green (incl. new `TrappedDollTimersCodecTest`).
- **NOT play-tested in-game** (HUD shake, overhead placement, explosion feel, Forge Gui mixin, and
  the full throw→helmet→boom loop all need a live pass).

---

## Squeak textures for dolls  ✅ added

Request: *"add support for uploading 'squeak' textures for dolls which will replace the regular
texture when the doll is being squeaked ... keep all flag matching in mind ... also allow adding a
numeric order to textures, ie squeak_1, squeak_2, squeak_3 ... replaced in that order each time
they get squeaked."*

### Assumption 1 — the `_squeak` marker goes AFTER the flag suffixes

- **Location:** `PokeblocksAssetResolver.resolveSqueakFramePaths` /
  `pokedollSqueakTextures`; `PokemonRegistry.SQUEAK_TEXTURE_PATTERN`.
- **Value set:** `pokedoll_<name><flagSuffixes>_squeak[_<n>][_texture].png`, e.g.
  `pokedoll_eiscue_noice_shiny_squeak_2_texture.png`.
- **Reasoning:** the flag suffixes are what the existing texture ladder strips off the end, so
  putting the marker last keeps a squeak file attached to exactly one variant and lets the same
  subset/permutation ladder (`subsetsLargestFirst` + `permutedSuffixes`) match it — the request's
  "keep all flag matching in mind". A marker placed *before* the flags would need a second,
  divergent parser. Consequence: a squeak texture inherits the regular texture's fallback
  behavior, so a single `pokedoll_<name>_squeak_texture.png` covers every variant while a
  flagged one overrides it for that variant only.
- **Confidence:** high. **Needs human:** none.

### Assumption 2 — numbered frames run from 1, contiguously, and win over the unnumbered file

- **Location:** `PokeblocksAssetResolver.resolveSqueakFramePaths` (`MAX_SQUEAK_FRAMES = 64`).
- **Value set:** probe `_squeak_1`, `_squeak_2`, ... and stop at the first miss; use that list if
  non-empty, else a lone `_squeak`. Cycle with `floorMod(squeakIndex, frames.size())`.
- **Reasoning:** the request names `squeak_1, squeak_2, squeak_3`, so 1-based. A directory listing
  isn't available through `ResourceManager` for an arbitrary name, so the count has to be probed;
  stopping at the first gap keeps that bounded and deterministic. Mixing a numbered sequence with
  an unnumbered file is ambiguous, so the explicit (numbered) one wins. The uploader warns about
  gaps, duplicates and mixed forms so a mistake is caught before it ships.
- **Confidence:** high on 1-based/contiguous; medium on numbered-beats-unnumbered (an author who
  ships both probably meant the sequence). **Needs human:** none unless that preference differs.

### Assumption 3 — the swap lasts the whole squish, one frame per squeak

- **Location:** `PokedollBlockEntity.isSqueaking()` / `getSqueakIndex()` (counter incremented in
  `triggerSquish`, wrapped at 2^16, persisted and included in `getUpdateTag`);
  `PokedollModel.getTextureResource`.
- **Value set:** the squeak texture is shown for the `SQUISH_DURATION_TICKS = 8` the squish
  animation already runs for, and the frame advances once per squeak (not per tick).
- **Reasoning:** "when the doll is being squeaked" is the squish window the mod already models, so
  it reuses `squishStartTick` rather than inventing a second timer. "Replaced in that order each
  time they get squeaked" reads as one frame per squeak, not an animation within one squeak. The
  index is synced (like `squishStartTick`) so every player watching a doll sees the same frame.
- **Confidence:** high. **Needs human:** whether a multi-frame sequence should instead animate
  *within* a single squeak — that would be a small change in `getSqueakIndex`'s caller.

### Assumption 4 — squeak textures are invisible to the registry

- **Location:** `PokemonRegistry.parseTextureCombos` → `isSqueakTexture`.
- **Value set:** squeak textures are skipped by the doll scan entirely.
- **Reasoning:** a squeak texture re-skins a variant that already exists. Letting it through would
  invent a pokemon (`pikachu_squeak_2`) or mark a flag combination as having a valid texture, which
  would offer a creative-tab/compendium variant the game can't actually render. Also keeps them out
  of `doll_rarity.json` prompts in the uploader. They are still shipped in the served resource pack
  (the pack builder copies textures by name and does not consult the registry).
- **Confidence:** high. **Needs human:** none.

### Scope

Pokedoll **blocks** only. Held/inventory/head-worn/thrown dolls and figurines
(`FigurineBlock`/`FigurineEntity`, which also play the squeak sound) keep their regular texture —
they have no squish window to key the swap off.

### Verification performed (this pass)

- `./gradlew :common:test` → **161 tests green**, incl. the new `registry.SqueakTextureTest`
  (12 cases covering numbering, gaps, flag specificity, permuted flag order, shared fallback and
  registry exclusion).
- `./gradlew :fabric:build :forge:build :neoforge:build` → **BUILD SUCCESSFUL** on all three.
- `python tools/doll_uploader.py --self-test` → passes, incl. new squeak classification cases.
- **NOT play-tested in-game** — no built-in doll ships a squeak texture yet, so the visual swap
  needs a live pass with a custom pack (`/pokeblocks resourcepack saveexample` now writes
  `exampledoll_squeak_1/_2` for exactly this).
