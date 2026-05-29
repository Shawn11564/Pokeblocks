#!/usr/bin/env python3
"""
Pokeblocks doll/figurine asset uploader.

Drop in a received zip (or an already-extracted folder) whose root holds
``<dex>_<name>`` subfolders of assets, and this tool:

  1. Sorts every model/texture/animation into the correct source directory.
  2. Edits the override JSON files needed to load the dolls/variants
     (doll_rarity.json, figurine_names.json, figurine_tags.json), prompting
     for anything that can't be auto-determined (mainly rarity).
  3. Either opens a GitHub PR via the `gh` CLI, or stages the files and writes
     a drag/drop output folder for a manual web upload (configurable).

This is a dev-side tool that edits the repo's *source* assets under
common/src/main/resources/assets/pokeblocks/ so the new dolls ship in a PR.

See tools/README.md for usage. The FLAGS table below mirrors ModelFlag.java and
the parsing mirrors PokemonRegistry.parseSuffixes — keep them in sync.
"""
from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
import zipfile
from dataclasses import dataclass, field
from datetime import datetime
from pathlib import Path

# --------------------------------------------------------------------------- #
# Repo layout
# --------------------------------------------------------------------------- #
REPO_ROOT = Path(__file__).resolve().parent.parent
ASSETS = REPO_ROOT / "common" / "src" / "main" / "resources" / "assets" / "pokeblocks"
GEO_DIR = ASSETS / "geo" / "block"
TEX_DIR = ASSETS / "textures" / "block"
ANIM_DIR = ASSETS / "animations" / "block"
DOLL_RARITY_JSON = ASSETS / "doll_rarity.json"
FIGURINE_NAMES_JSON = ASSETS / "figurine_names.json"
FIGURINE_TAGS_JSON = ASSETS / "figurine_tags.json"

CONFIG_PATH = Path(__file__).resolve().parent / "uploader_config.json"
OUTPUT_ROOT = Path(__file__).resolve().parent / "output"

DEFAULT_CONFIG = {
    "pr_mode": "manual",      # "manual" or "gh"
    "remote": "origin",
    "base_branch": "master",
}

RARITIES = ["none", "common", "uncommon", "rare", "epic", "legendary", "shiny", "gigantic"]


# --------------------------------------------------------------------------- #
# Model flags — MUST stay in sync with
# common/.../pokemon/ModelFlag.java
# --------------------------------------------------------------------------- #
@dataclass(frozen=True)
class Flag:
    tag: str
    texture_suffix: str
    model_suffix: str
    sort_order: int
    auto_rarity: str | None      # rarity resolved automatically without an entry
    exclusion_group: str | None


# Declaration order matches ModelFlag.values() — parse_suffixes relies on it.
FLAGS: list[Flag] = [
    Flag("gigantic", "", "", -1, "gigantic", None),
    Flag("shiny", "_shiny", "", 0, "shiny", None),
    Flag("family", "_family", "_family", 1, None, None),
    Flag("animated", "_animated", "_animated", 2, None, None),
    Flag("posed", "_posed", "_posed", 3, None, None),
    Flag("netherite", "_netherite", "", 4, None, None),
    Flag("zenith", "_zenith", "_zenith", 5, None, None),
    Flag("noice", "_noice", "_noice", 6, None, None),
    Flag("spiky", "_spiky", "_spiky", 7, None, None),
    Flag("eared", "_eared", "_eared", 8, None, None),
    Flag("male", "_male", "", 9, None, "gender"),
    Flag("female", "_female", "", 10, None, "gender"),
]
FLAG_BY_TAG = {f.tag: f for f in FLAGS}


def parse_suffixes(body: str) -> tuple[str, set[Flag]]:
    """Port of PokemonRegistry.parseSuffixes.

    Strips a trailing ``_texture`` then greedily strips flag suffixes (texture
    suffix or model suffix) until none match. Returns (name, {flags}).
    """
    flags: set[Flag] = set()
    remaining = body
    low = remaining.lower()
    if low.endswith("_texture"):
        remaining = remaining[: -len("_texture")]

    found = True
    while found:
        found = False
        low = remaining.lower()
        for flag in FLAGS:
            tex, mod = flag.texture_suffix, flag.model_suffix
            if tex and low.endswith(tex):
                flags.add(flag)
                remaining = remaining[: -len(tex)]
                found = True
                break
            if mod and mod != tex and low.endswith(mod):
                flags.add(flag)
                remaining = remaining[: -len(mod)]
                found = True
                break
    return remaining.lower(), flags


def sorted_flags(flags: set[Flag]) -> list[Flag]:
    return sorted(flags, key=lambda f: f.sort_order)


def rarity_key(name: str, flags: set[Flag]) -> str:
    """Canonical key for dedup against doll_rarity.json (name + sorted tags)."""
    parts = [name] + [f.tag for f in sorted_flags(flags)]
    return " ".join(parts)


# --------------------------------------------------------------------------- #
# Filename classification
# --------------------------------------------------------------------------- #
RE_DOLL_MODEL = re.compile(r"^pokedoll_(.+)\.geo\.json$", re.IGNORECASE)
RE_DOLL_TEX = re.compile(r"^pokedoll_(.+)\.png$", re.IGNORECASE)
RE_DOLL_ANIM = re.compile(r"^pokedoll_(.+)\.animation\.json$", re.IGNORECASE)
RE_FIG_MODEL = re.compile(r"^(.+)_figurine\.geo\.json$", re.IGNORECASE)
RE_FIG_TEX = re.compile(r"^(.+)_figurine(?:_texture)?\.png$", re.IGNORECASE)
RE_DEX_FOLDER = re.compile(r"^\d+[_\- ]+(.+)$")


@dataclass
class Asset:
    src: Path
    kind: str          # doll-model | doll-tex | doll-anim | fig-model | fig-tex
    name: str          # base pokemon / figurine id (lowercase)
    flags: set[Flag]
    dest_name: str     # normalized destination filename
    dest_dir: Path
    folder_hint: str | None = None


def classify(src: Path) -> Asset | None:
    fn = src.name
    # Figurines first only when there is no pokedoll_ prefix.
    if not fn.lower().startswith("pokedoll_"):
        m = RE_FIG_MODEL.match(fn)
        if m:
            return Asset(src, "fig-model", m.group(1).lower(), set(),
                         f"{m.group(1).lower()}_figurine.geo.json", GEO_DIR)
        m = RE_FIG_TEX.match(fn)
        if m:
            fid = m.group(1).lower()
            return Asset(src, "fig-tex", fid, set(),
                         f"{fid}_figurine_texture.png", TEX_DIR)

    m = RE_DOLL_MODEL.match(fn)
    if m:
        name, flags = parse_suffixes(m.group(1))
        return Asset(src, "doll-model", name, flags, fn.lower(), GEO_DIR)
    m = RE_DOLL_ANIM.match(fn)
    if m:
        name, flags = parse_suffixes(m.group(1))
        return Asset(src, "doll-anim", name, flags, fn.lower(), ANIM_DIR)
    m = RE_DOLL_TEX.match(fn)
    if m:
        name, flags = parse_suffixes(m.group(1))
        # Normalize to the _texture.png convention.
        body = m.group(1)
        dest = fn.lower() if body.lower().endswith("_texture") else f"pokedoll_{body.lower()}_texture.png"
        return Asset(src, "doll-tex", name, flags, dest, TEX_DIR)
    return None


# --------------------------------------------------------------------------- #
# I/O helpers
# --------------------------------------------------------------------------- #
def load_config() -> dict:
    cfg = dict(DEFAULT_CONFIG)
    if CONFIG_PATH.exists():
        try:
            cfg.update(json.loads(CONFIG_PATH.read_text()))
        except Exception as e:  # noqa: BLE001
            print(f"! Could not read {CONFIG_PATH.name}, using defaults: {e}")
    else:
        CONFIG_PATH.write_text(json.dumps(DEFAULT_CONFIG, indent=2) + "\n")
        print(f"Created default config at {CONFIG_PATH}")
    return cfg


def load_json_array(path: Path) -> list[str]:
    if not path.exists():
        return []
    return json.loads(path.read_text())


def write_json_array(path: Path, arr: list[str]) -> None:
    path.write_text(json.dumps(arr, indent=2) + "\n")


def existing_rarity_keys(arr: list[str]) -> set[str]:
    keys = set()
    for entry in arr:
        tokens = entry.split()
        if len(tokens) < 2:
            continue
        name = tokens[0].lower()
        flag_tokens = [t.lower() for t in tokens[1:-1]]
        flags = {FLAG_BY_TAG[t] for t in flag_tokens if t in FLAG_BY_TAG}
        keys.add(rarity_key(name, flags))
    return keys


def existing_name_ids(arr: list[str]) -> set[str]:
    return {e.split()[0].lower() for e in arr if e.split()}


def ask(prompt: str, default: str | None = None) -> str:
    if not sys.stdin.isatty():
        if default is not None:
            print(f"{prompt} [non-interactive -> {default!r}]")
            return default
        raise RuntimeError(f"Input required but stdin is not interactive: {prompt}")
    suffix = f" [{default}]" if default else ""
    resp = input(f"{prompt}{suffix}: ").strip()
    return resp or (default or "")


def ask_rarity(label: str, default: str = "common") -> str:
    while True:
        r = ask(f"  Rarity for {label} (one of: {', '.join(RARITIES)})", default).lower()
        if r in RARITIES:
            return r
        print(f"    '{r}' is not a valid rarity.")


def confirm(prompt: str, default: bool = True) -> bool:
    if not sys.stdin.isatty():
        return default
    d = "Y/n" if default else "y/N"
    resp = input(f"{prompt} [{d}]: ").strip().lower()
    if not resp:
        return default
    return resp.startswith("y")


# --------------------------------------------------------------------------- #
# Core processing
# --------------------------------------------------------------------------- #
@dataclass
class Plan:
    copies: list[tuple[Path, Path]] = field(default_factory=list)   # (src, dest)
    renames: list[tuple[str, str]] = field(default_factory=list)    # (orig, dest) names
    overwrites: list[Path] = field(default_factory=list)
    rarity_entries: list[str] = field(default_factory=list)
    name_entries: list[str] = field(default_factory=list)
    tag_entries: list[str] = field(default_factory=list)
    unknown: list[Path] = field(default_factory=list)
    warnings: list[str] = field(default_factory=list)
    doll_names: set[str] = field(default_factory=set)
    figurine_ids: set[str] = field(default_factory=set)


def gather_files(root: Path) -> list[Path]:
    return [p for p in sorted(root.rglob("*")) if p.is_file()]


def repo_has_base_model(name: str) -> bool:
    return (GEO_DIR / f"pokedoll_{name}.geo.json").exists()


def repo_has_base_texture(name: str) -> bool:
    return (TEX_DIR / f"pokedoll_{name}_texture.png").exists() or (TEX_DIR / f"pokedoll_{name}.png").exists()


def build_plan(src_root: Path, dry_run: bool) -> Plan:
    plan = Plan()
    assets: list[Asset] = []

    for f in gather_files(src_root):
        a = classify(f)
        if a is None:
            # Ignore obvious non-assets quietly; report the rest.
            if f.suffix.lower() in (".png", ".json") or f.name.endswith(".geo.json"):
                plan.unknown.append(f)
            continue
        # Folder hint for cross-checking the <dex>_<name> convention.
        parent = f.parent.name
        fm = RE_DEX_FOLDER.match(parent)
        a.folder_hint = fm.group(1).lower().replace(" ", "_") if fm else None
        assets.append(a)

    # --- Group dolls -------------------------------------------------------- #
    doll_assets = [a for a in assets if a.kind.startswith("doll")]
    fig_assets = [a for a in assets if a.kind.startswith("fig")]

    doll_names = sorted({a.name for a in doll_assets})
    for name in doll_names:
        group = [a for a in doll_assets if a.name == name]
        models = [a for a in group if a.kind == "doll-model"]
        texes = [a for a in group if a.kind == "doll-tex"]

        has_base_model = any(not a.flags for a in models) or repo_has_base_model(name)
        has_base_tex = any(not a.flags for a in texes) or repo_has_base_texture(name)
        if not has_base_model:
            plan.warnings.append(
                f"Doll '{name}': no base model (pokedoll_{name}.geo.json) in batch or repo — it will NOT load.")
        if not has_base_tex:
            plan.warnings.append(
                f"Doll '{name}': no base texture (pokedoll_{name}_texture.png) in batch or repo.")

        # Folder-name vs file-name mismatch check.
        hints = {a.folder_hint for a in group if a.folder_hint}
        for h in hints:
            if h and h != name:
                plan.warnings.append(
                    f"Folder name suggests '{h}' but files parse as '{name}' — verify the pokemon name.")

        plan.doll_names.add(name)

    # --- Schedule file copies ---------------------------------------------- #
    for a in doll_assets + fig_assets:
        dest = a.dest_dir / a.dest_name
        if a.src.name != a.dest_name:
            plan.renames.append((a.src.name, a.dest_name))
        if dest.exists():
            plan.overwrites.append(dest)
        plan.copies.append((a.src, dest))

    # --- doll_rarity.json edits -------------------------------------------- #
    existing_arr = load_json_array(DOLL_RARITY_JSON)
    existing_keys = existing_rarity_keys(existing_arr)
    planned_keys: set[str] = set()

    # Variant combos that the registry would see = union of model/tex/anim flag sets.
    for name in sorted(plan.doll_names):
        combos: set[frozenset[Flag]] = set()
        for a in doll_assets:
            if a.name == name:
                combos.add(frozenset(a.flags))
        combos.add(frozenset())  # base always
        for combo in sorted(combos, key=lambda c: (len(c), sorted(f.sort_order for f in c))):
            cset = set(combo)
            # Skip auto-resolved variants (shiny / gigantic need no entry).
            if any(f.auto_rarity for f in cset):
                continue
            key = rarity_key(name, cset)
            if key in existing_keys or key in planned_keys:
                continue
            planned_keys.add(key)
            label = key  # e.g. "pikachu posed" or just "pikachu"
            if dry_run:
                plan.rarity_entries.append(f"{key} <PROMPT-RARITY>")
            else:
                r = ask_rarity(label, default="common")
                plan.rarity_entries.append(f"{key} {r}")

    # --- figurine_names.json / figurine_tags.json -------------------------- #
    fig_ids = sorted({a.name for a in fig_assets})
    name_arr = load_json_array(FIGURINE_NAMES_JSON)
    tag_arr = load_json_array(FIGURINE_TAGS_JSON)
    have_names = existing_name_ids(name_arr)
    have_tags = existing_name_ids(tag_arr)

    for fid in fig_ids:
        plan.figurine_ids.add(fid)
        # Validate figurine has both model + texture (in batch or repo).
        has_model = any(a.kind == "fig-model" and a.name == fid for a in fig_assets) or \
            (GEO_DIR / f"{fid}_figurine.geo.json").exists()
        has_tex = any(a.kind == "fig-tex" and a.name == fid for a in fig_assets) or \
            (TEX_DIR / f"{fid}_figurine_texture.png").exists() or (TEX_DIR / f"{fid}_figurine.png").exists()
        if not has_model:
            plan.warnings.append(f"Figurine '{fid}': missing model {fid}_figurine.geo.json.")
        if not has_tex:
            plan.warnings.append(f"Figurine '{fid}': missing texture {fid}_figurine_texture.png — it will NOT load.")

        if fid not in have_names:
            default_name = "_".join(w.capitalize() for w in fid.split("_"))
            if dry_run:
                plan.name_entries.append(f"{fid} <PROMPT-NAME ({default_name})>")
            else:
                disp = ask(f"  Display name for figurine '{fid}'", default_name)
                if " " in disp:
                    print("    note: figurine display names should be a single token; spaces may be truncated.")
                plan.name_entries.append(f"{fid} {disp}")

        if fid not in have_tags:
            if dry_run:
                plan.tag_entries.append(f"{fid} <PROMPT-TAGS?>")
            elif confirm(f"  Add tags for figurine '{fid}' (e.g. cobblemon_team)?", default=False):
                tags = ask(f"    Tags for '{fid}' (space-separated)", "").strip()
                if tags:
                    plan.tag_entries.append(f"{fid} {tags}")

    return plan


# --------------------------------------------------------------------------- #
# Reporting / apply
# --------------------------------------------------------------------------- #
def print_summary(plan: Plan) -> None:
    print("\n" + "=" * 70)
    print("PLAN SUMMARY")
    print("=" * 70)

    print(f"\nDolls detected: {', '.join(sorted(plan.doll_names)) or '(none)'}")
    print(f"Figurines detected: {', '.join(sorted(plan.figurine_ids)) or '(none)'}")

    print(f"\nFiles to place ({len(plan.copies)}):")
    for src, dest in plan.copies:
        rel = dest.relative_to(REPO_ROOT)
        flag = "  [OVERWRITE]" if dest in plan.overwrites else ""
        print(f"  {src.name}  ->  {rel}{flag}")

    if plan.renames:
        print(f"\nNormalized filenames ({len(plan.renames)}):")
        for orig, dest in plan.renames:
            print(f"  {orig}  ->  {dest}")

    print(f"\ndoll_rarity.json additions ({len(plan.rarity_entries)}):")
    for e in plan.rarity_entries:
        print(f"  + \"{e}\"")

    if plan.name_entries:
        print(f"\nfigurine_names.json additions ({len(plan.name_entries)}):")
        for e in plan.name_entries:
            print(f"  + \"{e}\"")
    if plan.tag_entries:
        print(f"\nfigurine_tags.json additions ({len(plan.tag_entries)}):")
        for e in plan.tag_entries:
            print(f"  + \"{e}\"")

    if plan.unknown:
        print(f"\nUnrecognized files (NOT handled -- likely decorative blocks needing Java changes, "
              f"or stray files):")
        for u in plan.unknown:
            print(f"  ? {u.name}")

    if plan.warnings:
        print("\nWARNINGS:")
        for w in plan.warnings:
            print(f"  ! {w}")
    print()


def apply_plan(plan: Plan, mirror_dir: Path | None) -> list[Path]:
    """Copies files and edits override JSON. Returns list of changed repo paths."""
    changed: list[Path] = []

    for src, dest in plan.copies:
        dest.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(src, dest)
        changed.append(dest)
        if mirror_dir is not None:
            mdest = mirror_dir / dest.relative_to(REPO_ROOT)
            mdest.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(src, mdest)

    if plan.rarity_entries:
        arr = load_json_array(DOLL_RARITY_JSON)
        arr.extend(plan.rarity_entries)
        write_json_array(DOLL_RARITY_JSON, arr)
        changed.append(DOLL_RARITY_JSON)
    if plan.name_entries:
        arr = load_json_array(FIGURINE_NAMES_JSON)
        arr.extend(plan.name_entries)
        write_json_array(FIGURINE_NAMES_JSON, arr)
        changed.append(FIGURINE_NAMES_JSON)
    if plan.tag_entries:
        arr = load_json_array(FIGURINE_TAGS_JSON)
        arr.extend(plan.tag_entries)
        write_json_array(FIGURINE_TAGS_JSON, arr)
        changed.append(FIGURINE_TAGS_JSON)

    if mirror_dir is not None:
        for jp in (DOLL_RARITY_JSON, FIGURINE_NAMES_JSON, FIGURINE_TAGS_JSON):
            if jp in changed:
                mdest = mirror_dir / jp.relative_to(REPO_ROOT)
                mdest.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(jp, mdest)

    return changed


# --------------------------------------------------------------------------- #
# Git / PR
# --------------------------------------------------------------------------- #
def run_git(args: list[str], check: bool = True) -> subprocess.CompletedProcess:
    return subprocess.run(["git", "-C", str(REPO_ROOT), *args],
                          check=check, text=True, capture_output=True)


def remote_web_url(remote: str) -> str | None:
    try:
        url = run_git(["remote", "get-url", remote]).stdout.strip()
    except subprocess.CalledProcessError:
        return None
    if url.startswith("git@"):  # git@github.com:owner/repo.git
        url = "https://" + url[4:].replace(":", "/", 1)
    if url.endswith(".git"):
        url = url[:-4]
    return url


def default_branch_name(plan: Plan) -> str:
    bits = sorted(plan.doll_names | plan.figurine_ids)
    stamp = datetime.now().strftime("%Y%m%d")
    slug = "-".join(bits[:3]) if bits else "assets"
    if len(bits) > 3:
        slug += f"-and-{len(bits) - 3}-more"
    return f"dolls/{stamp}-{slug}"[:60]


def args_branch_or(default: str, cfg: dict) -> str:
    """Honor a --branch override (stored in cfg) when present."""
    return cfg.get("branch") or default


def pr_body(plan: Plan) -> str:
    lines = ["Automated doll/figurine upload.", ""]
    if plan.doll_names:
        lines.append("**Dolls:** " + ", ".join(sorted(plan.doll_names)))
    if plan.figurine_ids:
        lines.append("**Figurines:** " + ", ".join(sorted(plan.figurine_ids)))
    lines += ["", f"Files added/updated: {len(plan.copies)}.",
              f"Rarity entries: {len(plan.rarity_entries)}."]
    return "\n".join(lines)


def do_manual(plan: Plan, changed: list[Path], mirror_dir: Path, cfg: dict) -> None:
    base = cfg["base_branch"]
    web = remote_web_url(cfg["remote"])
    branch = args_branch_or(default_branch_name(plan), cfg)
    print("\n" + "=" * 70)
    print("MANUAL MODE -- files staged in the working tree")
    print("=" * 70)
    print(f"\nChanged paths ({len(changed)}):")
    for c in changed:
        print(f"  {c.relative_to(REPO_ROOT)}")
    print(f"\nDrag/drop upload folder (mirrors repo paths for GitHub web upload):\n  {mirror_dir}")
    add_args = " ".join(f'"{c.relative_to(REPO_ROOT)}"' for c in changed)
    print("\nTo commit & push manually (stages only the files above):")
    print(f"  git checkout -b {branch}")
    print(f"  git add -- {add_args}")
    print(f'  git commit -m "Add dolls: {", ".join(sorted(plan.doll_names | plan.figurine_ids)) or "assets"}"')
    print(f"  git push -u {cfg['remote']} {branch}")
    if web:
        print(f"\nThen open a PR:\n  {web}/compare/{base}...{branch}?expand=1")


def find_gh() -> str | None:
    """Locate the gh executable.

    ``shutil.which`` only checks the current process PATH, which on Windows often
    misses a freshly-installed GitHub CLI (the installer updates PATH but the running
    shell doesn't see it). So also probe the well-known install locations and return
    the full path to invoke directly.
    """
    exe = shutil.which("gh")
    if exe:
        return exe

    candidates: list[Path] = []
    for env in ("ProgramFiles", "ProgramFiles(x86)", "ProgramW6432"):
        base = os.environ.get(env)
        if base:
            candidates.append(Path(base) / "GitHub CLI" / "gh.exe")
    local = os.environ.get("LOCALAPPDATA")
    if local:
        candidates.append(Path(local) / "Microsoft" / "WinGet" / "Links" / "gh.exe")
        candidates.append(Path(local) / "Programs" / "GitHub CLI" / "gh.exe")
    for c in candidates:
        if c.exists():
            return str(c)
    return None


def do_gh(plan: Plan, changed: list[Path], cfg: dict) -> None:
    gh = find_gh()
    if gh is None:
        print("\n`gh` (GitHub CLI) was not found on PATH or in the usual install locations.")
        if confirm("Install it now via winget?", default=False):
            subprocess.run(["winget", "install", "--id", "GitHub.cli", "-e",
                            "--accept-source-agreements", "--accept-package-agreements"], check=False)
            gh = find_gh()
        if gh is None:
            print("gh still unavailable. If you just installed it, open a NEW terminal so PATH "
                  "refreshes and re-run, or set pr_mode to manual. Falling back to manual instructions.")
            do_manual(plan, changed, OUTPUT_ROOT, cfg)
            return
    print(f"Using gh: {gh}")

    branch = args_branch_or(default_branch_name(plan), cfg)
    base = cfg["base_branch"]
    remote = cfg["remote"]
    title = "Add dolls: " + (", ".join(sorted(plan.doll_names | plan.figurine_ids)) or "assets")

    # 1. Create the branch and stage + commit ONLY the paths we placed/edited.
    #    (Never `git add -A` — that would sweep in unrelated working-tree changes,
    #    the tool itself, caches, etc.)
    run_git(["checkout", "-b", branch])
    rel_paths = [str(p.relative_to(REPO_ROOT)) for p in changed]
    run_git(["add", "--", *rel_paths])
    run_git(["commit", "-m", title])
    print(f"\nBranch '{branch}' created and {len(rel_paths)} path(s) committed locally.")

    # 2. Confirm before pushing and opening the PR.
    if not confirm(f"Push '{branch}' to '{remote}' and open a PR into '{base}'?", default=True):
        print("\nPR not opened. The branch and commit are kept locally. To finish later:")
        print(f"  git push -u {remote} {branch}")
        print(f"  gh pr create --base {base} --head {branch} --title \"{title}\"")
        return

    # 3. Push and open the PR automatically.
    run_git(["push", "-u", remote, branch])
    subprocess.run([gh, "pr", "create", "--base", base, "--head", branch,
                    "--title", title, "--body", pr_body(plan)],
                   cwd=str(REPO_ROOT), check=False)


# --------------------------------------------------------------------------- #
# Self-test
# --------------------------------------------------------------------------- #
def self_test() -> int:
    cases = [
        ("pokedoll_pichu_spiky_eared.png", "pichu", {"spiky", "eared"}),
        ("pokedoll_absol_shiny_texture.png", "absol", {"shiny"}),
        ("pokedoll_gholdengo_netherite.png", "gholdengo", {"netherite"}),
        ("pokedoll_calyrex_animated.geo.json", "calyrex", {"animated"}),
        ("pokedoll_combee_female.png", "combee", {"female"}),
        ("pokedoll_snorunt_family_animated.geo.json", "snorunt", {"family", "animated"}),
        ("pokedoll_eevee_texture.png", "eevee", set()),
        ("pokedoll_marshadow_zenith.geo.json", "marshadow", {"zenith"}),
    ]
    ok = True
    for fn, exp_name, exp_tags in cases:
        body = re.sub(r"\.(geo\.json|animation\.json|png)$", "", fn, flags=re.IGNORECASE)
        body = re.sub(r"^pokedoll_", "", body, flags=re.IGNORECASE)
        name, flags = parse_suffixes(body)
        tags = {f.tag for f in flags}
        status = "ok " if (name == exp_name and tags == exp_tags) else "FAIL"
        if status == "FAIL":
            ok = False
        print(f"  [{status}] {fn} -> name={name!r} flags={sorted(tags)} (expected {exp_name!r} {sorted(exp_tags)})")

    # Classification smoke checks.
    fig = classify(Path("doncheadle_figurine.geo.json"))
    assert fig and fig.kind == "fig-model" and fig.name == "doncheadle", "figurine model classify"
    tex = classify(Path("pokedoll_pikachu_shiny.png"))
    assert tex and tex.dest_name == "pokedoll_pikachu_shiny_texture.png", "texture normalization"
    print("  [ok ] classification smoke checks")
    print("\nSELF-TEST PASSED" if ok else "\nSELF-TEST FAILED")
    return 0 if ok else 1


# --------------------------------------------------------------------------- #
# Main
# --------------------------------------------------------------------------- #
def main() -> int:
    ap = argparse.ArgumentParser(description="Pokeblocks doll/figurine asset uploader.")
    ap.add_argument("source", nargs="?", help="Path to the received .zip or an extracted folder.")
    ap.add_argument("--pr-mode", choices=["manual", "gh"], help="Override config pr_mode.")
    ap.add_argument("--branch", help="Branch name to use (gh mode / suggested in manual mode).")
    ap.add_argument("--dry-run", action="store_true", help="Show the plan without writing anything.")
    ap.add_argument("--self-test", action="store_true", help="Run the flag-parser self-test and exit.")
    args = ap.parse_args()

    if args.self_test:
        return self_test()
    if not args.source:
        ap.error("a source zip/folder is required (or use --self-test)")

    if not ASSETS.exists():
        print(f"ERROR: assets dir not found at {ASSETS} — run this from within the repo.")
        return 2

    cfg = load_config()
    if args.pr_mode:
        cfg["pr_mode"] = args.pr_mode
    if args.branch:
        cfg["branch"] = args.branch

    source = Path(args.source).expanduser().resolve()
    if not source.exists():
        print(f"ERROR: source not found: {source}")
        return 2

    tmp: tempfile.TemporaryDirectory | None = None
    if source.is_file() and source.suffix.lower() == ".zip":
        tmp = tempfile.TemporaryDirectory(prefix="doll_upload_")
        with zipfile.ZipFile(source) as zf:
            zf.extractall(tmp.name)
        src_root = Path(tmp.name)
        print(f"Extracted {source.name} -> {src_root}")
    elif source.is_dir():
        src_root = source
    else:
        print(f"ERROR: source must be a .zip or a folder: {source}")
        return 2

    try:
        plan = build_plan(src_root, dry_run=args.dry_run)
        print_summary(plan)

        if args.dry_run:
            print("(dry run -- nothing written)")
            return 0

        if not plan.copies and not plan.rarity_entries:
            print("Nothing to do.")
            return 0

        if not confirm("Apply this plan?", default=True):
            print("Aborted.")
            return 1

        mirror_dir = OUTPUT_ROOT / f"upload-{datetime.now().strftime('%Y%m%d-%H%M%S')}"
        use_mirror = cfg["pr_mode"] == "manual"
        changed = apply_plan(plan, mirror_dir if use_mirror else None)
        print(f"\nApplied: {len(changed)} path(s) changed.")

        if cfg["pr_mode"] == "gh":
            do_gh(plan, changed, cfg)
        else:
            do_manual(plan, changed, mirror_dir, cfg)
        return 0
    finally:
        if tmp is not None:
            tmp.cleanup()


if __name__ == "__main__":
    sys.exit(main())
