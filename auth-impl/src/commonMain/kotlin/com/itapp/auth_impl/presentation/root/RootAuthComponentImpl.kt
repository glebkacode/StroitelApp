package com.itapp.auth_impl.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.auth_api.root.RootAuthComponent
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.serialization.Serializable

@AssistedInject
class RootAuthComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onAuthSuccess: () -> Unit,
    private val phoneComponentFactory: Lazy<PhoneValidationComponent.Factory>,
    private val registrationComponentFactory: Lazy<RegistrationComponent.Factory>,
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
            Config.Registration -> RootAuthComponent.Child.RegistrationChild(
                registrationComponent(componentContext)
            )
            Config.SmsConfirmation -> RootAuthComponent.Child.SmsConfirmationChild(
                onConfirmed = onAuthSuccess,
                onBack = { navigation.pop() },
            )
        }

    private fun phoneValidationComponent(
        componentContext: ComponentContext
    ): PhoneValidationComponent =
        phoneComponentFactory.value(
            componentContext = componentContext,
            callbacks = PhoneValidationComponent.Callbacks(
                onAuthSuccess = { navigation.pushNew(Config.SmsConfirmation) },
                onRegisterClicked = { navigation.pushNew(Config.Registration) },
            ),
        )

    private fun registrationComponent(
        componentContext: ComponentContext,
    ): RegistrationComponent =
        registrationComponentFactory.value(
            componentContext = componentContext,
            callbacks = RegistrationComponent.Callbacks(
                onRegistrationSuccess = { navigation.pop() },
                onBack = { navigation.pop() },
            ),
        )

    @Serializable
    private sealed interface Config {
        @Serializable
        data object PhoneValidation : Config

        @Serializable
        data object Registration : Config

        @Serializable
        data object SmsConfirmation : Config
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
