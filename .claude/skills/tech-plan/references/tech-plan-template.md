# Tech Plan: [Feature Name]

## Meta

| Key | Value |
|-----|-------|
| Based on | [spec file path or brief description] |
| Total tasks | [N] |
| Parallelizable | [M of N] |

---

## Dependency Graph

```
Phase 0: Contracts
    |
    +---> Phase 1: Implementation  [PARALLEL: Task 1.1 | Task 1.2 | Task 1.3]
    |
    v
Phase 2: Integration
    |
    v
Phase 3: Tests  [PARALLEL: Task 3.1 | Task 3.2]
```

---

## Phase 0: Contracts

> Sequential — must complete before Phase 1

### Task 0.1: [Task Name]

| | |
|---|---|
| **Size** | S / M / L |
| **Creates** | `path/to/NewFile.kt` `(NEW)` |
| **Modifies** | `path/to/ExistingFile.kt` |

**Steps:**
1. [Concrete action]
2. [Concrete action]

**Key code:**
```kotlin
// Interface, model, or contract to define
```

**Done when:**
- [ ] [Verifiable condition]
- [ ] [Verifiable condition]

---

## Phase 1: Implementation

> PARALLEL — tasks are independent, execute simultaneously

### Task 1.1: Data Layer

| | |
|---|---|
| **Size** | M |
| **Creates** | `path/to/FeatureApi.kt` `(NEW)`, `path/to/RepositoryImpl.kt` `(NEW)` |
| **Modifies** | — |
| **Depends on** | Phase 0 |

**Steps:**
1. [Create API interface]
2. [Create RepositoryImpl]

```kotlin
// Key implementation skeleton
```

**Done when:**
- [ ] [Condition]

---

### Task 1.2: Domain Layer

| | |
|---|---|
| **Size** | S |
| **Creates** | `path/to/UseCaseImpl.kt` `(NEW)` |
| **Modifies** | — |
| **Depends on** | Phase 0 |

**Steps:**
1. [Create use case implementation]

```kotlin
// Key implementation skeleton
```

**Done when:**
- [ ] [Condition]

---

### Task 1.3: Presentation Layer

| | |
|---|---|
| **Size** | L |
| **Creates** | `path/to/mvi/FeatureStore.kt` `(NEW)`, `path/to/mvi/FeatureStoreFactory.kt` `(NEW)`, `path/to/component/FeatureComponentImpl.kt` `(NEW)`, `path/to/component/FeatureScreen.kt` `(NEW)`, `path/to/mapping/StateMapping.kt` `(NEW, опц.)` |
| **Modifies** | — |
| **Depends on** | Phase 0 |

**Steps:**
1. [Create FeatureStore: Intent, State, Label]
2. [Create FeatureStoreFactory: executor + reducer + internal Msg]
3. [Create FeatureComponentImpl: getStore, store.stateFlow → UiState, store.labels → callbacks]
4. [Create FeatureScreen: collectAsState, action delegation]

```kotlin
// Key implementation skeleton
```

**Done when:**
- [ ] [Condition]

---

## Phase 2: Integration

> Sequential — depends on all Phase 1 tasks

### Task 2.1: DI and Wiring

| | |
|---|---|
| **Size** | S |
| **Creates** | `path/to/DI.kt` `(NEW)` (or modifies existing) |
| **Modifies** | `path/to/build.gradle.kts`, `path/to/nav_graph.xml` |
| **Depends on** | Phase 1 (all tasks) |

**Steps:**
1. [Register dependencies in Koin module]
2. [Add module dependencies in build.gradle.kts]
3. [Add navigation entry if needed]
4. [Register feature toggle if needed]

```kotlin
// Koin module
val featureModule = module {
    // registrations
}
```

**Done when:**
- [ ] App compiles
- [ ] DI graph resolves without errors
- [ ] Navigation to the screen works

---

## Phase 3: Tests

> PARALLEL — test tasks are independent
> Implementation details: delegate to `/testing` skill

### Task 3.1: [Layer] Tests

| | |
|---|---|
| **Size** | S / M / L |
| **Creates** | `path/to/ClassTest.kt` `(NEW)` |
| **Depends on** | Phase 2 |

**What to test:**
- [Scenario 1]
- [Scenario 2]
- [Scenario 3]

### Task 3.2: [Layer] Tests

| | |
|---|---|
| **Size** | S / M / L |
| **Creates** | `path/to/ClassTest.kt` `(NEW)` |
| **Depends on** | Phase 2 |

**What to test:**
- [Scenario 1]
- [Scenario 2]
- [Scenario 3]

---

## Out of Scope

- [Explicitly excluded items]
- [Future improvements — note but do not implement]

---

## Post-Implementation Pipeline

> Запускается **после Phase N (последней) и до коммита**. Канонический источник — `CLAUDE.md` → `Workflow после реализации фичи (для AI-ассистентов)`. Здесь — короткое напоминание для исполнителя плана.

**Параллельный батч (запускать одновременно — работают на непересекающихся файлах):**
- `documentation-writer` — KDoc на новый публичный API + README модуля при необходимости.
- `unit-tester` — тесты в `src/test/java` + прогон `:<module>:testDebugUnitTest`.

**Последовательно после батча:**
1. `spec-compliance-checker` — сверка реализации со `spec.md` / `plan.md` (если они есть).
2. `code-reviewer` — общее ревью архитектуры (MVI/MviKotlin) / конкурентности / UI-производительности.

Только после успешного прохождения всей цепочки — коммит (если пользователь его просил).

**Триггер-команда:**
```bash
claude "Запусти post-implementation pipeline по CLAUDE.md для фичи <feature-name> (модули <list>). Параллельно: documentation-writer + unit-tester. Последовательно: spec-compliance-checker → code-reviewer."
```

Если пользователь явно отказался от какого-либо шага («без тестов» / «без ревью») — уважать.