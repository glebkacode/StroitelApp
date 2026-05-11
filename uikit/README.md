# :uikit

Дизайн-система приложения StroitelApp.

## Назначение

Предоставляет единую точку входа для всех UI-примитивов: тему, цветовую схему,
типографику и переиспользуемые Compose-компоненты. Все фичи используют этот модуль
для соблюдения визуальной консистентности.

## Публичный API

### Тема

| Элемент | Описание |
|---------|----------|
| `StroitelTheme` | Composable-обёртка темы; применяет цвета и типографику через `MaterialTheme` |
| `StroitelTheme.colorScheme` | `@ReadOnlyComposable` — доступ к токенам цветов (`SdsColorScheme`) из любого Composable |
| `SdsColorScheme` | Цветовая схема дизайн-системы; содержит `SdsTextColor` |
| `SdsDefaultColorScheme` | Светлая цветовая схема по умолчанию |

### Компоненты

| Компонент | Описание |
|-----------|----------|
| `ImagePlaceholder` | Серая заглушка для изображений при отсутствии URL или ошибке загрузки |

## Зависимости

- `Jetpack Compose BOM` — Compose UI, Material 3
- `:core-navigation` — не требуется (модуль не зависит от других модулей проекта)

## Использование

```kotlin
// Применить тему в корне приложения
StroitelTheme {
    // контент приложения
}

// Получить цвет из токенов темы
val color = StroitelTheme.colorScheme.text.moscow

// Использовать заглушку изображения
ImagePlaceholder(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
)
```
