package com.itapp.products_impl.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.ProductDetailsComponent
import com.itapp.products_api.ProductListComponent
import com.itapp.products_api.RootProductsComponent
import com.itapp.products_api.RootProductsComponent.Child
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.serialization.Serializable

@AssistedInject
class RootProductsComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val productListFactory: Lazy<ProductListComponent.Factory>,
    private val productDetailsFactory: Lazy<ProductDetailsComponent.Factory>,
) : BaseComponent(componentContext), RootProductsComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.ProductList, // The initial child component is List
            handleBackButton = true, // Automatically pop from the stack on back button presses
            childFactory = ::child,
        )

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.ProductList -> Child.ProductsList(productListComponent(componentContext))
            is Config.ProductDetails -> Child.ProductDetails(
                productDetailsComponent(
                    componentContext
                )
            )
        }

    private fun productListComponent(
        componentContext: ComponentContext
    ): ProductListComponent =
        productListFactory.value.invoke(
            componentContext = componentContext,
            openProductDetails = { id ->
                navigation.bringToFront(Config.ProductDetails(id))
            }
        )

    private fun productDetailsComponent(
        componentContext: ComponentContext
    ): ProductDetailsComponent = productDetailsFactory.value.invoke(componentContext)

    @Serializable
    private sealed interface Config {
        @Serializable
        data object ProductList : Config

        @Serializable
        data class ProductDetails(val id: Long) : Config
    }

    @Composable
    override fun render(modifier: Modifier) {
        RootProductsScreen(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : RootProductsComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
        ): RootProductsComponentImpl
    }
}