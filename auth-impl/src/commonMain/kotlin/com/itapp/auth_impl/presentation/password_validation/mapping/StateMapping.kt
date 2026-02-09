package com.itapp.auth_impl.presentation.password_validation.mapping

import com.itapp.auth_api.password_validation.PasswordValidationComponent.UiState
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.State

internal fun State.toUi(): UiState = UiState(password = password)
