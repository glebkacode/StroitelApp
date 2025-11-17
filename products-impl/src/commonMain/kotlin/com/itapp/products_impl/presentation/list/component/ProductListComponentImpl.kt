package com.itapp.products_impl.presentation.list.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.ProductListComponent
import com.itapp.products_impl.presentation.list.mvi.productsStore
import com.itapp.shelves_api.domain.usecase.GetShelvesUseCase
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent.ShelfModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@AssistedInject
class ProductListComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val shelvesRenderComponentFactory: ShelvesRenderComponent.Factory,
    private val storeFactory: StoreFactory,
    private val getShelvesUseCase: GetShelvesUseCase,
    @Assisted openProductDetails: (Long) -> Unit,
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
            shelvesRenderComponent.apply(
                shelves = listOf(
                    ShelfModel.Horizontal(index = 0),
                    ShelfModel.Grid(index = 1),
                    ShelfModel.Video(index = 2),

                    ShelfModel.Horizontal(index = 3),
                    ShelfModel.Grid(index = 4),
                    ShelfModel.Video(index = 5),

                    ShelfModel.Horizontal(index = 6),
                    ShelfModel.Grid(index = 7),
                    ShelfModel.Video(index = 8),

                    ShelfModel.Horizontal(index = 9),
                    ShelfModel.Grid(index = 10),
                    ShelfModel.Video(index = 11),

                    ShelfModel.Horizontal(index = 12),
                    ShelfModel.Grid(index = 13),
                    ShelfModel.Video(index = 14)
                )
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
            openProductDetails: (Long) -> Unit
        ): ProductListComponentImpl
    }
}