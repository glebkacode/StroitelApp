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
import com.itapp.auth_api.root.RootAuthComponent
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
    private val passwordComponentFactory: Lazy<PasswordValidationComponent.Factory>,
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
            Config.PasswordValidation -> RootAuthComponent.Child.PasswordValidationChild(
                passwordValidationComponent(componentContext)
            )
        }

    private fun phoneValidationComponent(
        componentContext: ComponentContext
    ): PhoneValidationComponent =
        phoneComponentFactory.value(
            componentContext = componentContext,
            callbacks = PhoneValidationComponent.Callbacks(
                onAuthSuccess = { navigation.push(Config.PasswordValidation) },
            ),
        )

    private fun passwordValidationComponent(
        componentContext: ComponentContext
    ): PasswordValidationComponent =
        passwordComponentFactory.value(
            componentContext = componentContext,
            callbacks = PasswordValidationComponent.Callbacks(
                onAuthSuccess = onAuthSuccess,
                onBack = { navigation.pop() },
            ),
        )

    @Serializable
    private sealed interface Config {
        @Serializable
        data object PhoneValidation : Config
        @Serializable
        data object PasswordValidation : Config
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
