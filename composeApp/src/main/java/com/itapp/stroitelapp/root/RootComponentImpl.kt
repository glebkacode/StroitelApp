package com.itapp.stroitelapp.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.itapp.auth_api.root.RootAuthComponent
import com.itapp.core_navigation.BaseComponent
import com.itapp.stroitelapp.App
import kotlinx.serialization.Serializable

internal class RootComponentImpl(
    componentContext: ComponentContext,
    private val authComponentFactory: Lazy<RootAuthComponent.Factory>,
) : BaseComponent(componentContext), RootComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Auth,
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            Config.Auth -> RootComponent.Child.Auth(authComponent(componentContext))
        }

    private fun authComponent(componentContext: ComponentContext): RootAuthComponent =
        authComponentFactory.value(
            componentContext = componentContext,
            onAuthSuccess = {
                // TODO: Navigate to main screen after successful authentication
            },
        )

    @Composable
    override fun render(modifier: Modifier) {
        App(
            modifier = modifier,
            component = this,
        )
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Auth : Config
    }
}
