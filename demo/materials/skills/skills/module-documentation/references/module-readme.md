# Структура README модуля

Каждый модуль должен содержать `README.md` в корневой директории с описанием назначения и использования.

## Шаблон README.md документа API модуля

```markdown
# :feature-api

Публичный API для функциональности [название функциональности].

## Назначение

[Краткое описание что предоставляет модуль и зачем он нужен]

## Публичный API

### Компоненты

| Компонент | Описание |
|-----------|----------|
| `FeatureComponent` | [Описание компонента] |

### Модели

| Модель | Описание |
|--------|----------|
| `FeatureModel` | [Описание модели] |

### Use Cases

| Use Case | Описание |
|----------|----------|
| `GetFeatureUseCase` | [Описание use case] |

## Зависимости

- `:core-architecture` — базовые классы (BaseCoroutineUseCase, getStore extension)
- `:core-navigation` — интерфейс UiComponent

## Использование

```kotlin
// Пример создания компонента
val component = featureComponentFactory.create(
    componentContext = componentContext,
    navigation = { event -> /* handle navigation */ }
)
```

## Шаблон README.md документа IMPL модуля

```markdown
# :feature-impl

Реализация функциональности [название функциональности].

## Назначение

[Описание что реализует модуль и как]

## Архитектура

```
feature-impl/src/main/java/com/itapp/<feature>_impl/
├── presentation/
│   └── feature_name/
│       ├── component/      # Decompose компонент и Screen
│       ├── mapping/        # Маппинг Store.State → UiState
│       └── mvi/            # MviKotlin: Store + StoreFactory (Intent/State/Label/Msg)
├── domain/
│   ├── model/              # Domain модели
│   ├── repository/         # Интерфейсы репозиториев
│   └── usecase/            # Use cases
├── data/
│   ├── api/                # DataSource интерфейсы и реализации
│   ├── model/              # DTO модели
│   └── repository/         # Реализации репозиториев
└── di/                     # DI граф модуля
```

## Ключевые классы

### Presentation

| Класс | Описание |
|-------|----------|
| `FeatureComponentImpl` | Реализация компонента с @AssistedInject |
| `FeatureScreen` | Compose UI экрана |
| `FeatureReducer` | Обработка интентов |
| `FeatureEffector` | Побочные эффекты |

### Domain

| Класс | Описание |
|-------|----------|
| `FeatureRepository` | Интерфейс репозитория |
| `GetDataUseCase` | Use case получения данных |

### Data

| Класс | Описание |
|-------|----------|
| `FeatureRepositoryImpl` | Реализация репозитория |
| `FeatureDataSource` | Источник данных (API/DB) |

## DI

Модуль предоставляет `FeatureGraph`:

```kotlin
@DependencyGraph(AppScope::class)
interface FeatureGraph {
    val featureComponentFactory: FeatureComponent.Factory
}
```

## Зависимости

### API
- `:feature-api` — публичный интерфейс

### Implementation
- `:core-architecture` — BaseCoroutineUseCase, MviKotlin re-exports, `getStore` extension
- `:core-navigation` — Decompose обёртки
- `:uikit` — дизайн-система (StroitelTheme, цвета, типографика)
```

```

## Правила написания README

### Структура

1. **Заголовок** — имя модуля с двоеточием (`:module-name`)
2. **Назначение** — 1-3 предложения о цели модуля
3. **Архитектура** — для impl модулей, структура директорий
4. **Ключевые классы** — таблица с основными классами
5. **DI** — как модуль интегрируется в DI граф
6. **Зависимости** — от каких модулей зависит
7. **Использование** — примеры кода

### Стиль

- Используй русский язык
- Названия классов и модулей — в backticks
- Примеры кода — в code blocks с указанием языка
- Таблицы для перечисления классов/методов

### Что включать

- Назначение модуля (зачем он нужен)
- Публичный API (что экспортирует)
- Примеры использования
- Зависимости

### Чего избегать

- Деталей реализации (если не критично)
- Устаревшей информации
- Дублирования KDoc документации
- Слишком длинных README (лучше ссылки на код)