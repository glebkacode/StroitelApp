package com.itapp.stroitelapp.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.itapp.auth_api.root.AuthRootComponent
import com.itapp.core_navigation.BaseUiComponent

interface RootComponent : BaseUiComponent {
    val stack: Value<ChildStack<*, RootComponent.Child>>
    sealed interface Child {
        class Auth(val component: AuthRootComponent) : Child
    }
}