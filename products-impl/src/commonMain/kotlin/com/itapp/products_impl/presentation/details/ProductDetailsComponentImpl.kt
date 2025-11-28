package com.itapp.products_impl.presentation.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.core_architecture.getTea
import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.ProductDetailsComponent
import com.itapp.products_impl.presentation.details.ProductDetailsTea.Intent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AssistedInject
class ProductDetailsComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted("shelfIf") private val shelfId: Long,
    @Assisted("shelfItemId") private val shelfItemId: Long,
    private val teaFactory: TeaFactory,
) : BaseComponent(componentContext), ProductDetailsComponent {

    private val tea = instanceKeeper.getTea {
        teaFactory.productDetailsTea(
            mainCoroutineContext = Dispatchers.Main
        )
    }

    override val models = tea.state.map { state ->
        ProductDetailsComponent.Model(state.text)
    }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = ProductDetailsComponent.Model()
    )

    override fun onTextChanged(text: String) {
        tea.accept(Intent.UiIntent.TextChanged(text))
    }

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
            componentContext: ComponentContext,
            @Assisted("shelfIf") shelfId: Long,
            @Assisted("shelfItemId") shelfItemId: Long
        ): ProductDetailsComponentImpl
    }
}