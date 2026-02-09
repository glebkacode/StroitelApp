package com.itapp.auth_impl.presentation.password_validation.mvi

import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Intent
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.State
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.passwordValidationTea(
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): PasswordValidationTea =
    object : PasswordValidationTea, Tea<State, Intent, Nothing> by create(
        initialState = State(),
        dispatcher = mainContext,
        effectors = emptyList(),
        reducer = PasswordValidationReducer(),
    ) {}

private class PasswordValidationReducer : DslReducer<State, Intent, Nothing>() {

    override fun ReducerContext<State, Nothing>.reduce(
        intent: Intent
    ) {
        when (intent) {
            Intent.ContinueClicked -> { /* No-op for now */ }
            is Intent.PasswordChanged -> { state { copy(password = intent.text) } }
        }
    }
}
