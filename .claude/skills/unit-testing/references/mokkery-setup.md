# Подключение Mokkery к KMP-модулю

[Mokkery](https://mokkery.dev/) — KMP-friendly mocking-библиотека для Kotlin. Работает через compiler-plugin, поддерживает все таргеты (Android / iOS / JVM / JS / WASM).

## 1. Добавь плагин Mokkery в version catalog

`gradle/libs.versions.toml`:

```toml
[versions]
mokkery = "2.10.0"

[plugins]
mokkery = { id = "dev.mokkery", version.ref = "mokkery" }
```

> ⚠️ Перед коммитом — проверь актуальную версию на https://mokkery.dev/docs/Setup/.

## 2. Подключи плагин в build.gradle.kts модуля

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.mokkery)   // ← добавляем
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.coroutines.test)
            // Mokkery подтягивается транзитивно через плагин — отдельная зависимость не нужна
        }
    }
}
```

## 3. Опционально — настрой режим mock для финальных классов

По умолчанию Mokkery мокает только интерфейсы и `open class`. Для финальных классов добавь:

```kotlin
mokkery {
    defaultMockMode.set(dev.mokkery.MockMode.autofill)
    rule.set(dev.mokkery.gradle.ApplicationRule.All)  // мокать все классы, включая final
}
```

Используй с осторожностью — мокание финальных классов часто признак плохого дизайна. Лучше выделить интерфейс.

## 4. Проверь что всё работает

```bash
./gradlew :auth-impl:testAndroidHostTest
```

В тесте:

```kotlin
import dev.mokkery.mock
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import dev.mokkery.matcher.any
```

## 5. Kover — учитывай при excludes

В Kover-конфигурации в `build-logic/.../KoverConventions.kt` уже исключены `*Graph*`, `*Factory*`, `*.di`. Для Mokkery дополнительно ничего не нужно — сгенерированные mock-классы существуют только в test source set и не влияют на покрытие.

## 6. Fake-классы запрещены

В проекте действует правило: все unit-тесты используют только Mokkery. Создавать `Fake*` классы (как `FakeAuthRepository`) нельзя — даже для простых случаев. Если встретил существующий Fake — мигрируй его на `mock<Interface>()` + `everySuspend` / `verifySuspend` и удали.
