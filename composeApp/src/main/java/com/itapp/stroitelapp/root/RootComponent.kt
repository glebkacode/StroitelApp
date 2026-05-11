package com.itapp.stroitelapp.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.itapp.auth_api.root.RootAuthComponent
import com.itapp.core_navigation.UiComponent

interface RootComponent : UiComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        class Auth(val component: RootAuthComponent) : Child
    }
}
