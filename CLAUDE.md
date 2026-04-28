# CLAUDE.md

Этот файл служит точкой входа для Claude Code (claude.ai/code) и других AI-ассистентов при работе с репозиторием. Он содержит обзор проекта, технологический стек, структуру директорий и базовое описание архитектуры.

## Project Overview

**StroitelApp** («Магазин Строитель») — мультиплатформенное e-commerce приложение / маркетплейс строительных товаров с русскоязычным интерфейсом.

- **Платформы:** Android, iOS, Web (JS / WASM), Desktop (JVM), бэкенд на Ktor.
- **Текущая функциональность:** авторизация по номеру телефона и регистрация пользователя.
- **В планах:** каталог товаров и сопутствующая e-commerce функциональность (модули `products-api` / `products-impl` уже объявлены в `settings.gradle.kts`, но пока не реализованы).
- **Application ID:** `com.itapp.stroitelapp`.

## Project Technology Stack

| Категория | Технология | Версия |
|-----------|------------|--------|
| Язык | Kotlin | 2.2.20 |
| UI | Compose Multiplatform | 1.9.1 |
| Hot Reload | Compose Hot Reload | 1.0.0-rc02 |
| Архитектура | Собственная TEA-реализация (`core-architecture`) | — |
| DI | Metro (`dev.zacsweers.metro`) | 0.7.3 |
| Навигация | Decompose (+ extensions-compose) | 3.4.0 |
| Сеть | Ktor (client + server) | 3.3.1 |
| Корутины | Kotlinx Coroutines | 1.10.2 |
| Сериализация | Kotlinx Serialization JSON | 1.9.0 |
| Изображения | Coil 3 (multiplatform) | 3.0.0-alpha08 |
| Сборка | Gradle (build cache + configuration cache) | 8.13 |
| Покрытие | Kover | 0.8.3 |

**HTTP-движки Ktor:** `okhttp` (Android), `darwin` (iOS), `js` (Web).

**Таргеты:** Android (`minSdk 24`, `compile/targetSdk 36`), iOS (`x64` / `arm64` / `simulatorArm64`), JVM Desktop, JS, WASM.

**Сборочные конвенции:** custom convention-плагины в `build-logic/` (например, `kover-conventions`).

## Work Organization

Структура top-level модулей проекта:

```
StroitelApp/
├── composeApp/          # Точка входа мультиплатформенного Compose-приложения (Android/iOS/Desktop/Web)
├── server/              # Ktor JVM бэкенд
├── shared/              # Код, общий для всех таргетов (константы, абстракция платформы)
│
├── core-architecture/   # Фреймворк TEA (Tea, Reducer, DslReducer, CoroutineEffector, BaseCoroutineUseCase)
├── core-navigation/     # Примитивы навигации поверх Decompose (BaseComponent, UiComponent, реле, child lists)
├── uikit/               # Дизайн-система: StroitelTheme, цвета, типографика, переиспользуемые компоненты
│
├── auth-api/            # Публичные интерфейсы фичи авторизации
├── auth-impl/           # Реализация фичи авторизации (data + domain + presentation + DI)
├── products-api/        # [Объявлен, не реализован] Публичные интерфейсы каталога товаров
├── products-impl/       # [Объявлен, не реализован] Реализация каталога товаров
│
├── build-logic/         # Gradle convention-плагины (kover-conventions и др.)
├── iosApp/              # Xcode-проект для iOS
└── gradle/              # Gradle wrapper и version catalog (libs.versions.toml)
```

**Корень пакетов:** `com.itapp.<module>`.

## Architecture

### Фича-модульная архитектура

Каждая фича разделена на два модуля по схеме **api / impl**:

- `*-api` — публичные интерфейсы, навигационные конфиги, доменные модели для внешних потребителей.
- `*-impl` — реализация (data / domain / presentation + DI).
- Другие фичи зависят **только от `*-api`**, что предотвращает циклические зависимости и снижает связность.

### TEA (The Elm Architecture)

Все экраны следуют паттерну TEA, реализованному в `core-architecture`:

```
User Action → Intent → Reducer → State (StateFlow)
                          ↓
                      Effect → Effector → Event (Flow, навигация / UI side effects)
```

**Ключевые компоненты:**
- `Tea<State, Intent, Event>` — store-интерфейс с `state: StateFlow`, `events: Flow`, `accept(intent)`.
- `Reducer<State, Intent, Effect>` — чистая функция трансформации состояния.
- `DslReducer` — DSL-обёртка с блоками `state { copy(...) }` и `effects(...)`.
- `CoroutineEffector<Effect, Intent, Event>` — обработчик side effects с `dispatch(intent)` и `publish(event)`.
- `BaseCoroutineUseCase<I, O>` — базовый класс для доменных use case-ов.

### Слоистая структура `*-impl` модуля

```
presentation/
  └── <screen>/
      ├── component/ScreenComponentImpl.kt   # Decompose-компонент
      ├── component/ScreenScreen.kt          # Compose UI
      ├── mvi/ScreenTea.kt                   # State / Intent / Effect
      ├── mvi/ScreenStoreFactory.kt          # Сборка TEA-store
      └── mapping/StateMapping.kt            # State → UiState
domain/
  ├── model/                                 # Доменные модели
  ├── usecase/                               # Use case (interface + Impl)
  └── repository/                            # Интерфейсы репозиториев
data/
  ├── api/                                   # DataSource (interface + Impl)
  ├── repository/                            # Реализации репозиториев
  └── model/                                 # Request/Response DTO + маппинг
di/
  └── FeatureGraph.kt                        # Metro @DependencyGraph
```

### Навигация (Decompose)

- `StackNavigation<Config>` для стека экранов.
- `childStack()` для создания навигационного стека.
- `navigation.push()` / `navigation.pop()` для перехода между экранами.
- Каждый экран — Decompose-компонент.

### DI (Metro)

- `@Inject` — конструкторное внедрение.
- `@AssistedInject` / `@AssistedFactory` — для зависимостей с runtime-параметрами.
- `@DependencyGraph` — конфигурация графа фичи.
- `@Binds` — связывание интерфейсов с реализациями.

## Build Commands

```bash
# Android
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug

# Desktop (JVM)
./gradlew :composeApp:run

# Web (WASM — современные браузеры)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun

# Web (JS — старые браузеры)
./gradlew :composeApp:jsBrowserDevelopmentRun

# Server
./gradlew :server:run

# iOS — открыть iosApp/ в Xcode
```

## Testing

```bash
# Все тесты
./gradlew test

# Тесты конкретного модуля
./gradlew :auth-impl:testAndroidHostTest      # commonTest на JVM (рекомендуется)
./gradlew :auth-impl:iosSimulatorArm64Test    # тесты на iOS-симуляторе

# Покрытие
./gradlew koverReport
```

> ⚠️ `:auth-impl:test` неоднозначен (выбирает между `testAndroid` и `testAndroidHostTest`). Для запуска `commonTest` используйте `:auth-impl:testAndroidHostTest`.

**Быстрая проверка компиляции:** `./gradlew :auth-impl:compileAndroidMain`.

## Conventions & Tips

- **Code style:** официальный Kotlin code style (`kotlin.code.style=official`).
- **Тесты:** располагаются в source set `commonTest`, файлы именуются `*Test.kt`. Для корутин используется `runTest`. Для дублёров — `Fake*` репозитории / data sources.
- **Kover excludes:** классы `*Graph*`, `*Factory*`, `*Component*Impl*Factory*`; пакеты `*.di`, `*.generated`.
- **Metro Assisted Inject:** Metro **не поддерживает** несколько `@Assisted`-параметров одного типа (например, два `() -> Unit`). Workaround — обернуть их в `data class Callbacks(...)` и передавать как один assisted-параметр.
- **TEA нюансы:**
  - `ReducerContext.state {}` возвращает `Unit`, а не state. Чтобы прочитать текущее значение перед изменением, обращайтесь к свойству `state` напрямую.
  - Передавайте данные в Effector через поля `Effect`, а не читайте state внутри Effector.
