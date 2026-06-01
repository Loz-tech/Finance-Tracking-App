# Quickstart: Liquid Bottom Navigation Redesign

Phase 1 output for `002-liquid-nav-redesign`. Manual validation checklist per spec §Assumptions ("No automated tests added... Manual smoke test on API 24 emulator and API 34 device validates the change.").

## Pre-conditions

- Branch: `002-liquid-nav-redesign`
- Working tree clean (or all changes staged for the redesign only)
- `./gradlew assembleDebug` succeeds before manual test
- `./gradlew ktlintCheck` passes before manual test

## Build Verification

```bash
./gradlew ktlintCheck
./gradlew assembleDebug
```

Both must exit 0 with no new warnings introduced by this change (SC-007).

## Devices

| Device | API | Purpose |
|--------|-----|---------|
| Pixel 6 emulator | API 34 | Modern path: blur on, spring animation, scroll-hide |
| Pixel 3a emulator | API 24 | Fallback path: blur off (translucent tint), no crash |
| Foldable emulator (Galaxy Z Fold) | API 34 | Sanity-check portrait + landscape width clamp (420dp max) |

## Manual Test Matrix

### Functional

| # | Test | Steps | Expected | Spec ref |
|---|------|-------|----------|----------|
| F-1 | Tap navigation | Tap each of 4 tabs in sequence (Home → Analytics → Search → Settings → Home) | Pill morphs to tapped tab; content swaps; no snap | FR-001, FR-003, FR-004 |
| F-2 | Tap current tab | On Home, tap Home again | No state change, no visual glitch | US1 AS-2 |
| F-3 | Tab mid-morph | Start morph Home→Analytics, tap Search during the animation | Pill redirects to Search without snap/overlap | US1 AS-3 |
| F-4 | Scroll down to hide | On Home with 20+ transactions, scroll down past 12dp cumulative | Bar slides down off-screen within 250ms | US2 AS-1, FR-006 |
| F-5 | Scroll up to show | After F-4, scroll up by any amount | Bar returns within 300ms | US2 AS-2, SC-002 |
| F-6 | Tab switch resets hide | After F-4 (bar hidden), tap Analytics | Bar returns to visible immediately | US2 AS-3, FR-007 |
| F-7 | Floating visual | Observe bar on Home | Rounded pill, visible shadow, content behind blurred (API 31+) | US3, FR-008, FR-009 |
| F-8 | Selected icon depth | Compare selected vs unselected icon size on Settings | Selected is visibly larger (1.18x) | US4, FR-016 |
| F-9 | Width clamp (small) | Run on 320dp-wide device or emulator | Bar width = 65% × 320dp ≈ 208dp → coerced to min 280dp | FR-001 |
| F-10 | Width clamp (large) | Run on 800dp-wide foldable in landscape | Bar width = 65% × 800dp = 520dp → coerced to max 420dp | FR-001 |

### Visual

| # | Test | Expected |
|---|------|----------|
| V-1 | Bar shape | Pill with 32dp corner radius, 64dp height |
| V-2 | Bar fill (light theme) | `surfaceVariant` @ 60% alpha + content behind visibly blurred |
| V-3 | Bar fill (dark theme) | `surfaceVariant` @ 40% alpha + content behind visibly blurred |
| V-4 | Bar fill (API 24 fallback) | Same rounded shape + shadow, solid translucent tint, NO crash |
| V-5 | Shadow | Visible drop shadow under bar, 8dp elevation feel |
| V-6 | Pill indicator | Smaller pill (40dp tall, 70% item width) inside each item, primary @ 25% alpha |
| V-7 | Icon scale | Selected = 1.18x unselected; smooth tween 300ms |

### Accessibility

| # | Test | Expected | Spec ref |
|---|------|----------|----------|
| A-1 | TalkBack focus | Swipe through 4 items; each announces its label | FR-011 |
| A-2 | Tap target | Each item accepts tap across full 48dp circle, not just the 24dp icon | FR-011 |
| A-3 | Reduced-motion on | Enable system "Remove animations"; tap tab → instant switch, no spring; scroll → bar stays visible | FR-010 |
| A-4 | Reduced-motion off | Disable setting; spring + scroll-hide active | FR-010 |
| A-5 | Content description | `adb shell uiautomator dump` shows `content-desc="Home"` etc. on each item | SC-006 |

### Edge Cases

| # | Test | Expected | Spec ref |
|---|------|----------|----------|
| E-1 | Nested scroll conflict | On a screen that uses `nestedScroll` for its own collapsing header, bar scroll-hide works in parallel; no double-binding crash | EC §Edge Cases |
| E-2 | Pull-to-refresh / overscroll | At top of Home list, pull down → bar stays visible (no false hide from overscroll) | EC §Edge Cases |
| E-3 | API 24 crash check | Launch on API 24 emulator; no crash, translucent tint shows | EC §Edge Cases, FR-008 |
| E-4 | Tablet | Run on 10" tablet; bar remains bottom-anchored (no rail); width clamped to 420dp | EC §Edge Cases |
| E-5 | Background leak | On each bottom-nav screen, scroll content under bar position → bar background stays opaque/translucent, does NOT show content through | FR-015 |

### Performance

| # | Test | Expected | Spec ref |
|---|------|----------|----------|
| P-1 | Idle fps | Open Profiler; on Home idle, GPU/CPU usage flat, 60fps sustained | SC-003 |
| P-2 | Morph fps | During F-1 tap sequence, frame time stays under 16ms (60fps) | SC-003 |
| P-3 | Scroll-hide fps | During F-4, no jank > 16ms while bar slides | SC-003 |

## Smoke Pass Criteria

Feature ships if:
- All F-* tests pass
- All A-* tests pass
- V-1 through V-7 visual checks pass
- At least F-1, F-4, F-5, F-6, E-3 verified on API 24
- No new ktlint or build errors (SC-007)

## Out of Scope (skip)

- 5th tab (spec §Out of Scope)
- Navigation rail mode (spec §Out of Scope, EC §Edge Cases)
- Animated icons (spec §Out of Scope)
- Persistence of last-selected tab (spec §Out of Scope)
- Automated unit/UI tests (spec §Assumptions)

## Rollback

If any critical test fails:
1. `git revert <redesign-commit-sha>`
2. Verify `BottomNavBar.kt` restored
3. `git push` (CI will confirm no broken state)
4. File issue with failing test ID + device + repro steps
