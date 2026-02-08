package com.itapp.auth_impl.presentation.password_validation.mapping

import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StateMappingTest {

    @Test
    fun `should map phoneNumber correctly when toUi called`() {
        val state = State(
            phoneNumber = "+79001234567",
            password = "password",
            isLoading = false,
            error = null
        )

        val uiState = state.toUi()

        assertEquals("+79001234567", uiState.phoneNumber)
    }

    @Test
    fun `should map password correctly when toUi called`() {
        val state = State(
            phoneNumber = "+79001234567",
            password = "secretPassword",
            isLoading = false,
            error = null
        )

        val uiState = state.toUi()

        assertEquals("secretPassword", uiState.password)
    }

    @Test
    fun `should map isLoading correctly when toUi called with loading true`() {
        val state = State(
            phoneNumber = "+79001234567",
            password = "password",
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
            password = "password",
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
            password = "password",
            isLoading = false,
            error = "Login failed"
        )

        val uiState = state.toUi()

        assertEquals("Login failed", uiState.error)
    }

    @Test
    fun `should map error correctly when toUi called without error`() {
        val state = State(
            phoneNumber = "+79001234567",
            password = "password",
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
        assertEquals("", uiState.password)
        assertEquals(false, uiState.isLoading)
        assertNull(uiState.error)
    }
}
