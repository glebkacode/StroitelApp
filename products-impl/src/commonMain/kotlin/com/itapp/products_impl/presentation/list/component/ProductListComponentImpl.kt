package com.itapp.products_impl.presentation.list.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnStart
import com.itapp.core_architecture.getTea
import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.ProductListComponent
import com.itapp.products_api.ProductListComponent.Model
import com.itapp.products_impl.presentation.list.mapping.toModel
import com.itapp.products_impl.presentation.list.mvi.ProductsTea
import com.itapp.products_impl.presentation.list.mvi.ProductsTea.Intent.UiIntent
import com.itapp.products_impl.presentation.list.mvi.productsTea
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
    private val teaFactory: TeaFactory,
    private val getShelvesUseCase: GetShelvesUseCase,
    @Assisted private val openProductDetails: (Long, Long) -> Unit,
) : BaseComponent(componentContext), ProductListComponent {

    private val _model: MutableStateFlow<Model> = MutableStateFlow(Model.Loading)
    override val model: StateFlow<Model> = _model

    private val tea = instanceKeeper.getTea {
        teaFactory.productsTea(
            getShelvesUseCase = getShelvesUseCase,
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    override val shelvesRenderComponent: ShelvesRenderComponent by lazy {
        shelvesRenderComponentFactory(
            componentContext = childContext("shelves_render"),
            onItemClicked = { shelfId, shelfItemId ->
                tea.accept(
                    UiIntent.ShelfItemClicked(
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
            tea.state.onEach(::handleState).launchIn(componentScope)
            tea.events.onEach(::handleEvents).launchIn(componentScope)
        }
    }

    private fun handleState(state: ProductsTea.State) {
        when (state) {
            is ProductsTea.State.Data -> {
                componentScope.launch {
                    shelvesRenderComponent.apply(
                        models = state.shelves.map { it.toModel() }
                    )
                    _model.emit(Model.Content)
                }
            }

            is ProductsTea.State.Error -> {
                componentScope.launch {
                    _model.emit(Model.Error(state.throwable))
                }
            }

            ProductsTea.State.Loading -> {
                componentScope.launch {
                    _model.emit(Model.Loading)
                }
            }
        }
    }

    private fun handleEvents(label: ProductsTea.Event) {
        when (label) {
            is ProductsTea.Event.OpenProductDetails -> openProductDetails(
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