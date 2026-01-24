package com.itapp.shelves_impl.data.mapper

import com.itapp.shelves_api.domain.model.shelf.CatalogShelfDto
import com.itapp.shelves_api.domain.model.shelf.DefaultShelfDto
import com.itapp.shelves_impl.data.model.FilterDtoResponse
import com.itapp.shelves_impl.data.model.SelfItemDtoResponse
import com.itapp.shelves_impl.data.model.ShelfDtoResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ShelvesMappingTest {

    // region toDomain tests

    @Test
    fun `should return DefaultShelfDto when type is default`() {
        // Given
        val response = ShelfDtoResponse(
            id = 1L,
            type = "default",
            header = "Default Shelf",
            filterOptions = null,
            items = emptyList()
        )

        // When
        val result = response.toDomain()

        // Then
        assertTrue(result is DefaultShelfDto)
        assertEquals(1L, result.id)
        assertEquals("Default Shelf", result.header)
    }

    @Test
    fun `should return CatalogShelfDto when type is catalog`() {
        // Given
        val response = ShelfDtoResponse(
            id = 2L,
            type = "catalog",
            header = "Catalog Shelf",
            filterOptions = listOf(
                FilterDtoResponse(id = 1L, option = "Option 1")
            ),
            items = emptyList()
        )

        // When
        val result = response.toDomain()

        // Then
        assertTrue(result is CatalogShelfDto)
        assertEquals(2L, result.id)
        assertEquals("Catalog Shelf", result.header)
    }

    @Test
    fun `should throw IllegalArgumentException when type is unknown`() {
        // Given
        val response = ShelfDtoResponse(
            id = 3L,
            type = "unknown",
            header = "Unknown Shelf",
            filterOptions = null,
            items = emptyList()
        )

        // When/Then
        val exception = assertFailsWith<IllegalArgumentException> {
            response.toDomain()
        }
        assertEquals("Unknown shelf type: unknown", exception.message)
    }

    // endregion

    // region toDefaultShelf tests

    @Test
    fun `should map id correctly when toDefaultShelf called`() {
        // Given
        val response = ShelfDtoResponse(
            id = 42L,
            type = "default",
            header = "Header",
            filterOptions = null,
            items = emptyList()
        )

        // When
        val result = response.toDefaultShelf()

        // Then
        assertEquals(42L, result.id)
    }

    @Test
    fun `should map header correctly when toDefaultShelf called`() {
        // Given
        val response = ShelfDtoResponse(
            id = 1L,
            type = "default",
            header = "My Custom Header",
            filterOptions = null,
            items = emptyList()
        )

        // When
        val result = response.toDefaultShelf()

        // Then
        assertEquals("My Custom Header", result.header)
    }

    @Test
    fun `should map items correctly when toDefaultShelf called`() {
        // Given
        val items = listOf(
            SelfItemDtoResponse(
                id = 1L,
                title = "Title 1",
                description = "Desc 1",
                url = "url1",
                labelInfo = "Label 1",
                price = 10.0
            ),
            SelfItemDtoResponse(
                id = 2L,
                title = "Title 2",
                description = "Desc 2",
                url = "url2",
                labelInfo = "Label 2",
                price = 20.0
            )
        )
        val response = ShelfDtoResponse(
            id = 1L,
            type = "default",
            header = "Header",
            filterOptions = null,
            items = items
        )

        // When
        val result = response.toDefaultShelf()

        // Then
        assertEquals(2, result.items.size)
        assertEquals("Title 1", result.items[0].title)
        assertEquals("Title 2", result.items[1].title)
    }

    @Test
    fun `should return empty items list when toDefaultShelf called with empty items`() {
        // Given
        val response = ShelfDtoResponse(
            id = 1L,
            type = "default",
            header = "Header",
            filterOptions = null,
            items = emptyList()
        )

        // When
        val result = response.toDefaultShelf()

        // Then
        assertTrue(result.items.isEmpty())
    }

    // endregion

    // region toDefaultShelfItem tests

    @Test
    fun `should map all fields correctly when toDefaultShelfItem called`() {
        // Given
        val item = SelfItemDtoResponse(
            id = 123L,
            title = "Product Title",
            description = "Product Description",
            url = "https://example.com/product",
            labelInfo = "Sale",
            price = 199.99
        )

        // When
        val result = item.toDefaultShelfItem()

        // Then
        assertEquals(123L, result.id)
        assertEquals("Product Title", result.title)
        assertEquals("Product Description", result.description)
        assertEquals("https://example.com/product", result.url)
        assertEquals("Sale", result.labelInfo)
        assertEquals(199.99, result.price)
    }

    @Test
    fun `should map empty strings correctly when toDefaultShelfItem called`() {
        // Given
        val item = SelfItemDtoResponse(
            id = 1L,
            title = "",
            description = "",
            url = "",
            labelInfo = "",
            price = 0.0
        )

        // When
        val result = item.toDefaultShelfItem()

        // Then
        assertEquals("", result.title)
        assertEquals("", result.description)
        assertEquals("", result.url)
        assertEquals("", result.labelInfo)
        assertEquals(0.0, result.price)
    }

    // endregion

    // region toCatalogShelf tests

    @Test
    fun `should map id correctly when toCatalogShelf called`() {
        // Given
        val response = ShelfDtoResponse(
            id = 99L,
            type = "catalog",
            header = "Header",
            filterOptions = emptyList(),
            items = emptyList()
        )

        // When
        val result = response.toCatalogShelf()

        // Then
        assertEquals(99L, result.id)
    }

    @Test
    fun `should map header correctly when toCatalogShelf called`() {
        // Given
        val response = ShelfDtoResponse(
            id = 1L,
            type = "catalog",
            header = "Catalog Header",
            filterOptions = emptyList(),
            items = emptyList()
        )

        // When
        val result = response.toCatalogShelf()

        // Then
        assertEquals("Catalog Header", result.header)
    }

    @Test
    fun `should map filterOptions correctly when toCatalogShelf called`() {
        // Given
        val filters = listOf(
            FilterDtoResponse(id = 1L, option = "Filter 1"),
            FilterDtoResponse(id = 2L, option = "Filter 2")
        )
        val response = ShelfDtoResponse(
            id = 1L,
            type = "catalog",
            header = "Header",
            filterOptions = filters,
            items = emptyList()
        )

        // When
        val result = response.toCatalogShelf()

        // Then
        assertEquals(2, result.filterOptions.size)
        assertEquals("Filter 1", result.filterOptions[0].option)
        assertEquals("Filter 2", result.filterOptions[1].option)
    }

    @Test
    fun `should return empty filterOptions when toCatalogShelf called with null filters`() {
        // Given
        val response = ShelfDtoResponse(
            id = 1L,
            type = "catalog",
            header = "Header",
            filterOptions = null,
            items = emptyList()
        )

        // When
        val result = response.toCatalogShelf()

        // Then
        assertTrue(result.filterOptions.isEmpty())
    }

    @Test
    fun `should map items correctly when toCatalogShelf called`() {
        // Given
        val items = listOf(
            SelfItemDtoResponse(
                id = 1L,
                title = "Catalog Item",
                description = "Description",
                url = "url",
                labelInfo = "Info",
                price = 50.0
            )
        )
        val response = ShelfDtoResponse(
            id = 1L,
            type = "catalog",
            header = "Header",
            filterOptions = emptyList(),
            items = items
        )

        // When
        val result = response.toCatalogShelf()

        // Then
        assertEquals(1, result.items.size)
        assertEquals("Catalog Item", result.items[0].title)
    }

    // endregion

    // region toCatalogFilterOption tests

    @Test
    fun `should map id correctly when toCatalogFilterOption called`() {
        // Given
        val filter = FilterDtoResponse(id = 777L, option = "Option")

        // When
        val result = filter.toCatalogFilterOption()

        // Then
        assertEquals(777L, result.id)
    }

    @Test
    fun `should map option correctly when toCatalogFilterOption called`() {
        // Given
        val filter = FilterDtoResponse(id = 1L, option = "My Filter Option")

        // When
        val result = filter.toCatalogFilterOption()

        // Then
        assertEquals("My Filter Option", result.option)
    }

    @Test
    fun `should map empty option correctly when toCatalogFilterOption called`() {
        // Given
        val filter = FilterDtoResponse(id = 1L, option = "")

        // When
        val result = filter.toCatalogFilterOption()

        // Then
        assertEquals("", result.option)
    }

    // endregion
}
