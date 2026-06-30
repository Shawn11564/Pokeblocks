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
