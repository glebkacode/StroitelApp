package com.itapp.core_architecture.tea

/**
 * Чистая функция, преобразующая состояние на основе intent.
 *
 * Reducer - ядро TEA архитектуры. Это чистая функция без побочных эффектов.
 * Получая текущее состояние и intent, производит новое состояние и опциональные эффекты.
 *
 * ## Использование
 * ```kotlin
 * class MyReducer : Reducer<State, Intent, Effect> {
 *     override fun State.reduce(intent: Intent): Next<State, Effect> {
 *         return when (intent) {
 *             is Intent.UpdateText -> Next(copy(text = intent.value), emptyList())
 *             Intent.Submit -> Next(this, listOf(Effect.SendData))
 *         }
 *     }
 * }
 * ```
 *
 * @param State неизменяемый тип состояния
 * @param Intent тип действия, вызывающего изменение состояния
 * @param Effect тип побочного эффекта для обработки в [Effector]
 *
 * @see DslReducer для DSL-стиля реализации
 * @see Next контейнер результата
 */
interface Reducer<State, in Intent, out Effect> {
    /**
     * Преобразует текущее состояние на основе переданного intent.
     *
     * Эта функция должна быть чистой - никаких побочных эффектов, никакого внешнего состояния.
     * Все асинхронные операции должны делегироваться через [Effect].
     *
     * @receiver текущее состояние
     * @param intent действие для обработки
     * @return [Next] содержащий новое состояние и список эффектов
     */
    fun State.reduce(intent: Intent): Next<State, Effect>
}

/**
 * Контейнер результата функции [Reducer.reduce].
 *
 * Содержит новое состояние и список эффектов для обработки effector-ами.
 *
 * @param State неизменяемый тип состояния
 * @param Effect тип побочного эффекта
 * @property state новое состояние после редукции
 * @property effects список побочных эффектов для обработки (может быть пустым)
 */
class Next<out State, out Effect>(
    val state: State,
    val effects: List<Effect>
)