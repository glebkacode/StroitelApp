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
import com.itapp.products_api.ProductListComponent.Model
import com.itapp.products_impl.presentation.list.mapping.toModel
import com.itapp.products_impl.presentation.list.mvi.ProductsStore
import com.itapp.products_impl.presentation.list.mvi.ProductsStore.Intent
import com.itapp.products_impl.presentation.list.mvi.productsStore
import com.itapp.shelves_api.domain.usecase.GetShelvesUseCase
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AssistedInject
class ProductListComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val shelvesRenderComponentFactory: ShelvesRenderComponent.Factory,
    private val storeFactory: StoreFactory,
    private val getShelvesUseCase: GetShelvesUseCase,
    @Assisted private val openProductDetails: (Long, Long) -> Unit,
) : BaseComponent(componentContext), ProductListComponent {

    private val _model: MutableStateFlow<Model> = MutableStateFlow(Model.Loading)
    override val model: StateFlow<Model> = _model

    private val store = instanceKeeper.getStore {
        storeFactory.productsStore(
            getShelvesUseCase = getShelvesUseCase,
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    override val shelvesRenderComponent: ShelvesRenderComponent by lazy {
        shelvesRenderComponentFactory(
            componentContext = childContext("shelves_render"),
            onItemClicked = { shelfId, shelfItemId ->
                store.accept(
                    Intent.ShelfItemClicked(
                        shelfId = shelfId,
                        shelfItemId = shelfItemId
                    )
                )
            },
            onShelfVisible = { shelfItemId ->
                println("Shelf visible with id = $shelfItemId")
            },
            onShelfItemVisible = { shelfItemId ->
                println("ShelfItem visible with id = $shelfItemId")
            }
        )
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
                componentScope.launch {
                    shelvesRenderComponent.apply(
                        models = state.shelves.map { it.toModel() }
                    )
                    _model.emit(Model.Content)
                }
            }

            is ProductsStore.State.Error -> {
                componentScope.launch {
                    _model.emit(Model.Error(state.throwable))
                }
            }

            ProductsStore.State.Loading -> {
                componentScope.launch {
                    _model.emit(Model.Loading)
                }
            }
        }
    }

    private fun handleLabels(label: ProductsStore.Label) {
        when (label) {
            is ProductsStore.Label.OpenProductDetails -> openProductDetails(
                label.shelfId,
                label.shelfItemId
            )
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
            openProductDetails: (Long, Long) -> Unit
        ): ProductListComponentImpl
    }
}