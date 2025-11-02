package com.itapp.auth_impl.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_api.root.AuthRootComponent
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.auth_impl.password_validation.PasswordValidationComponentImpl
import com.itapp.auth_impl.phone_validation.PhoneValidationComponentImpl
import com.itapp.auth_impl.sms_validation.SmsValidationComponentImpl
import com.itapp.core_navigation.BaseComponent
import kotlinx.serialization.Serializable

class AuthRootComponentImpl(
    componentContext: ComponentContext
) : BaseComponent(componentContext), AuthRootComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, AuthRootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.PhoneValidation, // The initial child component is List
            handleBackButton = true, // Automatically pop from the stack on back button presses
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): AuthRootComponent.Child =
        when (config) {
            Config.PasswordValidation -> AuthRootComponent.Child.PasswordValidationChild(
                passwordValidationComponent(
                    componentContext
                )
            )
            Config.PhoneValidation -> AuthRootComponent.Child.PhoneValidationChild(
                phoneValidationComponent(
                    componentContext
                )
            )
            Config.SmsValidation -> AuthRootComponent.Child.SmsValidationChild(
                smsValidationComponent(
                    componentContext
                )
            )
        }

    private fun passwordValidationComponent(
        componentContext: ComponentContext
    ): PasswordValidationComponent =
        PasswordValidationComponentImpl(
            componentContext = componentContext,
            openSmsScreen = { navigation.bringToFront(Config.SmsValidation) }
        )

    private fun phoneValidationComponent(
        componentContext: ComponentContext
    ): PhoneValidationComponent =
        PhoneValidationComponentImpl(
            componentContext = componentContext,
            openPasswordScreen = { navigation.bringToFront(Config.PasswordValidation) }
        )

    private fun smsValidationComponent(
        componentContext: ComponentContext
    ): SmsValidationComponent =
        SmsValidationComponentImpl(componentContext = componentContext)

    @Serializable
    private sealed interface Config {
        @Serializable
        object PhoneValidation : Config

        @Serializable
        object PasswordValidation : Config

        @Serializable
        object SmsValidation : Config
    }

    @Composable
    override fun render(modifier: Modifier) {
        AuthScreen(modifier, this)
    }
}