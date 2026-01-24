package com.itapp.core_architecture.tea

/**
 * Контекст для DSL-стиля редукции состояния в [DslReducer].
 *
 * Предоставляет методы для изменения состояния и эмиссии эффектов в builder-подобном паттерне.
 *
 * ## Использование
 * ```kotlin
 * fun ReducerContext<State, Effect>.reduce(intent: Intent) {
 *     // Изменение состояния через copy
 *     state { copy(text = "new value") }
 *
 *     // Эмиссия одного эффекта
 *     effects(Effect.LoadData)
 *
 *     // Эмиссия нескольких эффектов
 *     effects(Effect.SaveData, Effect.ShowMessage)
 * }
 * ```
 *
 * @param State неизменяемый тип состояния
 * @param Effect тип побочного эффекта
 * @property state текущее состояние (только для чтения извне)
 */
class ReducerContext<State, Effect>(initState: State) {
    /**
     * Текущее значение состояния. Изменяется через метод [state].
     */
    var state: State = initState
        private set
    private val effectsList = mutableListOf<Effect>()

    /**
     * Изменяет текущее состояние используя builder-функцию.
     *
     * ## Использование
     * ```kotlin
     * state { copy(text = "new value", isLoading = false) }
     * ```
     *
     * @param stateBuilder функция, получающая текущее состояние и возвращающая новое
     */
    fun state(stateBuilder: State.() -> State) {
        state = state.stateBuilder()
    }

    /**
     * Добавляет эффекты для обработки после редукции.
     *
     * @param effects vararg эффектов для эмиссии
     */
    fun effects(vararg effects: Effect) {
        effectsList.addAll(effects)
    }

    /**
     * Добавляет список эффектов для обработки после редукции.
     *
     * @param effects список эффектов для эмиссии
     */
    fun effects(effects: List<Effect>) {
        effectsList.addAll(effects)
    }

    /**
     * Собирает финальный результат [Next]. Только для внутреннего использования.
     */
    internal fun build(): Next<State, Effect> {
        return Next(state, effectsList)
    }
}