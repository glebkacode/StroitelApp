package com.itapp.auth_impl.presentation.password_validation.mvi

import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Intent
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.State
import com.itapp.core_architecture.tea.Tea

internal interface PasswordValidationTea : Tea<State, Intent, Nothing> {

    sealed interface Intent {
        data class PasswordChanged(
            val text: String
        ) : Intent
        data object ContinueClicked : Intent
    }

    data class State(
        val password: String = ""
    )
}
