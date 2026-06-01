# Research: Liquid Bottom Navigation Redesign

Phase 0 output for `002-liquid-nav-redesign`. Resolves all `NEEDS CLARIFICATION` items and pins implementation choices.

## R-001 — `haze` 1.7.0 API surface

**Decision**: Use `HazeState` + `Modifier.hazeSource()` on Scaffold content + `Modifier.hazeEffect()` on the pill bar `Surface`. Apply `hazeTint` = `MaterialTheme.colorScheme.surfaceVariant` with the alpha values from FR-008.

**Rationale**: `haze` is the canonical Compose multiplatform blur library by Chris Banes. 1.7.0 supports Compose 1.7.x+ (which Compose BOM 2026.02.01 ships). No native code required; works on minSdk 24 via accelerated `RenderEffect` path on API 31+ and falls back to translucent tint on lower APIs — exactly the FR-008 requirement. Apache 2.0 license confirmed acceptable (spec §Assumptions).

**Alternatives considered**:
- Manual `Modifier.blur()` (Compose 1.7+): available but draws inside the composable's bounds; doesn't read content behind it through Scaffold slot. Rejected — wouldn't blur the bar's background.
- `androidx.compose.ui.graphics.BlurEffect`: API 31+ only, no graceful fallback below. Rejected — fails API 24 target.
- Custom `RenderNode` blur: too much native code for a chrome component. Rejected.

**Key API shape** (verified against 1.7.0 docs):
```kotlin
val hazeState = remember { HazeState() }
Box(Modifier.hazeSource(hazeState)) { /* scaffold content */ }
Surface(
    modifier = Modifier.hazeEffect(
        state = hazeState,
        style = HazeStyle.Unspecified.copy(
            tint = HazeTint(color = surfaceVariant.copy(alpha = 0.6f))
        )
    )
) { /* pill bar */ }
```

## R-002 — Spring parameter mapping

**Decision**: `spring(stiffness = 380f, dampingRatio = 0.8f)`.

**Rationale**: Direct match to FR-004. Compose `spring()` accepts `stiffness: Float` (N/m) + `dampingRatio: Float`. 380 N/m + 0.8 ratio = underdamped, settles in ~3 oscillations, reads as "elastic not linear" per spec.

**Alternative considered**: Material3 spec uses `MotionScheme.expressiveSpring` (slow/medium/fast variants). At the time of this writing BOM 2026.02.01 ships M3 stable (no expressive scheme). Rejected for now; can swap later via `LocalMotionScheme` if/when it lands.

## R-003 — Nested-scroll bus pattern

**Decision**: Single process-scoped `object ScrollVisibilityBus { private val _delta = MutableStateFlow(0f); val delta: StateFlow<Float>; fun onScroll(deltaY: Float) { ... } }`. Content screens call `bus.onScroll(delta)` from their existing `nestedScroll` `onPostScroll` consumer (or wrap a `Modifier.nestedScroll(rememberNestedScrollInterop(...))`). Bar reads `bus.delta` to drive `AnimatedVisibility`.

**Rationale**:
- Doesn't double-bind nested scroll — screens keep their own `nestedScroll` connection, they just also publish delta to the bus on the same callback.
- Per-tab state: a `MutableStateFlow<TabVisibility>` keyed by route resets to `Visible` on tab change (FR-007). Cheap to implement, no per-screen state machines.
- Bus lives in `ui/navigation/` (composable-side helper), not DI — it's not a domain abstraction, just a UI event channel. AGENTS.md §13 has precedent for non-injected transient helpers (e.g., `DonutSegment`).

**Alternative considered**:
- Hilt-provided `ScrollBus` singleton: overkill — no other consumer, no test seams needed (no auto tests per spec).
- `CompositionLocal` of `ScrollVisibilityBus`: would force every screen to receive it via provider; global `object` is simpler and matches the "in-memory, not persisted" entity in spec §Key Entities.

**Threshold logic** (per FR-006 + clarification):
```kotlin
fun onScroll(deltaY: Float) {
    _accumulatedDownScroll.update { current ->
        (current + deltaY.coerceAtMost(0f)).coerceAtLeast(0f)
    }
    if (deltaY < 0f) _visible.value = true   // any up = show
    else if (_accumulatedDownScroll.value > 12f) _visible.value = false
}
```

## R-004 — `surfaceVariant` translucency per theme

**Decision**:
- Light: `surfaceVariant.copy(alpha = 0.6f)`
- Dark/OLED: `surfaceVariant.copy(alpha = 0.4f)`

**Rationale**: Matches FR-008 exactly. `surfaceVariant` exists in M3 color scheme out of the box, so zero new color tokens. 60/40 split is standard glassmorphism convention — darker themes need less fill to feel "tinted" because the underlying content is already darker.

**Alternative considered**: Per-accent-color tint (theme accent from `SettingsDataStore`): visually richer, but the spec §Out-of-Scope explicitly excludes "Per-screen theme overrides inside the bar" and the redesign scope is presentation chrome, not personalization. Rejected.

## R-005 — Responsive width clamping (FR-001)

**Decision**: In `LiquidBottomBar`:
```kotlin
val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
val targetWidth = (screenWidthDp * 0.65f).coerceIn(280.dp, 420.dp)
```

**Rationale**: `LocalConfiguration` recomposes on configuration changes (rotation, foldable unfold) automatically. `coerceIn` implements the min/max from the clarification.

**Alternative considered**: `BoxWithConstraints { maxWidth * 0.65f }` — works but forces a subcomposable scope; `LocalConfiguration` is cheaper and idiomatic.

## R-006 — Reduced-motion detection

**Decision**:
```kotlin
val context = LocalContext.current
val reduceMotion = remember(context) {
    (context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager)
        ?.isReduceMotionEnabled == true
}
```
Branch in `LiquidBottomBar`:
- `reduceMotion` → `spring(tween(0))` (instant) + skip scroll-hide entirely (bar always visible).

**Rationale**: FR-010 mandates respecting the system setting. `AccessibilityManager.isReduceMotionEnabled` is the official API, available since API 33 with `AccessibilityManager` query back to API 1 (the manager exists, the property is the only API 33+ addition; use a `Build.VERSION.SDK_INT >= 33` guard for the read, or fall back to `Settings.Global.ANIMATOR_DURATION_SCALE == 0f` for older devices).

**Refinement**: Use dual check — `isReduceMotionEnabled` (API 33+) OR `Settings.Global.getFloat(animatorDurationScale, 1f) == 0f` (all APIs). Covers all minSdk 24 devices.

**Alternative considered**: `LocalAccessibilityManager` from `androidx.compose.ui.platform` — it doesn't currently expose reduced-motion. Rejected.

## R-007 — Tap target sizing

**Decision**: Each `LiquidBottomItem` uses `Modifier.size(48.dp)` minimum clickable area (wrapping the 24dp icon + scale). Fill remaining space equally with `Modifier.weight(1f)` in the `Row`.

**Rationale**: FR-011 = 48dp minimum. Setting the `Box` container to 48dp guarantees the touch target. Icon visual size (24dp scaled to 28.3dp at 1.18x) is decorative; touch padding is independent.

**Alternative considered**: `minimumInteractiveComponentSize()` M3 modifier — works, but `size(48.dp)` is explicit and matches the acceptance criterion directly.

## R-008 — Shadow elevation

**Decision**: `Surface(shadowElevation = 8.dp, tonalElevation = 0.dp, shape = RoundedCornerShape(32.dp))`.

**Rationale**: FR-009. 8dp reads as "floating above content" without overpowering the small bar. `tonalElevation = 0` prevents M3 from auto-tinting the fill (we own the fill via haze).

**Alternative considered**: `Modifier.shadow(8.dp, RoundedCornerShape(32.dp))` on a `Box` — equivalent, but `Surface` gives correct clipping + elevation in one composable. Picked `Surface`.

## R-009 — Reset visibility on tab change (FR-007)

**Decision**: In `AppNavHost`, before calling `navController.navigate(...)`, call `ScrollVisibilityBus.show()`. Imperative reset; bar always appears on destination change regardless of prior scroll state.

**Rationale**: Cleanest place to enforce — `AppNavHost` owns both the bar and the navigate action. No per-screen cooperation needed.

**Alternative considered**: Compose `LaunchedEffect(currentRoute)` inside the bar to reset on route change — works but creates an implicit coupling between the bar and the navigation source. Picked explicit reset in `AppNavHost` for traceability.

## R-010 — Opacity-leak prevention (FR-015)

**Decision**: New `ScreenBackground` composable in `ui/components/`:
```kotlin
@Composable
fun ScreenBackground(content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) { content() }
}
```

**Rationale**: When the bar uses haze (which requires the Scaffold's content to be a `hazeSource`), the Scaffold itself must set a transparent container — otherwise the bar's blur captures Scaffold's own surface. Screen roots need to paint their own opaque background, or content underneath the bar shows through. Edge case 1 in spec.

Wrap all 4 bottom-nav screen roots (`HomeScreen`, `AnalyticsScreen`, `SearchScreen`, `SettingsScreen`) in `ScreenBackground { ... }`.

**Alternative considered**: Set Scaffold `containerColor = Color.Transparent` and rely on per-screen `Modifier.background()` — duplicates the same boilerplate in 4 files. `ScreenBackground` consolidates.

---

## Open architectural questions: NONE

All Technical Context items resolved. No `NEEDS CLARIFICATION` remaining. Ready for Phase 1.
