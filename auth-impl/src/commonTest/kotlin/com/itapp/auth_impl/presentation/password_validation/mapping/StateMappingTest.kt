package com.itapp.auth_impl.presentation.password_validation.mapping

import com.itapp.auth_api.password_validation.PasswordValidationComponent.UiState
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.PasswordValidationData
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.State
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PasswordValidationStateMappingTest {

    // region Init state tests

    @Test
    fun `should return Loading when Init state toUiState called`() {
        // Given
        val data = PasswordValidationData(phone = "+79001234567", password = "password123")
        val state = State.Init(data = data)

        // When
        val uiState = state.toUiState()

        // Then
        assertTrue(uiState is UiState.Loading)
    }

    @Test
    fun `should map password correctly when Init state toUiState called`() {
        // Given
        val data = PasswordValidationData(phone = "+79001234567", password = "myPassword")
        val state = State.Init(data = data)

        // When
        val uiState = state.toUiState()

        // Then
        assertEquals("myPassword", uiState.password)
    }

    @Test
    fun `should map empty password when Init state toUiState called with empty password`() {
        // Given
        val data = PasswordValidationData(phone = "+79001234567", password = "")
        val state = State.Init(data = data)

        // When
        val uiState = state.toUiState()

        // Then
        assertEquals("", uiState.password)
    }

    // endregion

    // region PasswordChanged state tests

    @Test
    fun `should return Content when PasswordChanged state toUiState called`() {
        // Given
        val data = PasswordValidationData(phone = "+79001234567", password = "password123")
        val state = State.PasswordChanged(data = data)

        // When
        val uiState = state.toUiState()

        // Then
        assertTrue(uiState is UiState.Content)
    }

    @Test
    fun `should map password correctly when PasswordChanged state toUiState called`() {
        // Given
        val data = PasswordValidationData(phone = "+79001234567", password = "newPassword")
        val state = State.PasswordChanged(data = data)

        // When
        val uiState = state.toUiState()

        // Then
        assertEquals("newPassword", uiState.password)
    }

    // endregion

    // region AuthFailed state tests

    @Test
    fun `should return Error when AuthFailed state toUiState called`() {
        // Given
        val data = PasswordValidationData(phone = "+79001234567", password = "password")
        val throwable = RuntimeException("Auth failed")
        val state = State.AuthFailed(data = data, throwable = throwable)

        // When
        val uiState = state.toUiState()

        // Then
        assertTrue(uiState is UiState.Error)
    }

    @Test
    fun `should map throwable correctly when AuthFailed state toUiState called`() {
        // Given
        val data = PasswordValidationData(phone = "+79001234567", password = "password")
        val throwable = RuntimeException("Network error")
        val state = State.AuthFailed(data = data, throwable = throwable)

        // When
        val uiState = state.toUiState()

        // Then
        assertTrue(uiState is UiState.Error)
        assertEquals(throwable, (uiState as UiState.Error).throwable)
    }

    @Test
    fun `should map password correctly when AuthFailed state toUiState called`() {
        // Given
        val data = PasswordValidationData(phone = "+79001234567", password = "failedPassword")
        val throwable = RuntimeException("Error")
        val state = State.AuthFailed(data = data, throwable = throwable)

        // When
        val uiState = state.toUiState()

        // Then
        assertEquals("failedPassword", uiState.password)
    }

    // endregion
}
