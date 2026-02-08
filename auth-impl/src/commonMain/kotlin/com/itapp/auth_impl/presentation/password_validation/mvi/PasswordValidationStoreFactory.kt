package com.itapp.auth_impl.presentation.password_validation.mvi

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.usecase.LoginUseCase
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Effect
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Intent
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.State
import com.itapp.core_architecture.tea.CoroutineEffector
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.passwordValidationTea(
    phoneNumber: String,
    loginUseCase: LoginUseCase,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): PasswordValidationTea =
    object : PasswordValidationTea, Tea<State, Intent, Effect> by create(
        initialState = State(phoneNumber = phoneNumber),
        dispatcher = mainContext,
        effectors = listOf(
            LoginEffector(loginUseCase, mainContext)
        ),
        reducer = PasswordValidationReducer(),
    ) {}

private class PasswordValidationReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(intent: Intent) {
        when (intent) {
            is Intent.PasswordChanged -> {
                state { copy(password = intent.text, error = null) }
            }
            Intent.LoginClicked -> {
                val currentState = state
                if (currentState.password.isNotBlank() && !currentState.isLoading) {
                    state { copy(isLoading = true, error = null) }
                    effects(Effect.PerformLogin(currentState.phoneNumber, currentState.password))
                }
            }
            Intent.LoginSuccess -> {
                state { copy(isLoading = false) }
                effects(Effect.NavigateToSuccess)
            }
            is Intent.LoginError -> {
                state { copy(isLoading = false, error = intent.message) }
            }
        }
    }
}

private class LoginEffector(
    private val loginUseCase: LoginUseCase,
    mainContext: CoroutineContext
) : CoroutineEffector<Effect, Intent, Effect>(mainContext) {

    override fun onEffect(effect: Effect) {
        when (effect) {
            is Effect.PerformLogin -> performLogin(effect.phoneNumber, effect.password)
            is Effect.NavigateToSuccess -> publish(effect)
        }
    }

    private fun performLogin(phoneNumber: String, password: String) {
        scope.launch {
            val result = loginUseCase(
                LoginUseCase.Params(
                    loginDto = LoginDto(
                        phoneNumber = phoneNumber,
                        password = password
                    )
                )
            )
            result.fold(
                onSuccess = { dispatch(Intent.LoginSuccess) },
                onFailure = { dispatch(Intent.LoginError(it.message ?: "Unknown error")) }
            )
        }
    }
}
