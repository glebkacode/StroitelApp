package com.itapp.core_architecture.tea

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.coroutines.CoroutineContext

/**
 * Базовый класс effector с поддержкой корутин.
 *
 * Предоставляет [CoroutineScope] для запуска асинхронных операций
 * и методы [dispatch] и [publish] для коммуникации со store.
 *
 * ## Использование
 * ```kotlin
 * class MyEffector(
 *     private val repository: Repository
 * ) : CoroutineEffector<Effect, Intent, Event>() {
 *
 *     override fun onEffect(effect: Effect) {
 *         when (effect) {
 *             Effect.LoadData -> loadData()
 *             is Effect.Navigate -> publish(Event.NavigateTo(effect.screen))
 *         }
 *     }
 *
 *     private fun loadData() {
 *         scope.launch {
 *             val result = repository.getData()
 *             dispatch(Intent.DataLoaded(result))
 *         }
 *     }
 * }
 * ```
 *
 * @param E тип эффекта, получаемого от reducer
 * @param I тип intent для отправки обратно в reducer
 * @param M тип события для публикации в UI компонент
 * @param mainCoroutineContext контекст корутин для scope (по умолчанию Main)
 *
 * @see Effector базовый интерфейс
 */
open class CoroutineEffector<E, I, M>(
    mainCoroutineContext: CoroutineContext = Dispatchers.Main
) : Effector<E, I, M> {

    @OptIn(ExperimentalAtomicApi::class)
    private val atomic = AtomicReference<Effector.Callback<I, M>?>(null)

    /**
     * CoroutineScope для запуска асинхронных операций.
     * Автоматически отменяется при вызове [dispose].
     */
    protected val scope = CoroutineScope(mainCoroutineContext + SupervisorJob())

    @OptIn(ExperimentalAtomicApi::class)
    override fun init(callback: Effector.Callback<I, M>) {
        atomic.compareAndSet(null, callback)
    }

    override fun onEffect(effect: E) {}

    /**
     * Диспатчит intent обратно в reducer.
     *
     * @param intent действие для отправки
     */
    @OptIn(ExperimentalAtomicApi::class)
    protected fun dispatch(intent: I) {
        atomic.load()?.onIntent(intent)
    }

    /**
     * Публикует событие в UI компонент.
     *
     * @param event одноразовое событие
     */
    @OptIn(ExperimentalAtomicApi::class)
    protected fun publish(event: M) {
        atomic.load()?.onEvent(event)
    }

    override fun dispose() {
        scope.cancel()
    }
}