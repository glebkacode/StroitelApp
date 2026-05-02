package com.itapp.auth_impl.presentation.registration.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.itapp.auth_api.registration.RegistrationComponent.Field
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationStore.Intent
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationStore.Label
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationStore.State

internal interface RegistrationStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class FirstNameChanged(val text: String) : Intent
        data class LastNameChanged(val text: String) : Intent
        data class LoginChanged(val text: String) : Intent
        data class PasswordChanged(val text: String) : Intent
        data class PhoneChanged(val text: String) : Intent
        data object PasswordVisibilityToggle : Intent
        data class FieldFocusLost(val field: Field) : Intent
        data object RegisterClicked : Intent
    }

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

    sealed interface Label {
        data object NavigateToSuccess : Label
    }
}
