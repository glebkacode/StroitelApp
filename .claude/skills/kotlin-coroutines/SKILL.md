---
name: kotlin-coroutines
description: Экспертиза по работе с Kotlin Coroutines и Flow. Используй при написании асинхронного кода, работе с потоками данных, обработке ошибок в корутинах, интеграции с MviKotlin Store / Decompose Component, оптимизации производительности многопоточности или ревью кода с корутинами.
---

# Kotlin Coroutines и Flow

Паттерны structured concurrency, реактивных потоков на Flow и тестирования корутин в Android-проекте.

## Когда активировать

- Написание асинхронного кода на Kotlin Coroutines
- Использование Flow, StateFlow или SharedFlow для реактивных данных
- Конкурентные операции (параллельная загрузка, debounce, retry)
- Тестирование корутин и Flow
- Управление scope'ами корутин и отменой

# Работа со skill
- Для понимания принципов как работать с kotlin coroutines в проекте, смотри [principles.md](references/principles.md)
- Для понимания как работать с Dispatchers в проекте, смотри в [dispatchers.md](references/dispatchers.md)
- Для понимания как обрабатывать ошибки внутри проекта, смотри в [error-handling.md](references/error-handling.md)
- Для понимания как работать с Kotlin Flow в проекте, смотри в [flows.md](references/flows.md)
- Для понимания как писать юнит-тесты на код который написан с помощью корутин, смотри в [testing.md](references/testing.md)
- Для понимания как обрабатывать отмену корутины, смотри в [cancellation.md](references/cancellation.md)
- Для понимания как делать НЕ НАДО, смотри в [antipatterns.md](references/antipatterns.md)