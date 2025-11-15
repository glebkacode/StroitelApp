package com.itapp.stroitelapp.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.itapp.auth_impl.di.AuthGraph
import com.itapp.products_impl.di.ProductsGraph
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph
interface AppGraph {
    val message: String
    val authGraph: AuthGraph

    val productsGraph: ProductsGraph

    @Provides
    fun provideMessage(): String = "Hello, Metro!"

    @Provides
    private fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()
}