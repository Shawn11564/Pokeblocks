# Adding New Models to Pokeblocks Source Code

> **Audience:** mod developers editing the source tree (not end users dropping files into
> a runtime `config/` folder — that's [ADDING_MODELS.md](ADDING_MODELS.md)).
>
> **Automated path:** most of this is handled for you by the uploader tool — drop a received
> zip in and it sorts the assets and edits the override files. See
> [tools/README.md](tools/README.md). This document is the source of truth the tool is
> built against; read it if you're adding models by hand or maintaining the tool.

## File Structure Overview

All built-in assets live under the **common** module and are scanned off the classpath at
startup by `PokemonRegistry` and `FigurineRegistry`:

```
common/src/main/resources/assets/pokeblocks/
├── geo/block/                     # Model files        (*.geo.json)
├── textures/block/                # Texture files      (*.png)
├── animations/block/              # Animation files    (*.animation.json)
├── doll_rarity.json               # Pokedoll rarity overrides
├── figurine_names.json            # Figurine display-name overrides
└── figurine_tags.json             # Figurine tag overrides
```

The three JSON files are the **default** copies bundled in the jar. At runtime they are
copied to `config/Pokeblocks/` on first launch if absent (`DollRarityOverrides.initialize`,
`FigurineNameOverrides.initialize`, `FigurineTagOverrides.initialize`) and reloaded from
there. Edit the source copies here so changes ship with the mod.

## File Requirements

### Pokedolls

| File | Pattern | Required? |
|------|---------|-----------|
| Base model | `geo/block/pokedoll_<name>.geo.json` | **Yes** — a doll is skipped without it |
| Base texture | `textures/block/pokedoll_<name>_texture.png` | **Yes** |
| Shiny texture | `textures/block/pokedoll_<name>_shiny_texture.png` | Optional |
| Squeak texture | `textures/block/pokedoll_<name>_squeak_texture.png` (numbered: `_squeak_1`, `_squeak_2`, ...) | Optional |
| Base animation | `animations/block/pokedoll_<name>.animation.json` | Optional |
| Variant model/texture/animation | add the flag suffix (see table) | Optional |

> **Texture naming:** the scanner's regex (`^pokedoll_(.+)\.png$`) accepts a bare
> `pokedoll_<name>.png` too, but the **repo convention is the `_texture` suffix**
> (`pokedoll_<name>_texture.png`). The suffix parser strips a trailing `_texture` before
> reading flags, so `pokedoll_absol_shiny_texture.png` → name `absol`, flag `shiny`.
> Keep the `_texture` suffix for consistency with every existing texture.

### Figurines

Figurines are character/community models, detected by the `_figurine` segment rather than
a `pokedoll_` prefix:

| File | Pattern | Required? |
|------|---------|-----------|
| Model | `geo/block/<id>_figurine.geo.json` | **Yes** |
| Texture | `textures/block/<id>_figurine_texture.png` (bare `<id>_figurine.png` also accepted) | **Yes** — figurine skipped without it |

## Model Variants & Flags

Flags are detected from filename suffixes. **This table mirrors `ModelFlag.java`** — if you
change the enum, update this table and `tools/doll_uploader.py`'s `FLAGS` table.

| Flag | Texture suffix | Model suffix | Auto rarity | Notes |
|------|----------------|--------------|-------------|-------|
| **GIGANTIC** | *(none)* | *(none)* | `gigantic` | Always available for every doll; scale-only, no files needed |
| **SHINY** | `_shiny` | *(none)* | `shiny` | Shiny coloring |
| **FAMILY** | `_family` | `_family` | — | Family group variant |
| **ANIMATED** | `_animated` | `_animated` | — | Animated variant |
| **POSED** | `_posed` | `_posed` | — | Special pose variant |
| **NETHERITE** | `_netherite` | *(none)* | — | Netherite variant |
| **ZENITH** | `_zenith` | `_zenith` | — | Zenith variant |
| **NOICE** | `_noice` | `_noice` | — | "No-ice" variant (e.g. Eiscue) |
| **SPIKY** | `_spiky` | `_spiky` | — | e.g. spiky-eared Pichu |
| **EARED** | `_eared` | `_eared` | — | combines with SPIKY |
| **MALE** | `_male` | *(none)* | — | `gender` group |
| **FEMALE** | `_female` | *(none)* | — | `gender` group |

- **Auto rarity** means the variant resolves to that rarity *without* a `doll_rarity.json`
  entry. Only SHINY and GIGANTIC do this. Every other variant defaults to `common` unless
  you add an explicit entry (see Rarity below).
- **MALE/FEMALE share the `gender` exclusion group** — they are mutually exclusive and never
  appear together on one variant (e.g. `pokedoll_combee_male`, `pokedoll_combee_female`).
- Suffixes stack and order-independent during parsing: `pokedoll_pichu_spiky_eared.geo.json`
  → name `pichu`, flags `{spiky, eared}`.

### Required flag combinations

If a multi-flag model like `pokedoll_snorunt_family_animated.geo.json` exists but **neither**
`pokedoll_snorunt_family.geo.json` nor `pokedoll_snorunt_animated.geo.json` exists, the
registry treats `family + animated` as a *required combination* — both flags must be applied
together. Provide the standalone single-flag models if you want them to be independently
selectable.

### Squeak textures

An optional alternate skin worn only while the doll is being squeaked (right-clicked), for the
8 ticks the squish lasts. The marker goes **after** the flag suffixes, so it belongs to one
variant:

```
pokedoll_<name><flagSuffixes>_squeak_texture.png        one skin, used for every squeak
pokedoll_<name><flagSuffixes>_squeak_<n>_texture.png    frame n, cycled one per squeak
```

Rules (see `PokeblocksAssetResolver.pokedollSqueakTextures`):

- Numbered frames must run from `1` upwards with no gaps; the probe stops at the first miss.
  If any numbered frame exists, an unnumbered `_squeak` file for the same variant is ignored.
- Matching uses the **same** subset/permutation ladder as `pokedollTexture` — most specific
  flag combination first, falling back to less specific ones. So one
  `pokedoll_<name>_squeak_texture.png` covers every variant, and
  `pokedoll_<name>_shiny_squeak_texture.png` overrides it for the shiny one. The first
  matching variant supplies the whole sequence; frames are never mixed across variants.
- `PokemonRegistry` deliberately **skips** squeak textures when scanning
  (`isSqueakTexture`) — they must not invent a pokemon, register a flag or mark a flag
  combination as having a valid texture, so they need no `doll_rarity.json` entry either.
- Which frame shows is driven by `PokedollBlockEntity`'s squeak counter, which is synced to
  clients so everyone watching the doll sees the same frame.

### Animations

- Base: `pokedoll_<name>.animation.json`
- Variant: `pokedoll_<name>_<flag>.animation.json`

A variant **animation** without a matching variant **model** still registers the flag — it
renders on the base geo model. Example:
```
pokedoll_calyrex.animation.json          # base
pokedoll_calyrex_animated.animation.json # animated variant (uses base or animated geo)
```

## Naming Conventions

- **Lowercase** pokemon names; **underscores** for spaces (`ho_oh`, `mr_mime`).
- Keep names simple and consistent — the name is the identity key used everywhere
  (rarity entries, registry map, NBT).

### Example — Pikachu with shiny + posed

```
geo/block/pokedoll_pikachu.geo.json
geo/block/pokedoll_pikachu_posed.geo.json
textures/block/pokedoll_pikachu_texture.png
textures/block/pokedoll_pikachu_shiny_texture.png
textures/block/pokedoll_pikachu_posed_texture.png
textures/block/pokedoll_pikachu_squeak_1_texture.png   # optional squeak frames
textures/block/pokedoll_pikachu_squeak_2_texture.png
animations/block/pokedoll_pikachu.animation.json
animations/block/pokedoll_pikachu_posed.animation.json
```

## Rarity

`doll_rarity.json` is a JSON array of strings, each `"<name> [flag...] <rarity>"`. The flag
tokens use the **tag names** from the table above (`shiny`, `posed`, `animated`, `family`,
`netherite`, `zenith`, `noice`, `spiky`, `eared`, `male`, `female`, `gigantic`).

```json
[
  "pikachu uncommon",
  "pikachu posed rare",
  "combee male common",
  "combee female common",
  "snorunt animated family legendary"
]
```

**Rarities:** `none` (weight 5), `common` (500), `uncommon` (300), `rare` (150),
`epic` (70), `legendary` (30), `shiny` (5), `gigantic` (0). Weights live in
`rarity_weights.json` and are overridable.

**When you need an entry (verified in `DollRarity.getRarity` / `RarityScoreCalculator`):**
- A doll with **no** matching entry defaults to `common`.
- **SHINY** and **GIGANTIC** variants auto-resolve to their own rarity — **don't** add
  entries for shiny-only or gigantic variants.
- The **base** doll and **every other** flag/combo (posed, animated, family, netherite,
  zenith, noice, spiky/eared, male/female, and multi-flag combos) need an explicit entry to
  get the intended rarity.

## Figurine names & tags

- `figurine_names.json` — array of `"<id> <DisplayName>"`. Without an entry the figurine
  falls back to a derived name; add one for a proper display name (`"doncheadle DonCheadle"`).
- `figurine_tags.json` — array of `"<id> <tag...>"`. Known tag: `cobblemon_team` (shows
  "Cobblemon Team Member" in the tooltip). Unknown tags are stored harmlessly.

## Special / decorative blocks (code-defined)

Blocks like `applin_basket`, `magikarp_fishbowl`, `eiscue_head_pile`, `luvdisc_cushion`, and
`pokemon_trophy` are **not** asset-scanned dolls — they are registered in
`DecorativeRegistry.java` and require Java changes to add. Their assets use bare ids
(`applin_basket.geo.json`, `applin_basket_texture.png`) with no `pokedoll_` prefix. The
uploader tool flags any such unrecognized assets for manual handling.

## Step-by-Step (manual)

1. Drop model/texture/animation files into the matching `geo/block`, `textures/block`,
   `animations/block` directories using the naming conventions above.
2. Add rarity entries to `doll_rarity.json` (and name/tag entries for figurines).
3. Rebuild / relaunch the mod.

## Reloading Changes

- **Rarity:** `/pokeblocks reload_rarity`
- **Weights:** `/pokeblocks reload_weights`
- **New models/textures:** restart (resource pack regeneration required)

## Debug Information

Startup logs report scan counts — use them to confirm new assets were picked up:
```
[Pokeblocks] built-in scan: 67 pokemon (67 new)
[Pokeblocks] built-in figurine scan: 11 found (11 new)
```
A new doll/figurine should bump the "new" count. If a doll is skipped, the log names the
missing base model or texture.

## Troubleshooting

- **Doll not appearing:** missing base model `pokedoll_<name>.geo.json` or base texture; check
  the startup log for the skip message.
- **Wrong texture when placed:** ensure the texture uses the `_texture.png` suffix and the
  name/flags match the model exactly.
- **Animation not playing / crash:** invalid JSON, or filename flags don't match the model.
- **Case sensitivity:** keep everything lowercase.
