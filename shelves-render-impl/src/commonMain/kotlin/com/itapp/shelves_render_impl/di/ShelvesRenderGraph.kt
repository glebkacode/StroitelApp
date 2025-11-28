package com.itapp.shelves_render_impl.di

import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.shelves_render_api.shelf.grid.GridShelfComponent
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent
import com.itapp.shelves_render_api.shelf.video.VideoShelfComponent
import com.itapp.shelves_render_impl.shelf.grid.GridShelfComponentImpl
import com.itapp.shelves_render_impl.shelf.horizontal.HorizontalShelfComponentImpl
import com.itapp.shelves_render_impl.shelf.root.ShelvesRenderComponentImpl
import com.itapp.shelves_render_impl.shelf.video.VideoShelfComponentImpl
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph
interface ShelvesRenderGraph {

    val shelvesRenderComponentFactory: ShelvesRenderComponent.Factory

    @Binds
    val ShelvesRenderComponentImpl.Factory.bind: ShelvesRenderComponent.Factory

    @Binds
    val HorizontalShelfComponentImpl.Factory.bind: HorizontalShelfComponent.Factory

    @Binds
    val VideoShelfComponentImpl.Factory.bind: VideoShelfComponent.Factory

    @Binds
    val GridShelfComponentImpl.Factory.bind: GridShelfComponent.Factory

    @DependencyGraph.Factory
    interface Factory {
        fun create(
            @Provides teaFactory: TeaFactory
        ): ShelvesRenderGraph
    }
}