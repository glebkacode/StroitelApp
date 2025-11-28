package com.itapp.auth_impl.presentation.phone_validation.mvi

import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.Event
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.Intent
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.State
import com.itapp.core_architecture.tea.Tea

internal interface PhoneValidationTea : Tea<State, Intent, Event> {

    sealed interface Intent {
        data class PhoneChanged(
            val text: String
        ) : Intent
        data object PhoneApply : Intent
    }

    sealed interface Effect {
        data class PhoneApply(val phone: String) : Effect
    }

    sealed interface Event {
        data class OpenPasswordValidation(val phone: String) : Event
    }

    data class State(
        val phone: String = ""
    )
}