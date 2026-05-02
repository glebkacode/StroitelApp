# Модификаторы в Jetpack Compose

Модификаторы — основной способ декорировать или расширять composable. Они задают layout, отрисовку, обработку жестов и поведение для accessibility. Понимание порядка модификаторов и доступных API критично для корректности и производительности.

## Порядок в цепочке модификаторов

Порядок имеет значение. Модификаторы применяются слева направо в DSL, но концептуально оборачивают друг друга снизу вверх. Каждый модификатор получает лямбду, которая отрисовывает/измеряет содержимое под ним.

```kotlin
// Пример: разные результаты в зависимости от порядка
Box(
    Modifier
        .background(Color.Red)
        .padding(16.dp)
        .size(100.dp)
)
// Красный фон оборачивает паддинг, который оборачивает Box 100x100

Box(
    Modifier
        .size(100.dp)
        .padding(16.dp)
        .background(Color.Red)
)
// Box 100x100 получает паддинг, и весь контейнер (132x132) — красный фон
```

**Делай:** упорядочивай модификаторы от внешних (layout/sizing) к внутренним (стили/взаимодействие).
**Не делай:** не ставь `size` после `padding`, если хочешь, чтобы паддинг входил в итоговый размер.

Источник: `compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/Modifier.kt`

## Распространённые паттерны

### Padding и Sizing

```kotlin
// Padding: внешние отступы вокруг контента
Box(Modifier.padding(16.dp)) { }

// Size: точные размеры (переопределяют запрошенный родителем размер)
Box(Modifier.size(100.dp)) { }
Box(Modifier.size(width = 200.dp, height = 100.dp)) { }

// FillMaxWidth/FillMaxHeight: расширение до доступного пространства
Box(Modifier.fillMaxWidth(0.8f)) { }  // 80% ширины родителя
Box(Modifier.fillMaxSize()) { }       // 100% родителя

// Делай: используй fillMaxWidth до padding для ясного выравнивания
Column(Modifier.fillMaxWidth()) {
    Box(Modifier.padding(16.dp).fillMaxWidth()) { }
}

// Не делай: не применяй fillMaxWidth после background, если хочешь, чтобы фон расширялся
// Правильно:
Box(Modifier.fillMaxWidth().background(Color.Blue)) { }
```

### Background и Border

```kotlin
// Background применяет цвет к поверхности
Box(Modifier.background(Color.Blue)) { }
Box(Modifier.background(Color.Blue, shape = RoundedCornerShape(8.dp))) { }

// Border рисует обводку (порядок имеет значение!)
Box(
    Modifier
        .size(100.dp)
        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        .background(Color.White)
)
// Border визуально рисуется ПОСЛЕ background (т. к. модификаторы ниже отрисовываются первыми)

// Комбинация background + border: border ставь первым в цепочке
Box(
    Modifier
        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        .background(Color.White)
)
```

### Clipping

```kotlin
// Обрезание контента по форме
Box(Modifier.clip(RoundedCornerShape(8.dp))) {
    Image(painter = painterResource(id = R.drawable.my_image), contentDescription = "")
}

// Делай: применяй clip до background, если хочешь, чтобы фон был внутри формы
Box(
    Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(Color.Blue)
) { }

// Не делай: background, потом clip (работает, но семантически неверно)
Box(
    Modifier
        .background(Color.Blue)
        .clip(RoundedCornerShape(8.dp))
) { }
```

## Clickable и Combined Clickable

```kotlin
// Базовая обработка клика с ripple-эффектом (по умолчанию в Material 3)
Button(onClick = { }) { Text("Click me") }

// Ручной clickable с ripple
Box(
    Modifier
        .size(100.dp)
        .clickable(
            indication = ripple(),  // Material ripple feedback
            interactionSource = remember { MutableInteractionSource() }
        ) { /* обработка клика */ }
)

// Combined clickable: long press + double click + click
Box(
    Modifier
        .combinedClickable(
            onClick = { },
            onLongClick = { },
            onDoubleClick = { },
            indication = ripple()
        )
) { }

// Делай: передавай явный interactionSource для тестов/наблюдения за состоянием
val interactionSource = remember { MutableInteractionSource() }
Box(
    Modifier.clickable(
        interactionSource = interactionSource,
        indication = ripple()
    ) { }
)

// Не делай: не забывай параметр indication (иначе не будет визуального фидбека)
Box(Modifier.clickable { }) { }  // Без ripple
```

## Modifier.composed vs Modifier.Node

Старый API (`composed`) выводится из обращения в пользу нового `ModifierNodeElement`. Оба работают, но в новом коде используй второй.

### Старый API: Modifier.composed

```kotlin
fun Modifier.myCustomModifier(value: String) = composed {
    val state = remember { mutableStateOf(value) }
    this.then(
        Modifier
            .background(Color.Blue)
            .clickable { state.value = "updated" }
    )
}
```

- Создаёт новую composable-область
- Захватывает composition local'ы
- Вызывает рекомпозицию при изменении remember-стейта
- Deprecated, но всё ещё поддерживается

### Новый API: Modifier.Node

```kotlin
class MyCustomNode(val value: String) : Modifier.Node {
    override fun onDetach() {
        // Очистка при удалении
    }
}

data class MyCustomElement(val value: String) : ModifierNodeElement<MyCustomNode>() {
    override fun create() = MyCustomNode(value)
    override fun update(node: MyCustomNode) {
        node.value = value
    }
}

fun Modifier.myCustomModifier(value: String) = this.then(MyCustomElement(value))
```

**Делай:** используй `Modifier.Node` для новых кастомных модификаторов. Эффективнее и не создаёт composition-областей.
**Не делай:** не создавай новые `composed`-модификаторы; мигрируй существующие на `Modifier.Node`.

Источник: `compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/modifier/ModifierNodeElement.kt`

## Layout vs Drawing vs Pointer Input модификаторы

Модификаторы делятся на категории по тому, в какой фазе они выполняются:

```kotlin
// Layout-модификатор: влияет на measurement и layout-проход
fun Modifier.customSize(width: Dp, height: Dp) =
    this.then(object : LayoutModifier {
        override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints) =
            measurable.measure(Constraints.fixed(width.roundToPx(), height.roundToPx()))
                .run { layout(width = size.width, height = size.height) { place(0, 0) } }
    })

// Drawing-модификатор: не влияет на layout, только рисует поверх контента
fun Modifier.customDraw() = drawBehind { drawCircle(Color.Red) }

// Pointer input модификатор: обрабатывает жесты/события
fun Modifier.detectCustomGesture() = pointerInput(Unit) {
    detectTapGestures { offset -> /* обработка */ }
}
```

**Делай:** layout-модификаторы — для размеров/позиционирования, drawing — для визуальных эффектов, pointer — для ввода.
**Не делай:** не используй layout-модификаторы для визуальных эффектов; используй drawing-модификаторы.

## Modifier.graphicsLayer — влияние на производительность

`graphicsLayer` применяет преобразования на уровне рендеринга графики. Это эффективнее, чем рекомпозиция, для анимаций.

```kotlin
// Эффективно: трансформации применяются на graphics layer, без рекомпозиции
Box(
    Modifier.graphicsLayer(
        scaleX = 1.2f,
        scaleY = 1.2f,
        translationX = 10f,
        rotationZ = 45f,
        alpha = 0.8f
    )
) { }

// Менее эффективно: рекомпозиция на каждом кадре
var scaleX by remember { mutableStateOf(1f) }
LaunchedEffect(Unit) {
    while (true) {
        scaleX = 1.2f
        delay(16)
    }
}
Box(Modifier.scale(scaleX)) { }
```

**Делай:** используй `graphicsLayer` для анимаций и часто меняющихся свойств.
**Не делай:** не анимируй state-значения, вызывающие рекомпозицию, если хватает `graphicsLayer`.

Источник: `compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/graphics/GraphicsLayerModifier.kt`

## Modifier.semantics — Accessibility

Семантика описывает смысл UI-элементов для скринридеров и accessibility-тестов.

```kotlin
// Добавление семантической метки
Button(onClick = { }) {
    Icon(Icons.Default.Add, contentDescription = null)
    Text("Add item")
}

// Кастомные семантические свойства
Box(
    Modifier
        .size(100.dp)
        .semantics {
            contentDescription = "Custom box"
            onClick(label = "Activate") { true }
        }
) { }

// Делай: всегда передавай contentDescription для изображений
Image(
    painter = painterResource(id = R.drawable.icon),
    contentDescription = "User avatar"
)

// Не делай: не забывай contentDescription (скринридеры не озвучат)
Image(painter = painterResource(id = R.drawable.icon), contentDescription = null) // Неправильно
```

Источник: `compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/semantics/Semantics.kt`

## Modifier.testTag — UI-тестирование

```kotlin
// Test tag для поиска composable в тестах
Box(Modifier.testTag("my_box")) { }

// В тестах:
composeTestRule.onNodeWithTag("my_box").performClick()
composeTestRule.onNodeWithTag("my_box").assertIsDisplayed()
```

**Делай:** используй уникальные, описательные test tag'и.
**Не делай:** не используй test tag в production-коде для бизнес-логики.

## Чеклист ревью: баги порядка модификаторов

### Хардкод размера ПОСЛЕ `modifier`-параметра вызывающего

Когда composable принимает `modifier: Modifier = Modifier` и затем чейнит фиксированные `.height()` / `.width()` / `.size()`, ограничения от вызывающего тихо игнорируются или клампятся.

```kotlin
// ПЛОХО: height вызывающего — внешний constraint, 172.dp компонента — внутренний;
// компонент всегда рендерится 172dp
@Composable
fun BannerCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier          // constraints вызывающего применяются первыми (внешние)
            .fillMaxWidth()
            .height(172.dp)          // внутренний — побеждает, если меньше; клампится, если больше
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Green.copy(alpha = 0.08f)),
    )
}

// Вызывающий ожидает 200dp, но получает 172dp:
BannerCard(modifier = Modifier.height(200.dp))

// Вызывающий ожидает 100dp — компонент клампится/сжимается:
BannerCard(modifier = Modifier.height(100.dp))
```

**Почему так:** цепочка модификаторов резолвится снаружи внутрь (слева направо). Внешний constraint задаёт максимум, внутренний — запрашивает в этих пределах. Первое size-ограничение становится потолком.

**Фикс 1:** дефолты компонента сначала, вызывающий может переопределить через `.then(modifier)`:
```kotlin
// ХОРОШО: дефолты компонента, modifier вызывающего применяется последним и может переопределить
@Composable
fun BannerCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Green.copy(alpha = 0.08f))
            .then(modifier),         // вызывающий может переопределить размер
    )
}
```

**Фикс 2:** используй `defaultMinSize` для гибкого размера:
```kotlin
// ХОРОШО: минимум гарантирован, вызывающий может сделать больше
@Composable
fun BannerCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 172.dp)  // пол, а не потолок
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Green.copy(alpha = 0.08f)),
    )
}
```

**Правило:** если composable принимает `modifier: Modifier = Modifier`, никогда не чейнь фиксированные `.height()` / `.width()` / `.size()` после `modifier` вызывающего — его constraints становятся внешними границами, и фиксированный размер компонента либо игнорируется, либо клампится. Используй `.then(modifier)` в конце или `defaultMinSize` для гибкого размера. Флагай в каждом PR review.

---

## Антипаттерны

### Создание модификаторов в композиции

```kotlin
// Не делай: создаёт новый Modifier на каждой рекомпозиции
@Composable
fun BadModifier() {
    Box(Modifier.padding(16.dp).background(Color.Blue)) { }
}

// Делай: вынеси в переменную или параметр
@Composable
fun GoodModifier(modifier: Modifier = Modifier) {
    Box(modifier.padding(16.dp).background(Color.Blue)) { }
}
```

### Условные цепочки модификаторов сделаны неправильно

```kotlin
// Не делай: ломает type checking и читаемость
val mod = if (isSelected) Modifier.background(Color.Blue) else Modifier
Box(mod.padding(16.dp)) { }

// Делай: используй then() для условного чейнинга
Box(
    Modifier
        .padding(16.dp)
        .then(if (isSelected) Modifier.background(Color.Blue) else Modifier)
) { }
```

---

**Итог:** освой порядок модификаторов, предпочитай `Modifier.Node` вместо `composed`, используй `graphicsLayer` для анимаций и всегда учитывай семантический слой для accessibility.
