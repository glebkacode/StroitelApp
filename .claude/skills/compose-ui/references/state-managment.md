# Управление состоянием в Jetpack Compose

## Основы состояния

Состояние в Compose — это наблюдаемые данные, которые запускают рекомпозицию при изменении.

### Создание состояния

Используй type-specific state-холдеры для эффективности:

```kotlin
// Универсальное состояние (любой тип)
val name = mutableStateOf("Alice")

// Специализации для примитивов (избегают boxing)
val count = mutableIntStateOf(0)
val progress = mutableFloatStateOf(0.5f)
val enabled = mutableStateOf(true)  // У Boolean нет специализации
```

**Подводный камень:** использование `mutableStateOf<Int>()` вместо `mutableIntStateOf()` приводит к ненужному boxing на каждом чтении/записи. Специализации для примитивов находятся в `androidx.compose.runtime` (источник: `State.kt`).

## remember vs rememberSaveable

Оба связывают состояние с ключом композиции, но различаются по области сохранения.

### remember
- Живёт всё время существования композиции
- Теряется при смерти процесса, изменении конфигурации, навигации назад
- Подходит для UI-состояния: выделение, expanded/collapsed, позиция скролла

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableIntStateOf(0) }
    Button(onClick = { count++ }) {
        Text("Count: $count")
    }
}
```

### rememberSaveable
- Переживает смерть процесса и изменение конфигурации
- По умолчанию работает с типами, совместимыми с `Bundle` (String, Int, Boolean и т. д.)
- Для кастомных типов нужен `Saver` или `@Parcelize`
- Подходит для данных, представляющих пользовательский ввод или состояние навигации

```kotlin
@Composable
fun SearchScreen() {
    var query by rememberSaveable { mutableStateOf("") }
    // переживёт изменение конфигурации
}

// Кастомный тип требует явного Saver
data class User(val id: Int, val name: String)
val userSaver = Saver<User, String>(
    save = { "${it.id}:${it.name}" },
    restore = { parts -> User(parts.split(":")[0].toInt(), parts.split(":")[1]) }
)
var user by rememberSaveable(stateSaver = userSaver) { mutableStateOf(User(1, "Alice")) }
```

**Подводный камень:** допущение, что `rememberSaveable` работает со всеми типами. Кастомным классам нужен явный `Saver` или `@Parcelize`. См. `SaveableStateRegistry` в `androidx.compose.runtime.saveable`.

## State Hoisting

Поднимай состояние в родительский composable, чтобы обеспечить переиспользуемость и тестируемость.

### Шаблон Stateful vs Stateless

```kotlin
// ❌ Stateful-вариант (жёсткая связанность)
@Composable
fun Counter() {
    var count by remember { mutableIntStateOf(0) }
    Button(onClick = { count++ }) { Text(count.toString()) }
}

// ✅ Stateless-вариант (переиспользуемый, тестируемый)
@Composable
fun Counter(
    count: Int,
    onCountChange: (Int) -> Unit
) {
    Button(onClick = { onCountChange(count + 1) }) { Text(count.toString()) }
}

// ✅ Composable-обёртка (предоставляет состояние, использует stateless-потомка)
@Composable
fun StatefulCounter() {
    var count by remember { mutableIntStateOf(0) }
    Counter(count = count, onCountChange = { count = it })
}
```

**Правило:** поднимай состояние настолько высоко, насколько нужно, но не выше. Если состояние нужно только одному потомку — оставь его там. Если оно нужно нескольким потомкам или родителям — поднимай выше.

## derivedStateOf

Вычисляет значение из существующего состояния, пересчитывая его только при изменении зависимостей.

```kotlin
// ❌ Неправильно: пересчитывается при каждой рекомпозиции
val isEven = count % 2 == 0

// ✅ Правильно: пересчитывается только при изменении count
val isEven = derivedStateOf { count % 2 == 0 }
```

**Когда использовать:**
- Дорогие вычисления из состояния (например, фильтрация, сортировка списков)
- Объединение нескольких значений состояния
- Создание промежуточного состояния для условной логики

```kotlin
@Composable
fun UserList(users: List<User>, filterText: String) {
    val filteredUsers = derivedStateOf {
        users.filter { it.name.contains(filterText, ignoreCase = true) }
    }

    LazyColumn {
        items(filteredUsers.value.size) { index ->
            UserRow(filteredUsers.value[index])
        }
    }
}
```

**Подводный камень:** использование `derivedStateOf` для дешёвых операций (конкатенация строк, простые условия) добавляет накладные расходы. Используй только когда вычисление нетривиальное.

**Подводный камень:** обращение к `.value` внутри лямбды, переданной в дочерний composable, не создаёт зависимости. Используй `snapshotFlow` для колбэков.

## snapshotFlow

Преобразует Compose State в Kotlin Flow для side effects и внешних API.

```kotlin
@Composable
fun SearchScreen(component: SearchComponent) {
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(500)
            .distinctUntilChanged()
            .collect { component.onSearchQueryChanged(it) }
    }
}
```

**Ключевые особенности:**
- Эмитит начальное значение, затем — только изменения
- Работает с derivedStateOf, коллекциями и вложенным состоянием
- Запускается в скоупе композиции (через `LaunchedEffect`)

**Подводный камень:** прямое обращение к состоянию внутри `LaunchedEffect` не отслеживает изменения:
```kotlin
// ❌ Не перезапустится при изменении query
LaunchedEffect(Unit) {
    component.onSearchQueryChanged(query)  // Захватывается только при запуске
}

// ✅ Перезапускается при изменении query
LaunchedEffect(query) {
    component.onSearchQueryChanged(query)
}
```

## SnapshotStateList и SnapshotStateMap

Наблюдаемые коллекции, которые запускают рекомпозицию при структурных изменениях.

```kotlin
val items = remember { mutableStateListOf<Item>() }
items.add(Item(1, "First"))
items[0] = Item(1, "Updated")
items.removeAt(0)

val map = remember { mutableStateMapOf<String, String>() }
map["key"] = "value"  // Запускает рекомпозицию
```

**Важно:** изменения содержимого списка запускают рекомпозицию, а изменения *элементов* (если это мутабельные объекты) — нет.

```kotlin
data class Item(val id: Int, var name: String)

val items = remember { mutableStateListOf(Item(1, "First")) }

// ✅ Запускает рекомпозицию (изменилась структура списка)
items[0] = Item(1, "Updated")

// ❌ НЕ запускает рекомпозицию (объект мутирован in-place)
items[0].name = "Updated"  // Мутирован, но ссылка в списке не изменилась

// ✅ Правильно: используй copy() или mutableStateOf для вложенного состояния
items[0] = items[0].copy(name = "Updated")
```

См. источник: `androidx.compose.runtime.snapshots` — реализация коллекций.

## Аннотации @Stable и @Immutable

Эти аннотации помогают компилятору оптимизировать рекомпозицию (strong skipping mode).

### @Immutable
- Все публичные поля — read-only примитивы или другие `@Immutable`-типы
- Инстансы никогда не изменяются после создания
- Компилятор может пропустить рекомпозицию, если параметр не изменился

```kotlin
@Immutable
data class User(val id: Int, val name: String)
```

### @Stable
- Реализует структурное равенство (`equals`)
- Публичные свойства — read-only или наблюдаемые
- Об изменениях всегда уведомляется Compose (через state-объекты)
- Более слабая гарантия, чем `@Immutable`, но подходит для типов с наблюдаемым состоянием

```kotlin
@Stable
class UserStateHolder {
    val userName: State<String> = mutableStateOf("")
    val isLoading: State<Boolean> = mutableStateOf(false)

    // Наблюдаемое состояние, а не прямые свойства
}
```

**Подводный камень:** не аннотировать data-классы, используемые как параметры. Без аннотаций типы считаются нестабильными, что вызывает ненужные рекомпозиции.

```kotlin
// ❌ Считается нестабильным, вызывает рекомпозицию
class Config(val title: String, val color: Color)

// ✅ Правильно аннотирован
@Immutable
class Config(val title: String, val color: Color)
```

## Strong Skipping Mode

В Compose 1.6+ режим strong skipping применяет более строгую логику рекомпозиции.

**Что изменилось:**
- Composable пропускает рекомпозицию, если *все* параметры имеют неизменную идентичность и значение
- Неаннотированные типы параметров считаются нестабильными (всегда рекомпозируются)
- Аннотации `@Stable` и `@Immutable` теперь критичны для производительности
- Лямбда-параметры всегда вызывают рекомпозицию (это новые инстансы)

**Включение strong skipping:**
```gradle
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.4+"  // включён по умолчанию
}
```

**Практическое влияние:**
```kotlin
// ❌ Создают новые инстансы, всегда рекомпозируют дочерний composable
@Composable
fun Parent() {
    Child(title = buildString { append("Title") })
    Child(config = Config(...))  // Нестабильный тип
}

// ✅ Кэшируй инстансы
@Composable
fun Parent() {
    val title = remember { "Title" }
    val config = remember { Config(...) }
    Child(title = title)
    Child(config = config)
}
```

## Состояние в Store + Component vs Compose State

В этом проекте presentation-слой строится на **MviKotlin Store + Decompose Component** (см. CLAUDE.md).

### StateFlow в Store + Component (рекомендуется)
- Состояние живёт в `Store<Intent, State, Label>` и переживает изменение конфигурации через `instanceKeeper`
- Component мапит `store.stateFlow` в публичный `UiState` через `stateIn(componentScope, ...)`
- Screen собирает `component.uiState.collectAsState()` — без `collectAsStateWithLifecycle`, т. к. жизненный цикл уже привязан к `componentScope` Decompose

```kotlin
// Store: внутренний State редьюсера
internal interface UserStore : Store<Intent, State, Label> {
    data class State(val isLoading: Boolean = true, val user: User? = null, val error: String? = null)
    sealed interface Intent { /* ... */ }
    sealed interface Label { /* one-shot события */ }
}

// Component: тонкая обёртка, владеет Store
@AssistedInject
class UserComponentImpl(
    @Assisted componentContext: ComponentContext,
    storeFactory: UserStoreFactory,
) : BaseComponent(componentContext), UserComponent {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    override val uiState: StateFlow<UserComponent.UiState> =
        store.stateFlow
            .map { it.toUi() }
            .stateIn(componentScope, SharingStarted.Lazily, UserComponent.UiState.Loading)
}

// Screen: только рендер
@Composable
fun UserScreen(component: UserComponent) {
    val uiState by component.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Success -> SuccessScreen(state.data)
        is UiState.Error -> ErrorScreen(state.message)
    }
}
```

### Compose State (Только для UI-состояния)
- Используй для временного, локального UI-состояния, которое не нужно никому за пределами этого composable
- Не поднимай в Store/Component (например, expanded/collapsed чисто визуального блока, focus, фокус-стейт текстового поля без бизнес-валидации)
- Теряется при навигации назад

```kotlin
@Composable
fun SearchScreen(component: SearchComponent) {
    var showFilters by remember { mutableStateOf(false) }  // Только UI
    val uiState by component.uiState.collectAsState()

    SearchUI(
        results = uiState.results,
        showFilters = showFilters,
        onToggleFilters = { showFilters = !showFilters },
        onQueryChanged = component::onSearchQueryChanged,
    )
}
```

**Ключевое отличие:** жизненный цикл `component.uiState` управляется `componentScope` (Decompose `BaseComponent`), который отменяется при destroy компонента. Поэтому в проекте используется `collectAsState()` без lifecycle-aware варианта.

## Распространённые антипаттерны

### Состояние в локальных переменных
```kotlin
// ❌ Теряется при рекомпозиции
@Composable
fun Counter() {
    var count = 0  // Сбрасывается в 0 на каждой рекомпозиции
    Button(onClick = { count++ }) { Text(count.toString()) }
}

// ✅ Правильно
@Composable
fun Counter() {
    var count by remember { mutableIntStateOf(0) }
    Button(onClick = { count++ }) { Text(count.toString()) }
}
```

### Чтение состояния в неправильном скоупе
```kotlin
// ❌ Чтения происходят внутри лямбды; изменения не перезапускают эффект
var count by remember { mutableIntStateOf(0) }
LaunchedEffect(Unit) {
    while (true) {
        delay(1000)
        println(count)  // Всегда печатает 0
    }
}

// ✅ Передавай состояние как ключ LaunchedEffect
LaunchedEffect(count) {
    println("Count changed: $count")
}
```

### Создание состояния в лямбдах
```kotlin
// ❌ Создаёт новое состояние при каждом вызове
val onButtonClick = {
    val newValue = remember { mutableStateOf(0) }  // ОШИБКА: нельзя вызывать remember в лямбде
}

// ✅ Создавай состояние на уровне композиции
var value by remember { mutableIntStateOf(0) }
val onButtonClick = { value++ }
```

---

**Ссылки на источники:** `androidx.compose.runtime.State`, `androidx.compose.runtime.saveable`, `androidx.lifecycle.runtime.compose`

---

## produceState

Мост между suspend-функциями и Compose State:

```kotlin
@Composable
fun UserProfile(userId: String): State<User?> = produceState<User?>(initialValue = null, userId) {
    value = repository.getUser(userId)
}
```

Используй, когда нужно превратить результат suspend-функции в наблюдаемый State. Корутина привязана к скоупу композиции и отменяется при выходе composable.

Также может наблюдать за flow:
```kotlin
@Composable
fun NetworkStatus(): State<Boolean> = produceState(initialValue = false) {
    connectivityManager.observeNetworkState().collect { value = it }
}
```

---

## rememberUpdatedState

Захват актуального значения колбэка в долгоживущих эффектах:

```kotlin
@Composable
fun Timer(onTimeout: () -> Unit) {
    val currentOnTimeout by rememberUpdatedState(onTimeout)
    LaunchedEffect(true) {
        delay(5000L)
        currentOnTimeout() // Всегда вызывает актуальный onTimeout, даже если он изменился
    }
}
```

Используй, когда: LaunchedEffect захватывает колбэк, который может измениться, но ты не хочешь перезапускать эффект. Без `rememberUpdatedState` эффект использовал бы устаревший исходный колбэк или должен был бы перезапускаться при каждом его изменении.

---

## Шаблон Sealed UiState

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

Безопасность smart-cast:
```kotlin
// ПЛОХО: smart cast может не сработать, если uiState изменится между проверкой и использованием
if (uiState is UiState.Success) {
    Content((uiState as UiState.Success).data) // Небезопасный каст
}

// ХОРОШО: захват в val для безопасного smart cast
when (val state = uiState) {
    is UiState.Loading -> LoadingIndicator()
    is UiState.Success -> Content(state.data) // Безопасный smart cast через val
    is UiState.Error -> ErrorMessage(state.message)
}
```

---

## Шаблон State Holder Class

Для сложных экранов с несколькими взаимосвязанными значениями состояния создавай state holder:

```kotlin
@Composable
fun rememberSearchState(
    listState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): SearchState = remember(listState, coroutineScope) {
    SearchState(listState, coroutineScope)
}

@Stable
class SearchState(
    val listState: LazyListState,
    private val coroutineScope: CoroutineScope
) {
    var query by mutableStateOf("")
        private set

    val isScrolled: Boolean
        get() = listState.firstVisibleItemIndex > 0

    fun updateQuery(newQuery: String) { query = newQuery }
    fun scrollToTop() { coroutineScope.launch { listState.animateScrollToItem(0) } }
}
```

Этот шаблон (используется `rememberScrollState`, `rememberDrawerState` и др.) группирует связанное состояние и логику в один класс, избегая разрастания параметров в composable.

---

## Production-правила работы с состоянием

### 1. mutableStateOf ТОЛЬКО в composable, никогда в Store / Component / Executor

Состояние фичи живёт исключительно в `Store.State` (редьюсер MviKotlin). Compose State — для локального UI (раскрытие блока, фокус, hover).

```kotlin
// ПЛОХО: Compose State в Component связывает presentation с Compose runtime
class MyComponentImpl(...) : MyComponent {
    var name by mutableStateOf("") // Так делать НЕЛЬЗЯ — это бизнес-состояние, его место в Store
}

// ПЛОХО: то же самое в Executor
coroutineExecutorFactory {
    onIntent<Intent.Foo> {
        var local by mutableStateOf("") // Compose-state в бизнес-логике — запрещено
    }
}

// ХОРОШО: состояние в Store, наружу — StateFlow<UiState>
internal interface MyStore : Store<Intent, State, Label> {
    data class State(val name: String = "")
    sealed interface Intent { data class NameChanged(val value: String) : Intent }
}

class MyComponentImpl(...) : MyComponent {
    private val store = instanceKeeper.getStore { storeFactory.create() }

    override val uiState: StateFlow<UiState> =
        store.stateFlow.map { it.toUi() }.stateIn(componentScope, SharingStarted.Lazily, UiState())

    override fun onNameChanged(value: String) = store.accept(Intent.NameChanged(value))
}
```

### 2. Labels из Store для one-shot событий, НЕ SharedFlow / Channel в Component

Для навигации, snackbar'ов и других единоразовых эффектов используется механизм `Label` MviKotlin. Component подписывается на `store.labels` в `init` и пробрасывает в `Callbacks`.

```kotlin
// Store: объявляем Label
internal interface RegistrationStore : Store<Intent, State, Label> {
    sealed interface Label {
        data object NavigateToSuccess : Label
        data class ShowError(val message: String) : Label
    }
}

// Executor: публикуем Label из бизнес-логики
coroutineExecutorFactory<Intent, Nothing, State, Msg, Label> {
    onIntent<Intent.RegisterClicked> {
        // ... логика регистрации ...
        publish(Label.NavigateToSuccess)
    }
}

// Component: подписываемся на labels и дёргаем callbacks
class RegistrationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val callbacks: RegistrationComponent.Callbacks,
    storeFactory: RegistrationStoreFactory,
) : BaseComponent(componentContext), RegistrationComponent {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    init {
        store.labels
            .onEach { label ->
                when (label) {
                    RegistrationStore.Label.NavigateToSuccess -> callbacks.onRegistrationSuccess()
                    is RegistrationStore.Label.ShowError -> callbacks.onError(label.message)
                }
            }
            .launchIn(componentScope)
    }
}
```

`MutableSharedFlow` / `Channel` в Component поверх Store — дублирование механизма Labels и нарушение архитектурной конвенции.

### 3. rememberSaveable только на уровне NavGraph

Используй `rememberSaveable` для состояния уровня экрана (поисковый запрос, выбранная вкладка) в точке входа NavGraph, а не глубоко в дереве composable, где это добавляет ненужные накладные расходы на персистентность.

### 4. snapshotFlow + distinctUntilChanged() для реактивного скролла

```kotlin
LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex }
        .distinctUntilChanged()
        .collect { index -> component.onScrollPositionChanged(index) }
}
```

### 5. .stateIn() с .map() для производных flow

В Component используется `componentScope` из `BaseComponent` (привязан к жизненному циклу Decompose-компонента).

```kotlin
override val uiState: StateFlow<UiState> =
    store.stateFlow
        .map { it.toUi() }
        .stateIn(componentScope, SharingStarted.Lazily, UiState())
```

---

## Заметки по Compose Multiplatform

### rememberSaveable и Bundle

`rememberSaveable`, `Bundle` и `@Parcelize` — **только Android**. На таргетах CMP:

```kotlin
// Android: @Parcelize работает
@Parcelize
data class SearchParams(val query: String, val filters: List<String>) : Parcelable

// CMP: используй @Serializable
@Serializable
data class SearchParams(val query: String, val filters: List<String>)
```

Для персистентности состояния при изменении конфигурации в CMP используй кастомные `Saver`-реализации на основе kotlinx.serialization.

### collectAsStateWithLifecycle

`collectAsStateWithLifecycle()` находится в `androidx.lifecycle:lifecycle-runtime-compose` — это Android-specific.

```kotlin
// Android (вне Decompose): lifecycle-aware, останавливает сбор при паузе
val state by component.uiState.collectAsStateWithLifecycle()

// В нашем проекте (Decompose-Component): жизненный цикл уже у componentScope, используем обычный collectAsState
val state by component.uiState.collectAsState()

// CMP с multiplatform lifecycle (lifecycle-runtime-compose:2.10.0+):
// collectAsStateWithLifecycle() доступен в commonMain
```

В этом проекте `component.uiState` уже отменяется при destroy компонента (`componentScope`), поэтому используется `collectAsState()` без lifecycle-обёртки. В CMP без multiplatform-lifecycle flow продолжают сбор в фоне — учитывай влияние на батарею и производительность.
