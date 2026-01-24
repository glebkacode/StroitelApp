package com.itapp.products_impl.presentation.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.details.ProductCharacteristicsComponent
import com.itapp.products_api.details.ProductDescriptionComponent
import com.itapp.products_api.details.ProductDetailsComponent
import com.itapp.products_api.details.ProductDetailsComponent.Child
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.serialization.Serializable

@AssistedInject
class ProductDetailsComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted("shelfIf") private val shelfId: Long,
    @Assisted("shelfItemId") private val shelfItemId: Long,
    private val productDescriptionComponentFactory: Lazy<ProductDescriptionComponent.Factory>,
    private val productCharacteristicsComponentFactory: Lazy<ProductCharacteristicsComponent.Factory>,
) : BaseComponent(componentContext), ProductDetailsComponent {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Description,
            childFactory = ::child,
        )

    override fun onDescriptionTabClicked() {
        navigation.bringToFront(Config.Description)
    }

    override fun onCharacteristicsTabClicked() {
        navigation.bringToFront(Config.Characteristics)
    }

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            Config.Characteristics -> Child.Characteristics(
                characteristicsComponent(componentContext)
            )
            Config.Description -> Child.Description(descriptionComponent(componentContext))
        }

    private fun descriptionComponent(componentContext: ComponentContext): ProductDescriptionComponent {
        return productDescriptionComponentFactory.value(componentContext)
    }

    private fun characteristicsComponent(componentContext: ComponentContext): ProductCharacteristicsComponent {
        return productCharacteristicsComponentFactory.value(componentContext)
    }

    @Composable
    override fun render(modifier: Modifier) {
        ProductDetailsScreen(
            modifier = modifier,
            component = this
        )
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Description : Config
        @Serializable
        data object Characteristics : Config
    }

    @AssistedFactory
    interface Factory : ProductDetailsComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            @Assisted("shelfIf") shelfId: Long,
            @Assisted("shelfItemId") shelfItemId: Long
        ): ProductDetailsComponentImpl
    }
}