package com.itapp.products_impl.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.itapp.products_api.ProductDetailsComponent
import com.itapp.products_api.ProductListComponent
import com.itapp.products_api.RootProductsComponent
import com.itapp.products_impl.presentation.details.ProductDetailsComponentImpl
import com.itapp.products_impl.presentation.list.ProductListComponentImpl
import com.itapp.products_impl.presentation.root.RootProductsComponentImpl
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
            @Provides storeFactory: StoreFactory,
            @Provides shelvesRenderComponentFactory: ShelvesRenderComponent.Factory,
        ): ProductsGraph
    }
}