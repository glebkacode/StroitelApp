package com.itapp.auth_impl.presentation.sms_validation.mapping

import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsValidationTea

internal fun SmsValidationTea.State.toUi(): SmsValidationComponent.UiState =
    SmsValidationComponent.UiState(
        phoneNumber = phoneNumber,
        smsCode = smsCode,
        isLoading = isLoading,
        error = error
    )
