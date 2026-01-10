package com.itapp.shelves_impl.data.repository

import com.itapp.shelves_api.domain.model.shelf.DefaultShelfDto
import com.itapp.shelves_api.domain.model.shelfitem.DefaultShelfItemDto
import com.itapp.shelves_impl.data.model.SelfItemDtoResponse
import com.itapp.shelves_impl.data.model.ShelfDtoResponse
import com.itapp.shelves_impl.fake.FakeShelvesDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ShelvesRepositoryImplTest {

    private lateinit var fakeDataSource: FakeShelvesDataSource
    private lateinit var repository: ShelvesRepositoryImpl

    @BeforeTest
    fun setup() {
        fakeDataSource = FakeShelvesDataSource()
        repository = ShelvesRepositoryImpl(fakeDataSource)
    }

    @Test
    fun `should call dataSource getShelves when getShelves called`() = runTest {
        // Given
        fakeDataSource.shelvesToReturn = emptyList()

        // When
        repository.getShelves()

        // Then
        assertEquals(1, fakeDataSource.getShelvesCalls.size)
    }

    @Test
    fun `should return mapped shelves when dataSource returns data`() = runTest {
        // Given
        val responseItems = listOf(
            SelfItemDtoResponse(
                id = 1L,
                title = "Item Title",
                description = "Item Description",
                url = "https://example.com/item",
                labelInfo = "New",
                price = 99.99
            )
        )
        val response = listOf(
            ShelfDtoResponse(
                id = 1L,
                type = "default",
                header = "Test Header",
                filterOptions = null,
                items = responseItems
            )
        )
        fakeDataSource.shelvesToReturn = response

        // When
        val result = repository.getShelves()

        // Then
        assertEquals(1, result.size)
        assertTrue(result[0] is DefaultShelfDto)
        val shelf = result[0] as DefaultShelfDto
        assertEquals(1L, shelf.id)
        assertEquals("Test Header", shelf.header)
        assertEquals(1, shelf.items.size)
        assertEquals("Item Title", shelf.items[0].title)
    }

    @Test
    fun `should return empty list when dataSource returns empty list`() = runTest {
        // Given
        fakeDataSource.shelvesToReturn = emptyList()

        // When
        val result = repository.getShelves()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `should propagate exception when dataSource throws`() = runTest {
        // Given
        val expectedException = RuntimeException("API error")
        fakeDataSource.exceptionToThrow = expectedException

        // When/Then
        val exception = assertFailsWith<RuntimeException> {
            repository.getShelves()
        }
        assertEquals("API error", exception.message)
    }

    @Test
    fun `should map multiple shelves when dataSource returns multiple items`() = runTest {
        // Given
        val response = listOf(
            ShelfDtoResponse(
                id = 1L,
                type = "default",
                header = "Shelf 1",
                filterOptions = null,
                items = emptyList()
            ),
            ShelfDtoResponse(
                id = 2L,
                type = "default",
                header = "Shelf 2",
                filterOptions = null,
                items = emptyList()
            )
        )
        fakeDataSource.shelvesToReturn = response

        // When
        val result = repository.getShelves()

        // Then
        assertEquals(2, result.size)
        assertEquals("Shelf 1", result[0].header)
        assertEquals("Shelf 2", result[1].header)
    }
}
