# Анти-паттерны

## ❌ Бизнес-логика в Composable
```kotlin
// Неправильно
@Composable
fun CheckoutButton(cart: Cart) {
    val total = cart.items.sumOf { it.price * it.quantity }
    val discount = if (total > 100) total * 0.1 else 0.0
    // ...
}

// Правильно: логика во ViewModel, UI получает готовое состояние
@Composable
fun CheckoutButton(
    total: String,
    onCheckout: () -> Unit
)
```

## ❌ Сайд-эффекты без LaunchedEffect
```kotlin
// Неправильно
@Composable
fun Screen(viewModel: ViewModel) {
    viewModel.loadData()  // вызывается при каждой рекомпозиции!
}

// Правильно
@Composable
fun Screen(viewModel: ViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
}
```

## ❌ Вложенный скролл без координации
```kotlin
// Неправильно: LazyColumn внутри Column с verticalScroll
Column(Modifier.verticalScroll(rememberScrollState())) {
    LazyColumn { }  // будет нулевая высота или краш
}

// Правильно: один скроллируемый контейнер или фиксированная высота
Column {
    LazyColumn(Modifier.weight(1f)) { }
    BottomBar()
}
```

## ❌ Игнорирование Insets
```kotlin
// Неправильно
Scaffold { 
    Content()  // игнорирует padding
}

// Правильно
Scaffold { paddingValues ->
    Content(Modifier.padding(paddingValues))
}
```

---