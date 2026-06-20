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
| [`pokeblocks.statetruth.mctest.yml`](pokeblocks.statetruth.mctest.yml) | The mod's real server-side state is correct. | The Pokeblocks `McTestStateProvider` SPI (see below): item registry membership, the asset-scanned Pokémon registry (`pokemon.count`=91), and the rarity overrides loaded from `doll_rarity.json`. |

Both honestly **skip** (never falsely pass) on any target whose server-truth agent isn't available.

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

Query reference (add cases here as the mod grows):

| query | args | returns |
|---|---|---|
| `item.exists` / `block.exists` / `entity.exists` | `{ id: "pokeblocks:…" }` | boolean — `BuiltInRegistries` membership |
| `pokemon.registered` | `{ name: "bulbasaur" }` | boolean — `PokemonRegistry` |
| `pokemon.count` | — | int — registered Pokémon |
| `rarity.override` | `{ pokemon, flags?: [..] }` | String — rarity display name from `doll_rarity.json`, or `"NONE"` |
| `rarity.flagTier` | `{ flag: "shiny" }` | String — that flag's intrinsic rarity tier |

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
