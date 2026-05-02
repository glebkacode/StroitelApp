# Тестирование

> Все зависимости мокаются через **Mokkery** (`mock<T>()` + `everySuspend` / `verifySuspend`). Fake-классы в проекте запрещены — см. `.claude/skills/unit-testing/SKILL.md`.

## runTest
```kotlin
@Test
fun `loadProducts updates state`() = runTest {
    val repository = mock<ProductRepository>()
    everySuspend { repository.getProducts() } returns testProducts
    val viewModel = ProductViewModel(repository)

    viewModel.loadProducts()

    assertEquals(State.Success(testProducts), viewModel.state.value)
}
```

## TestDispatcher
```kotlin
@Test
fun `delayed operation completes`() = runTest {
    val repository = mock<ProductRepository>()
    everySuspend { repository.getProducts() } returns testProducts
    val viewModel = ProductViewModel(
        repository = repository,
        ioDispatcher = StandardTestDispatcher(testScheduler)
    )

    viewModel.loadProducts()
    advanceUntilIdle()  // прокручивает виртуальное время

    assertEquals(expected, viewModel.state.value)
}
```

## Turbine для тестирования Flow
```kotlin
@Test
fun `products flow emits correctly`() = runTest {
    repository.observeProducts().test {
        assertEquals(emptyList(), awaitItem())
        
        repository.addProduct(testProduct)
        assertEquals(listOf(testProduct), awaitItem())
        
        cancelAndIgnoreRemainingEvents()
    }
}
```

---