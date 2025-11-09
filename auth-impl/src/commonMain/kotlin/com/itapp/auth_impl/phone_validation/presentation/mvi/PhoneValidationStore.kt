package com.itapp.auth_impl.phone_validation.presentation.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.Intent
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.Label
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.State

internal interface PhoneValidationStore : Store<Intent, State, Label> {

    sealed interface Intent : JvmSerializable {
        data class PhoneChanged(
            val text: String
        ) : Intent
        data object PhoneApply : Intent
    }

    sealed interface Label : JvmSerializable {
        data class OpenPasswordValidation(val phone: String) : Label
    }

    data class State(
        val phone: String = ""
    ) : JvmSerializable
}