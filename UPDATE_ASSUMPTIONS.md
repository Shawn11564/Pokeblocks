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
