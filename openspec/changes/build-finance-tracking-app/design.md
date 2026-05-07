## Context

ISpend is a greenfield Android finance tracking app. An HTML design prototype (9 screens) and a detailed Hermes implementation plan exist. The app must be offline-first, private, and fast — all data stays on-device. The existing scaffold has package `com.financetracker` with a basic `MainActivity` using Jetpack Compose and Material 3.

## Goals / Non-Goals

**Goals:**
- Implement all 9 screens matching the HTML prototype's layout, visual design, and interaction patterns
- Offline-first architecture with Room (SQLite) for all user data
- Material 3 theming with Light, Dark, and OLED modes plus user-selectable accent colors
- Custom Canvas-based chart components (donut, bar) — avoid heavy chart library dependencies
- Data export to CSV/JSON for backup and spreadsheet use
- Fast cold launch (< 500 ms to usable UI) on mid-range devices (API 24+)
- MVVM + Repository architecture with Hilt DI

**Non-Goals:**
- Cloud sync or accounts (no network, no auth)
- Income tracking (expenses only for MVP; schema supports future addition)
- Recurring transactions (deferred; forward-compatible schema flag)
- Automated backups (manual export only for MVP)
- Multi-currency support
- Widgets, notifications, or background processing

## Decisions

### Decision 1: Compose Canvas for Charts vs Library (vico / MPAndroidChart)
**Choice**: Custom Compose Canvas implementations for donut chart and bar chart.

**Rationale**: The charts are simple enough (2 chart types, moderate complexity) to implement with Compose Canvas. This avoids adding 1–2 MB of library dependencies, keeping the APK small and giving full control over animation and theming. If complexity grows later, `vico` is the fallback library.

**Alternatives considered**: `vico` — excellent Compose-native library, but adds dependency weight. `MPAndroidChart` — mature but View-based, requires AndroidView interop.

### Decision 2: BigDecimal stored as TEXT in Room
**Choice**: Store monetary amounts as `String` (TEXT column) in Room, mapped to `BigDecimal` in Kotlin via TypeConverter.

**Rationale**: SQLite has no decimal type. Storing as INTEGER (cents) risks overflow on large amounts and loses cent precision readability. TEXT preserves exact precision and is easily human-readable in SQL queries.

**Alternatives considered**: INTEGER (cents) — compact but requires conversion logic everywhere; REAL — floating-point precision errors on monetary values.

### Decision 3: Single-Activity Architecture with Navigation Compose
**Choice**: Single `MainActivity` hosting `NavHost` with Compose Navigation. Bottom nav with 4 top-level destinations (Home, Analytics, Search, Settings). Other screens (History, Categories, Budget, Calendar) navigated via app-bar back button or explicit navigation.

**Rationale**: Standard modern Android pattern. Matches the prototype's navigation model (bottom nav + back-stack sub-screens). Type-safe navigation with Kotlin DSL.

### Decision 4: Hilt for Dependency Injection
**Choice**: Hilt (Dagger-based) for DI.

**Rationale**: Standard Android DI solution, integrates with ViewModel, Room, DataStore out of the box. KSP-based codegen is fast. The app's dependency graph is straightforward.

### Decision 5: DataStore for Preferences
**Choice**: Jetpack DataStore (Preferences) for theme, accent color, and user settings.

**Rationale**: Replaces SharedPreferences with async, type-safe API. Coroutines-native. Small footprint.

### Decision 6: UUID Primary Keys
**Choice**: UUIDs for all entity primary keys (transactions, categories, budgets).

**Rationale**: Avoids auto-increment integer collision risks if data is ever exported/imported. UUIDs are safe for client-generated IDs. Room supports UUID natively.

## Risks / Trade-offs

- **Custom Canvas charts become hard to maintain** → Start simple. If any chart exceeds ~200 LOC or needs advanced features, switch to `vico` library.
- **Room schema changes after release** → Version database from day 1; write migrations for every schema change in `AppDatabase`.
- **Package rename breaks IDE caches** → `./gradlew clean` + Invalidate Caches after rename.
- **Date/time handling across API levels** → Use `java.time` with desugaring (already enabled in the scaffold); keep everything in device local time.
- **APK size** → Avoid unnecessary heavy libraries. Custom charts and emoji-as-text keep the APK lean. Target < 10 MB.
