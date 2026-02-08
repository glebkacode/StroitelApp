package com.itapp.auth_impl.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.auth_api.root.RootAuthComponent
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.serialization.Serializable

@OptIn(DelicateDecomposeApi::class)
@AssistedInject
class RootAuthComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onAuthSuccess: () -> Unit,
    private val phoneComponentFactory: Lazy<PhoneValidationComponent.Factory>,
    private val smsComponentFactory: Lazy<SmsValidationComponent.Factory>,
    private val passwordComponentFactory: Lazy<PasswordValidationComponent.Factory>,
    private val registrationComponentFactory: Lazy<RegistrationComponent.Factory>
) : BaseComponent(componentContext), RootAuthComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootAuthComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.PhoneValidation,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): RootAuthComponent.Child =
        when (config) {
            Config.PhoneValidation -> RootAuthComponent.Child.PhoneValidationChild(
                phoneValidationComponent(componentContext)
            )
            is Config.SmsValidation -> RootAuthComponent.Child.SmsValidationChild(
                smsValidationComponent(componentContext, config.phoneNumber)
            )
            is Config.PasswordValidation -> RootAuthComponent.Child.PasswordValidationChild(
                passwordValidationComponent(componentContext, config.phoneNumber)
            )
            Config.Registration -> RootAuthComponent.Child.RegistrationChild(
                registrationComponent(componentContext)
            )
        }

    private fun phoneValidationComponent(
        componentContext: ComponentContext
    ): PhoneValidationComponent =
        phoneComponentFactory.value(
            componentContext = componentContext,
            onNavigateToPassword = { phoneNumber ->
                navigation.push(Config.SmsValidation(phoneNumber))
            },
            onNavigateToRegistration = {
                navigation.push(Config.Registration)
            }
        )

    private fun smsValidationComponent(
        componentContext: ComponentContext,
        phoneNumber: String
    ): SmsValidationComponent =
        smsComponentFactory.value(
            componentContext = componentContext,
            phoneNumber = phoneNumber,
            onSmsValidated = {
                navigation.push(Config.PasswordValidation(phoneNumber))
            },
            onBack = { navigation.pop() }
        )

    private fun passwordValidationComponent(
        componentContext: ComponentContext,
        phoneNumber: String
    ): PasswordValidationComponent =
        passwordComponentFactory.value(
            componentContext = componentContext,
            phoneNumber = phoneNumber,
            onAuthSuccess = onAuthSuccess,
            onBack = { navigation.pop() }
        )

    private fun registrationComponent(
        componentContext: ComponentContext
    ): RegistrationComponent =
        registrationComponentFactory.value(
            componentContext = componentContext,
            onRegistrationSuccess = onAuthSuccess,
            onBack = { navigation.pop() }
        )

    @Serializable
    private sealed interface Config {
        @Serializable
        data object PhoneValidation : Config

        @Serializable
        data class SmsValidation(val phoneNumber: String) : Config

        @Serializable
        data class PasswordValidation(val phoneNumber: String) : Config

        @Serializable
        data object Registration : Config
    }

    @Composable
    override fun render(modifier: Modifier) {
        AuthScreen(modifier, this)
    }

    @AssistedFactory
    interface Factory : RootAuthComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            onAuthSuccess: () -> Unit
        ): RootAuthComponentImpl
    }
}
