# Паттерны тестов по слоям

Все примеры — для KMP-модуля, source set `commonTest`. Использование Mokkery + `kotlinx-coroutines-test` + `kotlin.test`.

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
        repository = mock<AuthRepository>()
        useCase = ValidatePhoneNumberUseCaseImpl(repository)
    }

    @Test
    fun `should call repository validatePhone with correct dto`() = runTest {
        // Given
        val dto = ValidationPhoneDto("+79001234567", "password123")
        everySuspend { repository.validatePhone(any()) } returns Unit

        // When
        useCase.run(ValidatePhoneNumberUseCase.Params(dto))

        // Then
        verifySuspend { repository.validatePhone(eq(dto)) }
    }

    @Test
    fun `should return success when repository succeeds`() = runTest {
        val dto = ValidationPhoneDto("+79001234567", "password123")
        everySuspend { repository.validatePhone(any()) } returns Unit

        val result = useCase(ValidatePhoneNumberUseCase.Params(dto))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return failure when repository throws`() = runTest {
        val dto = ValidationPhoneDto("+79001234567", "password123")
        everySuspend { repository.validatePhone(any()) } throws RuntimeException("Validation failed")

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
        dataSource = mock<AuthDataSource>()
        repository = AuthRepositoryImpl(dataSource)
    }

    @Test
    fun `should call dataSource with mapped request when validatePhone called`() = runTest {
        // Given
        val captor = Capture.slot<ValidatePhoneRequest>()
        everySuspend { dataSource.validatePhone(capture(captor)) } returns Unit
        val dto = ValidationPhoneDto("+79001234567", "password123")

        // When
        repository.validatePhone(dto)

        // Then
        assertEquals("+79001234567", captor.value.phoneNumber)
        assertEquals("password123", captor.value.password)
    }

    @Test
    fun `should propagate exception when dataSource throws`() = runTest {
        everySuspend { dataSource.validatePhone(any()) } throws RuntimeException("Network error")

        val exception = assertFailsWith<RuntimeException> {
            repository.validatePhone(ValidationPhoneDto("+79001234567", "p"))
        }

        assertEquals("Network error", exception.message)
    }
}
```

---

## 3. Reducer (DslReducer)

Reducer — чистая функция. Не нужны корутины, не нужны моки. Тестируем:

- Каждый Intent → ожидаемый State
- Каждый Intent → ожидаемые Effects (или их отсутствие)

```kotlin
class PhoneValidationReducerTest {

    private val reducer = PhoneValidationReducer()

    private fun reduce(state: State, intent: Intent): ReduceResult<State, Effect> {
        return reducer(state, intent)  // адаптируй под реальный API проекта
    }

    @Test
    fun `should update phone in state when OnPhoneChanged dispatched`() {
        val initial = State(phone = "")

        val (newState, effects) = reduce(initial, Intent.OnPhoneChanged("+79001234567"))

        assertEquals("+79001234567", newState.phone)
        assertTrue(effects.isEmpty())
    }

    @Test
    fun `should emit ValidatePhone effect when OnContinueClicked with valid phone`() {
        val initial = State(phone = "+79001234567")

        val (_, effects) = reduce(initial, Intent.OnContinueClicked)

        assertEquals(listOf(Effect.ValidatePhone("+79001234567")), effects)
    }

    @Test
    fun `should not emit effects when OnContinueClicked with empty phone`() {
        val initial = State(phone = "")

        val (newState, effects) = reduce(initial, Intent.OnContinueClicked)

        assertEquals(initial, newState)
        assertTrue(effects.isEmpty())
    }
}
```

> Если в проекте Reducer возвращает `Unit` через DSL и пишет state/effects через контекст — адаптируй тестовый хелпер под актуальный API из `core-architecture`.

---

## 4. Mapping (State → UiState, DTO → Domain)

Чистые функции — мокать нечего. Покрывай все ветки.

```kotlin
class PhoneValidationStateMappingTest {

    @Test
    fun `should map phone correctly when toUi called`() {
        val state = State(phone = "+79001234567")

        val uiState = state.toUi()

        assertEquals("+79001234567", uiState.phone)
    }

    @Test
    fun `should map empty phone when toUi called with empty state`() {
        assertEquals("", State(phone = "").toUi().phone)
    }

    @Test
    fun `should preserve isLoading flag in ui state`() {
        val state = State(phone = "+79001234567", isLoading = true)

        assertTrue(state.toUi().isLoading)
    }
}
```

---

## 5. Effector (CoroutineEffector)

Effector принимает Effect, делает побочный эффект (вызов use case), может dispatch Intent / publish Event.

```kotlin
class PhoneValidationEffectorTest {

    private lateinit var validatePhoneUseCase: ValidatePhoneNumberUseCase
    private lateinit var effector: PhoneValidationEffector

    @BeforeTest
    fun setup() {
        validatePhoneUseCase = mock<ValidatePhoneNumberUseCase>()
        effector = PhoneValidationEffector(validatePhoneUseCase)
    }

    @Test
    fun `should dispatch ValidationSucceeded when use case returns success`() = runTest {
        // Given
        everySuspend { validatePhoneUseCase(any()) } returns Result.success(Unit)
        val dispatched = mutableListOf<Intent>()
        val published = mutableListOf<Event>()

        // When
        with(effector) {
            EffectorScope(
                dispatch = { dispatched.add(it) },
                publish = { published.add(it) }
            ).handle(Effect.ValidatePhone("+79001234567"))
        }

        // Then
        assertEquals(listOf(Intent.ValidationSucceeded), dispatched)
        assertTrue(published.isEmpty())
    }

    @Test
    fun `should publish ShowError event when use case returns failure`() = runTest {
        everySuspend { validatePhoneUseCase(any()) } returns Result.failure(RuntimeException("e"))
        // ... аналогично
    }
}
```

> Точная подпись Effector / EffectorScope — смотри `core-architecture`. Если API сложный — оборачивай вызов в фабрику `createEffector(...)` и тестируй её результат.

---

## 6. DataSource (Ktor MockEngine)

DataSource напрямую работает с `HttpClient`. Используй `MockEngine` для тестирования сериализации запроса и парсинга ответа.

```kotlin
class AuthDataSourceImplTest {

    @Test
    fun `should send correct json when validatePhone called`() = runTest {
        // Given
        var capturedBody: String? = null
        val mockEngine = MockEngine { request ->
            capturedBody = (request.body as TextContent).text
            respond("", HttpStatusCode.OK)
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
        }
        val dataSource = AuthDataSourceImpl(client)

        // When
        dataSource.validatePhone(ValidatePhoneRequest("+79001234567", "p"))

        // Then
        assertTrue(capturedBody!!.contains("+79001234567"))
        assertTrue(capturedBody!!.contains("password"))
    }

    @Test
    fun `should throw when server returns 500`() = runTest {
        val mockEngine = MockEngine { respond("", HttpStatusCode.InternalServerError) }
        val client = HttpClient(mockEngine)
        val dataSource = AuthDataSourceImpl(client)

        assertFailsWith<ServerResponseException> {
            dataSource.validatePhone(ValidatePhoneRequest("+79001234567", "p"))
        }
    }
}
```

---

## Шаблон тестового файла

```kotlin
package com.itapp.<module>.<layer>

import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import dev.mokkery.answering.returns
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
        dependency = mock<Dependency>()
        sut = ClassUnderTest(dependency)
    }

    @Test
    fun `should <expected behavior> when <condition>`() = runTest {
        // Given
        everySuspend { dependency.method(any()) } returns Unit

        // When
        sut.action()

        // Then
        verifySuspend { dependency.method(any()) }
    }
}
```
