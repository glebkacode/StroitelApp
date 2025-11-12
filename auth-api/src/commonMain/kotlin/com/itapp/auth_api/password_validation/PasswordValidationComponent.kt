package com.itapp.auth_api.password_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent
import kotlinx.coroutines.flow.StateFlow

interface PasswordValidationComponent : BaseUiComponent {
    val uiState: StateFlow<UiState>
    fun onPasswordChanged(text: String)
    fun onNextClicked()
    fun onForgotPasswordClicked()
    fun onBackClicked()
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            phone: String,
            openSmsScreen: () -> Unit
        ): PasswordValidationComponent
    }
    sealed interface UiState {
        data class Loading(val password: String = "") : UiState
        data class Content(val password: String = "") : UiState
        data class Error(val throwable: Throwable) : UiState
    }
}