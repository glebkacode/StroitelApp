# Антипаттерны — чего нельзя делать в тестах

## 1. ❌ runBlocking вместо runTest

```kotlin
// ❌
@Test
fun bad() = runBlocking { useCase.fetch() }

// ✅
@Test
fun good() = runTest { useCase.fetch() }
```

**Почему:** `runBlocking` блокирует поток, `delay` работает реальным временем. `runTest` — виртуальное время.

---

## 2. ❌ Thread.sleep в тестах

```kotlin
// ❌
sut.startTimer()
Thread.sleep(1000)
assertEquals(expected, sut.state.value)

// ✅
sut.startTimer()
advanceTimeBy(1000)
runCurrent()
assertEquals(expected, sut.state.value)
```

**Почему:** реальный sleep — 1000 мс задержки в CI. Виртуальное время — мгновенно.

---

## 3. ❌ Мок собственной модели данных

```kotlin
// ❌
val user = mock<User>()
every { user.name } returns "Иван"

// ✅
val user = User(id = 1, name = "Иван")
```

**Почему:** `data class` — это значение. Создавай напрямую, не мокай. Моки — для **поведения** (интерфейсов с методами), не для **данных**.

---

## 4. ❌ Несколько ассертов на разные поведения в одном тесте

```kotlin
// ❌
@Test
fun `everything works`() = runTest {
    val r1 = useCase.fetch(1)
    assertTrue(r1.isSuccess)

    val r2 = useCase.fetch(-1)
    assertTrue(r2.isFailure)

    val r3 = useCase.fetch(Int.MAX_VALUE)
    assertEquals(...)
}

// ✅ — три отдельных теста
@Test fun `should return success for positive id`()
@Test fun `should return failure for negative id`()
@Test fun `should handle Int MAX_VALUE`()
```

---

## 5. ❌ Чтение текущего времени без инжекта Clock

```kotlin
// ❌
class TokenChecker {
    fun isExpired(token: Token): Boolean = token.expiresAt < Clock.System.now()
}

// ✅
class TokenChecker(private val clock: Clock) {
    fun isExpired(token: Token): Boolean = token.expiresAt < clock.now()
}
```

**Почему:** тесты с `Clock.System.now()` неcтабильны — зависят от текущего времени. Инжектируй `Clock`.

---

## 6. ❌ Зависимость тестов от порядка запуска

```kotlin
// ❌
class BadTest {
    companion object {
        var sharedState = 0
    }

    @Test fun first() { sharedState = 1 }
    @Test fun second() { assertEquals(1, sharedState) }  // зависит от first()
}
```

**Почему:** JUnit не гарантирует порядок. Каждый тест — изолирован. Инициализируй всё в `@BeforeTest`.

---

## 7. ❌ Логика в тесте

```kotlin
// ❌
@Test
fun `should map all items`() {
    val input = (1..100).map { TestData.item(id = it) }
    val output = mapper.map(input)
    output.forEachIndexed { i, item ->
        assertEquals(input[i].id * 2, item.id)
    }
}

// ✅
@Test
fun `should map item id by doubling`() {
    val input = listOf(TestData.item(id = 5))
    val output = mapper.map(input)
    assertEquals(10, output[0].id)
}
```

**Почему:** циклы / условия в тесте могут быть сами багаваты. Тест должен быть прямой проверкой "вход → выход".

---

## 8. ❌ Завышенно-широкий verify

```kotlin
// ❌
verifySuspend { repo.fetch(any()) }

// ✅ — проверяй конкретное значение, если известно
verifySuspend { repo.fetch(eq("expected_id")) }

// ✅ — или захвати и ассертни
val captor = Capture.slot<String>()
everySuspend { repo.fetch(capture(captor)) } returns user
useCase.run("expected_id")
assertEquals("expected_id", captor.value)
```

**Почему:** `any()` пропустит передачу любого мусора. Если знаешь что должно прийти — проверяй.

---

## 9. ❌ Тестирование getter / setter / toString

```kotlin
// ❌
@Test
fun `data class copy works`() {
    val a = User(1, "x")
    val b = a.copy(name = "y")
    assertEquals("y", b.name)
}
```

**Почему:** ты тестируешь Kotlin, а не свой код.

---

## 10. ❌ Слишком много моков в одном тесте

Если в тесте 5+ моков — класс делает слишком много. Это сигнал к разбиению на меньшие единицы или интеграционному тесту.

---

## 11. ❌ Verify без необходимости

```kotlin
// ❌ — verify дублирует ассерт результата
val result = useCase("123")
assertTrue(result.isSuccess)
verifySuspend { repository.fetch(any()) }  // и так понятно из result

// ✅ — verify когда ничего больше не наблюдаемо
useCase.fireAndForget("123")
verifySuspend { repository.log(eq("123")) }
```

Правило: **проверяй результат, если он есть; verify — только если результата нет.**

---

## 12. ❌ Хардкод константы вместо общих фабрик

```kotlin
// ❌ — повтор в каждом тесте
val dto = ValidationPhoneDto("+79001234567", "password123")

// ✅ — testdata-фабрика
object AuthTestData {
    fun validationDto(
        phone: String = "+79001234567",
        password: String = "password123"
    ) = ValidationPhoneDto(phone, password)
}

// в тесте
val dto = AuthTestData.validationDto()
val customPhone = AuthTestData.validationDto(phone = "+79999999999")
```

---

## 13. ❌ Прокидывание реального HTTP-клиента

```kotlin
// ❌
val client = HttpClient(OkHttp)
val dataSource = AuthDataSourceImpl(client)

// ✅
val mockEngine = MockEngine { respond("{\"ok\":true}", HttpStatusCode.OK) }
val client = HttpClient(mockEngine)
val dataSource = AuthDataSourceImpl(client)
```

**Почему:** реальные сети — flaky, медленные, требуют интернет. Только `MockEngine`.

---

## 14. ❌ println для отладки в коммите

```kotlin
// ❌
@Test
fun debug() = runTest {
    val result = sut.run()
    println(result)            // нет!
    assertEquals(expected, result)
}
```

Удаляй `println` перед коммитом. Тест либо валидирует сам себя, либо это не тест.

---

## 15. ❌ Игнор failing-теста через @Ignore без причины

```kotlin
// ❌
@Test
@Ignore
fun `should validate`() { ... }
```

Если тест падает — почини или удали. `@Ignore` — это лежащий мёртвым код. Допустимо только с комментарием-ссылкой на тикет и сроком.

---

## 16. ❌ Излишний MockMode.autofill

`autofill` маскирует "забыл застабить метод X". Используй `strict` (default) — пусть тест падает на нестабленном вызове.
