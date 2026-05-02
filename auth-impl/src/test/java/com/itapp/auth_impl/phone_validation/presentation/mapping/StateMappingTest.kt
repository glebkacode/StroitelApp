package com.itapp.auth_impl.phone_validation.presentation.mapping

import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.State
import kotlin.test.Test
import kotlin.test.assertEquals

class PhoneValidationStateMappingTest {

    @Test
    fun `should map phone correctly when toUi called`() {
        // Given
        val state = State(phone = "+79001234567")

        // When
        val uiState = state.toUi()

        // Then
        assertEquals("+79001234567", uiState.phone)
    }

    @Test
    fun `should map empty phone when toUi called with empty state`() {
        // Given
        val state = State(phone = "")

        // When
        val uiState = state.toUi()

        // Then
        assertEquals("", uiState.phone)
    }

    @Test
    fun `should map phone with spaces when toUi called`() {
        // Given
        val state = State(phone = "+7 900 123 45 67")

        // When
        val uiState = state.toUi()

        // Then
        assertEquals("+7 900 123 45 67", uiState.phone)
    }

    @Test
    fun `should map default state correctly when toUi called`() {
        // Given
        val state = State()

        // When
        val uiState = state.toUi()

        // Then
        assertEquals("", uiState.phone)
    }
}
