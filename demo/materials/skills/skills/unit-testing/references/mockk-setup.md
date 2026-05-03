# MockK — подключение в Android-модуль

В проекте StroitelApp моки делаются исключительно через **MockK** (`io.mockk:mockk`). Зависимость подключается на уровне `testImplementation` в каждом модуле, который содержит unit-тесты.

## Шаг 1. Версия в `gradle/libs.versions.toml`

```toml
[versions]
mockk = "1.13.13"

[libraries]
mockk = { module = "io.mockk:mockk", version.ref = "mockk" }
```

(Уже подключено в проекте — если делаешь новый модуль, ничего трогать здесь не надо.)

## Шаг 2. Подключение в модуле

В `feature-impl/build.gradle.kts` модуля, где есть `src/test/java`:

```kotlin
dependencies {
    // ...
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockk)
}
```

После добавления — синкни Gradle и MockK будет доступен в `src/test/java/`.

## Шаг 3. Базовый тест на проверку

```kotlin
// src/test/java/com/itapp/<feature>_impl/SmokeMockkTest.kt
package com.itapp.<feature>_impl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SmokeMockkTest {

    interface Dep {
        suspend fun fetch(id: Int): String
    }

    @Test
    fun `mockk works`() = runTest {
        val dep: Dep = mockk()
        coEvery { dep.fetch(any()) } returns "ok"

        val result = dep.fetch(1)

        assertEquals("ok", result)
        coVerify { dep.fetch(1) }
    }
}
```

Если `./gradlew :<module>:testDebugUnitTest` даёт `BUILD SUCCESSFUL` — MockK подключён корректно.

## Замечания

- **Не нужно** включать какой-либо плагин компилятора (как было у Mokkery в KMP) — MockK работает через рантайм-моки на JVM, и для Android `src/test` это работает «из коробки».
- **Не используй** `mockk-android` — это для instrumented-тестов на устройстве (`src/androidTest`). Для обычных unit-тестов в `src/test` всегда используй обычный `io.mockk:mockk`.
- **Final-классы**: MockK по умолчанию умеет мокать финальные классы (включает в себя `mockk-agent-jvm`). Никакой дополнительной настройки не требуется.
