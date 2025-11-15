package com.itapp.products_api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.itapp.core_navigation.UiComponent
import com.itapp.core_navigation.childLists.ChildLazyLists
import com.itapp.products_api.shelf.grid.GridShelfComponent
import com.itapp.products_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.products_api.shelf.video.VideoShelfComponent

interface ProductListComponent : UiComponent {
    val shelves: Value<ChildLazyLists<*, ChildShelf>>
    fun onFirstVisibleElementChange(index: Int)
    fun onLastVisibleElementChange(index: Int)
    sealed interface ChildShelf {
        class Horizontal(val component: HorizontalShelfComponent) : ChildShelf
        class Grid(val component: GridShelfComponent) : ChildShelf
        class Video(val component: VideoShelfComponent) : ChildShelf
    }
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openProductDetails: (Long) -> Unit
        ): ProductListComponent
    }
}