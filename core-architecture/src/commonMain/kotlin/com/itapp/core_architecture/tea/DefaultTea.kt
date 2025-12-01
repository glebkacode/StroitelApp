package com.itapp.core_architecture.tea

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DefaultTea<out State, in Intent, in Effect, Event>(
    initialState: State,
    private val initialEffects: List<Effect> = emptyList(),
    dispatcher: CoroutineContext,
    private val effectors: List<Effector<Effect, Intent, Event>>,
    private val reducer: Reducer<State, Intent, Effect>,
) : Tea<State, Intent, Event> {

    private val _states = MutableStateFlow(initialState)
    override val state: StateFlow<State> = _states

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 64)
    override val events: Flow<Event> = _events.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val intents = Channel<Intent>(capacity = 64)

    override fun init() {
        effectors.forEach { effector ->
            effector.init(object : Effector.Callback<Intent, Event> {
                override fun onIntent(intent: Intent) {
                    intents.trySend(intent)
                }

                override fun onEvent(event: Event) {
                    _events.tryEmit(event)
                }
            })
        }

        initialEffects.forEach { effect ->
            effectors.forEach { effector ->
                effector.onEffect(effect)
            }
        }
        scope.launch {
            while (isActive) {
                for (intent in intents) {
                    reducer.run {
                        val curState = _states.value
                        val next = curState.reduce(intent)
                        _states.update { next.state }
                        next.effects.forEach { effect ->
                            effectors.forEach { effector ->
                                effector.onEffect(effect)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun accept(intent: Intent) {
        intents.trySend(intent)
    }

    override fun dispose() {
        effectors.forEach { effector ->
            effector.dispose()
        }
        scope.cancel()
    }
}