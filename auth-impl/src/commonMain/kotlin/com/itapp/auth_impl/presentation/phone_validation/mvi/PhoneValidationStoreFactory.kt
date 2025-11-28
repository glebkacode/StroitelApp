package com.itapp.auth_impl.presentation.phone_validation.mvi

import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.Effect
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.Event
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.Intent
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea.State
import com.itapp.core_architecture.tea.CoroutineEffector
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.phoneValidationTea(
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): PhoneValidationTea =
    object : PhoneValidationTea, Tea<State, Intent, Event> by create(
        initialState = State(),
        dispatcher = mainContext,
        effector = PhoneValidationEffectorImpl(mainContext),
        reducer = PhoneValidationReducer(),
    ) {}

private class PhoneValidationEffectorImpl(
    mainContext: CoroutineContext
) : CoroutineEffector<Effect, Intent, Event>(mainContext) {

    override fun onEffect(effect: Effect) {
        when (effect) {
            is Effect.PhoneApply -> {
                publish(Event.OpenPasswordValidation(effect.phone))
            }
        }
    }
}

private class PhoneValidationReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(
        intent: Intent
    ) {
        when (intent) {
            Intent.PhoneApply -> { effects(Effect.PhoneApply(phone = state.phone)) }
            is Intent.PhoneChanged -> { state { copy(phone = intent.text) } }
        }
    }
}
