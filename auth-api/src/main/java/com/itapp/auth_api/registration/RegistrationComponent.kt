package com.itapp.auth_api.registration

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import kotlinx.coroutines.flow.StateFlow

interface RegistrationComponent : UiComponent {
    val uiState: StateFlow<UiState>

    fun onFirstNameChanged(text: String)
    fun onLastNameChanged(text: String)
    fun onLoginChanged(text: String)
    fun onPasswordChanged(text: String)
    fun onPhoneChanged(text: String)
    fun onPasswordVisibilityToggle()
    fun onFieldFocusLost(field: Field)
    fun onRegisterClicked()
    fun onBackClicked()

    enum class Field { FIRST_NAME, LAST_NAME, LOGIN, PASSWORD, PHONE }

    data class Callbacks(
        val onRegistrationSuccess: () -> Unit,
        val onBack: () -> Unit,
    )

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            callbacks: Callbacks,
        ): RegistrationComponent
    }

    data class UiState(
        val firstName: String = "",
        val lastName: String = "",
        val login: String = "",
        val password: String = "",
        val phone: String = "",
        val isPasswordVisible: Boolean = false,
        val firstNameError: Boolean = false,
        val lastNameError: Boolean = false,
        val loginError: Boolean = false,
        val passwordError: Boolean = false,
        val phoneError: Boolean = false,
    )
}
