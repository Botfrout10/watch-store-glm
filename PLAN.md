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
| 6 | Home: masthead, featured hero, collections rail, staggered grid, nav bar | ✅ done |
| 7 | Catalog + search + filter chips | ✅ done |
| 8 | Product detail: gallery, spec accordion, add-to-cart flight, heart burst | ✅ done (dot-flight deferred) |
| 9 | Cart + checkout confirm: swipe-dismiss, count-up total, success check | ✅ done |
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

## Milestone 6 — Home ✅ (2026-10-17)

- [x] Masthead (HOROLOGY Nº 01 / The Boutique) + hero block (matching `FeaturedProducts.first()`)
- [x] Hero parallax: image scales 1.3× inside 1:1 clip, drifts 0.38× of scroll (capped at 0.145 of own height), 0.99 press-scale
- [x] Collections rail: LazyRow of category plates; red border glow fades in while the rail is dragged; press-scale 0.97; representative image per category
- [x] SELECTION header (red mono label + reference count) above 2-col staggered grid; ProductCard reuse (heart toggle → Room)
- [x] GridStagger cascade: 75ms/card (cap 5), 320ms EaseOut tween, 24dp rise; gated by `motionDurationScale`
- [x] Paper skeleton while the bundled catalog parses
- [x] **`formatUsd` fixed** ($$021,450 → $21,450): template was emitting two literal `$`; grouping loop zero-padded the leading group. New impl: groups collected least→most, most-significant group unpadded, negative = `-$`
- [x] Device-verified (emulator-5556): price correct on hero, rail plates populate, no FATAL
- Notes: images in catalog still come from the unverified candidate pool (off-topic/brand text visible on several, e.g. Tissot hero, Apple Watch diver, cat, ocean) — visual re-curation deferred by user; tracked below

## Milestone 7 — Catalog ✅ (2026-10-17)

- [x] Fixed header column: masthead (Nº 02 / The Inventory) + search + chips stay pinned; grid scrolls beneath (imePadding aware)
- [x] Search: BasicTextField over paper, morphing red underline grows on focus (220ms), search icon, clear ×; debounce 260ms before refine
- [x] Filter chips: All + 5 categories, flat 2dp-corner plates; active = ink fill w/ paper text; press-scale 0.97 spring
- [x] Refine pipeline: 240ms skeleton pulse (infinite 0.35→0.85 alpha steel blocks + text bars) via AnimatedContent fade-through 150ms; grid re-entrance 150ms alpha
- [x] Query matches name/house/collection/reference (case-insensitive); combined with the active chip
- [x] Empty state: NO REFERENCES + contextual copy + CLEAR FILTERS (resets query + chip)
- [x] 2-col grid of ProductCards keyed by id; favorites flow -> Room; nav to detail (route id passed — device-verified)
- [x] Device-verified (emulator-5556): default grid, search "chro" -> empty state, "commandeur" -> 1 card, DIVER chip -> 2 divers, card -> The Study stub, no FATAL
- Notes: chip row horizontal-scrolls if clipped (6 chips at 1080w fits ~4.5); red underline is the only red accent on the screen

## Milestone 8 — Product detail ✅ (2026-10-17)

- [x] Top bar: back icon + BurstHeart (spring 1→1.35→1 + red ring pulse fading outward, 420ms)
- [x] Gallery: 1.15:1 card image (Coil crossfade); single URL per product so no swipe dots yet
- [x] Identity block: house·collection·year mono eyebrow, grotesk name, REF mono, PriceXL, body description
- [x] SPECIFICATIONS accordion: chevron spring-rotates 180°, animateContentSize, 6 hairline rows (movement/case/water/dial/strap/year), mono label+value
- [x] AddToCartBar: fixed ink bar `ADD TO CART — $21,450` → morphs to redSoft+red-bordered `✓ ADDED TO CART` (AnimatedContent 160ms fade-through), auto-reverts after 1400ms
- [x] LoadingBlock (steel pulse skeleton) + NOT FOUND state (loaded but unknown id)
- [x] Device-verified: accordion expand/collapse, heart fills red & persists cross-route, add-to-cart morphs and Cart badge pops to "1" on the tab bar, no FATAL
- Notes: DESIGN's red dot FLIES-to-badge — impossible while detail is a detached route (no badge visible); deferred until detail lives inside the shell. Click-morph bug caught on device: handler launched `onAdd` but never set `added` — fixed

## Milestone 9 — Cart + checkout ✅ (2026-10-17)

- [x] Cart rows: 84dp thumb, name/REF/price, SwipeToDismissBox (EndToStart only) with redSoft "✕ RELEASE TO RETURN" track; confirm → Room remove
- [x] Stepper: hairline roundels −/+, springy 0.88 press-scale, mono count; add/decrement through CartRepository (lines pruning at 0)
- [x] Summary: hairline receipt row + count-up total (Animatable → formatUsd, 450ms EaseOutCubic); CTA label mirrors total ("RESERVE THE VAULT — $64,350")
- [x] LazyColumn `animateItem` (spring placement/fade-in) for grid re-layout after dismissal
- [x] Empty state: THE HOLD IS EMPTY + copy
- [x] CheckoutConfirm (new detached route `CheckoutConfirm(totalUsd, lineCount)`): red ring draws with spring overshoot → check path drawn via PathMeasure → order-no ticker reveals "HR-87WTBK"-style code (kotlin.random, unambiguous alphabet); cart.clear() on entry
- [x] RETURN TO THE BOUTIQUE → Home (popUpTo Home), badge cleared
- [x] Device-verified: +/− stepper (3→4→3 with count-up settling $85,800→$64,350), swipe-dismiss → empty state + badge clear, checkout → seal/ticker/total, return → Home badge gone; no FATAL
- Notes: (1) M3 SwipeToDismissBox + per-frame `progress` read in content graphicsLayer froze rows at partial dismissal after the shell's enter transition — removed the per-frame read; plain slide-out reveal is stable. (2) Detached routes have no nav bar, so their bottom CTA sits at y≈2293 while shell screens' CTA sits above the bar (~y≈2130) — coordinate care for device scripting.

## Remaining risks / TODO

- iOS: Xcode project + Info.plist + Assets scaffold not yet written (after Android passes milestones 2-4).
- **M1 crash fixed:** AGP-9 built-in Kotlin does **not** auto-apply the Compose compiler — `androidApp` was missing it, so `MainActivity`'s `setContent` lambda shipped as plain `Function0` while `activity-compose`'s method signature expects the compiler-transformed `Function2` → runtime `NoSuchMethodError`. Fix: `plugins { alias(libs.plugins.composeCompiler) }` in `androidApp/build.gradle.kts`. Verified live: app boots, no FATAL.
- **Emulator testing isolation:** the shared `pixel10` AVD (emulator-5554, "Pulse") is used by another AI agent (com.taxiapp.android) — a separate AVD `horo10` (emulator-5556, same API-37 image, fresh userdata) was handcrafted at `~/.android/avd/horo10.*` for HOROLOGY testing. Use `adb -s emulator-5556`.
- On-device flow verified (2026-10-16, emulator-5556): onboarding CTA → Home; all 4 tabs track the sliding red dot + filled icons; force-stop + relaunch skips onboarding (DataStore gate persists).
- `gradle-9.8.1` cached dist used; wrapper pinned to same version.
- **Catalog image pool re-curation (open):** shipped `catalog.json` URLs contain off-topic / brand-visible photos (Tissot, Rolex, Fossil, IWC, Michael Kors, Cartier box, Apple Watch, ocean, earrings). Only Pexels 190819 (steel chrono) verified product-grade + brand-safe. Needs a fresh visually-verified pool (contact-sheet pass) before detail/gallery milestones ship held-up visuals. Deferred by user 2026-10-17.
