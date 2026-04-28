package com.itapp.auth_impl.presentation.registration.mvi

import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.Event
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.Intent
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.State
import com.itapp.core_architecture.tea.Tea

internal interface RegistrationTea : Tea<State, Intent, Event> {

    data class State(
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

    sealed interface Intent {
        data class FirstNameChanged(val text: String) : Intent
        data class LastNameChanged(val text: String) : Intent
        data class LoginChanged(val text: String) : Intent
        data class PasswordChanged(val text: String) : Intent
        data class PhoneChanged(val text: String) : Intent
        data object PasswordVisibilityToggle : Intent
        data class FieldFocusLost(val field: RegistrationComponent.Field) : Intent
        data object RegisterClicked : Intent
    }

    sealed interface Effect {
        data object NavigateRegistrationSuccess : Effect
    }

    sealed interface Event {
        data object RegistrationSuccess : Event
    }
}
