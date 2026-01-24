package com.itapp.shopping_cart_impl.di

import com.itapp.shopping_cart_api.ShoppingCartComponent
import com.itapp.shopping_cart_impl.presentation.component.ShoppingCartComponentImpl
import com.itapp.core_architecture.tea.TeaFactory
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph
interface ShoppingCartGraph {

    val shoppingCartComponentFactory: Lazy<ShoppingCartComponent.Factory>

    @Binds
    val ShoppingCartComponentImpl.Factory.bind: ShoppingCartComponent.Factory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides teaFactory: TeaFactory
        ): ShoppingCartGraph
    }
}
