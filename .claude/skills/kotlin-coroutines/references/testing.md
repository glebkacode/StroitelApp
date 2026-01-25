# Тестирование

## runTest
```kotlin
@Test
fun `loadProducts updates state`() = runTest {
    val repository = FakeProductRepository()
    val viewModel = ProductViewModel(repository)
    
    viewModel.loadProducts()
    
    assertEquals(State.Success(testProducts), viewModel.state.value)
}
```

## TestDispatcher
```kotlin
@Test
fun `delayed operation completes`() = runTest {
    val viewModel = ProductViewModel(
        repository = FakeRepository(),
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