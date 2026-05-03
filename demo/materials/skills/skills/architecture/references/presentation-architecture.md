# Presentation Layer (MVI / MviKotlin + Decompose + Jetpack Compose)

Целевой паттерн презентационного слоя — **MVI на MviKotlin**: `Store<Intent, State, Label>` владеет состоянием и оркестрирует side-effects, Decompose-`Component` — тонкая обёртка между Store и навигацией, Compose-`Screen` — чистый рендер.

```
Compose Screen  ───►  Component  ───►  Store  ───►  UseCase  ───►  Repository
                       (Decompose)     (MviKotlin)
                                       ├─ executor (side effects, UseCase)
                                       └─ reducer (pure: Msg → State)
```

>  **Один паттерн на все экраны - MviKotlin Store**. Даже если экран содержит только локальную валидацию формы — он всё равно реализуется через MviKotlin Store. Так выдерживается единообразие, тестируемость и предсказуемость.

---

## Store: Intent, State, Label, Msg

`Store<Intent, State, Label>` — публичный интерфейс из MviKotlin.

| Параметр | Что это | Пример |
|----------|---------|--------|
| **Intent** | Что хочет пользователь / экран | `Intent.LoadItems`, `Intent.ItemClicked(id)` |
| **State** | Текущее состояние экрана | `State(items, isLoading, error)` |
| **Label** | One-shot событие наружу (навигация, toast) | `Label.NavigateToDetails(id)` |
| **Msg** (внутренний) | Результат side-effect для reducer'а | `Msg.ItemsLoaded(list)`, `Msg.LoadingStarted` |

`Msg` — приватный sealed interface внутри `StoreFactory`. Снаружи виден только `Intent`. Это разделение даёт «UI-вход» (Intent) и «системный апдейт состояния» (Msg).

```kotlin
// presentation/feature_screen/mvi/FeatureStore.kt
internal interface FeatureStore : Store<FeatureStore.Intent, FeatureStore.State, FeatureStore.Label> {

    sealed interface Intent {
        data object Load : Intent
        data class ItemClicked(val id: Long) : Intent
    }

    data class State(
        val items: List<Item> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
    )

    sealed interface Label {
        data class NavigateToDetails(val id: Long) : Label
    }
}
```

---

## StoreFactory

`StoreFactory` (фича-локальный, инжектится через Metro) создаёт `Store`. `MviKotlin` `StoreFactory` (`DefaultStoreFactory`) поставляется через `AppGraph`.

```kotlin
// presentation/feature_screen/mvi/FeatureStoreFactory.kt
@Inject
class FeatureStoreFactory(
    private val storeFactory: StoreFactory,
    private val getItemsUseCase: GetItemsUseCase,
) {

    internal fun create(): FeatureStore =
        object : FeatureStore, Store<Intent, State, Label> by storeFactory.create(
            name = "FeatureStore",
            initialState = State(),
            executorFactory = coroutineExecutorFactory<Intent, Nothing, State, Msg, Label> {
                onIntent<Intent.Load> {
                    dispatch(Msg.LoadingStarted)
                    launch {
                        getItemsUseCase(Unit).fold(
                            onSuccess = { dispatch(Msg.ItemsLoaded(it)) },
                            onFailure = { dispatch(Msg.LoadFailed(it.message ?: "Error")) },
                        )
                    }
                }
                onIntent<Intent.ItemClicked> { intent ->
                    publish(Label.NavigateToDetails(intent.id))
                }
            },
            reducer = Reducer<State, Msg> { msg ->
                when (msg) {
                    Msg.LoadingStarted -> copy(isLoading = true, error = null)
                    is Msg.ItemsLoaded -> copy(items = msg.items, isLoading = false)
                    is Msg.LoadFailed -> copy(isLoading = false, error = msg.message)
                }
            },
        ) {}

    internal sealed interface Msg {
        data object LoadingStarted : Msg
        data class ItemsLoaded(val items: List<Item>) : Msg
        data class LoadFailed(val message: String) : Msg
    }
}
```

**Правила Store / StoreFactory:**

| Правило | Описание |
|---------|----------|
| Generic-порядок | `storeFactory.create<Intent, Action, State, Message, Label>` и `coroutineExecutorFactory<Intent, Action, State, Message, Label>` — **этот порядок строго соблюдать** |
| `Action` | Если нет — указывать `Nothing`. Используется для `bootstrapper` (стартовые действия — например, авто-загрузка). |
| Reducer | Чистая функция `(State, Msg) -> State`. **Без** `if-else` с side-effect, без обращения к UseCase, без обращения к UI. |
| Side effects | Только в `coroutineExecutorFactory` через `onIntent` / `onAction`. UseCase вызываются именно отсюда. |
| Msg | `internal` или `private` sealed interface — наружу не должен торчать |
| Label | One-shot события для Component. Не дублирует State. |
| Имя Store | `name = "FeatureStore"` для логирования и time-travel |

---

## Continuous state vs one-shot events

Чёткое разделение: **State** — то, что описывает экран в любой момент времени; **Label** — одноразовое действие, не имеющее «текущего значения».

| | State (`store.stateFlow`) | Label (`store.labels`) |
|---|---------------------------|------------------------|
| **Что** | Текущее состояние UI | Навигация, toast, snackbar |
| **Replay** | Всегда отдаёт последнее значение | Доставляется один раз |
| **Где живёт** | `state` экрана | `Label` sealed interface |
| **Откуда читается** | Compose: `store.stateFlow.collectAsState()` (или маппинг в Component) | Component: `store.labels.collect { ... }` |

**Правило:** если поле описывает «что показывается» — оно в State. Если описывает «что произошло один раз» — оно в Label.

---

## Component (Decompose-обёртка)

```kotlin
// presentation/feature_screen/component/FeatureComponentImpl.kt
@AssistedInject
class FeatureComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val callbacks: FeatureComponent.Callbacks,
    storeFactory: FeatureStoreFactory,
) : BaseComponent(componentContext), FeatureComponent {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    override val uiState: StateFlow<FeatureComponent.UiState> = store.stateFlow
        .map { it.toUi() }
        .stateIn(
            scope = componentScope,
            started = SharingStarted.Eagerly,
            initialValue = FeatureStore.State().toUi(),
        )

    init {
        store.labels
            .onEach { label ->
                when (label) {
                    is FeatureStore.Label.NavigateToDetails -> callbacks.onOpenDetails(label.id)
                }
            }
            .launchIn(componentScope)
    }

    override fun onLoadClicked() = store.accept(FeatureStore.Intent.Load)
    override fun onItemClicked(id: Long) = store.accept(FeatureStore.Intent.ItemClicked(id))
    override fun onBackClicked() = callbacks.onBack()

    @Composable
    override fun render(modifier: Modifier) {
        FeatureScreen(component = this, modifier = modifier)
    }

    @AssistedFactory
    interface Factory : FeatureComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            callbacks: FeatureComponent.Callbacks,
        ): FeatureComponentImpl
    }
}
```

**Правила Component:**

- Наследуется от `BaseComponent(componentContext)` (см. `core-navigation`).
- Реализует `<Feature>Component` из `*-api`.
- Создаёт Store через `instanceKeeper.getStore { storeFactory.create() }` (`com.itapp.core_architecture.getStore`) — Store переживает пересоздания.
- Подписывает `store.labels` в `componentScope` и вызывает `callbacks` — навигация только так.
- `store.stateFlow` мапится в `UiState` через `.map { it.toUi() }.stateIn(componentScope, ...)`.
- Action-методы — это `store.accept(Intent.X)`. Никакой бизнес-логики в Component нет.

---

## State Mapping (опционально)

Применяется, когда `Store.State` шире, чем публичный `UiState`, или содержит доменные модели.

```kotlin
// presentation/feature_screen/mapping/StateMapping.kt
internal fun FeatureStore.State.toUi(): FeatureComponent.UiState {
    return FeatureComponent.UiState(
        items = items.map { it.toUiItem() },
        isLoading = isLoading,
        error = error,
    )
}

private fun Item.toUiItem(): UiItem {
    return UiItem(
        id = id,
        title = name,
        subtitle = formatPrice(price),
    )
}
```

Если `Store.State` уже совпадает с `UiState` — mapping не нужен.

---

## Screen (Compose UI)

Screen — чистый рендер. Не знает про Store, не дёргает UseCase, не делает навигацию.

```kotlin
// presentation/feature_screen/component/FeatureScreen.kt
@Composable
internal fun FeatureScreen(
    component: FeatureComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.uiState.collectAsState()

    FeatureContent(
        state = state,
        onLoadClick = component::onLoadClicked,
        onItemClick = component::onItemClicked,
        onBackClick = component::onBackClicked,
        modifier = modifier,
    )
}

@Composable
private fun FeatureContent(
    state: FeatureComponent.UiState,
    onLoadClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            state.error != null -> ErrorState(state.error, onLoadClick)
            else -> ItemsList(state.items, onItemClick)
        }
    }
}

@Composable
private fun ItemsList(
    items: List<UiItem>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(
            items = items,
            key = { it.id },
        ) { item ->
            ItemCard(
                item = item,
                onClick = { onItemClick(item.id) },
            )
        }
    }
}
```

**Правила Screen:**

- Получает `Component` (а не Store) — это сохраняет границу: Screen работает с публичным контрактом из `*-api`.
- `collectAsState()` для подписки на StateFlow.
- Внутренний `private @Composable Content(state, onX, ...)` принимает чистый state и колбэки — для preview и тестируемости.
- Нет `LaunchedEffect` с бизнес-логикой. Бизнес-логика — в Store executor.

---

## Сводка контрактов и зон ответственности

| Слой | Где живёт | За что отвечает | Чего НЕ делает |
|------|-----------|-----------------|----------------|
| `*-api` `FeatureComponent` | `feature-api/.../FeatureComponent.kt` | Публичный контракт: `uiState`, action-методы, `Callbacks`, `UiState`, `Factory` | Не знает про Store, корутины, UseCase |
| `Store` | `feature-impl/.../mvi/FeatureStore.kt` | Контракт MVI: `Intent`, `State`, `Label` | Без реализации (только interface + types) |
| `StoreFactory` | `feature-impl/.../mvi/FeatureStoreFactory.kt` | Реализация Store: executor (side effects + UseCase) + reducer (pure) | Не делает навигацию (только публикует Label) |
| `Component` | `feature-impl/.../component/FeatureComponentImpl.kt` | Связь Store ↔ Decompose, навигация через `Callbacks`, mapping State | Не содержит бизнес-логики |
| `Mapping` | `feature-impl/.../mapping/StateMapping.kt` | `Store.State → UiState` | Не делает асинхронных операций |
| `Screen` | `feature-impl/.../component/FeatureScreen.kt` | Рендер `UiState` | Не вызывает UseCase, не управляет состоянием |
