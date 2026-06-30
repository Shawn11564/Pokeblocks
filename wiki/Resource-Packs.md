# Resource Packs (Serving & Caching)

This page is for **server admins** and **pack makers**. It explains how Pokeblocks builds its custom resource pack from your sub-packs, how that pack is served to players (either self-hosted by the mod or from your own URL), and how content hashing keeps client downloads cached and stable across restarts.

If you want the broader config reference, see [Configuration](Configuration). For how to author the sub-packs that get merged into the served pack, see [Custom Content & Admin Override Packs](Custom-Content-Packs). For the full command list, see [Commands](Commands).

---

## Overview: how the pack reaches players

1. **Build on start** — when the server is about to start, Pokeblocks scans every sub-pack under `config/Pokeblocks/resourcepack/`, merges them into a single zip, and caches it as `pokeblocks_custom_pack.zip` in the server directory.
2. **Serve** — depending on the `distribution` mode, the pack is either served by a small built-in HTTP server (default) or advertised from a remote URL you control.
3. **Push on join** — every player is sent the pack when they join. Admins can rebuild and re-push at runtime with a command.

The built pack always advertises a **content-derived UUID** so the vanilla client reuses its cached download whenever the pack content hasn't changed.

---

## Building the pack

On `onServerAboutToStart`, Pokeblocks builds and caches the merged pack:

- Every sub-pack under `config/Pokeblocks/resourcepack/` is scanned and merged into `<serverDir>/pokeblocks_custom_pack.zip`.
- The merged `pack.mcmeta` is written with **`pack_format` 26** and description **`Pokeblocks Custom Dolls`**.
- Sub-packs are loaded in **alphabetical order** for a deterministic, stable hash. The `custom/` folder is loaded **last**, so it overrides all other sub-packs.
- Cross-pack file conflicts are logged during the build.
- All zip entries are written with a fixed timestamp (`setTime(0L)`) so the same inputs always produce the same hash.

### Rebuild-skip (stable hash across restarts)

Before rebuilding, Pokeblocks computes an **input fingerprint** — a SHA-1 over the sorted `relative-path|size|mtime` of every file under `config/Pokeblocks/resourcepack/`. If the cached zip still exists and the fingerprint matches the previously cached one, **the build is skipped** and the existing cached pack and SHA are reused. This keeps the served pack's SHA (and therefore its UUID and ETag) stable across restarts when nothing has changed.

### Sub-pack layout

```
config/Pokeblocks/resourcepack/
  <anypack>/            # a folder OR a .zip; loaded in alphabetical order
    assets/
      dolls/      {models,textures,animations}/<bare-id>.<ext>   # typed layout (preferred)
      figurines/  {models,textures}/<bare-id>.<ext>
      decorations/{models,textures}/<bare-id>.<ext>
  custom/               # loaded LAST -> overrides all other sub-packs; may contain pack.png (pack icon)
    assets/{models,textures,animations}/<full_name>
```

See [Custom Content & Admin Override Packs](Custom-Content-Packs) for the full authoring guide.

---

## Distribution modes

The serving behavior is controlled in the `[resourcepack]` section of `config/Pokeblocks/config.toml`:

```toml
[resourcepack]
distribution = self_host   # self_host (default) | remote_url
```

The default is **`self_host`**. Parsing tolerates aliases:

- `remote`, `url`, `remoteurl` → `remote_url`
- `self`, `host`, `selfhost`, `self-host` → `self_host`
- Any unknown value falls back to `self_host`.

### Mode 1: `self_host` (default)

Pokeblocks starts a built-in `HttpServer` bound to `0.0.0.0` on an **ephemeral, OS-chosen port**, with a single-thread executor, serving the cached zip at the context path `/pokeblocks_custom_pack.zip`. Starting it is idempotent: if it's already up and serving the same file, the existing URL is reused.

The advertised URL takes the form:

```
http://<host>:<port>/pokeblocks_custom_pack.zip
```

**How `<host>` is resolved:**

1. If `self_host_address` is set (non-blank), that host/IP is advertised.
2. Otherwise, on a **dedicated server**, the mod uses `server-ip` from `server.properties`; if that's unset, it auto-detects the first non-loopback, up, IPv4 site-local address (e.g. `192.168.x.x` / `10.x.x.x`).
3. Otherwise (integrated/singleplayer), it advertises `127.0.0.1`.

`self_host_address` defaults to an empty string (`""` = auto-detect).

> [!NOTE]
> The auto-detected address is usually **LAN-only**. For a public server, set `self_host_address` to a publicly reachable host or domain, or your players won't be able to download the pack. This setting is only used when `distribution = self_host`.

**Example — self-host on a public server:**

```toml
# config/Pokeblocks/config.toml — self-host on a PUBLIC server
[resourcepack]
kick_on_decline = true
distribution = self_host
self_host_address = "play.example.com"   # reachable public host/IP; blank = auto-detect (LAN-only)
remote_url = ""
remote_sha1 = ""
```

Dedicated-server serving works out of the box: the HTTP server binds to `0.0.0.0` so remote clients can reach it, and the advertised host comes from `server-ip` or an auto-detected LAN IPv4.

### Mode 2: `remote_url`

In `remote_url` mode, Pokeblocks advertises your `remote_url` directly and **tears down the built-in HTTP server**.

- The SHA-1 sent to clients comes from `remote_sha1` if set.
- If `remote_sha1` is blank, it falls back to the locally-built pack's SHA — so you **must upload that exact built zip** to `remote_url`, or clients will reject the download.
- If `remote_url` is blank, nothing is sent (a one-time warning is logged).
- If `remote_sha1` is blank **and** no pack was built locally, nothing is sent.

`remote_url` and `remote_sha1` both default to an empty string (`""`).

> [!NOTE]
> `remote_sha1` is strongly recommended. If you leave it blank, clients are told the hash of the locally-built zip, so the file you host at `remote_url` must be byte-for-byte that exact built zip — otherwise the hash check fails and clients reject it.

**Example — serve from your own CDN:**

```toml
# config/Pokeblocks/config.toml — serve the pack from your own CDN instead
[resourcepack]
distribution = remote_url
remote_url = "https://cdn.example.com/pokeblocks_pack.zip"
remote_sha1 = "<sha1-of-that-exact-zip>"   # if blank, the locally-built pack's sha1 is advertised and you MUST upload that exact zip
```

---

## Requiring the pack

```toml
[resourcepack]
kick_on_decline = true   # default
```

When `kick_on_decline` is `true` (the default), the pushed pack is marked **required** and players see the prompt:

> This server requires the Pokeblocks resource pack to play.

Set it to `false` to make the pack optional.

---

## Caching: stable UUID and HTTP revalidation

Pokeblocks is built so the vanilla client reuses its cached download whenever the pack content is unchanged.

### Content-derived pack UUID

The pack UUID is derived from the pack's SHA-1:

```
packUuid(sha) = UUID.nameUUIDFromBytes("pokeblocks-pack:" + sha)
```

Because the UUID is a function of the content hash, the client reuses its cached download across restarts and reconnects as long as the content is unchanged. The push packet sent to clients is:

```
ClientboundResourcePackPushPacket(packUuid(sha), url, sha, required, prompt)
```

### HTTP ETag / 304 (self-host only)

When self-hosting, the built-in HTTP handler supports conditional requests:

- The **ETag** is the quoted cached SHA-1 (`"<sha>"`).
- If the client's `If-None-Match` header matches, the server replies **`304 Not Modified`** with no body.
- Otherwise it replies **`200`** with `Content-Type: application/zip`, the `ETag` header, `Cache-Control: max-age=0, must-revalidate`, and a `Last-Modified` header from the file's mtime, then streams the zip.

---

## Runtime commands

Both subcommands require **permission level 2** (op). These are the only two `resourcepack` subcommands.

### Rebuild and re-push

```
# Rebuild the served pack from config/Pokeblocks/resourcepack/ and re-push to everyone online (op / perm level 2)
/pokeblocks resourcepack rebuild
```

This rebuilds and re-caches the pack, re-resolves distribution (self-host vs remote, starting the built-in server if self-hosting), then re-sends the pack to **all connected players**. If there's nothing to distribute, it fails with a message. The pack stays rebuildable and re-servable while the server runs.

### Save a worked example

```
# Drop a fully-worked example sub-pack into config/Pokeblocks/resourcepack/example/
/pokeblocks resourcepack saveexample
```

This writes a fully-worked example sub-pack — every pack feature plus a `README.txt` — into `config/Pokeblocks/resourcepack/example/`.

See [Commands](Commands) for the complete command reference.

---

## Where to look in code

- `common/src/main/java/dev/mrshawn/pokeblocks/resourcepack/ResourcePackServer.java` — content-derived `packUuid` (37–39), HTTP handler ETag/304 (69–95), advertised-host resolution (101–117), `prepare`/distribution switch (164–202), push packet (205–211), `pushTo`/`pushToAll` (222–242).
- `common/src/main/java/dev/mrshawn/pokeblocks/resourcepack/CustomPackManager.java` — `buildAndCache` + input-fingerprint rebuild-skip (37–69), cached SHA accessors.
- `common/src/main/java/dev/mrshawn/pokeblocks/resourcepack/CustomPackBuilder.java` — `PACK_NAME` (41), `buildResourcePack` (203–320), `pack.mcmeta` `pack_format` 26 (280–287), `computeSHA1` (445–462), `computeInputFingerprint` (467–493).
- `common/src/main/java/dev/mrshawn/pokeblocks/config/PokeblocksConfig.java` — defaults (19–25, 148–153), `[resourcepack]` parsing (203–211), key comments (305–334).
- `common/src/main/java/dev/mrshawn/pokeblocks/config/PackDistribution.java` — `SELF_HOST`/`REMOTE_URL` enum + alias parsing.
- `common/src/main/java/dev/mrshawn/pokeblocks/command/ResourcePackCMD.java` — command registration (33–41), `rebuild` (43–90), `saveexample` (92–133), permission level 2.
- `common/src/main/java/dev/mrshawn/pokeblocks/PokeblocksServerLifecycle.java:62` — `buildAndCache` on server about-to-start.
- `common/src/main/java/dev/mrshawn/pokeblocks/mixin/PlayerJoinMixin.java:25` — `pushTo` on player join.
- `common/src/main/java/dev/mrshawn/pokeblocks/resourcepack/PackBuildResult.java` — build-result record.
