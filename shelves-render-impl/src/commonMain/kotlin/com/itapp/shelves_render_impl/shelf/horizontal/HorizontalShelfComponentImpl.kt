package com.itapp.shelves_render_impl.shelf.horizontal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.itapp.core_navigation.BaseComponent
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class HorizontalShelfComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted override val index: Int
) : BaseComponent(componentContext), HorizontalShelfComponent {

    init {
        println("HorizontalShelfComponent index = $index Created ${hashCode()}")
        lifecycle.subscribe(
            onStart = { println("HorizontalShelfComponent index = $index Start ${hashCode()}") },
            onStop = { println("HorizontalShelfComponent index = $index Stop ${hashCode()}") }
        )
    }

    @Composable
    override fun render(modifier: Modifier) {
        HorizontalShelf(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : HorizontalShelfComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            index: Int
        ): HorizontalShelfComponentImpl
    }
}