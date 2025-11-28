package com.itapp.auth_impl.presentation.sms_validation.mvi

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.usecase.AuthUseCase
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationTea.Effect
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationTea.Event
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationTea.Intent
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationTea.State
import com.itapp.core_architecture.tea.CoroutineEffector
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.smsValidationTea(
    mainContext: CoroutineContext,
    defaultContext: CoroutineContext,
    ioContext: CoroutineContext,
    phone: String,
    password: String,
    authUseCase: AuthUseCase
): SmsCodeValidationTea = object : SmsCodeValidationTea, Tea<State, Intent, Event> by create(
    initialState = State(
        phone = phone,
        password = password
    ),
    dispatcher = mainContext,
    effector = SmsValidationEffectorImpl(
        mainContext,
        defaultContext,
        ioContext,
        authUseCase
    ),
    reducer = SmsValidationReducer()
) {}

private class SmsValidationEffectorImpl(
    private val mainContext: CoroutineContext,
    private val defaultContext: CoroutineContext,
    private val ioContext: CoroutineContext,
    private val authUseCase: AuthUseCase
) : CoroutineEffector<Effect, Intent, Event>(mainContext) {

    override fun onEffect(effect: Effect) {
        when (effect) {
            is Effect.Login -> onLoginClicked(
                phone = effect.phone,
                password = effect.password,
                smsCode = effect.smsCode
            )
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
                        dispatch(Intent.AuthSuccess)
                        publish(Event.OpenProducts)
                    }
                },
                onFailure = { throwable ->
                    withContext(mainContext) {
                        dispatch(Intent.AuthFailed(throwable))
                    }
                }
            )
        }
    }
}

private class SmsValidationReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(
        intent: Intent
    ) {
        when (intent) {
            is Intent.AuthFailed -> {
                state { copy(loading = false, throwable = intent.throwable) }
            }

            Intent.AuthSuccess -> {
                state { copy(loading = false) }
            }

            Intent.LoginClicked -> {
                effects(
                    Effect.Login(
                        phone = state.phone,
                        password = state.password,
                        smsCode = state.smsCode
                    )
                )
            }

            is Intent.SmsCodeChanged -> {
                state { copy(loading = false, smsCode = intent.text) }
            }
        }
    }
}