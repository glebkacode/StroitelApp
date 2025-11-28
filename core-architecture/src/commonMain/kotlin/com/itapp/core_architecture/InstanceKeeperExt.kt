package com.itapp.core_architecture

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.itapp.core_architecture.tea.Tea
import kotlin.reflect.typeOf

fun <T : Tea<*, *, *>> InstanceKeeper.getTea(key: Any, factory: () -> T): T =
    getOrCreate(key = key) {
        TeaInstance(factory())
    }.tea

inline fun <reified T : Tea<*, *, *>> InstanceKeeper.getTea(noinline factory: () -> T): T =
    getTea(key = typeOf<T>(), factory = factory)

private class TeaInstance<out T : Tea<*, *, *>>(
    val tea: T
) : InstanceKeeper.Instance {
    override fun onDestroy() {
        tea.dispose()
    }
}