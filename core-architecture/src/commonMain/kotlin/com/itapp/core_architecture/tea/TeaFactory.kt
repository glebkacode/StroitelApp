package com.itapp.core_architecture.tea

import kotlin.coroutines.CoroutineContext

interface TeaFactory {

    fun<State, Intent, Event, Effect> create(
        initialState: State,
        dispatcher: CoroutineContext,
        effector: Effector<Effect, Intent, Event>,
        reducer: Reducer<State, Intent, Effect>
    ): Tea<State, Intent, Event>
}