package com.itapp.auth_impl.phone_validation.presentation.mapping

import com.itapp.auth_api.phone_validation.PhoneValidationComponent.UiState
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationTea.State

internal fun State.toUi(): UiState = UiState(phone = phone)