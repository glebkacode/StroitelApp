package com.itapp.auth_impl.presentation.password_validation.mvi

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCase
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationStore.Intent
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationStore.Label
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationStore.PasswordValidationData
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationStore.State
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal fun StoreFactory.passwordValidationStore(
    phone: String,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext,
    defaultContext: CoroutineContext,
    validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
): PasswordValidationStore =
    object : PasswordValidationStore, Store<Intent, State, Label> by create(
        name = "password_validation_store",
        initialState = State.Init(data = PasswordValidationData(phone = phone)),
        executorFactory = {
            ExecutorImpl(
                mainContext,
                ioContext,
                defaultContext,
                validatePhoneNumberUseCase
            )
        },
        reducer = PasswordValidationReducer
    ) {}

private sealed interface Msg : JvmSerializable {
    data class PasswordChanged(val text: String) : Msg
    data class LoginFailed(val throwable: Throwable) : Msg
}

private class ExecutorImpl(
    mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
    private val defaultContext: CoroutineContext,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
) : CoroutineExecutor<Intent, Nothing, State, Msg, Label>(mainContext) {

    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.LoginClicked -> onLoginClicked(state().data.phone, state().data.password)
            is Intent.PasswordChanged -> onPasswordChanged(intent.text)
        }
    }

    private fun onLoginClicked(phone: String, password: String) {
        scope.launch(defaultContext) {
            validatePhoneNumberUseCase(
                ValidatePhoneNumberUseCase.Params(
                    validationPhoneDto = ValidationPhoneDto(phone, password)
                )
            ).fold(
                onSuccess = { publish(Label.OpenSmsValidation(phone = state().data.phone)) },
                onFailure = { throwable -> dispatch(Msg.LoginFailed(throwable)) }
            )
        }
    }

    private fun onPasswordChanged(password: String) {
        dispatch(Msg.PasswordChanged(password))
    }
}

private object PasswordValidationReducer : Reducer<State, Msg> {

    override fun State.reduce(
        msg: Msg
    ): State {
        return when (msg) {
            is Msg.PasswordChanged -> State.PasswordChanged(
                data = PasswordValidationData(
                    phone = data.phone,
                    password = msg.text
                )
            )
            is Msg.LoginFailed -> State.AuthFailed(
                data = PasswordValidationData(
                    phone = data.phone,
                    password = data.password
                ),
                throwable = msg.throwable
            )
        }
    }
}