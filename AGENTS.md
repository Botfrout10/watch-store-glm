# HOROLOGY — KMP Watch Store (AGENTS.md)

Store-app showcase: Android + iOS from one Kotlin/Compose codebase.
Design language: **Light minimal Swiss** (paper white, ink black, signal red accent).

## Commands (Windows / pwsh)

| Purpose | Command |
|---|---|
| Build Android APK (primary check) | `.\gradlew.bat :androidApp:assembleDebug` |
| Fast compile check, no dex | `.\gradlew.bat :composeApp:compileAndroidMain` |
| Unit tests (once present) | `.\gradlew.bat allTests` |
| Clean | `.\gradlew.bat clean` |

- **Run before every commit.** Never commit red.
- iOS framework compile (`:composeApp:allTests`, `assembleXCFramework`, linkDebugFrameworkIosArm64) requires macOS — on Windows, iosMain must still **compile clean** (`compileKotlinIosSimulatorArm64` works on Windows with native dependencies resolution... only if CI caches KLib failures; rely on code review + flag any Kotlin/Native compiler errors).

## Layout (AGP 9 KMP rules — do NOT retro-fit old plugin styles)

```
composeApp/   KMP library (org.jetbrains.kotlin.multiplatform + com.android.kotlin.multiplatform.library)
              kotlin { androidLibrary { ... } }      <- NOT androidTarget(), NOT top-level android {}
              namespace: com.horology.store.shared
androidApp/   Thin launcher app (com.android.application ONLY — AGP 9 built-in Kotlin, no KGP plugin)
              namespace + applicationId: com.horology.store
              launcher icons / theme / manifest live here
iosApp/       Xcode project consuming the "ComposeApp" framework (from composeApp ios binaries)
```

## Version pins (October 2026) — update only with cause, record in PLAN.md

| Library | Version | Note |
|---|---|---|
| Kotlin | 2.4.20 | CI-tested pair with CMP 1.12.2 (2.4.21 postdates it — resources wiring broke with it) |
| AGP / Gradle | 9.2.0 / 9.8.1 | AGP 9 forbids old `com.android.library` + KGP combo |
| Compose Multiplatform | 1.12.2 | JetBrains plugin `org.jetbrains.compose` |
| Material3 | **1.12.0-alpha03** | M3 tracks alpha train independently of compose — pairing per JetBrains catalog examples |
| Navigation | org.jetbrains.androidx.navigation → 2.10.0-rc01 | iOS support lives on the JetBrains mirror (androidx.navigation-compose has no iOS klib at 2.10) |
| Lifecycle viewmodel-compose | 2.11.0 | androidx artifacts are already multiplatform at 2.11 |
| Room / KSP | 2.8.5 / 2.3.12 | room-gradle-plugin; KSP versions are decoupled from Kotlin since 2.3 |
| DataStore | 1.2.1 | prefs, KMP |
| Coil | 3.6.3 | + coil-network-ktor3; compileSdk must be **37** (AAR metadata) |
| Ktor | 3.6.0 (okhttp android / darwin iOS) | Coil engines |
| sqlite-bundled | 2.7.1 | Room driver for iOS |

## Conventions

- Package root of all shared code: `com.horology.store.shared`
- UI: 100% Compose, fully in `composeApp/src/commonMain` — screens in `ui/`, design tokens in `theme/`
- Custom icon vectors only (xml in `composeResources/drawable`) — no material-icons-extended dependency
- **Compose resources (CMP 1.12 + AGP-9 KMP plugin — learned the hard way):**
  - commonMain needs the direct `org.jetbrains.compose.components:components-resources` dependency or `Res` never generates
  - Generated accessors are top-level extension properties imported by **flat FQN**: `import com.horology.store.shared.ic_heart`, `import com.horology.store.shared.space_grotesk_400` (NOT `…shared.drawable.ic_heart` — that's a phantom package)
  - `org.jetbrains.compose.resources.Font()` is **@Composable** — font families can't be static vals; use `HoroFonts.grotesk()`/`mono()` in composition and named type tokens via `LocalHoroType.current.*`
  - `TextStyle(...)` always with named args (color-first positional order doesn't match)
- Coil 3.6: import is top-level `coil3.compose.AsyncImage` (not `io.coil3…`)
- Product images: **URL-based via Coil** (candidates verified with live HEAD checks before shipping — see `data/ImageCatalog.kt` when built); every URL must return 200 + image/jpeg before being trusted
- Fonts: Space Grotesk (display/UI) + IBM Plex Mono (numerals) inside `composeResources/font`
- Catalog data: bundled JSON in `commonMain/composeResources/files`
- **androidApp composes too:** AGP-9 built-in Kotlin needs `alias(libs.plugins.composeCompiler)` applied explicitly in `androidApp/build.gradle.kts` (auto-apply does not happen; without it `setContent` lambdas ship untransformed and crash at runtime).
- Per-feature commits; update PLAN.md in the same commit as the feature it describes

## Design source of truth

`DESIGN.md` (palette/type/motion) and `PLAN.md` (status). Inventing UI outside those two is a bug.
