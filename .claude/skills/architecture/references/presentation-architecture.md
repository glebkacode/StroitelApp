# Presentation Layer (TEA + Decompose + Compose)

## TEA Contract

Определяет State, Intent, Effect, Event.

```kotlin
// presentation/feature/mvi/FeatureTea.kt
internal interface FeatureTea : Tea<State, Intent, Event> {

    data class State(
        val items: List<Item> = emptyList(),
        val isLoading: Boolean = false,
        val error: Throwable? = null
    )

    sealed interface Intent {
        // От UI
        sealed interface UiIntent : Intent {
            data object LoadClicked : UiIntent
            data class ItemClicked(val id: Long) : UiIntent
        }
        // От Effector
        sealed interface EffectIntent : Intent {
            data class LoadCompleted(val items: List<Item>) : EffectIntent
            data class LoadFailed(val error: Throwable) : EffectIntent
        }
    }

    sealed interface Effect {
        data object LoadItems : Effect
        data class OpenDetails(val id: Long) : Effect
    }

    sealed interface Event {
        data class NavigateToDetails(val id: Long) : Event
    }
}
```

**Правила:**
- `State` — immutable data class с текущим состоянием
- `Intent` — действия (разделяй `UiIntent` и `EffectIntent`)
- `Effect` — команды для Effector (side effects)
- `Event` — одноразовые события для Component (навигация, toast)

---

## Reducer

Чистая функция: State + Intent → State + Effects.

```kotlin
// presentation/feature/mvi/FeatureStoreFactory.kt
private class FeatureReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(intent: Intent) {
        when (intent) {
            is Intent.UiIntent -> reduceUi(intent)
            is Intent.EffectIntent -> reduceEffect(intent)
        }
    }

    private fun ReducerContext<State, Effect>.reduceUi(intent: Intent.UiIntent) {
        when (intent) {
            Intent.UiIntent.LoadClicked -> {
                state { copy(isLoading = true, error = null) }
                effects(Effect.LoadItems)
            }
            is Intent.UiIntent.ItemClicked -> {
                effects(Effect.OpenDetails(intent.id))
            }
        }
    }

    private fun ReducerContext<State, Effect>.reduceEffect(intent: Intent.EffectIntent) {
        when (intent) {
            is Intent.EffectIntent.LoadCompleted -> {
                state { copy(items = intent.items, isLoading = false) }
            }
            is Intent.EffectIntent.LoadFailed -> {
                state { copy(error = intent.error, isLoading = false) }
            }
        }
    }
}
```

**Правила Reducer:**
- Наследуется от `DslReducer<State, Intent, Effect>`
- `state { copy(...) }` — изменение состояния
- `effects(...)` — эмиссия эффектов
- **Чистая функция** — без side effects, без suspend

---

## Effector

Обрабатывает side effects.

```kotlin
// presentation/feature/mvi/FeatureStoreFactory.kt
private class FeatureEffector(
    private val getItemsUseCase: GetItemsUseCase,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext
) : CoroutineEffector<Effect, Intent, Event>(mainContext) {

    override fun onEffect(effect: Effect) {
        when (effect) {
            Effect.LoadItems -> loadItems()
            is Effect.OpenDetails -> publish(Event.NavigateToDetails(effect.id))
        }
    }

    private fun loadItems() {
        scope.launch(ioContext) {
            getItemsUseCase(Unit).fold(
                onSuccess = { items ->
                    withContext(mainContext) {
                        dispatch(Intent.EffectIntent.LoadCompleted(items))
                    }
                },
                onFailure = { error ->
                    withContext(mainContext) {
                        dispatch(Intent.EffectIntent.LoadFailed(error))
                    }
                }
            )
        }
    }
}
```

**Правила Effector:**
- Наследуется от `CoroutineEffector<Effect, Intent, Event>`
- `dispatch(intent)` — отправить Intent в Reducer
- `publish(event)` — отправить Event в Component
- `scope.launch` — для асинхронных операций
- Всегда `withContext(mainContext)` перед dispatch/publish

---

## Store Factory

Собирает TEA store.

```kotlin
// presentation/feature/mvi/FeatureStoreFactory.kt
internal fun TeaFactory.featureTea(
    getItemsUseCase: GetItemsUseCase,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): FeatureTea =
    object : FeatureTea, Tea<State, Intent, Event> by create(
        initialState = State(),
        initialEffects = listOf(Effect.LoadItems),  // опционально: загрузка при старте
        dispatcher = mainContext,
        effectors = listOf(
            FeatureEffector(getItemsUseCase, mainContext, ioContext)
        ),
        reducer = FeatureReducer()
    ) {}
```

---

## State Mapping

Преобразование TEA State → UI State.

```kotlin
// presentation/feature/mapping/StateMapping.kt
internal fun State.toUi(): FeatureComponent.UiState {
    return FeatureComponent.UiState(
        items = items.map { it.toUiItem() },
        isLoading = isLoading,
        error = error?.message
    )
}

private fun Item.toUiItem(): UiItem {
    return UiItem(
        id = id,
        title = name,
        subtitle = formatPrice(price)
    )
}
```

---

## Component Implementation

Связывает TEA с Decompose.

```kotlin
// presentation/feature/component/FeatureComponentImpl.kt
@AssistedInject
class FeatureComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val teaFactory: TeaFactory,
    private val getItemsUseCase: GetItemsUseCase,
    @Assisted private val onNavigate: (FeatureTea.Event) -> Unit
) : BaseComponent(componentContext), FeatureComponent {

    private val store = instanceKeeper.getTea {
        teaFactory.featureTea(
            getItemsUseCase = getItemsUseCase,
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    override val uiState: StateFlow<FeatureComponent.UiState> = store.state
        .map { it.toUi() }
        .stateIn(
            scope = componentScope,
            started = SharingStarted.Lazily,
            initialValue = FeatureComponent.UiState()
        )

    init {
        lifecycle.doOnCreate {
            store.events.onEach { event ->
                onNavigate(event)
            }.launchIn(componentScope)
        }
    }

    override fun onLoadClicked() {
        store.accept(FeatureTea.Intent.UiIntent.LoadClicked)
    }

    override fun onItemClicked(id: Long) {
        store.accept(FeatureTea.Intent.UiIntent.ItemClicked(id))
    }

    @Composable
    override fun render(modifier: Modifier) {
        FeatureScreen(component = this, modifier = modifier)
    }

    @AssistedFactory
    interface Factory : FeatureComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onNavigate: (FeatureTea.Event) -> Unit
        ): FeatureComponentImpl
    }
}
```

---

## Screen (Compose UI)

```kotlin
// presentation/feature/component/FeatureScreen.kt
@Composable
internal fun FeatureScreen(
    component: FeatureComponent,
    modifier: Modifier = Modifier
) {
    val state by component.uiState.collectAsState()

    FeatureContent(
        state = state,
        onLoadClick = component::onLoadClicked,
        onItemClick = component::onItemClicked,
        modifier = modifier
    )
}

@Composable
private fun FeatureContent(
    state: FeatureComponent.UiState,
    onLoadClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
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
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = items,
            key = { it.id }
        ) { item ->
            ItemCard(
                item = item,
                onClick = { onItemClick(item.id) }
            )
        }
    }
}
```

---