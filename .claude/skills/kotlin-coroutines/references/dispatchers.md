# Диспатчеры

| Диспетчер | Когда использовать |
|-----------|-------------------|
| `Dispatchers.Main` | UI операции, обновление состояния |
| `Dispatchers.IO` | Сеть, база данных, файлы |
| `Dispatchers.Default` | CPU-intensive: сортировка, парсинг, вычисления |
| `Dispatchers.Unconfined` | Почти никогда (только для тестов) |
```kotlin
// Инъекция диспетчеров для тестируемости
class MyRepository(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun fetchData() = withContext(ioDispatcher) {
        // ...
    }
}

// В тестах
val testDispatcher = StandardTestDispatcher()
val repository = MyRepository(ioDispatcher = testDispatcher)
```

---
