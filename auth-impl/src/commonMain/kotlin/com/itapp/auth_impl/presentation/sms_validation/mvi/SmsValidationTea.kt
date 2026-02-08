package com.itapp.auth_impl.presentation.sms_validation.mvi

import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsValidationTea.Effect
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsValidationTea.Intent
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsValidationTea.State
import com.itapp.core_architecture.tea.Tea

internal interface SmsValidationTea : Tea<State, Intent, Effect> {

    sealed interface Intent {
        data class SmsCodeChanged(val text: String) : Intent
        data object ConfirmClicked : Intent
        data object VerificationSuccess : Intent
        data class VerificationError(val message: String) : Intent
    }

    sealed interface Effect {
        data class VerifySmsCode(val phoneNumber: String, val smsCode: String) : Effect
        data object NavigateToPassword : Effect
    }

    data class State(
        val phoneNumber: String = "",
        val smsCode: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
