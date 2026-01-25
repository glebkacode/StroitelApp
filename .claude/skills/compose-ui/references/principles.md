# Принципы

## 1. Stateless по умолчанию
Компоненты получают состояние, а не создают его.
```kotlin
// ✅ Правильно
@Composable
fun UserCard(
    user: User,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
)

// ❌ Неправильно
@Composable
fun UserCard(userId: String) {
    val user by viewModel.getUser(userId).collectAsState()
}
```

## 2. Единственная ответственность
Один компонент = одна задача.
```kotlin
// ✅ Правильно: отдельные компоненты
@Composable
fun ProductImage(url: String, modifier: Modifier = Modifier)

@Composable
fun ProductPrice(price: Price, modifier: Modifier = Modifier)

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        ProductImage(product.imageUrl)
        ProductPrice(product.price)
    }
}

// ❌ Неправильно: всё в одном месте
@Composable
fun ProductCard(product: Product) {
    Column {
        AsyncImage(...)
        Text(formatPrice(...))
        // 200 строк смешанной логики
    }
}
```

## 3. Modifier всегда последний параметр
```kotlin
@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier  // всегда последний со значением по умолчанию
)
```

## Управление состоянием

### UI State
```kotlin
data class FeatureState(
    val items: List = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedId: String? = null
) {
    val isEmpty: Boolean get() = items.isEmpty() && !isLoading
    val hasSelection: Boolean get() = selectedId != null
}
```

### Actions
```kotlin
sealed interface FeatureAction {
    data object Load : FeatureAction
    data object Retry : FeatureAction
    data class ItemClick(val id: String) : FeatureAction
    data class Search(val query: String) : FeatureAction
}
```

### Интеграция с ViewModel
```kotlin
@Composable
fun FeatureRoute(
    viewModel: FeatureViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    FeatureScreen(
        state = state,
        onAction = viewModel::onAction
    )
}
```

---