package com.itapp.shelves_render_impl.shelf.grid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.itapp.core_navigation.BaseComponent
import com.itapp.shelves_render_api.shelf.grid.GridShelfComponent
import com.itapp.shelves_render_api.shelf.grid.GridShelfModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class GridShelfComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted override val model: GridShelfModel
) : BaseComponent(componentContext), GridShelfComponent {

    init {
        println("GridShelfComponent index = ${model.id} Created ${hashCode()}")
        lifecycle.subscribe(
            onStart = { println("GridShelfComponent index = ${model.id} Start ${hashCode()}") },
            onStop = { println("GridShelfComponent index = ${model.id} Stop ${hashCode()}") }
        )
    }

    @Composable
    override fun render(modifier: Modifier) {
        GridShelf(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : GridShelfComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            model: GridShelfModel
        ): GridShelfComponentImpl
    }
}