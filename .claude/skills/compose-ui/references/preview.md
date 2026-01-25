# Превью

## Каждый компонент должен иметь превью
```kotlin
@Preview(showBackground = true)
@Composable
private fun ProductCardPreview() {
    AppTheme {
        ProductCard(
            product = previewProduct,
            onAddToCart = {},
            onClick = {}
        )
    }
}
```

## Несколько состояний
```kotlin
@Preview(name = "По умолчанию")
@Preview(name = "Тёмная тема", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ButtonPreview() {
    AppTheme {
        AppButton(text = "Нажми меня", onClick = {})
    }
}

@Preview(name = "Загрузка")
@Composable
private fun ScreenLoadingPreview() {
    AppTheme {
        FeatureScreen(
            state = FeatureState(isLoading = true),
            onAction = {}
        )
    }
}

@Preview(name = "Ошибка")
@Composable
private fun ScreenErrorPreview() {
    AppTheme {
        FeatureScreen(
            state = FeatureState(error = "Ошибка сети"),
            onAction = {}
        )
    }
}

@Preview(name = "Пустой список")
@Composable
private fun ScreenEmptyPreview() {
    AppTheme {
        FeatureScreen(
            state = FeatureState(items = emptyList()),
            onAction = {}
        )
    }
}
```

## Данные для превью
```kotlin
// В отдельном файле или companion object
internal val previewProduct = Product(
    id = "1",
    name = "Тестовый товар",
    price = "9 999 ₽",
    imageUrl = ""
)

internal val previewProducts = List(5) { index ->
    previewProduct.copy(id = "$index", name = "Товар $index")
}
```

---