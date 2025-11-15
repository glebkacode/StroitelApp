package com.itapp.products_impl.presentation.list.shelf.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.itapp.core_navigation.BaseComponent
import com.itapp.products_api.shelf.video.VideoShelfComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class VideoShelfComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted override val index: Int
) : BaseComponent(componentContext), VideoShelfComponent {

    init {
        println("VideoShelfComponent index = $index Created ${hashCode()}")
        lifecycle.subscribe(
            onStart = { println("VideoShelfComponent index = $index Start ${hashCode()}") },
            onStop = { println("VideoShelfComponent index = $index Stop ${hashCode()}") }
        )
    }

    @Composable
    override fun render(modifier: Modifier) {
        VideoShelf(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : VideoShelfComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            index: Int
        ): VideoShelfComponentImpl
    }
}