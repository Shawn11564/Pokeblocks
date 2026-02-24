# Adding New Models to Pokeblocks Source Code

## File Structure Overview

### Developer Asset Structure 
```
common/src/main/resources/assets/pokeblocks/
├── geo/block/                    # Model files (.geo.json)
├── textures/block/               # Texture files (.png)
└── animations/block/             # Animation files (.animation.json)
```

## File Requirements

### Required Files

- **Model**: `pokedoll_<pokemon_name>.geo.json` - The 3D model file
- **Base Texture**: `pokedoll_<pokemon_name>.png` OR `pokedoll_<pokemon_name>_texture.png` - Default texture

### Optional Files

- **Shiny Texture**: `pokedoll_<pokemon_name>_shiny.png` - Shiny variant texture
- **Posed Model**: `pokedoll_<pokemon_name>_posed.geo.json` - Special pose model
- **Animated Model**: `pokedoll_<pokemon_name>_animated.geo.json` - Animated model
- **Animation**: `pokedoll_<pokemon_name>.animation.json` - Animation data
- **Variant Animations**: `pokedoll_<pokemon_name>_<flag>.animation.json` - Flag-specific animations

## Model Variants & Flags

The system supports several model flags that can be combined:

| Flag | Model Suffix | Texture Suffix | Description |
|------|-------------|----------------|-------------|
| **POSED** | `_posed` | `_posed` | Special pose variant |
| **ANIMATED** | `_animated` | `_animated` | Animated variant |
| **FAMILY** | `_family` | `_family` | Family group variant |
| **SHINY** | *(none)* | `_shiny` | Shiny coloring |
| **GIGANTIC** | *(none)* | *(none)* | Large scale (auto-available) |
| **NETHERITE** | *(none)* | `_netherite` | Netherite variant |
| **ZENITH** | `_zenith` | `_zenith` | Zenith variant |
| **NOICE** | `_noice` | `_noice` | Noice variant |

## Naming Conventions

### Pokemon Names
- Use **lowercase** names
- Use **underscores** for spaces: `ho_oh`, `mr_mime`
- Keep names **simple and consistent**

### File Naming Examples

For a Pokemon named "pikachu":

```
# Required Files
pokedoll_pikachu.geo.json         # Base model
pokedoll_pikachu.png              # Base texture (preferred)
# OR
pokedoll_pikachu_texture.png      # Base texture (alternative)

# Optional Variants
pokedoll_pikachu_shiny.png        # Shiny texture
pokedoll_pikachu_posed.geo.json   # Posed model
pokedoll_pikachu_posed.png        # Posed texture (optional)
pokedoll_pikachu_animated.geo.json # Animated model
pokedoll_pikachu_animated.png     # Animated texture (optional)

# Animation Files
pokedoll_pikachu.animation.json   # Base animation
pokedoll_pikachu_posed.animation.json    # Posed animation
pokedoll_pikachu_animated.animation.json # Animated animation
```

## Step-by-Step Guide

For mod developers working on the source code:

1. Place files in `common/src/main/resources/assets/pokeblocks/`
3. Rebuild the mod

## Advanced Features

### Required Flag Combinations

If you create a model like `pokedoll_snorunt_family_animated.geo.json` without individual `pokedoll_snorunt_family.geo.json` or `pokedoll_snorunt_animated.geo.json` files, the system treats `family + animated` as a required combination (both flags must be present together).

### Animation System

- **Base Animation**: `pokedoll_<name>.animation.json`
- **Variant Animations**: `pokedoll_<name>_<flag>.animation.json`

Example animation setup:
```
pokedoll_calyrex.animation.json          # Base animation
pokedoll_calyrex_animated.animation.json # Animated variant
pokedoll_calyrex_posed.animation.json    # Posed variant
```

### Texture Variants

Each model flag can have its own texture:
```
pokedoll_pokemon.png              # Base texture
pokedoll_pokemon_shiny.png        # Shiny variant
pokedoll_pokemon_posed.png        # Posed variant
pokedoll_pokemon_animated.png     # Animated variant
```

### Custom Pack Icon

Add a `pack.png` file to your custom folder root to set a custom icon for the generated resource pack.

## Available Rarities

Configure Pokemon rarity in `config/Pokeblocks/doll_rarity.json`:

- `none` - Not available in loot (weight: 5)
- `common` - Most common (weight: 500)
- `uncommon` - Fairly common (weight: 300)
- `rare` - Less common (weight: 150)
- `epic` - Rare (weight: 70)
- `legendary` - Very rare (weight: 30)
- `shiny` - Special shiny chance (weight: 5%)
- `gigantic` - Not in loot by default (weight: 0)

## Troubleshooting

### Pokemon Not Appearing

1. **Check file names** - must follow exact naming convention
2. **Check file locations** - must be in correct folders
3. **Check console logs** - look for error messages
4. **Verify required files** - model and texture are mandatory
5. **Restart game** - config folder changes require restart

### Texture Issues

**Problem**: Model appears correctly in inventory but uses wrong texture when placed.

**Solutions**:
1. Use `pokedoll_<name>_texture.png` naming
2. Check that texture file is in the `textures/` folder
3. Verify texture file name matches model name exactly

### Animation Issues

**Problem**: Animations not playing or causing crashes.

**Solutions**:
1. Ensure animation files are valid JSON
2. Check animation file names match model names
3. Some Pokemon may not support animated variants (noted in code comments)

### Common Issues

- **Case sensitivity**: Use lowercase names consistently
- **Missing base files**: Every Pokemon needs base model + texture
- **Invalid file formats**: Use `.geo.json` for models, `.png` for textures
- **Folder structure**: Files must be in correct subfolders

## Debug Information

The mod provides helpful startup logs:
```
[Pokeblocks] built-in scan: 67 pokemon (67 new)
[Pokeblocks] built-in figurine scan: 11 found (11 new)
[Pokeblocks] Custom resource pack: 7 model(s), 14 texture(s), 0 animation(s)
[Pokeblocks] resource scan: 7 pokemon (7 new)
```

## Examples

### Basic Pokemon (Pikachu)
```
Files needed:
config/Pokeblocks/custom/
├── models/pokedoll_pikachu.geo.json
├── textures/pokedoll_pikachu.png
└── textures/pokedoll_pikachu_shiny.png

Rarity config:
"pikachu uncommon"
```

### Advanced Pokemon with Poses (Charizard)
```
Files needed:
config/Pokeblocks/custom/
├── models/
│   ├── pokedoll_charizard.geo.json
│   └── pokedoll_charizard_posed.geo.json
├── textures/
│   ├── pokedoll_charizard.png
│   ├── pokedoll_charizard_shiny.png
│   └── pokedoll_charizard_posed.png
└── animations/
    ├── pokedoll_charizard.animation.json
    └── pokedoll_charizard_posed.animation.json

Rarity config:
"charizard rare"
"charizard posed epic"
```

### Animated Pokemon (Calyrex)
```
Files needed:
config/Pokeblocks/custom/
├── models/
│   ├── pokedoll_calyrex.geo.json
│   └── pokedoll_calyrex_animated.geo.json
├── textures/
│   ├── pokedoll_calyrex.png
│   └── pokedoll_calyrex_animated.png
└── animations/
    ├── pokedoll_calyrex.animation.json
    └── pokedoll_calyrex_animated.animation.json

Rarity config:
"calyrex epic"
"calyrex animated legendary"
```

### Figurine (Custom Character)
```
Files needed:
config/Pokeblocks/custom/
├── models/pokedoll_custom_figurine.geo.json
└── textures/pokedoll_custom_figurine.png

Rarity config:
"custom_figurine none"
```

## Reloading Changes

- **Rarity changes**: Use `/pokeblocks reload_rarity` command
- **Weight changes**: Use `/pokeblocks reload_weights` command  
- **New models/textures**: Restart the game (resource pack regeneration required)