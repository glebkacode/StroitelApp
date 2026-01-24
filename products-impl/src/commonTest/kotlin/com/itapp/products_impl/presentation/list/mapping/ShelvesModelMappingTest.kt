package com.itapp.products_impl.presentation.list.mapping

import com.itapp.shelves_api.domain.model.shelf.CatalogShelfDto
import com.itapp.shelves_api.domain.model.shelf.CatalogShelfFilterDto
import com.itapp.shelves_api.domain.model.shelf.DefaultShelfDto
import com.itapp.shelves_api.domain.model.shelfitem.DefaultShelfItemDto
import com.itapp.shelves_render_api.shelf.grid.GridShelfModel
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ShelvesModelMappingTest {

    // region ShelfDto.toModel() tests

    @Test
    fun `should return HorizontalShelfModel when DefaultShelfDto toModel called`() {
        // Given
        val dto: com.itapp.shelves_api.domain.model.shelf.ShelfDto = DefaultShelfDto(
            id = 1L,
            header = "Default Shelf",
            items = emptyList()
        )

        // When
        val model = dto.toModel()

        // Then
        assertTrue(model is HorizontalShelfModel)
    }

    @Test
    fun `should return GridShelfModel when CatalogShelfDto toModel called`() {
        // Given
        val dto: com.itapp.shelves_api.domain.model.shelf.ShelfDto = CatalogShelfDto(
            id = 1L,
            header = "Catalog Shelf",
            items = emptyList(),
            filterOptions = emptyList()
        )

        // When
        val model = dto.toModel()

        // Then
        assertTrue(model is GridShelfModel)
    }

    // endregion

    // region DefaultShelfDto.toModel() tests

    @Test
    fun `should map id correctly when DefaultShelfDto toModel called`() {
        // Given
        val dto = DefaultShelfDto(
            id = 42L,
            header = "Header",
            items = emptyList()
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals(42L, model.id)
    }

    @Test
    fun `should map header correctly when DefaultShelfDto toModel called`() {
        // Given
        val dto = DefaultShelfDto(
            id = 1L,
            header = "Popular Products",
            items = emptyList()
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals("Popular Products", model.header)
    }

    @Test
    fun `should map items correctly when DefaultShelfDto toModel called`() {
        // Given
        val items = listOf(
            DefaultShelfItemDto(
                id = 1L,
                title = "Item 1",
                description = "Desc 1",
                url = "url1",
                labelInfo = "Label 1",
                price = 10.0
            ),
            DefaultShelfItemDto(
                id = 2L,
                title = "Item 2",
                description = "Desc 2",
                url = "url2",
                labelInfo = "Label 2",
                price = 20.0
            )
        )
        val dto = DefaultShelfDto(
            id = 1L,
            header = "Header",
            items = items
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals(2, model.items.size)
        assertEquals("Item 1", model.items[0].title)
        assertEquals("Item 2", model.items[1].title)
    }

    @Test
    fun `should return empty items list when DefaultShelfDto toModel called with empty items`() {
        // Given
        val dto = DefaultShelfDto(
            id = 1L,
            header = "Empty Shelf",
            items = emptyList()
        )

        // When
        val model = dto.toModel()

        // Then
        assertTrue(model.items.isEmpty())
    }

    // endregion

    // region CatalogShelfDto.toModel() tests

    @Test
    fun `should map id correctly when CatalogShelfDto toModel called`() {
        // Given
        val dto = CatalogShelfDto(
            id = 99L,
            header = "Header",
            items = emptyList(),
            filterOptions = emptyList()
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals(99L, model.id)
    }

    @Test
    fun `should map header correctly when CatalogShelfDto toModel called`() {
        // Given
        val dto = CatalogShelfDto(
            id = 1L,
            header = "Catalog Header",
            items = emptyList(),
            filterOptions = emptyList()
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals("Catalog Header", model.header)
    }

    @Test
    fun `should map items correctly when CatalogShelfDto toModel called`() {
        // Given
        val items = listOf(
            DefaultShelfItemDto(
                id = 1L,
                title = "Catalog Item",
                description = "Desc",
                url = "url",
                labelInfo = "Label",
                price = 50.0
            )
        )
        val dto = CatalogShelfDto(
            id = 1L,
            header = "Header",
            items = items,
            filterOptions = emptyList()
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals(1, model.items.size)
        assertEquals("Catalog Item", model.items[0].title)
    }

    @Test
    fun `should return empty items list when CatalogShelfDto toModel called with empty items`() {
        // Given
        val dto = CatalogShelfDto(
            id = 1L,
            header = "Header",
            items = emptyList(),
            filterOptions = emptyList()
        )

        // When
        val model = dto.toModel()

        // Then
        assertTrue(model.items.isEmpty())
    }

    @Test
    fun `should ignore filterOptions when CatalogShelfDto toModel called`() {
        // Given - GridShelfModel doesn't have filterOptions field
        val dto = CatalogShelfDto(
            id = 1L,
            header = "Header",
            items = emptyList(),
            filterOptions = listOf(
                CatalogShelfFilterDto(id = 1L, option = "Filter 1"),
                CatalogShelfFilterDto(id = 2L, option = "Filter 2")
            )
        )

        // When
        val model = dto.toModel()

        // Then - mapping should succeed, filterOptions are not in the model
        assertEquals(1L, model.id)
        assertEquals("Header", model.header)
    }

    // endregion
}
