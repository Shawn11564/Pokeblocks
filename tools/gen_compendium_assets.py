#!/usr/bin/env python3
"""Generates the compendium textures (item icons + GUI art) for Pokeblocks.

All art is original pixel work in Minecraft's visual language (1px dark outlines, short
shading ramps, top-left light) using the mod's parchment/wood palette — no vanilla assets
are copied. Deterministic: re-running always produces identical bytes.

Outputs (relative to common/src/main/resources/assets/pokeblocks/):
  textures/item/compendium_texture.png              16x16 doll compendium book
  textures/item/figurine_compendium_texture.png     16x16 figurine compendium book
  textures/gui/sprites/compendium/panel.png(+meta)  48x48 nine-slice wood/parchment panel
  textures/gui/sprites/compendium/slot.png(+meta)   24x24 nine-slice inset entry slot
  textures/gui/sprites/compendium/search_field.png(+meta) 16x14 nine-slice search inset
  textures/gui/sprites/compendium/page_{backward,forward}[_highlighted].png 18x11 arrows
  textures/gui/compendium/book_spread.png           256x180 open-book detail background
"""

import json
import random
from pathlib import Path

from PIL import Image

ASSETS = Path(__file__).resolve().parent.parent / "common/src/main/resources/assets/pokeblocks"

# ---------------------------------------------------------------------------- palette

PARCHMENT = (236, 222, 185, 255)
PARCHMENT_EDGE = (215, 196, 155, 255)
PARCHMENT_EDGE2 = (226, 209, 169, 255)
PARCHMENT_SPECK = (229, 214, 176, 255)

WOOD_OUTLINE = (58, 38, 20, 255)
WOOD_BASE = (140, 100, 54, 255)
WOOD_LIGHT = (168, 124, 70, 255)
WOOD_LIGHT2 = (154, 112, 60, 255)
WOOD_DARK = (106, 74, 40, 255)
WOOD_DARK2 = (118, 84, 46, 255)
WOOD_SEAM = (90, 62, 34, 255)

COVER = (74, 52, 28, 255)
COVER_OUTLINE = (44, 29, 15, 255)
COVER_LIP = (96, 68, 38, 255)


def img(w, h):
    return Image.new("RGBA", (w, h), (0, 0, 0, 0))


def rect(image, x0, y0, x1, y1, color):
    """Fills the inclusive pixel rectangle."""
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            image.putpixel((x, y), color)


def save(image, rel_path):
    path = ASSETS / rel_path
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)
    print(f"  wrote {rel_path}  ({image.width}x{image.height})")


def save_nine_slice_meta(rel_path, width, height, border):
    path = ASSETS / (rel_path + ".mcmeta")
    meta = {"gui": {"scaling": {"type": "nine_slice", "width": width, "height": height, "border": border}}}
    path.write_text(json.dumps(meta, indent=2) + "\n")
    print(f"  wrote {rel_path}.mcmeta")


# ---------------------------------------------------------------------------- item icons

def paint_map(rows, legend):
    image = img(16, 16)
    for y, row in enumerate(rows):
        assert len(row) == 16, f"row {y} is {len(row)} wide"
        for x, ch in enumerate(row):
            if ch != ".":
                image.putpixel((x, y), legend[ch])
    return image


# Both books share one silhouette (spine left, page block right, emblem centre, side clasp)
# so they read as a matching series; palette and emblem tell them apart.
BOOK_ROWS = [
    "................",
    "..OOOOOOOOOOO...",
    ".OSsBBBBBBBBPO..",
    ".OSsBLLLLLLLPO..",
    ".OSsBBBBBBBBPO..",
    ".OSsBBB111BBPO..",
    ".OSsBB11111BPO..",
    ".OSsBB16161BCcO.",
    ".OSsBB81M19BCcO.",
    ".OSsBBB112BBPO..",
    ".OSsBBBBBBBBPO..",
    ".OSsBLBBBBBBPO..",
    ".OSsBBBBBBBBPO..",
    ".OSsDDDDDDDpPO..",
    "..OOOOOOOOOOO...",
    "................",
]

DOLL_LEGEND = {
    "O": (43, 16, 20, 255),      # outline
    "S": (122, 38, 49, 255),     # spine dark
    "s": (150, 54, 64, 255),     # spine light
    "B": (168, 60, 72, 255),     # cover base
    "L": (198, 88, 98, 255),     # cover highlight
    "D": (134, 45, 57, 255),     # cover bottom shade
    "P": (236, 219, 178, 255),   # page block
    "p": (205, 182, 138, 255),   # page shadow
    "C": (219, 166, 58, 255),    # clasp gold
    "c": (243, 208, 100, 255),   # clasp gold light
    # emblem: embroidered plush face (eyes, mouth, cheek blush)
    "1": (246, 230, 197, 255),   # face cream
    "2": (219, 196, 157, 255),   # face shade (bottom-right)
    "6": (56, 40, 31, 255),      # eye
    "M": (56, 40, 31, 255),      # mouth
    "8": (222, 142, 124, 255),   # blush
    "9": (222, 142, 124, 255),   # blush
}

FIGURINE_LEGEND = {
    "O": (13, 36, 41, 255),      # outline
    "S": (30, 72, 78, 255),      # spine dark
    "s": (44, 94, 99, 255),      # spine light
    "B": (52, 110, 116, 255),    # cover base
    "L": (82, 146, 148, 255),    # cover highlight
    "D": (38, 86, 92, 255),      # cover bottom shade
    "P": (236, 219, 178, 255),   # page block
    "p": (205, 182, 138, 255),   # page shadow
    "C": (203, 214, 219, 255),   # clasp silver
    "c": (238, 245, 248, 255),   # clasp silver light
    # emblem cells all paint plain cover; the faceted gem is drawn over them afterwards
    "1": (52, 110, 116, 255),
    "2": (52, 110, 116, 255),
    "6": (52, 110, 116, 255),
    "M": (52, 110, 116, 255),
    "8": (52, 110, 116, 255),
    "9": (52, 110, 116, 255),
}

GOLD = (219, 166, 58, 255)
GOLD_LIGHT = (243, 208, 100, 255)
GOLD_DARK = (155, 109, 38, 255)


def gen_item_icons():
    doll = paint_map(BOOK_ROWS, DOLL_LEGEND)
    save(doll, "textures/item/compendium_texture.png")

    fig = paint_map(BOOK_ROWS, FIGURINE_LEGEND)
    # Faceted gem emblem, light from the top-left.
    gem = {
        (8, 5): GOLD_LIGHT,
        (7, 6): GOLD_LIGHT, (8, 6): GOLD, (9, 6): GOLD,
        (6, 7): GOLD_LIGHT, (7, 7): GOLD, (8, 7): GOLD_LIGHT, (9, 7): GOLD, (10, 7): GOLD_DARK,
        (7, 8): GOLD_DARK, (8, 8): GOLD, (9, 8): GOLD_DARK,
        (8, 9): GOLD_DARK,
    }
    for (x, y), color in gem.items():
        fig.putpixel((x, y), color)
    save(fig, "textures/item/figurine_compendium_texture.png")


# ---------------------------------------------------------------------------- gui sprites

def gen_panel():
    """48x48 nine-slice: dark outline, bevelled wood frame, seam, parchment field."""
    size = 48
    image = img(size, size)
    rect(image, 0, 0, size - 1, size - 1, WOOD_OUTLINE)
    rect(image, 1, 1, size - 2, size - 2, WOOD_BASE)
    # Bevel: light along top/left, dark along bottom/right (two steps each).
    rect(image, 1, 1, size - 2, 1, WOOD_LIGHT)
    rect(image, 2, 2, size - 3, 2, WOOD_LIGHT2)
    rect(image, 1, 1, 1, size - 2, WOOD_LIGHT)
    rect(image, 2, 2, 2, size - 3, WOOD_LIGHT2)
    rect(image, 1, size - 2, size - 2, size - 2, WOOD_DARK)
    rect(image, 2, size - 3, size - 3, size - 3, WOOD_DARK2)
    rect(image, size - 2, 1, size - 2, size - 2, WOOD_DARK)
    rect(image, size - 3, 2, size - 3, size - 3, WOOD_DARK2)
    # Seam ring, parchment edge ring, parchment field.
    rect(image, 6, 6, size - 7, size - 7, WOOD_SEAM)
    rect(image, 7, 7, size - 8, size - 8, (214, 193, 151, 255))
    rect(image, 8, 8, size - 9, size - 9, PARCHMENT)
    # Carved corner studs (corners never stretch, so they can carry detail).
    for cx, cy in ((2, 2), (size - 5, 2), (2, size - 5), (size - 5, size - 5)):
        rect(image, cx, cy, cx + 2, cy + 2, (150, 108, 58, 255))
        image.putpixel((cx, cy), (176, 132, 76, 255))
        image.putpixel((cx + 1, cy), (176, 132, 76, 255))
        image.putpixel((cx, cy + 1), (176, 132, 76, 255))
        image.putpixel((cx + 2, cy + 2), (112, 80, 44, 255))
    save(image, "textures/gui/sprites/compendium/panel.png")
    save_nine_slice_meta("textures/gui/sprites/compendium/panel.png", size, size, 8)


def gen_slot():
    """24x24 nine-slice: inset parchment card (shadow top/left, light bottom/right)."""
    size = 24
    image = img(size, size)
    rect(image, 0, 0, size - 1, size - 1, (154, 131, 90, 255))
    rect(image, 1, 1, size - 2, size - 2, (221, 201, 159, 255))
    rect(image, 1, 1, size - 2, 1, (203, 182, 140, 255))
    rect(image, 1, 1, 1, size - 2, (203, 182, 140, 255))
    rect(image, 2, 2, size - 3, 2, (211, 190, 148, 255))
    rect(image, 2, 2, 2, size - 3, (211, 190, 148, 255))
    rect(image, 1, size - 2, size - 2, size - 2, (242, 228, 192, 255))
    rect(image, size - 2, 1, size - 2, size - 2, (242, 228, 192, 255))
    save(image, "textures/gui/sprites/compendium/slot.png")
    save_nine_slice_meta("textures/gui/sprites/compendium/slot.png", size, size, 5)


def gen_search_field():
    """16x14 nine-slice: lighter parchment inset for the search box."""
    w, h = 16, 14
    image = img(w, h)
    rect(image, 0, 0, w - 1, h - 1, (122, 98, 60, 255))
    rect(image, 1, 1, w - 2, h - 2, (246, 233, 199, 255))
    rect(image, 1, 1, w - 2, 1, (216, 196, 152, 255))
    rect(image, 1, 1, 1, h - 2, (216, 196, 152, 255))
    rect(image, 1, h - 2, w - 2, h - 2, (252, 244, 216, 255))
    save(image, "textures/gui/sprites/compendium/search_field.png")
    save_nine_slice_meta("textures/gui/sprites/compendium/search_field.png", w, h, 4)


def gen_chip(highlighted):
    """16x14 nine-slice: raised parchment button (filter / toggle chips)."""
    w, h = 16, 14
    base = (232, 214, 172, 255) if not highlighted else (243, 228, 190, 255)
    light = (247, 233, 196, 255) if not highlighted else (252, 243, 212, 255)
    dark = (204, 182, 138, 255) if not highlighted else (216, 196, 154, 255)
    image = img(w, h)
    rect(image, 0, 0, w - 1, h - 1, (122, 98, 60, 255))
    rect(image, 1, 1, w - 2, h - 2, base)
    rect(image, 1, 1, w - 2, 1, light)
    rect(image, 1, 1, 1, h - 2, light)
    rect(image, 1, h - 2, w - 2, h - 2, dark)
    rect(image, w - 2, 2, w - 2, h - 2, dark)
    name = "chip_highlighted" if highlighted else "chip"
    save(image, f"textures/gui/sprites/compendium/{name}.png")
    save_nine_slice_meta(f"textures/gui/sprites/compendium/{name}.png", w, h, 4)


def gen_close(highlighted):
    """12x12 close button: little leather pad with a cream X, at home on wood or parchment."""
    s = 12
    pad = (114, 80, 44, 255) if not highlighted else (140, 100, 56, 255)
    pad_light = (140, 102, 58, 255) if not highlighted else (166, 124, 72, 255)
    pad_dark = (88, 60, 32, 255) if not highlighted else (108, 76, 42, 255)
    cross = (236, 219, 178, 255) if not highlighted else (252, 240, 205, 255)
    image = img(s, s)
    rect(image, 1, 0, s - 2, s - 1, pad)
    rect(image, 0, 1, s - 1, s - 2, pad)
    rect(image, 1, 1, s - 2, 1, pad_light)
    rect(image, 1, 1, 1, s - 2, pad_light)
    rect(image, 1, s - 2, s - 2, s - 2, pad_dark)
    rect(image, s - 2, 2, s - 2, s - 2, pad_dark)
    for i in range(3, s - 3):
        image.putpixel((i, i), cross)
        image.putpixel((s - 1 - i, i), cross)
    # Thicken the strokes so the X reads at 1x.
    for i in range(3, s - 4):
        image.putpixel((i + 1, i), cross)
        image.putpixel((s - 2 - i, i), cross)
    name = "close_highlighted" if highlighted else "close"
    save(image, f"textures/gui/sprites/compendium/{name}.png")


def arrow_mask(forward):
    """Pixel set of an 18x11 page-turn arrow (shaft + triangular head)."""
    mask = set()
    for x in range(2, 11):          # shaft
        for y in range(4, 7):
            mask.add((x, y))
    for x in range(10, 16):        # head
        half = min(4, 15 - x)
        for y in range(5 - half, 5 + half + 1):
            mask.add((x, y))
    if not forward:
        mask = {(17 - x, y) for (x, y) in mask}
    return mask


def gen_arrow(forward, highlighted):
    base = (232, 186, 84, 255) if highlighted else (206, 158, 62, 255)
    light = (252, 224, 140, 255) if highlighted else (238, 198, 102, 255)
    dark = (176, 128, 52, 255) if highlighted else (150, 106, 38, 255)
    outline = (82, 56, 26, 255) if highlighted else (66, 44, 22, 255)

    mask = arrow_mask(forward)
    image = img(18, 11)
    for (x, y) in mask:
        if (x, y - 1) not in mask:
            color = light
        elif (x, y + 1) not in mask:
            color = dark
        else:
            color = base
        image.putpixel((x, y), color)
    for (x, y) in mask:
        for nx, ny in ((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)):
            if 0 <= nx < 18 and 0 <= ny < 11 and (nx, ny) not in mask:
                if image.getpixel((nx, ny))[3] == 0:
                    image.putpixel((nx, ny), outline)

    name = "page_forward" if forward else "page_backward"
    if highlighted:
        name += "_highlighted"
    save(image, f"textures/gui/sprites/compendium/{name}.png")


# ---------------------------------------------------------------------------- book spread

def gen_book_spread():
    w, h = 256, 180
    image = img(w, h)
    rng = random.Random(2024)

    # Cover: rounded dark-leather slab with a lighter inner lip.
    corner = {(0, 0), (1, 0), (0, 1), (w - 1, 0), (w - 2, 0), (w - 1, 1),
              (0, h - 1), (0, h - 2), (1, h - 1), (w - 1, h - 1), (w - 2, h - 1), (w - 1, h - 2)}
    for y in range(h):
        for x in range(w):
            if (x, y) in corner:
                continue
            on_edge = x in (0, w - 1) or y in (0, h - 1) or \
                (x in (1, w - 2) and y in (1, h - 2))
            image.putpixel((x, y), COVER_OUTLINE if on_edge else COVER)
    for x in range(3, w - 3):
        image.putpixel((x, 2), COVER_LIP)
        image.putpixel((x, h - 3), COVER_LIP)
    for y in range(3, h - 3):
        image.putpixel((2, y), COVER_LIP)
        image.putpixel((w - 3, y), COVER_LIP)

    # Stacked page edges peeking out along the bottom of the cover.
    rect(image, 10, 170, 245, 170, (218, 199, 158, 255))
    rect(image, 12, 172, 243, 172, (206, 186, 146, 255))
    rect(image, 14, 174, 241, 174, (192, 171, 131, 255))

    # The two pages.
    def page(x0, x1):
        rect(image, x0, 6, x1, 169, PARCHMENT)
        # Vignetted edges: darker outer ring, softer second ring.
        rect(image, x0, 6, x1, 6, PARCHMENT_EDGE)
        rect(image, x0, 169, x1, 169, PARCHMENT_EDGE)
        rect(image, x0, 6, x0, 169, PARCHMENT_EDGE)
        rect(image, x1, 6, x1, 169, PARCHMENT_EDGE)
        rect(image, x0 + 1, 7, x1 - 1, 7, PARCHMENT_EDGE2)
        rect(image, x0 + 1, 168, x1 - 1, 168, PARCHMENT_EDGE2)
        rect(image, x0 + 1, 7, x0 + 1, 168, PARCHMENT_EDGE2)
        rect(image, x1 - 1, 7, x1 - 1, 168, PARCHMENT_EDGE2)
        # Faint fibre specks.
        for _ in range(90):
            sx = rng.randint(x0 + 3, x1 - 3)
            sy = rng.randint(9, 166)
            image.putpixel((sx, sy), PARCHMENT_SPECK)

    page(8, 123)
    page(132, 247)

    # Spine gutter: pages curving down into the binding.
    gutter = [(204, 184, 143, 255), (184, 162, 122, 255), (163, 140, 102, 255), (140, 118, 84, 255)]
    for i, color in enumerate(gutter):
        rect(image, 124 + i, 6, 124 + i, 169, color)
        rect(image, 131 - i, 6, 131 - i, 169, color)

    # Page-corner curl hints (bottom outer corners).
    for i in range(3):
        image.putpixel((11 + i, 164 + i), (210, 190, 150, 255))
        image.putpixel((244 - i, 164 + i), (210, 190, 150, 255))

    # Left page: display pedestal shadow the rendered entry stands on.
    cx, cy, rx, ry = 66, 134, 34, 8
    for y in range(cy - ry, cy + ry + 1):
        for x in range(cx - rx, cx + rx + 1):
            dx = (x - cx) / rx
            dy = (y - cy) / ry
            d = dx * dx + dy * dy
            if d <= 1.0:
                image.putpixel((x, y), (208, 189, 148, 255) if d >= 0.78 else (222, 205, 164, 255))

    # Right page: title divider with a small diamond, plus a faint rule above the status line.
    ink = (176, 148, 100, 255)
    rect(image, 146, 38, 234, 38, ink)
    for x, y in ((190, 37), (189, 38), (190, 38), (191, 38), (190, 39)):
        image.putpixel((x, y), (150, 120, 76, 255))
    image.putpixel((144, 38), ink)
    image.putpixel((236, 38), ink)
    rect(image, 156, 137, 224, 137, (206, 186, 146, 255))

    # Ribbon bookmark draped over the right page.
    for y in range(3, 25):
        for x in range(222, 229):
            if x == 222:
                color = (178, 84, 92, 255)
            elif x == 228:
                color = (124, 42, 53, 255)
            else:
                color = (162, 59, 70, 255)
            image.putpixel((x, y), color)
    for x in (222, 223, 227, 228):
        image.putpixel((x, 25), (162, 59, 70, 255) if x in (223, 227) else (178, 84, 92, 255))
    image.putpixel((222, 26), (178, 84, 92, 255))
    image.putpixel((228, 26), (124, 42, 53, 255))

    save(image, "textures/gui/compendium/book_spread.png")


if __name__ == "__main__":
    print("Generating compendium assets:")
    gen_item_icons()
    gen_panel()
    gen_slot()
    gen_search_field()
    for hl in (True, False):
        gen_chip(hl)
        gen_close(hl)
    for fwd in (True, False):
        for hl in (True, False):
            gen_arrow(fwd, hl)
    gen_book_spread()
    print("Done.")
