package com.itapp.auth_impl.presentation.phone_validation.mapping

import com.itapp.auth_api.phone_validation.PhoneValidationComponent.UiState
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.State

internal fun State.toUi(): UiState = UiState(phone = phone)