# Pokedoll Phone

The **Pokedoll Phone** is a craftable gadget that receives calls from **one specific pokedoll** — the doll you crafted it with. Answer a call, and that doll asks you to dig up a doll it lost somewhere nearby — a small treasure-hunt minigame that ends with a rarity-rolled pokedoll reward. Each phone wears out after a handful of answered calls.

## Getting a phone

Craft one on a crafting table:

```
copper  copper  copper
copper  POKEDOLL copper
copper  redstone copper
```

**The phone attunes to whichever pokedoll you place in the centre** (that exact species and its flags): from then on, that is the **only** doll that ever calls it. The doll is consumed — it lives inside the phone now. The phone's tooltip shows which doll it is **Attuned to**.

Phones also appear in the **Pokeblocks – Misc** creative tab, but a creative/`/give` phone has **no attunement and never rings on its own** — its tooltip reads *"Not attuned — craft with a Pokedoll to attune it."* Craft it (or use `/pokeblocks phonering` to force a test call). Phones from before the attunement update are likewise unattuned; re-craft them to get calls.

## Receiving a call

While the phone sits anywhere in your inventory it occasionally starts **ringing**: it buzzes audibly and its screen lights up in the inventory. A call rings for **30 seconds** (configurable) before it counts as missed. A chat line names who is calling and what they lost.

- **Right-click the phone while it buzzes** to pick up. The call screen shows the doll on the line and its request: it lost a doll near you but forgot where.
- The request names the **rarity of the lost doll**, coloured with that rarity's colour. It is picked relative to the caller's own rarity:
  - a doll of the **same rarity** as the caller (most common),
  - a doll **one rarity below** the caller,
  - a doll within a **rarity-percent window** around the caller — shown as a compact `min% – max%` pair. The window scales with the caller's rarity, so rarer callers pull in only comparably-rare dolls,
  - rarely, a doll **one rarity above** the caller.
- The doll you eventually dig up always matches the announced rarity/window — you just don't know exactly which doll until you unearth it.
- Choose **Accept** to start the dig, or **Hang Up** (closing the screen also hangs up).
- Calls never arrive while another call or dig is already running. Right-click the phone at any time for a status message.

## Durability

Every phone has **durability** (default **8**, configurable). **Each accepted call that starts a dig spends one point**, and the phone **breaks** when it runs out. A call you hang up on — or one that fizzles because there is no digging ground nearby — costs nothing. The remaining charge shows as a normal durability bar.

## The dig

Accepting scatters **6 dig sites** (configurable) within ~32 blocks of you — **plus one extra site per rarity tier of the phone's attuned doll** (Uncommon +1, Rare +2, … Gigantic +6, all configurable), so a rarer phone runs a bigger hunt:

- Sites appear only in **open air above grass/dirt-type ground** (grass block, dirt, coarse dirt, podzol, mycelium, rooted dirt). **No existing block is ever replaced or destroyed.**
- Each site is marked with a small dirt mound and a column of golden sparkles (client-side particles), so you can spot them from a distance.
- **Right-click a mound repeatedly to dig.** The hole visibly deepens over four stages; the final scoop resolves the site.
- Each resolved site coughs up either **junk** (bones, string, flint, an old boot's worth of leather...) or the **buried doll** — a rarity-weighted pokedoll drawn from the variants that **match the announced rarity/percent window** (using the same weighting and loot exclusions as chest loot, honoring `doll_rarity.json` and the weight config). If nothing matches the descriptor it falls back to the full pool, so a dig is never left without a reward.
- You are **guaranteed to find the doll within 3 dug sites** (configurable) — **plus one per rarity tier of the phone's attuned doll** (same tier scale as the sites), so a Rare phone guarantees it within 3 + 2 = 5 digs. The winning dig number is rolled when the quest starts, so which mounds you choose never matters.
- The moment the doll surfaces, all remaining sites vanish.

Dig sites are unbreakable, piston-proof and explosion-proof — they only leave the world through digging, quest completion, or losing the block under them. Quests persist across relogs and restarts; only the quest's owner can dig their sites.

## Configuration

All knobs live under `[phone]` in `config/Pokeblocks/config.toml`:

| Key | Default | Meaning |
|-----|---------|---------|
| `enabled` | `true` | Master switch. When off, phones never start new calls (an active dig still finishes). |
| `average_call_interval_minutes` | `15` | Average minutes between calls while a phone is carried. |
| `ring_seconds` | `30` | How long a call rings before it is missed. |
| `dig_sites` | `6` | Base number of dig sites an accepted call scatters. |
| `site_radius` | `32` | Radius (blocks) around the player the sites spawn in. |
| `guaranteed_attempts` | `3` | Base number of fully-dug sites the buried doll is guaranteed within. |
| `durability` | `8` | How many calls a phone can answer before it breaks. Applied as phones are crafted. |
| `dig_sites_per_rarity` | `1` | Extra dig sites added per rarity tier of the phone's attuned doll. `0` disables the scaling. |
| `guaranteed_attempts_per_rarity` | `1` | Extra guaranteed attempts added per rarity tier of the phone's attuned doll. `0` disables the scaling. |

## Notes for server admins

- The dig quest is per-player and stored in world data (`data/pokeblocks_phone_digs.dat` on the overworld), so it survives restarts.
- The phone's network traffic is **optional** on every loader: clients running an older Pokeblocks (without the phone) still join fine — their phones simply never ring.
- The buried-doll roll reuses the loot pipeline's exclusions: flags in `[loot] excluded_flags` and dolls in `[loot] excluded_dolls` never turn up buried.
