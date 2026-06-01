# Implementation Plan: Liquid Bottom Navigation Redesign

**Branch**: `002-liquid-nav-redesign` | **Date**: 2026-06-01 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/002-liquid-nav-redesign/spec.md`

## Summary

Replace Material3 `NavigationBar` with a custom floating pill bar at 65% screen width, 64dp height, full pill rounding (32dp radius). Selection indicator morphs between items via spring (stiffness≈380, damping ratio≈0.8). Bar hides on 12dp+ downward scroll, reappears on any upward scroll. Glass effect via `dev.chrisbanes.haze:haze:1.7.0` with `surfaceVariant` translucent fallback. Pure presentation-layer change: no domain, data, or DI modifications.

## Technical Context

**Language/Version**: Kotlin 2.2.10 (JVM target 11)
**Primary Dependencies**: Jetpack Compose BOM 2026.02.01, Material3, Navigation Compose 2.9.0, **haze 1.7.0** (new)
**Storage**: N/A (presentation-only)
**Testing**: Manual smoke test on API 24 emulator + API 34 device. No automated tests per AGENTS.md §11 + spec assumption.
**Target Platform**: Android, minSdk 24, compileSdk 36
**Project Type**: Mobile app (single `:app` module)
**Performance Goals**: 60 fps idle (SC-003); ≤500ms morph perceived as instant (SC-001)
**Constraints**: 48dp tap targets (a11y), respect reduced-motion (FR-010), no new persistent state
**Scale/Scope**: 1 chrome component, 1 nav route set unchanged, ~4 files modified + 2 new

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Constitution file (`.specify/memory/constitution.md`) is a placeholder template — no ratified principles exist. Project's authoritative guidance is `AGENTS.md` §1-17.

**Gates derived from AGENTS.md**:

| Gate | Status | Note |
|------|--------|------|
| Architecture: UI = Compose + Material3 | PASS | Custom `Box`/`Surface` Compose, M3 theming |
| Layering: no domain/data touched | PASS | FR-012; only `ui/navigation/` and `ui/components/` |
| DI: no module change | PASS | No new injectable |
| Navigation routes unchanged | PASS | FR-013, FR-014 |
| Naming: `*Screen`/`*ViewModel`/`*UiState` | PASS | Component named `LiquidBottomBar` (descriptive, not a screen) |
| Code style: ktlint passes | PASS | SC-007; will run `ktlintCheck` |
| Build: `./gradlew assembleDebug` clean | PASS | SC-007 |
| Testing scope agreed (no auto tests) | PASS | Per spec §Assumptions |

No violations. No complexity tracking needed.

## Project Structure

### Documentation (this feature)

```text
specs/002-liquid-nav-redesign/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (UI contract only — no external API)
└── tasks.md             # Phase 2 output (NOT created by /speckit.plan)
```

### Source Code (repository root)

Touched/added paths only — single `:app` module, Android:

```text
app/src/main/java/com/financetracker/ui/navigation/
├── AppNavHost.kt        # MODIFIED: drop Material3 NavigationBar slot, host LiquidBottomBar in Scaffold bottomBar
├── BottomNavBar.kt      # DELETED (replaced by LiquidBottomBar)
├── LiquidBottomBar.kt   # NEW: floating pill bar composable
├── LiquidBottomItem.kt  # NEW: single-item composable (icon + scale animation)
├── LiquidPill.kt        # NEW: animated selection pill (spring morph)
├── ScrollVisibilityBus.kt  # NEW: in-memory scroll-delta Flow shared with content (ScrollBus entity)
└── Screen.kt            # UNCHANGED (routes preserved per FR-014)

app/src/main/java/com/financetracker/ui/components/
└── ScreenBackground.kt  # NEW: opaque background helper for screen roots (FR-015) — single composable reused by all bottom-nav screens

app/src/main/java/com/financetracker/ui/home/HomeScreen.kt        # MODIFIED: pipe scroll to ScrollVisibilityBus, wrap in ScreenBackground
app/src/main/java/com/financetracker/ui/analytics/AnalyticsScreen.kt # MODIFIED: same
app/src/main/java/com/financetracker/ui/search/SearchScreen.kt    # MODIFIED: same
app/src/main/java/com/financetracker/ui/settings/SettingsScreen.kt # MODIFIED: same

app/build.gradle.kts     # MODIFIED: add `dev.chrisbanes.haze:haze:1.7.0` dependency via libs.versions.toml
gradle/libs.versions.toml # MODIFIED: add `haze = "1.7.0"` version + `haze` library alias
```

**Structure Decision**: Single-module app, no feature-module split. All new code lives under existing `ui/navigation/` + `ui/components/` packages. No domain or data layer touched.

## Complexity Tracking

No violations. Table omitted.

---

## Phase 0: Research (see research.md)

Topics resolved:
1. `haze` 1.7.0 API surface (HazeState, `Modifier.hazeSource`, `hazeChild` style overlay) + Compose BOM 2026.02.01 compatibility
2. Spring animation params mapping (Material3 spec stiffness 380 / dampingRatio 0.8 → Compose `spring()` literals)
3. Nested-scroll bus pattern for sharing scroll delta across Scaffold slots without breaking existing nestedScroll consumers
4. `surfaceVariant` translucency math for Light/Dark per FR-008
5. `LocalConfiguration.current.screenWidthDp` for responsive width clamping (280dp–420dp) per FR-001
6. Reduced-motion detection via `LocalAccessibilityManager.current` / `AccessibilityManager.isReduceMotionEnabled` → branch in `LiquidBottomBar` to skip springs + scroll-hide

## Phase 1: Design (see data-model.md, quickstart.md)

- **Data model**: 2 transient in-memory entities (`ScrollBus`, `NavItem` already defined in spec). No persistence.
- **Contracts**: UI component contract only — `LiquidBottomBar` props + `ScrollVisibilityBus` interface. Written to `contracts/liquid-bottom-bar.md`. No external API.
- **Quickstart**: Manual validation checklist (API 24 + API 34, reduced-motion on/off, scroll-up restore, tab-switch reset, blur fallback, accessibility audit).

## Phase 2: Tasks (NOT generated by /speckit.plan — produced by `/speckit.tasks`)

Planned task buckets (preview only):
1. Catalog: add `haze` version + library alias
2. Build: wire `haze` dependency in `app/build.gradle.kts`
3. UI: `ScrollVisibilityBus` (in-memory `MutableStateFlow<Float>`)
4. UI: `LiquidPill` (spring-animated `offset`/`width` `Animatable`)
5. UI: `LiquidBottomItem` (icon + scale `animateFloatAsState` 1.0→1.18)
6. UI: `LiquidBottomBar` (composes pill + items, `haze` overlay, scroll-hide `AnimatedVisibility`, reduced-motion branch)
7. UI: `ScreenBackground` opaque wrapper
8. Nav: `AppNavHost` swap `BottomNavBar` → `LiquidBottomBar`; reset visibility on tab change (FR-007)
9. Screens: Home/Analytics/Search/Settings pipe nested-scroll to bus + wrap in `ScreenBackground`
10. Cleanup: delete `BottomNavBar.kt`
11. Verify: `./gradlew ktlintCheck assembleDebug`
