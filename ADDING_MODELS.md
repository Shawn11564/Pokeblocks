# Adding Custom Pokemon Models to Pokeblocks

This guide is for **players/packmakers** adding your own dolls or figurines to an installed
copy of the mod via the config folder. (Mod developers editing the source tree should read
[ADDING_MODELS DEVS ONLY.md](ADDING_MODELS%20DEVS%20ONLY.md).)

## Quick Start

1. Find your custom folder: `config/Pokeblocks/resourcepack/custom/`
2. Put your files in `custom/assets/models/`, `custom/assets/textures/`,
   `custom/assets/animations/`
3. (Optional) Set rarity in `config/Pokeblocks/doll_rarity.json`
4. Launch / restart the game

## Folder Structure

The mod reads custom assets from a **simplified, flat** layout and remaps them into a
generated resource pack automatically. Place files directly in these folders (no
sub-directories):

```
config/Pokeblocks/resourcepack/custom/
├── assets/
│   ├── models/        # *.geo.json model files
│   ├── textures/      # *.png texture files
│   └── animations/    # *.animation.json files (optional)
└── pack.png           # optional custom pack icon
```

> You can also drop a complete pack — a folder **or** a `.zip` — directly into
> `config/Pokeblocks/resourcepack/` (anything other than the `custom` folder), using the same
> `assets/{models,textures,animations}/` layout. The `custom` folder is loaded last, so its
> files win on conflicts.

## File Requirements

### Pokedolls

| File | Goes in | Required? |
|------|---------|-----------|
| Base model `pokedoll_<name>.geo.json` | `assets/models/` | **Yes** |
| Base texture `pokedoll_<name>_texture.png` | `assets/textures/` | **Yes** |
| Shiny texture `pokedoll_<name>_shiny_texture.png` | `assets/textures/` | Optional |
| Animation `pokedoll_<name>.animation.json` | `assets/animations/` | Optional |
| Variant files (add a flag suffix) | matching folder | Optional |

> A bare `pokedoll_<name>.png` is accepted, but the recommended convention is the `_texture`
> suffix: `pokedoll_<name>_texture.png`.

### Figurines

| File | Goes in | Required? |
|------|---------|-----------|
| Model `<id>_figurine.geo.json` | `assets/models/` | **Yes** |
| Texture `<id>_figurine_texture.png` | `assets/textures/` | **Yes** |

## Model Variants & Flags

Flags are detected from filename suffixes and can be combined:

| Flag | Texture suffix | Model suffix | Notes |
|------|----------------|--------------|-------|
| **SHINY** | `_shiny` | *(none)* | Shiny coloring (auto-rarity: shiny) |
| **GIGANTIC** | *(none)* | *(none)* | Always available; large scale |
| **FAMILY** | `_family` | `_family` | Family group variant |
| **ANIMATED** | `_animated` | `_animated` | Animated variant |
| **POSED** | `_posed` | `_posed` | Special pose |
| **NETHERITE** | `_netherite` | *(none)* | Netherite variant |
| **ZENITH** | `_zenith` | `_zenith` | Zenith variant |
| **NOICE** | `_noice` | `_noice` | No-ice variant |
| **SPIKY** | `_spiky` | `_spiky` | e.g. spiky-eared Pichu |
| **EARED** | `_eared` | `_eared` | combines with SPIKY |
| **MALE / FEMALE** | `_male` / `_female` | *(none)* | mutually exclusive gender variants |

Suffixes stack: `pokedoll_pikachu_posed.geo.json` + `pokedoll_pikachu_posed_texture.png`.

### Required flag combinations
If you provide `pokedoll_snorunt_family_animated.geo.json` without the individual
`pokedoll_snorunt_family.geo.json` / `pokedoll_snorunt_animated.geo.json` models, the
`family + animated` flags become a required pair (both must be applied together).

## Naming Conventions

- **Lowercase**, **underscores** for spaces (`ho_oh`, `mr_mime`).
- Texture/animation names must match the model name (plus the same flag suffixes) exactly.

## Hitboxes

You don't configure hitboxes — the mod derives each doll's hitbox from its `.geo.json`
automatically (on both server and client), rotated to how it was placed. It's a **single box
sized to fit the model**: it never sticks out past the model on any side (it sits at or just
inside the real surface), and very thin parts (paper-thin decal cubes, 1px fins) are ignored
rather than padded. Because it's one box, it can't hug a concave or diagonal silhouette — the
gaps around, say, a thin tail are expected. If a model can't be read, that doll just uses the
classic centered box.

## Rarity

Edit `config/Pokeblocks/doll_rarity.json` — a JSON array of `"<name> [flag...] <rarity>"`:

```json
[
  "pikachu uncommon",
  "pikachu posed rare",
  "calyrex epic",
  "calyrex animated legendary"
]
```

Rarities: `none`, `common`, `uncommon`, `rare`, `epic`, `legendary`, `shiny`, `gigantic`.

- No entry → defaults to `common`.
- **Shiny** and **gigantic** variants get their rarity automatically — no entry needed.
- Every other variant (posed/animated/family/…) needs its own line to set a non-common rarity.

## Figurine names & tags

- `config/Pokeblocks/figurine_names.json` — `"<id> <DisplayName>"` to set the display name.
- `config/Pokeblocks/figurine_tags.json` — `"<id> <tag>"`; `cobblemon_team` adds a
  "Cobblemon Team Member" tooltip line.

## Custom Pack Icon

Put a `pack.png` in `config/Pokeblocks/resourcepack/custom/` to set the generated pack's icon.

## Reloading Changes

- **Rarity:** `/pokeblocks reload_rarity`
- **Weights:** `/pokeblocks reload_weights`
- **New models/textures:** restart the game (the resource pack is regenerated on launch)

## Troubleshooting

1. **Not appearing?** Check file names match the convention exactly, that the base model +
   texture exist, and read the console log — the mod prints what it scanned and names any
   skipped doll (missing model/texture).
2. **Wrong texture when placed?** Use the `_texture.png` suffix and make sure flag suffixes on
   the texture match the model.
3. **Animation broken?** Verify the JSON is valid and the filename flags match the model.
4. **Case sensitivity** — keep everything lowercase.

The startup log confirms detection:
```
[Pokeblocks] Custom resource pack: 7 model(s), 14 texture(s), 0 animation(s)
[Pokeblocks] resource scan: 7 pokemon (7 new)
```
