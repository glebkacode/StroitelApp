package com.itapp.core_navigation

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * Создаёт [CoroutineScope], привязанный к жизненному циклу [LifecycleOwner].
 *
 * Scope автоматически отменяется при вызове `onDestroy` в lifecycle.
 * Используется в [BaseComponent] для создания [BaseComponent.componentScope].
 *
 * ## Использование
 * ```kotlin
 * class MyComponent(componentContext: ComponentContext) : ComponentContext by componentContext {
 *     private val scope = coroutineScope()
 *
 *     fun loadData() {
 *         scope.launch {
 *             // Корутина будет отменена при уничтожении компонента
 *             val data = repository.getData()
 *         }
 *     }
 * }
 * ```
 *
 * @param context контекст корутин (по умолчанию [Dispatchers.Main.immediate])
 * @return [CoroutineScope] привязанный к lifecycle
 */
fun LifecycleOwner.coroutineScope(
    context: CoroutineContext = Dispatchers.Main.immediate,
): CoroutineScope =
    CoroutineScope(context = context).also { scope ->
        lifecycle.doOnDestroy(scope::cancel)
    }