package com.itapp.products_impl.presentation.list.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.ProductListComponent
import com.itapp.products_impl.presentation.list.mapping.toModel
import com.itapp.products_impl.presentation.list.mvi.ProductsStore
import com.itapp.products_impl.presentation.list.mvi.productsStore
import com.itapp.shelves_api.domain.usecase.GetShelvesUseCase
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AssistedInject
class ProductListComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val shelvesRenderComponentFactory: ShelvesRenderComponent.Factory,
    private val storeFactory: StoreFactory,
    private val getShelvesUseCase: GetShelvesUseCase,
    @Assisted private val openProductDetails: (Long) -> Unit,
) : BaseComponent(componentContext), ProductListComponent {

    private val store = instanceKeeper.getStore {
        storeFactory.productsStore(
            getShelvesUseCase = getShelvesUseCase,
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    override val shelvesRenderComponent: ShelvesRenderComponent by lazy {
        shelvesRenderComponentFactory(childContext("shelves_render"))
    }

    init {
        lifecycle.doOnStart {
            store.stateFlow.onEach(::handleState).launchIn(componentScope)
            store.labels.onEach(::handleLabels).launchIn(componentScope)
        }
    }

    private fun handleState(state: ProductsStore.State) {
        when (state) {
            is ProductsStore.State.Data -> {
                shelvesRenderComponent.apply(
                    models = state.shelves.map { it.toModel() }
                )
            }
            is ProductsStore.State.Error -> TODO()
            ProductsStore.State.Loading -> TODO()
        }
    }

    private fun handleLabels(label: ProductsStore.Label) {
        when (label) {
            is ProductsStore.Label.OpenProductDetails -> openProductDetails(label.id)
        }
    }

    @Composable
    override fun render(modifier: Modifier) {
        ProductListScreen(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : ProductListComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            openProductDetails: (Long) -> Unit
        ): ProductListComponentImpl
    }
}