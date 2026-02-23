# Adding New Pokemon Models to Pokeblocks

## Quick Start

1. **Prepare your files** (model, textures)
2. **Drop files in the right folders**
3. **Configure rarity** (optional)
4. **Test in-game**

## File Requirements

### Required Files

- **Model**: `pokedoll_<pokemon_name>.geo.json` - The 3D model file
- **Base Texture**: `pokedoll_<pokemon_name>.png` - Default texture

### Optional Files

- **Shiny Texture**: `pokedoll_<pokemon_name>_shiny.png` - Shiny variant texture
- **Posed Model**: `pokedoll_<pokemon_name>_posed.geo.json` - Special pose model
- **Animated Model**: `pokedoll_<pokemon_name>_animated.geo.json` - Animated model
- **Animation**: `pokedoll_<pokemon_name>.animation.json` - Animation data

## File Locations

Place files in these directories within your mod:

```
common/src/main/resources/assets/pokeblocks/
├── geo/block/                    # Model files (.geo.json)
├── textures/block/               # Texture files (.png)
└── animations/block/             # Animation files (.animation.json)
```

## Naming Conventions

### Pokemon Names
- Use **lowercase** names
- Use **underscores** for spaces: `ho_oh`, `mr_mime`
- Keep names **simple and consistent**

### File Naming Examples

For a Pokemon named "pikachu":

```
# Required
pokedoll_pikachu.geo.json         # Base model
pokedoll_pikachu.png              # Base texture

# Optional variants
pokedoll_pikachu_shiny.png        # Shiny texture
pokedoll_pikachu_posed.geo.json   # Posed model
pokedoll_pikachu_animated.geo.json # Animated model
pokedoll_pikachu.animation.json   # Animation data
```

## Step-by-Step Guide

### Step 1: Prepare Your Files

1. **Export your model** as `.geo.json` from Blockbench
2. **Export textures** as `.png` files
3. **Name files** following the conventions above

### Step 2: Add Files to Mod

1. Copy model file to: `common/src/main/resources/assets/pokeblocks/geo/block/`
2. Copy texture files to: `common/src/main/resources/assets/pokeblocks/textures/block/`
3. Copy animation files to: `common/src/main/resources/assets/pokeblocks/animations/block/`

### Step 3: Configure Rarity (Optional)

Edit `config/Pokeblocks/doll_rarity.json` and add your Pokemon:

```json
[
  "pikachu uncommon",
  "pikachu posed rare"
]
```

**Available rarities**: `common`, `uncommon`, `rare`, `epic`, `legendary`, `shiny`, `none`

### Step 4: Test

1. **Launch the game**
2. **Check creative menu** - your Pokemon should appear in the Pokeblocks tab
3. **Test loot tables** - spawn chests to verify loot generation
4. **Use reload command** if you change rarity: `/pokeblocks reload_rarity`

## Advanced Features

### Model Variants

The system automatically detects variants based on file names:

- **Shiny**: Any texture with `_shiny` suffix
- **Posed**: Any model with `_posed` suffix  
- **Animated**: Any model with `_animated` suffix
- **Gigantic**: Automatically available for all Pokemon

### Required Combinations

If you create a model like `pokedoll_snorunt_family_animated.geo.json`, the system will treat `family + animated` as a required combination (both flags must be present together).

### Animation Support

- **Base animations**: `pokedoll_<name>.animation.json`
- **Variant animations**: `pokedoll_<name>_<flag>.animation.json`

Example:
```
pokedoll_calyrex.animation.json          # Base animation
pokedoll_calyrex_animated.animation.json # Animated variant animation
```

## Troubleshooting

### Pokemon Not Appearing

1. **Check file names** - must follow exact naming convention
2. **Check file locations** - must be in correct asset folders
3. **Check console** - look for error messages about missing files
4. **Verify base files** - model and texture are required

### Common Issues

- **Case sensitivity**: Use lowercase names consistently
- **Missing base texture**: Every Pokemon needs `pokedoll_<name>.png`
- **Missing base model**: Every Pokemon needs `pokedoll_<name>.geo.json`
- **Invalid JSON**: Validate your .geo.json files

### Debug Information

The mod logs helpful information on startup:
```
[Pokeblocks] built-in scan: 67 pokemon (67 new)
[Pokeblocks] resource scan: 1 pokemon (1 new)
```

## Examples

### Basic Pokemon (Pikachu)
```
Files needed:
- pokedoll_pikachu.geo.json
- pokedoll_pikachu.png
- pokedoll_pikachu_shiny.png

Rarity config:
"pikachu uncommon"
```

### Advanced Pokemon (Charizard with poses)
```
Files needed:
- pokedoll_charizard.geo.json
- pokedoll_charizard.png
- pokedoll_charizard_shiny.png
- pokedoll_charizard_posed.geo.json
- pokedoll_charizard.animation.json

Rarity config:
"charizard rare"
"charizard posed epic"
```

### Figurine (Custom Character)
```
Files needed:
- pokedoll_custom_figurine.geo.json
- pokedoll_custom_figurine.png

Rarity config:
"custom_figurine none"
```

## Migration from Old System

If you have Pokemon from the old Fabric system:

1. **Rename files** to follow new conventions
2. **Remove Java code** - no longer needed
3. **Add to rarity config** instead of hardcoding
4. **Test thoroughly** - behavior may differ slightly

## Tips

- **Start simple** - add basic model + texture first, then add variants
- **Use existing Pokemon** as reference for file structure
- **Test frequently** - catch issues early
- **Keep backups** - especially when migrating existing content
- **Check logs** - the mod provides helpful debug information

## Getting Help

- **Check console logs** for specific error messages
- **Compare with existing Pokemon** in the mod
- **Verify file permissions** and locations
- **Test with minimal setup** first (just model + texture)