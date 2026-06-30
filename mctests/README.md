# Pokeblocks automated tests (mc-test)

Real, end-to-end automated tests for the **server side** of Pokeblocks, driven by
[mc-test](../../mc-test) — a WebDriver/Appium-style framework for Minecraft mods/plugins. Each test
boots a **real, modded Fabric 1.21.1 dedicated server** with the built Pokeblocks jar + its runtime
deps (GeckoLib, Fabric API) + the mc-test server-truth agent, then asserts against the running mod's
**authoritative runtime state** over the MC Test Protocol (MCTP) — no mocks.

These use the cost-1 **`server` driver** (server-truth-only, no player join), so they verify
server-side state without a rendered client. The matrix is in [`../mc-test.yml`](../mc-test.yml).

## Tests

| File | What it proves | How |
|---|---|---|
| [`pokeblocks.modloaded.mctest.yml`](pokeblocks.modloaded.mctest.yml) | The mod + its hard dep actually load on a real server. | Built-in `mod.loaded` query (zero SUT code): `pokeblocks`=true, `geckolib`=true, bogus=false (negative control). |
| [`pokeblocks.statetruth.mctest.yml`](pokeblocks.statetruth.mctest.yml) | The mod's real server-side state is correct. | The Pokeblocks `McTestStateProvider` SPI (see below): item registry membership, the asset-scanned Pokémon registry (`pokemon.count`≥50), and the rarity overrides loaded from `doll_rarity.json`. |
| [`pokeblocks.registry.mctest.yml`](pokeblocks.registry.mctest.yml) | All the mod's **content is registered** on a real server. | `block.exists`/`entity.exists`/`item.exists`/`pokemon.*`: fixed + decorative + pokeblock blocks, the `seat` entity (with a `seat_entity` negative control), misc items, and the Pokémon set (`count`≥91). Fills the previously-untested `block.exists`/`entity.exists` queries. |
| [`pokeblocks.rarity.mctest.yml`](pokeblocks.rarity.mctest.yml) | The **rarity engine** (the mod's central mechanic) is correct. | Full tier resolution, the effective-weight "score" (asserted ×1000, e.g. gigantic substitute = `625`), tier weights, acquisition divisors, ignored flags, and flag exclusion conflicts. Deterministic + registry-independent → exact equality. |
| [`pokeblocks.loot.mctest.yml`](pokeblocks.loot.mctest.yml) | **Loot routing** sends the right dolls to the right pools/tables. | Doll→group assignment, group→table matching (exact + glob), per-group drop chances, default-pool table targeting, and `excluded_dolls`/`excluded_flags`. The config/mapping layer, not the stochastic roll. |
| [`pokeblocks.recipe.mctest.yml`](pokeblocks.recipe.mctest.yml) | The **custom crafting layer** is wired and discriminates dolls. | Recipe serializers are registered, and `PokeblocksIngredient`'s doll matcher tells variants apart by exact species + flag set (an "Applin" ingredient rejects a "Shiny Applin"). |
| [`pokeblocks.config.mctest.yml`](pokeblocks.config.mctest.yml) | The **config layer** loaded its bundled defaults + override maps. | `config.toml` defaults (doll-popping, loot drop chance, update mode), `ConfigSync` file visibility, and the figurine name/tag overrides. |

Every test honestly **skips** (never falsely passes) on any target whose server-truth agent isn't available.

The state-truth files (everything except `modloaded`) all query the Pokeblocks `McTestStateProvider` SPI
(see below) and assert the mod's authoritative server-side state — no mocks, no client/render. Every
expected value is derived first-hand from the registry classes and the bundled config JSON, so a wrong
id or value is a real failure rather than a brittle guess; negative controls guard against vacuous greens.

## Run

Prereqs: Node 18+, JDK 21, network (first run downloads the Fabric launcher + deps via Modrinth,
then caches). The Pokeblocks Fabric jar must be built (`./gradlew :fabric:remapJar`).

```bash
# from the geckolib-examples repo root, pointing at the mc-test runner CLI:
node <path-to>/mc-test/packages/runner/dist/cli.js \
  run mctests/pokeblocks.modloaded.mctest.yml mctests/pokeblocks.statetruth.mctest.yml \
  --target pokeblocks-fabric-server-1.21.1 --matrix ./mc-test.yml --out ./mc-test-report
```

Reports land in `mc-test-report/` (`report.html` + `junit/results.xml`, the CI contract).

## CI — automated runs + a hosted report

These same tests run in **GitHub Actions** on every push to `master` (or a `multiloader/**` rewrite
branch), pull request, and manual dispatch,
via [`.github/workflows/mc-test.yml`](../.github/workflows/mc-test.yml). That file calls mc-test's
**reusable workflow** (`Shawn11564/mc-test/.github/workflows/mc-test-ci.yml@main` — pinned to `main`
because the workflows feature post-dates the `v1.0.0` tag). The reusable workflow builds the mc-test
engine + the `server-fabric` / `server-neoforge` truth agents, builds the Pokeblocks jars
(`./gradlew build`), runs both step files across the whole [`mc-test.yml`](../mc-test.yml) matrix
(`--target all`), and publishes the same `mc-test-report/report.html`:

- **always** as a downloadable workflow artifact named `mc-test-report` (zero repo setup), and
- to **GitHub Pages** at <https://shawn11564.github.io/Pokeblocks/> (best-effort).

The Fabric lane runs green; the NeoForge lane honest-skips `NO_SERVER_AGENT` (see below) — never a
false green. The workflow publishes the `compileOnly` SPI (`mc-test-agent-core:0.1.0`) to mavenLocal
*before* the Pokeblocks build, so it resolves with no extra config.

**One-time, to serve the report on Pages:** repo **Settings → Pages → Build and deployment → Source:
"GitHub Actions"**. Until then the run still passes and the report stays available as the artifact.
Regenerate this workflow any time with the runner's `init-ci` command:
`node <path-to>/mc-test/packages/runner/dist/cli.js init-ci --agents "server-fabric server-neoforge"`.

## The server-truth SPI

`pokeblocks.statetruth` queries are answered by
[`PokeblocksStateProvider`](../common/src/main/java/dev/mrshawn/pokeblocks/mctest/PokeblocksStateProvider.java),
Pokeblocks' implementation of mc-test's `io.mctest.agent.core.McTestStateProvider`. The co-selected
`server-fabric` agent discovers it via `java.util.ServiceLoader` (registration:
[`META-INF/services/io.mctest.agent.core.McTestStateProvider`](../common/src/main/resources/META-INF/services/io.mctest.agent.core.McTestStateProvider)).
The SPI is a **`compileOnly`** dependency (`io.mctest:mc-test-agent-core`, from mavenLocal) — the agent
provides the class at runtime; it must **never be bundled** (that would break ServiceLoader class
identity). The provider only **reads** state and returns primitives; it's dormant when no agent is present.

Query reference (add cases here as the mod grows). Numbers are plain ints; fractional values are
scaled `×1000` and rounded so they assert as exact integers (the agent's `equals` predicate compares
numbers numerically). Optional `flags` is a JSON array of flag tag names (e.g. `["shiny","posed"]`):

| query | args | returns |
|---|---|---|
| `item.exists` / `block.exists` / `entity.exists` / `recipe.serializerExists` | `{ id: "pokeblocks:…" }` | boolean — `BuiltInRegistries` membership |
| `pokemon.registered` | `{ name: "bulbasaur" }` | boolean — `PokemonRegistry` |
| `pokemon.count` | — | int — registered Pokémon |
| `rarity.override` | `{ pokemon, flags? }` | String — rarity display name from `doll_rarity.json`, or `"NONE"` |
| `rarity.resolved` | `{ pokemon, flags? }` | String — fully resolved tier (override → strip ignored → highest flag → COMMON) |
| `rarity.weightX1000` | `{ pokemon, flags? }` | int — effective loot weight ("score") ×1000 (tier weight, ÷4 per extra flag, ÷ acquisition divisor, gigantic ÷4 base) |
| `rarity.tierWeight` | `{ rarity: "common" }` | int — that tier's weight from `rarity_weights.json` |
| `rarity.acquisitionDivisor` | `{ pokemon, flags? }` | int — extra obtain-mechanic divisor (default 1) |
| `rarity.flagIgnored` | `{ pokemon, flag }` | boolean — is the flag ignored for that pokemon's rarity |
| `rarity.flagTier` | `{ flag: "shiny" }` | String — that flag's intrinsic rarity tier (`""` for NONE) |
| `flag.exclusionConflict` | `{ flags: [..] }` | boolean — do two flags share a mutual-exclusion group |
| `loot.groupForDoll` | `{ pokemon, flags? }` | String — loot group name, or `"NONE"` (default pool) |
| `loot.groupCount` | — | int — number of compiled loot groups |
| `loot.targetsTable` | `{ id }` | boolean — does the default global pool target this loot table |
| `loot.groupMatchesTable` | `{ group, id }` | boolean — does a named group inject into this loot table |
| `loot.groupDropChanceX1000` | `{ group }` | int — that group's resolved drop chance ×1000 |
| `loot.dollExcluded` | `{ pokemon, flags? }` | boolean — `[loot] excluded_dolls` membership |
| `loot.flagExcluded` | `{ flag }` | boolean — `[loot] excluded_flags` membership |
| `recipe.dollIngredientMatches` | `{ ingredientPokemon, ingredientFlags?, stackPokemon, stackFlags? }` | boolean — does the doll ingredient accept that doll stack (exact species + flags) |
| `config.dollPopping` | — | boolean — `eastereggs.doll_popping_enabled` |
| `config.lootDropChanceX1000` | — | int — `[loot] drop_chance` ×1000 |
| `config.updateMode` | — | String — `[config_sync] auto_update_configs` mode name |
| `config.fileShown` | `{ file }` | boolean — is that managed config file written to `config/Pokeblocks/` |
| `figurine.nameOverride` | `{ id }` | String — display-name override from `figurine_names.json`, or `"NONE"` |
| `figurine.hasTag` | `{ id, tag }` | boolean — does the figurine carry that tag (`figurine_tags.json`) |

## NeoForge lane (currently skips — follow-up)

The `pokeblocks-neoforge-server-1.21.1` target is wired but **honest-skips `NO_SERVER_AGENT`** because
mc-test's `agent-server-neoforge.jar` is not built in that repo (it's a standalone NeoGradle build,
excluded from CI). The Pokeblocks `common` logic these tests exercise is identical across loaders, so
the Fabric lane already covers it. To light up NeoForge, build the agent in the mc-test repo:

```bash
# in <mc-test>/agents/server-neoforge  (NeoGradle 7.x → Gradle 8.x; needs network + verifying the
# Names.java Mojmap spellings against MC 1.21.1 / NeoForge 21.1.x):
gradle build   # → build/libs/agent-server-neoforge.jar  (the runner resolves it by default path)
```

Then re-run with `--target pokeblocks-neoforge-server-1.21.1`.
