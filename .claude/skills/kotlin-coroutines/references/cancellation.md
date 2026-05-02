# Отмена

## Проверка отмены
Длительные циклы необходимо проверять на отмену:
```kotlin
suspend fun processItems(items: List) {
    items.forEach { item ->
        ensureActive()  // проверяет отмену, бросает CancellationException
        processItem(item)
    }
}

// Или через isActive
suspend fun processItems(items: List) = coroutineScope {
    items.forEach { item ->
        if (!isActive) return@coroutineScope
        processItem(item)
    }
}
```

## Очистка ресурсов при отмене
```kotlin
suspend fun readFile(path: String): String {
    val file = File(path).bufferedReader()
    try {
        return file.readText()
    } finally {
        withContext(NonCancellable) {
            file.close()  // выполнится даже при отмене
        }
    }
}
```

## Timeout
```kotlin
val result = withTimeoutOrNull(5.seconds) {
    repository.fetchData()
} ?: defaultValue

// Или с исключением
try {
    withTimeout(5.seconds) {
        repository.fetchData()
    }
} catch (e: TimeoutCancellationException) {
    // обработка таймаута
}
```

---