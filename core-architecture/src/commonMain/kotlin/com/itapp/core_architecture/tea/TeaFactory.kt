package com.itapp.core_architecture.tea

import kotlin.coroutines.CoroutineContext

/**
 * Фабрика для создания [Tea] экземпляров.
 *
 * Используется для внедрения зависимостей и создания store с нужной конфигурацией.
 *
 * ## Использование
 * ```kotlin
 * val tea = teaFactory.create(
 *     initialState = State(),
 *     initialEffects = listOf(Effect.LoadData),
 *     dispatcher = Dispatchers.Main,
 *     effectors = listOf(myEffector),
 *     reducer = MyReducer()
 * )
 * ```
 *
 * @see DefaultTeaFactory реализация по умолчанию
 * @see Tea созданный интерфейс
 */
interface TeaFactory {

    /**
     * Создаёт новый [Tea] экземпляр.
     *
     * @param State тип состояния
     * @param Intent тип intent
     * @param Event тип события
     * @param Effect тип эффекта
     * @param initialState начальное состояние
     * @param initialEffects эффекты для выполнения при инициализации
     * @param dispatcher контекст корутин
     * @param effectors список effector-ов
     * @param reducer reducer для обработки intent
     * @return настроенный [Tea] экземпляр
     */
    fun<State, Intent, Event, Effect> create(
        initialState: State,
        initialEffects: List<Effect> = emptyList(),
        dispatcher: CoroutineContext,
        effectors: List<Effector<Effect, Intent, Event>>,
        reducer: Reducer<State, Intent, Effect>
    ): Tea<State, Intent, Event>
}