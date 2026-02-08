# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Android
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug

# Desktop (JVM)
./gradlew :composeApp:run

# Web (WASM - modern browsers)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Web (JS - older browsers)
./gradlew :composeApp:jsBrowserDevelopmentRun

# Server
./gradlew :server:run

# iOS - open iosApp/ in Xcode
```

## Testing

```bash
# All tests
./gradlew test

# Specific module tests
./gradlew :auth-impl:test
./gradlew :auth-impl:iosSimulatorArm64Test

# Code coverage
./gradlew koverReport
```

## Architecture Overview

**Kotlin Multiplatform** project targeting Android, iOS, Web (JS/WASM), Desktop (JVM), and Server.

### Module Structure

```
composeApp/          # Main multiplatform Compose app entry point
server/              # Ktor server (JVM)
shared/              # Code shared across all platforms
auth-api/            # Public interfaces for auth feature
auth-impl/           # Auth feature implementation
core-architecture/   # TEA (The Elm Architecture) framework
core-navigation/     # Decompose navigation base classes
uikit/               # Shared UI components and theme
build-logic/         # Gradle convention plugins
```

### TEA (The Elm Architecture) Pattern

All screens follow TEA pattern from `core-architecture`:

```
User Action → Intent → Reducer → State
                         ↓
                      Effect → Effector → Event (Navigation)
```

**Key components:**
- `Tea<State, Intent, Event>` - main store interface with `state: StateFlow`, `events: Flow`, `accept(intent)`
- `Reducer<State, Intent, Effect>` - pure function transforming state
- `DslReducer` - DSL-style reducer with `state { copy(...) }` and `effects(...)`
- `CoroutineEffector<Effect, Intent, Event>` - handles side effects with `dispatch(intent)` and `publish(event)`
- `BaseCoroutineUseCase<I, O>` - base class for domain use cases

### Feature Module Structure (auth-impl example)

```
presentation/
  └── screen_name/
      ├── component/ScreenComponentImpl.kt  # Decompose component
      ├── component/ScreenScreen.kt          # Compose UI
      ├── mvi/ScreenTea.kt                   # State, Intent, Effect definitions
      ├── mvi/ScreenStoreFactory.kt          # TEA store creation
      └── mapping/StateMapping.kt            # State → UiState
domain/
  ├── model/                                 # Domain DTOs
  ├── usecase/                               # Abstract + Impl use cases
  └── repository/                            # Repository interface
data/
  ├── api/                                   # DataSource interface + impl
  ├── repository/                            # Repository implementation
  └── model/                                 # Request/Response DTOs + mapping
di/
  └── FeatureGraph.kt                        # Metro DI configuration
```

### DI Framework

Uses **Metro** (dev.zacsweers.metro) for dependency injection:
- `@Inject` for constructors
- `@AssistedInject` / `@AssistedFactory` for components with runtime parameters
- `@DependencyGraph` for module configuration
- `@Binds` for interface-to-implementation bindings

### Navigation

Uses **Decompose** for navigation:
- `StackNavigation<Config>` for screen stack
- `childStack()` for creating navigation stack
- `navigation.push()` / `navigation.pop()` for navigation

## Key Technologies

| Component | Version |
|-----------|---------|
| Kotlin | 2.2.20 |
| Compose Multiplatform | 1.9.1 |
| Decompose | 3.4.0 |
| Ktor | 3.3.1 |
| Metro DI | 0.7.3 |
| Kotlinx Coroutines | 1.10.2 |

## Testing Patterns

- Use `FakeRepository` / `FakeDataSource` for mocking
- Use `runTest` for coroutine tests
- Test file naming: `*Test.kt` in `src/commonTest/`

## Code Coverage (Kover)

Excludes from coverage:
- `*Graph*`, `*Factory*`, `*Component*Impl*Factory*` classes
- `*.di`, `*.generated` packages
