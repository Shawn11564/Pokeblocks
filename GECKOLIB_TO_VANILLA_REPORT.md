# GeckoLib → Vanilla Datapack/Resource-Pack Converter — Feasibility & Design Report

*Date: 2026-07-08. Scope: evaluate a library/tool that converts GeckoLib models (bedrock-format
`.geo.json` + `.animation.json` + texture) into vanilla-client content — datapack + resource pack —
with a path to a server-side-only "parent" mod for Pokeblocks.*

---

## 1. Verdict

**Highly plausible, and proven in the wild.** The conversion domain is not novel — GeckoLib's
geometry format *is* the Bedrock geometry format, and two mature ecosystems already do this exact
translation:

- **ModelEngine** (Ticxo/MythicCraft, Paper/Spigot plugin): converts Blockbench/bedrock-format
  models into a generated resource pack and animates them server-side with display entities.
  Vanilla clients, no client mod. Runs on thousands of production servers.
- **Animated Java** (Blockbench plugin): exports rigged, animated Blockbench models to a
  **datapack + resource pack** pair driven entirely by `item_display` entities and mcfunctions.

So the question is not *can it be done* but *how much fidelity survives, at which Minecraft
version, and what architecture fits Pokeblocks*. Short answers:

| Question | Answer |
|---|---|
| Plausible? | Yes — existence-proofs above; our corpus (121 geo models) uses only convertible features |
| Minimum MC version (full fidelity) | **1.21.4** (RP format 46 / DP format 61) — see §3 |
| Absolute floor (world rendering only) | 1.19.4 (display + interaction entities) |
| Perfect 1‑1? | **Provably exact for anything placed in the world** (display-entity transform = arbitrary affine matrix). Item/GUI forms are exact only for models whose cubes reduce to single-axis ≤45° rotations; others need a baked sprite or approximation (§6) |
| Hard non-translatables | Partial-shape collision, runtime Molang state, GeckoLib render-layer hacks (incl. our z-fight depth bias), per-instance shader effects (§9) |

---

## 2. Format gap analysis — what maps to what

GeckoLib model = bedrock geometry: bones (pivot + 3-axis Euler rotation, ZYX application order,
degrees, nested), cubes (origin/size in 1/16-block units, optional per-cube pivot+rotation,
`inflate`, `mirror`, box-UV or per-face UV), one texture per model, animations as per-bone
keyframe channels (rotation/position/scale) with optional Molang expressions.

Vanilla has **two** rendering vehicles, and the tool must use both:

**A. JSON block/item models** (`assets/<ns>/models/`): axis-aligned elements `from`/`to` in
0–16 space, coordinates clamped to **[-16, 32]** (a 3×3×3-block envelope), **one rotation per
element, single axis, ±45°** (arbitrary angle since 1.21.4; 22.5° steps before), per-face UV
`[u1,v1,u2,v2]` in 16-space with 0/90/180/270 face rotation, `tintindex` per face.

**B. Display entities** (1.19.4+): `item_display` renders any item model with a `transformation`
that is a **full 4×4 affine matrix** (internally decomposed as translation · left_rotation ·
scale · right_rotation — i.e. an SVD, so *any* affine linear part, including shear from nested
non-uniform bone scales, is exactly representable). Plus per-entity `brightness` override,
`view_range`, `shadow_radius`, `glow_color_override`, and keyframe-style client-side
interpolation (`interpolation_duration`, `start_interpolation`; `teleport_duration` since 1.20.2).
`interaction` entities (1.19.4) give click/attack detection AABBs.

Feature-by-feature:

| GeckoLib feature | Vanilla equivalent | Fidelity |
|---|---|---|
| Bone hierarchy, pivots, 3-axis rotation | Flattened world matrix per **rotation group** → display entity `transformation` | **Exact** (matrix is arbitrary affine) |
| Cube origin/size | Element `from`/`to` (coordinate-space conversion, §5) | Exact |
| Per-cube rotation (multi-axis, e.g. Luvdisc `[12.5,45,0]`) | Own rotation group → own display entity, or in-model element rotation when residual is single-axis ≤45° | Exact in-world; restricted in item form |
| `inflate` | Bake into `from`/`to` (±inflate each axis); UVs untouched — texel stretch matches GeckoLib behavior | Exact |
| `mirror` / negative UV | Swap face U coordinates (`u1↔u2`) | Exact |
| Box-UV | Expand to 6 per-face UVs using the bedrock box layout (note bedrock's up/down flip vs Java) | Exact |
| Per-face UV (`uv` + `uv_size`, incl. negative sizes) | Direct per-face UV; negative size = coordinate swap | Exact (only `pokedoll_skibidi_mewlet` uses this today) |
| Texture (single per model) | `textures` map + `#0` on all faces; px→16-space scaling (§4) | Exact, texel-perfect |
| Animated textures (mcmeta flipbook) | Same `.mcmeta` format — copy through | Exact |
| Emissive/glow layers | Split glowing cubes into a separate display entity with `brightness:{block:15,sky:15}` | Very close (uniform full-bright vs shader glow) |
| Per-bone/instance color | `tintindex` + item model definition tint sources; per-instance via `custom_model_data` colors component (1.21.4) | Exact RGB (§7) |
| Keyframe animations (all ours are linear) | Pre-sampled transforms + display interpolation | Near-exact (§8) |
| Molang (ours: only `query.anim_time`, `math.sin/cos`) | Pure functions of time → pre-bake by sampling | Exact at sample resolution |
| Molang with runtime state (`query.head_yaw` etc.) | Not representable statically | ✗ (not used in our corpus) |
| Geo-derived hitboxes | `interaction` entity AABB (clicks); **no partial collision** — only full barrier blocks | Partial (§9) |
| Scale-0 bones (phantom geo) | Cull cubes at bake time (we already do this in the voxelizer/GeoPose work) | Exact |

**Rotation-group algorithm** (the core trick): compute each cube's cumulative rotation
`R = R_bone-chain · R_cube` using the exact GeckoLib bone math (already replicated and
corpus-tested in our GeoPose/voxelizer code — reuse it, including its sign conventions).
Partition cubes into groups such that within a group, every cube's rotation equals
`R_group · R_residual` where `R_residual` is identity or a single-axis rotation ≤45°
(any angle on 1.21.4+, 22.5° multiples below). Each group becomes one generated item model
(residuals as element rotations) rendered by one `item_display` whose matrix is
`T_group · R_group · S_group`. Typical static doll: **1–4 display entities + 1 interaction
entity**. Luvdisc worked example: bone Y27.5° × cube rotations {Y45, X12.5·Y45, X12.5·Y-45} →
group 1 absorbs the body + two axis-aligned decals (residual Y45 stays in-model), the two fins
land in 1–2 more groups. Three entities total.

**Size cap handling:** element coords must fit [-16, 32]. Gigantic dolls exceed it → uniformly
scale the model geometry down to fit and multiply the display entity's scale back up. Lossless
(floats end-to-end).

---

## 3. Minimum Minecraft version

Feature ladder (Java Edition):

| Version | Packs (RP/DP) | What it adds for us |
|---|---|---|
| 1.19.4 | 13 / 12 | `item_display`, `block_display`, `interaction` entities; affine `transformation`; interpolation. **Floor for in-world rendering with exact rotations/angles.** |
| 1.20.2 | 18 / 18 | `teleport_duration` — smooth entity movement (moving/thrown composites) |
| 1.20.5 | — | Item components (`dyed_color` on any dyeable base item) |
| 1.21.2 | 42 / 57 | `minecraft:item_model` component (bind any model without CMD-override hacks); `equippable` component (survival head-equip for doll hats) |
| **1.21.4** | **46 / 61** | **Items model definitions** (`assets/<ns>/items/*.json`): `select` on `display_context`, `condition`, `range_dispatch`, `composite`; reworked `custom_model_data` (floats/flags/strings/**colors**); **tint sources** incl. `minecraft:custom_model_data` (arbitrary per-instance RGB on any item); element rotation angle restriction lifted (arbitrary angle, still single-axis ±45°) |
| 1.21.5 | 55 / 71 | Minor: more tint sources (`firework`, `team`), `component` select source — nice-to-haves |
| 1.21.6+ | 63+ / 80+ | `on_shelf` display context etc. — nothing load-bearing |

**Recommendation: target 1.21.4 as the "full support" minimum.** That is the first version where
rotations (arbitrary in-model angles), colors (per-instance RGB via CMD colors + tint source), and
clean item registration (`item_model` component + items definitions) are all present. Everything
below 1.21.4 forces legacy hacks:

- **1.21.1 (Pokeblocks' current target)** — worth a compatibility profile since our own servers run
  it. Degradations: item models bound via legacy `overrides`+`custom_model_data` float predicates
  on a base item; per-instance tint only via `dyed_color` on a **leather** base item
  (`leather_horse_armor` is the mapmaker standard); in-model residual rotations limited to 22.5°
  steps (→ slightly more display entities per static bake; in-world fidelity unaffected).
- 1.19.4–1.20.x — world rendering fine, item/color story painful. Not worth supporting unless a
  concrete need appears.

Ship version support as pluggable **target profiles** (`1.21.1-compat`, `1.21.4+`, `latest`), each
owning pack-format numbers, item-binding strategy, tint strategy, and rotation-angle granularity.

---

## 4. Texture pipeline — mapping the texture to the datapack "item"

A datapack alone **cannot** add textures/models — the tool must emit a **resource pack + datapack
pair**. (For Pokeblocks this is a feature, not a bug: the smart pack-serving pipeline already
builds and serves packs at runtime; the converter output slots straight into it.)

Per model `foo` with texture `foo.png`:

1. **Copy** `foo.png` → `assets/<ns>/textures/item/geo/foo.png` (plus `foo.png.mcmeta` if
   animated — the flipbook format is identical, pass through).
2. **Emit models** `assets/<ns>/models/item/geo/foo_g0.json` … `foo_gN.json` (one per rotation
   group), each with `"textures": {"0": "<ns>:item/geo/foo", "particle": "#0"}` and every face
   referencing `"texture": "#0"`.
3. **UV conversion** — geometry `description` gives `texture_width`/`texture_height` (px);
   bedrock UVs are in pixels, vanilla UVs in 0–16 space regardless of resolution:
   `u16 = u_px · 16 / texture_width`, `v16 = v_px · 16 / texture_height`. Floats are legal in
   model JSON, so **any texel rectangle maps exactly**, including non-square and odd-sized
   textures (Luvdisc is 32×32 → factor 0.5).
   - **Box-UV** expands to six faces via the bedrock box layout (U-major strip anchored at
     `uv:[u,v]`; sizes derived from cube size; bedrock's `up`/`down` faces are V-flipped relative
     to Java — flip by swapping V coords).
   - **`mirror: true`** → swap `u1↔u2` on each face.
   - **Per-face UV** with negative `uv_size` → coordinate swap on that axis.
4. **Item binding** (1.21.4 profile): emit `assets/<ns>/items/foo.json`:

   ```json
   { "model": { "type": "minecraft:select", "property": "minecraft:display_context",
       "cases": [ { "when": "gui", "model": { "type": "minecraft:model", "model": "<ns>:item/geo/foo_sprite" } } ],
       "fallback": { "type": "minecraft:composite",
         "models": [ { "type": "minecraft:model", "model": "<ns>:item/geo/foo_g0",
                       "tints": [ { "type": "minecraft:custom_model_data", "index": 0, "default": -1 } ] } ] } } }
   ```

   The in-game item is then just `/give @s minecraft:poisonous_potato[item_model="<ns>:foo",
   custom_model_data={colors:[16711680]}]` — base item choice is irrelevant on 1.21.4+.
   On the 1.21.1 profile this becomes `overrides` on `leather_horse_armor` keyed by CMD float,
   with an ID registry file so numbers stay stable across regenerations.
5. **GUI sprite fallback** — for models whose item form can't be exact (§6), generate a flat
   render (we already have `tools/gen_compendium_assets.py` producing exactly these doll renders)
   and route the `gui` context to it via the `select` above. Pixel-perfect icon, 3D everywhere
   the restrictions allow.

---

## 5. Coordinate & rotation math (reference)

- **Space conversion** (Blockbench-standard bedrock→Java, X axis is mirrored):
  `from = [8 − (origin_x + size_x), origin_y, origin_z + 8]`, `to = from + size`;
  rotation origin `= [8 − pivot_x, pivot_y, pivot_z + 8]`; under the X-mirror, X-axis angles keep
  sign, Y/Z-axis angles negate. **Do not re-derive these signs by hand** — reuse the GeoPose bone
  math that already passes the 121-model corpus, and let the golden-vertex tests (§6) catch any
  convention mismatch.
- **Euler order**: GeckoLib applies bone rotation Z, then Y, then X about the pivot; per-cube
  rotation likewise about the cube pivot. The rotation-group builder composes these into a single
  matrix per group.
- **Display transformation**: emit the composed group matrix directly as the 16-float
  `transformation` NBT array — the client performs the T·L·S·R decomposition itself; no quaternion
  math needed in the emitter, and shear (nested non-uniform scales) survives exactly.

---

## 6. Ensuring perfect 1‑1 translation

Split the guarantee into three tiers and *enforce each mechanically*:

**Tier A — provably exact (world placement).** For every cube, final vertex positions are
`M_display · convert(cube)` with float math end-to-end. Fidelity harness:
1. **Golden-vertex tests**: for each model in the corpus, compute the 8 corner vertices of every
   cube via (a) the existing GeoPose/GeckoLib-replica math and (b) the converter's
   emitted-model + emitted-matrix pipeline; assert `‖Δ‖ < 1e-4` blocks. This is the same corpus
   pattern as the voxelizer tests — extend that suite rather than building a new one.
2. **Texel assertions**: for each face, map its vanilla UV rect back to pixel space and assert it
   equals the bedrock-side texel rect exactly (integers/halves in, same out).
3. **Visual regression**: mc-test lane spawns the converted composite next to the GeckoLib
   original (modded client renders both), fixed camera + seed, screenshot, image-diff with a small
   per-pixel tolerance (lighting sampling differs slightly at entity vs block-entity anchor).
   Run per-model over the corpus in CI like the existing mc-test workflow.

**Tier B — exact-if-representable (item/GUI/hand/head forms).** A single JSON model cannot express
two-axis cube rotations. The converter computes an **item-form fidelity score** per model: if all
residual rotations are single-axis ≤45° → full 3D item model, exact. Otherwise choose per policy:
`sprite` (default for `gui`; pixel-perfect image, loses 3D), `approximate` (snap residuals; report
max angular error), or `fail`. Never silently approximate — see Tier C.

**Tier C — loud failure on the non-representable.** The converter ends every run with a
**fidelity report** (JSON + human-readable): per model, per feature, `exact | approximated(ε) |
unsupported`, and a strict mode that fails the build on anything below `exact`. This is the single
most important design rule: 1‑1 is "ensured" not by hoping the math is right but by making every
deviation a diffable, CI-gated artifact.

---

## 7. Colors

- **Per-face static tint**: `tintindex` on faces + `minecraft:constant` tint source (1.21.4).
- **Per-instance dynamic RGB** (dyeable dolls, team colors, rarity glints): set
  `custom_model_data={colors:[...]}` on the item stack carried by the `item_display`; the model's
  `minecraft:custom_model_data` tint source picks it up per `tintindex`. Multiple independent
  color channels per model = multiple tint indices. Full 24-bit RGB, changeable at runtime with a
  single `/data modify` or `/item` call — this exceeds what most modded tint paths offer.
- **Glow outline** (selection/laser-pointer-style highlights): `glow_color_override` + `Glowing`
  on the display entity — free extra feature, no pack support needed.
- 1.21.1 profile: one tint channel only, via `dyed_color` on a leather base item.

---

## 8. Animations

Our corpus is ideal: ~35 clips, ~30 are single-frame poses (static bake — zero runtime cost), all
keyframes linear (no `catmullrom`/easing found), and the 4 Molang clips use only
`query.anim_time` + `math.sin/cos` — pure functions of time.

Pipeline: **sample every animated rotation-group's world matrix at a fixed rate** (10–20 Hz;
configurable), then play back by writing `transformation` + `start_interpolation:0` +
`interpolation_duration:<step>` each sample — the client slerps/lerps between samples. Notes:
- Even "linear" bedrock keyframes need sampling: bedrock interpolates Euler channels
  componentwise, the client slerps quaternions — identical only for single-axis segments. At
  10–20 Hz the divergence is sub-perceptual; the fidelity report records the max deviation.
- Loops: bake one period (the sine bobs have exact periods) and restart.
- Datapack driver: generated functions with a tick scheduler keyed by entity tags + storage
  (this is precisely Animated Java's architecture — proven). Server-mod driver: just set the
  transforms from Java each N ticks — simpler, cheaper, and can interpolate adaptively.

---

## 9. Limitations & risks (the honest list)

1. **Collision**: display entities have none; `interaction` gives a single upright AABB for
   clicks/attacks only. Walk-through collision requires real blocks (barrier = full cube only).
   Ironically our geo-derived *single best-fit box* hitbox maps almost 1:1 onto an interaction
   entity for interaction purposes — but physical collision for sub-block dolls is lost. Decide
   per block: no collision (most dolls) vs barrier fill (gigantic dolls).
2. **Item-form rotation ceiling** (§6 Tier B) — multi-axis cubes can't be exact in GUI/hand/head.
3. **Z-fighting**: our GeoDepthBias fix is a client-side render hack; vanilla clients get raw
   geometry, so coplanar same-bone cubes will shimmer again. Mitigation at *bake time*: apply the
   same detection (we already have it) and nudge the emitted element coords by ~1e-3 — i.e. move
   the fix from render-time back to geometry-time, but only in the converted output.
4. **Entity count / server cost**: ~2–7 entities per placed static doll vs 1 block entity today.
   Fine for decorative densities (display entities are cheap client-side; no AI, no ticking
   logic), but a plaza with 500 dolls becomes ~2k entities — set `view_range`, consider merging
   groups aggressively, and document a budget.
5. **Runtime Molang / state-driven animation** (look-at-player, laser-pointer head tracking):
   impossible in a pure datapack at fidelity; possible from the server mod (it can compute
   per-player transforms), but per-viewer rendering doesn't exist — one shared pose for all.
6. **Lighting/AO differences**: entity light sampling vs block rendering; no smooth lighting/AO
   on display entities; face culling against neighbors doesn't happen. Visually minor for dolls,
   visible for tight-fitting "block-like" builds.
7. **Fragility of the entity composite** (datapack route): `/kill @e`, pistons pushing the space,
   water flow etc. can desync visual entity vs interaction entity. Needs an integrity tick
   function (cheap tag-scoped repair) — or the server mod, which owns lifecycle robustly.
8. **No datapack on Realms-like restrictions**: none relevant — datapacks + server resource packs
   both work on vanilla servers and Realms.

---

## 10. Architecture — library, CLI, and the Pokeblocks parent mod

Three modules, strictly layered; the top two have **zero Minecraft dependencies** so they run in
unit tests, build scripts, and inside the server at runtime:

```
geo2vanilla-core        pure Java library (this is "the tool")
 ├─ parser/       geo.json + animation.json + mcmeta → IR (reuse/extract the existing
 │                GeoPose parsing + bone math — it is already corpus-validated)
 ├─ ir/           Model → Bones → Cubes → Faces; Animations → sampled TransformTracks;
 │                public, stable, versioned — the extension surface
 ├─ bake/         rotation-group partitioner, static-pose baker, animation sampler,
 │                scale normalizer, z-fight nudger, box-UV expander
 ├─ emit/         ResourcePackEmitter (models, items defs, textures, sprites)
 │                DatapackEmitter (place/break/animate functions, advancements, loot)
 │                → both write deterministic output (stable ordering, no timestamps)
 │                  so the served-pack SHA stays reproducible
 ├─ profile/      TargetProfile SPI: 1.21.1-compat | 1.21.4 | latest
 └─ report/       FidelityReport (exact/approx/unsupported per model+feature)

geo2vanilla-cli         thin wrapper: `geo2vanilla bake --target 1.21.4 --strict in/ out/`
                        (usable by any third party — this is the community-facing tool)

geo2vanilla-runtime     server-side mod module (multiloader common, like the rest of Pokeblocks)
 ├─ composite/    spawn/remove/repose display+interaction composites; chunk persistence
 │                via entity tags + a small SavedData registry (same pattern as the
 │                phone-quest SavedData)
 ├─ anim/         tick driver playing sampled tracks (adaptive rate, pause when unseen)
 └─ api/          PlacedGeoModel handle: place(pos, model, pose), setColor(rgb),
                  playAnimation(id), remove()
```

**Why this shape:**
- *Ease of use*: the CLI covers the "just convert my model" user; the core library covers build
  pipelines (a Gradle task can bake packs at build time); the runtime covers servers.
- *Extension*: everything pluggable is an SPI — `TargetProfile` (new MC versions), `Emitter`
  (e.g. a future Bedrock-edition emitter), `FallbackPolicy` (sprite vs approximate vs fail),
  `BaseItemStrategy`. Same compileOnly-SPI pattern that worked for mc-test.
- *Determinism*: emitters must be byte-stable given identical input — the smart-pack-serving
  delta handshake keys on content hashes, so nondeterministic output would resend packs.

**Pokeblocks integration — the parent server-side mod.** The flagship use case: **mixed-client
servers**. Pokeblocks already (a) builds and serves resource packs at runtime, (b) knows every
placed doll, (c) has a handshake that identifies modded clients. Add a `pokeblocks-vanilla-bridge`
that:
1. At pack-build time, runs `geo2vanilla-core` over the active doll set (built-in + custom,
   including runtime-added custom decorations) and merges the emitted resource-pack files into
   the served pack. Vanilla clients accept server resource packs — they get doll visuals with
   **no mod installed**.
2. Detects clients that never complete the mod handshake (= vanilla) and, for chunks they watch,
   spawns display-entity proxies mirroring each placed doll block (position, pose, color, spin).
   Modded clients keep the real GeckoLib block entities; proxy entities are spawned with
   per-player visibility if the platform allows, or simply hidden from modded clients by a tiny
   client-side rule (ignore entities tagged `pokeblocks_proxy`).
3. Optionally exports a standalone **datapack edition** (`/pokeblocks export-datapack`) — the
   pure-vanilla product for servers that won't run the mod at all: placement via `placed_block`
   advancement triggers on the base item, breaking via interaction-entity attack detection,
   drops via generated loot functions.

This makes the converter three products from one core: a community CLI tool, a vanilla-compat
layer for Pokeblocks servers, and an exportable datapack.

---

## 11. Suggested roadmap

1. **Week 1–2 — core + static bake**: extract GeoPose math into `geo2vanilla-core`; rotation-group
   partitioner; resource-pack emitter; golden-vertex + texel test suites over the 121-model
   corpus; fidelity report.
2. **Week 2–3 — items + colors**: 1.21.4 items definitions, CMD color tints, GUI sprite fallback
   (reuse `gen_compendium_assets.py` renders); 1.21.1 compat profile (CMD overrides +
   leather-horse-armor tint).
3. **Week 3–4 — world composites**: datapack emitter (place/break/integrity functions); mc-test
   visual regression lane (converted vs GeckoLib side-by-side screenshots).
4. **Week 4–5 — animation baking** + the 4 Molang clips; runtime module with the tick driver.
5. **Week 5+ — Pokeblocks bridge**: pack-merge into the served pack; vanilla-client proxy
   spawning; datapack export command.

Biggest schedule risks: sign/axis conventions in the space conversion (mitigated by golden-vertex
tests on day one) and interaction-entity UX polish on the datapack route (break/place feel).

---

## 12. Sources

- [Minecraft Snapshot 24w45a (minecraft.net)](https://www.minecraft.net/en-us/article/minecraft-snapshot-24w45a) — element rotation no longer limited to 22.5° multiples (still −45…+45, single axis)
- [Blockbench issue #2754](https://github.com/JannisX11/blockbench/issues/2754) — confirms arbitrary angles within ±45 in Java models
- [Items model definition — Minecraft Wiki](https://minecraft.wiki/w/Items_model_definition) — 1.21.4 items definitions, tint sources incl. `minecraft:custom_model_data`
- [Data component format/custom model data — Minecraft Wiki](https://minecraft.wiki/w/Data_component_format/custom_model_data) — CMD floats/flags/strings/colors
- [Java Edition 1.21.4 — Minecraft Wiki](https://minecraft.wiki/w/Java_Edition_1.21.4), [Java Edition 1.21.5 — Minecraft Wiki](https://minecraft.wiki/w/Java_Edition_1.21.5)
- [Pack format — Minecraft Wiki](https://minecraft.wiki/w/Pack_format) — RP/DP format numbers per version
- [Model — Minecraft Wiki](https://minecraft.wiki/w/Model) — element/rotation/display-section constraints
- Prior art: [Animated Java](https://animated-java.dev/) (Blockbench → datapack + resource pack via item displays), [ModelEngine](https://www.spigotmc.org/resources/conxeptworks-model-engine%E2%80%94ultimate-entity-model-manager-1-19-4-1-21-1.79477/) (server-side bedrock-model rendering on Paper)
