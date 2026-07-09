# Configuration

This page is for **server admins** running Pokeblocks and for **pack makers** who want to tune
rarities, loot, and figurine text. It covers the on-disk config layout, the `config.toml` settings
that control how Pokeblocks keeps its bundled config and override files up to date, the managed JSON
files you can edit, and the in-game commands for previewing and applying updates.

For pack-related topics see [Resource Packs](Resource-Packs) and
[Custom Content & Admin Override Packs](Custom-Content-Packs). For the full command list see
[Commands](Commands).

---

## Where the config lives

Pokeblocks stores everything under one folder in your server directory:

```
<serverDir>/config/Pokeblocks/
├── config.toml                     # the main settings file (this page)
├── doll_rarity.json                # managed JSON override files (see below)
├── figurine_names.json
├── figurine_descriptions.json
├── figurine_tags.json
├── rarity_weights.json
├── loot_groups.json
└── .sync/                          # hidden working dir — leave it alone
    ├── baseline/                   # last-synced snapshots (used for 3-way merge)
    ├── backups/                    # timestamped pre-update backups
    └── last_sync_report.txt        # what the most recent sync did
```

`config.toml` is created automatically on first run. On later runs, any **missing** keys or sections
are appended in place — your existing edits are never lost during an upgrade. The file uses a simple
TOML-ish format with the sections `[config_sync]`, `[eastereggs]`, `[resourcepack]`, and `[loot]`.
Section names and keys are case-insensitive.

> **Note:** The `.sync/` folder is internal bookkeeping. The `baseline/` snapshots are what make the
> non-destructive `merge` mode possible. Don't delete or hand-edit anything in `.sync/` — doing so can
> make a later update re-apply changes you already had.

---

## The `[config_sync]` section

This section controls how Pokeblocks reconciles its **bundled defaults** (shipped in the jar) with
**your live files** when the mod updates.

```toml
# config/Pokeblocks/config.toml
[config_sync]
# merge (default) | overwrite | off
auto_update_configs = merge
backup_before_update = true
frozen_files = [
]
show_files = [
]
hide_files = [
]
```

### `auto_update_configs` — update mode

Accepts exactly one of three values (case-insensitive). **Default: `merge`.**

| Value | Behavior |
|-------|----------|
| `merge` | **(default, recommended)** Non-destructive three-way merge of the bundled defaults against your live file using a baseline snapshot. New default entries are added and upstream changes to untouched entries are applied, while **your custom entries and your deliberate deletions are preserved.** After a successful pass the baseline advances to the current bundled default. |
| `overwrite` | On **every startup**, each managed file is replaced wholesale with the mod's bundled default, **discarding your edits**. A timestamped backup is taken first when `backup_before_update` is `true`. |
| `off` | Managed files are **never** auto-updated on startup. Pull updates manually with `/pokeblocks config sync`. |

For convenience, a few aliases are also accepted and mapped to the modes above:

- `true` → `merge`
- `replace` → `overwrite`
- `false`, `none`, `frozen` → `off`

Any unrecognized value falls back to the default (`merge`).

> **Note:** `overwrite` is the admin footgun — it clobbers your customizations on every startup.
> Pokeblocks logs a `WARN` at startup whenever the mode is `overwrite`. Prefer `merge` unless you
> specifically want every file reset to stock on each launch.

When `off` is set, the baseline is intentionally **not** advanced. That means when you later switch
back to `merge` (or run a manual sync), every change accumulated while you were off is replayed
cleanly — nothing is skipped.

### `backup_before_update` — pre-update backups

**Default: `true`.** When enabled, a timestamped backup is taken before a `merge` or `overwrite`
rewrites a file. Backups are written to:

```
config/Pokeblocks/.sync/backups/<file>.<yyyyMMdd-HHmmss>.bak
```

### `frozen_files` — per-file opt-out

**Default: `[]`.** A list of individual files to exclude from auto-update (whether the mode is
`merge` or `overwrite`) while every other file keeps updating. Use this for a file you've heavily
customized. Matching is case-insensitive on the file name.

```toml
# Heavily customized doll_rarity.json — keep it, but let everything else update:
[config_sync]
auto_update_configs = merge
frozen_files = [
  "doll_rarity.json"
]
```

A frozen file is skipped **and** its baseline is not advanced — so if you unfreeze it later, all the
accumulated changes are replayed at once.

### `show_files` / `hide_files` — per-file visibility

Both **default to `[]`.** These control whether a managed file is written into the
`config/Pokeblocks/` folder at all. The decision precedence is:

1. `hide_files` wins first (force-hidden),
2. then `show_files` (force-shown),
3. then the file's built-in shown-by-default flag,
4. and any unmanaged file defaults to shown.

```toml
# Reveal a hidden-by-default file so you can edit it:
[config_sync]
show_files = [
  "rarity_acquisition_divisors.json",
  "ignored_rarity_flags.json"
]

# Hide a shown-by-default file (its bundled default still applies):
hide_files = [
  "loot_groups.json"
]
```

> **Note:** Hiding a file only removes clutter — it does **not** change game balance. Hidden files are
> simply kept out of the folder; their bundled defaults still load at runtime. Pokeblocks always reads
> the on-disk file if present, otherwise falls back to the jarred default. So balance never drifts when
> a file is hidden, and you can reveal it any time by adding it to `show_files`.

---

## Legacy migration

Older installs used a boolean `[config_sync] auto_update` key. That key is still honored for backward
compatibility:

- On upgrade, when the new `auto_update_configs` key is inserted, Pokeblocks reads any legacy
  `auto_update` boolean and seeds the new key: `auto_update = true` → `merge`, `auto_update = false`
  → `off` (logged as a migration).
- At load time, if the new `auto_update_configs` key is present it takes precedence; the legacy
  `auto_update` line is honored only when the new key is absent.

```toml
# Legacy upgrade: an old file containing
[config_sync]
auto_update = false
# auto-migrates on next launch to
auto_update_configs = off
# and the old auto_update line is then ignored (safe to delete).
```

The migration log line says the old `auto_update` line is now ignored and can be removed.

---

## Managed JSON files

These are the override files `ConfigSync` owns. Each has a default visibility and a merge strategy.
**Two files are hidden by default** (`rarity_acquisition_divisors.json` and
`ignored_rarity_flags.json`) — their defaults still apply; reveal them via `show_files` to edit.
Note that `sounds.json` is **not** a managed config — it is a vanilla resource-pack asset.

| File | Shown by default | What it controls |
|------|:---:|------------------|
| `doll_rarity.json` | yes | Per-variant rarity overrides. |
| `rarity_weights.json` | yes | Loot weight per rarity tier. |
| `loot_groups.json` | yes | Routes specific dolls into their own loot pools. |
| `figurine_names.json` | yes | Custom figurine display names. |
| `figurine_descriptions.json` | yes | Compendium blurbs. |
| `figurine_tags.json` | yes | Figurine tooltip tags. |
| `rarity_acquisition_divisors.json` | **hidden** | Extra rarity penalty by how a variant is obtained. |
| `ignored_rarity_flags.json` | **hidden** | Flags ignored when computing rarity. |

### `doll_rarity.json`

A JSON array of `"pokemon [flags...] rarity"` entries — the **last token is the rarity**.

```json
[
  "bulbasaur common",
  "bulbasaur posed rare",
  "calyrex animated legendary"
]
```

### `rarity_weights.json`

A JSON object mapping each rarity name to an integer loot weight. **Higher = more common; `0` excludes
that tier from loot.** Shipped defaults:

```json
{
  "_comment": "Higher values = more common in loot. Set to 0 to exclude from loot tables.",
  "none": 5, "common": 500, "uncommon": 300, "rare": 150,
  "epic": 70, "legendary": 30, "shiny": 5, "gigantic": 0
}
```

### `ignored_rarity_flags.json`

A JSON array. A **single token** is a blanket flag ignored for *every* pokemon; **two or more tokens**
ignore a flag for *one* pokemon, when computing rarity.

```json
[
  "noice",
  "eiscue noice"
]
```

### `rarity_acquisition_divisors.json` *(hidden by default)*

A JSON array of `"pokemon [flags...] divisor"` entries. The divisor is an extra rarity penalty
reflecting how a variant is obtained — it divides the effective loot weight.

```json
[
  "substitute 12"
]
```

### `figurine_names.json`

A JSON array of `"id Display Name"` entries giving figurines custom display names.

### `figurine_descriptions.json`

A JSON array of `"id description text"` entries (split on the first whitespace). Shown on the
figurine's detail page in the [Figurine Compendium](Compendiums); missing entries fall back to a
generic blurb.

### `figurine_tags.json`

A JSON array of `"id tag1 tag2..."` entries. The known tag `cobblemon_team` shows
*"Cobblemon Team Member"* in the tooltip.

### `loot_groups.json`

Routes specific dolls into their own loot pools that are injected only into targeted loot tables (for
example, archaeology suspicious sand/gravel). Any doll placed in a group is removed from the default
global loot pool.

> **Note:** If you edit a managed JSON file and break its syntax, `merge` mode will detect the invalid
> JSON, log an error, and leave the file untouched (reported as skipped) rather than corrupting it.
> Fix the JSON or delete the file to regenerate the default.

---

## Manual sync commands

Both subcommands require **permission level 2 (op)**. They run with `force=true`, so they work even
when `auto_update_configs = off` — a forced `off` runs **once** as a non-destructive `merge`. Frozen
files are still respected.

```
# Preview pending config changes without writing anything (op only):
/pokeblocks config status

# Apply pending updates and hot-reload caches (works even when auto-update is off):
/pokeblocks config sync
```

- **`/pokeblocks config status`** is a dry run. It shows the current auto-update mode (green normally,
  yellow when `off`) and previews what would change. Hidden files surface as a note:
  *"`<file>`: hidden by default (not created; its bundled default still applies). Add it to show_files
  to edit it."*
- **`/pokeblocks config sync`** applies pending changes and then hot-reloads every config cache and
  invalidates the loot map — **no server restart required.**

To edit a hidden file, add it to `show_files` so it gets written to the folder, then run a sync (or
restart) to have it created.

---

## Other `config.toml` sections (quick reference)

These live in the same file and are documented in detail on their own pages:

- **`[eastereggs]`** — `doll_popping_enabled` (default `true`).
- **`[resourcepack]`** — `kick_on_decline` (default `true`), `include_builtin_assets` (default `true`;
  bundles the mod's own doll assets into the served pack so outdated clients still see dolls added by
  newer server-side mod updates), `delta_serving` (default `true`; joining players negotiate a small
  per-player pack holding only what their install is missing), `distribution` = `self_host | remote_url`
  (default `self_host`), `self_host_address`, `remote_url`, `remote_sha1`.
  See [Resource Packs](Resource-Packs).
- **`[loot]`** — `drop_chance` (default `0.33`), `loot_tables`, `excluded_flags`
  (defaults `gigantic`, `noice`), `excluded_dolls` (default `substitute`).

---

## Where to look in code

- `common/src/main/java/dev/mrshawn/pokeblocks/config/PokeblocksConfig.java` — config path, `reload()`
  defaults, `auto_update_configs` + legacy `auto_update` parse, `KeyDef` defaults/comments,
  `show_files`/`hide_files`/`frozen_files` parse, legacy migration (`resolveDefaultValue` /
  `findLegacyAutoUpdate`).
- `common/src/main/java/dev/mrshawn/pokeblocks/config/ConfigUpdateMode.java` — `merge|overwrite|off`
  enum and alias parsing.
- `common/src/main/java/dev/mrshawn/pokeblocks/config/ConfigSync.java` — `MANAGED` file list with
  shown-by-default flags and merge strategies, `isFileShown` precedence, startup logging, hidden-file
  / baseline-not-advanced semantics, and the backup directory.
- `common/src/main/java/dev/mrshawn/pokeblocks/config/PokeblocksConfigFiles.java` — writes a file only
  when shown; jar fallback so hidden files keep their defaults.
- `common/src/main/java/dev/mrshawn/pokeblocks/command/ConfigSyncCMD.java` —
  `/pokeblocks config status|sync`, permission level 2, `force=true`, `reloadAll`.
- `common/src/main/java/dev/mrshawn/pokeblocks/PokeblocksLog.java` — the side-effect-free `pokeblocks`
  SLF4J logger used by all config loaders.
- Per-file loaders in `common/src/main/java/dev/mrshawn/pokeblocks/item/` (`DollRarityOverrides`,
  `RarityWeightConfig`, `DollRarityIgnoredFlags`, `DollRarityAcquisitionDivisors`,
  `FigurineNameOverrides`, `FigurineDescriptionOverrides`, `FigurineTagOverrides`) and
  `item/loot/LootGroupConfig.java`.
- Bundled defaults in `common/src/main/resources/assets/pokeblocks/`.
