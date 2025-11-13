package com.itapp.auth_impl.presentation.sms_validation.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore.Intent
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore.Label
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore.State

interface SmsCodeValidationStore : Store<Intent, State, Label> {

    sealed interface Intent : JvmSerializable {
        data class SmsCodeChanged(val text: String) : Intent
        data object LoginClicked : Intent
    }

    sealed interface Label : JvmSerializable {
        data object OpenProducts : Label
    }

    data class State(
        val loading: Boolean = true,
        val phone: String = "",
        val password: String = "",
        val smsCode: String = "",
        val throwable: Throwable? = null
    ) : JvmSerializable
}