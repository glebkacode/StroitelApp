package com.itapp.shelves_render_api.shelf.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.itapp.core_navigation.UiComponent
import com.itapp.core_navigation.childLists.ChildLazyLists
import com.itapp.shelves_render_api.shelf.grid.GridShelfComponent
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.shelves_render_api.shelf.root.model.shelf.ShelfModel
import com.itapp.shelves_render_api.shelf.video.VideoShelfComponent

interface ShelvesRenderComponent : UiComponent {
    val shelves: Value<ChildLazyLists<*, Child>>
    fun apply(models: List<ShelfModel>)
    fun onFirstVisibleElementChange(index: Int)
    fun onLastVisibleElementChange(index: Int)
    sealed interface Child {
        class Horizontal(val component: HorizontalShelfComponent) : Child
        class Grid(val component: GridShelfComponent) : Child
        class Video(val component: VideoShelfComponent) : Child
    }

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onItemClicked: (Long, Long) -> Unit
        ): ShelvesRenderComponent
    }
}