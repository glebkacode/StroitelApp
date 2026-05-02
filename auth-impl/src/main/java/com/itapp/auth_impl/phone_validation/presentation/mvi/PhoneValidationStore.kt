package com.itapp.auth_impl.phone_validation.presentation.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.Intent
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.Label
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.State

internal interface PhoneValidationStore : Store<Intent, State, Label> {

    sealed interface Intent {
        data class PhoneChanged(val text: String) : Intent
        data object PhoneApply : Intent
    }

    data class State(
        val phone: String = ""
    )

    sealed interface Label
}
