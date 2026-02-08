package com.itapp.auth_impl.presentation.password_validation.mvi

import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Effect
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Intent
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.State
import com.itapp.core_architecture.tea.Tea

internal interface PasswordValidationTea : Tea<State, Intent, Effect> {

    sealed interface Intent {
        data class PasswordChanged(val text: String) : Intent
        data object LoginClicked : Intent
        data object LoginSuccess : Intent
        data class LoginError(val message: String) : Intent
    }

    sealed interface Effect {
        data class PerformLogin(val phoneNumber: String, val password: String) : Effect
        data object NavigateToSuccess : Effect
    }

    data class State(
        val phoneNumber: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
