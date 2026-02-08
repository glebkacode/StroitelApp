package com.itapp.auth_impl.presentation.registration.mapping

import com.itapp.auth_api.registration.RegistrationComponent.UiState
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.State

internal fun State.toUi(): UiState = UiState(
    firstName = firstName,
    lastName = lastName,
    login = login,
    password = password,
    phone = phone,
    isPasswordVisible = isPasswordVisible,
    isLoading = isLoading
)
