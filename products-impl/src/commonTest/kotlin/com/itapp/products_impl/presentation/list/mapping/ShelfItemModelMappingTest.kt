package com.itapp.products_impl.presentation.list.mapping

import com.itapp.shelves_api.domain.model.shelfitem.DefaultShelfItemDto
import kotlin.test.Test
import kotlin.test.assertEquals

class ShelfItemModelMappingTest {

    @Test
    fun `should map id correctly when toModel called`() {
        // Given
        val dto = DefaultShelfItemDto(
            id = 123L,
            title = "Title",
            description = "Description",
            url = "url",
            labelInfo = "label",
            price = 100.0
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals(123L, model.id)
    }

    @Test
    fun `should map title correctly when toModel called`() {
        // Given
        val dto = DefaultShelfItemDto(
            id = 1L,
            title = "Product Title",
            description = "Description",
            url = "url",
            labelInfo = "label",
            price = 100.0
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals("Product Title", model.title)
    }

    @Test
    fun `should map description correctly when toModel called`() {
        // Given
        val dto = DefaultShelfItemDto(
            id = 1L,
            title = "Title",
            description = "Product Description",
            url = "url",
            labelInfo = "label",
            price = 100.0
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals("Product Description", model.description)
    }

    @Test
    fun `should map url correctly when toModel called`() {
        // Given
        val dto = DefaultShelfItemDto(
            id = 1L,
            title = "Title",
            description = "Description",
            url = "https://example.com/image.png",
            labelInfo = "label",
            price = 100.0
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals("https://example.com/image.png", model.url)
    }

    @Test
    fun `should map labelInfo correctly when toModel called`() {
        // Given
        val dto = DefaultShelfItemDto(
            id = 1L,
            title = "Title",
            description = "Description",
            url = "url",
            labelInfo = "Sale -20%",
            price = 100.0
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals("Sale -20%", model.labelInfo)
    }

    @Test
    fun `should map price correctly when toModel called`() {
        // Given
        val dto = DefaultShelfItemDto(
            id = 1L,
            title = "Title",
            description = "Description",
            url = "url",
            labelInfo = "label",
            price = 199.99
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals(199.99, model.price)
    }

    @Test
    fun `should map all fields correctly when toModel called`() {
        // Given
        val dto = DefaultShelfItemDto(
            id = 42L,
            title = "Complete Product",
            description = "Full Description",
            url = "https://shop.com/product.jpg",
            labelInfo = "New Arrival",
            price = 299.50
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals(42L, model.id)
        assertEquals("Complete Product", model.title)
        assertEquals("Full Description", model.description)
        assertEquals("https://shop.com/product.jpg", model.url)
        assertEquals("New Arrival", model.labelInfo)
        assertEquals(299.50, model.price)
    }

    @Test
    fun `should map empty strings correctly when toModel called`() {
        // Given
        val dto = DefaultShelfItemDto(
            id = 1L,
            title = "",
            description = "",
            url = "",
            labelInfo = "",
            price = 0.0
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals("", model.title)
        assertEquals("", model.description)
        assertEquals("", model.url)
        assertEquals("", model.labelInfo)
        assertEquals(0.0, model.price)
    }

    @Test
    fun `should map zero price correctly when toModel called`() {
        // Given
        val dto = DefaultShelfItemDto(
            id = 1L,
            title = "Free Item",
            description = "Description",
            url = "url",
            labelInfo = "Free",
            price = 0.0
        )

        // When
        val model = dto.toModel()

        // Then
        assertEquals(0.0, model.price)
    }
}
