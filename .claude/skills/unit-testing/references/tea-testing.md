# Тестирование TEA-стора

TEA-стор объединяет State / Intent / Effect / Event / Reducer / Effector. Стратегия:

1. **Reducer** тестируем как чистую функцию (без корутин, без моков) — см. [patterns.md](patterns.md#3-reducer-dslreducer).
2. **Effector** тестируем отдельно с моками use case-ов — см. [patterns.md](patterns.md#5-effector-coroutineeffector).
3. **Целиком store** тестируем integration-стилем — реальный Reducer + замоканные use case-ы, проверяем `state` и `events` через Turbine.

---

## Тест целого стора (StoreFactory)

```kotlin
class PhoneValidationStoreTest {

    private lateinit var validatePhoneUseCase: ValidatePhoneNumberUseCase
    private lateinit var factory: PhoneValidationStoreFactory

    @BeforeTest
    fun setup() {
        validatePhoneUseCase = mock<ValidatePhoneNumberUseCase>()
        factory = PhoneValidationStoreFactory(validatePhoneUseCase)
    }

    @Test
    fun `should update phone when OnPhoneChanged accepted`() = runTest {
        val store = factory.create(this)

        store.accept(Intent.OnPhoneChanged("+79001234567"))

        assertEquals("+79001234567", store.state.value.phone)
    }

    @Test
    fun `should publish NavigateToPassword event when validation succeeds`() = runTest {
        everySuspend { validatePhoneUseCase(any()) } returns Result.success(Unit)
        val store = factory.create(this)

        store.events.test {
            store.accept(Intent.OnPhoneChanged("+79001234567"))
            store.accept(Intent.OnContinueClicked)

            assertEquals(Event.NavigateToPassword, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should publish ShowError event when validation fails`() = runTest {
        everySuspend { validatePhoneUseCase(any()) } returns Result.failure(RuntimeException("bad"))
        val store = factory.create(this)

        store.events.test {
            store.accept(Intent.OnPhoneChanged("+79001234567"))
            store.accept(Intent.OnContinueClicked)

            val event = awaitItem()
            assertTrue(event is Event.ShowError)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

---

## Проверка серии State через Turbine

```kotlin
@Test
fun `should toggle isLoading during validation`() = runTest {
    everySuspend { validatePhoneUseCase(any()) } returns Result.success(Unit)
    val store = factory.create(this)

    store.state.test {
        assertEquals(false, awaitItem().isLoading)        // initial

        store.accept(Intent.OnContinueClicked)

        assertEquals(true, awaitItem().isLoading)         // во время запроса
        assertEquals(false, awaitItem().isLoading)        // после

        cancelAndIgnoreRemainingEvents()
    }
}
```

---

## Особенности TEA в этом проекте

Из CLAUDE.md и существующего кода:

- `ReducerContext.state {}` возвращает `Unit`. Чтобы прочитать текущее значение перед изменением — обращайся к свойству `state` напрямую.
- Передавай данные в Effector через поля `Effect`, не читай state внутри Effector.
- StateFlow всегда имеет initial value — учитывай это при `awaitItem()` (первый вызов вернёт initial).

---

## Что не тестировать в TEA

- ❌ Что `StateFlow` хранит последнее значение — это контракт фреймворка.
- ❌ Что `Reducer` действительно вызывается — это контракт `core-architecture`.
- ✅ Только то, что **твоя** логика преобразует Intent в правильный State и Effect.
