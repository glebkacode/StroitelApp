package com.itapp.products_impl.presentation.details.characteristics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.details.ProductCharacteristicsComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class ProductCharacteristicsComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val teaFactory: TeaFactory,
) : BaseComponent(componentContext), ProductCharacteristicsComponent {

    @Composable
    override fun render(modifier: Modifier) {
        ProductCharacteristicsScreen(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : ProductCharacteristicsComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext
        ): ProductCharacteristicsComponentImpl
    }
}