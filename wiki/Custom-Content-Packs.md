# Custom Content & Admin Override Packs

This page is for **server admins** and **pack makers** who want to add their own dolls,
figurines, and decorations to Pokeblocks, or override the rarity/name/tag settings of
existing content — all without touching the mod jar.

Everything here is server-side. The admin drops sub-packs into a config folder, runs one
command, and the mod merges them into a single resource pack that is served to connected
players automatically. Nothing has to be installed on the client beyond accepting the
served pack.

See also: [Configuration](Configuration) · [Resource Packs](Resource-Packs) · [Commands](Commands)

---

## Where packs live

All custom content and override packs live as **sub-packs** under:

```
<serverDir>/config/Pokeblocks/resourcepack/
```

Everything directly under that folder is treated as a sub-pack:

- each **top-level subdirectory** is a sub-pack, and
- each **`.zip` file** is a sub-pack, and
- a special **`custom/`** folder which is always loaded **last**, so it overrides every
  other sub-pack.

```
config/Pokeblocks/resourcepack/
  myaddon/          <- a folder sub-pack
  another_pack.zip  <- a zip sub-pack
  custom/           <- always wins over the rest
```

When you run `/pokeblocks resourcepack rebuild`, the mod scans this folder, merges all
sub-packs, and writes the served pack `pokeblocks_custom_pack.zip` into the server game
directory. The served zip gets a generated `pack.mcmeta` (`pack_format` 26,
description "Pokeblocks Custom Dolls") and, if `custom/pack.png` exists, that file as the
pack icon.

> [!NOTE]
> The built pack is **cached** and only rebuilt when the input files change (the mod
> fingerprints every file under `config/Pokeblocks/resourcepack/` by relative
> path + size + mtime). After editing or adding packs, run
> `/pokeblocks resourcepack rebuild` (or have players rejoin) to pick up the changes.

---

## Asset layout inside a sub-pack

Asset files go under the sub-pack's `assets/` directory. Two layouts are accepted.

### Typed layout (preferred)

```
assets/<type>/<kind>/<bare-id>.<ext>
```

- `type` = `dolls` | `figurines` | `decorations`
- `kind` = `models` | `textures` | `animations`
- the file name is the **bare id**; the `type` folder supplies the internal naming marker
  on build.

Per-kind extensions are enforced — a file whose extension doesn't match its `kind` folder
is **ignored**:

| kind         | required extension   |
|--------------|----------------------|
| `models`     | `.geo.json`          |
| `animations` | `.animation.json`    |
| `textures`   | `.png`               |

Example pack tree:

```
config/Pokeblocks/resourcepack/myaddon/
  assets/
    dolls/
      models/      exampledoll.geo.json         -> pokedoll_exampledoll.geo.json
      textures/    exampledoll.png              -> pokedoll_exampledoll_texture.png
                   exampledoll_shiny.png        -> pokedoll_exampledoll_shiny_texture.png
      animations/  exampledoll.animation.json   -> pokedoll_exampledoll.animation.json
    figurines/
      models/      examplefig.geo.json          -> examplefig_figurine.geo.json
      textures/    examplefig.png               -> examplefig_figurine_texture.png
    decorations/
      models/      exampledeco.geo.json         -> exampledeco_decoration.geo.json
      textures/    exampledeco.png              -> exampledeco_decoration_texture.png
      animations/  exampledeco.animation.json   -> exampledeco_decoration.animation.json (optional)
  pokeblocks/
    config/
      doll_rarity.json
      ignored_rarity_flags.json
      rarity_acquisition_divisors.json
      figurine_names.json
      figurine_descriptions.json
      figurine_tags.json
      rarity_weights.json
```

### How bare ids are marked

The `type` folder supplies the naming marker:

| type            | marker applied to bare id  |
|-----------------|----------------------------|
| `dolls/`        | `pokedoll_<id>`            |
| `figurines/`    | `<id>_figurine`            |
| `decorations/`  | `<id>_decoration`          |

Textures additionally get a `_texture` suffix on the marked name:

```
dolls/textures/exampledoll.png        -> pokedoll_exampledoll_texture.png
figurines/textures/examplefig.png     -> examplefig_figurine_texture.png
decorations/textures/exampledeco.png  -> exampledeco_decoration_texture.png
```

Dolls keep their flag suffixes in the bare id:

```
dolls/textures/orange_shiny.png       -> pokedoll_orange_shiny_texture.png
```

A texture bare name tolerates an already-present `_texture` suffix — both `orange.png` and
`orange_texture.png` resolve to the same internal name.

#### Squeak textures

A doll can ship an optional **squeak texture** that replaces its regular one for the moment the
doll is squeaked (right-clicked). The marker is `_squeak`, and it goes **after** any flag
suffixes — so it attaches to one specific variant:

```
dolls/textures/orange_squeak.png        -> pokedoll_orange_squeak_texture.png
dolls/textures/orange_shiny_squeak.png  -> pokedoll_orange_shiny_squeak_texture.png
```

Add a number to supply several and they are shown **in order, one per squeak**, looping back to
the first after the last:

```
dolls/textures/orange_squeak_1.png  <- shown on the 1st squeak
dolls/textures/orange_squeak_2.png  <- 2nd
dolls/textures/orange_squeak_3.png  <- 3rd, then back to _squeak_1
```

Notes:

- Numbers must run from `1` upwards with no gaps — the game stops looking at the first missing
  number, so `_squeak_1` + `_squeak_3` gives you a one-frame sequence.
- If numbered files exist, an unnumbered `_squeak` file for the same variant is ignored.
- Squeak textures are matched with the same flag rules as regular textures: the most specific
  match wins, and a doll falls back to a less specific one. So a single `orange_squeak.png`
  covers *every* variant of `orange`, while `orange_shiny_squeak.png` overrides it for the
  shiny one only. Frames are never mixed between variants.
- A squeak texture is a re-skin of a variant that must already exist. It never creates a doll,
  a variant or a rarity entry of its own — a `_squeak` file for a flag combination that has no
  regular texture simply does nothing.
- Every doll keeps working without one; dolls with no squeak texture just squish as before.

### Flat / legacy layout (fallback)

```
assets/<kind>/<full_name>
```

Here the file name already carries the `pokedoll_` / `_figurine` / `_decoration` marker;
there is no `type` folder.

```
assets/
  models/      pokedoll_orange.geo.json
  textures/    pokedoll_orange_texture.png
               pokedoll_orange_shiny_texture.png
               examplefig_figurine_texture.png
               exampledeco_decoration_texture.png
  animations/  pokedoll_orange.animation.json
```

Both layouts are remapped to the same final pack paths:

| kind       | final pack path                              |
|------------|----------------------------------------------|
| textures   | `assets/pokeblocks/textures/block/<name>`    |
| models     | `assets/pokeblocks/geo/block/<name>`         |
| animations | `assets/pokeblocks/animations/block/<name>`  |

> [!NOTE]
> Inside a `.zip` sub-pack, entry paths are split on `/`: a 4-part path is read as the
> typed layout (`assets/<type>/<kind>/<bare>`), a 3-part path as the legacy flat layout
> (`assets/<kind>/<fullname>`). Only entries starting with `assets/` are considered; other
> entries are skipped.

---

## Adding a brand-new decoration

Decorations are **fully data-driven** — there is no registration list. Drop the asset files
and the decoration is discovered by filename scan. All decorations share one generic
block/item (`CustomDecorationBlock` / `CustomDecorationItem`); the specific decoration is a
string id stored on the block entity and resolved by id at render time. The block has
`FACING` + `WATERLOGGED` states, a wool break sound, and 0.4 hardness. Its hitbox is a
**single box derived automatically from the decoration's `.geo.json`** (rotated to the placed
facing, sized so it never sticks out past the rendered model on any axis; see `DollShapes`),
falling back to a centered `Block.box(4,0,4,12,12,12)` when the model can't be resolved.

> [!NOTE]
> The same applies to pokedolls — and if a doll ships an `animation.json` whose
> `animation.idle` is a **static pose** (constant bone rotations/positions/scales, the way most
> bundled dolls sit, crouch or tuck their limbs), that pose is baked into the hitbox too, so the
> box matches the posed silhouette rather than the model's authored bind pose. Bones hidden with
> `scale: 0` are excluded. Time-varying channels (keyframe timelines, molang expressions) are
> ignored for hitbox purposes and evaluate at the bind pose.

A new decoration `<id>` is registered only when **both a model and a texture are present**:

```
assets/decorations/models/trophy.geo.json        (required)
assets/decorations/textures/trophy.png            (required — or trophy_texture.png)
assets/decorations/animations/trophy.animation.json   (optional)
```

These build into `geo/block/trophy_decoration.geo.json` +
`textures/block/trophy_decoration_texture.png`, and the id `trophy` is registered. The
generic decoration block/item then render it, and it appears in the creative tab.

> [!NOTE]
> If a model has **no matching texture**, the decoration is **skipped with a WARN** and
> will not register. Both files are required.

If a placed decoration has no id or its assets are missing, rendering falls back to the
built-in `missing` decoration (the decoration set is seeded with `missing` so the creative
tab always has at least one entry). The placeholder ships in the jar at
`assets/pokeblocks/geo/block/missing_decoration.geo.json` and
`textures/block/missing_decoration_texture.png`.

Dolls and figurines are discovered the same way — by filename scan, split on the
`_figurine` substring marker (figurine vs pokedoll; animations belong only to pokedolls).
No registration list is needed; just drop the assets.

---

## Per-id setting overrides

A pack can override Pokeblocks **settings** (rarity, names, tags, etc.) via a separate file
tree inside the sub-pack — **not** under `assets/`, and **not** bundled into the served zip.
These files are read server-side at config load:

```
<pack>/pokeblocks/config/<fileName>
```

(For zip sub-packs, the zip entry `pokeblocks/config/<fileName>`.)

The mod enumerates the same sub-packs as the asset builder (top-level folders + `.zip`,
sorted alphabetically, `custom/` last) and applies each pack's overrides **on top of** the
admin's live `config/Pokeblocks/<file>` (or the bundled default if the admin file is
absent). Merge is **additive / last-wins by key** — later packs and `custom/` win.

### Supported files

| file                              | what it overrides                          |
|-----------------------------------|--------------------------------------------|
| `doll_rarity.json`                | a doll's rarity (per id)                    |
| `ignored_rarity_flags.json`       | flags ignored for rarity calc (per id)      |
| `rarity_acquisition_divisors.json`| extra rarity divisor (per id)               |
| `figurine_names.json`             | figurine display name (per id)              |
| `figurine_descriptions.json`      | figurine description (per id)               |
| `figurine_tags.json`              | figurine tag (per id)                       |
| `rarity_weights.json`             | **global** rarity name -> loot weight       |

> [!NOTE]
> All **seven** files support pack overrides. The `saveexample` command and its README
> demonstrate only **six** of them — `figurine_descriptions.json` is omitted from the
> generated example but is still honored if you add it yourself.

### Examples

The string-array files are merged by key (`<id> <value>`), pack wins:

`pokeblocks/config/doll_rarity.json`
```json
[
  "exampledoll rare"
]
```

`pokeblocks/config/ignored_rarity_flags.json`
```json
[
  "exampledoll shiny"
]
```

`pokeblocks/config/rarity_acquisition_divisors.json`
```json
[
  "exampledoll 2"
]
```

`pokeblocks/config/figurine_names.json`
```json
[
  "examplefig Example Figurine"
]
```

`pokeblocks/config/figurine_tags.json`
```json
[
  "examplefig cobblemon_team"
]
```

`rarity_weights.json` is a **flat JSON object** that is **global** (rarity name -> loot
weight), not per-id. Only the rarity keys you actually specify are replaced; unspecified
rarities keep the base/default value. The example below uses the **default** `rare` weight
(150), so it has no balance impact — change the number to rebalance:

`pokeblocks/config/rarity_weights.json`
```json
{
  "rare": 150
}
```

---

## Pack precedence & conflicts

- Sub-packs are sorted **alphabetically** (deterministic) and loaded **last-wins**.
- The `custom/` folder is loaded **after** all of them, so it always wins.

```
config/Pokeblocks/resourcepack/custom/assets/dolls/textures/exampledoll.png
# overrides exampledoll from any other sub-pack
```

> [!NOTE]
> When the same path is provided by more than one pack, the mod logs a **WARN** on
> (re)build naming the file and the providing packs, and the **last** loader wins
> (`custom/` over sub-packs; otherwise last alphabetically). Check the server log after a
> rebuild if content isn't appearing as expected.

---

## Commands

Commands live under `/pokeblocks resourcepack` and require **permission level 2 (op)**.
See [Commands](Commands) for the full list.

```
# Write a fully-worked example sub-pack + README into config/Pokeblocks/resourcepack/example/
/pokeblocks resourcepack saveexample

# After editing or adding packs: rebuild the served zip, re-hash it, resolve
# self_host vs remote_url distribution, and re-push to connected players
/pokeblocks resourcepack rebuild
```

### `saveexample`

Writes a complete, working example sub-pack into
`config/Pokeblocks/resourcepack/example/`. The asset files are **copied from existing
bundled assets** so the example renders out of the box — replace them with your own.

It writes:

- typed-layout assets:
  - `dolls/` — `models/exampledoll.geo.json`, `textures/exampledoll.png`,
    `textures/exampledoll_shiny.png`, `animations/exampledoll.animation.json`,
    `textures/exampledoll_squeak_1.png` / `_squeak_2.png` (optional squeak textures)
  - `figurines/` — `models/examplefig.geo.json`, `textures/examplefig.png`
  - `decorations/` — `models/exampledeco.geo.json`, `textures/exampledeco.png`
- six per-id override files under `pokeblocks/config/` (`doll_rarity`,
  `ignored_rarity_flags`, `rarity_acquisition_divisors`, `figurine_names`, `figurine_tags`,
  `rarity_weights`)
- a `README.txt`

After running it, run `/pokeblocks resourcepack rebuild` (or rejoin) to load the example.

### `rebuild`

Rebuilds the served pack from `config/Pokeblocks/resourcepack/`, re-hashes it, resolves the
configured distribution (`self_host` vs `remote_url`), and re-pushes the pack to connected
players. See [Resource Packs](Resource-Packs) for how the pack is distributed.

> [!NOTE]
> Both commands require **op (permission level 2)**. Rebuilding re-hashes the served pack,
> so clients download the new copy once and reuse their cached copy on later reconnects.

---

## Where to look in code

- `common/.../resourcepack/CustomPackBuilder.java` — sub-pack discovery, typed/flat
  layout, bare-id marking (`markBareName`), path remapping (`remapToPackPath`), conflict
  handling (`addEntry`), zip build, and input fingerprinting.
- `common/.../resourcepack/CustomPackManager.java` — early `registerCustomAssets` and
  `buildAndCache` (cache reuse by fingerprint + SHA-1).
- `common/.../config/PokeblocksConfigFiles.java` — `PACK_CONFIG_PREFIX`,
  `readConfigContent`, `collectPackOverrides` (per-id override enumeration + load order).
- `common/.../item/DollRarityOverrides.java` — string-array override merge semantics.
- `common/.../item/RarityWeightConfig.java` — `applyOverride` (only specified keys replaced).
- `common/.../registry/AssetScanner.java` — doll/figurine split by the `_figurine` marker.
- `common/.../registry/CustomDecorationRegistry.java` — model/texture patterns, `missing`
  seed, model + texture both required.
- `common/.../block/custom/CustomDecorationBlock.java` — generic data-driven decoration
  block (FACING/WATERLOGGED, shape).
- `common/.../client/model/block/CustomDecorationModel.java` — id-resolved geo/texture/anim
  with `missing` fallback.
- `common/.../client/model/PokeblocksAssetResolver.java` — decoration asset resolution and
  the missing-texture fallback.
- `common/.../constants/ModSettings.java` — `DEFAULT_DECORATION = "missing"`.
- `common/.../command/ResourcePackCMD.java` — command registration/permission, `rebuild`,
  `saveExample`, and the example README.
- `common/src/main/resources/assets/pokeblocks/geo/block/missing_decoration.geo.json` +
  `textures/block/missing_decoration_texture.png` — bundled placeholder.
