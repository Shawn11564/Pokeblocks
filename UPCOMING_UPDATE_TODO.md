# Pokeblocks — Upcoming Update To-Do (Shawn)

> Source: Trello board **Pokeblocks** → list **"Upcoming Update To-Do"**, filtered to the **Shawn** label.
> Generated 2026-06-18 · **Updated 2026-06-19**. 14 cards (the 4 "Matt"-only cards — Dragonite/Golurk/Giratina variants and decoration hitboxes — are excluded; "Review new models" carries both labels so it's kept).
>
> **Status: Phases 1–3 are complete** — cards #34, #48, #68, #56, #49, #50 are implemented on `multiloader/initial`, compiling on all four loaders, with config changes unit-tested and #56 verified in-game. **Phases 4–7 (8 cards) remain.** Details in "Completed this session" below.

## Legend — who does it

| Tag | Meaning |
|-----|---------|
| ✅ **Claude** | Code/text task I can complete end-to-end in this repo. You review + build. |
| 🟡 **Collaborative** | I do the code/draft portion; a human must verify visually, run the game, or publish externally. |
| 🔴 **Human** | Needs a person — visual modeling, in-game judgment, or a release decision. I can assist but can't own it. |

**Split:** 7 ✅ Claude · 5 🟡 Collaborative · 2 🔴 Human  ·  **Progress: 6 / 14 done (Phases 1–3).**

Ordering below is by **dependency + priority**: foundational code first, then the config and resource-pack feature stacks, then content/model polish (a parallel track), then quality passes, docs, and finally the release merge which gates on everything.

---

## ✅ Completed this session (Phases 1–3)

All six Phase 1–3 cards are implemented on `multiloader/initial` — compiling on common/fabric/forge/neoforge, config changes unit-tested, and #56 confirmed working in-game (client reuses its cached pack on reconnect).

- **#34** — logging moved to parameterized SLF4J (`PokeblocksLog.LOGGER`); exceptions log with stack traces; noisy dumps demoted to `debug`.
- **#48** — top-level `auto_update_configs` = `merge | overwrite | off` (extends the existing `ConfigSync`; legacy `auto_update` boolean still parsed + migrated).
- **#68** — per-file visibility manifest (`show_files`/`hide_files`); hidden files still load their defaults from the jar (no balance drift).
- **#56** — pack UUID derived from content SHA-1 (stable across restarts) + HTTP ETag/304 + input-fingerprint rebuild skip. **Verified in-game** via the run logs.
- **#49** — admin packs can override the 6 per-id JSON settings (rarity, ignored flags, divisors, weights, figurine names/tags), merged server-side.
- **#50** — client reload-listener so pack-added content refreshes live, plus an **additive** generic data-driven decoration block/item. The 5 built-in decorations were left untouched (a full conversion was deferred — it needs data fixers).

**Follow-on enhancements delivered (beyond the cards):**
- Typed resource-pack layout `assets/<type>/<kind>/<id>` (dolls/figurines/decorations); the old flat layout still works as a fallback.
- `/pokeblocks resourcepack saveexample` writes a complete example pack (+README); the rebuild command moved to `/pokeblocks resourcepack rebuild`.
- Pack precedence fixed so `custom/` correctly overrides (last-wins), with conflict logging that names the file + the packs involved.
- `customtestpack` generated under the neoforge run dir for manual testing.

> ⚠️ **Before merge:** #50's decoration rendering/placement/creative-tab still needs an in-game pass (can't be verified by compile). Everything else above is compile- and (where noted) runtime-verified.

---

## Phase 1 — Foundational code cleanup (no dependencies — do first)

### 1. ✅ DONE — Replace all `System.out.println` calls with Logger — [card #34](https://trello.com/c/MdzdHEyP/34-replace-all-systemoutprintln-calls-with-logger)
Also review log sites and demote/remove excessive logs (move to `debug`).
- **Scope (verified):** ~18 occurrences across 6 main-source files — `config/PokeblocksConfig`, `resourcepack/CustomPackBuilder`, `resourcepack/ResourcePackServer`, `resourcepack/resources/PokedollsInMemoryPack`, `mixin/PlayerJoinMixin`, `PokeblocksServerLifecycle` (+~20 in `LootRaritySimulationTest`, which can stay). Decompiled MC under `neoforge/build/` is out of scope.
- **Why first:** Pure mechanical refactor, zero design risk, and it touches the same config/resourcepack files the feature work below will edit — cleaner to do before they change.
- **Deps:** none.

---

## Phase 2 — Config system

### 2. ✅ DONE — Add easy way to update config & override files each update — [card #48](https://trello.com/c/zc5HElxS/48-add-easy-way-to-update-config-and-override-files-each-update)
Add an `auto-update-configs` setting at the very top of the main config, default **true**. Warn admins it can overwrite custom settings on update, and note that turning it off means manually updating configs.
- **Why here:** Central config-lifecycle plumbing the rest of the config/resourcepack work leans on. Likely extends the recent *"config synchronization"* commit — I'll reconcile with that rather than duplicate it.
- **Deps:** none hard (do after #1 to avoid churn).

### 3. ✅ DONE — Denote which mod-asset files are even presented to the admin — [card #68](https://trello.com/c/fLxeC92y/68-add-a-way-to-denote-which-files-from-mod-assets-should-even-be-presented-to-the-admin)
Config setting controlling which asset files get written into the server config folder. Keep some always visible (e.g. doll rarities); hide hardcoded-tied ones (e.g. substitute divisor) to de-clutter for the average user.
- **Deps:** builds on the config-population logic from #2.

---

## Phase 3 — Resource-pack system

### 4. ✅ DONE — Improve resource-pack generation & serving for client caching — [card #56](https://trello.com/c/xIUlpTh5/56-improve-resource-pack-generation-and-serving-to-allow-the-client-to-cache-the-resource-pack)
Let the client cache the served pack (stable hashing / ETag / conditional requests in `ResourcePackServer` + `CustomPackBuilder`).
- **Why here:** Foundational resource-pack infra that #5 and #6 build on.
- **Deps:** none hard; precedes #5/#6.

### 5. ✅ DONE — Resource packs can override all settings like built-in dolls — [card #49](https://trello.com/c/Q3buLnzk/49-ensure-resource-packs-have-a-way-to-override-all-settings-like-the-built-in-dolls-can)
Extend the override mechanism so external resource packs reach parity with built-in dolls.
- **Deps:** #4 (shared pack pipeline).

### 6. ✅ DONE — Resource packs can add custom figurines or decorations — [card #50](https://trello.com/c/W80VVgsi/50-ensure-resource-packs-can-add-custom-figurines-or-decorations)
Allow packs to register new figurines/decorations, not just override existing ones.
- **Deps:** #4, and shares the loading/override path with #5.

---

## Phase 4 — Models & content polish (parallel track — can run alongside Phases 2–3)

### 7. 🟡 Make gigantics item model bigger than the regular dolls — [card #59](https://trello.com/c/eIRKdq7X/59-make-gigantics-item-model-bigger-than-the-regular-dolls)
- **Claude:** adjust `display`/scale values in the gigantic item-model JSON.
- **Human:** confirm the in-game size looks right; may need a scale pass or two.

### 8. 🟡 Check all block break speeds / tools / sounds — [card #61](https://trello.com/c/1D9wUncb/61-check-all-block-break-speeds-tools-sounds-etc)
- **Claude:** audit and set block properties in code (hardness/destroy time, correct tool/tier, `SoundType`) across the block definitions for consistency.
- **Human:** play-test that the "feel" (mining speed, sound) is right.

### 9. 🟡 Inspect & fix any weird item models — [card #58](https://trello.com/c/c1TfB5Tt/58-inspect-and-fix-any-weird-item-models)
Known: Luvdisc cushion.
- **Human:** visually identify what's wrong (rotation/scale/origin) — needs the model rendered.
- **Claude:** apply the JSON/transform fixes once the defect is described.

### 10. 🔴 Review new models — [card #60](https://trello.com/c/v2pxLN7r/60-review-new-models) · *also labeled Matt*
Known: Cutiefly's legs clip into the ground.
- **Human (modeling):** geometry/rig fixes in Blockbench — outside what I can do. Coordinate with Matt.

---

## Phase 5 — Quality passes (after feature work lands)

### 11. ✅ Review entire project for improvements — [card #55](https://trello.com/c/qTaasmcG/55-review-entire-project-for-improvements)
Full-codebase review (correctness, structure, multi-loader duplication, reuse).
- **Why here:** Most valuable once Phases 2–3 are merged so it covers the new code. I produce the findings; you decide what to action.
- **Deps:** Phases 2–3.

### 12. 🟡 Run the mod with a profiler and look for optimizations — [card #25](https://trello.com/c/dlIwoSuV/25-run-the-mod-with-a-profiler-and-look-for-optimizations)
- **Human:** run the game + profiler (Spark / JFR) and capture a recording — I can't launch Minecraft.
- **Claude:** analyze the captured dump (I have JFR/flamegraph tooling) and implement the fixes it points to.
- **Deps:** needs a feature-complete build (after Phases 2–3).

---

## Phase 6 — Documentation (after features are finalized)

### 13. 🟡 Create wiki pages for resource packs & new config files — [card #22](https://trello.com/c/gThiD41F/22-create-wiki-pages-for-resourcepacks-and-new-config-files)
- **Claude:** draft the full wiki markdown for the new config (#2, #3) and resource-pack (#4–#6) behavior.
- **Human:** publish to the wiki platform (GitHub wiki / site) and add screenshots.
- **Deps:** #2, #3, #4, #5, #6 (document the final, settled behavior).

---

## Phase 7 — Release (gates on everything)

### 14. 🔴 Merge final update to master — [card #8](https://trello.com/c/VP89VTX6/8-merge-final-update-to-master)
- **Human:** release decision + final merge from `multiloader/initial` → `master`. I can prep/clean the branch and open the PR, but the merge is a person's call.
- **Deps:** all of the above.

---

### Dependency map (quick view)

```
#1 Logger ─┐
           ├─► (independent quick win)
#2 config-update ──► #3 config-file-presentation ─┐
#4 RP caching ──► #5 RP overrides                 ├─► #13 Wiki ──► #14 Merge to master
              └─► #6 RP custom content ───────────┘                     ▲
#11 Project review  (after #2–#6) ──────────────────────────────────────┤
#12 Profiler (after #2–#6, human-run) ───────────────────────────────────┤
#7 #8 #9 #10  model/content polish (parallel) ───────────────────────────┘
```
