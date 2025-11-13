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
            openSmsScreen: (String, String) -> Unit
        ): PasswordValidationComponent
    }
    sealed class UiState(
        open val password: String
    ) {
        data class Loading(override val password: String = "") : UiState(password)
        data class Content(override val password: String = "") : UiState(password)
        data class Error(
            override val password: String = "",
            val throwable: Throwable
        ) : UiState(password)
    }
}