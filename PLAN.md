# PLAN.md — HOROLOGY watch store

Store-app showcase built with Kotlin KMP + Compose Multiplatform, modern "Light minimal Swiss" design.
Data: bundled JSON catalog + verified image URLs. Full shopping flow, offline-friendly.
Platforms: Android (buildable on this machine) + iOS (scaffold only — Xcode/macOS required).

## Milestones

| # | Milestone | Status |
|---|---|---|
| 1 | Scaffold: git, KMP modules, AGP9-KMP plugin setup, wrapper, docs, green `:androidApp:assembleDebug` | ✅ done |
| 2 | Design system: Swiss theme (palette/type/grid), tokens, custom icon vectors, core components | ✅ done |
| 3 | Data layer: models, catalog JSON + repository, Room cart/favorites, DataStore onboarding flag | ✅ done |
| 4 | Navigation shell + bottom bar + screen transitions | ✅ done (stub screens) |
| 5 | Onboarding: Canvas Swiss dial animation + parallax pages | pending |
| 6 | Home: masthead, featured hero, collections rail, staggered grid, nav bar | pending |
| 7 | Catalog + search + filter chips | pending |
| 8 | Product detail: gallery, spec accordion, add-to-cart flight, heart burst | pending |
| 9 | Cart + checkout confirm: swipe-dismiss, count-up total, success check | pending |
| 10 | Favorites + polish pass (empty states, insets, deep polish) | pending |
| 11 | Final QA: assembleDebug + allTests + docs refresh | pending |

## Accepted decisions

- AGP 9 rule: shared module uses `com.android.kotlin.multiplatform.library`; launcher is separate thin `androidApp`.
- `compileSdk 37` required by Coil 3.6.3 artifacts; `targetSdk 36`, `minSdk 26`.
- Material3 `1.12.0-alpha03` (JetBrains split-version pattern).
- Navigation from JetBrains mirror `org.jetbrains.androidx.navigation` 2.10.0-rc01 (type-safe routes).
- Manual DI (lazy service locator) — no Koin/Hilt for a UI showcase.
- 20 image source URLs already verified 200 OK (mixture of Unsplash + Pexels); final 24-30 products map onto these at data-layer time; each image gets a themed gradient placeholder fallback.
- **Catalog = 13 curated references** (was "24-30"): the 20 candidate URLs were VISUALLY verified via contact sheets (2026-10-16) — only 13 are product-grade watch photography; several were off-topic (Doritos/cat/ocean/shots) or brand-unsafe (visible Cartier trademark, Apple Watch). Every shipped URL re-verified 200 + image/jpeg at final w=1200 size. Pocket-watch image reserved for onboarding, not a product.
- **Room KMP gotchas (compile-proven):** @Database needs `@ConstructedBy` + expect `RoomDatabaseConstructor` object (native); Android-only `withTransaction` is forbidden — DAO ops use single-statement atomic SQL (upsert-increment / decrement+prune); `Dispatchers.IO` is JVM-only — common/ios code uses `Dispatchers.Default`.
- **Kotlin pinned 2.4.20** (not 2.4.21): CMP 1.12.2 resources accessors failed to resolve with 2.4.21; 2.4.20 is JetBrains' CI-tested pair.
- **Fonts are composition-provided** (CMP `Font()` is @Composable): `HoroFonts.grotesk()/mono()` called inside `HorologyTheme`; named tokens via `LocalHoroType.current.*` (static top-level TextStyle vals don't exist by design).

## Milestone 5 — Onboarding ✅ (2026-10-16)

- [x] 3-page HorizontalPager; per-page Masthead w/ trailing SKIP; mono body copy
- [x] Giant ghost numerals 01/02/03 (grotesk bold 200sp, ink 7%) on slow parallax layer (0.42), dial on 0.16 — both settle exactly at rest, fade with drag
- [x] `RailwayDial` (Canvas): white face + hairline ring, 60-bar railway track (major at hours), ink hour/minute hands (rounded, tails), red 2nd hand w/ lollipop disc + beat easing (EaseOutBack snap in first 15% of each second, rest after) — all angles derived from real wall clock (frame-time aligned to `nowMs()`); draw-phase-only state reads (no recomposition/frame)
- [x] Staggered copy fade+slide: 60ms cascade, EaseOutCubic 420ms; replay on page activation
- [x] Spring page indicator: 4dp hairline dots → 10dp red with overshoot spring (damping 0.5)
- [x] Reduce motion: `motionDurationScale()` expect/actual — Android animator scale (0 → 0.5 halving), iOS `UIAccessibilityIsReduceMotionEnabled` → 0.5; scales stagger + gate transitions
- [x] Gate exit = shared-axis (1/6 slide + crossfade) via `AnimatedContent` in App.kt
- [x] Device-verified on horo10 (emulator-5556): fresh install → p1/p2/p3 correct, dial live & true-time, Explore → Home, force-stop relaunch → Home (persistence), no FATAL; Android compile + iOS klib both green
- Notes: page copy echoes the app's tab mastheads (HOROLOGY / The Inventory / Yours to Wind); EXPLORE completes from any page; SKIP in masthead trailing slot

## Remaining risks / TODO

- iOS: Xcode project + Info.plist + Assets scaffold not yet written (after Android passes milestones 2-4).
- **M1 crash fixed:** AGP-9 built-in Kotlin does **not** auto-apply the Compose compiler — `androidApp` was missing it, so `MainActivity`'s `setContent` lambda shipped as plain `Function0` while `activity-compose`'s method signature expects the compiler-transformed `Function2` → runtime `NoSuchMethodError`. Fix: `plugins { alias(libs.plugins.composeCompiler) }` in `androidApp/build.gradle.kts`. Verified live: app boots, no FATAL.
- **Emulator testing isolation:** the shared `pixel10` AVD (emulator-5554, "Pulse") is used by another AI agent (com.taxiapp.android) — a separate AVD `horo10` (emulator-5556, same API-37 image, fresh userdata) was handcrafted at `~/.android/avd/horo10.*` for HOROLOGY testing. Use `adb -s emulator-5556`.
- On-device flow verified (2026-10-16, emulator-5556): onboarding CTA → Home; all 4 tabs track the sliding red dot + filled icons; force-stop + relaunch skips onboarding (DataStore gate persists).
- `gradle-9.8.1` cached dist used; wrapper pinned to same version.
