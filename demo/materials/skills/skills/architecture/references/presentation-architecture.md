# Presentation Layer (MVVM + Decompose + Compose)

Целевой паттерн презентационного слоя во всем приложении — **MVVM**: `ViewModel` владеет состоянием экрана и логикой UI, Decompose-`Component` — это тонкая обёртка, которая привязывает ViewModel к жизненному циклу и навигации, а Compose-`Screen` — чистый рендер.
Всегда используй MVVM для реализации фичей в presentation слое.

```
Compose Screen  ───►  Component  ───►  ViewModel  ───►  UseCase  ───►  Repository
                       (Decompose)      (StateFlow)
```

---

## ViewModel

Plain Kotlin class в `commonMain`. **Не** наследуется от Android `androidx.lifecycle.ViewModel` — это KMP common, поэтому ViewModel — обычный класс, время жизни которым управляет Component через `instanceKeeper` или `componentScope`.

```kotlin
// presentation/feature_screen/viewmodel/FeatureViewModel.kt
internal class FeatureViewModel(
    private val getItemsUseCase: GetItemsUseCase,
    private val viewModelScope: CoroutineScope,
) {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _navigateToDetails = Channel<Long>(Channel.BUFFERED)
    val navigateToDetails = _navigateToDetails.receiveAsFlow()

    init {
        loadItems()
    }

    fun onLoadClicked() {
        loadItems()
    }

    fun onItemClicked(id: Long) {
        _navigateToDetails.trySend(id)
    }

    private fun loadItems() {
        _state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            getItemsUseCase(Unit).fold(
                onSuccess = { items ->
                    _state.update { it.copy(items = items, isLoading = false) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, error = error) }
                },
            )
        }
    }

    data class State(
        val items: List<Item> = emptyList(),
        val isLoading: Boolean = false,
        val error: Throwable? = null,
    )
}
```

**Правила ViewModel:**

| Правило | Описание |
|---------|----------|
| Базовый класс | Plain Kotlin class, без Android `ViewModel` |
| Состояние | `MutableStateFlow<State>` приватный + публичный `StateFlow<State>` |
| Изменение состояния | Только через `_state.update { it.copy(...) }` — атомарно |
| Корутины | Запускать в `viewModelScope`, который инжектится снаружи (это `componentScope` Component-а) |
| Public API | Только `state`, one-shot Flow и `fun onXxx()` — без `suspend`, без возврата значений |
| State | `data class`, immutable, дефолтные значения |
| Бизнес-логика | Делегируется в UseCase, ViewModel оркестрирует |
| Навигация | НЕ здесь — VM эмитит one-shot событие, навигацию делает Component через `Callbacks` |

---

## Continuous state vs one-shot events

Чёткое разделение: **state** — то, что описывает экран в любой момент времени; **event** — одноразовое действие, не имеющее «текущего значения».

| | StateFlow | Channel / SharedFlow |
|---|-----------|----------------------|
| **Что** | Текущее состояние UI | Навигация, toast, snackbar |
| **Replay** | Всегда отдаёт последнее значение | Доставляется один раз |
| **Где живёт** | `val state: StateFlow<State>` | `val navigateToX: Flow<Payload>` |
| **Откуда читается** | Compose: `collectAsState()` | Component: `.onEach { callbacks.onX(it) }.launchIn(componentScope)` |

**Правило:** если поле описывает «что показывается» — оно в State. Если описывает «что произошло один раз» — оно в Channel/SharedFlow.

---

## Component (Decompose-обёртка)

Component — тонкая прослойка между Decompose-навигацией и ViewModel. Сам не содержит UI-логики.

```kotlin
// presentation/feature_screen/component/FeatureComponentImpl.kt
@AssistedInject
class FeatureComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val callbacks: FeatureComponent.Callbacks,
    private val getItemsUseCase: GetItemsUseCase,
) : BaseComponent(componentContext), FeatureComponent {

    private val viewModel = instanceKeeper.getOrCreate {
        RetainedViewModel(
            FeatureViewModel(
                getItemsUseCase = getItemsUseCase,
                viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate),
            )
        )
    }.viewModel

    override val uiState: StateFlow<FeatureComponent.UiState> = viewModel.state
        .map { it.toUi() }
        .stateIn(
            scope = componentScope,
            started = SharingStarted.Eagerly,
            initialValue = FeatureViewModel.State().toUi(),
        )

    init {
        viewModel.navigateToDetails
            .onEach { id -> callbacks.onOpenDetails(id) }
            .launchIn(componentScope)
    }

    override fun onLoadClicked() = viewModel.onLoadClicked()
    override fun onItemClicked(id: Long) = viewModel.onItemClicked(id)
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

    private class RetainedViewModel(val viewModel: FeatureViewModel) : InstanceKeeper.Instance {
        override fun onDestroy() {
            (viewModel as? AutoCloseable)?.close()
        }
    }
}
```

**Правила Component:**

- Наследуется от `BaseComponent(componentContext)` (см. `core-navigation`).
- Реализует `<Feature>Component` из `*-api`.
- Создаёт ViewModel **через `instanceKeeper.getOrCreate {}`** — чтобы пережить пересоздания (process death на Android).
- Если ViewModel простая и без долгоживущих ресурсов — допустимо `private val viewModel = FeatureViewModel(...)` (как в текущем `RegistrationComponentImpl`).
- `componentScope` (от `BaseComponent`) — для подписок Component-а на потоки VM. Сам же VM использует свой `viewModelScope`, привязанный к `instanceKeeper`.
- Подписывает one-shot Flow VM в `componentScope` и вызывает `callbacks` — навигация только так.
- Делегирует все action-методы в VM.

---

## State Mapping (опционально)

Применяется, когда внутреннее состояние VM шире, чем публичный `UiState`, или содержит доменные модели.

```kotlin
// presentation/feature_screen/mapping/StateMapping.kt
internal fun FeatureViewModel.State.toUi(): FeatureComponent.UiState {
    return FeatureComponent.UiState(
        items = items.map { it.toUiItem() },
        isLoading = isLoading,
        error = error?.message,
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

Если ViewModel хранит сразу `UiState` (как `RegistrationViewModel`) — mapping не нужен, и `override val uiState = viewModel.state` достаточно.

---

## Screen (Compose UI)

Screen — чистый рендер. Не знает про ViewModel, не дёргает UseCase, не делает навигацию.

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

- Получает `Component` (а не VM) — это сохраняет границу: Screen работает с публичным контрактом из `*-api`.
- `collectAsState()` для подписки на StateFlow.
- Внутренний `private @Composable Content(state, onX, ...)` принимает чистый state и колбэки — для preview и тестируемости.
- Нет логики `LaunchedEffect`, кроме рендер-зависимой (анимации, фокус). Бизнес-логика — в VM.

---

## Сводка контрактов и зон ответственности

| Слой | Где живёт | За что отвечает | Чего НЕ делает |
|------|-----------|-----------------|----------------|
| `*-api` `FeatureComponent` | `feature-api/.../FeatureComponent.kt` | Публичный контракт: `uiState`, action-методы, `Callbacks`, `UiState`, `Factory` | Не знает про VM, корутины, UseCase |
| `ViewModel` | `feature-impl/.../viewmodel/FeatureViewModel.kt` | State, one-shot события, оркестрация UseCase | Не знает про Decompose, Compose, навигацию |
| `Component` | `feature-impl/.../component/FeatureComponentImpl.kt` | Связь VM ↔ Decompose, навигация через `Callbacks`, mapping | Не содержит бизнес-логики |
| `Mapping` | `feature-impl/.../mapping/StateMapping.kt` | `VM.State → UiState` | Не делает асинхронных операций |
| `Screen` | `feature-impl/.../component/FeatureScreen.kt` | Рендер `UiState` | Не вызывает UseCase, не управляет состоянием |
