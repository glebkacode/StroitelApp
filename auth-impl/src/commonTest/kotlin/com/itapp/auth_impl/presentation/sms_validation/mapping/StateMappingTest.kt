package com.itapp.auth_impl.presentation.sms_validation.mapping

import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationTea.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SmsValidationStateMappingTest {

    @Test
    fun `should map loading correctly when toUiState called with loading true`() {
        // Given
        val state = State(loading = true, smsCode = "1234")

        // When
        val uiState = state.toUiState()

        // Then
        assertTrue(uiState.loading)
    }

    @Test
    fun `should map loading correctly when toUiState called with loading false`() {
        // Given
        val state = State(loading = false, smsCode = "1234")

        // When
        val uiState = state.toUiState()

        // Then
        assertFalse(uiState.loading)
    }

    @Test
    fun `should map smsCode correctly when toUiState called`() {
        // Given
        val state = State(loading = false, smsCode = "9876")

        // When
        val uiState = state.toUiState()

        // Then
        assertEquals("9876", uiState.smsCode)
    }

    @Test
    fun `should map empty smsCode when toUiState called with empty code`() {
        // Given
        val state = State(loading = false, smsCode = "")

        // When
        val uiState = state.toUiState()

        // Then
        assertEquals("", uiState.smsCode)
    }

    @Test
    fun `should map default state correctly when toUiState called`() {
        // Given
        val state = State()

        // When
        val uiState = state.toUiState()

        // Then
        assertTrue(uiState.loading)
        assertEquals("", uiState.smsCode)
    }

    @Test
    fun `should ignore phone field when toUiState called`() {
        // Given
        val state = State(
            loading = false,
            phone = "+79001234567",
            password = "password",
            smsCode = "1234"
        )

        // When
        val uiState = state.toUiState()

        // Then
        // UiState doesn't contain phone or password, only loading and smsCode
        assertEquals("1234", uiState.smsCode)
        assertFalse(uiState.loading)
    }

    @Test
    fun `should ignore throwable field when toUiState called`() {
        // Given
        val state = State(
            loading = false,
            smsCode = "5678",
            throwable = RuntimeException("Error")
        )

        // When
        val uiState = state.toUiState()

        // Then
        // UiState doesn't contain throwable
        assertEquals("5678", uiState.smsCode)
    }
}
