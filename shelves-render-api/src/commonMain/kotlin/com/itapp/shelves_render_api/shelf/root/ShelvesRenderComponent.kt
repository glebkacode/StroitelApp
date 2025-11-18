package com.itapp.shelves_render_api.shelf.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.itapp.core_navigation.UiComponent
import com.itapp.core_navigation.childLists.ChildLazyLists
import com.itapp.shelves_render_api.shelf.grid.GridShelfComponent
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.shelves_render_api.shelf.video.VideoShelfComponent

interface ShelvesRenderComponent : UiComponent {
    val model: Value<ChildLazyLists<*, Child>>
    fun apply(models: List<Model>)
    fun onFirstVisibleElementChange(index: Int)
    fun onLastVisibleElementChange(index: Int)
    sealed interface Child {
        class Horizontal(val component: HorizontalShelfComponent) : Child
        class Grid(val component: GridShelfComponent) : Child
        class Video(val component: VideoShelfComponent) : Child
    }
    sealed interface Model {
        val id: Long
        val header: String
        val items: List<ModelItem>

        data class Default(
            override val id: Long,
            override val header: String,
            override val items: List<ModelItem>
        ) : Model

        data class Catalog(
            override val id: Long,
            override val header: String,
            override val items: List<ModelItem>,
            val filterOptions: List<FilterOption>
        ) : Model
    }

    data class FilterOption(
        val id: Long,
        val option: String
    )

    sealed interface ModelItem {
        val id: Long

        data class Default(
            override val id: Long,
            val title: String,
            val description: String,
            val url: String,
            val labelInfo: String,
            val price: Double
        ) : ModelItem
    }

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): ShelvesRenderComponent
    }
}