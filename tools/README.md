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
2. Validate each doll has a base model + texture (in the batch or already in the repo).
3. **Prompt** for the rarity of each new doll and each non-auto variant. Shiny and gigantic
   variants are skipped — they resolve automatically and need no entry.
4. Prompt for figurine display names (and optional tags like `cobblemon_team`).
5. Print a full plan; on confirmation, copy files and append entries to
   `doll_rarity.json` / `figurine_names.json` / `figurine_tags.json`.
6. Run the configured PR step.

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

The `FLAGS` table and `parse_suffixes` in `doll_uploader.py` mirror
`common/src/main/java/dev/mrshawn/pokeblocks/pokemon/ModelFlag.java` and
`PokemonRegistry.parseSuffixes`. **If you change the flag enum or the suffix-parsing logic
in Java, update the tool to match** and run `python tools/doll_uploader.py --self-test`.
