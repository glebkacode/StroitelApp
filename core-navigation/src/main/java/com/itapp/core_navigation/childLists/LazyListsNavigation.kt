package com.itapp.core_navigation.childLists

import com.arkivanov.decompose.router.children.NavigationSource

/**
 * Комбинирует [LazyListsNavigator] и [NavigationSource].
 *
 * Позволяет одновременно навигироваться по списку и подписываться на события навигации.
 *
 * ## Использование
 * ```kotlin
 * private val navigation = LazyListNavigation<ShelfConfig>()
 *
 * val children = childLazyLists(
 *     source = navigation,
 *     serializer = ShelfConfig.serializer(),
 *     childFactory = { config, ctx -> shelfFactory(ctx, config) }
 * )
 *
 * fun updateItems(newItems: List<ShelfConfig>) {
 *     navigation.navigate { LazyLists(newItems) }
 * }
 * ```
 *
 * @param C тип конфигурации элементов списка
 *
 * @see LazyListsNavigator интерфейс навигации
 * @see LazyListNavigation фабричная функция
 */
interface LazyListsNavigation<C : Any> : LazyListsNavigator<C>, NavigationSource<LazyListsNavigation.Event<C>> {

    /**
     * Событие навигации, содержащее трансформер и callback завершения.
     *
     * @param C тип конфигурации
     * @property transformer функция преобразования состояния списка
     * @property onComplete callback, вызываемый после завершения навигации
     */
    class Event<C : Any>(
        val transformer: (LazyLists<C>) -> LazyLists<C>,
        val onComplete: (newLazyLists: LazyLists<C>, oldLazyLists: LazyLists<C>) -> Unit = { _, _ -> },
    )
}

/**
 * Создаёт реализацию [LazyListsNavigation] по умолчанию.
 *
 * Транслирует события навигации всем подписанным наблюдателям.
 *
 * @param C тип конфигурации элементов списка
 * @return экземпляр [LazyListsNavigation]
 */
fun <C : Any> LazyListNavigation(): LazyListsNavigation<C> = DefaultLazyListsNavigation()
