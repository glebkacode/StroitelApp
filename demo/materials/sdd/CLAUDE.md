# CLAUDE.md

Этот файл служит точкой входа для Claude Code (claude.ai/code) и других AI-ассистентов при работе с репозиторием. Он содержит обзор проекта, технологический стек, структуру директорий и базовое описание архитектуры.

## Project Overview

**StroitelApp** («Магазин Строитель») — нативное Android e-commerce приложение / маркетплейс строительных товаров с русскоязычным интерфейсом.

- **Платформа:** только Android (Jetpack Compose).
- **Текущая функциональность:** авторизация по номеру телефона и регистрация пользователя.
- **В планах:** каталог товаров и сопутствующая e-commerce функциональность.
- **Application ID:** `com.itapp.stroitelapp`.

## Project Technology Stack

| Категория | Технология | Версия |
|-----------|------------|--------|
| Язык | Kotlin | 2.2.20 |
| UI | Jetpack Compose (BOM) | 2025.10.00 |
| DI | Metro (`dev.zacsweers.metro`) | 0.7.3 |
| Навигация | Decompose (+ extensions-compose) | 3.4.0 |
| MVI / Store | MviKotlin | 4.3.0 |
| Корутины | Kotlinx Coroutines | 1.10.2 |
| Сериализация | Kotlinx Serialization JSON | 1.9.0 |
| Тесты — моки | MockK | 1.13.13 |
| Тесты — корутины | kotlinx-coroutines-test | 1.10.2 |
| Сборка | Gradle (build cache + configuration cache) | 8.13 |
| AGP | Android Gradle Plugin | 8.11.2 |
| Покрытие | Kover | 0.8.3 |

**Android SDK:** `minSdk 24`, `compileSdk 36`, `targetSdk 36`.

**Сборочные конвенции:** custom convention-плагины в `build-logic/` (например, `kover-conventions`).

## Work Organization

Структура top-level модулей проекта:

```
StroitelApp/
├── composeApp/          # Точка входа Android-приложения (com.android.application)
│
├── core-architecture/   # Базовые архитектурные примитивы (BaseCoroutineUseCase, getStore extension и пр.)
├── core-navigation/     # Примитивы навигации поверх Decompose (BaseComponent, UiComponent, Relay, child lists)
├── uikit/               # Дизайн-система: StroitelTheme, цвета, типографика, шрифты Inter
│
├── auth-api/            # Публичные интерфейсы фичи авторизации
├── auth-impl/           # Реализация фичи авторизации (data + domain + presentation + DI)
│
├── build-logic/         # Gradle convention-плагины (kover-conventions и др.)
└── gradle/              # Gradle wrapper и version catalog (libs.versions.toml)
```

**Корень пакетов:** `com.itapp.<module>`.

**Все модули — `com.android.library`/`com.android.application`** (без KMP, без iOS/Web/Desktop таргетов).

## Workflow

Применяется к **новым фичам**, изменениям в `*-impl` модулях и задачам с >2 файлов логики.

### Схема

```
[1] Спецификация          (subagent: business-analyst → spec.md)
        │   Бизнес-требования, User Flow, Acceptance Criteria.
        │   Согласование с пользователем до перехода дальше.
        ▼
[2] Технический план      (subagent: Plan → presented via ExitPlanMode)
        │   Анализ кодовой базы, разбивка на шаги, критичные файлы, trade-offs.
        │   Согласование с пользователем (Approve/Reject) до перехода дальше.
        ▼
[3] Реализация            (skill: architecture; по ситуации compose-ui / kotlin-coroutines)
        │
        ▼
[4] Code review           (subagent: code-reviewer)
        │   Critical/Major закрываются до следующего шага.
        │   Minor — согласовать с пользователем.
        ▼
[5] Тесты    ║   Доки     (subagents: unit-tester, documentation-writer)
        │              ↑ рекомендуется запустить параллельно
        ▼
[6] Quality gate          (./gradlew :composeApp:assembleDebug — зелёный)
        │
        ▼
    git commit
```

### Шаги
1. **Спецификация.** Субагент `business-analyst` интервьюирует пользователя и формирует `spec.md` через skill `spec-generator` — Goal, User Flow, Functional Requirements, Edge Cases, Out of Scope, Acceptance Criteria. Без технических деталей. Согласовать спеку с пользователем перед переходом к следующему шагу. Для уже готовой спеки (в т.ч. правок существующей) — шаг пропускается.
2. **Технический план.** Субагент `Plan` (software architect) изучает кодовую базу и формирует пошаговый план реализации: ключевые файлы, архитектурные решения, trade-offs, последовательность работ. План презентуется пользователю через `ExitPlanMode` и должен быть явно подтверждён до перехода к реализации.
3. **Реализация.** Перед presentation-кодом — обязательно skill `architecture`. По ситуации — `compose-ui` для UI, `kotlin-coroutines` для асинхронной логики.
4. **Code review.** Субагент `code-reviewer` по «сырому» коду. Critical/Major замечания закрываются до перехода к следующему шагу. Minor — согласовать с пользователем.
5. **Unit-тесты + документация.** После закрытия замечаний — субагенты `unit-tester` и `documentation-writer`. Рекомендуется запускать параллельно (они не зависят друг от друга), но это **не строгое требование** — можно последовательно. Прогон `./gradlew :<module>:testDebugUnitTest` обязателен и должен быть зелёным.
6. **Quality gate перед коммитом.** Не предлагать `git commit`, пока не выполнен п. 5 и `./gradlew :composeApp:assembleDebug` зелёный.

### Skip-фильтр (мягкий)

По умолчанию workflow применяется. Для мелких правок — **спросить пользователя**, нужен ли полный workflow. Кандидаты на пропуск:

- Опечатки в строках, правки `strings.xml`.
- Правки `build.gradle.kts` / `settings.gradle.kts` без логики.
- Чисто косметические правки (форматирование, переименования без смены семантики).
- Правки только тестов или документации.
- Изменения в `.claude/` (skills, subagents, settings).
- Однострочные баг-фиксы без изменения публичного API.

### Принципы
- **Откат шагов:** если ревью требует изменить публичный API — вернуться к шагу 3 (Реализация), потом повторить ревью. Если в ходе реализации/ревью выясняется, что бизнес-требования или техплан некорректны — вернуться к шагу 1 или 2 и обновить соответствующий документ.
- **Триггеры субагентов** уже описаны в их `.claude/agents/*.md`. Этот workflow задаёт только **порядок и quality gate**, а не дублирует условия вызова.

## Architecture

### Фича-модульная архитектура

Каждая фича разделена на два модуля по схеме **api / impl**:

- `*-api` — публичные интерфейсы, навигационные конфиги, доменные модели для внешних потребителей.
- `*-impl` — реализация (data / domain / presentation + DI).
- Другие фичи зависят **только от `*-api`**, что предотвращает циклические зависимости и снижает связность.

### Слоистая структура `*-impl` модуля

Структура новых папок должна ВСЕГДА СТРОГО следовать этому описанию.

```
src/main/java/com/itapp/<feature>_impl/
├── presentation/
│   └── <screen>/                              # Presentation UI-логика
│       ├── component/                         # Decompose Component impl + Compose Screen
│       ├── mvi/                               # MviKotlin Store + StoreFactory (Intent/State/Label)
│       └── mapping/                           # (опционально) Store.State → UiState
├── domain/
│   ├── model/                                 # Доменные модели
│   ├── usecase/                               # Use case (interface + Impl)
│   └── repository/                            # Интерфейсы репозиториев
├── data/
│   ├── api/                                   # DataSource (interface + Impl)
│   ├── repository/                            # Реализации репозиториев
│   └── model/                                 # Request/Response DTO + маппинг
└── di/
    └── FeatureGraph.kt                        # Metro @DependencyGraph
```

Ресурсы фичи (строки, drawable) — в `src/main/res/`.

### Presentation pattern: ТОЛЬКО MVI на MviKotlin

В presentation-слое всех `*-impl` модулей используется **исключительно MVI на основе MviKotlin Store**. Plain Kotlin ViewModel / `StateFlow + Channel` без Store — **запрещены**.

```
Compose Screen  ───►  Component  ───►  Store  ───►  UseCase  ───►  Repository
                       (Decompose)     (MviKotlin)
```

- **Store** — `Store<Intent, State, Label>` из MviKotlin. Создаётся через `StoreFactory` (`DefaultStoreFactory`), внутри — `coroutineExecutorFactory` (обработка `Intent` + side effects) и `Reducer<State, Msg>` (чистая функция). Внутренние сообщения редьюсера (`Msg`) скрыты от внешнего мира.
- **StoreFactory** (фича-локальный) — создаёт инстанс Store, конфигурирует executor (бизнес-логика, вызовы UseCase) и reducer.
- **Component** — тонкая Decompose-обёртка: владеет Store через `instanceKeeper.getStore { storeFactory.create() }` (`com.itapp.core_architecture.getStore`), мапит `store.stateFlow` в `UiState`, подписывает `store.labels` на one-shot события и дёргает `Callbacks`. Без бизнес-логики.
- **Screen** — чистый Compose-рендер, разбит на внешний `Screen(component)` и внутренний `private Content(state, onX, ...)` для preview/тестов.

Эталоны — фичи `phone_validation` и `registration` (`*Store` + `*StoreFactory` + `*ComponentImpl`). Полная спецификация: [`.claude/skills/architecture/references/presentation-architecture.md`](.claude/skills/architecture/references/presentation-architecture.md). Перед написанием любого presentation-кода обязательно вызвать skill `architecture`.

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
- `@Provides` — фабрики (включая `StoreFactory` в `AppGraph`).

## Build Commands

```bash
# Сборка debug APK
./gradlew :composeApp:assembleDebug

# Установка debug на подключённое устройство
./gradlew :composeApp:installDebug

# Релизная сборка
./gradlew :composeApp:assembleRelease

# Чистая сборка
./gradlew clean :composeApp:assembleDebug
```

## Testing

```bash
# Все unit-тесты
./gradlew test

# Тесты конкретного модуля (debug-вариант)
./gradlew :auth-impl:testDebugUnitTest

# Принудительный перезапуск (без кэша)
./gradlew test --rerun-tasks

# Покрытие
./gradlew koverReport
```

**Быстрая проверка компиляции:** `./gradlew :auth-impl:compileDebugKotlin`.

## Conventions & Tips

- **Code style:** официальный Kotlin code style (`kotlin.code.style=official`).
- **Тесты:** располагаются в `src/test/java/`, файлы именуются `*Test.kt`. Для корутин используется `runTest` из `kotlinx-coroutines-test`. Все зависимости мокаются **только через MockK** (`mockk()` + `coEvery` / `coVerify`) — Fake-классы запрещены.
- **Ресурсы:** строки в `src/main/res/values/strings.xml`, шрифты в `src/main/res/font/` (имена в lower_snake_case), drawable в `src/main/res/drawable/`. В Compose использовать `androidx.compose.ui.res.stringResource(R.string.X)` и `androidx.compose.ui.text.font.Font(R.font.X)`.
- **Kover excludes:** классы `*Graph*`, `*Factory*`, `*Component*Impl*Factory*`; пакеты `*.di`, `*.generated`.
- **Metro Assisted Inject:** Metro **не поддерживает** несколько `@Assisted`-параметров одного типа (например, два `() -> Unit`). Workaround — обернуть их в `data class Callbacks(...)` и передавать как один assisted-параметр.
- **MviKotlin generic order:** при использовании `storeFactory.create<Intent, Action, State, Message, Label>` и `coroutineExecutorFactory<Intent, Action, State, Message, Label>` важен порядок типов: **Intent, Action, State, Message, Label**.
