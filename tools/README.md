# Pokeblocks Dev Tools

## `doll_uploader.py` — batch asset uploader

Drop in a received zip of new Pokemon dolls/figurines and this tool sorts the assets into
the correct source directories, edits the override JSON files needed to load them, and
prepares a PR (or stages files for a manual upload).

### Input layout

The tool accepts a `.zip` **or** an already-extracted folder. The expected shape is what
artists usually send — a root containing `<dex>_<name>` subfolders of assets:

```
batch.zip
├── 0025_pikachu/
│   ├── pokedoll_pikachu.geo.json
│   ├── pokedoll_pikachu_texture.png
│   ├── pokedoll_pikachu_shiny_texture.png
│   ├── pokedoll_pikachu_posed.geo.json
│   ├── pokedoll_pikachu_squeak_1_texture.png
│   ├── pokedoll_pikachu_squeak_2_texture.png
│   └── pokedoll_pikachu.animation.json
└── 0133_eevee/
    └── ...
```

Files may also be flat or nested — the tool walks recursively and classifies by filename.
File naming must follow the conventions in
[ADDING_MODELS DEVS ONLY.md](../ADDING_MODELS%20DEVS%20ONLY.md). Textures without the
`_texture` suffix are auto-renamed to it.

### Usage

```bash
python tools/doll_uploader.py path/to/batch.zip            # process & prepare PR
python tools/doll_uploader.py path/to/batch.zip --dry-run  # preview only, write nothing
python tools/doll_uploader.py path/to/folder/              # extracted folder also works
python tools/doll_uploader.py --self-test                  # verify the flag parser
python tools/doll_uploader.py batch.zip --pr-mode gh       # override config for this run
```

It will:
1. Classify every model/texture/animation as a pokedoll or figurine (anything else is
   reported as unrecognized — e.g. decorative blocks that need Java changes).
2. Validate each doll has a base model + texture (in the batch or already in the repo). When a
   set of variant files has **no flagless base** (e.g. `sinistea` ships only `_antique` and
   `_phony`), it **asks** whether to register them as one base doll with flag variants (the
   usual answer, when each variant has its own model) or to skip and handle it manually.
3. **Prompt** for the rarity of each new doll and each non-auto variant. Shiny and gigantic
   variants are skipped — they resolve automatically and need no entry.
   Squeak textures are skipped too — see below.
4. Prompt for figurine display names (and optional tags like `cobblemon_team`). Figurine flag
   variants (e.g. `amongsans1015_devoured`) are attached to the base figurine automatically.
5. Print a full plan; on confirmation, copy files and append entries to
   `doll_rarity.json` / `figurine_names.json` / `figurine_tags.json`.
6. Run the configured PR step.

### Squeak textures

`pokedoll_<name>[_flags]_squeak[_<n>][_texture].png` is a **squeak texture** — the skin a doll
wears for the moment it is squeaked, with numbered files cycling one frame per squeak. The
uploader places them like any other texture and lists them under *Squeak textures* in the plan,
but it never derives a doll, a variant or a rarity entry from one: a squeak texture only re-skins
a variant that already exists.

The game only looks for the marker **after** the flag suffixes
(`pokedoll_bellibolt_shiny_squeak_1_texture.png`), but files often arrive with it right after the
name (`pokedoll_bellibolt_squeak_1_shiny_texture.png`). Both are accepted: the marker is moved to
the end on copy (the flag ordering is left alone — the game probes every ordering) and the plan
notes the move next to that variant's frame list.

It warns when

- the variant a squeak texture names has **no regular texture** in the batch or the repo (the
  game only shows a squeak texture for a variant it can already render),
- the numbers **skip or repeat** (they must run from `1` upwards — the game stops at the first
  gap), or
- numbered and unnumbered files are mixed for one variant (the numbered sequence wins).

### Configuration — `uploader_config.json`

Auto-created on first run:

```json
{
  "pr_mode": "manual",
  "remote": "origin",
  "base_branch": "master"
}
```

- **`pr_mode: "manual"`** — files are staged in the working tree and a drag/drop folder is
  written to `tools/output/upload-<timestamp>/` (mirrors the repo paths so you can drag it
  straight into GitHub's web uploader). The tool prints ready-to-run `git` commands and a
  GitHub *compare* URL.
- **`pr_mode: "gh"`** — uses the GitHub CLI: creates a new branch, stages and commits all
  changes locally, then **prompts for confirmation** before pushing to the remote and opening
  the PR (into `base_branch`) with `gh pr create`. If you decline, the local branch and commit
  are kept and the exact push/PR commands are printed so you can finish later. If `gh` isn't
  installed it offers to install it via `winget` and otherwise falls back to manual instructions.

CLI flags `--pr-mode` and `--branch` override the config for a single run.

### Maintenance

The flag tables are **read live at startup** from
`common/.../pokemon/ModelFlag.java` and `common/.../pokemon/FigurineFlag.java`, so adding a
flag to those enums is picked up automatically — no edit to the tool needed. The startup line
and `--self-test` print which source was used (`ModelFlag.java` vs the baked-in fallback that
kicks in only when the tool is run outside a checkout).

`parse_suffixes` still mirrors `PokemonRegistry.parseSuffixes` /
`FigurineRegistry.parseSuffixes`. **If you change that suffix-parsing logic in Java** (not just
add a flag), update the tool to match and run `python tools/doll_uploader.py --self-test`.
