package com.itapp.shelves_impl.domain.usecase

import com.itapp.shelves_api.domain.model.shelf.DefaultShelfDto
import com.itapp.shelves_api.domain.model.shelfitem.DefaultShelfItemDto
import com.itapp.shelves_impl.fake.FakeShelvesRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetShelvesUseCaseImplTest {

    private lateinit var fakeRepository: FakeShelvesRepository
    private lateinit var useCase: GetShelvesUseCaseImpl

    @BeforeTest
    fun setup() {
        fakeRepository = FakeShelvesRepository()
        useCase = GetShelvesUseCaseImpl(fakeRepository)
    }

    @Test
    fun `should return shelves when repository returns data`() = runTest {
        // Given
        val expectedShelves = listOf(
            DefaultShelfDto(
                id = 1L,
                header = "Test Shelf",
                items = listOf(
                    DefaultShelfItemDto(
                        id = 1L,
                        title = "Item 1",
                        description = "Description",
                        url = "https://example.com",
                        labelInfo = "Label",
                        price = 100.0
                    )
                )
            )
        )
        fakeRepository.shelvesToReturn = expectedShelves

        // When
        val result = useCase.run(Unit)

        // Then
        assertEquals(expectedShelves, result)
    }

    @Test
    fun `should call repository getShelves when run called`() = runTest {
        // Given
        fakeRepository.shelvesToReturn = emptyList()

        // When
        useCase.run(Unit)

        // Then
        assertEquals(1, fakeRepository.getShelvesCalls.size)
    }

    @Test
    fun `should return empty list when repository returns empty list`() = runTest {
        // Given
        fakeRepository.shelvesToReturn = emptyList()

        // When
        val result = useCase.run(Unit)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should propagate exception when repository throws`() = runTest {
        // Given
        val expectedException = RuntimeException("Network error")
        fakeRepository.exceptionToThrow = expectedException

        // When
        val result = runCatching { useCase.run(Unit) }

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
