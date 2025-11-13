package com.itapp.auth_impl.presentation.sms_validation.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.usecase.AuthUseCase
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore.Intent
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore.Label
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore.State
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal fun StoreFactory.smsValidationStore(
    mainContext: CoroutineContext,
    defaultContext: CoroutineContext,
    ioContext: CoroutineContext,
    phone: String,
    password: String,
    authUseCase: AuthUseCase
): SmsCodeValidationStore = object : SmsCodeValidationStore, Store<Intent, State, Label> by create(
    name = "sms_validation_store",
    initialState = State(
        phone = phone,
        password = password
    ),
    executorFactory = { ExecutorImpl(
        mainContext,
        defaultContext,
        ioContext,
        authUseCase
    ) },
    reducer = { reduce(it) }
) {}

private class ExecutorImpl(
    private val mainContext: CoroutineContext,
    private val defaultContext: CoroutineContext,
    private val ioContext: CoroutineContext,
    private val authUseCase: AuthUseCase
) : CoroutineExecutor<Intent, Nothing, State, Msg, Label>(mainContext) {

    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.LoginClicked -> {
                onLoginClicked(
                    phone = state().phone,
                    password = state().password,
                    smsCode = state().smsCode
                )
            }

            is Intent.SmsCodeChanged -> onSmsCodeChanged(smsCode = intent.text)
        }
    }

    private fun onLoginClicked(
        phone: String,
        password: String,
        smsCode: String
    ) {
        scope.launch(defaultContext) {
            authUseCase(
                AuthUseCase.Params(
                    dto = (LoginDto(
                        phone = phone,
                        password = password,
                        smsCode = smsCode
                    ))
                )
            ).fold(
                onSuccess = {
                    withContext(mainContext) {
                        dispatch(Msg.AuthSuccess)
                        publish(Label.OpenProducts)
                    }
                },
                onFailure = { throwable ->
                    withContext(mainContext) {
                        dispatch(Msg.AuthFailed(throwable))
                    }
                }
            )
        }
        dispatch(Msg.AuthLoading)
    }

    private fun onSmsCodeChanged(smsCode: String) {
        dispatch(Msg.SmsCodeChanged(smsCode))
    }
}

private sealed interface Msg : JvmSerializable {
    data class SmsCodeChanged(val smsCode: String) : Msg
    data object AuthLoading : Msg
    data object AuthSuccess : Msg
    data class AuthFailed(val throwable: Throwable) : Msg
}

private fun State.reduce(msg: Msg): State {
    return when (msg) {
        is Msg.AuthFailed -> copy(loading = false, throwable = msg.throwable)
        Msg.AuthLoading -> copy(loading = true)
        Msg.AuthSuccess -> copy(loading = false)
        is Msg.SmsCodeChanged -> copy(loading = false, smsCode = msg.smsCode)
    }
}