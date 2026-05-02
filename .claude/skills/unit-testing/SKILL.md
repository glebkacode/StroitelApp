---
name: unit-testing
description: Экспертиза по написанию unit-тестов в нативном Android-проекте. Используй при написании тестов для UseCase, Repository, MviKotlin Store (Reducer/Executor), Mapping, при покрытии новой логики, ревью существующих тестов или настройке MockK/JUnit. Триггеры — "напиши тесты", "покрой тестами", "unit test", "замокай", "тест для use case / репозитория / reducer".
---

# Unit Testing

Skill для написания качественных unit-тестов в Android-проекте StroitelApp. 
Стек: *
- *JUnit 4**
- **MockK** (`io.mockk`)
- **kotlinx-coroutines-test** 
- **kotlin.test** assertions

## Когда использовать

- Покрываешь новый код тестами после реализации фичи
- Пишешь тесты для существующего модуля (UseCase / Repository / Store reducer / Mapping / DataSource)
- Нужно замокать зависимость с помощью MockK
- Ревьюишь чужие тесты или рефакторишь старые
- Настраиваешь MockK / тестовое окружение в новом модуле

## Базовые правила проекта

1. **Расположение тестов** — `src/test/java/...`. Тесты идут рядом с тестируемым кодом по той же пакетной структуре.
2. **Запуск** — `./gradlew :<module>:testDebugUnitTest`. Для гарантированного перезапуска (если был cache hit) — `--rerun-tasks`.
3. **Имена тестов** — backtick-стиль: `` `should <expected> when <condition>` ``. Чётко описывают поведение, а не реализацию.
4. **Структура** — Given / When / Then (разделяй блоки пустой строкой или комментариями).
5. **Корутины** — оборачивай тесты в `runTest { }`, не используй `runBlocking`.
6. **Один тест — одна проверка поведения.** Не сваливай несколько кейсов в один `@Test`.

## Mocking — только MockK

В проекте действует жёсткое правило: **все** зависимости в unit-тестах мокаются через **MockK** (`mockk()` + `every` / `coEvery` / `verify` / `coVerify` / `slot()` / matchers). Подход с Fake-классами (`FakeAuthRepository`, `FakeAuthDataSource`) **запрещён** и удалён из кодовой базы.

| Зависимость | Что использовать |
|-------------|------------------|
| Любой интерфейс / абстрактный класс | **MockK `mockk()`** |
| Финальный класс из библиотеки | **MockK `mockk()`** (по умолчанию умеет финалы) |
| Use case в тестах презентации | **MockK `mockk()`** |
| Захват аргумента | **MockK `slot<T>() + capture(slot)`** |
| Чистая функция / data-класс | Без моков — вызов напрямую |

Если зависимость MockK ещё не подключена в модуле — добавь `testImplementation(libs.mockk)` в `build.gradle.kts` модуля (см. [mockk-setup.md](references/mockk-setup.md)). Создавать новые `Fake*` классы **нельзя**.

## Обязательная структура теста

```kotlin
class MyUseCaseImplTest {

    private lateinit var repository: MyRepository
    private lateinit var useCase: MyUseCaseImpl

    @BeforeTest
    fun setup() {
        repository = mockk()
        useCase = MyUseCaseImpl(repository)
    }

    @Test
    fun `should return success when repository succeeds`() = runTest {
        // Given
        coEvery { repository.fetch() } returns Unit

        // When
        val result = useCase(Unit)

        // Then
        assertTrue(result.isSuccess)
        coVerify { repository.fetch() }
    }
}
```

## Что покрываем тестами

| Слой | Цель тестов |
|------|-------------|
| **Reducer (`Reducer<State, Msg>`)** | переходы State под каждый Msg — чистая функция, без моков |
| **Executor (через Store)** | реакция на Intent: вызов use case, dispatch Msg, publish Label — здесь моки |
| **UseCase** | проверка делегирования + обработка ошибок (Result.success / failure) |
| **Repository** | корректный вызов DataSource, маппинг DTO → Domain, проброс исключений |
| **DataSource** | сериализация запроса, парсинг ответа (через тестовый http-клиент или прямой mock) |
| **Mapping** | чистые функции `Store.State → UiState`, `Response → Domain` — все ветки |

UI / Compose в этом skill **не покрываем** — для Compose тестов смотри отдельный workflow (Compose UI test / Paparazzi).

## Чек-лист перед коммитом теста

- [ ] Тест запускается командой `./gradlew :<module>:testDebugUnitTest`
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
- Подключение и настройка MockK в Android-модуле — [mockk-setup.md](references/mockk-setup.md)
- API MockK (`mockk`, `every`, `coEvery`, `verify`, `coVerify`, `slot`, matchers) — [mockk-api.md](references/mockk-api.md)
- Паттерны тестов по слоям (UseCase / Repository / Reducer / Mapping / Executor) — [patterns.md](references/patterns.md)
- Тестирование MviKotlin Store (Intent / State / Label / Msg) — [mvikotlin-testing.md](references/mvikotlin-testing.md)
- Тестирование корутин и Flow (runTest, TestDispatcher) — [coroutines-testing.md](references/coroutines-testing.md)
- Антипаттерны — чего нельзя делать в тестах — [antipatterns.md](references/antipatterns.md)
