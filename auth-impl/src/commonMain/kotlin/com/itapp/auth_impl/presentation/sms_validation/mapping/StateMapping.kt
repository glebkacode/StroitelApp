package com.itapp.auth_impl.presentation.sms_validation.mapping

import com.itapp.auth_api.sms_validation.SmsValidationComponent.UiState
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore.State

internal fun State.toUiState(): UiState {
    return UiState(
        loading = loading,
        smsCode = smsCode
    )
}