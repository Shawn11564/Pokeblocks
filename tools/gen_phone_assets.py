#!/usr/bin/env python3
"""Generates the Pokedoll Phone assets: the off/on item textures, the call-screen
portrait ring, and (via ffmpeg) the phone_buzz.ogg vibration sound.

Run from the repo root (or anywhere - paths are resolved relative to this file):
    python tools/gen_phone_assets.py

Idempotent: rewrites the same files every run. Requires Pillow; the sound step is
skipped with a warning when ffmpeg is not on PATH.
"""

import shutil
import subprocess
import sys
from pathlib import Path

from PIL import Image, ImageDraw

REPO = Path(__file__).resolve().parent.parent
ASSETS = REPO / "common" / "src" / "main" / "resources" / "assets" / "pokeblocks"
ITEM_TEX = ASSETS / "textures" / "item"
GUI_TEX = ASSETS / "textures" / "gui" / "phone"
SOUNDS = ASSETS / "sounds"


# ---------------------------------------------------------------------------
# 16x16 item textures - a pokeball-coloured candybar phone with an antenna.
# One character per pixel; the palette maps characters to RGBA.
# ---------------------------------------------------------------------------

PHONE_ROWS = [
    "..........A.....",
    "..........A.....",
    "....OOOOOOOO....",
    "...ORRRRRRRRO...",
    "...ORRRRRRRrO...",
    "...OBSSSSSSBO...",
    "...OBSSSSSSBO...",
    "...OBSSSSSsBO...",
    "...OBBBGGBBBO...",
    "...OWWWGGWWWO...",
    "...OWGWWGWWWO...",
    "...OWWWWWWGWO...",
    "...OWGWWGWWWO...",
    "...OwwwwwwwwO...",
    "....OOOOOOOO....",
    "................",
]

PALETTE_OFF = {
    ".": (0, 0, 0, 0),
    "O": (43, 36, 48, 255),     # outline
    "A": (86, 86, 96, 255),     # antenna
    "R": (217, 72, 72, 255),    # pokeball red
    "r": (168, 50, 50, 255),    # red shade
    "B": (30, 26, 34, 255),     # black band
    "S": (58, 70, 82, 255),     # screen (off)
    "s": (44, 54, 64, 255),     # screen shade (off)
    "G": (138, 138, 148, 255),  # buttons / centre button
    "W": (244, 240, 236, 255),  # pokeball white
    "w": (207, 201, 196, 255),  # white shade
}

# The "on" phone: lit cyan screen with a tiny yellow bell, glowing centre button,
# and ring sparks flying off the antenna and body.
PALETTE_ON = dict(
    PALETTE_OFF,
    S=(127, 227, 255, 255),     # screen lit
    s=(63, 184, 224, 255),      # screen lit shade
    G=(255, 217, 77, 255),      # buttons glow yellow
)

# (x, y) -> palette char overrides applied only to the ON texture.
ON_OVERRIDES = {
    (7, 5): "Y", (8, 6): "Y", (6, 6): "Y", (7, 6): "Y", (7, 7): "Y",  # bell on screen
    (10, 0): "Y",                                                     # antenna spark
    (2, 4): "Y", (13, 4): "Y",                                        # ring marks
    (1, 6): "Y", (14, 6): "Y",
}
ON_EXTRA = {"Y": (255, 217, 77, 255)}


def paint_phone(palette, overrides):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    colors = dict(palette, **ON_EXTRA)
    for y, row in enumerate(PHONE_ROWS):
        assert len(row) == 16, f"row {y} is {len(row)} chars"
        for x, ch in enumerate(row):
            ch = overrides.get((x, y), ch)
            px[x, y] = colors[ch]
    return img


# ---------------------------------------------------------------------------
# 64x64 portrait ring - a pokeball-styled circular frame with an open centre,
# drawn at 4x and downsampled so the ring stays smooth.
# ---------------------------------------------------------------------------

def paint_portrait_ring():
    scale = 4
    size = 64 * scale
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx = cy = size // 2

    outline = (43, 36, 48, 255)
    red = (217, 72, 72, 255)
    white = (244, 240, 236, 255)

    r_out, r_in = 30 * scale, 24 * scale

    def circle(r, fill):
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=fill)

    # Outer outline disc, then the two ring halves, then the inner outline,
    # then punch out the centre so the doll renders through it.
    circle(r_out + scale, outline)
    draw.pieslice([cx - r_out, cy - r_out, cx + r_out, cy + r_out], 180, 360, fill=red)
    draw.pieslice([cx - r_out, cy - r_out, cx + r_out, cy + r_out], 0, 180, fill=white)
    # Band stubs where a pokeball's black seam crosses the ring.
    band_h = 2 * scale
    draw.rectangle([cx - r_out - scale, cy - band_h, cx - r_in + scale, cy + band_h], fill=outline)
    draw.rectangle([cx + r_in - scale, cy - band_h, cx + r_out + scale, cy + band_h], fill=outline)
    circle(r_in + scale, outline)
    # Transparent centre.
    hole = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    mask = Image.new("L", (size, size), 255)
    ImageDraw.Draw(mask).ellipse([cx - r_in, cy - r_in, cx + r_in, cy + r_in], fill=0)
    img = Image.composite(img, hole, mask)

    # Pokeball centre button sitting on the bottom of the ring.
    draw = ImageDraw.Draw(img)
    br = 6 * scale
    bx, by = cx, cy + r_out - 3 * scale
    draw.ellipse([bx - br, by - br, bx + br, by + br], fill=outline)
    draw.ellipse([bx - br + 2 * scale, by - br + 2 * scale, bx + br - 2 * scale, by + br - 2 * scale], fill=white)

    return img.resize((64, 64), Image.LANCZOS)


# ---------------------------------------------------------------------------
# phone_buzz.ogg - a two-tone vibration buzz synthesized by ffmpeg (mono, ~0.9s).
# ---------------------------------------------------------------------------

def gen_buzz(dest: Path):
    if shutil.which("ffmpeg") is None:
        print("WARNING: ffmpeg not found - skipping phone_buzz.ogg", file=sys.stderr)
        return False
    cmd = [
        "ffmpeg", "-y", "-loglevel", "error",
        "-f", "lavfi",
        "-i", "aevalsrc=0.45*sin(2*PI*165*t)+0.28*sin(2*PI*330*t)+0.10*sin(2*PI*660*t):s=44100:d=0.9",
        "-af", "tremolo=f=26:d=0.95,afade=t=in:st=0:d=0.03,afade=t=out:st=0.72:d=0.18,volume=0.7",
        "-ac", "1", "-c:a", "libvorbis", "-qscale:a", "4",
        str(dest),
    ]
    subprocess.run(cmd, check=True)
    return True


def main():
    ITEM_TEX.mkdir(parents=True, exist_ok=True)
    GUI_TEX.mkdir(parents=True, exist_ok=True)
    SOUNDS.mkdir(parents=True, exist_ok=True)

    paint_phone(PALETTE_OFF, {}).save(ITEM_TEX / "pokedoll_phone.png")
    paint_phone(PALETTE_ON, ON_OVERRIDES).save(ITEM_TEX / "pokedoll_phone_on.png")
    print(f"wrote {ITEM_TEX / 'pokedoll_phone.png'}")
    print(f"wrote {ITEM_TEX / 'pokedoll_phone_on.png'}")

    paint_portrait_ring().save(GUI_TEX / "portrait_ring.png")
    print(f"wrote {GUI_TEX / 'portrait_ring.png'}")

    if gen_buzz(SOUNDS / "phone_buzz.ogg"):
        print(f"wrote {SOUNDS / 'phone_buzz.ogg'}")


if __name__ == "__main__":
    main()
