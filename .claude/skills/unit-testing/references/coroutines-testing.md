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

## Тестирование Flow с Turbine

Turbine — стандартный инструмент проекта для тестирования `Flow`.

```kotlin
import app.cash.turbine.test

@Test
fun `should emit values in order`() = runTest {
    repository.observeProducts().test {
        assertEquals(emptyList(), awaitItem())

        repository.addProduct(testProduct)
        assertEquals(listOf(testProduct), awaitItem())

        cancelAndIgnoreRemainingEvents()
    }
}
```

Подключение:

```kotlin
// build.gradle.kts
kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation("app.cash.turbine:turbine:1.1.0")
        }
    }
}
```

### Полезные методы Turbine

| Метод | Назначение |
|-------|------------|
| `awaitItem()` | Ждать следующий элемент. |
| `awaitComplete()` | Ждать завершение flow. |
| `awaitError()` | Ждать выброс исключения. |
| `expectNoEvents()` | Утверждать, что нет неожиданных эмитов. |
| `cancelAndIgnoreRemainingEvents()` | Завершить тест без проверки хвоста. |
| `skipItems(n)` | Пропустить N элементов. |

---

## StateFlow — учитывай initial value

`StateFlow` всегда отдаёт текущее значение первым подписчикам:

```kotlin
@Test
fun `should emit initial state then update`() = runTest {
    val sut = createSut()

    sut.state.test {
        assertEquals(State.initial, awaitItem())   // ← initial всегда первый

        sut.accept(Intent.Load)

        assertEquals(State.loading, awaitItem())
        cancelAndIgnoreRemainingEvents()
    }
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
    verifySuspend(VerifyMode.exactly(1)) { repository.fetch() }

    advanceTimeBy(2)  // 5_001 мс — должен сработать retry
    verifySuspend(VerifyMode.exactly(2)) { repository.fetch() }
}
```

---

## Несколько диспетчеров — `Dispatchers.setMain` (только Android/JVM)

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

> В KMP `commonTest` это недоступно. Лучше — инжектируй диспетчер через конструктор.

---

## Подключение coroutines-test

```toml
# libs.versions.toml — уже есть
kotlin-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "kotlinx-coroutines" }
```

```kotlin
// build.gradle.kts
commonTest.dependencies {
    implementation(libs.kotlin.coroutines.test)
    implementation(libs.kotlin.test)
}
```
