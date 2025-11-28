package com.itapp.stroitelapp.di

import com.itapp.core_architecture.tea.DefaultTeaFactory
import com.itapp.core_architecture.tea.TeaFactory
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph
interface AppGraph {
    val message: String

    val teaFactory: TeaFactory

    @Provides
    fun provideMessage(): String = "Hello, Metro!"

    @Provides
    private fun provideTeaFactory(): TeaFactory = DefaultTeaFactory()
}