package com.itapp.stroitelapp.di

import com.itapp.auth_impl.di.AuthGraph
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph
interface AppGraph {
    val message: String
    val authGraph: AuthGraph

    @Provides
    fun provideMessage(): String = "Hello, Metro!"
}