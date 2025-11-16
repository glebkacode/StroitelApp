package com.itapp.shelves_render_impl.shelf.grid

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.itapp.core_navigation.BaseComponent
import com.itapp.shelves_render_api.shelf.grid.GridShelfComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class GridShelfComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted override val index: Int
) : BaseComponent(componentContext), GridShelfComponent {

    init {
        println("GridShelfComponent index = $index Created ${hashCode()}")
        lifecycle.subscribe(
            onStart = { println("GridShelfComponent index = $index Start ${hashCode()}") },
            onStop = { println("GridShelfComponent index = $index Stop ${hashCode()}") }
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
            index: Int
        ): GridShelfComponentImpl
    }
}