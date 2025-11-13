package com.itapp.auth_impl.presentation.password_validation.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationStore.Intent
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationStore.Label
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationStore.State

interface PasswordValidationStore : Store<Intent, State, Label> {

    sealed interface Intent : JvmSerializable {
        data class PasswordChanged(val text: String) : Intent
        data object ValidatePasswordClicked : Intent
    }

    sealed interface Label : JvmSerializable {
        data class OpenSmsValidation(
            val phone: String,
            val password: String
        ) : Label
    }

    sealed class State(
        open val data: PasswordValidationData
    ) : JvmSerializable {

        data class Init(
            override val data: PasswordValidationData
        ) : State(data)

        data class PasswordChanged(
            override val data: PasswordValidationData
        ) : State(data)

        data class AuthFailed(
            override val data: PasswordValidationData,
            val throwable: Throwable
        ) : State(data)
    }

    data class PasswordValidationData(val phone: String = "", val password: String = "")
}