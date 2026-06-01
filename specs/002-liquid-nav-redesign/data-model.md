# Data Model: Liquid Bottom Navigation Redesign

Phase 1 output for `002-liquid-nav-redesign`. Presentation-only feature — no persistent entities, no schema change (FR-012). Documents transient in-memory structures and re-states the spec's `NavItem` + `ScrollBus` for implementation reference.

## Entities

### NavItem (derived, not stored)

Already defined in `Screen.kt` as `BottomNavItem`. Redefined here per spec §Key Entities for the redesign's perspective.

| Field | Type | Source | Notes |
|-------|------|--------|-------|
| route | String | `Screen.*.route` | Used for selection match |
| label | String | hardcoded in `Screen.kt` | For content-description (a11y) |
| selectedIcon | ImageVector | `Icons.Filled.*` | Shown when active |
| unselectedIcon | ImageVector | `Icons.Outlined.*` | Shown when inactive |
| order | Int | list index in `bottomNavItems` | Implicit; not a field today, not needed for redesign |

**State transitions**: none. The list is static. The active route is a property of `NavController`, not of `NavItem`.

**Validation rules**:
- Exactly 4 items (FR-002). Currently satisfied (`Home`, `Analytics`, `Search`, `Settings`).
- Each item gets a unique route string. Currently satisfied.
- Each item's `selectedIcon` and `unselectedIcon` are distinct (or animation has no visual effect). Currently satisfied — all 4 use filled/outlined pairs.

**No new fields required** for the redesign. The `label` field becomes the `contentDescription` on the `Icon` (already done in current `BottomNavBar.kt:29`).

### ScrollBus (new, in-memory only)

| Field | Type | Source | Notes |
|-------|------|--------|-------|
| accumulatedDownDp | Float (private) | internal accumulator | resets on tab change (FR-007) |
| visible | Boolean | derived from accumulated + direction | drives `AnimatedVisibility` on bar |

**Operations**:
- `fun onScroll(deltaY: Float)` — called from screen-side `nestedScroll` consumers. Updates accumulator; recomputes `visible` per R-003 logic.
- `fun show()` — force `visible = true`, reset accumulator. Called by `AppNavHost` on tab change (FR-007, R-009).
- `fun reset()` — clear accumulator only; used on tab change before `show()`.

**Threading**: `MutableStateFlow` ops must run on the main thread. `onScroll` is invoked from Compose's `nestedScroll` callback (main thread). Safe.

**Lifecycle**: Lives for the lifetime of the process. Not persisted (per spec §Assumptions). On process death, fresh start with `visible = true` (per spec §Assumptions "Hide-on-scroll visibility is transient in-memory only").

**State transitions**:

```
[Visible] --deltaY < 0, any amount--> [Visible]  (always re-show on up-scroll)
[Visible] --deltaY > 0, cumulative > 12dp--> [Hidden]
[Hidden]  --deltaY < 0, any amount--> [Visible]
[Hidden|Visible] --show() called (tab change)--> [Visible, accumulator=0]
```

## Relationships

```
NavController (existing)
    │
    ├── currentRoute ──matches──> NavItem (one of 4)
    │
    └── on navigate() ──calls──> ScrollBus.show()
                                     │
                                     └── accumulates──> ScrollBus.onScroll(deltaY)
                                                              ▲
                                                              │ published by
                                          ScreenRoot.nestedScroll consumer
                                                              │
                                                              └── (HomeScreen, AnalyticsScreen, SearchScreen, SettingsScreen)
```

## Validation Rules (cross-cutting)

- `bottomNavItems.size == 4` (FR-002). Asserted at startup? No — compile-time constant.
- `accumulatedDownDp >= 0f` always. Enforced in `onScroll` via `coerceAtLeast(0f)`.
- `visible` flips to `true` on any up-scroll (R-003 + clarification). Enforced unconditionally before threshold check.

## Migration / Schema Change

**None.** No Room version bump. No `@Entity` changes. `exportSchema = false` remains in `AppDatabase` (AGENTS.md §6).
