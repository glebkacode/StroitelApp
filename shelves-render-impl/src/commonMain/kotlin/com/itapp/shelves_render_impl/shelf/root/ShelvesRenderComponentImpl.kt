package com.itapp.shelves_render_impl.shelf.root

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
import com.itapp.core_navigation.childLists.navigate
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent.Child
import com.itapp.shelves_render_api.shelf.grid.GridShelfComponent
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.shelves_render_api.shelf.video.VideoShelfComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.serialization.Serializable

@AssistedInject
class ShelvesRenderComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val horizontalShelfComponentFactory: HorizontalShelfComponent.Factory,
    private val gridShelfComponentFactory: GridShelfComponent.Factory,
    private val videoShelfComponentFactory: VideoShelfComponent.Factory,
) : BaseComponent(componentContext), ShelvesRenderComponent {

    private val shelvesNavigation = LazyListNavigation<Config>()

    override val shelves: Value<ChildLazyLists<*, Child>> =
        childLazyLists(
            source = shelvesNavigation,
            serializer = Config.serializer(),
            initialLazyLists = {
                LazyLists(items = emptyList())
            }
        ) { config, componentContext ->
            when (config) {
                is Config.Grid -> Child.Grid(gridShelfComponent(componentContext, config.index))
                is Config.Horizontal -> Child.Horizontal(horizontalShelfComponent(componentContext, config.index))
                is Config.Video -> Child.Video(videoShelfComponent(componentContext, config.index))
            }
        }

    override fun apply(shelves: List<ShelvesRenderComponent.ShelfModel>) {
        val configs = shelves.map { shelf ->
            when (shelf) {
                is ShelvesRenderComponent.ShelfModel.Grid -> Config.Grid(shelf.index)
                is ShelvesRenderComponent.ShelfModel.Horizontal -> Config.Horizontal(shelf.index)
                is ShelvesRenderComponent.ShelfModel.Video -> Config.Video(shelf.index)
            }
        }
        shelvesNavigation.navigate { LazyLists(configs) }
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
        ShelvesRender(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : ShelvesRenderComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext
        ): ShelvesRenderComponentImpl
    }
}