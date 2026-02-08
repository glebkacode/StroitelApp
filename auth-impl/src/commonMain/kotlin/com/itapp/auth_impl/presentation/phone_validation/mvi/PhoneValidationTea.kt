package com.itapp.auth_impl.presentation.phone_validation.mvi

import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.Intent
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.State
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
