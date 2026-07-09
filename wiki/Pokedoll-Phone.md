# Pokedoll Phone

The **Pokedoll Phone** is a craftable gadget that occasionally receives calls from pokedolls. Answer one, and the doll on the line asks you to dig up its buried favorite doll somewhere nearby — a small treasure-hunt minigame that ends with a rarity-rolled pokedoll reward.

## Getting a phone

Craft one on a crafting table:

```
copper  copper  copper
copper  POKEDOLL copper
copper  redstone copper
```

Any pokedoll works as the centre ingredient (it is consumed — it lives inside the phone now). Phones also appear in the **Pokeblocks – Misc** creative tab.

## Receiving a call

While the phone sits anywhere in your inventory it occasionally starts **ringing**: it buzzes audibly and its screen lights up in the inventory. A call rings for **30 seconds** (configurable) before it counts as missed.

- **Right-click the phone while it buzzes** to pick up. The call screen shows the doll on the line and its request: it buried its favorite doll near you but forgot where.
- Choose **Accept** to start the dig, or **Hang Up** (closing the screen also hangs up).
- Calls average one per **15 minutes** of carrying the phone (configurable) and never arrive while another call or dig is already running. Right-click the phone at any time for a status message.

## The dig

Accepting scatters **6 dig sites** (configurable) within ~32 blocks of you:

- Sites appear only in **open air above grass/dirt-type ground** (grass block, dirt, coarse dirt, podzol, mycelium, rooted dirt). **No existing block is ever replaced or destroyed.**
- Each site is marked with a small dirt mound and a column of golden sparkles (client-side particles), so you can spot them from a distance.
- **Right-click a mound repeatedly to dig.** The hole visibly deepens over four stages; the final scoop resolves the site.
- Each resolved site coughs up either **junk** (bones, string, flint, an old boot's worth of leather...) or the **buried doll** — a random pokedoll rolled with the mod's standard rarity weights (the same weighting as chest loot, honoring `doll_rarity.json`, weight config, and loot exclusions).
- You are **guaranteed to find the doll within 3 dug sites** (configurable): the winning dig number is rolled when the quest starts, so which mounds you choose never matters.
- The moment the doll surfaces, all remaining sites vanish.

Dig sites are unbreakable, piston-proof and explosion-proof — they only leave the world through digging, quest completion, or losing the block under them. Quests persist across relogs and restarts; only the quest's owner can dig their sites.

## Configuration

All knobs live under `[phone]` in `config/Pokeblocks/config.toml`:

| Key | Default | Meaning |
|-----|---------|---------|
| `enabled` | `true` | Master switch. When off, phones never start new calls (an active dig still finishes). |
| `average_call_interval_minutes` | `15` | Average minutes between calls while a phone is carried. |
| `ring_seconds` | `30` | How long a call rings before it is missed. |
| `dig_sites` | `6` | How many dig sites an accepted call scatters. |
| `site_radius` | `32` | Radius (blocks) around the player the sites spawn in. |
| `guaranteed_attempts` | `3` | The buried doll is guaranteed within this many fully-dug sites. |

## Notes for server admins

- The dig quest is per-player and stored in world data (`data/pokeblocks_phone_digs.dat` on the overworld), so it survives restarts.
- The phone's network traffic is **optional** on every loader: clients running an older Pokeblocks (without the phone) still join fine — their phones simply never ring.
- The buried-doll roll reuses the loot pipeline's exclusions: flags in `[loot] excluded_flags` and dolls in `[loot] excluded_dolls` never turn up buried.
