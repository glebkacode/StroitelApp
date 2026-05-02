# Основные принципы

## 1. Structured Concurrency
Корутины привязаны к scope и автоматически отменяются вместе с ним.
Всегда используй принцип structured concurrency — никогда GlobalScope.
```kotlin
// ✅ Правильно: корутина привязана к компоненту
class ProductComponent(componentContext: ComponentContext,) : BaseComponent(componentContext) {
    fun loadProducts() {
        componentScope.launch {
            // автоматически отменится при очистке ViewModel
        }
    }
}

// ❌ Неправильно: глобальный scope, утечка корутины
class ProductComponent(componentContext: ComponentContext,) : BaseComponent(componentContext) {
    fun loadProducts() {
        GlobalScope.launch {
            // никогда не отменится!
        }
    }
}
```

## 2. Не блокируй main thread
Тяжёлые операции выносятся на другой диспетчер.
```kotlin
// ✅ Правильно
suspend fun parseJson(json: String): Data = withContext(Dispatchers.Default) {
    // CPU-intensive работа
    parser.parse(json)
}

suspend fun loadFromDb(): List = withContext(Dispatchers.IO) {
    // блокирующий I/O
    database.getAll()
}

// ❌ Неправильно: блокировка main thread
suspend fun parseJson(json: String): Data {
    return parser.parse(json)  // выполняется на вызывающем диспетчере
}
```

## 3. Suspend функции безопасны для вызова
Suspend функция сама отвечает за выбор диспетчера.
```kotlin
// ✅ Правильно: функция сама переключает контекст
class ProductRepository(
    private val api: ProductApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getProducts(): List = withContext(ioDispatcher) {
        api.fetchProducts()
    }
}

// Вызывающий код не думает о диспетчерах
componentScope.launch {
    val products = repository.getProducts()  // безопасно
}
```

---