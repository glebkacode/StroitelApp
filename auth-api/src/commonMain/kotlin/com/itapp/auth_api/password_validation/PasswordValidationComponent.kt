package com.itapp.auth_api.password_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import kotlinx.coroutines.flow.StateFlow

interface PasswordValidationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onPasswordChanged(text: String)
    fun onContinueClicked()
    fun onRemindPasswordClicked()
    fun onBackClicked()

    data class Callbacks(
        val onAuthSuccess: () -> Unit,
        val onBack: () -> Unit,
    )

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            callbacks: Callbacks,
        ) : PasswordValidationComponent
    }
    data class UiState(
        val password: String = ""
    )
}
