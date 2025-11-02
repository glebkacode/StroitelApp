package com.itapp.core_navigation

import com.arkivanov.decompose.ComponentContext

abstract class BaseComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    protected val componentScope = coroutineScope()
}