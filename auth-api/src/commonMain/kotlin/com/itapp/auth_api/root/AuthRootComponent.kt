package com.itapp.auth_api.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.core_navigation.BaseUiComponent

interface AuthRootComponent : BaseUiComponent {
    val stack: Value<ChildStack<*, Child>>
    sealed interface Child {
        class PasswordValidationChild(val component: PasswordValidationComponent) : Child
        class PhoneValidationChild(val component: PhoneValidationComponent) : Child
        class SmsValidationChild(val component: SmsValidationComponent) : Child
    }
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openProducts: Lazy<() -> Unit>
        ): AuthRootComponent
    }
}