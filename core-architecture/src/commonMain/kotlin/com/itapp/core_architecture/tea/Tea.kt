package com.itapp.core_architecture.tea

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Tea<out State, in Intent, out Event> {
    val state: StateFlow<State>
    val events: Flow<Event>

    fun init()
    fun accept(intent: Intent)
    fun dispose()
}