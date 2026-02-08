package com.itapp.auth_api.password_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import dev.zacsweers.metro.Assisted
import kotlinx.coroutines.flow.StateFlow

interface PasswordValidationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onPasswordChanged(text: String)
    fun onLoginClicked()
    fun onBackClicked()

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            @Assisted("phoneNumber") phoneNumber: String,
            @Assisted("onAuthSuccess") onAuthSuccess: () -> Unit,
            @Assisted("onBack") onBack: () -> Unit
        ): PasswordValidationComponent
    }

    data class UiState(
        val phoneNumber: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
