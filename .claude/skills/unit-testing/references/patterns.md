# Паттерны тестов по слоям

Все примеры — для Android-модуля, source set `src/test/java/`. Использование MockK + `kotlinx-coroutines-test` + `kotlin.test`.

---

## 1. UseCase

UseCase — тонкая обёртка над репозиторием, оборачивающая результат в `Result`. Тестируем:

- Делегирование вызова репозиторию с правильными аргументами
- Возврат `Result.success` при успехе
- Возврат `Result.failure` с правильным исключением

```kotlin
class ValidatePhoneNumberUseCaseImplTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: ValidatePhoneNumberUseCaseImpl

    @BeforeTest
    fun setup() {
        repository = mockk()
        useCase = ValidatePhoneNumberUseCaseImpl(repository)
    }

    @Test
    fun `should call repository validatePhone with correct dto`() = runTest {
        // Given
        val dto = ValidationPhoneDto("+79001234567", "password123")
        coEvery { repository.validatePhone(any()) } returns Unit

        // When
        useCase.run(ValidatePhoneNumberUseCase.Params(dto))

        // Then
        coVerify { repository.validatePhone(dto) }
    }

    @Test
    fun `should return success when repository succeeds`() = runTest {
        val dto = ValidationPhoneDto("+79001234567", "password123")
        coEvery { repository.validatePhone(any()) } returns Unit

        val result = useCase(ValidatePhoneNumberUseCase.Params(dto))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return failure when repository throws`() = runTest {
        val dto = ValidationPhoneDto("+79001234567", "password123")
        coEvery { repository.validatePhone(any()) } throws RuntimeException("Validation failed")

        val result = useCase(ValidatePhoneNumberUseCase.Params(dto))

        assertTrue(result.isFailure)
        assertEquals("Validation failed", result.exceptionOrNull()?.message)
    }
}
```

---

## 2. Repository

Repository связывает DataSource (network/db) с domain-слоем. Тестируем:

- Корректный вызов DataSource (передача аргументов, маппинг Domain → DTO)
- Корректный маппинг ответа DataSource → Domain
- Проброс / трансформация исключений

```kotlin
class AuthRepositoryImplTest {

    private lateinit var dataSource: AuthDataSource
    private lateinit var repository: AuthRepositoryImpl

    @BeforeTest
    fun setup() {
        dataSource = mockk()
        repository = AuthRepositoryImpl(dataSource)
    }

    @Test
    fun `should call dataSource with mapped request when validatePhone called`() = runTest {
        // Given
        val captured = slot<ValidatePhoneRequest>()
        coEvery { dataSource.validatePhone(capture(captured)) } returns Unit
        val dto = ValidationPhoneDto("+79001234567", "password123")

        // When
        repository.validatePhone(dto)

        // Then
        assertEquals("+79001234567", captured.captured.phoneNumber)
        assertEquals("password123", captured.captured.password)
    }

    @Test
    fun `should propagate exception when dataSource throws`() = runTest {
        coEvery { dataSource.validatePhone(any()) } throws RuntimeException("Network error")

        val exception = assertFailsWith<RuntimeException> {
            repository.validatePhone(ValidationPhoneDto("+79001234567", "p"))
        }

        assertEquals("Network error", exception.message)
    }
}
```

---

## 3. Reducer (MviKotlin)

Reducer — чистая функция `(State, Msg) -> State`. Не нужны корутины, не нужны моки.

Если reducer определён inline внутри `StoreFactory.create(reducer = Reducer<State, Msg> { ... })`, вынеси его в `internal val FeatureReducer = Reducer<State, Msg> { ... }`, чтобы тестировать отдельно.

```kotlin
class FeatureReducerTest {

    @Test
    fun `should set isLoading=true when LoadingStarted`() {
        val state = FeatureStore.State(isLoading = false)

        val next = with(FeatureReducer) { state.reduce(FeatureStoreFactory.Msg.LoadingStarted) }

        assertEquals(true, next.isLoading)
    }

    @Test
    fun `should populate items and reset loading when ItemsLoaded`() {
        val state = FeatureStore.State(isLoading = true)
        val items = listOf(Item(1, "a"))

        val next = with(FeatureReducer) { state.reduce(FeatureStoreFactory.Msg.ItemsLoaded(items)) }

        assertEquals(items, next.items)
        assertEquals(false, next.isLoading)
    }

    @Test
    fun `should set error when LoadFailed`() {
        val state = FeatureStore.State(isLoading = true)

        val next = with(FeatureReducer) { state.reduce(FeatureStoreFactory.Msg.LoadFailed("boom")) }

        assertEquals("boom", next.error)
        assertEquals(false, next.isLoading)
    }
}
```

> Подробности и тесты Store целиком (Intent → Msg/Label через executor) — см. [`mvikotlin-testing.md`](mvikotlin-testing.md).

---

## 4. Mapping (State → UiState, Response → Domain)

Чистые функции — мокать нечего. Покрывай все ветки.

```kotlin
class FeatureStateMappingTest {

    @Test
    fun `should map items to ui items`() {
        val state = FeatureStore.State(items = listOf(Item(1, "Foo")))

        val uiState = state.toUi()

        assertEquals(1, uiState.items.size)
        assertEquals("Foo", uiState.items[0].title)
    }

    @Test
    fun `should preserve isLoading flag`() {
        val state = FeatureStore.State(isLoading = true)

        assertTrue(state.toUi().isLoading)
    }

    @Test
    fun `should map empty state correctly`() {
        val ui = FeatureStore.State().toUi()

        assertTrue(ui.items.isEmpty())
        assertEquals(false, ui.isLoading)
        assertEquals(null, ui.error)
    }
}
```

---

## 5. Executor (через Store целиком)

Executor — это бизнес-логика поверх MviKotlin: реакция на `Intent`, вызов UseCase, диспатч `Msg`, публикация `Label`. Тестируется через собранный `Store` с реальным `DefaultStoreFactory`.

```kotlin
class FeatureStoreExecutorTest {

    private lateinit var getItemsUseCase: GetItemsUseCase
    private lateinit var feature: FeatureStoreFactory

    @BeforeTest
    fun setup() {
        getItemsUseCase = mockk()
        feature = FeatureStoreFactory(DefaultStoreFactory(), getItemsUseCase)
    }

    @Test
    fun `should call use case and emit items when Intent_Load`() = runTest {
        coEvery { getItemsUseCase(Unit) } returns Result.success(listOf(Item(1, "a")))
        val store = feature.create()

        store.accept(FeatureStore.Intent.Load)
        advanceUntilIdle()

        assertEquals(listOf(Item(1, "a")), store.state.items)
        assertEquals(false, store.state.isLoading)
        coVerify { getItemsUseCase(Unit) }
    }

    @Test
    fun `should publish NavigateToDetails label when Intent_ItemClicked`() = runTest {
        val store = feature.create()
        val labels = mutableListOf<FeatureStore.Label>()
        val job = launch { store.labels.toList(labels) }

        store.accept(FeatureStore.Intent.ItemClicked(42L))
        advanceUntilIdle()

        assertEquals(listOf(FeatureStore.Label.NavigateToDetails(42L)), labels)
        job.cancel()
    }
}
```

---

## 6. DataSource (если используется HttpClient)

Если в проекте появится сетевой DataSource — тестируем его через тестовый http-движок (`MockEngine` Ktor / `MockWebServer` для OkHttp). Сейчас в проекте сетевого слоя нет — это шаблон на будущее.

```kotlin
class AuthDataSourceImplTest {

    @Test
    fun `should send correct json when validatePhone called`() = runTest {
        val capturedBody = slot<ValidatePhoneRequest>()
        // ... через Ktor MockEngine или мок HttpClient
    }
}
```

---

## Шаблон тестового файла

```kotlin
package com.itapp.<module>.<layer>

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ClassUnderTestTest {

    private lateinit var dependency: Dependency
    private lateinit var sut: ClassUnderTest          // sut = system under test

    @BeforeTest
    fun setup() {
        dependency = mockk()
        sut = ClassUnderTest(dependency)
    }

    @Test
    fun `should <expected behavior> when <condition>`() = runTest {
        // Given
        coEvery { dependency.method(any()) } returns Unit

        // When
        sut.action()

        // Then
        coVerify { dependency.method(any()) }
    }
}
```
