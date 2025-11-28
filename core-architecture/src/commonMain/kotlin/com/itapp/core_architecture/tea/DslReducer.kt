package com.itapp.core_architecture.tea

abstract class DslReducer<State, in Intent, Effect> : Reducer<State, Intent, Effect> {

    override fun State.reduce(intent: Intent): Next<State, Effect> {
        return ReducerContext<State, Effect>(initState = this).apply { reduce(intent) }.build()
    }

    abstract fun ReducerContext<State, Effect>.reduce(intent: Intent)
}