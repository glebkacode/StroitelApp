# Тестирование

> Все зависимости мокаются через **MockK** (`mockk()` + `coEvery` / `coVerify` / `slot()`). Fake-классы в проекте запрещены — см. `.claude/skills/unit-testing/SKILL.md`.

## runTest
```kotlin
@Test
fun `loadProducts updates state`() = runTest {
    val repository = mockk<ProductRepository>()
    coEvery { repository.getProducts() } returns testProducts
    val component = ProductComponent(repository)
        
    component.loadProducts()

    assertEquals(State.Success(testProducts), component.state.value)
}
```

## TestDispatcher
```kotlin
@Test
fun `delayed operation completes`() = runTest {
    val repository = mockk<ProductRepository>()
    coEvery { repository.getProducts() } returns testProducts
    val component = ProductComponent(
        repository = repository,
        ioDispatcher = StandardTestDispatcher(testScheduler)
    )

    component.loadProducts()
    advanceUntilIdle()  // прокручивает виртуальное время

    assertEquals(expected, component.state.value)
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