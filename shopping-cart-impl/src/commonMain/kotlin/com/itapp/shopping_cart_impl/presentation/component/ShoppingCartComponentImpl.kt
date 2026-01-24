package com.itapp.shopping_cart_impl.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.shopping_cart_api.ShoppingCartComponent
import com.itapp.shopping_cart_impl.presentation.mapping.toUi
import com.itapp.shopping_cart_impl.presentation.mvi.ShoppingCartTea
import com.itapp.shopping_cart_impl.presentation.mvi.shoppingCartTea
import com.itapp.core_architecture.getTea
import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AssistedInject
class ShoppingCartComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val teaFactory: TeaFactory,
) : BaseComponent(componentContext), ShoppingCartComponent {

    private val store = instanceKeeper.getTea {
        teaFactory.shoppingCartTea(
            mainContext = Dispatchers.Main,
        )
    }

    override val uiState = store.state.map { it.toUi() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = ShoppingCartComponent.UiState()
    )

    @Composable
    override fun render(modifier: Modifier) {
        ShoppingCartScreen(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : ShoppingCartComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
        ): ShoppingCartComponentImpl
    }
}
