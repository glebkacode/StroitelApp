package com.itapp.products_impl.presentation.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.ProductDetailsComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class ProductDetailsComponentImpl(
    @Assisted componentContext: ComponentContext
) : BaseComponent(componentContext), ProductDetailsComponent {

    @Composable
    override fun render(modifier: Modifier) {
        ProductDetailsScreen(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : ProductDetailsComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext
        ): ProductDetailsComponentImpl
    }
}