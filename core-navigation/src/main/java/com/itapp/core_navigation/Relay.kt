package com.itapp.core_navigation

import com.arkivanov.decompose.Cancellation
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal class Relay<T> {

    private val lock = Lock()
    private val queue = ArrayDeque<T>()
    private var isDraining = false
    private var observers = emptySet<(T) -> Unit>()

    fun accept(value: T) {
        lock.withLockSync {
            queue.addLast(value)

            if (isDraining) {
                return
            }

            isDraining = true
        }

        drainLoop()
    }

    private fun drainLoop() {
        while (true) {
            val value: T
            val observersCopy: Set<(T) -> Unit>

            lock.withLockSync {
                if (queue.isEmpty()) {
                    isDraining = false
                    return
                }

                value = queue.removeFirst()
                observersCopy = observers
            }

            observersCopy.forEach { observer ->
                observer(value)
            }
        }
    }

    fun subscribe(observer: (T) -> Unit): Cancellation {
        lock.withLockSync { observers += observer }

        return Cancellation {
            lock.withLockSync { observers -= observer }
        }
    }
}

internal class Lock {
    // Since Relay is used on the main thread for Decompose navigation,
    // we can use a simple inline execution for common code.
    // The synchronization primitives are platform-specific.
    @Suppress("LEAKED_IN_PLACE_LAMBDA", "WRONG_INVOCATION_KIND")
    @OptIn(ExperimentalContracts::class)
    inline fun <T> withLockSync(block: () -> T): T {
        contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
        return block()
    }
}
