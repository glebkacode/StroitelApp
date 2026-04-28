# Mokkery API — основной справочник

## Создание мока

```kotlin
import dev.mokkery.mock
import dev.mokkery.MockMode

val repo: AuthRepository = mock<AuthRepository>()
val repoStrict: AuthRepository = mock<AuthRepository>(MockMode.strict)
val repoAutofill: AuthRepository = mock<AuthRepository>(MockMode.autofill)
```

| MockMode | Поведение |
|----------|-----------|
| `strict` (default) | Любой нестабленный вызов кидает `MockingException`. |
| `autofill` | Возвращает дефолтные значения (null / 0 / emptyList) для нестабленных. |
| `autoUnit` | Как `strict`, но методы с `Unit` возвращают `Unit` без стаба. |
| `original` | Если у мока есть super-метод — вызывает его. |

## Стабинг — every / everySuspend

```kotlin
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws

every { repo.getCachedPhone() } returns "+79001234567"
everySuspend { repo.validatePhone(any()) } returns Unit
everySuspend { repo.fetchUser(any()) } throws RuntimeException("network")
```

> Используй `everySuspend` для `suspend`-функций, `every` — для обычных.

## Динамический ответ — calls / answers

```kotlin
import dev.mokkery.answering.calls

every { repo.format(any()) } calls { (input: String) ->
    "formatted: $input"
}

everySuspend { repo.fetchById(any()) } calls { (id: Int) ->
    if (id < 0) throw IllegalArgumentException()
    User(id, "name")
}
```

## Sequential answers

```kotlin
everySuspend { repo.poll() } returnsMany listOf("a", "b", "c")
// 1-й вызов — "a", 2-й — "b", 3-й — "c", 4-й — снова "c" (или ошибка в strict)
```

## Matchers (dev.mokkery.matcher.*)

```kotlin
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.matcher.matching

everySuspend { repo.find(any()) } returns user           // любой аргумент
everySuspend { repo.find(eq("123")) } returns user       // только "123"
everySuspend { repo.find(matching { it.startsWith("+") }) } returns user
```

## Verify — verify / verifySuspend

```kotlin
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import dev.mokkery.verify.VerifyMode

verifySuspend { repo.validatePhone(any()) }                  // ровно 1 вызов (default exactly(1))
verifySuspend(VerifyMode.exactly(2)) { repo.validatePhone(any()) }
verifySuspend(VerifyMode.atLeast(1)) { repo.validatePhone(any()) }
verifySuspend(VerifyMode.atMost(3)) { repo.validatePhone(any()) }
verifySuspend(VerifyMode.not) { repo.validatePhone(any()) }  // НЕ вызывался
```

## Verify order

```kotlin
import dev.mokkery.verifySuspend
import dev.mokkery.verify.VerifyMode.order

verifySuspend(order) {
    repo.fetchUser(any())
    repo.saveUser(any())
}
```

## Захват аргументов (capture)

```kotlin
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture

val captor = Capture.slot<ValidationPhoneDto>()
everySuspend { repo.validatePhone(capture(captor)) } returns Unit

useCase(params)

assertEquals("+79001234567", captor.value.phoneNumber)
```

Множественный захват:

```kotlin
val captor = Capture.all<ValidationPhoneDto>()
everySuspend { repo.validatePhone(capture(captor)) } returns Unit

useCase(p1)
useCase(p2)

assertEquals(2, captor.values.size)
assertEquals(p1.dto, captor.values[0])
```

## Reset

```kotlin
import dev.mokkery.resetAnswers
import dev.mokkery.resetCalls

@AfterTest
fun tearDown() {
    resetAnswers(repo)   // сбрасывает стабы
    resetCalls(repo)     // сбрасывает историю вызовов
}
```

Обычно в `@BeforeTest` создаётся свежий `mock<>()` — и чистка не нужна.

## Шпаргалка по импортам

```kotlin
import dev.mokkery.mock
import dev.mokkery.MockMode
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import dev.mokkery.answering.returns
import dev.mokkery.answering.returnsMany
import dev.mokkery.answering.throws
import dev.mokkery.answering.calls
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.matcher.matching
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.verify.VerifyMode
```
