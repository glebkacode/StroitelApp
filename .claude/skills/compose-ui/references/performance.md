# Производительность

## Remember для тяжёлых операций
```kotlin
// ✅ Правильно
@Composable
fun ItemList(items: List) {
    val sortedItems = remember(items) {
        items.sortedBy { it.name }
    }
}

// ❌ Неправильно: сортировка при каждой рекомпозиции
@Composable
fun ItemList(items: List) {
    val sortedItems = items.sortedBy { it.name }
}
```

## Стабильные ссылки на лямбды
```kotlin
// ✅ Правильно
@Composable
fun ItemList(
    items: List,
    onItemClick: (String) -> Unit
) {
    LazyColumn {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            ItemRow(
                item = item,
                onClick = { onItemClick(item.id) }
            )
        }
    }
}

// ✅ Ещё лучше для сложных колбэков
@Composable
fun ItemList(
    items: List,
    onItemClick: (String) -> Unit
) {
    val clickHandler = remember(onItemClick) {
        { id: String -> onItemClick(id) }
    }
    // ...
}
```

## derivedStateOf для вычисляемых значений
```kotlin
@Composable
fun SearchResults(
    items: List,
    query: String
) {
    val filteredItems by remember(items, query) {
        derivedStateOf {
            if (query.isBlank()) items
            else items.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}
```

## Ключи в LazyColumn
```kotlin
// ✅ Всегда указывай ключи
LazyColumn {
    items(
        items = products,
        key = { it.id }  // стабильный уникальный ключ
    ) { product ->
        ProductCard(product)
    }
}
```

---