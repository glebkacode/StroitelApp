package com.itapp.auth_impl.presentation.registration.mvi

import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.Intent
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.State
import com.itapp.core_architecture.tea.Tea

internal interface RegistrationTea : Tea<State, Intent, Nothing> {

    sealed interface Intent {
        data class FirstNameChanged(val text: String) : Intent
        data class LastNameChanged(val text: String) : Intent
        data class LoginChanged(val text: String) : Intent
        data class PasswordChanged(val text: String) : Intent
        data class PhoneChanged(val text: String) : Intent
        data object TogglePasswordVisibility : Intent
        data object RegisterClicked : Intent
    }

    data class State(
        val firstName: String = "",
        val lastName: String = "",
        val login: String = "",
        val password: String = "",
        val phone: String = "",
        val isPasswordVisible: Boolean = false,
        val isLoading: Boolean = false
    )
}
