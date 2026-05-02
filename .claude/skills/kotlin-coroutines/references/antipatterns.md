# Анти-паттерны

## ❌ GlobalScope
```kotlin
// Неправильно: утечка корутины
GlobalScope.launch {
    repository.syncData()
}

// Правильно: привязка к lifecycle
componentScope.launch {
    repository.syncData()
}
```

## ❌ Проглатывание CancellationException
```kotlin
// Неправильно
try {
    suspendFunction()
} catch (e: Exception) {
    log(e)  // проглотили CancellationException!
}

// Правильно
try {
    suspendFunction()
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    log(e)
}
```

## ❌ Блокирующий код в suspend функции
```kotlin
// Неправильно
suspend fun readFile(): String {
    return File("path").readText()  // блокирует поток!
}

// Правильно
suspend fun readFile(): String = withContext(Dispatchers.IO) {
    File("path").readText()
}
```

## ❌ collect в launch без обработки ошибок
```kotlin
// Неправильно: краш при ошибке
componentScope.launch {
    flow.collect { }
}

// Правильно
componentScope.launch {
    flow
        .catch { e -> handleError(e) }
        .collect { }
}
```

## ❌ Создание Flow в Composable
```kotlin
// Неправильно: новый Flow при каждой рекомпозиции
@Composable
fun Screen(component: ProductsComponent) {
    val state by component.getProducts().collectAsState(emptyList())
}

// Правильно: Flow как свойство
@Composable
fun Screen(component: ProductsComponent) {
    val state by component.products.collectAsStateWithLifecycle()
}
```

---