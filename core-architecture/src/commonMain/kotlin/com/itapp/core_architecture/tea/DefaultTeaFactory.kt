package com.itapp.core_architecture.tea

import kotlin.coroutines.CoroutineContext

class DefaultTeaFactory : TeaFactory {

    override fun <State, Intent, Event, Effect> create(
        initialState: State,
        initialEffects: List<Effect>,
        dispatcher: CoroutineContext,
        effectors: List<Effector<Effect, Intent, Event>>,
        reducer: Reducer<State, Intent, Effect>,
    ): Tea<State, Intent, Event> {
        return DefaultTea(
            initialState = initialState,
            initialEffects = initialEffects,
            dispatcher = dispatcher,
            effectors = effectors,
            reducer = reducer
        ).apply {
            init()
        }
    }
}