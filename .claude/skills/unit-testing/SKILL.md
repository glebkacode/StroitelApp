---
name: unit-testing
description: Экспертная работа с unit-тестами в Kotlin Multiplatform проекте. Используй при написании тестов для UseCase, Repository, Reducer, Mapping, Effector, при покрытии новой логики, ревью существующих тестов или настройке Mokkery/JUnit. Триггеры — "напиши тесты", "покрой тестами", "unit test", "замокай", "тест для use case / репозитория / reducer".
---

# Unit Testing

Skill для написания качественных unit-тестов в KMP проекте StroitelApp. Стек: **JUnit 4** + **Mokkery** (KMP-friendly mocking) + **kotlinx-coroutines-test** + **kotlin.test** assertions.

## Когда использовать

- Покрываешь новый код тестами после реализации фичи
- Пишешь тесты для существующего модуля (UseCase / Repository / Reducer / Mapping / DataSource)
- Нужно замокать зависимость с помощью Mokkery
- Ревьюишь чужие тесты или рефакторишь старые
- Настраиваешь Mokkery / тестовое окружение в новом модуле

## Базовые правила проекта

1. **Расположение тестов** — `src/commonTest/kotlin/...`. Тесты идут рядом с тестируемым кодом по той же пакетной структуре.
2. **Запуск** — `./gradlew :<module>:testAndroidHostTest` (НЕ `:test` — он неоднозначен).
3. **Имена тестов** — backtick-стиль: `` `should <expected> when <condition>` ``. Чётко описывают поведение, а не реализацию.
4. **Структура** — Given / When / Then (разделяй блоки пустой строкой или комментариями).
5. **Корутины** — оборачивай тесты в `runTest { }`, не используй `runBlocking`.
6. **Один тест — одна проверка поведения.** Не сваливай несколько кейсов в один `@Test`.

## Когда мок vs Fake

| Зависимость | Что использовать |
|-------------|------------------|
| Простой интерфейс с 1-2 методами и состоянием для проверки | **Fake** (как `FakeAuthRepository`) |
| Сложный интерфейс / нужен matcher / verify count / sequence | **Mokkery mock** |
| Финальный класс из библиотеки | **Mokkery mock** (работает с финалами через compiler plugin) |
| Use case в тестах презентационного слоя | **Mokkery mock** — выразительнее |

> Существующий код в `auth-impl` использует Fake-подход (см. `FakeAuthRepository`, `FakeAuthDataSource`). Это валидный путь для простых случаев. Mokkery нужен там, где Fake-классы превращаются в комбинаторный взрыв состояний.

## Обязательная структура теста

```kotlin
class MyUseCaseImplTest {

    private lateinit var repository: MyRepository
    private lateinit var useCase: MyUseCaseImpl

    @BeforeTest
    fun setup() {
        repository = mock<MyRepository>()
        useCase = MyUseCaseImpl(repository)
    }

    @Test
    fun `should return success when repository succeeds`() = runTest {
        // Given
        everySuspend { repository.fetch() } returns Unit

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        verifySuspend { repository.fetch() }
    }
}
```

## Что покрываем тестами

| Слой | Цель тестов |
|------|-------------|
| **Reducer / DslReducer** | переходы State, генерируемые Effects под каждый Intent |
| **UseCase** | проверка делегирования + обработка ошибок (Result.success / failure) |
| **Repository** | корректный вызов DataSource, маппинг DTO → Domain, проброс исключений |
| **DataSource** | сериализация запроса, парсинг ответа (через `MockEngine` Ktor) |
| **Mapping** | чистые функции State → UiState, DTO → Domain — все ветки |
| **Effector** | реакция на Effect: вызов use case, dispatch Intent, publish Event |

UI / Compose в этом skill **не покрываем** — для Compose тестов смотри отдельный workflow.

## Чек-лист перед коммитом теста

- [ ] Тест запускается командой `./gradlew :<module>:testAndroidHostTest`
- [ ] Имя теста описывает поведение, а не реализацию
- [ ] Структура Given/When/Then читается за 5 секунд
- [ ] Нет `runBlocking` — только `runTest`
- [ ] Нет `Thread.sleep` — только `advanceUntilIdle()` / `advanceTimeBy()`
- [ ] Нет реальных HTTP/DB вызовов — всё замокано
- [ ] Каждый `@Test` проверяет одно поведение
- [ ] Покрыты happy path + минимум один failure path
- [ ] Edge cases (пустая строка, null, 0, граница диапазона) учтены

## Дополнительные ресурсы

- Принципы и философия unit-тестов в проекте — [principles.md](references/principles.md)
- Подключение и настройка Mokkery в KMP-модуле — [mokkery-setup.md](references/mokkery-setup.md)
- API Mokkery (mock, every, everySuspend, verify, matchers) — [mokkery-api.md](references/mokkery-api.md)
- Паттерны тестов по слоям (UseCase / Repository / Reducer / Mapping / Effector) — [patterns.md](references/patterns.md)
- Тестирование TEA-стора (State / Intent / Effect / Event) — [tea-testing.md](references/tea-testing.md)
- Тестирование корутин и Flow (runTest, TestDispatcher, Turbine) — [coroutines-testing.md](references/coroutines-testing.md)
- Антипаттерны — чего нельзя делать в тестах — [antipatterns.md](references/antipatterns.md)
