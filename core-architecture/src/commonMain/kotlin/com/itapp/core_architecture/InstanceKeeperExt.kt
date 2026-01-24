package com.itapp.core_architecture

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.itapp.core_architecture.tea.Tea
import kotlin.reflect.typeOf

/**
 * Получает или создаёт [Tea] экземпляр, привязанный к жизненному циклу [InstanceKeeper].
 *
 * Tea автоматически освобождается при уничтожении InstanceKeeper (например, при уничтожении компонента).
 *
 * ## Использование
 * ```kotlin
 * class MyComponent(componentContext: ComponentContext) : BaseComponent(componentContext) {
 *     private val tea = instanceKeeper.getTea(key = "my_tea") {
 *         teaFactory.create(...)
 *     }
 * }
 * ```
 *
 * @param T тип Tea
 * @param key уникальный ключ для хранения экземпляра
 * @param factory фабрика для создания Tea при первом вызове
 * @return существующий или новый Tea экземпляр
 */
fun <T : Tea<*, *, *>> InstanceKeeper.getTea(key: Any, factory: () -> T): T =
    getOrCreate(key = key) {
        TeaInstance(factory())
    }.tea

/**
 * Получает или создаёт [Tea] экземпляр с автоматическим ключом на основе типа.
 *
 * Упрощённая версия [getTea] без явного указания ключа.
 *
 * ## Использование
 * ```kotlin
 * private val tea = instanceKeeper.getTea {
 *     teaFactory.featureTea(...)
 * }
 * ```
 *
 * @param T тип Tea (используется как ключ)
 * @param factory фабрика для создания Tea
 * @return существующий или новый Tea экземпляр
 */
inline fun <reified T : Tea<*, *, *>> InstanceKeeper.getTea(noinline factory: () -> T): T =
    getTea(key = typeOf<T>(), factory = factory)

/**
 * Обёртка для хранения Tea в InstanceKeeper с автоматическим освобождением.
 */
private class TeaInstance<out T : Tea<*, *, *>>(
    val tea: T
) : InstanceKeeper.Instance {
    override fun onDestroy() {
        tea.dispose()
    }
}