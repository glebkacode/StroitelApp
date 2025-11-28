package com.itapp.core_architecture.tea

interface Reducer<State, in Intent, out Effect> {
    fun State.reduce(intent: Intent): Next<State, Effect>
}

class Next<out State, out Effect>(
    val state: State,
    val effects: List<Effect>
)