# Pokeblocks — Upcoming Update Wiki

Admin and pack-maker documentation for the Pokeblocks **Upcoming Update** (Minecraft 1.21.1, multiloader). These pages document the final, settled behavior of the update: how to configure the mod, how custom dolls and figurines are packaged and served to players, and the in-game commands that drive it all.

Everything is server-side — admins edit files under `config/Pokeblocks/` and run `/pokeblocks` commands; players only have to accept the served pack.

## Pages

- [Configuration](Configuration) — The `config.toml` settings, the managed JSON override files (rarity, names, descriptions, tags, weights, loot groups), and how config sync keeps them up to date.
- [Resource Packs](Resource-Packs) — How the merged custom pack is built, served (self-hosted or remote URL), and content-hashed so client downloads stay cached and stable across restarts.
- [Custom Content & Admin Override Packs](Custom-Content-Packs) — Authoring sub-packs to add your own dolls, figurines, and decorations, or override existing content, without touching the mod jar.
- [Commands](Commands) — Every `/pokeblocks` subcommand: giving items, inspecting dolls, hot-reloading config-backed data, and rebuilding or re-pushing the served pack.
- [Compendiums](Compendiums) — The craftable Doll/Figurine Compendium books: how per-player collection tracking works and where entry descriptions come from.
- [Pokedoll Phone](Pokedoll-Phone) — The ringing phone gadget and its buried-doll dig minigame: calls, dig sites, rewards, and the `[phone]` config knobs.
