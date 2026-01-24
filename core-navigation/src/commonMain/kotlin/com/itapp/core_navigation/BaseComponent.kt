package com.itapp.core_navigation

import com.arkivanov.decompose.ComponentContext

/**
 * Базовый класс для всех компонентов приложения.
 *
 * Делегирует [ComponentContext] и предоставляет [componentScope] для запуска корутин,
 * привязанный к жизненному циклу компонента.
 *
 * ## Использование
 * ```kotlin
 * @AssistedInject
 * class MyComponentImpl(
 *     @Assisted componentContext: ComponentContext,
 *     private val teaFactory: TeaFactory
 * ) : BaseComponent(componentContext), MyComponent {
 *
 *     private val tea = instanceKeeper.getTea { teaFactory.myTea() }
 *
 *     init {
 *         lifecycle.doOnCreate {
 *             tea.events.onEach { handleEvent(it) }.launchIn(componentScope)
 *         }
 *     }
 * }
 * ```
 *
 * @param componentContext контекст компонента от Decompose
 *
 * @see UiComponent интерфейс для Compose UI
 * @see componentScope coroutine scope компонента
 */
abstract class BaseComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    /**
     * CoroutineScope привязанный к жизненному циклу компонента.
     * Автоматически отменяется при уничтожении компонента.
     */
    protected val componentScope = coroutineScope()
}