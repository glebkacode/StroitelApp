package com.itapp.products_api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.itapp.core_navigation.UiComponent

interface RootProductsComponent : UiComponent {
    val stack: Value<ChildStack<*, Child>>
    sealed interface Child {
        class ProductsList(val component: ProductListComponent) : Child
        class ProductDetails(val component: ProductDetailsComponent) : Child
    }
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): RootProductsComponent
    }
}