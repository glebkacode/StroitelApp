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
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.coroutines.CoroutineContext

/**
 * Реализация TEA store по умолчанию.
 *
 * Оркестрирует поток данных между reducer и effector-ами:
 * 1. Intent отправляется через [accept]
 * 2. Reducer обрабатывает intent и возвращает новое состояние + эффекты
 * 3. Состояние эмитится через [state]
 * 4. Эффекты передаются всем effector-ам
 * 5. Effector-ы могут диспатчить новые intent или публиковать события
 * 6. События эмитятся через [events]
 *
 * @param State неизменяемый тип состояния
 * @param Intent тип действия
 * @param Effect тип побочного эффекта
 * @param Event тип одноразового события
 * @param initialState начальное состояние
 * @param initialEffects эффекты для выполнения при инициализации
 * @param dispatcher контекст корутин для основного цикла
 * @param effectors список effector-ов для обработки эффектов
 * @param reducer reducer для преобразования состояния
 *
 * @see Tea интерфейс
 * @see TeaFactory для создания экземпляров
 */
@OptIn(ExperimentalAtomicApi::class)
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
    private val isInitialized = AtomicBoolean(false)

    override fun init() {
        if (!isInitialized.compareAndSet(expectedValue = false, newValue = true)) return
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
        intents.cancel()
        effectors.forEach { effector ->
            effector.dispose()
        }
        scope.cancel()
    }
}