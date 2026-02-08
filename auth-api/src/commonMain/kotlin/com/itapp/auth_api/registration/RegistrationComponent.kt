package com.itapp.auth_api.registration

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import dev.zacsweers.metro.Assisted
import kotlinx.coroutines.flow.StateFlow

interface RegistrationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onFirstNameChanged(text: String)
    fun onLastNameChanged(text: String)
    fun onLoginChanged(text: String)
    fun onPasswordChanged(text: String)
    fun onPhoneChanged(text: String)
    fun onTogglePasswordVisibility()
    fun onRegisterClicked()
    fun onBackClicked()

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            @Assisted("onRegistrationSuccess") onRegistrationSuccess: () -> Unit,
            @Assisted("onBack") onBack: () -> Unit
        ): RegistrationComponent
    }

    data class UiState(
        val firstName: String = "",
        val lastName: String = "",
        val login: String = "",
        val password: String = "",
        val phone: String = "",
        val isPasswordVisible: Boolean = false,
        val isLoading: Boolean = false
    )
}
