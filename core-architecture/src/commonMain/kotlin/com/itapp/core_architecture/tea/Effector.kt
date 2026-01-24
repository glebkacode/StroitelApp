package com.itapp.core_architecture.tea

/**
 * Обработчик побочных эффектов, производимых [Reducer].
 *
 * Effector получает эффекты от reducer и выполняет асинхронные операции.
 * Может диспатчить новые intent обратно в reducer или публиковать события в UI.
 *
 * ## Использование
 * ```kotlin
 * class MyEffector : Effector<Effect, Intent, Event> {
 *     private var callback: Callback<Intent, Event>? = null
 *
 *     override fun init(callback: Callback<Intent, Event>) {
 *         this.callback = callback
 *     }
 *
 *     override fun onEffect(effect: Effect) {
 *         when (effect) {
 *             Effect.LoadData -> loadData()
 *         }
 *     }
 *
 *     private fun loadData() {
 *         // Асинхронная операция
 *         callback?.onIntent(Intent.DataLoaded(data))
 *     }
 * }
 * ```
 *
 * @param E тип эффекта, получаемого от reducer
 * @param I тип intent для отправки обратно в reducer
 * @param M тип события для публикации в UI компонент
 *
 * @see CoroutineEffector реализация на корутинах
 * @see Callback интерфейс коммуникации
 */
interface Effector<in E, I, M> {
    /**
     * Инициализирует effector с callback для коммуникации.
     *
     * @param callback интерфейс для диспатча intent и публикации событий
     */
    fun init(callback: Callback<I, M>)

    /**
     * Обрабатывает эффект от reducer.
     *
     * @param effect эффект для обработки
     */
    fun onEffect(effect: E)

    /**
     * Освобождает effector и ресурсы.
     */
    fun dispose()

    /**
     * Callback интерфейс для коммуникации effector со store.
     *
     * @param I тип intent для диспатча
     * @param M тип события для публикации
     */
    interface Callback<in I, in M> {
        /**
         * Диспатчит intent обратно в reducer.
         *
         * @param intent действие для отправки в reducer
         */
        fun onIntent(intent: I)

        /**
         * Публикует событие в UI компонент.
         *
         * @param event одноразовое событие для навигации или UI эффектов
         */
        fun onEvent(event: M)
    }
}