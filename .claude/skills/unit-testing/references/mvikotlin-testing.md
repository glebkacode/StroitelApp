# Тестирование MviKotlin Store

`Store<Intent, State, Label>` — это «коробка», внутри которой:
- **Reducer** — чистая функция `(State, Msg) -> State`. Тестируем напрямую, без моков.
- **Executor** — обработчик `Intent`/`Action`, дёргает UseCase, диспатчит `Msg`, публикует `Label`. Тестируем через целиком собранный Store.

Соответственно — две стратегии тестов.

---

## 1. Reducer как чистая функция

Самый простой и быстрый кейс: достаём reducer (или собираем эквивалент) и проверяем, что под каждый `Msg` он строит ожидаемый `State`.

Reducer в проекте лежит внутри `StoreFactory` как `Reducer<State, Msg> { msg -> ... }`. Если он закрыт — выноси его в `internal`-функцию или `internal object`, тогда его можно тестировать отдельно.

```kotlin
// production: presentation/feature_screen/mvi/FeatureReducer.kt
internal val FeatureReducer = Reducer<FeatureStore.State, FeatureStoreFactory.Msg> { msg ->
    when (msg) {
        FeatureStoreFactory.Msg.LoadingStarted -> copy(isLoading = true, error = null)
        is FeatureStoreFactory.Msg.ItemsLoaded -> copy(items = msg.items, isLoading = false)
        is FeatureStoreFactory.Msg.LoadFailed -> copy(isLoading = false, error = msg.message)
    }
}
```

```kotlin
// test: src/test/java/.../FeatureReducerTest.kt
class FeatureReducerTest {

    @Test
    fun `should set isLoading=true and clear error when LoadingStarted`() {
        val state = FeatureStore.State(error = "old")

        val next = with(FeatureReducer) { state.reduce(FeatureStoreFactory.Msg.LoadingStarted) }

        assertEquals(true, next.isLoading)
        assertEquals(null, next.error)
    }

    @Test
    fun `should set items and isLoading=false when ItemsLoaded`() {
        val state = FeatureStore.State(isLoading = true)
        val items = listOf(Item(1, "a"))

        val next = with(FeatureReducer) { state.reduce(FeatureStoreFactory.Msg.ItemsLoaded(items)) }

        assertEquals(items, next.items)
        assertEquals(false, next.isLoading)
    }
}
```

Reducer — чистая функция, поэтому моки не нужны.

---

## 2. Store целиком (Executor + Reducer)

Когда нужно проверить «отправил Intent → Store вызвал UseCase → диспатчил Msg → State обновился → Label опубликован» — собираем настоящий Store с замоканными зависимостями.

**Подготовка StoreFactory:**

```kotlin
class FeatureStoreTest {

    private lateinit var getItemsUseCase: GetItemsUseCase
    private lateinit var storeFactory: StoreFactory
    private lateinit var feature: FeatureStoreFactory

    @BeforeTest
    fun setup() {
        getItemsUseCase = mockk()
        storeFactory = DefaultStoreFactory()      // настоящий MviKotlin StoreFactory
        feature = FeatureStoreFactory(storeFactory, getItemsUseCase)
    }
}
```

**Стандартный тест на Intent → State:**

```kotlin
@Test
fun `should set isLoading=true then load items when Intent_Load`() = runTest {
    coEvery { getItemsUseCase(Unit) } returns Result.success(listOf(Item(1, "a")))
    val store = feature.create()

    store.accept(FeatureStore.Intent.Load)
    advanceUntilIdle()                            // дать executor отработать suspend-вызов

    assertEquals(false, store.state.isLoading)
    assertEquals(listOf(Item(1, "a")), store.state.items)
    coVerify { getItemsUseCase(Unit) }
}
```

**Тест на failure:**

```kotlin
@Test
fun `should set error when use case fails`() = runTest {
    coEvery { getItemsUseCase(Unit) } returns Result.failure(RuntimeException("boom"))
    val store = feature.create()

    store.accept(FeatureStore.Intent.Load)
    advanceUntilIdle()

    assertEquals(false, store.state.isLoading)
    assertEquals("boom", store.state.error)
}
```

**Тест на Label:**

```kotlin
@Test
fun `should publish NavigateToDetails label when Intent_ItemClicked`() = runTest {
    val store = feature.create()
    val labels = mutableListOf<FeatureStore.Label>()
    val job = launch { store.labels.toList(labels) }

    store.accept(FeatureStore.Intent.ItemClicked(id = 42L))
    advanceUntilIdle()

    assertEquals(listOf(FeatureStore.Label.NavigateToDetails(42L)), labels)
    job.cancel()
}
```

> `store.state` — это уже текущее значение (не Flow). `store.stateFlow` доступен через `mvikotlin-extensions-coroutines`. `store.labels` — `Source<Label>` из MviKotlin; для тестов чаще удобнее обернуть его в Flow и собрать.

---

## Что покрывать

| Что | Стратегия |
|-----|-----------|
| Каждый `Msg` → ожидаемый `State` | Reducer-тесты (без моков) |
| Каждый `Intent` → правильные `Msg` / `Label` / вызовы UseCase | Store-тесты (executor + замоканные UseCase) |
| Bootstrapper / стартовая загрузка | Store-тест: `feature.create()` и сразу `advanceUntilIdle()` |
| `Mapping` `Store.State → UiState` | Чистые тесты на extension-функцию |

---

## Антипаттерны

- ❌ Тестировать сразу UI / Compose поверх Store. Это integration-уровень.
- ❌ Подменять executor «голым моком» — теряется проверка контракта Intent → Msg.
- ❌ Использовать `Thread.sleep` или `delay()` для ожидания executor — только `advanceUntilIdle()` / `advanceTimeBy()` через `runTest`.
- ❌ Собирать `store.labels` без отмены `Job` — оставит висящую корутину и сломает следующие тесты.
