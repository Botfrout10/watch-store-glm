# PLAN.md — HOROLOGY watch store

Store-app showcase built with Kotlin KMP + Compose Multiplatform, modern "Light minimal Swiss" design.
Data: bundled JSON catalog + verified image URLs. Full shopping flow, offline-friendly.
Platforms: Android (buildable on this machine) + iOS (scaffold only — Xcode/macOS required).

## Milestones

| # | Milestone | Status |
|---|---|---|
| 1 | Scaffold: git, KMP modules, AGP9-KMP plugin setup, wrapper, docs, green `:androidApp:assembleDebug` | ✅ done |
| 2 | Design system: Swiss theme (palette/type/grid), tokens, custom icon vectors, core components | in progress |
| 3 | Data layer: models, catalog JSON + repository, Room cart/favorites, DataStore onboarding flag | pending |
| 4 | Navigation shell + bottom bar + screen transitions | pending |
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

## Remaining risks / TODO

- iOS: Xcode project + Info.plist + Assets scaffold not yet written (after Android passes milestones 2-4).
- Compose compiler on `androidApp` is provided by AGP 9 built-in Kotlin — verify explicitly if any composable lambda errors appear in androidApp later.
- `gradle-9.8.1` cached dist used; wrapper pinned to same version.
