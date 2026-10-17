# ARCHITECTURE.md — HOROLOGY

## Module graph

```
androidApp (com.android.application, AGP9 built-in Kotlin          iosApp (Xcode, Swift)
  manifest + MainActivity + launcher res)                            │ embed framework
        │ :composeApp                                                │ :composeApp
        ▼                                                            ▼
        └──────────► composeApp (KMP library) ◄──────────────────────┘
                    plugins: kotlin-mpp(2.4.20), android-kmp-library(9.2.0), jetbrains-compose(1.12.2),
                             compose-compiler(2.4.20), serialization(2.4.20), ksp(2.3.12), room(2.8.5)
                    kotlin { androidLibrary { namespace "com.horology.store.shared", compileSdk 37, withHostTest {}
                             iosArm64(), iosSimulatorArm64() → static framework "ComposeApp" }
                    commonTest → kotlin.test (JVM-hosted via testAndroidHostTest)
```

- The Android APK is produced by `:androidApp:assembleDebug`; MainActivity (in androidApp)
  calls `com.horology.store.shared.App()`.
- iOS entry: `MainViewController()` from `composeApp/src/iosMain` → Xcode `iosApp` project.
- No new AGP-legacy patterns: `androidTarget()`/`com.android.library` combos are forbidden
  under AGP 9 (build breaks; see AGENTS.md).

## Source layout (composeApp)

```
commonMain/kotlin/com/horology/store/shared/
  App.kt                 root composable (theme + root nav host + reduce-motion scale)
  motion/                Motion.kt — expect motionDurationScale() (Android/iOS settings)
  theme/                 Swiss palette, Typography from fonts, custom Shapes
  components/            ProductCard, Masthead, CountBadge, FavoriteBadge, etc.
  data/                  models (WatchProduct, CartItemEntity, FavoriteEntity), CatalogRepository,
                         CartRepository, FavoritesRepository, Entities+DAOs, ImageCatalog
  composeResources/      files/catalog.json (13 references), font/, drawable/ (custom icon set)
  ui/onboarding/         3-part parallax onboarding, RailwayDial (Canvas)
  ui/home/               HomeScreen (hero parallax, collections rail, staggered grid)
  ui/catalog/            CatalogScreen (sticky search, chips, debounced refine, skeleton)
  ui/detail/             ProductDetailScreen (gallery, spec accordion, heart burst, add-to-cart morph)
  ui/cart/               CartScreen (swipe-dismiss rows, stepper, count-up total)
  ui/checkout/           CheckoutConfirmScreen (vault seal, order ticker)
  ui/favorites/          FavoritesScreen (notes grid, empty state)
  ui/shell/              ShellScreen + HoroNavBar (detached-tab bar, badge pop)
  navigation/            type-safe routes (kotlinx.serialization) via JetBrains mirror nav
  di/                    AppContainer (manual lazy singletons)
commonTest/kotlin/       FormatUsdTest — regression pins for the USD formatter
```

Screens are **stateless composables** that bind repositories via `produceState` /
`collectAsState` (no ViewModel layer — deliberate: the app is a single offline
flow; state survives tab switches through `rememberSaveable` + nav `restoreState`).

## Key infrastructure decisions

| Topic | Decision | Why |
|---|---|---|
| Navigation | org.jetbrains.androidx.navigation:navigation-compose 2.10.0-rc01 | only KMP-built navigation artifact at 2.x; type-safe serde routes |
| ViewModel | androidx.lifecycle:lifecycle-viewmodel-compose 2.11.0 | multiplatform since 2.8/2.11 (klib verified) |
| Persistence | Room 2.8.5 (cart, favorites); sqlite-bundled 2.7.1 driver; DataStore 1.2.1 (onboarding flag) | KMP-proven (Google sample stack) |
| KSP | 2.3.12 (decoupled versioning) | Room compiler on all 3 targets |
| Images | Coil 3.6.3 + coil-network-ktor3; engines: okhttp (android), darwin (iOS) | one code path both platforms |
| Fonts/compose resources | JetBrains compose resources (fonts downloaded & committed as TTF) | deterministic builds |
| DI | manual `AppContainer` injected via CompositionLocal + ViewModel factory | zero extra deps for a showcase |
| JDK | 21 host, jvmTarget 17 | stable combo |

## Data flow

Screen (stateless composable, tabs wrapped in ShellScreen; detail/checkout detached)
→ `produceState`/`collectAsState` bound directly to repositories →
[Catalog: bundled JSON via compose resources reader; Cart/Fav: Room DAOs (Flow-mounted);
prefs: DataStore] → UI re-renders on Room/flow emissions. No network calls for data
(offline store); images are URL-fetched (Coil) only. Routes: Home/Catalog/Favorites/Cart
(tabs) + ProductDetail(productId) + CheckoutConfirm(totalUsd, lineCount) — order cleared
on confirm entry.

## Milestone risk watch-list

1. material3 alpha ↔ compose stable binary pairing — locked to 1.12.0-alpha03, verified by build.
2. Room KSP on iOS (arm64/sim) — klib compiles clean on Windows (`compileKotlinIosSimulatorArm64`
   green through M10); framework linking stays macOS/CI-only.
3. AGP built-in Kotlin on androidApp — RESOLVED at M1: `alias(libs.plugins.composeCompiler)`
   applied explicitly in androidApp (commit 0ef964a), else untransformed `setContent` lambdas
   crash at runtime.
4. SwipeToDismissBox (m3 alpha03) — never read `state.progress` per-frame inside row content:
   it froze rows at partial dismissal after the shell's enter transition (M9 fix, PLAN.md).
