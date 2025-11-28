package com.itapp.core_architecture.tea

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.coroutines.CoroutineContext

open class CoroutineEffector<E, I, M>(
    mainCoroutineContext: CoroutineContext = Dispatchers.Main
) : Effector<E, I, M> {

    @OptIn(ExperimentalAtomicApi::class)
    private val atomic = AtomicReference<Effector.Callback<I, M>?>(null)
    protected val scope = CoroutineScope(mainCoroutineContext)

    @OptIn(ExperimentalAtomicApi::class)
    override fun init(callback: Effector.Callback<I, M>) {
        atomic.compareAndSet(null, callback)
    }

    override fun onEffect(effect: E) {}

    @OptIn(ExperimentalAtomicApi::class)
    protected fun dispatch(intent: I) {
        atomic.load()?.onIntent(intent)
    }

    @OptIn(ExperimentalAtomicApi::class)
    protected fun publish(event: M) {
        atomic.load()?.onEvent(event)
    }

    override fun dispose() {
        scope.cancel()
    }
}