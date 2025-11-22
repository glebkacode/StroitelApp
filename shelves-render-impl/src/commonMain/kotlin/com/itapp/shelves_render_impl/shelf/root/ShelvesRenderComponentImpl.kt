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
import com.itapp.shelves_render_api.shelf.grid.GridShelfModel
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfModel
import com.itapp.shelves_render_api.shelf.root.model.shelf.ShelfModel
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
    @Assisted private val onItemClicked: (Long, Long) -> Unit
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
                is Config.Grid -> Child.Grid(gridShelfComponent(componentContext, config))
                is Config.Horizontal -> Child.Horizontal(horizontalShelfComponent(componentContext, config))
            }
        }

    override fun apply(models: List<ShelfModel>) {
        val configs = models.map { shelve ->
            when(shelve) {
                is GridShelfModel -> Config.Grid(model = shelve)
                is HorizontalShelfModel -> Config.Horizontal(model = shelve)
                else -> throw IllegalArgumentException("Unknown shelf model type")
            }
        }
        shelvesNavigation.navigate { LazyLists(configs) }
    }

    private fun horizontalShelfComponent(
        componentContext: ComponentContext,
        config: Config.Horizontal
    ): HorizontalShelfComponent = horizontalShelfComponentFactory(
        componentContext = componentContext,
        model = config.model,
        onItemClicked = { shelfId, shelfItemId -> onItemClicked(shelfId, shelfItemId) }
    )

    private fun gridShelfComponent(
        componentContext: ComponentContext,
        config: Config.Grid
    ): GridShelfComponent = gridShelfComponentFactory(componentContext, config.model)

    override fun onFirstVisibleElementChange(index: Int) {
        shelvesNavigation.changeFirstVisibleElementIndex(index)
    }

    override fun onLastVisibleElementChange(index: Int) {
        shelvesNavigation.changeLastVisibleElementIndex(index)
    }

    @Serializable
    private sealed interface Config {
        data class Horizontal(
            val model: HorizontalShelfModel
        ) : Config
        data class Grid(
            val model: GridShelfModel
        ) : Config
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
            componentContext: ComponentContext,
            onItemClicked: (Long, Long) -> Unit
        ): ShelvesRenderComponentImpl
    }
}