package com.itapp.auth_impl.presentation.root

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
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.serialization.Serializable

@AssistedInject
class AuthRootComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val passwordComponentFactory: Lazy<PasswordValidationComponent.Factory>,
    private val phoneComponentFactory: Lazy<PhoneValidationComponent.Factory>,
    private val smsComponentFactory: Lazy<SmsValidationComponent.Factory>,
    private val openProducts: Lazy<() -> Unit>
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
            is Config.PasswordValidation -> AuthRootComponent.Child.PasswordValidationChild(
                passwordValidationComponent(
                    componentContext = componentContext,
                    phone = config.phone
                )
            )

            Config.PhoneValidation -> AuthRootComponent.Child.PhoneValidationChild(
                phoneValidationComponent(
                    componentContext
                )
            )

            is Config.SmsValidation -> AuthRootComponent.Child.SmsValidationChild(
                smsValidationComponent(
                    componentContext,
                    phone = config.phone,
                    password = config.password
                )
            )
        }

    private fun passwordValidationComponent(
        componentContext: ComponentContext,
        phone: String
    ): PasswordValidationComponent =
        passwordComponentFactory.value(
            componentContext = componentContext,
            phone = phone,
            openSmsScreen = { phone, password ->
                navigation.bringToFront(
                    Config.SmsValidation(
                        phone = phone,
                        password = password
                    )
                )
            }
        )

    private fun phoneValidationComponent(
        componentContext: ComponentContext
    ): PhoneValidationComponent =
        phoneComponentFactory.value(
            componentContext = componentContext,
            openPasswordScreen = { phone ->
                navigation.bringToFront(Config.PasswordValidation(phone))
            }
        )

    private fun smsValidationComponent(
        componentContext: ComponentContext,
        phone: String,
        password: String
    ): SmsValidationComponent =
        smsComponentFactory.value(
            componentContext = componentContext,
            phone = phone,
            password = password,
            openProducts = { openProducts.value() }
        )

    @Serializable
    private sealed interface Config {
        @Serializable
        data object PhoneValidation : Config

        @Serializable
        data class PasswordValidation(val phone: String) : Config

        @Serializable
        data class SmsValidation(
            val phone: String,
            val password: String
        ) : Config
    }

    @Composable
    override fun render(modifier: Modifier) {
        AuthScreen(modifier, this)
    }

    @AssistedFactory
    interface Factory : AuthRootComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            openProducts: Lazy<() -> Unit>
        ): AuthRootComponentImpl
    }
}