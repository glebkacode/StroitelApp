package com.itapp.shelves_render_api.shelf.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.itapp.core_navigation.UiComponent
import com.itapp.core_navigation.childLists.ChildLazyLists
import com.itapp.shelves_render_api.shelf.grid.GridShelfComponent
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.shelves_render_api.shelf.video.VideoShelfComponent

interface ShelvesRenderComponent : UiComponent {
    val shelves: Value<ChildLazyLists<*, ChildShelf>>
    fun apply(shelves: List<ShelfModel>)
    fun onFirstVisibleElementChange(index: Int)
    fun onLastVisibleElementChange(index: Int)
    sealed interface ChildShelf {
        class Horizontal(val component: HorizontalShelfComponent) : ChildShelf
        class Grid(val component: GridShelfComponent) : ChildShelf
        class Video(val component: VideoShelfComponent) : ChildShelf
    }
    sealed interface ShelfModel {
        data class Horizontal(val index: Int) : ShelfModel
        data class Grid(val index: Int) : ShelfModel
        data class Video(val index: Int) : ShelfModel
    }
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): ShelvesRenderComponent
    }
}