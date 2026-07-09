# Compendiums

Two craftable books let players browse and track the server's full collection:

- **Doll Compendium** — one entry per doll species. Crafted from a **book + any wool**.
- **Figurine Compendium** — one entry per figurine, including pack-added ones. Crafted from a **book + a nether quartz**.

Right-clicking opens a paged parchment index of every registered entry as a live 3D render. Entries the player has collected show in full colour with their name; everything else is a dark silhouette labelled `???`. The index has a search box (it only matches entries the player has already collected, so hidden names can't be fished out of it) and an **All / Found / Missing** filter chip. Pages turn via the corner arrows, mouse wheel, or arrow keys; the corner pad, `ESC`, or the inventory key (`E`) closes the book.

Clicking an entry opens a book-spread detail page:

- The large render can be **dragged to inspect** it from any angle; a couple of seconds after letting go it eases back into its idle spin.
- Doll pages list every **valid variant** of the species (shiny, gigantic, gendered forms, and so on — only combinations that actually exist as textures) as mini-slots. Each variant stays a silhouette until that exact variant has been collected; clicking one shows it in the large render, and the status line reflects the selected variant.
- Figurine pages get a **Hide Box** chip that removes the display case around the figure in the large render.
- The corner arrows / wheel / arrow keys flip through entries without leaving the book. `ESC` returns to the index; `E` closes the book entirely.

## How collection is tracked

A doll or figurine counts as **collected the first time the player has carried it** — picked up, crafted, traded, or pulled from a chest into the inventory. Discovery is recorded server-side (per player, per world, in `data/pokeblocks_compendium.dat` on the overworld) and never un-records: placing, trading away, or losing the item keeps the entry filled.

Dolls are tracked **per variant**: carrying a shiny records the shiny specifically, and the species counts as discovered once any of its variants has been carried. The index shows species-level completion; the detail page shows the per-variant breakdown.

Notes for admins:

- Progress is per **player UUID** per world/server. There is nothing to configure.
- Existing inventories are grandfathered automatically: anything a player is carrying gets recorded within a second of the server updating to this version. Dolls sitting in chests are recorded when next picked up.
- Progress sync to the client uses an optional network payload. Clients on an older Pokeblocks still join fine — their compendium falls back to showing what's in the inventory right now (the old behaviour).
- The entry list itself comes from the client's registered content, so pack-added dolls/figurines served through the [resource pack](Resource-Packs) appear automatically.

## Entry descriptions

Figurine detail pages show per-figurine blurbs from [`figurine_descriptions.json`](Configuration#figurine_descriptionsjson); figurines without an entry get a generic line. When the server serves override data through the resource pack, those descriptions are server-authoritative on clients, so all players read the same text.

Doll detail pages currently use a generic blurb for every species.
