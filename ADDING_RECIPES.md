# Adding Crafting Recipes for Dolls, Figurines & Decorations

Pokeblocks ships a small **custom crafting recipe type** (`pokeblocks:crafting_shaped`) that lets
you craft dolls, figurines and decorations in a normal crafting table. Recipes are plain JSON
datapack files, so adding one needs **no Java code** and works identically on Forge, NeoForge and
Fabric.

## Why a custom recipe type is needed

Dolls and figurines are **not** separate registered items. Every doll is the single
`pokeblocks:pokedoll` item, with its species (`applin`, `luvdisc`, …) and variant flags (`shiny`, …)
stored in data components. A vanilla recipe ingredient can only match by item id, so it can't tell an
*Applin Doll* apart from a *Shiny Applin Doll*, and it can't produce a doll/decoration with the right
components. The `pokeblocks:crafting_shaped` type adds:

- a `doll` ingredient that matches a species + an exact flag set, and
- `decorative` / `doll` / `figurine` results that build the correct component item.

Ordinary items and tags still work too, so you can freely mix them with dolls.

## Where recipes live

```
common/src/main/resources/data/pokeblocks/recipe/<recipe_name>.json
```

The `common` module's resources are bundled into all three loader jars, so one file covers every
platform. Drop in a new `.json` file (the file name is the recipe id) and you're done.

## Recipe format

```json
{
  "type": "pokeblocks:crafting_shaped",
  "category": "misc",
  "pattern": [
    "PPP",
    "AAA",
    "PPP"
  ],
  "key": {
    "P": { "tag": "minecraft:planks" },
    "A": { "doll": "applin" }
  },
  "result": { "decorative": "applin_basket" }
}
```

- **`pattern`** — up to 3 rows of up to 3 characters. A space (`" "`) is an empty slot. Like vanilla
  shaped recipes, the shape can sit anywhere in the grid and may be mirrored.
- **`category`** — optional crafting-book category (`misc`, `building`, `equipment`, `redstone`).
  Defaults to `misc`.

### Ingredients (`key`)

Each symbol maps to one of these forms:

| Form                                         | Matches                                  |
|----------------------------------------------|------------------------------------------|
| `{ "item": "minecraft:pink_wool" }`          | a specific item                          |
| `{ "tag": "minecraft:planks" }`              | any item in a tag (e.g. any plank type)  |
| `{ "doll": "applin" }`                        | an Applin doll with **no** flags         |
| `{ "doll": "applin", "flags": ["shiny"] }`    | a Shiny Applin doll                      |

> Doll matching is exact on flags, so a plain `{"doll":"applin"}` will **not** accept a shiny (or
> gigantic) Applin, and vice-versa. The species name is the doll's id, e.g. `applin`, `luvdisc`.

### Results (`result`)

Exactly one output kind, plus optional `flags` and `count` (default `1`):

| Form                                                       | Produces                                   |
|------------------------------------------------------------|--------------------------------------------|
| `{ "item": "minecraft:stick", "count": 2 }`                | a vanilla item                             |
| `{ "decorative": "applin_basket" }`                        | a decoration                               |
| `{ "decorative": "applin_basket", "flags": ["shiny"] }`    | a shiny decoration                         |
| `{ "doll": "applin", "flags": ["shiny"] }`                 | a pokedoll                                 |
| `{ "figurine": "doncheadle" }`                             | a figurine                                 |

Valid `flags` are the doll model flags: `shiny`, `gigantic`, `family`, `animated`, `posed`,
`netherite`, `zenith`, `noice`, `spiky`, `eared`, `male`, `female` (whichever the target supports).

## Current recipes

| Result                       | File                                              | Shape |
|------------------------------|---------------------------------------------------|-------|
| Applin Basket                | `data/pokeblocks/recipe/applin_basket.json`       | Planks / Applin Dolls / Planks |
| Shiny Applin Basket          | `data/pokeblocks/recipe/shiny_applin_basket.json` | Planks / Shiny Applin Dolls / Planks |
| Luvdisc Cushion              | `data/pokeblocks/recipe/luvdisc_cushion.json`     | Pink wool diamond around a Luvdisc Doll |

## Adding a new recipe — checklist

1. Create `common/src/main/resources/data/pokeblocks/recipe/<name>.json`.
2. Set `"type": "pokeblocks:crafting_shaped"`.
3. Fill in `pattern`, `key`, and a single `result`.
4. Launch any loader and confirm it appears in the crafting table / recipe book. The same file works
   on Forge, NeoForge and Fabric.

## Code map (for maintainers)

- `recipe/PokeblocksShapedRecipe.java` — the recipe + JSON/network serializer.
- `recipe/PokeblocksIngredient.java` — vanilla-or-doll ingredient matcher.
- `recipe/PokeblocksRecipeResult.java` — item / decorative / doll / figurine output.
- `recipe/PokeblocksRecipeSerializers.java` — registers the serializer (called from
  `PokeblocksCommon.doRegistrations()`); per-loader registration goes through `PokeblocksPlatform`.
