# MockK API — основной справочник

## Создание мока

```kotlin
import io.mockk.mockk

val repo: AuthRepository = mockk()                    // strict: нестабленный вызов кинет MockKException
val repoRelaxed: AuthRepository = mockk(relaxed = true)
val repoUnit: AuthRepository = mockk(relaxUnitFun = true)
```

| Опция | Поведение |
|-------|-----------|
| `mockk()` (default) | Strict: любой нестабленный вызов → `MockKException`. Самый строгий и предпочтительный режим. |
| `mockk(relaxed = true)` | Возвращает дефолтные значения (null / 0 / `""` / пустая коллекция) для нестабленных вызовов. Использовать редко — затирает баги. |
| `mockk(relaxUnitFun = true)` | Только Unit-функции возвращают `Unit` без стаба. Хороший компромисс, если у мока много `suspend fun ...: Unit`. |

## Стабинг — `every` / `coEvery`

```kotlin
import io.mockk.every
import io.mockk.coEvery

every { repo.getCachedPhone() } returns "+79001234567"
coEvery { repo.validatePhone(any()) } returns Unit
coEvery { repo.fetchUser(any()) } throws RuntimeException("network")
```

- `every` — для обычных функций.
- `coEvery` — для `suspend`-функций.

## Динамический ответ — `answers`

```kotlin
every { repo.format(any()) } answers {
    val input = firstArg<String>()
    "formatted: $input"
}

coEvery { repo.fetchById(any()) } answers {
    val id = firstArg<Int>()
    if (id < 0) throw IllegalArgumentException()
    User(id, "name")
}
```

## Sequential answers

```kotlin
coEvery { repo.poll() } returnsMany listOf("a", "b", "c")
// 1-й вызов — "a", 2-й — "b", 3-й — "c", 4-й — снова "c"

coEvery { repo.poll() }.returnsMany("a", "b") andThenThrows RuntimeException("end")
```

## Matchers (`io.mockk` — встроенные)

```kotlin
import io.mockk.coEvery

coEvery { repo.find(any()) } returns user                           // любой аргумент
coEvery { repo.find(eq("123")) } returns user                       // ровно "123"
coEvery { repo.find(match { it.startsWith("+") }) } returns user    // условие
coEvery { repo.find(neq("0")) } returns user                        // не "0"
coEvery { repo.find(more(10)) } returns user                        // > 10
coEvery { repo.find(less(10)) } returns user                        // < 10
coEvery { repo.find(or(eq("a"), eq("b"))) } returns user
```

## Verify — `verify` / `coVerify`

```kotlin
import io.mockk.verify
import io.mockk.coVerify

coVerify { repo.validatePhone(any()) }                       // хотя бы 1 вызов
coVerify(exactly = 2) { repo.validatePhone(any()) }
coVerify(atLeast = 1) { repo.validatePhone(any()) }
coVerify(atMost = 3) { repo.validatePhone(any()) }
coVerify(exactly = 0) { repo.validatePhone(any()) }          // НЕ вызывался
```

## Verify order

```kotlin
import io.mockk.coVerifyOrder
import io.mockk.coVerifySequence

// Порядок важен, но между могут быть другие вызовы
coVerifyOrder {
    repo.fetchUser(any())
    repo.saveUser(any())
}

// Точная последовательность всех вызовов на моке
coVerifySequence {
    repo.fetchUser(any())
    repo.saveUser(any())
}
```

## Захват аргументов — `slot()` / `capture()`

```kotlin
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.slot

val captured = slot<ValidationPhoneDto>()
coEvery { repo.validatePhone(capture(captured)) } returns Unit

useCase(params)

assertEquals("+79001234567", captured.captured.phoneNumber)
```

Множественный захват:

```kotlin
import io.mockk.mutableListOf
import io.mockk.slot

val all = mutableListOf<ValidationPhoneDto>()
coEvery { repo.validatePhone(capture(all)) } returns Unit

useCase(p1)
useCase(p2)

assertEquals(2, all.size)
assertEquals(p1.dto, all[0])
```

## Reset / Clear

```kotlin
import io.mockk.clearMocks
import io.mockk.unmockkAll

@AfterTest
fun tearDown() {
    clearMocks(repo)            // сбрасывает поведение и историю вызовов на конкретном моке
    // unmockkAll()              // глобальный сброс — обычно не нужен, мы создаём свежий mockk() в @BeforeTest
}
```

Обычно в `@BeforeTest` создаётся свежий `mockk()` — и чистка не нужна.

## Шпаргалка по импортам

```kotlin
import io.mockk.mockk
import io.mockk.every
import io.mockk.coEvery
import io.mockk.verify
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.coVerifySequence
import io.mockk.slot
import io.mockk.clearMocks
// matchers — встроены в DSL, отдельных импортов не требуют:
//   any(), eq(), neq(), match { ... }, more(), less(), or(), and(), not()
```
