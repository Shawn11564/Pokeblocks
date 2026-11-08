#!/usr/bin/env python3
"""In-game regression check for the doll-surgery minigame (crafting a trapped doll).

Launches a dev Fabric client straight into a throwaway copy of a test world, where the dormant
DollSurgeryHarness (common/.../client/trapped/DollSurgeryHarness.java) wakes up because this script
drops `pokeblocks-surgery-check.json` into the run directory. In survival, the harness clicks a
doll + TNT crafting result through the real container-click path and checks the surgery screen
opens instead of the doll being handed out; cancels with ESC (nothing used, back at the table);
plays a full operation by autopilot (trapped doll in the inventory, grid consumed, no wall touched);
rips one on purpose (doll + TNT gone, player hurt, table intact); and checks the Crafter can't trap
dolls. It screenshots the screen along the way, then quits. This script moves the screenshots to an
output folder and removes everything it added to the run dir.

    python tools/surgery_check.py
    python tools/surgery_check.py --out build/surgery-check --world "Pokeblocks testing"

Pass = every "[Surgery check]" line is PASS and the last one reads "RESULT PASS". Look at the images
too. Needs a machine that can open a Minecraft window (about two minutes).
"""

import argparse
import json
import shutil
import subprocess
import sys
import time
from pathlib import Path

# This file lives in <repo>/tools/.
REPO = Path(__file__).resolve().parents[1]
RUN_DIR = REPO / "fabric" / "run"
CHECK_WORLD = "Surgery Check"
CONFIG_NAME = "pokeblocks-surgery-check.json"


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--world", default="Pokeblocks testing", help="world in fabric/run/saves to copy")
    ap.add_argument("--out", default=str(REPO / "build" / "surgery-check"))
    ap.add_argument("--timeout", type=int, default=600, help="seconds before giving up on the client")
    ap.add_argument("--keep-world", action="store_true", help="leave the throwaway world for inspection")
    ap.add_argument("--offset", type=int, default=300, help="blocks to move (east) before starting, away from clutter")
    ap.add_argument("--size", default="1600x900", help="window size WxH")
    args = ap.parse_args()

    template = RUN_DIR / "saves" / args.world
    if not template.is_dir():
        sys.exit(f"no such world: {template}")
    world = RUN_DIR / "saves" / CHECK_WORLD
    if world.exists():
        shutil.rmtree(world)
    shutil.copytree(template, world, ignore=shutil.ignore_patterns("session.lock"))

    config_path = RUN_DIR / CONFIG_NAME
    config_path.write_text(json.dumps({"exit_when_done": True, "area_offset": args.offset}, indent=2), encoding="utf-8")

    shots_dir = RUN_DIR / "screenshots"
    before = set(p.name for p in shots_dir.glob("surgery_*.png")) if shots_dir.exists() else set()
    gradlew = REPO / ("gradlew.bat" if sys.platform.startswith("win") else "gradlew")
    width, height = args.size.lower().split("x")
    cmd = [str(gradlew), ":fabric:runClient", "--offline", "-q",
           f"--args=--quickPlaySingleplayer \"{CHECK_WORLD}\" --width {width} --height {height}"]
    print("launching:", " ".join(cmd))
    start = time.time()
    try:
        subprocess.run(cmd, cwd=REPO, timeout=args.timeout, check=False)
    except subprocess.TimeoutExpired:
        print("client timed out", file=sys.stderr)
    finally:
        config_path.unlink(missing_ok=True)
        if not args.keep_world:
            shutil.rmtree(world, ignore_errors=True)

    out = Path(args.out)
    out.mkdir(parents=True, exist_ok=True)
    taken = sorted(p for p in shots_dir.glob("surgery_*.png") if p.name not in before and p.stat().st_mtime >= start) \
        if shots_dir.exists() else []
    for p in taken:
        shutil.move(str(p), out / p.name)
    print(f"{len(taken)} screenshots -> {out}")

    result = None
    log = RUN_DIR / "logs" / "latest.log"
    if log.exists():
        for line in log.read_text(encoding="utf-8", errors="replace").splitlines():
            if "[Surgery check]" in line:
                message = line.split("[Surgery check] ", 1)[-1]
                print("  " + message)
                if message.startswith("RESULT"):
                    result = message
    if result is None:
        print("no RESULT line: the harness never finished", file=sys.stderr)
    sys.exit(0 if result is not None and result.startswith("RESULT PASS") else 1)


if __name__ == "__main__":
    main()
