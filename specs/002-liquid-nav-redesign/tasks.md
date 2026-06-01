# Tasks: Liquid Bottom Navigation Redesign

**Input**: Design documents from `/specs/002-liquid-nav-redesign/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/liquid-bottom-bar.md
**Tests**: Skipped per spec §Assumptions (manual smoke only). No test tasks generated.
**Organization**: Tasks grouped by user story; each phase independently testable per spec §User Scenarios.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Parallelizable (different files, no dependencies)
- **[Story]**: US1, US2, US3, US4 from spec.md
- File paths relative to `app/src/main/java/com/financetracker/`

## Path Conventions

Single Android module `:app`. Package root: `com.financetracker`. Build files at repo root.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Add `haze` dependency and prepare build for new component.

- [ ] T001 Add `haze = "1.7.0"` version entry in `gradle/libs.versions.toml`
- [ ] T002 [P] Add `haze = { group = "dev.chrisbanes.haze", name = "haze", version.ref = "haze" }` library alias in `gradle/libs.versions.toml` `[libraries]` section
- [ ] T003 Add `haze` to the `implementation` list in `app/build.gradle.kts` (insert near other UI libs, e.g. after `material3`)

**Checkpoint**: `./gradlew :app:dependencies` shows `haze:1.7.0` resolved. No source changes yet.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core helpers that ALL 4 user stories depend on (scroll bus, opaque background wrapper, shared bus reset hook in nav host).

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [ ] T004 Create `ScrollVisibilityBus` object in `app/src/main/java/com/financetracker/ui/navigation/ScrollVisibilityBus.kt` per `contracts/liquid-bottom-bar.md` (StateFlow `visible`, `onScroll(deltaY)`, `show()`, `reset()`; threshold 12dp down / 0dp up)
- [ ] T005 [P] Create `ScreenBackground` composable in `app/src/main/java/com/financetracker/ui/components/ScreenBackground.kt` per `contracts/liquid-bottom-bar.md` (opaque `surface` background, `BoxScope` content slot)
- [ ] T006 [P] Create `LiquidPill` composable in `app/src/main/java/com/financetracker/ui/navigation/LiquidPill.kt` per `contracts/liquid-bottom-bar.md` (animated `offset` + `width` via `spring(stiffness=380f, dampingRatio=0.8f)`, 40dp tall, 20dp radius, primary @ 25% alpha)
- [ ] T007 [P] Create `LiquidBottomItem` composable in `app/src/main/java/com/financetracker/ui/navigation/LiquidBottomItem.kt` per `contracts/liquid-bottom-bar.md` (icon swap via `Crossfade`, scale `animateFloatAsState` 1.0↔1.18 tween 300ms, 48dp clickable circle)
- [ ] T008 Wire `ScrollVisibilityBus.show()` call in `app/src/main/java/com/financetracker/ui/navigation/AppNavHost.kt` — add the call inside the `onNavigate` lambda passed to `LiquidBottomBar` (and to the existing `BottomNavBar` if still present) so tab change resets visibility (FR-007). Imports: add `com.financetracker.ui.navigation.ScrollVisibilityBus`.

**Checkpoint**: `ScrollVisibilityBus`, `ScreenBackground`, `LiquidPill`, `LiquidBottomItem` compile. `AppNavHost` still uses old `BottomNavBar` (unchanged routing).

---

## Phase 3: User Story 1 - Tap-to-navigate with morphing selection (Priority: P1) 🎯 MVP

**Goal**: Replace `BottomNavBar` with `LiquidBottomBar` so tapping any of the 4 tabs morphs the selection pill between them.

**Independent Test**: Per spec US1 — launch app, tap each of 4 tabs in sequence, confirm pill morphs and content swaps. Same-tap no-op. Mid-morph redirect works.

### Implementation for User Story 1

- [ ] T009 [US1] Create `LiquidBottomBar` composable in `app/src/main/java/com/financetracker/ui/navigation/LiquidBottomBar.kt` per `contracts/liquid-bottom-bar.md` — implements bar layout (Box, width clamp 65% [280dp, 420dp], height 64dp, corner radius 32dp), hosts `LiquidPill` + 4 `LiquidBottomItem`s, **without** haze/scroll-hide/reduced-motion yet (those added in US2/US3). Uses `MaterialTheme.colorScheme.primary.copy(alpha=0.25f)` for pill, plain `surface` fill for now.
- [ ] T010 [US1] Swap `BottomNavBar` → `LiquidBottomBar` in `app/src/main/java/com/financetracker/ui/navigation/AppNavHost.kt` (replace the `bottomBar = { if (showBottomBar) BottomNavBar(...) }` block). Delete `app/src/main/java/com/financetracker/ui/navigation/BottomNavBar.kt`.
- [ ] T011 [US1] Manual smoke: API 34 device/emulator — tap Home/Analytics/Search/Settings in sequence. Verify pill morphs each tap, no snap, same-tab tap is no-op, mid-morph redirect works. Capture as completed in `quickstart.md` F-1/F-2/F-3.

**Checkpoint**: US1 fully functional. App builds, navigates correctly, pill morphs. NO haze, NO scroll-hide yet — those are US2/US3.

---

## Phase 4: User Story 2 - Hide on scroll-down, return on scroll-up (Priority: P2)

**Goal**: Bar slides off-screen on downward scroll past 12dp; returns on any upward scroll; resets to visible on tab change.

**Independent Test**: Per spec US2 — on Home with many transactions, scroll down → bar hides within 250ms; scroll up → returns within 300ms; tab switch restores visibility.

### Implementation for User Story 2

- [ ] T012 [US2] Add `AnimatedVisibility` slide-out wrapper around the `Surface` inside `LiquidBottomBar` (in `app/src/main/java/com/financetracker/ui/navigation/LiquidBottomBar.kt`) driven by `ScrollVisibilityBus.visible` collected via `collectAsState()`. Slide direction: `slideOutVertically { it }` / `slideInVertically { -it }` (down off-screen), `tween(250)`. (FR-006)
- [ ] T013 [US2] Pipe nested-scroll from `HomeScreen` to `ScrollVisibilityBus.onScroll` in `app/src/main/java/com/financetracker/ui/home/HomeScreen.kt` — add `Modifier.nestedScroll(rememberNestedScrollInterop())` if not present, or hook into the existing `LazyColumn` `nestedScroll` connection; call `ScrollVisibilityBus.onScroll(available.y)` in the `onPostScroll` callback. Wrap screen root in `ScreenBackground { ... }` (FR-015).
- [ ] T014 [P] [US2] Pipe nested-scroll from `AnalyticsScreen` in `app/src/main/java/com/financetracker/ui/analytics/AnalyticsScreen.kt` — same pattern as T013. Wrap in `ScreenBackground`.
- [ ] T015 [P] [US2] Pipe nested-scroll from `SearchScreen` in `app/src/main/java/com/financetracker/ui/search/SearchScreen.kt` — same pattern. Wrap in `ScreenBackground`.
- [ ] T016 [P] [US2] Pipe nested-scroll from `SettingsScreen` in `app/src/main/java/com/financetracker/ui/settings/SettingsScreen.kt` — same pattern. Wrap in `ScreenBackground`.
- [ ] T017 [US2] Manual smoke: API 34 — Home screen with 20+ txns, scroll down → bar hides (F-4); scroll up → returns (F-5); while hidden, tap Analytics → bar visible (F-6); repeat on Analytics + Search. Verify E-2 (pull-to-refresh at top doesn't false-hide). Update `quickstart.md` results.

**Checkpoint**: US1 + US2 work. Bar morphs + hides on scroll. Haze still OFF.

---

## Phase 5: User Story 3 - Floating glass-blurred visual treatment (Priority: P2)

**Goal**: Apply `haze` blur behind bar with `surfaceVariant` translucent fill; API 24 fallback to solid translucent tint.

**Independent Test**: Per spec US3 — bar appears as rounded pill with shadow + blur on API 31+; on API 24, solid translucent tint with same shape + shadow, no crash.

### Implementation for User Story 3

- [ ] T018 [US3] Add `HazeState` + `Modifier.hazeSource` integration in `app/src/main/java/com/financetracker/ui/navigation/AppNavHost.kt` — `remember { HazeState() }`, apply `Modifier.hazeSource(hazeState)` to the Scaffold's `container` (wrapping `NavHost` content area). Pass `hazeState` into `LiquidBottomBar` via a new parameter.
- [ ] T019 [US3] Add `Modifier.hazeEffect` to the `Surface` inside `LiquidBottomBar` in `app/src/main/java/com/financetracker/ui/navigation/LiquidBottomBar.kt` — `HazeStyle.Unspecified.copy(tint = HazeTint(color = surfaceVariant.copy(alpha = if (isDark) 0.4f else 0.6f)))`. Keep `shadowElevation = 8.dp`, `tonalElevation = 0.dp`, `shape = RoundedCornerShape(32.dp)`. Change the `Surface`'s `color` parameter to `Color.Transparent` so haze is the fill.
- [ ] T020 [US3] Add `Build.VERSION`-aware fallback in `LiquidBottomBar` — if `Build.VERSION.SDK_INT < 31` (no `RenderEffect`), skip `hazeEffect` and use a solid `surfaceVariant.copy(alpha = ...)` `Surface` color. Same shape + shadow preserved. (FR-008 fallback)
- [ ] T021 [US3] Manual smoke: API 31+ device — bar shows blur (V-2/V-3); API 24 emulator — bar shows solid translucent tint, no crash (V-4, E-3). Update `quickstart.md`.

**Checkpoint**: US1 + US2 + US3 work. Bar morphs, hides on scroll, glass-blurred.

---

## Phase 6: User Story 4 - Depth on selected item (Priority: P3)

**Goal**: Selected icon scales 1.18x; visible depth difference vs unselected.

**Independent Test**: Per spec US4 — selected icon is visibly larger than unselected; no glow effect (per clarification).

### Implementation for User Story 4

- [ ] T022 [US4] Verify `LiquidBottomItem` in `app/src/main/java/com/financetracker/ui/navigation/LiquidBottomItem.kt` implements `animateFloatAsState(targetValue = if (selected) 1.18f else 1.0f, animationSpec = tween(300))` and applies `Modifier.graphicsLayer(scaleX = scale, scaleY = scale)` to the icon. (T007 already scaffolds this — confirm/fix scale value matches FR-016.)
- [ ] T023 [US4] Manual smoke: API 34 — observe all 4 tabs, confirm selected icon noticeably larger (V-7). Cycle through tabs, confirm scale animates. Update `quickstart.md` V-7.

**Checkpoint**: All 4 user stories complete. Bar has full feature set.

---

## Phase 7: Accessibility & Reduced-Motion (Cross-cutting, but in spec FR-010/FR-011/SC-006)

**Purpose**: Honor system reduced-motion; ensure 48dp tap targets + content descriptions are exposed.

- [ ] T024 [P] Add reduced-motion branch in `LiquidBottomBar` in `app/src/main/java/com/financetracker/ui/navigation/LiquidBottomBar.kt` — compute `val reduceMotion = (context.getSystemService(ACCESSIBILITY_SERVICE) as? AccessibilityManager)?.isReduceMotionEnabled == true || Settings.Global.getFloat(contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f` (gate `isReduceMotionEnabled` with `Build.VERSION.SDK_INT >= 33`). When true: pass `tween(0)` to pill + icon scale, AND ignore `ScrollVisibilityBus.visible` (force `visible = true`). (FR-010, R-006)
- [ ] T025 [P] Audit `LiquidBottomItem` 48dp tap target — confirm `Modifier.size(48.dp).clip(CircleShape)` wraps the icon; confirm `contentDescription = item.label` on the inner `Icon` (FR-011, A-1/A-2/A-5). Fix if not.
- [ ] T026 Manual smoke reduced-motion: enable system "Remove animations" — taps are instant, bar stays visible during scroll (A-3); disable — spring + scroll-hide active (A-4). TalkBack swipe announces each tab (A-1). Update `quickstart.md`.

**Checkpoint**: Accessibility + reduced-motion complete.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Cleanup, lint, build verification, manual regression pass.

- [ ] T027 [P] Delete `app/src/main/java/com/financetracker/ui/navigation/BottomNavBar.kt` (already done in T010 — verify gone).
- [ ] T028 [P] Add `// noinspection` or `@Suppress` only if ktlint reports false-positive; otherwise fix actual lint issues.
- [ ] T029 Run `./gradlew ktlintCheck` from repo root; resolve any ktlint violations introduced by this change. (SC-007)
- [ ] T030 Run `./gradlew assembleDebug` from repo root; resolve any build errors. (SC-007)
- [ ] T031 Manual full smoke pass per `quickstart.md` — execute F-1..F-10, V-1..V-7, A-1..A-5, E-1..E-5, P-1..P-3 on API 24 + API 34 emulators. Record pass/fail for each in the `Smoke Pass Criteria` section.
- [ ] T032 [P] Update `AGENTS.md` §13 (`Custom Abstractions / Utils`) — add `LiquidBottomBar`, `LiquidPill`, `LiquidBottomItem`, `ScrollVisibilityBus`, `ScreenBackground` to the list of custom abstractions (one-line each).

**Checkpoint**: All `quickstart.md` criteria pass. Ready to ship.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies. T001/T002 parallel, T003 depends on T001+T002.
- **Foundational (Phase 2)**: T004-T007 depend on Phase 1. T008 depends on T004. T004-T007 are `[P]`.
- **User Story 1 (Phase 3)**: T009-T011 depend on Phase 2. T010 depends on T009. T011 depends on T010.
- **User Story 2 (Phase 4)**: T012-T017 depend on Phase 3 (T009 in particular). T013-T016 are `[P]` (different files). T017 depends on T013-T016.
- **User Story 3 (Phase 5)**: T018-T021 depend on Phase 4. T019 depends on T018. T020 depends on T019. T021 depends on T020.
- **User Story 4 (Phase 6)**: T022-T023 depend on Phase 5. T022 is verification, T023 manual.
- **A11y/Reduced-Motion (Phase 7)**: T024-T026 depend on Phase 5 (touches same files as US3). T024, T025 are `[P]`.
- **Polish (Phase 8)**: T027-T032 depend on all prior phases. T029, T030 are gates (SC-007).

### User Story Dependencies

- **US1 (P1)**: No dependencies on other stories. Core nav replacement.
- **US2 (P2)**: Builds on US1's `LiquidBottomBar` shell. Adds scroll-hide + `AnimatedVisibility` on the same file. **Can be developed sequentially after US1** but is independently testable (per spec US2 independent test).
- **US3 (P2)**: Builds on US1's `LiquidBottomBar` (same file). Adds haze integration. **Touches same file as US2** — must be developed sequentially. Independently testable.
- **US4 (P3)**: Pure polish on `LiquidBottomItem`. The scale logic is already in T007. **Effectively a verification phase**. Independently testable.

### Within Each User Story

- Implementation before manual smoke (T-nn manual tasks come last in each story phase).
- Screens modifications (T013-T016) are parallel — different files.
- `AppNavHost` modifications (T008, T010, T018) are sequential — same file.

### Parallel Opportunities

- Phase 1: T001 + T002 (different sections of same file — actually serial; treat as one logical step)
- Phase 2: T005 + T006 + T007 (all different files, parallel)
- Phase 4: T014 + T015 + T016 (different screen files, parallel)
- Phase 7: T024 + T025 (different files, parallel)
- Phase 8: T027 + T028 + T032 (different files / concerns, parallel after US complete)

---

## Parallel Example: User Story 2 (largest parallel opportunity)

```bash
# After T012 lands in LiquidBottomBar.kt, launch all 4 screen modifications in parallel:
Task: "Pipe nested-scroll in HomeScreen.kt + wrap in ScreenBackground"
Task: "Pipe nested-scroll in AnalyticsScreen.kt + wrap in ScreenBackground"
Task: "Pipe nested-scroll in SearchScreen.kt + wrap in ScreenBackground"
Task: "Pipe nested-scroll in SettingsScreen.kt + wrap in ScreenBackground"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (3 tasks)
2. Complete Phase 2: Foundational (5 tasks)
3. Complete Phase 3: User Story 1 (3 tasks)
4. **STOP and VALIDATE**: Manually test US1 — pill morphs, app navigates. No haze, no scroll-hide yet — those are later.
5. **MVP deliverable**: A working app with a styled floating pill bar, even without blur or scroll-hide. Ships if user is satisfied with core nav redesign alone.

### Incremental Delivery

1. **MVP** (US1) → ship floating pill + morph, no glass, no scroll-hide
2. **+US2** → add hide-on-scroll, all 4 screens get `ScreenBackground` + scroll bus
3. **+US3** → add `haze` glass effect
4. **+US4** → verify scale polish (likely already visible)
5. **+A11y/Reduced-Motion** → ship with accessibility honored
6. **Polish** → lint clean, build clean, full smoke pass

### Why Sequential After Phase 2

US2, US3 all touch `LiquidBottomBar.kt`. Parallel development would cause merge conflicts. Single-developer sequential is the realistic path; parallel team strategy below for completeness.

### Parallel Team Strategy (if multi-dev)

1. Dev A: Phase 1 + Phase 2 (unblocks all)
2. Once Phase 2 done:
   - Dev A: US1 (foundational bar)
   - Dev B: wait — can't start US2/US3 until US1 lands
3. After US1: Dev A: US2 (scroll-hide), Dev B: US3 (haze) — **but** both edit `LiquidBottomBar.kt` → coordinate with file locks or sequential
4. Reality: this is a single-file-intensive change. Solo dev or 2-dev with strict serialization on `LiquidBottomBar.kt` is the practical model.

---

## Notes

- [P] tasks = different files, no dependencies (parallelizable)
- [Story] label = US1/US2/US3/US4 from spec.md
- Each user story independently testable per spec §User Scenarios acceptance scenarios
- No automated tests per spec §Assumptions; manual smoke per `quickstart.md` is the gate
- Commit after each phase (8 commits total) for clean history
- Stop at any phase checkpoint to validate before proceeding
- Avoid: vague tasks (all have file paths + concrete action), same-file conflicts (noted in dependencies), cross-story deps (each story independently completable)
