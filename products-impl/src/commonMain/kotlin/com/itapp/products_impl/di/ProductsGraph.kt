package com.itapp.products_impl.di

import com.itapp.products_api.ProductDetailsComponent
import com.itapp.products_api.ProductListComponent
import com.itapp.products_api.RootProductsComponent
import com.itapp.products_api.shelf.grid.GridShelfComponent
import com.itapp.products_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.products_api.shelf.video.VideoShelfComponent
import com.itapp.products_impl.presentation.details.ProductDetailsComponentImpl
import com.itapp.products_impl.presentation.list.ProductListComponentImpl
import com.itapp.products_impl.presentation.list.shelf.grid.GridShelfComponentImpl
import com.itapp.products_impl.presentation.list.shelf.horizontal.HorizontalShelfComponentImpl
import com.itapp.products_impl.presentation.list.shelf.video.VideoShelfComponentImpl
import com.itapp.products_impl.presentation.root.RootProductsComponentImpl
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.GraphExtension

@GraphExtension
interface ProductsGraph {

    val productsComponentFactory: Lazy<RootProductsComponent.Factory>

    @Binds
    val RootProductsComponentImpl.Factory.bind: RootProductsComponent.Factory

    @Binds
    val ProductListComponentImpl.Factory.bind: ProductListComponent.Factory

    @Binds
    val ProductDetailsComponentImpl.Factory.bind: ProductDetailsComponent.Factory

    @Binds
    val HorizontalShelfComponentImpl.Factory.bind: HorizontalShelfComponent.Factory

    @Binds
    val VideoShelfComponentImpl.Factory.bind: VideoShelfComponent.Factory

    @Binds
    val GridShelfComponentImpl.Factory.bind: GridShelfComponent.Factory
}