package com.itapp.auth_impl.phone_validation.presentation.mvi

import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationTea.Intent
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationTea.State
import com.itapp.core_architecture.tea.Tea

internal interface PhoneValidationTea : Tea<State, Intent, Nothing> {

    sealed interface Intent {
        data class PhoneChanged(
            val text: String
        ) : Intent
        data object PhoneApply : Intent
    }

    data class State(
        val phone: String = ""
    )
}
