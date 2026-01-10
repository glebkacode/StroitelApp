package com.itapp.shopping_cart_impl.presentation.mapping

import com.itapp.shopping_cart_impl.presentation.mvi.ShoppingCartTea.State
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ShoppingCartStateMappingTest {

    @Test
    fun `should map isEmpty true when toUi called with isEmpty true`() {
        // Given
        val state = State(isEmpty = true)

        // When
        val uiState = state.toUi()

        // Then
        assertTrue(uiState.isEmpty)
    }

    @Test
    fun `should map isEmpty false when toUi called with isEmpty false`() {
        // Given
        val state = State(isEmpty = false)

        // When
        val uiState = state.toUi()

        // Then
        assertFalse(uiState.isEmpty)
    }

    @Test
    fun `should map default state correctly when toUi called`() {
        // Given
        val state = State()

        // When
        val uiState = state.toUi()

        // Then
        assertTrue(uiState.isEmpty) // Default is true
    }
}
