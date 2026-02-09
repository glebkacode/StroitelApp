package com.itapp.auth_api.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.core_navigation.UiComponent

interface RootAuthComponent : UiComponent {
    val stack: Value<ChildStack<*, Child>>
    sealed interface Child {
        class PhoneValidationChild(val component: PhoneValidationComponent) : Child
        class PasswordValidationChild(val component: PasswordValidationComponent) : Child
    }
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onAuthSuccess: () -> Unit
        ): RootAuthComponent
    }
}
