package com.itapp.products_impl.presentation.details.description

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.details.ProductDescriptionComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class ProductDescriptionComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val teaFactory: TeaFactory,
) : BaseComponent(componentContext), ProductDescriptionComponent {

    @Composable
    override fun render(modifier: Modifier) {
        ProductDescriptionScreen(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : ProductDescriptionComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext
        ): ProductDescriptionComponentImpl
    }
}