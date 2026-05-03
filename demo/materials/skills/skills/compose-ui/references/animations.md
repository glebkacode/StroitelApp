# Анимации в Jetpack Compose

Источник: `androidx/compose/animation/animation/src/commonMain/kotlin/androidx/compose/animation/`

## Анимации на основе состояния

### animate*AsState

Анимация отдельных свойств по целевому значению. Запускается при изменении значения.

```kotlin
val size by animateDpAsState(
    targetValue = if (isExpanded) 200.dp else 100.dp,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "size"
)

Box(modifier = Modifier.size(size))
```

Распространённые варианты:

```kotlin
animateColorAsState(targetValue = Color.Blue)
animateFloatAsState(targetValue = 1f)
animateIntAsState(targetValue = 100)
animateOffsetAsState(targetValue = Offset(10f, 20f))
```

Каждый автоматически управляет корутинами и рекомпозицией. Используй `label` для отладки.

## AnimatedVisibility

Управляет анимациями появления/скрытия через enter и exit transitions.

```kotlin
var visible by remember { mutableStateOf(true) }

AnimatedVisibility(visible = visible) {
    Text("Hello!")
}

// Триггер
Button(onClick = { visible = !visible }) { Text("Toggle") }
```

### Enter/Exit Transitions

```kotlin
AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
) {
    Text("Animated!")
}
```

Встроенные transitions:
- `slideInVertically`, `slideOutVertically`
- `slideInHorizontally`, `slideOutHorizontally`
- `expandVertically`, `shrinkVertically`
- `expandHorizontally`, `shrinkHorizontally`
- `fadeIn`, `fadeOut`
- `scaleIn`, `scaleOut`
- Комбинируй через `+`: `slideInVertically() + fadeIn()`

### Расширенно: кастомные animation specs

```kotlin
AnimatedVisibility(
    visible = visible,
    enter = slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = spring()
    ),
    exit = slideOutVertically(
        targetOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(durationMillis = 300)
    )
) {
    Box(Modifier.fillMaxWidth().height(100.dp).background(Color.Blue))
}
```

## AnimatedContent

Замена контента с плавными переходами.

```kotlin
var count by remember { mutableStateOf(0) }

AnimatedContent(targetState = count) { target ->
    Text(text = "Count: $target")
}

Button(onClick = { count++ }) { Text("Increment") }
```

### Кастомный transitionSpec

```kotlin
AnimatedContent(
    targetState = count,
    transitionSpec = {
        slideInVertically(initialOffsetY = { it }) with slideOutVertically(targetOffsetY = { -it })
    }
) { target ->
    Text("$target")
}
```

`with` задаёт exit и enter одновременно — выполняются параллельно.

### Sequencing transitions

```kotlin
AnimatedContent(
    targetState = count,
    transitionSpec = {
        slideInVertically(initialOffsetY = { it }) with slideOutVertically(targetOffsetY = { -it }) using SizeTransform(clip = false)
    }
) { target ->
    Text(
        "Count: $target",
        modifier = Modifier.fillMaxWidth()
    )
}
```

`SizeTransform` плавно анимирует размер контейнера при смене контента.

## Crossfade

Простая смена контента с fade-эффектом.

```kotlin
var showFirst by remember { mutableStateOf(true) }

Crossfade(targetState = showFirst) { state ->
    if (state) {
        Text("First")
    } else {
        Text("Second")
    }
}
```

Лёгкая альтернатива `AnimatedContent` для простых переключений видимости.

## updateTransition

Координация нескольких анимируемых значений по одному состоянию.

```kotlin
var expanded by remember { mutableStateOf(false) }
val transition = updateTransition(targetState = expanded)

val size by transition.animateDp { if (it) 200.dp else 100.dp }
val color by transition.animateColor { if (it) Color.Blue else Color.Red }

Box(
    modifier = Modifier
        .size(size)
        .background(color)
        .clickable { expanded = !expanded }
)
```

Все анимации идут синхронно от одного изменения состояния. Подходит для сложных компонентов с несколькими анимируемыми свойствами.

## rememberInfiniteTransition

Бесконечно повторяющиеся анимации.

```kotlin
val infiniteTransition = rememberInfiniteTransition(label = "infinite")

val alpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(1000),
        repeatMode = RepeatMode.Reverse
    ),
    label = "alpha"
)

Text("Pulsing", modifier = Modifier.alpha(alpha))
```

Работает непрерывно, пока composable существует. Подходит для лоадеров, пульсирующих индикаторов.

## Animatable

Императивное управление анимацией в корутинах. Используй для тонкого контроля.

```kotlin
val animatable = remember { Animatable(0f) }

LaunchedEffect(trigger) {
    animatable.animateTo(
        targetValue = 100f,
        animationSpec = spring()
    )
}

Box(Modifier.graphicsLayer(translationX = animatable.value))
```

Удобно для жестов или сложных условий:

```kotlin
val animatable = remember { Animatable(0f) }

LaunchedEffect(Unit) {
    animatable.animateTo(targetValue = 360f, animationSpec = tween(2000))
}

Box(
    Modifier
        .size(100.dp)
        .background(Color.Blue)
        .graphicsLayer(rotationZ = animatable.value)
)
```

## Animation Specifications

### spring — реалистичная физика

```kotlin
val size by animateDpAsState(
    targetValue = 200.dp,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
)
```

- `dampingRatio`: `NoBouncy` (1f), `LowBouncy` (0.75f), `MediumBouncy` (0.5f), `HighBouncy` (0.2f)
- `stiffness`: `Low`, `Medium`, `High`

Подходит для интерактивного фидбека, привычно пользователю.

### tween — на основе времени

```kotlin
val color by animateColorAsState(
    targetValue = Color.Blue,
    animationSpec = tween(durationMillis = 500, easing = EaseInOutCubic)
)
```

Easing-функции: `EaseInQuad`, `EaseOutQuad`, `EaseInOutQuad`, `LinearEasing`, `FastOutSlowInEasing`.

Предсказуемое тайминг — хорошо для последовательных анимаций.

### keyframes — по кадрам

```kotlin
val position by animateFloatAsState(
    targetValue = 100f,
    animationSpec = keyframes {
        0f at 0 using EaseInQuad
        50f at 150 using EaseOutQuad
        100f at 300
    }
)
```

Точные значения в конкретные таймстемпы. Для сложной хореографии.

## Автоматическая анимация размера

### animateContentSize

Плавно анимирует размер Box при изменении контента.

```kotlin
var expanded by remember { mutableStateOf(false) }

Box(
    modifier = Modifier
        .animateContentSize()
        .background(Color.Blue)
        .clickable { expanded = !expanded }
) {
    Column {
        Text("Header")
        if (expanded) {
            Text("Expanded content...")
        }
    }
}
```

Не нужно явное `AnimatedVisibility` или layout-transitions. Контейнер обрабатывается автоматически.

## Layout Animation в LazyLists

### animateItem — заменяет animateItemPlacement

Анимация появления, удаления и переупорядочивания элементов.

```kotlin
LazyColumn {
    items(items, key = { it.id }) { item ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .animateItem()
                .padding(8.dp)
                .background(Color.Gray)
        ) {
            Text(item.name)
        }
    }
}
```

Автоматически анимирует:
- Новые элементы — слайдят внутрь
- Удалённые — слайдят наружу
- Переупорядоченные — переезжают на новые позиции

Применяется к элементам Lazy-лейаутов (LazyColumn, LazyRow, LazyVerticalGrid).

## Shared Element Transitions

Бесшовно анимируй элементы между экранами через `SharedTransitionLayout` и Navigation Compose.

### sharedElement() vs sharedBounds()

| Аспект | `sharedElement()` | `sharedBounds()` |
|---|---|---|
| **Контент** | Идентичен на обоих экранах (та же картинка, та же иконка) | Разный контент в source и target (например, карточка раскрывается в детали) |
| **Сценарий** | Hero-картинка, аватар, превью | Container transform, card-to-page |
| **Во время transition** | Рендерится только target | Source и target видны и кроссфейдятся |

### Полный рабочий пример

```kotlin
@Composable
fun App() {
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = "list") {
            composable("list") {
                ListScreen(
                    onItemClick = { id -> navController.navigate("detail/$id") },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            composable("detail/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                DetailScreen(
                    itemId = id,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
        }
    }
}

@Composable
fun ListScreen(
    onItemClick: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .clickable { onItemClick(item.id) }
                // sharedBounds оборачивает весь контейнер карточки (контент source/target различается)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "card-${item.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = BoundsTransform { initialBounds, targetBounds ->
                        keyframes {
                            durationMillis = 500
                            initialBounds at 0 using ArcMode.ArcBelow
                            targetBounds at 500
                        }
                    }
                )
        ) {
            Image(
                painter = painterResource(item.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    // sharedElement для идентичной картинки на разных экранах
                    .sharedElement(
                        state = rememberSharedContentState(key = "image-${item.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
            Text(
                text = item.title,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "title-${item.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    // Предотвращаем reflow текста — снап на финальный размер
                    .skipToLookaheadSize()
            )
        }
    }
}

@Composable
fun DetailScreen(
    itemId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "card-$itemId"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        ) {
            Image(
                painter = painterResource(item.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .sharedElement(
                        state = rememberSharedContentState(key = "image-$itemId"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "title-$itemId"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .skipToLookaheadSize()
            )
            // Несхожий контент — fade in
            Text(
                text = item.description,
                modifier = Modifier.animateEnterExit(
                    enter = fadeIn() + slideInVertically { it / 3 },
                    exit = fadeOut()
                )
            )
        }
    }
}
```

### BoundsTransform для дугового движения

Управляй траекторией анимации между source и target:

```kotlin
val arcBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
    keyframes {
        durationMillis = 500
        initialBounds at 0 using ArcMode.ArcBelow
        targetBounds at 500
    }
}

// Применяй к sharedElement или sharedBounds
Modifier.sharedElement(
    state = rememberSharedContentState(key = "hero"),
    animatedVisibilityScope = animatedVisibilityScope,
    boundsTransform = arcBoundsTransform
)
```

### Overlay Rendering

Держи shared-элементы поверх остального контента во время transition:

```kotlin
Modifier.sharedElement(
    state = rememberSharedContentState(key = "fab"),
    animatedVisibilityScope = animatedVisibilityScope,
    renderInSharedTransitionScopeOverlay = true // Рендерится поверх navigation transitions
)
```

### Предотвращение reflow текста

Используй `skipToLookaheadSize()`, чтобы Text сразу принимал финальный размер — не будет неудобной смены переноса строк по ходу transition:

```kotlin
Text(
    text = item.title,
    modifier = Modifier
        .sharedElement(
            state = rememberSharedContentState(key = "title-${item.id}"),
            animatedVisibilityScope = animatedVisibilityScope
        )
        .skipToLookaheadSize() // Текст использует target-размер сразу — без reflow
)
```

## Производительность: graphicsLayer для трансформаций

Анимируй трансформации через `graphicsLayer`, а не через изменения layout.

```kotlin
// ✅ Правильно: GPU-ускоренный graphicsLayer
val offset by animateFloatAsState(targetValue = 100f)
Box(modifier = Modifier.graphicsLayer(translationX = offset))

// ❌ Избегай: вызывает рекомпозицию и relayout
val offset by animateFloatAsState(targetValue = 100f)
Box(modifier = Modifier.offset(x = offset.dp))
```

Используй `graphicsLayer` для:
- Translation (`translationX`, `translationY`)
- Rotation (`rotationX`, `rotationY`, `rotationZ`)
- Scale (`scaleX`, `scaleY`)
- Alpha (прозрачность)

## Антипаттерны

### Не делай: анимация видимости через if

```kotlin
// ❌ Антипаттерн
@Composable
fun MyScreen() {
    if (visible) {
        Text("Content") // Появляется/исчезает без анимации
    }
}

// ✅ Правильно
@Composable
fun MyScreen() {
    AnimatedVisibility(visible = visible) {
        Text("Content")
    }
}
```

### Не делай: создание Animatable в композиции

```kotlin
// ❌ Антипаттерн
@Composable
fun MyScreen() {
    val animatable = Animatable(0f) // Пересоздаётся на каждой рекомпозиции!

    LaunchedEffect(Unit) {
        animatable.animateTo(100f)
    }
}

// ✅ Правильно
@Composable
fun MyScreen() {
    val animatable = remember { Animatable(0f) } // Сохраняется между рекомпозициями

    LaunchedEffect(Unit) {
        animatable.animateTo(100f)
    }
}
```

### Не делай: анимация в фазе композиции

```kotlin
// ❌ Антипаттерн
@Composable
fun MyScreen() {
    var position by remember { mutableStateOf(0f) }
    position = position + 10f // Бесконечный цикл рекомпозиции!
}

// ✅ Правильно
@Composable
fun MyScreen() {
    var position by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        repeat(10) {
            position += 10f
            delay(16)
        }
    }
}
```

### Не делай: забывать параметр label

```kotlin
// ❌ Антипаттерн (сложнее дебажить)
val size by animateDpAsState(targetValue = 100.dp)

// ✅ Правильно
val size by animateDpAsState(
    targetValue = 100.dp,
    label = "box_size"
)
```

Лейблы помогают в layout inspector и инструментах инспекции анимаций.

---

## Дерево решений по анимациям

### Что использовать

| API | Когда |
|---|---|
| `animate*AsState` | Анимация одного свойства (size, color, alpha) от состояния |
| `AnimatedVisibility` | Показ/скрытие composable с enter/exit transitions |
| `AnimatedContent` / `Crossfade` | Переключение между разными composable (смена контента) |
| `updateTransition` | Несколько свойств, которые синхронно анимируются от одного состояния |
| `Animatable` | Жесты или императивное управление (на корутинах, поддерживает `snapTo`, `animateDecay`) |
| `rememberInfiniteTransition` | Бесконечные циклические анимации (пульсация, вращение, shimmer) |
| `animateContentSize` | Плавная анимация размера контейнера при смене контента |
| `animateItem` | Появление/исчезание/переупорядочивание элементов в Lazy-лейаутах |

### На какую фазу влияет каждая анимация

Рендеринг Compose имеет три фазы: **Composition** (что показать), **Layout** (где разместить), **Draw** (как отрисовать). Анимации должны читать стейт в максимально поздней фазе, чтобы минимизировать работу.

```kotlin
// ЛУЧШЕЕ: только Draw — без relayout, без рекомпозиции
val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, label = "alpha")
Box(
    modifier = Modifier.graphicsLayer { this.alpha = alpha }
)

// ХОРОШО: только Layout — relayout, но без рекомпозиции
val offsetPx by animateIntAsState(targetValue = if (moved) 300 else 0, label = "offset")
Box(
    modifier = Modifier.offset { IntOffset(offsetPx, 0) }
)

// УМЕРЕННО: Composition + Layout — рекомпозиция на каждом кадре
val offsetDp by animateDpAsState(targetValue = if (moved) 100.dp else 0.dp, label = "offset")
Box(
    modifier = Modifier.offset(x = offsetDp)
)
```

**Правило:** откладывай чтения состояния на максимально позднюю фазу. Используй лямбда-модификаторы (`graphicsLayer { }`, `offset { }`), а не параметрические (`graphicsLayer(alpha = ...)`, `offset(x = ...)`).

---

## Перевод дизайна в анимации

### Easing-кривые Figma → Compose

| Figma Easing | Эквивалент в Compose |
|---|---|
| Linear | `LinearEasing` |
| Ease In | `FastOutLinearInEasing` |
| Ease Out | `LinearOutSlowInEasing` |
| Ease In and Out | `FastOutSlowInEasing` |
| Custom Bezier (x1, y1, x2, y2) | `CubicBezierEasing(x1, y1, x2, y2)` |

### Motion Duration токены M3

| Токен | Длительность |
|---|---|
| Short1 | 50ms |
| Short2 | 100ms |
| Short3 | 150ms |
| Short4 | 200ms |
| Medium1 | 250ms |
| Medium2 | 300ms |
| Medium3 | 350ms |
| Medium4 | 400ms |
| Long1 | 450ms |
| Long2 | 500ms |
| Long3 | 550ms |
| Long4 | 600ms |
| ExtraLong1 | 700ms |
| ExtraLong2 | 800ms |
| ExtraLong3 | 900ms |
| ExtraLong4 | 1000ms |

### Easing-токены M3

| Токен | Значение Compose |
|---|---|
| Emphasized | `CubicBezierEasing(0.2f, 0f, 0f, 1f)` |
| EmphasizedDecelerate | `CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)` |
| EmphasizedAccelerate | `CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)` |
| Standard | `FastOutSlowInEasing` |
| StandardDecelerate | `LinearOutSlowInEasing` |
| StandardAccelerate | `FastOutLinearInEasing` |

### Интуиция параметров spring

**Stiffness** (как быстро анимация движется к цели):

| Значение | Константа | Ощущение |
|---|---|---|
| ~26f | — | Медленно, тяжело, вяло |
| 200f | `Spring.StiffnessLow` | Мягко, расслабленно |
| 400f | `Spring.StiffnessMediumLow` | Спокойно, комфортно |
| 1500f | `Spring.StiffnessMedium` | Отзывчиво, по умолчанию |
| 10000f | `Spring.StiffnessHigh` | Резко, мгновенно |

**Damping Ratio** (сколько отскока):

| Значение | Константа | Ощущение |
|---|---|---|
| 1.0f | `Spring.DampingRatioNoBouncy` | Без overshoot, успокаивается прямо |
| 0.75f | `Spring.DampingRatioLowBouncy` | Лёгкий отскок, профессионально |
| 0.5f | `Spring.DampingRatioMediumBouncy` | Игриво, заметный отскок |
| 0.2f | `Spring.DampingRatioHighBouncy` | Мультяшный, преувеличенный отскок |

### Конвертация spring из Figma в Compose

```kotlin
fun figmaSpringToCompose(mass: Float, stiffness: Float, damping: Float): SpringSpec<Float> {
    val dampingRatio = damping / (2f * sqrt(stiffness * mass))
    return spring(dampingRatio = dampingRatio, stiffness = stiffness)
}
```

### Production-проверенные spring specs

```kotlin
val figmaMatchedSpring = spring<Float>(dampingRatio = 0.444f, stiffness = 26.5f)
val responsiveSpring = spring<Float>(dampingRatio = 0.7f, stiffness = 800f)
val snappySpring = spring<Float>(dampingRatio = 0.6f, stiffness = 1000f)
```

---

## Анимации, управляемые жестами

### Swipe-to-Dismiss с Animatable

```kotlin
fun Modifier.swipeToDismiss(onDismiss: () -> Unit): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    val decay = rememberSplineBasedDecay<Float>()

    pointerInput(Unit) {
        coroutineScope {
            while (true) {
                val velocityTracker = VelocityTracker()
                // Ждём touch down
                val pointerId = awaitPointerEventScope {
                    awaitFirstDown().id
                }
                // Отменяем текущую анимацию
                offsetX.stop()

                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        val horizontalDragOffset = offsetX.value + change.positionChange().x
                        launch { offsetX.snapTo(horizontalDragOffset) }
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        change.consume()
                    }
                }

                val velocity = velocityTracker.calculateVelocity().x
                val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)

                offsetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )

                launch {
                    if (abs(targetOffsetX) >= size.width * 0.5f) {
                        // Достаточно сильное fling — dismiss
                        offsetX.animateDecay(velocity, decay)
                        onDismiss()
                    } else {
                        // Возвращаем
                        offsetX.animateTo(
                            targetValue = 0f,
                            initialVelocity = velocity
                        )
                    }
                }
            }
        }
    }.offset { IntOffset(offsetX.value.roundToInt(), 0) }
}
```

### AnchoredDraggable Snap Points

```kotlin
enum class DragValue { Start, Center, End }

@Composable
fun AnchoredDraggableExample() {
    val density = LocalDensity.current
    val anchors = with(density) {
        DraggableAnchors {
            DragValue.Start at -200.dp.toPx()
            DragValue.Center at 0f
            DragValue.End at 200.dp.toPx()
        }
    }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Center,
            anchors = anchors,
            positionalThreshold = { totalDistance -> totalDistance * 0.5f },
            velocityThreshold = { with(density) { 125.dp.toPx() } },
            animationSpec = spring()
        )
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(state.requireOffset().roundToInt(), 0) }
            .anchoredDraggable(state, Orientation.Horizontal)
            .size(80.dp)
            .background(Color.Blue, RoundedCornerShape(16.dp))
    )
}
```

### Transformable: Pinch, Zoom, Rotate

```kotlin
@Composable
fun TransformableExample() {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
        rotation += rotationChange
        offset += offsetChange
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
                translationX = offset.x
                translationY = offset.y
            }
            .transformable(state = transformableState)
            .size(200.dp)
            .background(Color.Blue)
    )
}
```

---

## Рецепты анимаций

### Shimmer / Skeleton Loading

```kotlin
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 500f, 0f)
    )

    background(shimmerBrush)
}

@Composable
fun SkeletonCard() {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
    }
}

@Composable
fun ContentWithLoading(isLoading: Boolean, content: @Composable () -> Unit) {
    Crossfade(targetState = isLoading, label = "loading_crossfade") { loading ->
        if (loading) {
            SkeletonCard()
        } else {
            content()
        }
    }
}
```

### Staggered list entrance

```kotlin
@Composable
fun StaggeredListEntrance(items: List<String>) {
    Column {
        items.forEachIndexed { index, item ->
            val animatable = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                delay(index * 100L)
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
            Text(
                text = item,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = animatable.value
                        translationX = (1f - animatable.value) * 100f
                    }
                    .padding(8.dp)
            )
        }
    }
}
```

### Swipe-to-Dismiss (Material 3)

```kotlin
@Composable
fun SwipeToDismissItem(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onDismiss()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Color.Green
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                },
                label = "dismiss_bg"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    else -> Alignment.CenterEnd
                }
            ) {
                Icon(
                    imageVector = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Done
                        else -> Icons.Default.Delete
                    },
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        content()
    }
}
```

### Раскрывающаяся карточка

```kotlin
@Composable
fun ExpandableCard(title: String, description: String) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrow_rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.graphicsLayer { rotationZ = arrowRotation }
                )
            }
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
```

### Кастомный Pull-to-Refresh

```kotlin
@Composable
fun CustomPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        indicator = { state ->
            val distanceFraction = state.distanceFraction.coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refreshing",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer {
                            scaleX = distanceFraction
                            scaleY = distanceFraction
                            rotationZ = distanceFraction * 360f
                        }
                )
            }
        }
    ) {
        content()
    }
}
```

### FAB Morph

**Паттерн 1: ExtendedFloatingActionButton с раскрытием/сворачиванием по скроллу**

```kotlin
@Composable
fun CollapsibleFab(listState: LazyListState) {
    val expandedFab by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    ExtendedFloatingActionButton(
        onClick = { /* действие */ },
        expanded = expandedFab,
        icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
        text = { Text("New Item") }
    )
}
```

**Паттерн 2: «Взрывающийся» FAB через updateTransition**

```kotlin
@Composable
fun ExplodingFab(isExpanded: Boolean, onClick: () -> Unit) {
    val transition = updateTransition(targetState = isExpanded, label = "fab_explode")

    val size by transition.animateDp(label = "size") { if (it) 200.dp else 56.dp }
    val cornerRadius by transition.animateDp(label = "corner") { if (it) 16.dp else 28.dp }
    val color by transition.animateColor(label = "color") {
        if (it) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.primaryContainer
    }
    val contentAlpha by transition.animateFloat(label = "alpha") { if (it) 1f else 0f }

    Surface(
        modifier = Modifier.size(size).clickable { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        color = color
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (!isExpanded) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
            Column(
                modifier = Modifier.graphicsLayer { alpha = contentAlpha },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Раскрытый контент
                Text("Option 1")
                Text("Option 2")
                Text("Option 3")
            }
        }
    }
}
```

### Драг bottom sheet

```kotlin
enum class SheetValue { Hidden, Collapsed, Expanded }

@Composable
fun DraggableBottomSheet(content: @Composable () -> Unit) {
    val density = LocalDensity.current
    val anchors = with(density) {
        DraggableAnchors {
            SheetValue.Hidden at 0f
            SheetValue.Collapsed at -200.dp.toPx()
            SheetValue.Expanded at -600.dp.toPx()
        }
    }

    val state = remember {
        AnchoredDraggableState(
            initialValue = SheetValue.Hidden,
            anchors = anchors,
            positionalThreshold = { totalDistance -> totalDistance * 0.5f },
            velocityThreshold = { with(density) { 125.dp.toPx() } },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, (state.requireOffset()).roundToInt()) }
                .anchoredDraggable(state, Orientation.Vertical),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().height(600.dp).padding(16.dp)) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sheet Content")
            }
        }
    }
}
```

### Параллакс-хедер при скролле

```kotlin
@Composable
fun ParallaxHeader(scrollState: ScrollState) {
    val scrollOffset = scrollState.value.toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .graphicsLayer {
                translationY = scrollOffset * 0.6f // Параллакс-фактор
                scaleX = 1f + (scrollOffset * 0.001f).coerceAtLeast(0f)
                scaleY = 1f + (scrollOffset * 0.001f).coerceAtLeast(0f)
                alpha = (1f - (scrollOffset / 600f)).coerceIn(0f, 1f)
            }
    ) {
        Image(
            painter = painterResource(R.drawable.header),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
```

### Анимированное переключение вкладок

```kotlin
@Composable
fun AnimatedTabContent(selectedTabIndex: Int) {
    AnimatedContent(
        targetState = selectedTabIndex,
        transitionSpec = {
            val direction = if (targetState > initialState) 1 else -1
            slideInHorizontally(
                initialOffsetX = { fullWidth -> direction * fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)) togetherWith
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -direction * fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300)) using
            SizeTransform(clip = false)
        },
        label = "tab_content"
    ) { tabIndex ->
        when (tabIndex) {
            0 -> TabOneContent()
            1 -> TabTwoContent()
            2 -> TabThreeContent()
        }
    }
}
```

---

## Хореография: последовательные/параллельные анимации

### Последовательно (цепочка корутин)

Каждый `animateTo` приостанавливается до завершения, поэтому цепочка идёт последовательно:

```kotlin
val alpha = remember { Animatable(0f) }
val translateY = remember { Animatable(100f) }
val scale = remember { Animatable(0.5f) }

LaunchedEffect(Unit) {
    alpha.animateTo(1f, animationSpec = tween(300))
    translateY.animateTo(0f, animationSpec = spring())
    scale.animateTo(1f, animationSpec = tween(200))
}
```

### Параллельно (несколько launch)

```kotlin
val alpha = remember { Animatable(0f) }
val translateY = remember { Animatable(100f) }

LaunchedEffect(Unit) {
    coroutineScope {
        launch { alpha.animateTo(1f, animationSpec = tween(300)) }
        launch { translateY.animateTo(0f, animationSpec = spring()) }
    }
    // Код здесь выполнится после завершения ОБЕИХ анимаций
}
```

### Staggered-задержки

```kotlin
val items = remember { List(5) { Animatable(0f) } }

LaunchedEffect(Unit) {
    items.forEachIndexed { index, animatable ->
        launch {
            delay(index * 80L)
            animatable.animateTo(1f, animationSpec = spring())
        }
    }
}
```

### Смешано: последовательно + параллельно

```kotlin
LaunchedEffect(Unit) {
    // Фаза 1: Последовательно — сначала fade in
    alpha.animateTo(1f, animationSpec = tween(200))

    // Фаза 2: Параллельно — двигаемся и масштабируемся одновременно
    coroutineScope {
        launch { translateY.animateTo(0f, animationSpec = spring()) }
        launch { scale.animateTo(1f, animationSpec = spring()) }
    }

    // Фаза 3: Последовательно — финальный штрих после Фазы 2
    rotation.animateTo(360f, animationSpec = tween(400))
}
```

---

## Predictive Back Gesture (Android 14+)

### NavHost transitions

```kotlin
NavHost(
    navController = navController,
    startDestination = "home",
    enterTransition = {
        slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(300))
    },
    exitTransition = {
        slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut(animationSpec = tween(300))
    },
    popEnterTransition = {
        slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn(animationSpec = tween(300))
    },
    popExitTransition = {
        slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(300))
    }
) {
    composable("home") { HomeScreen() }
    composable("detail") { DetailScreen() }
}
```

### PredictiveBackHandler

```kotlin
@Composable
fun PredictiveBackExample(onBack: () -> Unit) {
    var boxScale by remember { mutableFloatStateOf(1f) }

    PredictiveBackHandler(enabled = true) { progress: Flow<BackEventCompat> ->
        try {
            progress.collect { backEvent ->
                boxScale = 1f - (0.3f * backEvent.progress)
            }
            onBack()
        } catch (e: CancellationException) {
            boxScale = 1f
            throw e
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = boxScale
                scaleY = boxScale
            }
    ) {
        Text("Свайпни назад, чтобы увидеть scale-анимацию")
    }
}
```

### Автоматический Predictive Back в M3

Эти Material 3 компоненты анимируются под predictive back из коробки (без дополнительного кода):

- `SearchBar` — сворачивается на свайпе
- `ModalBottomSheet` — сдвигается вниз с прогрессом жеста
- `ModalNavigationDrawer` — закрывается с прогрессом жеста

---

## Дополнительные антипаттерны

### Не делай: чтение анимируемого state в композиции, когда хватает draw-фазы

```kotlin
// ПЛОХО: читает alpha в композиции, рекомпозиция на каждом кадре
val alpha by animateFloatAsState(targetValue = 0.5f, label = "alpha")
Box(modifier = Modifier.alpha(alpha))

// ХОРОШО: читает alpha только в draw-фазе, без рекомпозиции
val alpha by animateFloatAsState(targetValue = 0.5f, label = "alpha")
Box(modifier = Modifier.graphicsLayer { this.alpha = alpha })
```

### Не делай: использовать offset(x, y) для анимированного движения

```kotlin
// ПЛОХО: параметрический offset вызывает рекомпозицию + relayout
val animatedDp by animateDpAsState(targetValue = 100.dp, label = "x")
Box(modifier = Modifier.offset(x = animatedDp))

// ЛУЧШЕ: лямбда-offset — только layout-фаза, без рекомпозиции
val animatedPx by animateIntAsState(targetValue = 300, label = "x")
Box(modifier = Modifier.offset { IntOffset(animatedPx, 0) })

// ЛУЧШЕЕ: graphicsLayer — только draw-фаза
val animatedPx by animateFloatAsState(targetValue = 300f, label = "x")
Box(modifier = Modifier.graphicsLayer { translationX = animatedPx })
```

### Не делай: использовать updateTransition для независимых свойств

```kotlin
// ПЛОХО: свойствам не нужна синхронизация, но они связаны
val transition = updateTransition(targetState = state, label = "t")
val alpha by transition.animateFloat(label = "a") { if (it) 1f else 0f }
val size by transition.animateDp(label = "s") { if (it) 200.dp else 100.dp }

// ХОРОШО: независимые свойства — отдельные animate*AsState
val alpha by animateFloatAsState(targetValue = if (state) 1f else 0f, label = "alpha")
val size by animateDpAsState(targetValue = if (state) 200.dp else 100.dp, label = "size")
```

### Не делай: хардкодить произвольные длительности

```kotlin
// ПЛОХО: произвольная длительность без дизайн-обоснования
val anim by animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(durationMillis = 347),
    label = "anim"
)

// ХОРОШО: используй M3 motion-токены для консистентности
val anim by animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(durationMillis = MotionTokens.DurationMedium2.toInt()),
    label = "anim"
)

// ЛУЧШЕ: spring() для прерываемых, естественно ощущающихся анимаций
val anim by animateFloatAsState(
    targetValue = 1f,
    animationSpec = spring(stiffness = Spring.StiffnessMedium),
    label = "anim"
)
```
