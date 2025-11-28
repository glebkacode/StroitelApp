package com.itapp.auth_impl.presentation.password_validation.mapping

import com.itapp.auth_api.password_validation.PasswordValidationComponent.UiState
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.*

fun State.toUiState(): UiState {
    return when (this) {
        is State.AuthFailed -> UiState.Error(throwable = throwable)
        is State.Init -> UiState.Loading(password = data.password)
        is State.PasswordChanged -> UiState.Content(password = data.password)
    }
}