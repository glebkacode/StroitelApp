package com.itapp.core_navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Интерфейс для компонентов с Compose UI.
 *
 * Реализуйте этот интерфейс вместе с [BaseComponent] для создания компонентов
 * с собственным UI.
 *
 * ## Использование
 * ```kotlin
 * class MyComponentImpl(...) : BaseComponent(componentContext), MyComponent, UiComponent {
 *
 *     @Composable
 *     override fun render(modifier: Modifier) {
 *         MyScreen(modifier = modifier, component = this)
 *     }
 * }
 *
 * // В родительском компоненте
 * @Composable
 * fun ParentScreen(child: UiComponent) {
 *     child.render(Modifier.fillMaxSize())
 * }
 * ```
 *
 * @see BaseComponent базовый класс компонента
 */
interface UiComponent {
    /**
     * Рендерит UI компонента.
     *
     * @param modifier модификатор для корневого элемента
     */
    @Composable
    fun render(modifier: Modifier = Modifier)
}