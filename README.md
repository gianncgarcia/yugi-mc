<div align="center">

# Yugi MC — "Yugi+"

**A client-side Minecraft Fabric utility mod, maintained across four Minecraft versions (1.21 → 26.3).**

Built from scratch with Java, Gradle, and bytecode-level Mixin injection into the Minecraft client.

[![build](https://github.com/gianncgarcia/yugi-mc/actions/workflows/build.yml/badge.svg)](https://github.com/gianncgarcia/yugi-mc/actions/workflows/build.yml)
[![License: CC0-1.0](https://img.shields.io/badge/License-CC0--1.0-lightgrey.svg)](https://creativecommons.org/publicdomain/zero/1.0/)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21%20%7C%2026.1%E2%80%9326.3-62A0D8?logo=minecraft&logoColor=white)
![Java](https://img.shields.io/badge/Java-21%20%7C%2025-orange?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-Multi--project-02303A?logo=gradle&logoColor=white)

</div>

---

## About The Project

**Yugi+** is a quality-of-life / utility client mod for Minecraft (Java Edition) running on the
[Fabric](https://fabricmc.net/) toolchain. It bundles a dozen client-side features — rendering
overlays, input toggles, network utilities, and gameplay helpers — behind keybinds, an on-screen
HUD, and a full Brigadier client command tree.

What makes this project interesting as an engineering exercise is that **none of it can be done
with normal mod code**: Minecraft's client is a closed binary, so every feature works by
*injecting code into specific methods of the game at class-load time* using the
[SpongePowered Mixin](https://github.com/SpongePowered/Mixin) framework. The repo is also a study
in **multi-version maintenance**: the same mod ID is built and shipped for Minecraft 1.21 and the
new 26.x version line, each against its own mappings, loader, and Fabric API release.

> [!IMPORTANT]
> **Fair play notice.** Some features (Brand Spoof, FakeLag, No Mining Cooldown) give a
> competitive or technical edge and are **against the rules on most public servers**
> (Hypixel, etc.). This project exists as a technical demonstration of client modding —
> use it on singleplayer or servers where you have permission. The author is not
> responsible for account sanctions.

<!-- Screenshots — drop images into /docs and uncomment
<p align="center">
  <img src="docs/screenshots/hud.png" width="45%"/>
  <img src="docs/screenshots/outline.png" width="45%"/>
</p>
-->

---

## Features

| Feature | Default | Keybind / Command | What it does |
|---|---|---|---|
| **Entity Outline** | Off | Keybind or `/yugimc outline …` | Glow-outlines any player, entity, or block type in the world, with per-target custom colors |
| **Nametag Sneak** | On | Keybind | Player nametags stay visible (full-bright, unlimited render distance) even while sneaking |
| **Brand Spoof** | On | Keybind or `/yugimc brandspoof <text>` | Reports the client brand (default `vanilla`) to the server instead of `fabric` |
| **Anti-Blind** | On | Keybind | Neutralizes Blindness/Darkness effects and their fog/lightmap rendering |
| **No Hurt Cam** | On | Keybind | Removes the screen-shake / tilt when taking damage |
| **Zoom** | Off | Keybind | 4.3× FOV zoom (multiplier `0.23`) |
| **Portal GUI** | On | Keybind | Suppresses portal nausea so GUIs are usable inside nether portals |
| **No Mining Cooldown** | On | Keybind | Removes the block-breaking delay between mined blocks |
| **FakeLag** | Off | Keybind or `/yugimc fakelag <0-5000>` | Delays all outgoing packets by a configurable window (with optional random jitter) |
| **Fast Pickup** | On | Keybind | Items can be collected the instant they drop (no pickup delay) |
| **Pickup Magnet** | Off | Keybind | Items within 1.5 blocks are pulled toward the player |
| **HUD Overlay** | Hidden | `V`+`B` combo | On-screen panel listing every currently enabled feature |

All keybinds are **unbound by default** and can be assigned in *Options → Controls → Key Binds → Yugi+*.

---

## How It Works

### Architecture overview

The mod is a standard Fabric mod with three cooperating layers:

```
┌────────────────────────────────────────────────────────────────┐
│  Fabric Loader                                                 │
│    ├── main entrypoint        com.yugimc.YugiMC                │
│    │     └── YugiClient.initialize()                          │
│    │           ├── registers 12 KeyMappings (Yugi+ category)  │
│    │           ├── ClientTickEvents.END_CLIENT_TICK handlers  │
│    │           └── ClientPlayConnectionEvents.DISCONNECT reset│
│    └── mixins                 yugi-mc.mixins.json              │
│          └── SpongePowered Mixin weaves hooks into the        │
│              Minecraft client classes at class-load time      │
├────────────────────────────────────────────────────────────────┤
│  Command layer                                                 │
│    └── Brigadier dispatcher (Fabric Client Command API v2)    │
│        /yugimc outline | fakelag | brandspoof | ip            │
├────────────────────────────────────────────────────────────────┤
│  State layer                                                   │
│    └── YugiClient (enum singleton) — feature flags, packet    │
│        queue, outline maps, block-position cache              │
└────────────────────────────────────────────────────────────────┘
```

- **`YugiMC`** — the `ModInitializer` entrypoint. Boots `YugiClient`, registers commands, and
  guards against double initialization.
- **`YugiClient`** — an enum-singleton that owns every feature flag, the shared tick handler
  (keybind polling, anti-blind effect removal, block rescanning, FakeLag flushing), the
  60+ entry mob-color palette (`MOB_COLORS`), and the named-color table (`COLOR_NAMES`).
- **Mixins** — one small class per vanilla target class (see below). Each hook reads the
  shared feature flags, so mixins stay dumb and all logic lives in one place.

### Feature deep-dives

#### Entity / Player / Block Outline
Three mixins cooperate:

| Mixin | Target (26.3) | Role |
|---|---|---|
| `MinecraftMixin` | `Minecraft#shouldEntityAppearGlowing` | Forces the glowing render flag for outlined targets |
| `EntityMixin` | `Entity#getTeamColor` | Returns the outline color: per-name override, `MOB_COLORS` palette, or the entity's team color |
| `LevelRendererMixin` | `LevelRenderer#collectPerFrameRenderThreadGizmos` | Draws glow cuboids around outlined blocks |

Two targeting modes:
- **All** — `/yugimc outline on` sets `entityOutlineAll = true`; every rendered entity glows.
- **Selective** — names/IDs are stored in `outlinedPlayers` / `outlinedEntities` maps
  (`name → ARGB color`, `-1` = "use team/default color"). Pressing the same command on an
  already-outlined target *toggles it off*.

**Block outlines** are the most involved: the command registers block IDs into
`outlinedBlockTypes`. A **throttled world scan** (max once per 5 s) then walks loaded chunks in a
±32-block vertical band around the player — clamped to render distance, skipping unloaded chunks
via `hasChunk` — using a single reusable `BlockPos.MutableBlockPos`, and caches every match in a
`ConcurrentHashMap.newKeySet()` (`cachedBlockPositions`). The renderer draws cuboids from that
cache, so there is zero per-frame scanning cost. The cache is invalidated whenever the outline
list changes or the player moves to rescan.

#### Brand Spoof
`ClientBrandRetrieverMixin` injects at `HEAD` of `ClientBrandRetriever#getClientModName` and
cancels the original return, substituting `brandSpoofText` (default `"vanilla"`). The brand string
is what the server reads from the client's connection handshake channel — this makes
Fabric-based clients indistinguishable from vanilla at the brand-check level.
Configurable at runtime: `/yugimc brandspoof <text>`.

#### FakeLag (packet delay)
`ClientConnectionMixin` injects at `HEAD` of `Connection#send` (the single choke point for all
outbound traffic):

1. While `fakeLagEnabled`, every outbound packet is **cancelled in-place** and appended to
   `packetQueue`.
2. `ServerboundKeepAlivePacket` is deliberately *not* delayed — the server's keep-alive
   responses keep flowing so the connection is never kicked for timing out.
3. A client-tick scheduler calls `PacketUtil.flushPackets()` every `fakelagDelay` ms
   (configurable 0–5000 ms), replaying the queue in order.
4. `fakelag randomize` adds ±40% jitter to the flush interval to simulate unstable connections.
5. An `isFlushing` re-entrancy guard stops the flush itself from being re-captured, and
   **disconnect cleanup** (`ClientPlayConnectionEvents.DISCONNECT`) empties the queue and resets
   all FakeLag state so stale packets can never leak into the next server.

#### Anti-Blind
Defends against Blindness and Darkness with three independent layers so no rendering path leaks:

- **Effect layer** — an `END_CLIENT_TICK` handler removes the `DARKNESS` status effect from the
  local player the moment it appears.
- **Fog layer** — `FogRendererMixin#setupFog` (26.x) rewrites the returned `FogData` at `RETURN`,
  pushing `renderDistanceStart` to 10× the end distance so the "closing-in" fog never engages.
  (1.21 equivalent: `BackgroundRendererMixin` strips the effect's fog modifier.)
- **Light layer** — `LivingEntityMixin#getEffectBlendFactor` (26.x) flattens the blend factor of
  the darkness overlay to neutral; (1.21: `LightTextureManagerMixin` keeps the lightmap updated as
  if the effect were absent, and `WorldRendererMixin#hasBlindnessOrDarkness` reports false).

#### Nametag Sneak
`EntityRendererMixin` hooks the render-state extraction pass:
- `renderState.isDiscrete = false` — undoes the "sneaking shrinks/hides nametag" flag.
- `renderState.lightCoords = 15728880` — full-bright light coordinate, so tags are readable in
  caves and at night.
- `@ModifyArg` on the nametag extraction call sets render distance to `Double.MAX_VALUE` —
  tags render at any range instead of the default ~64-block cutoff.

#### Zoom
`CameraZoomMixin#calculateFov` (26.x) / `MinecraftZoomMixin#getFov` (1.21) multiplies the computed
FOV by `0.23` while `zoomEnabled` is set — a clean optical zoom that keeps camera position,
mouse sensitivity, and perspective untouched.

#### Portal GUI
Vanilla applies a warp/nausea effect while standing in a portal, which distorts the screen and
makes GUIs impractical. `LocalPlayerMixin#handlePortalTransitionEffect` (26.x) /
`ClientPlayerEntityMixin#tickNausea` (1.21) neutralize the transition effect so screens render
normally inside portals.

#### Fast Pickup / Pickup Magnet
Both live in `ItemEntityMixin` on `ItemEntity#tick`, client-side only:
- **Fast Pickup** — `setPickUpDelay(0)` removes the 10-tick delay before dropped items can be
  collected.
- **Pickup Magnet** — for items within a 1.5-block radius (squared-distance check between
  `1.0` and `2.25`, spectating players excluded), sets the item's velocity to a normalized
  `0.4`-magnitude vector aimed at the nearest player — a smooth, continuous pull.

#### No Mining Cooldown
`MultiPlayerGameModeMixin` hooks `startDestroyBlock` / `continueDestroyBlock` so the client
doesn't insert the post-break hold delay, allowing continuous mining at block-change speed.

#### HUD Overlay
`HudMixin` injects at `TAIL` of `Hud#extractRenderState` and draws a left-aligned panel (vertically
centered) listing every active feature with color-coded status. Toggled with the `V`+`B`
combination (500 ms debounce so the tick handler can't double-fire).

### Command system

Commands are registered through the **Fabric Client Command API v2** (Brigadier), so they run
client-side and never reach the server. The 26.x command tree:

```
/yugimc
├── outline
│   ├── on | off | clear | list
│   ├── player <name> [color]        (+ player clear)
│   ├── entity <name> [color]        (+ entity clear)
│   └── block <name>                 (+ block clear)
├── fakelag <delay 0-5000>
│   └── randomize                    (toggle ±40% jitter)
├── brandspoof <text>
└── ip                               (click-to-copy server address + resolved IP)
```

Custom `SuggestionProvider`s make the tree feel native:
- **players** — live names from `level.entitiesForRendering()`
- **entities** — entity type IDs actually present in the loaded world
- **blocks** — the full block registry (`BuiltInRegistries.BLOCK`)
- **colors** — 18 named colors (`red`, `gold`, `aqua`, `pink`, …) plus any `#RRGGBB` hex value

The `/yugimc ip` command resolves the server's address asynchronously via
`InetAddress.getByName` and attaches a `ClickEvent.CopyToClipboard` with a hover hint — a small
example of mixing Brigadier feedback components with background work.

### State management & safety

- All runtime state lives on `YugiClient` as plain statics — no world objects are cached across
  sessions.
- **On disconnect**, everything server-scoped is wiped: outline maps, block caches, packet queue,
  FakeLag timers. Nothing from server A can affect server B.
- The block scan is bounded (render-distance clamp + vertical band + 5 s throttle) and uses a
  concurrent set, so it never blocks the render or tick thread.

---

## Commands & Keybinds Reference (26.x)

| Command | Effect |
|---|---|
| `/yugimc outline on` / `off` | Glow everything / clear all glow state |
| `/yugimc outline player <name> [color]` | Toggle outline on a player (repeat to remove) |
| `/yugimc outline entity <name> [color]` | Toggle outline on an entity type |
| `/yugimc outline block <name>` | Toggle outline on a block type (world scan) |
| `/yugimc outline clear` | Clear players + entities + blocks |
| `/yugimc fakelag <0-5000>` | Set packet delay window (ms) |
| `/yugimc fakelag randomize` | Toggle jitter on the flush interval |
| `/yugimc brandspoof <text>` | Set spoofed client brand (enables spoofing) |
| `/yugimc ip` | Print + click-copy the server address and resolved IP |

| Keybind | Action |
|---|---|
| *(unbound)* | Toggle each feature (Entity Outline, Nametag Sneak, Brand Spoof, Anti-Blind, Zoom, Hurt Shake, Portal GUI, No Mining Cooldown, FakeLag, Pickup Magnet, Fast Pickup) |
| `V` + `B` | Show/hide the HUD overlay |

Every toggle prints a `§a[Yugi+]` chat confirmation.

---

## Supported Versions

This repo is a **Gradle multi-project**: the root builds the 1.21 module, and each `yugi-mc-26.x`
folder is a self-contained Gradle project for the newer version line (new loader, new mappings,
new Fabric API).

| Module | Minecraft | Fabric Loader | Fabric API | Loom | Mappings | Java |
|---|---|---|---|---|---|---|
| `.` (root) | **1.21** | 0.15.11 | 0.100.4+1.21 | 1.7 | Yarn `1.21+build.7` | 21 |
| `yugi-mc-26.1` | **26.1** | 0.19.3 | 0.145.1+26.1 | 1.17.12 | Mojang official | 21+ |
| `yugi-mc-26.2` | **26.2** | 0.19.3 | 0.153.0+26.2 | 1.17.12 | Mojang official | 21+ |
| `yugi-mc-26.3` | **26.3** | 0.19.5 | 0.161.0+26.3 | 1.18 | Mojang official | 25 |

> The 1.21 module uses **Yarn** mappings (Fabric's community names, e.g. `MinecraftClient`),
> while the 26.x modules use **Mojang's official mappings** (e.g. `Minecraft`). Keeping both in
> one repo documents the real-world cost of multi-version mod maintenance: class names, method
> signatures, and even rendering pipelines (`WorldRenderer` → `LevelRenderer`, fog handling, the
> new `Hud`/render-state extraction API) shift between versions, and each module pins exactly
> the mixins that work there.

---

## Getting Started

### Prerequisites

- **JDK 21** (1.21 module) and/or **JDK 25** (26.3 module)
- Git
- An IDE is optional — IntelliJ IDEA with the Minecraft Development plugin is recommended

### Install (as a player)

1. Build the module for your Minecraft version (see below) or grab a jar from
   [Releases](https://github.com/gianncgarcia/yugi-mc/releases).
2. Install the [Fabric Loader](https://fabricmc.net/use/installer/) for that Minecraft version.
3. Drop **Fabric API** and the built `yugi-mc-*.jar` into your `mods/` folder.
4. In game: *Options → Controls → Key Binds → Yugi+* to bind features, and try
   `/yugimc outline entity creeper red`.

### Build from source

```bash
# 1.21 module (repo root, JDK 21)
./gradlew build
# → build/libs/yugi-mc-1.0.0.jar

# 26.x modules (each folder is its own Gradle project)
cd yugi-mc-26.3 && ./gradlew build
# → yugi-mc-26.3/build/libs/yugi-mc-1.0.0.jar
```

### Run in a dev environment

```bash
./gradlew runClient    # launches a Minecraft client with the mod loaded
```

---

## Project Structure

```
yugi-mc/
├── src/main/java/com/yugimc/            # Minecraft 1.21 module (Yarn mappings)
│   ├── YugiMC.java                      # ModInitializer entrypoint
│   ├── YugiClient.java                  # Feature flags, keybinds, tick handlers
│   ├── command/                         # 1.21 command classes
│   ├── mixin/                           # 10 mixins into the 1.21 client
│   │   ├── ClientBrandRetrieverMixin.java   # Brand Spoof
│   │   ├── GameRendererMixin.java           # No Hurtcam (tiltViewWhenHurt)
│   │   ├── MinecraftZoomMixin.java          # Zoom (getFov)
│   │   ├── BackgroundRendererMixin.java     # Anti-Blind (fog)
│   │   ├── LightTextureManagerMixin.java    # Anti-Blind (lightmap)
│   │   ├── WorldRendererMixin.java          # Anti-Blind + Entity Outline
│   │   ├── MinecraftClientMixin.java        # Entity Outline (hasOutline)
│   │   ├── EntityRendererMixin.java         # Nametag Sneak (renderLabelIfPresent)
│   │   ├── ClientPlayerEntityMixin.java     # Portal GUI (tickNausea)
│   │   └── DoubleSliderCallbacksMixin.java  # UI callback validation fix
│   └── resources/
│       ├── fabric.mod.json              # Mod metadata (entrypoints, mixins, deps)
│       ├── yugi-mc.mixins.json          # Mixin registration
│       └── assets/yugi-mc/icon.png
├── yugi-mc-26.1/                        # Minecraft 26.1 module (Mojang mappings)
├── yugi-mc-26.2/                        # Minecraft 26.2 module (Mojang mappings)
├── yugi-mc-26.3/                        # Minecraft 26.3 module — flagship
│   └── src/main/java/com/yugimc/
│       ├── YugiMC.java                  # Entrypoint + init guard
│       ├── YugiClient.java              # All state: flags, queues, caches, palettes
│       ├── command/YugiMCCommand.java   # Full /yugimc Brigadier tree + suggestors
│       ├── util/
│       │   ├── ModCommandRegister.java  # Command registration hook
│       │   └── PacketUtil.java          # FakeLag flush logic
│       └── mixin/                       # 14 mixins into the 26.3 client
│           ├── MinecraftMixin.java          # Entity Outline (shouldEntityAppearGlowing)
│           ├── EntityMixin.java             # Entity Outline (getTeamColor)
│           ├── LevelRendererMixin.java      # Block Outline (gizmos)
│           ├── ClientConnectionMixin.java   # FakeLag (Connection#send)
│           ├── ClientBrandRetrieverMixin.java# Brand Spoof
│           ├── FogRendererMixin.java        # Anti-Blind (setupFog)
│           ├── LivingEntityMixin.java       # Anti-Blind (getEffectBlendFactor)
│           ├── GameRendererMixin.java       # No Hurtcam / Portal GUI
│           ├── CameraZoomMixin.java         # Zoom (calculateFov)
│           ├── EntityRendererMixin.java     # Nametag Sneak (render states)
│           ├── LocalPlayerMixin.java        # Portal GUI (portal transition)
│           ├── MultiPlayerGameModeMixin.java# No Mining Cooldown
│           ├── ItemEntityMixin.java         # Fast Pickup / Pickup Magnet
│           └── HudMixin.java               # HUD Overlay
└── .github/workflows/build.yml          # CI: build + artifact upload on every push/PR
```

---

## What This Project Demonstrates

- **Java 21/25** — modern client-side application code, enum singletons, collections, concurrency
  (`ConcurrentHashMap` key sets for cross-thread caching)
- **Bytecode weaving with SpongePowered Mixin** — `@Inject`, `@Redirect`, `@ModifyArg`, injection
  points (`HEAD`, `TAIL`, `RETURN`), cancellable callbacks, and re-entrancy guards
- **Reverse-engineering a closed binary** — reading obfuscated/mapped game code and finding the
  minimal interception point for each feature across two mapping ecosystems (Yarn + Mojang)
- **Multi-version engineering** — four parallel builds against shifting internal APIs, with
  per-version mixin sets and dependency pins
- **Gradle multi-project builds** — fabric-loom configuration, resource expansion
  (`processResources`), dependency pinning, and per-module wrappers
- **Brigadier command design** — nested literals/arguments, runtime argument validation, custom
  suggestion providers backed by live game state
- **Client networking internals** — intercepting the outbound packet pipeline while preserving
  protocol-level liveness (keep-alive passthrough)
- **CI/CD** — GitHub Actions building and archiving artifacts on every push and pull request

---

## References & Further Reading

This project stands on the shoulders of the Fabric ecosystem — these are the references that
document every technique used here:

| Resource | Link |
|---|---|
| **Fabric documentation** (setup, entrypoints, events) | https://docs.fabricmc.net/ |
| **Fabric develop portal** (version/loader/API matrices) | https://fabricmc.net/develop/ |
| **fabric-example-mod** (the scaffold this repo started from) | https://github.com/FabricMC/fabric-example-mod |
| **SpongePowered Mixin** (the bytecode-injection framework) | https://github.com/SpongePowered/Mixin |
| **Mixin wiki** (annotations: `@Inject`, `@Redirect`, `@ModifyArg`…) | https://github.com/SpongePowered/MixinWiki/wiki |
| **Fabric API** (events, keybindings, client commands, networking) | https://github.com/FabricMC/fabric |
| **Yarn mappings** (community mappings used by the 1.21 module) | https://github.com/FabricMC/yarn |
| **fabric-loom** (the Gradle plugin that builds Fabric mods) | https://github.com/FabricMC/fabric-loom |
| **Brigadier** (Mojang's command engine behind `/yugimc`) | https://github.com/Mojang/brigadier |
| **Minecraft wiki — client brand & protocol details** | https://minecraft.wiki/ |

---

## Roadmap

- [ ] Config file persistence (save feature flags between sessions)
- [ ] In-game configuration screen (the `;` selector bind is reserved for it)
- [ ] Per-entity outline colors derived live from `MOB_COLORS` in all versions
- [ ] ModMenu integration for settings UI

## License

Distributed under the **CC0 1.0 Universal** license (public domain). See [`LICENSE`](LICENSE).

---

<div align="center">

Built by **[gianncgarcia](https://github.com/gianncgarcia)**

</div>
