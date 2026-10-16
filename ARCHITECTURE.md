# ARCHITECTURE.md — HOROLOGY

## Module graph

```
androidApp (com.android.application, AGP9 built-in Kotlin          iosApp (Xcode, Swift)
  manifest + MainActivity + launcher res)                            │ embed framework
        │ :composeApp                                                │ :composeApp
        ▼                                                            ▼
        └──────────► composeApp (KMP library) ◄──────────────────────┘
                    plugins: kotlin-mpp, android-kmp-library(9.2.0), jetbrains-compose(1.12.2),
                             compose-compiler(2.4.21), serialization(2.4.21), ksp(2.3.12), room(2.8.5)
                    kotlin { androidLibrary { namespace "com.horology.store.shared", compileSdk 37 }
                             iosArm64(), iosSimulatorArm64() → static framework "ComposeApp" }
```

- The Android APK is produced by `:androidApp:assembleDebug`; MainActivity (in androidApp)
  calls `com.horology.store.shared.App()`.
- iOS entry: `MainViewController()` from `composeApp/src/iosMain` → Xcode `iosApp` project.
- No new AGP-legacy patterns: `androidTarget()`/`com.android.library` combos are forbidden
  under AGP 9 (build breaks; see AGENTS.md).

## Source layout (composeApp)

```
commonMain/kotlin/com/horology/store/shared/
  App.kt                 root composable (theme + root nav host)
  theme/                 Swiss palette, Typography from fonts, custom Shapes
  components/            ProductCard, RailHeader, Chips, Buttons, FieldLine, NavScaffold, Icons
  data/                  models (WatchProduct, CartItem, Favorite), CatalogRepository,
                         CartRepository, FavoritesRepository, OnboardingPrefs, mappers
  data/catalog.json      bundled in composeResources/files (~24-30 products)
  ui/onboarding/         3-part parallax onboarding, SwissRailDial (Canvas)
  ui/home/               HomeScreen + HomeViewModel
  ui/catalog/            CatalogScreen (search/filters) + ViewModel
  ui/detail/             ProductDetailScreen + ViewModel
  ui/cart/, ui/favorites/, ui/checkout/
  navigation/            type-safe routes (kotlinx.serialization) via JetBrains mirror nav
  di/                    AppContainer (manual lazy singletons)
```

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

Screen (stateless Composable) → ViewModel (kotlinx/StateFlow) → Repository →
[Catalog: bundled JSON via compose resources reader; Cart/Fav: Room DAOs;
prefs: DataStore] → Mappers → UI re-renders. No network calls for data (offline store);
images are URL-fetched (Coil) only.

## Milestone risk watch-list

1. material3 alpha ↔ compose stable binary pairing — locked to 1.12.0-alpha03, verified by build.
2. Room KSP on iOS (arm64/sim) — KSP tasks wired; verify at data-layer milestone via
   `:composeApp:compileKotlinIosArm64` on macOS/CI only (Windows can't link frameworks).
3. AGP built-in Kotlin on androidApp — if composable-lambda compile errors appear, consult
   `developer.android.com/build/migrate-to-built-in-kotlin` (compose compiler auto-capable).
