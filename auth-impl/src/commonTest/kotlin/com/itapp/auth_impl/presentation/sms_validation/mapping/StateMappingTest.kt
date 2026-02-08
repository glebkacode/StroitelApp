package com.itapp.auth_impl.presentation.sms_validation.mapping

import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsValidationTea.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StateMappingTest {

    @Test
    fun `should map phoneNumber correctly when toUi called`() {
        val state = State(
            phoneNumber = "+79001234567",
            smsCode = "1234",
            isLoading = false,
            error = null
        )

        val uiState = state.toUi()

        assertEquals("+79001234567", uiState.phoneNumber)
    }

    @Test
    fun `should map smsCode correctly when toUi called`() {
        val state = State(
            phoneNumber = "+79001234567",
            smsCode = "5678",
            isLoading = false,
            error = null
        )

        val uiState = state.toUi()

        assertEquals("5678", uiState.smsCode)
    }

    @Test
    fun `should map isLoading correctly when toUi called with loading true`() {
        val state = State(
            phoneNumber = "+79001234567",
            smsCode = "1234",
            isLoading = true,
            error = null
        )

        val uiState = state.toUi()

        assertEquals(true, uiState.isLoading)
    }

    @Test
    fun `should map isLoading correctly when toUi called with loading false`() {
        val state = State(
            phoneNumber = "+79001234567",
            smsCode = "1234",
            isLoading = false,
            error = null
        )

        val uiState = state.toUi()

        assertEquals(false, uiState.isLoading)
    }

    @Test
    fun `should map error correctly when toUi called with error`() {
        val state = State(
            phoneNumber = "+79001234567",
            smsCode = "1234",
            isLoading = false,
            error = "Invalid code"
        )

        val uiState = state.toUi()

        assertEquals("Invalid code", uiState.error)
    }

    @Test
    fun `should map error correctly when toUi called without error`() {
        val state = State(
            phoneNumber = "+79001234567",
            smsCode = "1234",
            isLoading = false,
            error = null
        )

        val uiState = state.toUi()

        assertNull(uiState.error)
    }

    @Test
    fun `should map default state correctly when toUi called`() {
        val state = State()

        val uiState = state.toUi()

        assertEquals("", uiState.phoneNumber)
        assertEquals("", uiState.smsCode)
        assertEquals(false, uiState.isLoading)
        assertNull(uiState.error)
    }
}
