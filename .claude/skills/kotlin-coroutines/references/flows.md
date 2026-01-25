# Flow

## Базовое использование
```kotlin
// Создание Flow
fun observeProducts(): Flow<List> = flow {
    while (true) {
        emit(repository.getProducts())
        delay(30.seconds)
    }
}

// Подписка в ViewModel
viewModelScope.launch {
    observeProducts()
        .catch { e -> _state.value = State.Error(e.message) }
        .collect { products -> _state.value = State.Success(products) }
}
```

## StateFlow vs SharedFlow

| StateFlow | SharedFlow |
|-----------|------------|
| Всегда имеет значение | Может не иметь начального значения |
| Хранит только последнее значение | Можно настроить replay |
| Для UI state | Для событий |
```kotlin
// StateFlow для состояния
private val _state = MutableStateFlow(UiState.Loading)
val state: StateFlow = _state.asStateFlow()

// SharedFlow для одноразовых событий
private val _events = MutableSharedFlow()
val events: SharedFlow = _events.asSharedFlow()

fun onButtonClick() {
    viewModelScope.launch {
        _events.emit(Event.NavigateToDetails)
    }
}
```

## Операторы Flow
```kotlin
repository.observeProducts()
    .map { products -> products.filter { it.isAvailable } }
    .distinctUntilChanged()
    .debounce(300.milliseconds)
    .flowOn(Dispatchers.Default)  // операции выше выполняются на Default
    .catch { e -> emit(emptyList()) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
```

## stateIn и shareIn
```kotlin
// Холодный Flow → горячий StateFlow
val products: StateFlow<List> = repository
    .observeProducts()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),  // живёт 5 сек после последнего подписчика
        initialValue = emptyList()
    )

// Холодный Flow → горячий SharedFlow
val events: SharedFlow = eventSource
    .shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        replay = 0
    )
```

## Combine и Zip
```kotlin
// Combine: эмитит при изменении любого из Flow
val uiState: StateFlow = combine(
    productsFlow,
    cartFlow,
    userFlow
) { products, cart, user ->
    UiState(
        products = products,
        cartCount = cart.items.size,
        userName = user.name
    )
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

// Zip: ждёт значения от всех Flow
productsFlow.zip(pricesFlow) { products, prices ->
    products.map { it.copy(price = prices[it.id]) }
}
```

---