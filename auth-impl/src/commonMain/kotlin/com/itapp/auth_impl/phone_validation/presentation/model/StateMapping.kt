package com.itapp.auth_impl.phone_validation.presentation.model

import com.itapp.auth_api.phone_validation.PhoneValidationUi
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.State

internal fun State.toUi(): PhoneValidationUi = PhoneValidationUi(phone = phone)