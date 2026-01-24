# CLAUDE.md

Этот файл предоставляет руководство для Claude Code (claude.ai/code) при работе с кодом в этом репозитории.

## Команды сборки

```bash
# Android
./gradlew :composeApp:assembleDebug

# Desktop (JVM)
./gradlew :composeApp:run

# Server (Ktor, порт 8080)
./gradlew :server:run

# Web (Wasm - современные браузеры)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Web (JS - старые браузеры)
./gradlew :composeApp:jsBrowserDevelopmentRun

# iOS - открыть iosApp/ в Xcode
```

## Обзор архитектуры

**Kotlin Multiplatform** проект (Android, iOS, Web/Wasm, Desktop/JVM, Server) с использованием **Compose Multiplatform** для общего UI.

### Структура модулей

Модули следуют паттерну **API/Implementation split**:
- `feature-api`: Публичные интерфейсы (например, `PhoneValidationComponent`)
- `feature-impl`: Конкретные реализации с послойной архитектурой (presentation → domain → data)

Основные модули:
- `core-architecture`: Кастомный TEA для управления состоянием
- `core-navigation`: Система компонентов на основе Decompose
- `uikit`: Общие UI-компоненты и темизация

### TEA (The Elm Architecture)

Кастомное управление состоянием в `core-architecture/src/commonMain/kotlin/com/itapp/core_architecture/tea/`:

```
Intent → Reducer → State + Effects
                        ↓
              Effector → Intent/Event
```

Ключевые классы:
- `Tea<State, Intent, Event>`: Главный интерфейс с `state: StateFlow`, `events: Flow`, `accept(intent)`, `init()`, `dispose()`
- `Reducer<State, Intent, Effect>`: Чистая функция `State.reduce(intent) → Next<State, Effect>`
- `DslReducer`: DSL-хелпер с `ReducerContext` для чистого синтаксиса (`state {}`, `effects()`)
- `Effector<Effect, Intent, Event>`: Обрабатывает побочные эффекты, диспатчит интенты или публикует события
- `CoroutineEffector`: Базовый класс для асинхронных эффектов с `dispatch(intent)` и `publish(event)`
- `DefaultTea`: Оркестрирует поток, поддерживает множество эффекторов

### Паттерн компонентов

Компоненты наследуют `BaseComponent` (Decompose `ComponentContext`) и реализуют `UiComponent`:

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

Используется **Metro** (`dev.zacsweeny.metro`):
- `@DependencyGraph` для графов модулей
- `@Provides` / `@Binds` для биндингов
- `@AssistedInject` / `@AssistedFactory` для компонентов с runtime-параметрами

Пример в `auth-impl/src/commonMain/kotlin/com/itapp/auth_impl/di/AuthGraph.kt`.

### Слои feature-модулей

```
presentation/
  └── feature_name/
      ├── component/     # ComponentImpl, Screen
      ├── mapping/       # State → UiState маппинг
      └── mvi/           # Tea интерфейс, StoreFactory, Reducer, Effector
domain/
  ├── model/            # Domain DTO
  ├── repository/       # Интерфейсы репозиториев
  └── usecase/          # Интерфейсы и реализации use cases
data/
  ├── api/              # Интерфейсы и реализации DataSource
  ├── model/            # Request/Response DTO, маппинги
  └── repository/       # Реализации репозиториев
```

## Ключевые зависимости

- Kotlin 2.2.20, Compose Multiplatform 1.9.1
- Decompose 3.4.0 (навигация/жизненный цикл)
- Metro 0.7.3 (DI)
- Ktor 3.3.1 (HTTP клиент/сервер)
- kotlinx-serialization, kotlinx-coroutines
- MVIKotlin 4.3.0 (некоторые модули)
- Coil 3.0.0-alpha08 (загрузка изображений)
