package com.itapp.auth_impl

import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.root.AuthRootComponent

internal class AuthRootComponentImpl(
    componentContext: ComponentContext
) : AuthRootComponent, ComponentContext by componentContext {

}