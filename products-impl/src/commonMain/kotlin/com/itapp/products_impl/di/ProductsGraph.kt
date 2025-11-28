package com.itapp.products_impl.di

import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.products_api.ProductDetailsComponent
import com.itapp.products_api.ProductListComponent
import com.itapp.products_api.RootProductsComponent
import com.itapp.products_impl.presentation.details.ProductDetailsComponentImpl
import com.itapp.products_impl.presentation.list.component.ProductListComponentImpl
import com.itapp.products_impl.presentation.root.RootProductsComponentImpl
import com.itapp.shelves_api.domain.usecase.GetShelvesUseCase
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph
interface ProductsGraph {

    val productsComponentFactory: Lazy<RootProductsComponent.Factory>

    @Binds
    val RootProductsComponentImpl.Factory.bind: RootProductsComponent.Factory

    @Binds
    val ProductListComponentImpl.Factory.bind: ProductListComponent.Factory

    @Binds
    val ProductDetailsComponentImpl.Factory.bind: ProductDetailsComponent.Factory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides teaFactory: TeaFactory,
            @Provides shelvesRenderComponentFactory: ShelvesRenderComponent.Factory,
            @Provides getShelvesUseCase: GetShelvesUseCase
        ): ProductsGraph
    }
}