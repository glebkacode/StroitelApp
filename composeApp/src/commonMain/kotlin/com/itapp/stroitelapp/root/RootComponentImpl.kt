package com.itapp.stroitelapp.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.itapp.auth_api.root.RootAuthComponent
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.RootProductsComponent
import com.itapp.stroitelapp.App
import com.itapp.stroitelapp.root.RootComponent.Child.*
import kotlinx.serialization.Serializable

internal class RootComponentImpl(
    componentContext: ComponentContext,
    private val authComponentFactory: Lazy<RootAuthComponent.Factory>,
    private val productsComponentFactory: Lazy<RootProductsComponent.Factory>
) : BaseComponent(componentContext), RootComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Auth, // The initial child component is List
            handleBackButton = true, // Automatically pop from the stack on back button presses
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            Config.Auth -> Auth(authComponent(componentContext))
            Config.Products -> Products(productsComponent(componentContext))
        }

    private fun authComponent(
        componentContext: ComponentContext
    ): RootAuthComponent = authComponentFactory.value(
        componentContext = componentContext,
        openProducts = lazy { { navigation.bringToFront(Config.Products) } }
    )

    private fun productsComponent(
        componentContext: ComponentContext
    ): RootProductsComponent = productsComponentFactory.value(
        componentContext = componentContext
    )

    @Composable
    override fun render(modifier: Modifier) {
        App(
            modifier = modifier,
            component = this
        )
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Auth : Config
        @Serializable
        data object Products : Config
    }
}