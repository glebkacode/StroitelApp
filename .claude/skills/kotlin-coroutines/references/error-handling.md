# Обработка ошибок

## Try-catch в корутине
```kotlin
componentScope.launch {
    try {
        val data = repository.getData()
        _state.value = State.Success(data)
    } catch (e: CancellationException) {
        throw e  // ⚠️ Всегда пробрасывай CancellationException!
    } catch (e: Exception) {
        _state.value = State.Error(e.message)
    }
}
```

## Result wrapper
```kotlin
sealed interface Result {
    data class Success(val data: T) : Result
    data class Error(val exception: Throwable) : Result
}

suspend fun  safeCall(block: suspend () -> T): Result {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Error(e)
    }
}

// Использование
componentScope.launch {
    when (val result = safeCall { repository.getProducts() }) {
        is Result.Success -> _state.value = State.Success(result.data)
        is Result.Error -> _state.value = State.Error(result.exception.message)
    }
}
```

## CoroutineExceptionHandler
```kotlin
// Для fire-and-forget корутин
private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
    _state.value = State.Error(throwable.message)
}

fun loadData() {
    componentScope.launch(exceptionHandler) {
        val data = repository.getData()  // исключение поймается handler'ом
        _state.value = State.Success(data)
    }
}
```