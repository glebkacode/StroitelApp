package com.itapp.auth_impl.presentation.password_validation.mvi

import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCase
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.*
import com.itapp.core_architecture.tea.CoroutineEffector
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.passwordValidationTea(
    phone: String,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext,
    defaultContext: CoroutineContext,
    validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
): PasswordValidationTea =
    object : PasswordValidationTea, Tea<State, Intent, Event> by create(
        initialState = State.Init(data = PasswordValidationData(phone = phone)),
        dispatcher = mainContext,
        effector = PasswordValidationEffector(
            mainContext, ioContext, defaultContext, validatePhoneNumberUseCase
        ),
        reducer = PasswordValidationReducer()
    ) {}

private class PasswordValidationEffector(
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
    private val defaultContext: CoroutineContext,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase
) : CoroutineEffector<Effect, Intent, Event>(mainContext) {

    override fun onEffect(effect: Effect) {
        when (effect) {
            is Effect.ValidatePassword -> onLoginClicked(
                phone = effect.phone,
                password = effect.password
            )
        }
    }

    private fun onLoginClicked(phone: String, password: String) {
        scope.launch(defaultContext) {
            validatePhoneNumberUseCase(
                ValidatePhoneNumberUseCase.Params(
                    validationPhoneDto = ValidationPhoneDto(phone, password)
                )
            ).fold(
                onSuccess = {
                    withContext(mainContext) {
                        publish(
                            Event.OpenSmsValidation(
                                phone = phone,
                                password = password
                            )
                        )
                    }
                },
                onFailure = { throwable ->
                    withContext(mainContext) {
                        dispatch(Intent.LoginFailed(throwable))
                    }
                }
            )
        }
    }
}

private class PasswordValidationReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(
        intent: Intent
    ) {
        when (intent) {
            is Intent.LoginFailed -> {
                state {
                    State.AuthFailed(
                        data = PasswordValidationData(
                            phone = data.phone,
                            password = data.password
                        ),
                        throwable = intent.throwable
                    )
                }
            }

            is Intent.PasswordChanged -> {
                state {
                    State.PasswordChanged(
                        data = PasswordValidationData(
                            phone = data.phone,
                            password = intent.text
                        )
                    )
                }
            }

            Intent.ValidatePasswordClicked -> {
                effects(
                    Effect.ValidatePassword(
                        phone = state.data.phone,
                        password = state.data.password
                    )
                )
            }
        }
    }
}