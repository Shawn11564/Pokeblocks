# Pokeblocks

A 1.21.1 Minecraft mod that introduces animated 3D Pokemon collectibles. 

## Features

* **3D Animated Models**: Over 67 models.
* **Variants**: Includes Shiny, posed, animated, and gigantic versions.
* **Multiloader Support**: Compatible with NeoForge, Fabric (WIP), and Forge (WIP).

## Configuration

Rarities and weights are managed via `config/Pokeblocks`.

### Commands

* `/pokeblocks reload_rarity`: Reloads Pokemon rarity assignments.
* `/pokeblocks reload_weights`: Reloads rarity weight configurations.

## Included Pokemon

* **Common**: Bulbasaur, Charmander, Squirtle, Smoliv, Rellor, Swinub, Happiny.
* **Uncommon**: Lickitung, Mareep, Dolliv, Arboliva, Wooper, Gastly, Shellder.
* **Rare**: Flaaffy, Snorlax, Sentret, Furret, Munchlax, Wartortle, Eevee.
* **Epic**: Calyrex, Ampharos, Sableye, Absol, Ivysaur, Riolu, Froslass.
* **Legendary**: Venusaur, Blastoise, Gengar, Corviknight, Kyogre.

## Documentation

* **[Adding Models Guide](ADDING_MODELS.md)**: Instructions for importing new 3D assets.

## Extensions and Ports

For users seeking additional features, a NeoForge 1.21.1 port is available which adds:
* Native audio cues for models.
* Dungeon loot table integration.
* Configurable loot weights and hot-reloading.

## Mod Compatibility

All integrations are optional: the mod runs unchanged when the other mod is absent.

* **JEI (Just Enough Items)** — Forge, NeoForge and Fabric. Every valid doll, figurine and decoration
  variant is listed as its own entry, and every Pokeblocks crafting recipe is viewable, including the
  dynamic ones (throwable / trapped / gigantic dolls, the Pokedoll Phone, laser pointer dyeing), which
  cycle through every doll with its exact result.
* **Curios** (NeoForge) / **Trinkets** (Fabric) — dolls can also be worn in an accessory head slot.

## Building

`./gradlew build` produces `pokeblocks-<loader>-<version>-<minecraft_version>.jar` in each loader's
`build/libs`. The build pulls Parchment mappings and Forge's Parchment plugin from
`maven.parchmentmc.org`; when that host is down, a machine that has built before can still build with
`./gradlew --offline build` (everything needed is in Gradle's cache). CI has no such cache and will
fail until the host is back.

## Discord build notifications

The `discord-build` GitHub Action (`.github/workflows/discord-build.yml`) builds the Fabric, Forge and
NeoForge jars on every push to `master` or a `multiloader/**` branch and posts them to a Discord
channel together with release notes from [`CHANGELOG.md`](CHANGELOG.md).

One-time setup:

1. In Discord, open the target channel's settings → **Integrations** → **Webhooks** → **New Webhook**
   and copy the webhook URL. The webhook's channel is where builds are posted.
2. In GitHub, go to the repository's **Settings** → **Secrets and variables** → **Actions** and add a
   repository secret named `DISCORD_WEBHOOK_URL` containing that URL.

Before each push, write the notes for the build under a new `## ` heading at the **top** of
`CHANGELOG.md`; only the topmost section is posted. The jars are also attached to the workflow run
as an artifact for 30 days.

## Credits and License

* **Original Author**: Shawn11564 (MrShawn)
* **Contributors**: KaptainWutax, Yurgsy
* **License**: CC BY-NC-SA 4.0