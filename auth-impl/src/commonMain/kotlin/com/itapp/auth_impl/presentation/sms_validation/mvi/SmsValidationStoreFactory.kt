package com.itapp.auth_impl.presentation.sms_validation.mvi

import com.itapp.auth_impl.domain.usecase.ValidateSmsCodeUseCase
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsValidationTea.Effect
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsValidationTea.Intent
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsValidationTea.State
import com.itapp.core_architecture.tea.CoroutineEffector
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.smsValidationTea(
    phoneNumber: String,
    validateSmsCodeUseCase: ValidateSmsCodeUseCase,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): SmsValidationTea =
    object : SmsValidationTea, Tea<State, Intent, Effect> by create(
        initialState = State(phoneNumber = phoneNumber),
        dispatcher = mainContext,
        effectors = listOf(
            SmsVerificationEffector(validateSmsCodeUseCase, mainContext)
        ),
        reducer = SmsValidationReducer(),
    ) {}

private class SmsValidationReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(intent: Intent) {
        when (intent) {
            is Intent.SmsCodeChanged -> {
                state { copy(smsCode = intent.text, error = null) }
            }
            Intent.ConfirmClicked -> {
                val currentState = state
                if (currentState.smsCode.isNotBlank() && !currentState.isLoading) {
                    state { copy(isLoading = true, error = null) }
                    effects(Effect.VerifySmsCode(currentState.phoneNumber, currentState.smsCode))
                }
            }
            Intent.VerificationSuccess -> {
                state { copy(isLoading = false) }
                effects(Effect.NavigateToPassword)
            }
            is Intent.VerificationError -> {
                state { copy(isLoading = false, error = intent.message) }
            }
        }
    }
}

private class SmsVerificationEffector(
    private val validateSmsCodeUseCase: ValidateSmsCodeUseCase,
    mainContext: CoroutineContext
) : CoroutineEffector<Effect, Intent, Effect>(mainContext) {

    override fun onEffect(effect: Effect) {
        when (effect) {
            is Effect.VerifySmsCode -> verifySmsCode(effect.phoneNumber, effect.smsCode)
            is Effect.NavigateToPassword -> publish(effect)
        }
    }

    private fun verifySmsCode(phoneNumber: String, smsCode: String) {
        scope.launch {
            val result = validateSmsCodeUseCase(
                ValidateSmsCodeUseCase.Params(
                    phoneNumber = phoneNumber,
                    smsCode = smsCode
                )
            )
            result.fold(
                onSuccess = { dispatch(Intent.VerificationSuccess) },
                onFailure = { dispatch(Intent.VerificationError(it.message ?: "Unknown error")) }
            )
        }
    }
}
