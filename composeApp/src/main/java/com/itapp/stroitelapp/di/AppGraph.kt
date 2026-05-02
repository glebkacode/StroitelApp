package com.itapp.stroitelapp.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph
interface AppGraph {
    val storeFactory: StoreFactory

    @Provides
    private fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()
}
