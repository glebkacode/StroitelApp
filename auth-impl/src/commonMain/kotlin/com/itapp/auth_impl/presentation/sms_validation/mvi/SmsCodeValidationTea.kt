package com.itapp.auth_impl.presentation.sms_validation.mvi

import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationTea.Event
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationTea.Intent
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationTea.State
import com.itapp.core_architecture.tea.Tea

interface SmsCodeValidationTea : Tea<State, Intent, Event> {

    sealed interface Intent {
        data class SmsCodeChanged(val text: String) : Intent
        data object LoginClicked : Intent
        data object AuthSuccess : Intent
        data class AuthFailed(val throwable: Throwable) : Intent
    }

    sealed interface Effect {
        data class Login(
            val phone: String,
            val password: String,
            val smsCode: String
        ) : Effect
    }

    sealed interface Event {
        data object OpenProducts : Event
    }

    data class State(
        val loading: Boolean = true,
        val phone: String = "",
        val password: String = "",
        val smsCode: String = "",
        val throwable: Throwable? = null
    )
}