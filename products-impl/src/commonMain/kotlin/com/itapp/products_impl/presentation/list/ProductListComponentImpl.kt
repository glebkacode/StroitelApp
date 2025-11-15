package com.itapp.products_impl.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.itapp.core_navigation.BaseComponent
import com.itapp.core_navigation.childLists.ChildLazyLists
import com.itapp.core_navigation.childLists.LazyListNavigation
import com.itapp.core_navigation.childLists.LazyLists
import com.itapp.core_navigation.childLists.changeFirstVisibleElementIndex
import com.itapp.core_navigation.childLists.changeLastVisibleElementIndex
import com.itapp.core_navigation.childLists.childLazyLists
import com.itapp.products_api.ProductListComponent
import com.itapp.products_api.ProductListComponent.ChildShelf
import com.itapp.products_api.shelf.grid.GridShelfComponent
import com.itapp.products_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.products_api.shelf.video.VideoShelfComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.serialization.Serializable

@AssistedInject
class ProductListComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted openProductDetails: (Long) -> Unit,
    private val horizontalShelfComponentFactory: HorizontalShelfComponent.Factory,
    private val gridShelfComponentFactory: GridShelfComponent.Factory,
    private val videoShelfComponentFactory: VideoShelfComponent.Factory,
) : BaseComponent(componentContext), ProductListComponent {

    private val shelvesNavigation = LazyListNavigation<Config>()

    override val shelves: Value<ChildLazyLists<*, ChildShelf>> =
        childLazyLists(
            source = shelvesNavigation,
            serializer = Config.serializer(),
            initialLazyLists = {
                LazyLists(
                    items = listOf(
                        Config.Horizontal(index = 0),
                        Config.Grid(index = 1),
                        Config.Video(index = 2),

                        Config.Horizontal(index = 3),
                        Config.Grid(index = 4),
                        Config.Video(index = 5),

                        Config.Horizontal(index = 6),
                        Config.Grid(index = 7),
                        Config.Video(index = 8),

                        Config.Horizontal(index = 9),
                        Config.Grid(index = 10),
                        Config.Video(index = 11),

                        Config.Horizontal(index = 12),
                        Config.Grid(index = 13),
                        Config.Video(index = 14)
                    ),
                )
            }
        ) { config, componentContext ->
            when (config) {
                is Config.Grid -> ChildShelf.Grid(gridShelfComponent(componentContext, config.index))
                is Config.Horizontal -> ChildShelf.Horizontal(horizontalShelfComponent(componentContext, config.index))
                is Config.Video -> ChildShelf.Video(videoShelfComponent(componentContext, config.index))
            }
        }

    private fun horizontalShelfComponent(
        componentContext: ComponentContext,
        index: Int
    ): HorizontalShelfComponent = horizontalShelfComponentFactory(componentContext, index)

    private fun gridShelfComponent(
        componentContext: ComponentContext,
        index: Int
    ): GridShelfComponent = gridShelfComponentFactory(componentContext, index)

    private fun videoShelfComponent(
        componentContext: ComponentContext,
        index: Int
    ): VideoShelfComponent = videoShelfComponentFactory(componentContext, index)

    override fun onFirstVisibleElementChange(index: Int) {
        shelvesNavigation.changeFirstVisibleElementIndex(index)
    }

    override fun onLastVisibleElementChange(index: Int) {
        shelvesNavigation.changeLastVisibleElementIndex(index)
    }

    @Serializable
    private sealed interface Config {
        data class Horizontal(val index: Int) : Config
        data class Grid(val index: Int) : Config
        data class Video(val index: Int) : Config
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