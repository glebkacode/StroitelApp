package com.itapp.auth_impl.presentation.phone_validation.mvi

import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.Intent
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.State
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.phoneValidationTea(
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): PhoneValidationTea =
    object : PhoneValidationTea, Tea<State, Intent, Nothing> by create(
        initialState = State(),
        dispatcher = mainContext,
        effectors = emptyList(),
        reducer = PhoneValidationReducer(),
    ) {}

private class PhoneValidationReducer : DslReducer<State, Intent, Nothing>() {

    override fun ReducerContext<State, Nothing>.reduce(
        intent: Intent
    ) {
        when (intent) {
            Intent.PhoneApply -> { /* No-op for now */ }
            is Intent.PhoneChanged -> { state { copy(phone = intent.text) } }
        }
    }
}
