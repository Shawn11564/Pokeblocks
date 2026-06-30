# Commands Reference

This page documents every in-game command Pokeblocks adds. It is written for **server admins** and **pack makers** who need to give items, inspect dolls, hot-reload config-backed data, rebuild the served resource pack, and pull in new content on demand.

All commands live under a single root literal: **`/pokeblocks`**. There is no bare `/pokeblocks` executor and no `help` subcommand — every subcommand is registered as its own branch and merged by Brigadier into the one `/pokeblocks` tree. The same command set is registered on all three loaders (Fabric, NeoForge, Forge); no loader adds extra subcommands.

> [!NOTE]
> **Permissions:** Every subcommand requires **op / permission level 2** (`hasPermission(2)`), with one exception — `/pokeblocks dollinfo` has no permission requirement and is usable by anyone (level 0).

## Command tree at a glance

| Command | Permission | Purpose |
| --- | --- | --- |
| `/pokeblocks dollinfo <pokemon>` | level 0 (anyone) | Print a doll's model flags |
| `/pokeblocks pokegive <player> ...` | level 2 | Give a pokedoll (specific / random / by rarity) |
| `/pokeblocks figurinegive <player> <figurine>` | level 2 | Give a figurine item |
| `/pokeblocks decorativegive <player> <decorative> [flags/variants]` | level 2 | Give a decorative block |
| `/pokeblocks resourcepack rebuild` | level 2 | Rebuild and re-push the served pack |
| `/pokeblocks resourcepack saveexample` | level 2 | Write a worked example sub-pack |
| `/pokeblocks reload_rarity` | level 2 | Reload doll rarity overrides |
| `/pokeblocks reload_weights` | level 2 | Reload rarity weights from config |
| `/pokeblocks recreate_weights` | level 2 | Recreate the rarity weights config with defaults |
| `/pokeblocks reload_loot_groups` | level 2 | Reload loot groups |
| `/pokeblocks intable <loot_table> <search_string>` | level 2 | Search a loot table for drops |
| `/pokeblocks config status` | level 2 | Preview pending config sync |
| `/pokeblocks config sync` | level 2 | Apply config sync and reload caches |

The give commands are flat — there is no shared `give` parent. They are `pokegive`, `figurinegive`, and `decorativegive`.

## Inspecting dolls

### `/pokeblocks dollinfo <pokemon>`

Prints the given pokemon's model flags as `flag=true/false` pairs. This is the only command with **no permission requirement** (usable by anyone). The `<pokemon>` argument is a single word, suggested from the registered pokemon list. An unknown pokemon fails with `Pokemon X is not registered`.

```text
# Inspect a doll's model flags (no op required)
/pokeblocks dollinfo applin
```

## Giving items

### `/pokeblocks pokegive <player> ...` (op level 2)

`<player>` is a player selector. There are three branches:

- **`random`** — gives a random valid pokedoll across all registered dolls and flag combinations.
- **`rarity <rarity>`** — gives a random doll whose effective rarity equals `<rarity>`. Suggestions are the `DollRarity` values **except `NONE`**, lowercased.
- **`<pokemon> [flag1 [flag2 [flag3 [flag4]]]]`** — gives that specific pokedoll with up to four model flags. Flag suggestions only offer the pokemon's available **true** flags that are not already used. Missing required-flag combinations are rejected.

```text
# Give a specific pokedoll with model flags
/pokeblocks pokegive @p applin shiny

# Give a random pokedoll
/pokeblocks pokegive @p random

# Give a random doll of a given rarity (rarity != none)
/pokeblocks pokegive @p rarity legendary
```

> [!NOTE]
> `/pokeblocks pokegive <player> rarity NONE` is explicitly rejected with `Cannot give a doll with rarity NONE`. An unrecognized value fails with `Unknown rarity: X`.

### `/pokeblocks figurinegive <player> <figurine>` (op level 2)

`<player>` is a player selector; `<figurine>` is a word suggested from the registered figurine list. Gives the figurine item (it drops on the ground if the inventory is full). An unknown figurine fails with `Figurine X is not registered`.

```text
# Give a figurine
/pokeblocks figurinegive @p doncheadle
```

### `/pokeblocks decorativegive <player> <decorative> [flag1..flag4]` (op level 2)

`<decorative>` is the decoration definition id, suggested from the registered decorative entries. Each of up to four trailing arguments can be **either**:

- a `ModelFlag` tag-name, **or**
- a non-stackable NBT variant in `key=value` form.

Suggestions include both the flag tag-names and the `nbtKey=value` options for non-stackable variants. An unknown token fails with `Unknown flag or variant: X`; an unsupported flag fails with `Flag 'X' is not supported for <id>`.

```text
# Give a decorative block, with a flag and a non-stackable NBT variant
/pokeblocks decorativegive @p applin_basket gigantic color=red
```

## Resource pack admin tools (op level 2)

> [!NOTE]
> These commands manage the server-served resource pack. See [Resource Packs](Resource-Packs) and [Custom Content & Admin Override Packs](Custom-Content-Packs) for the full pack-building workflow.

### `/pokeblocks resourcepack rebuild`

Rebuilds the served pack from `config/Pokeblocks/resourcepack/`, reports `FOUND`/`MISS` for the `custom/` subdirectory, prints the built zip path and its SHA1, then resolves distribution and re-pushes the pack to all online players. It fails if there is nothing to distribute — for example, no custom resources, or `distribution=remote_url` without a usable `remote_url` / `remote_sha1`.

### `/pokeblocks resourcepack saveexample`

Writes a fully-worked example sub-pack into `config/Pokeblocks/resourcepack/example/`. It copies bundled dolls / figurine / decoration assets, writes six per-id override JSON files plus a `README.txt`, and tells you to run `/pokeblocks resourcepack rebuild` (or rejoin) to load it.

The six override files written are:

```text
config/Pokeblocks/resourcepack/example/
├── doll_rarity.json
├── ignored_rarity_flags.json
├── rarity_acquisition_divisors.json
├── figurine_names.json
├── figurine_tags.json
├── rarity_weights.json
└── README.txt
```

```text
# Resource pack admin tools (op level 2)
/pokeblocks resourcepack rebuild
/pokeblocks resourcepack saveexample
```

## Hot-reload commands (op level 2)

All of these take no arguments and apply changes without a server restart. See [Configuration](Configuration) for the underlying config files.

| Command | Effect | Message |
| --- | --- | --- |
| `/pokeblocks reload_rarity` | Reloads doll rarity overrides, ignored rarity flags, and acquisition divisors, then rebuilds loot/tooltip weights. | `Reloaded doll rarity overrides` |
| `/pokeblocks reload_weights` | Reloads rarity weights from the config file. | `Rarity weights reloaded from config file` |
| `/pokeblocks recreate_weights` | Recreates the rarity weights config file with bundled defaults. | `Rarity weights config file recreated with defaults` |
| `/pokeblocks reload_loot_groups` | Reloads loot groups and rebuilds the partitioned loot pools. | `Reloaded loot groups (N group(s))` |

```text
# Hot-reload config-backed data without a restart
/pokeblocks reload_rarity
/pokeblocks reload_weights
/pokeblocks recreate_weights
/pokeblocks reload_loot_groups
```

## Searching loot tables

### `/pokeblocks intable <loot_table> <search_string>` (op level 2)

`<loot_table>` is a resource location suggested from the server's reloadable loot-table registry (substring match anywhere in the id). `<search_string>` is a greedy string. The command searches the codec-serialized vanilla / datapack pools **and** Pokeblocks-injected loot entries for the string, then prints clickable chat results:

- **Vanilla entries** suggest a `/give @s <name>` command.
- **Pokeblocks entries** run the matching give command (`/pokeblocks figurinegive`, `/pokeblocks pokegive`, or `/pokeblocks decorativegive`).

It returns the number of matches found.

```text
# Search what Pokeblocks/vanilla items a loot table can drop (clickable give links)
/pokeblocks intable minecraft:chests/simple_dungeon shiny
```

## Config sync (op level 2)

Both config subcommands run with `force=true`, so they work even when `auto_update_configs` is turned **off**. This is intentional: admins who keep auto-updates off can still pull in new content on demand. Frozen files are always respected (skipped). See [Configuration](Configuration) for `auto_update_configs` and frozen files.

### `/pokeblocks config status`

Runs a forced sync **preview** (nothing is applied). It prints the current `auto_update_configs` mode (yellow if off, green otherwise) and lists the pending `+new` / `~changed` / overwritten / skipped files. It then tells you to run `config sync` to apply.

### `/pokeblocks config sync`

Applies a forced sync, then reloads every config-backed cache — doll rarity overrides / ignored flags / acquisition divisors, figurine name / description / tag overrides, rarity weights, and loot groups — and invalidates the loot map, so changes take effect without a restart. Frozen files are respected; it reports the applied `+new` / `~changed` / overwritten / skipped files.

```text
# Preview vs apply config sync (works even with auto_update_configs off)
/pokeblocks config status
/pokeblocks config sync
```

## Related pages

- [Configuration](Configuration)
- [Resource Packs](Resource-Packs)
- [Custom Content & Admin Override Packs](Custom-Content-Packs)
- [Commands](Commands)

## Where to look in code

- `common/src/main/java/dev/mrshawn/pokeblocks/command/ModCommands.java` — registers all 10 command classes.
- `common/src/main/java/dev/mrshawn/pokeblocks/command/DollInfoCMD.java` — `dollinfo`; the only subcommand without a permission requirement.
- `common/src/main/java/dev/mrshawn/pokeblocks/command/PokeGiveCMD.java` — `pokegive random|rarity|<pokemon>+flags` (perm 2).
- `common/src/main/java/dev/mrshawn/pokeblocks/command/FigurineGiveCMD.java` — `figurinegive` (perm 2).
- `common/src/main/java/dev/mrshawn/pokeblocks/command/DecorativeGiveCMD.java` — `decorativegive` flags + `key=value` NBT variants (perm 2).
- `common/src/main/java/dev/mrshawn/pokeblocks/command/ResourcePackCMD.java` — `resourcepack rebuild|saveexample` (perm 2).
- `common/src/main/java/dev/mrshawn/pokeblocks/command/ReloadRarityCMD.java` — `reload_rarity` (perm 2).
- `common/src/main/java/dev/mrshawn/pokeblocks/command/ReloadWeightsCMD.java` — `reload_weights`, `recreate_weights` (perm 2).
- `common/src/main/java/dev/mrshawn/pokeblocks/command/ReloadLootGroupsCMD.java` — `reload_loot_groups` (perm 2).
- `common/src/main/java/dev/mrshawn/pokeblocks/command/IntableSearchCMD.java` — `intable <loot_table> <search_string>` (perm 2).
- `common/src/main/java/dev/mrshawn/pokeblocks/command/ConfigSyncCMD.java` — `config status|sync` (perm 2, `force=true`).
- `fabric/src/main/java/dev/mrshawn/pokeblocks/PokeblocksFabric.java`, `neoforge/.../PokeblocksNeoForge.java`, `forge/.../PokeblocksForge.java` — loader glue; all delegate to `ModCommands`.
