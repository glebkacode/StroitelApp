package com.itapp.core_architecture.tea

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Главный интерфейс TEA (The Elm Architecture) store.
 *
 * Обеспечивает однонаправленный поток данных с наблюдением состояния через [StateFlow]
 * и эмиссией одноразовых событий через [Flow].
 *
 * ## Использование
 * ```kotlin
 * val tea: Tea<State, Intent, Event> = teaFactory.create(...)
 * tea.init()
 *
 * // Наблюдение за состоянием
 * tea.state.collect { state -> updateUI(state) }
 *
 * // Наблюдение за событиями
 * tea.events.collect { event -> handleNavigation(event) }
 *
 * // Отправка intent
 * tea.accept(Intent.LoadData)
 * ```
 *
 * @param State неизменяемый тип состояния экрана
 * @param Intent тип действия для изменения состояния
 * @param Event тип одноразового события для навигации или UI эффектов
 *
 * @see DefaultTea реализация по умолчанию
 * @see TeaFactory для создания Tea экземпляров
 */
interface Tea<out State, in Intent, out Event> {
    /**
     * Текущее состояние как [StateFlow]. Подписывайтесь на этот flow для наблюдения за изменениями.
     */
    val state: StateFlow<State>

    /**
     * Одноразовые события как [Flow]. Подписывайтесь для навигации и UI эффектов.
     * События не переигрываются для новых подписчиков.
     */
    val events: Flow<Event>

    /**
     * Инициализирует store. Должен быть вызван перед отправкой intent.
     * Безопасно вызывать несколько раз - повторные вызовы игнорируются.
     */
    fun init()

    /**
     * Отправляет intent в store для обработки reducer.
     *
     * @param intent действие для обработки
     */
    fun accept(intent: Intent)

    /**
     * Освобождает store и все ресурсы.
     * После вызова dispose store не должен использоваться.
     */
    fun dispose()
}