package com.itapp.stroitelapp.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph
interface AppGraph {
    val message: String

    val storeFactory: StoreFactory

    @Provides
    fun provideMessage(): String = "Hello, Metro!"

    @Provides
    private fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()
}