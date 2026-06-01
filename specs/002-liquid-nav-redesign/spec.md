# Feature Specification: Liquid Bottom Navigation Redesign

**Feature Branch**: `002-liquid-nav-redesign`

**Created**: 2026-06-01

## Clarifications

### Session 2026-06-01
- Q: Bar width sizing → A: 65% of screen width, centered (min 280dp, max 420dp)
- Q: Hide-on-scroll threshold value → A: 12dp down / 0dp up
- Q: Selected item depth treatment → A: Icon scale only, 1.0x → 1.18x
- Q: Pill/background color source → A: surfaceVariant @ 60% (light) / 40% (dark) alpha, on haze blur
- Q: Pill shape geometry → A: Height 64dp, corner radius 32dp (full pill)

**Status**: Draft

**Input**: User description: "remake bottom nav: floating icons only (no bar bg), depth + shadows, hide-on-scroll, round icons, liquid morph between items."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Tap-to-navigate with morphing selection (Priority: P1)

User on any primary tab (Home, Analytics, Search, Settings) taps a different tab icon in the bottom navigation. Selected icon receives an animated pill background that morphs from the previous tab's icon to the new tab's icon with a smooth spring transition. The active icon and pill visibly grow/shift in lockstep. Tap target covers the entire circular icon area plus a small padding ring.

**Why this priority**: Core navigation function. Without working tap + visible selection, feature has no value.

**Independent Test**: Launch app, tap each of the 4 tabs in sequence, confirm icon morphs between them and content area updates.

**Acceptance Scenarios**:

1. **Given** user is on Home tab, **When** they tap Analytics icon, **Then** selected pill animates from under Home icon to under Analytics icon, content swaps to Analytics screen.
2. **Given** user is on any tab, **When** they tap the currently selected tab, **Then** no state change occurs and no visual glitch (pill stays put).
3. **Given** bar is mid-morph, **When** user taps a third tab, **Then** morph redirects to new target without snap or overlap.

---

### User Story 2 - Hide on scroll-down, return on scroll-up (Priority: P2)

User scrolls a long screen (Home transaction list, Analytics, Search results). Bottom navigation slides down off the visible area as user scrolls down past a threshold, freeing content space. As soon as user scrolls up (even if overall position is still net-down), bar slides back into view.

**Why this priority**: Improves content immersion. App still functional without it (bar always visible works), but users expect modern hide-on-scroll behavior.

**Independent Test**: On Home tab with many transactions, scroll down — bar hides. Scroll up even slightly — bar returns. Repeat on Analytics and Search.

**Acceptance Scenarios**:

1. **Given** bar visible at bottom, **When** user scrolls down by more than threshold, **Then** bar slides down off-screen within 250ms.
2. **Given** bar hidden, **When** user scrolls up by any amount, **Then** bar slides back into view.
3. **Given** bar hidden via down-scroll, **When** user navigates to a different tab, **Then** bar returns to visible state on the new tab (no carry-over hide).

---

### User Story 3 - Floating glass-blurred visual treatment (Priority: P2)

User sees the bottom navigation as a floating pill bar (not edge-to-edge) with rounded corners, visible elevation/shadow, and content behind it shows through with a blur effect on supported devices. Bar does not span full screen width; content sits on either side.

**Why this priority**: Differentiator vs. default Material 3 nav bar. Without it, redesign is just a color swap.

**Independent Test**: Visually compare default and redesigned bar on device. Bar should appear to float with shadow underneath and have noticeable rounded pill shape.

**Acceptance Scenarios**:

1. **Given** app running on device with blur support, **When** user views bottom nav, **Then** bar appears as rounded pill with shadow and content behind it is blurred.
2. **Given** app running on device without blur support (API <31), **When** user views bottom nav, **Then** bar shows solid translucent tint with same rounded shape and shadow.

---

### User Story 4 - Depth on selected item (Priority: P3)

User sees the selected tab icon visually elevated via icon scale or glow accent under the pill, making the active state unmistakable at a glance.

**Why this priority**: Polish layer. App functional without it.

**Independent Test**: Compare selected vs unselected icon sizes/glow visually.

**Acceptance Scenarios**:

1. **Given** one tab is selected, **When** user observes, **Then** selected icon is visibly larger or has a glow effect distinguishing it from unselected icons.

---

### Edge Cases

- **Bottom-nav screens lose background fill**: When scaffold background is set to transparent (to let blur show through), each screen root must provide its own opaque background. Otherwise content underneath the bar shows through even without blur.
- **Nested scroll conflicts**: If a screen already uses `nestedScroll` (e.g., for collapsing toolbar), the bar's scroll-hide must not break that screen's scroll behavior. Scroll events shared via a single bus, not double-bound.
- **Pull-to-refresh interference**: Hide-on-scroll must not trigger on overscroll/elastic bounce at the top of a scrollable list. The 12dp downward threshold (FR-006) prevents accidental hide.
- **Tab switch mid-scroll animation**: If user changes tabs while bar is sliding off-screen, bar must reset to visible state on the new tab.
- **Tablet/foldables**: Layout assumes bottom-anchored bar; spec does not require navigation rail adaptation (out of scope).
- **Accessibility**: Tap targets must be ≥ 48dp. Reduced-motion users should get instant transitions, not springs.
- **API 24 fallback**: Blur fallback path must not crash on devices missing required graphics APIs.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST render bottom navigation as a floating rounded pill bar centered horizontally, not edge-to-edge, sized at 65% of the screen width with a minimum of 280dp and a maximum of 420dp.
- **FR-002**: System MUST contain exactly 4 navigation items (Home, Analytics, Search, Settings) as round icon buttons.
- **FR-003**: System MUST display a pill-shaped selection indicator under the active item that morphs position and width when the user changes tabs.
- **FR-004**: System MUST animate the pill between positions using a spring (stiffness ≈ 380, damping ratio ≈ 0.8) so the motion reads as elastic, not linear.
- **FR-005**: System MUST animate the icon swap on tab change via scale+fade (≈ 300ms tween).
- **FR-006**: System MUST hide the bar on downward scroll past a threshold of 12dp cumulative delta and show it on upward scroll by any amount (0dp threshold).
- **FR-007**: System MUST keep the bar visible on tab change regardless of previous hide state.
- **FR-008**: System MUST apply a blur effect to the area behind the bar on devices that support it, with a solid translucent fallback on devices that do not. Bar fill MUST use `surfaceVariant` color at 60% alpha in light theme and 40% alpha in dark theme, composited on top of the `haze` blur layer.
- **FR-009**: System MUST render a visible shadow under the bar so it reads as elevated above content.
- **FR-010**: System MUST respect system reduced-motion preferences by skipping spring/scroll-hide animations for users who enable them.
- **FR-011**: System MUST provide tap targets of at least 48dp for each navigation item to remain accessibility-compliant.
- **FR-012**: System MUST NOT require any data-layer, schema, or preference changes — this is a pure presentation-layer change.
- **FR-013**: System MUST continue routing to the same 4 destinations (Home, Analytics, Search, Settings) the current bar handles.
- **FR-014**: System MUST NOT change the routes for the existing 4 tabs.
- **FR-015**: System MUST prevent the floating bar from leaking the scaffold's transparent background through to show content under the bar.
- **FR-016**: System MUST scale the selected icon from 1.0x to 1.18x relative to unselected icons, with no additional glow effect.
- **FR-017**: System MUST render the floating bar at 64dp height with a fully rounded pill shape (corner radius = 32dp).

### Key Entities *(include if feature involves data)*

- **NavItem**: Represents a single bottom-nav destination. Attributes: route identifier, icon resource, label, display order. No persistent storage needed — derived from current navigation graph.
- **ScrollBus**: Transient in-memory value (current vertical scroll delta) shared between content area and bar to drive hide-on-scroll visibility. Not persisted; resets on process death.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can tap any of the 4 nav items and see the selection indicator morph to that item within 500ms (perceived as instant).
- **SC-002**: Bar remains hidden during continuous downward scroll and reappears within 300ms when user begins scrolling upward.
- **SC-003**: Bar renders at 60 fps during idle state on a mid-range device (API 26+, 4GB RAM).
- **SC-004**: Visual treatment (floating pill + shadow + blur) is present on 100% of supported devices, with documented fallback on unsupported devices (no missing assets, no broken layout).
- **SC-005**: All existing navigation destinations remain reachable via the redesigned bar with no regression in routing logic.
- **SC-006**: Accessibility services can identify and activate each nav item (≥ 48dp target, content-description present).
- **SC-007**: Static analysis (`./gradlew ktlintCheck`) and debug build (`./gradlew assembleDebug`) pass with zero new errors or warnings introduced by this change.

## Assumptions

- **Existing navigation graph unchanged**: The 4 tabs (Home, Analytics, Search, Settings) keep their current routes and screen content. This is a chrome replacement, not a feature expansion.
- **No new destinations added**: Sub-screens (History, Categories, Budget, Calendar) and overlay (AddTransaction) remain reachable via their existing top-bar / FAB / list interactions. They do not appear in the bottom bar.
- **Widget screen unaffected**: The `QuickAddTransactionActivity` widget surface does not use the bottom bar; this redesign does not change it.
- **No data-layer changes**: No Room schema change, no DataStore preference change, no repository change, no Hilt module change.
- **No new persistence**: Hide-on-scroll visibility is transient in-memory only; not persisted across process death.
- **Blur fallback acceptable**: Devices that cannot blur (API <31) receive a solid translucent tint. This is the standard industry pattern and does not require user choice.
- **Reduced-motion default**: We default to respecting system reduced-motion setting; user is not offered a per-app toggle in this spec.
- **Tablet / foldable layout**: Not in scope. Bottom-anchored pill remains the only layout. A future navigation-rail mode would be a separate feature.
- **Testing scope**: No automated tests added for this UI-only change, per existing project convention (AGENTS.md §11) and confirmed by the user. Manual smoke test on API 24 emulator and API 34 device validates the change.
- **Third-party dependency**: The redesign adds `dev.chrisbanes.haze:haze:1.7.0` (Apache 2.0) to enable the glass blur effect. Confirmed acceptable by the user.

## Out of Scope

- Adding a 5th navigation item
- Changing tab order or labels
- Adding badges or notification counts on icons
- Per-screen theme overrides inside the bar
- Animated icons or custom icon set
- Navigation rail mode for tablets
- Persisting last-selected tab across app launches (current behavior preserved)
- Dark / light / OLED theme adjustments to the bar beyond Material3 defaults
