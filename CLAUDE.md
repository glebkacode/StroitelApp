# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Android
./gradlew :composeApp:assembleDebug

# Desktop (JVM)
./gradlew :composeApp:run

# Server (Ktor, port 8080)
./gradlew :server:run

# Web (Wasm - modern browsers)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Web (JS - older browsers)
./gradlew :composeApp:jsBrowserDevelopmentRun

# iOS - open iosApp/ in Xcode
```

## Architecture Overview

**Kotlin Multiplatform** project (Android, iOS, Web/Wasm, Desktop/JVM, Server) using **Compose Multiplatform** for shared UI.

### Module Structure

Modules follow an **API/Implementation split pattern**:
- `feature-api`: Public interfaces (e.g., `PhoneValidationComponent`)
- `feature-impl`: Concrete implementations with layered architecture (presentation → domain → data)

Core modules:
- `core-architecture`: Custom TEA state management
- `core-navigation`: Component system built on Decompose
- `uikit`: Shared UI components and theming

### TEA (The Elm Architecture)

Custom state management in `core-architecture/src/commonMain/kotlin/com/itapp/core_architecture/tea/`:

```
Intent → Reducer → State + Effects
                        ↓
              Effector → Intent/Event
```

Key classes:
- `Tea<State, Intent, Event>`: Main interface with `state: StateFlow`, `events: Flow`, `accept(intent)`, `init()`, `dispose()`
- `Reducer<State, Intent, Effect>`: Pure function `State.reduce(intent) → Next<State, Effect>`
- `DslReducer`: DSL helper using `ReducerContext` for cleaner syntax (`state {}`, `effects()`)
- `Effector<Effect, Intent, Event>`: Handles side effects, dispatches intents or publishes events
- `CoroutineEffector`: Base class for async side effects with `dispatch(intent)` and `publish(event)`
- `DefaultTea`: Orchestrates the flow, supports multiple effectors

### Component Pattern

Components extend `BaseComponent` (Decompose `ComponentContext`) and implement `UiComponent`:

```kotlin
@AssistedInject
class FeatureComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val teaFactory: TeaFactory,
    @Assisted private val navigation: (Event) -> Unit,
) : BaseComponent(componentContext), FeatureComponent {

    private val store = instanceKeeper.getTea { teaFactory.featureTea(...) }
    override val uiState = store.state.map { it.toUi() }.stateIn(componentScope, ...)

    @Composable
    override fun render(modifier: Modifier) { FeatureScreen(modifier, this) }

    @AssistedFactory
    interface Factory : FeatureComponent.Factory
}
```

### Dependency Injection

Uses **Metro** (`dev.zacsweeny.metro`):
- `@DependencyGraph` for module graphs
- `@Provides` / `@Binds` for bindings
- `@AssistedInject` / `@AssistedFactory` for components with runtime parameters

Example in `auth-impl/src/commonMain/kotlin/com/itapp/auth_impl/di/AuthGraph.kt`.

### Feature Module Layers

```
presentation/
  └── feature_name/
      ├── component/     # ComponentImpl, Screen
      ├── mapping/       # State → UiState mapping
      └── mvi/           # Tea interface, StoreFactory, Reducer, Effector
domain/
  ├── model/            # Domain DTOs
  ├── repository/       # Repository interfaces
  └── usecase/          # Use case interfaces and implementations
data/
  ├── api/              # DataSource interfaces and implementations
  ├── model/            # Request/Response DTOs, mappings
  └── repository/       # Repository implementations
```

## Key Dependencies

- Kotlin 2.2.20, Compose Multiplatform 1.9.1
- Decompose 3.4.0 (navigation/lifecycle)
- Metro 0.7.3 (DI)
- Ktor 3.3.1 (HTTP client/server)
- kotlinx-serialization, kotlinx-coroutines
- MVIKotlin 4.3.0 (some modules)
- Coil 3.0.0-alpha08 (image loading)
