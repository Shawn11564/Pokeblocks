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

See tools/README.md for usage. The flag tables are read live from ModelFlag.java
and FigurineFlag.java at startup (so they can't drift), and the parsing mirrors
PokemonRegistry.parseSuffixes / FigurineRegistry.parseSuffixes.

When a group of variant files has no flagless base (e.g. sinistea only ships
antique + phony), the tool detects it and asks whether to register them as one
base doll with flag variants (the usual answer) or handle it manually.
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
# Model / figurine flags.
#
# The flag tables below are DERIVED AT RUNTIME from the mod's own enum source
# (ModelFlag.java / FigurineFlag.java) so the uploader can never drift out of
# sync with the game. If those files can't be read/parsed (e.g. the tool is run
# outside a checkout) we fall back to the baked-in tables, which are only a
# best-effort snapshot. `python doll_uploader.py --self-test` prints the source.
# --------------------------------------------------------------------------- #
MODELFLAG_JAVA = REPO_ROOT / "common" / "src" / "main" / "java" / "dev" / "mrshawn" / "pokeblocks" / "pokemon" / "ModelFlag.java"
FIGURINEFLAG_JAVA = REPO_ROOT / "common" / "src" / "main" / "java" / "dev" / "mrshawn" / "pokeblocks" / "pokemon" / "FigurineFlag.java"


@dataclass(frozen=True)
class Flag:
    tag: str
    texture_suffix: str
    model_suffix: str
    sort_order: int
    auto_rarity: str | None      # rarity resolved automatically without an entry (dolls only)
    exclusion_group: str | None


# ModelFlag constant:  NAME("tag", "texSuffix", "modelSuffix", sortOrder, DollRarity.X, null|"group"),
_MODELFLAG_RE = re.compile(
    r'^\s*[A-Z][A-Z0-9_]*\(\s*'
    r'"([^"]*)"\s*,\s*'          # tag
    r'"([^"]*)"\s*,\s*'          # texture suffix
    r'"([^"]*)"\s*,\s*'          # model suffix
    r'(-?\d+)\s*,\s*'            # sort order
    r'DollRarity\.(\w+)\s*,\s*'  # rarity
    r'(null|"[^"]*")\s*\)',      # exclusion group
    re.MULTILINE,
)
# FigurineFlag constant:  NAME("tag", "texSuffix", "modelSuffix", sortOrder, null|"group"),
_FIGFLAG_RE = re.compile(
    r'^\s*[A-Z][A-Z0-9_]*\(\s*'
    r'"([^"]*)"\s*,\s*'          # tag
    r'"([^"]*)"\s*,\s*'          # texture suffix
    r'"([^"]*)"\s*,\s*'          # model suffix
    r'(-?\d+)\s*,\s*'            # sort order
    r'(null|"[^"]*")\s*\)',      # exclusion group
    re.MULTILINE,
)


def _excl(token: str) -> str | None:
    return None if token == "null" else token.strip('"')


def _load_model_flags(path: Path) -> list[Flag] | None:
    """Parse ModelFlag.java's enum constants (declaration order == values() order)."""
    try:
        text = path.read_text(encoding="utf-8")
    except OSError:
        return None
    flags = [
        Flag(tag, tex, mod, int(order),
             None if rarity.upper() == "NONE" else rarity.lower(), _excl(excl))
        for tag, tex, mod, order, rarity, excl in (m.groups() for m in _MODELFLAG_RE.finditer(text))
    ]
    return flags or None


def _load_fig_flags(path: Path) -> list[Flag] | None:
    """Parse FigurineFlag.java's enum constants (figurines have no auto-rarity)."""
    try:
        text = path.read_text(encoding="utf-8")
    except OSError:
        return None
    flags = [
        Flag(tag, tex, mod, int(order), None, _excl(excl))
        for tag, tex, mod, order, excl in (m.groups() for m in _FIGFLAG_RE.finditer(text))
    ]
    return flags or None


# Baked-in fallbacks — a best-effort snapshot; the live source above wins when present.
_FALLBACK_FLAGS: list[Flag] = [
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
    Flag("phony", "_phony", "_phony", 11, None, "antique"),
    Flag("antique", "_antique", "_antique", 12, None, "antique"),
]
_FALLBACK_FIG_FLAGS: list[Flag] = [
    Flag("devoured", "_devoured", "_devoured", 0, None, None),
]

_loaded_model = _load_model_flags(MODELFLAG_JAVA)
_loaded_fig = _load_fig_flags(FIGURINEFLAG_JAVA)

FLAGS: list[Flag] = _loaded_model or _FALLBACK_FLAGS
FIG_FLAGS: list[Flag] = _loaded_fig or _FALLBACK_FIG_FLAGS
FLAGS_SOURCE = "ModelFlag.java" if _loaded_model else "baked-in fallback"
FIG_FLAGS_SOURCE = "FigurineFlag.java" if _loaded_fig else "baked-in fallback"

FLAG_BY_TAG = {f.tag: f for f in FLAGS}
FIG_FLAG_BY_TAG = {f.tag: f for f in FIG_FLAGS}


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


def parse_fig_suffixes(body: str) -> tuple[str, set[Flag]]:
    """Port of FigurineRegistry.parseSuffixes — strips figurine-flag suffixes.

    ``body`` is the part before ``_figurine`` (a trailing ``_texture`` is already
    consumed by the figurine texture regex). Returns (base_id, {fig_flags}).
    """
    flags: set[Flag] = set()
    remaining = body
    found = True
    while found:
        found = False
        low = remaining.lower()
        for flag in FIG_FLAGS:
            tex, mod = flag.texture_suffix, flag.model_suffix
            if mod and low.endswith(mod):
                flags.add(flag)
                remaining = remaining[: -len(mod)]
                found = True
                break
            if tex and tex != mod and low.endswith(tex):
                flags.add(flag)
                remaining = remaining[: -len(tex)]
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
            body = m.group(1).lower()
            base, flags = parse_fig_suffixes(body)
            # dest keeps the full variant filename; name is the base figurine id.
            return Asset(src, "fig-model", base, flags,
                         f"{body}_figurine.geo.json", GEO_DIR)
        m = RE_FIG_TEX.match(fn)
        if m:
            body = m.group(1).lower()
            base, flags = parse_fig_suffixes(body)
            return Asset(src, "fig-tex", base, flags,
                         f"{body}_figurine_texture.png", TEX_DIR)

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
    # name -> sorted variant flag tags, for dolls registered without a flagless base model.
    no_base_dolls: dict[str, list[str]] = field(default_factory=dict)


def gather_files(root: Path) -> list[Path]:
    return [p for p in sorted(root.rglob("*")) if p.is_file()]


def resolve_no_base(name: str, group: list[Asset], dry_run: bool) -> str:
    """Decide how to handle a doll group that has no flagless base model.

    Returns "merge" (register as one base doll whose flag variants each use their
    own model) or "skip" (leave for manual handling). The recommendation, and the
    non-interactive/dry-run default, is "merge" when every variant has its own
    model file — the case the mod is set up for.
    """
    model_flagsets = {frozenset(a.flags) for a in group if a.kind == "doll-model"}
    tex_flagsets = {frozenset(a.flags) for a in group if a.kind == "doll-tex"}
    variant_labels = sorted(
        ", ".join(t.tag for t in sorted_flags(set(fs))) or "(flagless)" for fs in model_flagsets)

    # "Each file has its own geo" == every texture variant is backed by a model whose flags
    # are a subset of it (the model gives the geometry; extra texture-only flags like shiny
    # just re-skin it). Uses flag SETS from the actual files, so it's robust even if a flag's
    # model-suffix in the Java is out of step with its filename.
    each_has_model = bool(model_flagsets) and all(
        any(ms <= tc for ms in model_flagsets) for tc in tex_flagsets)
    orphan_tex = sorted(
        ", ".join(t.tag for t in sorted_flags(set(tc)))
        for tc in tex_flagsets if not any(ms <= tc for ms in model_flagsets))

    print(f"\n  Doll '{name}' has NO flagless base model (pokedoll_{name}.geo.json).")
    print(f"    Variant models present: {', '.join(variant_labels) or '(none)'}")
    if orphan_tex:
        print(f"    ! Texture variants with no matching model: {'; '.join(orphan_tex)} -- verify before merging.")

    recommended = "merge" if each_has_model else "skip"
    if dry_run or not sys.stdin.isatty():
        print(f"    -> defaulting to '{recommended}'"
              + (" (register as one base doll with flag variants)." if recommended == "merge"
                 else " (skip; handle manually)."))
        return recommended

    print("    How should this be handled?")
    print(f"      [1] Register as ONE base doll '{name}' with these flag variants"
          + (" (recommended)." if recommended == "merge" else "."))
    print("      [2] Skip this doll for now (handle manually).")
    default = "1" if recommended == "merge" else "2"
    while True:
        resp = ask("    Choose [1/2]", default).strip().lower()
        if resp in ("1", "merge"):
            return "merge"
        if resp in ("2", "skip"):
            return "skip"
        print("      Enter 1 or 2.")


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
    names_without_base: set[str] = set()
    skipped_names: set[str] = set()
    for name in doll_names:
        group = [a for a in doll_assets if a.name == name]
        models = [a for a in group if a.kind == "doll-model"]
        texes = [a for a in group if a.kind == "doll-tex"]

        has_base_model = any(not a.flags for a in models) or repo_has_base_model(name)
        has_base_tex = any(not a.flags for a in texes) or repo_has_base_texture(name)

        if not has_base_model:
            names_without_base.add(name)
            handling = resolve_no_base(name, group, dry_run)
            if handling == "skip":
                skipped_names.add(name)
                plan.warnings.append(
                    f"Doll '{name}': skipped — no flagless base model; you chose to handle it manually.")
                continue
            # merge: register as one base doll whose variants each use their own model.
            plan.no_base_dolls[name] = sorted({t.tag for a in group for t in a.flags})
            plan.warnings.append(
                f"Doll '{name}': no flagless base model — registering as a base doll whose variants "
                f"({', '.join(plan.no_base_dolls[name])}) each use their own model. Requires the mod's "
                f"PokemonRegistry to accept variant-only dolls (a doll with no pokedoll_{name}.geo.json).")
        elif not has_base_tex:
            plan.warnings.append(
                f"Doll '{name}': no base texture (pokedoll_{name}_texture.png) in batch or repo.")

        # Folder-name vs file-name mismatch check.
        hints = {a.folder_hint for a in group if a.folder_hint}
        for h in hints:
            if h and h != name:
                plan.warnings.append(
                    f"Folder name suggests '{h}' but files parse as '{name}' — verify the pokemon name.")

        plan.doll_names.add(name)

    # Drop skipped dolls from the placement/rarity passes entirely.
    if skipped_names:
        doll_assets = [a for a in doll_assets if a.name not in skipped_names]

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
        # A flagless base variant exists for normal dolls, but NOT for variant-only
        # dolls (e.g. sinistea) — don't invent a base rarity entry the game never sees.
        if name not in names_without_base:
            combos.add(frozenset())
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
        # A figurine variant (e.g. amongsans1015_devoured) attaches to a flagless base
        # figurine, so validate the BASE model/texture specifically — a variant alone
        # won't register (mirrors FigurineRegistry, which requires a flagless base).
        has_base_model = any(a.kind == "fig-model" and a.name == fid and not a.flags for a in fig_assets) or \
            (GEO_DIR / f"{fid}_figurine.geo.json").exists()
        has_base_tex = any(a.kind == "fig-tex" and a.name == fid and not a.flags for a in fig_assets) or \
            (TEX_DIR / f"{fid}_figurine_texture.png").exists() or (TEX_DIR / f"{fid}_figurine.png").exists()
        variant_tags = sorted({t.tag for a in fig_assets if a.name == fid for t in a.flags})
        if variant_tags:
            plan.warnings.append(
                f"Figurine '{fid}': variant(s) {', '.join(variant_tags)} detected — these attach to the "
                f"base figurine and inherit its name with a flag prefix (e.g. 'Devoured {fid}').")
        if not has_base_model:
            plan.warnings.append(
                f"Figurine '{fid}': no base model {fid}_figurine.geo.json — a variant needs a base "
                f"figurine to attach to; it will NOT load.")
        if not has_base_tex:
            plan.warnings.append(f"Figurine '{fid}': missing base texture {fid}_figurine_texture.png — it will NOT load.")

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

    if plan.no_base_dolls:
        print("\nVariant-only dolls (no flagless base — registered as a base doll with flag variants):")
        for name, tags in sorted(plan.no_base_dolls.items()):
            print(f"  {name}: {', '.join(tags)}")

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
    print(f"Flag source: {len(FLAGS)} model ({FLAGS_SOURCE}), {len(FIG_FLAGS)} figurine ({FIG_FLAGS_SOURCE})")

    cases = [
        ("pokedoll_pichu_spiky_eared.png", "pichu", {"spiky", "eared"}),
        ("pokedoll_absol_shiny_texture.png", "absol", {"shiny"}),
        ("pokedoll_gholdengo_netherite.png", "gholdengo", {"netherite"}),
        ("pokedoll_calyrex_animated.geo.json", "calyrex", {"animated"}),
        ("pokedoll_combee_female.png", "combee", {"female"}),
        ("pokedoll_snorunt_family_animated.geo.json", "snorunt", {"family", "animated"}),
        ("pokedoll_eevee_texture.png", "eevee", set()),
        ("pokedoll_marshadow_zenith.geo.json", "marshadow", {"zenith"}),
        # Variant-only doll: the antique/phony suffixes must strip to the base 'sinistea'.
        ("pokedoll_sinistea_antique.geo.json", "sinistea", {"antique"}),
        ("pokedoll_sinistea_phony_shiny_texture.png", "sinistea", {"phony", "shiny"}),
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

    # Flag tables were sourced (from the mod when in a checkout, else the fallback).
    assert "antique" in FLAG_BY_TAG and "phony" in FLAG_BY_TAG, "antique/phony must be known model flags"
    assert "devoured" in FIG_FLAG_BY_TAG, "devoured must be a known figurine flag"

    # Classification smoke checks.
    fig = classify(Path("doncheadle_figurine.geo.json"))
    assert fig and fig.kind == "fig-model" and fig.name == "doncheadle" and not fig.flags, "figurine model classify"
    fig_v = classify(Path("amongsans1015_devoured_figurine.geo.json"))
    assert fig_v and fig_v.kind == "fig-model" and fig_v.name == "amongsans1015" \
        and {f.tag for f in fig_v.flags} == {"devoured"} \
        and fig_v.dest_name == "amongsans1015_devoured_figurine.geo.json", "figurine variant classify"
    tex = classify(Path("pokedoll_pikachu_shiny.png"))
    assert tex and tex.dest_name == "pokedoll_pikachu_shiny_texture.png", "texture normalization"
    print("  [ok ] classification smoke checks")
    print("\nSELF-TEST PASSED" if ok else "\nSELF-TEST FAILED")
    return 0 if ok else 1


# --------------------------------------------------------------------------- #
# Main
# --------------------------------------------------------------------------- #
def main() -> int:
    # The Windows console defaults to cp1252, which mangles the em-dashes/arrows in our
    # output. Best-effort switch to UTF-8; harmless if the stream doesn't support it.
    for stream in (sys.stdout, sys.stderr):
        try:
            stream.reconfigure(encoding="utf-8")  # type: ignore[attr-defined]
        except Exception:  # noqa: BLE001
            pass

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

    print(f"Flags: {len(FLAGS)} model ({FLAGS_SOURCE}), {len(FIG_FLAGS)} figurine ({FIG_FLAGS_SOURCE}).")
    if FLAGS_SOURCE.startswith("baked-in"):
        print(f"  ! Could not read {MODELFLAG_JAVA.name}; using the baked-in snapshot, which may be stale.")

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
