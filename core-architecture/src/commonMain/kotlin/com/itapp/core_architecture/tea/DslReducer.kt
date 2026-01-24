package com.itapp.core_architecture.tea

/**
 * DSL-стиль reducer для более читаемых преобразований состояния.
 *
 * Расширяет [Reducer] с DSL, позволяющим использовать методы [ReducerContext]
 * такие как [ReducerContext.state] и [ReducerContext.effects] напрямую.
 *
 * ## Использование
 * ```kotlin
 * class MyReducer : DslReducer<State, Intent, Effect>() {
 *     override fun ReducerContext<State, Effect>.reduce(intent: Intent) {
 *         when (intent) {
 *             is Intent.UpdateText -> {
 *                 state { copy(text = intent.value) }
 *             }
 *             Intent.Submit -> {
 *                 state { copy(isLoading = true) }
 *                 effects(Effect.SendData)
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param State неизменяемый тип состояния
 * @param Intent тип действия, вызывающего изменение состояния
 * @param Effect тип побочного эффекта для обработки в [Effector]
 *
 * @see ReducerContext доступные DSL методы
 * @see Reducer базовый интерфейс
 */
abstract class DslReducer<State, in Intent, Effect> : Reducer<State, Intent, Effect> {

    override fun State.reduce(intent: Intent): Next<State, Effect> {
        return ReducerContext<State, Effect>(initState = this).apply { reduce(intent) }.build()
    }

    /**
     * DSL метод для реализации логики редукции состояния.
     *
     * Используйте [ReducerContext.state] для изменения состояния и [ReducerContext.effects] для эмиссии эффектов.
     *
     * @param intent действие для обработки
     */
    abstract fun ReducerContext<State, Effect>.reduce(intent: Intent)
}