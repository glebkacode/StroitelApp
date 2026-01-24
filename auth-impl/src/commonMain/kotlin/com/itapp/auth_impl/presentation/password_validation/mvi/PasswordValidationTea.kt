package com.itapp.auth_impl.presentation.password_validation.mvi

import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Event
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Intent
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.State
import com.itapp.core_architecture.tea.Tea

interface PasswordValidationTea : Tea<State, Intent, Event> {

    sealed interface Intent {
        data class PasswordChanged(val text: String) : Intent
        data object ValidatePasswordClicked : Intent
        data class LoginFailed(val throwable: Throwable) : Intent
    }

    sealed interface Effect {
        data class ValidatePassword(
            val phone: String,
            val password: String
        ) : Effect
    }

    sealed interface Event {
        data class OpenSmsValidation(
            val phone: String,
            val password: String
        ) : Event
    }

    sealed class State(
        open val data: PasswordValidationData
    ) {

        data class Init(
            override val data: PasswordValidationData
        ) : State(data)

        data class PasswordChanged(
            override val data: PasswordValidationData
        ) : State(data)

        data class AuthFailed(
            override val data: PasswordValidationData,
            val throwable: Throwable
        ) : State(data)
    }

    data class PasswordValidationData(val phone: String = "", val password: String = "")
}