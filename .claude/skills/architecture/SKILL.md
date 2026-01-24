---
name: architecture
description: Архитектура проекта и руководство по созданию новых фич. Используй при создании новых feature-модулей, реализации Clean Architecture слоёв, настройке TEA + Decompose + Compose, или когда нужно понять структуру проекта.
---

# Архитектура проекта
- Для понимания как в проекте структурируются модули и из каких папок состоят модули, смотри в [module-structure.md](module-structure.md)
- Для понимания как в проекте устроена Clean Architecture, смотри в [clean-architecture.md](clean-architecture.md)
- Для понимания как реализовывать в проекте presentation слой из Clean Architecture, смотри [presentation-architecture.md](presentation-architecture.md)
- Для понимания как работать с DI в проекте, смотри [di.md](di.md)

## Чеклист создания новой фичи

### Модули
- [ ] Создать `feature-api` модуль
- [ ] Создать `feature-impl` модуль
- [ ] Добавить в `settings.gradle.kts`
- [ ] Настроить `build.gradle.kts` с зависимостями

### API модуль
- [ ] `FeatureComponent` интерфейс с `UiState`, методами, `Factory`
- [ ] Domain модели (если нужны снаружи)
- [ ] UseCase абстрактные классы (если нужны снаружи)

### Domain слой (impl)
- [ ] Domain модели (`domain/model/`)
- [ ] Repository интерфейс (`domain/repository/`)
- [ ] UseCase абстрактный класс + реализация (`domain/usecase/`)

### Data слой (impl)
- [ ] Response/Request модели (`data/model/`)
- [ ] Mapper extension-функции (`data/mapper/`)
- [ ] DataSource интерфейс + реализация (`data/api/`)
- [ ] Repository реализация (`data/repository/`)

### Presentation слой (impl)
- [ ] `FeatureTea` интерфейс (State, Intent, Effect, Event)
- [ ] `FeatureReducer` (DslReducer)
- [ ] `FeatureEffector` (CoroutineEffector)
- [ ] `TeaFactory.featureTea()` extension
- [ ] `StateMapping` (State → UiState)
- [ ] `FeatureComponentImpl` с `@AssistedInject`
- [ ] `FeatureScreen` (Compose)

### DI
- [ ] `FeatureGraph` с `@DependencyGraph`
- [ ] Все `@Binds` для интерфейсов
- [ ] Подключить к родительскому графу

### Тесты
- [ ] Unit-тесты для UseCase
- [ ] Unit-тесты для Repository
- [ ] Unit-тесты для Reducer (опционально)
