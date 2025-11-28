package com.itapp.core_architecture.tea

class ReducerContext<State, Effect>(initState: State) {
    var state: State = initState
        private set
    private val effectsList = mutableListOf<Effect>()

    fun state(stateBuilder: State.() -> State) {
        state = state.stateBuilder()
    }

    fun effects(vararg effects: Effect) {
        effectsList.addAll(effects)
    }

    fun effects(effects: List<Effect>) {
        effectsList.addAll(effects)
    }

    internal fun build(): Next<State, Effect> {
        return Next(state, effectsList)
    }
}