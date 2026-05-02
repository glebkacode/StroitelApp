# Тестирование корутин и Flow

В проекте используется `kotlinx-coroutines-test`. Все асинхронные тесты — через `runTest`.

---

## Базовый шаблон

```kotlin
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@Test
fun `should fetch user`() = runTest {
    val result = useCase.fetch()
    assertEquals(expected, result)
}
```

`runTest` создаёт `TestScope` с `StandardTestDispatcher`. Виртуальное время продвигается явно.

---

## NEVER: запрет на runBlocking

```kotlin
// ❌ КАТЕГОРИЧЕСКИ НЕТ
@Test
fun bad() = runBlocking { ... }

// ✅ ДА
@Test
fun good() = runTest { ... }
```

`runBlocking` блокирует реальный поток. `delay(10_000)` в тесте превратится в 10 реальных секунд. `runTest` использует виртуальное время — `delay` мгновенно.

---

## TestDispatcher

Если SUT принимает `CoroutineDispatcher`, инжектируй `StandardTestDispatcher` из тестового scheduler:

```kotlin
@Test
fun `should load on io dispatcher`() = runTest {
    val sut = SomeViewModel(
        repository = repository,
        ioDispatcher = StandardTestDispatcher(testScheduler)
    )

    sut.load()
    advanceUntilIdle()  // прокручиваем виртуальное время

    assertEquals(expected, sut.state.value)
}
```

| Команда | Что делает |
|---------|------------|
| `advanceUntilIdle()` | Выполнить все ожидающие корутины. |
| `advanceTimeBy(ms)` | Продвинуть виртуальное время на N мс. |
| `runCurrent()` | Выполнить корутины, готовые к запуску прямо сейчас. |

---

## Тестирование Flow без Turbine

Сейчас Turbine в проекте не подключён. Простые случаи покрываются вручную через `toList()` и `runTest`:

```kotlin
@Test
fun `should emit values in order`() = runTest {
    val emitted = mutableListOf<List<Product>>()
    val job = launch { repository.observeProducts().toList(emitted) }

    repository.addProduct(testProduct)
    advanceUntilIdle()

    assertEquals(emptyList(), emitted[0])
    assertEquals(listOf(testProduct), emitted[1])

    job.cancel()
}
```

Для одиночных `StateFlow` достаточно `flow.first()` или `flow.value`.

> Если Flow-тестов станет много — можно подключить `app.cash.turbine:turbine` (`testImplementation`). Но это не дефолт стека.

---

## StateFlow — учитывай initial value

`StateFlow` всегда отдаёт текущее значение первым подписчикам:

```kotlin
@Test
fun `should emit initial state then update`() = runTest {
    val sut = createSut()
    val states = mutableListOf<State>()
    val job = launch { sut.state.toList(states) }

    sut.accept(Intent.Load)
    advanceUntilIdle()

    assertEquals(State.initial, states[0])    // ← initial всегда первый
    assertEquals(State.loading, states[1])

    job.cancel()
}
```

---

## Тестирование таймаутов / delay

```kotlin
@Test
fun `should retry after 5 seconds`() = runTest {
    val sut = createSut()

    sut.startWithRetry()

    advanceTimeBy(4_999)
    coVerify(exactly = 1) { repository.fetch() }

    advanceTimeBy(2)  // 5_001 мс — должен сработать retry
    coVerify(exactly = 2) { repository.fetch() }
}
```

---

## Несколько диспетчеров — `Dispatchers.setMain`

Только если SUT использует `Dispatchers.Main` напрямую (а не инжектит):

```kotlin
@BeforeTest
fun setup() {
    Dispatchers.setMain(StandardTestDispatcher())
}

@AfterTest
fun tearDown() {
    Dispatchers.resetMain()
}
```

> Лучше — инжектируй диспетчер через конструктор. Тогда `setMain` не нужен.

---

## Подключение coroutines-test

Уже подключено в `gradle/libs.versions.toml`:

```toml
kotlin-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "kotlinx-coroutines" }
```

В `build.gradle.kts` модуля:

```kotlin
dependencies {
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
}
```
