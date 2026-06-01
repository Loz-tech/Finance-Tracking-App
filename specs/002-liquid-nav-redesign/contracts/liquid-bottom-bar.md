# Contracts: Liquid Bottom Navigation Redesign

Phase 1 output for `002-liquid-nav-redesign`. Single UI contract (no external API). Project is a mobile app — consumers of the bar are internal Compose screens only.

---

## `contracts/liquid-bottom-bar.md`

### Component: `LiquidBottomBar`

Floating pill bottom navigation. Replaces `BottomNavBar` in `AppNavHost`'s `Scaffold.bottomBar` slot.

**Package**: `com.financetracker.ui.navigation`

**Signature**:
```kotlin
@Composable
fun LiquidBottomBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
)
```

**Parameters**:

| Name | Type | Required | Description |
|------|------|----------|-------------|
| `currentRoute` | `String?` | yes | Current `NavController` destination route. Used to determine which item is selected. May be `null` during transitions. |
| `onNavigate` | `(Screen) -> Unit` | yes | Invoked when user taps an item. Caller (`AppNavHost`) handles actual `NavController.navigate(...)` logic and calls `ScrollVisibilityBus.show()` before/after. |
| `modifier` | `Modifier` | no | Applied to the outer `Box` that hosts the floating pill. Use to set `padding` if a screen needs extra inset. |

**Behavior contracts**:

1. **Selection matching**: item is `selected` iff `item.screen.route == currentRoute` (strict equality, not hierarchy).
2. **Pill morph**: when `currentRoute` changes, the pill animates from old item position to new item position via `spring(stiffness = 380f, dampingRatio = 0.8f)`.
3. **Icon swap**: on tab change, outgoing icon fades out + scales down (1.18→1.0), incoming icon fades in + scales up (1.0→1.18), via `tween(300)`.
4. **Tap target**: each item clickable area ≥ 48dp × 48dp.
5. **Size**: 65% of screen width, clamped to `[280dp, 420dp]`. Height 64dp. Corner radius 32dp.
6. **Fill**: `surfaceVariant` @ 60% alpha (light) / 40% alpha (dark), composited on `haze` blur layer.
7. **Shadow**: `Surface.shadowElevation = 8.dp`.
8. **Hide on scroll**: reads `ScrollVisibilityBus.visible`. Animated slide-down off-screen via `AnimatedVisibility(visible = bus.visible)`.
9. **Reduced motion**: when `AccessibilityManager.isReduceMotionEnabled || animatorDurationScale == 0f`, swap spring→tween(0) AND skip scroll-hide (`visible` always true).
10. **Self-reset on tab change**: caller must invoke `ScrollVisibilityBus.show()` in `onNavigate` (or before/after `navigate(...)`). Bar itself does not subscribe to route changes for reset.

**Accessibility contracts**:
- Each `Icon` has `contentDescription = item.label`.
- Tap targets reported as 48dp × 48dp to `AccessibilityNodeProvider`.
- Focus order: left-to-right by `bottomNavItems` order.

**Error contracts**:
- No exceptions thrown for any `currentRoute` value (including `null` and unknown routes). Unknown route → no item selected, no pill visible.

**Performance contracts**:
- Idle: 0 recompositions per second (compose stable).
- During morph: ≤ 1 recomposition per frame.
- During scroll: ≤ 1 recomposition per scroll event (bus updates are throttled by Compose snapshot system).

---

### Component: `LiquidBottomItem`

Single tab item — icon + animated scale. Internal to the bar; not exported for reuse.

**Package**: `com.financetracker.ui.navigation`

**Signature**:
```kotlin
@Composable
internal fun LiquidBottomItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
)
```

**Behavior**:
- Icon swaps between `item.selectedIcon` (when `selected`) and `item.unselectedIcon` (when not).
- Scale: `animateFloatAsState(targetValue = if (selected) 1.18f else 1.0f, animationSpec = tween(300))`.
- Fade cross-dissolve on icon swap via `Crossfade`.
- Min clickable size: `Modifier.size(48.dp).clip(CircleShape)`.

---

### Component: `LiquidPill`

Animated selection pill background. Internal.

**Signature**:
```kotlin
@Composable
internal fun LiquidPill(
    targetIndex: Int,
    itemCount: Int,
    containerWidth: Dp,
    modifier: Modifier = Modifier
)
```

**Behavior**:
- Position: `offset` animates from old item center to new item center via `spring(stiffness = 380f, dampingRatio = 0.8f)`.
- Size: pill width = `containerWidth / itemCount` * 0.7f (centered within item slot), height = 40dp, corner radius = 20dp.
- Color: `MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)`.

---

### Service: `ScrollVisibilityBus`

In-memory scroll-delta accumulator + visibility state.

**Package**: `com.financetracker.ui.navigation`

**Signature**:
```kotlin
object ScrollVisibilityBus {
    val visible: StateFlow<Boolean>
    fun onScroll(deltaY: Float)
    fun show()
    fun reset()
}
```

**Behavior**:
- `onScroll(deltaY)`: if `deltaY < 0` → `visible = true` and reset accumulator. If `deltaY > 0` → add to accumulator; if accumulator > 12f → `visible = false`.
- `show()`: sets `visible = true` and resets accumulator.
- `reset()`: clears accumulator only; does not change visibility.
- Initial state: `visible = true`, accumulator = 0f.

**Threading**: main thread only. Callers from `nestedScroll` callbacks are guaranteed main.

---

### Component: `ScreenBackground`

Opaque background wrapper. Required for all bottom-nav screens (FR-015 + edge case 1).

**Package**: `com.financetracker.ui.components`

**Signature**:
```kotlin
@Composable
fun ScreenBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
)
```

**Behavior**:
- `Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) { content() }`.
- Pure presentation helper. No state, no effects.

---

## Contract Conformance Checklist

- [ ] `LiquidBottomBar` matches signature above.
- [ ] `AppNavHost` invokes `ScrollVisibilityBus.show()` on every `navigate()` call.
- [ ] All 4 bottom-nav screen roots wrap content in `ScreenBackground`.
- [ ] No external API surface added.
- [ ] Public visibility only for `LiquidBottomBar` + `ScreenBackground` + `ScrollVisibilityBus`. `LiquidBottomItem` + `LiquidPill` are `internal`.
