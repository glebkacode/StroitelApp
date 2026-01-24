package com.itapp.core_navigation.childLists

import kotlinx.serialization.Serializable

/**
 * Состояние модели навигации Child Lazy Lists.
 *
 * Хранит список конфигураций дочерних компонентов и индексы видимых элементов.
 * Используется для управления жизненным циклом компонентов в зависимости от видимости.
 *
 * ## Использование
 * ```kotlin
 * // Создание с элементами
 * val lists = LazyLists(listOf(Config(1), Config(2), Config(3)))
 *
 * // Обновление видимости
 * val updated = lists.copy(firstVisibleIndex = 0, lastVisibleIndex = 2)
 * ```
 *
 * @param C тип конфигурации, должен быть уникальным в списке
 * @property items список конфигураций дочерних компонентов (может быть пустым)
 * @property firstVisibleIndex индекс первого видимого элемента (-1 если нет видимых)
 * @property lastVisibleIndex индекс последнего видимого элемента (-1 если нет видимых)
 */
@Serializable
data class LazyLists<out C : Any> internal constructor(
    val items: List<C>,
    val firstVisibleIndex: Int,
    val lastVisibleIndex: Int,
) {

    /**
     * Создаёт [LazyLists] с указанными элементами без информации о видимости.
     *
     * @param items список конфигураций
     */
    constructor(items: List<C>) : this(items = items, firstVisibleIndex = -1, lastVisibleIndex = -1)

    /**
     * Создаёт пустой [LazyLists].
     */
    constructor() : this(items = emptyList(), firstVisibleIndex = -1, lastVisibleIndex = -1)
}
