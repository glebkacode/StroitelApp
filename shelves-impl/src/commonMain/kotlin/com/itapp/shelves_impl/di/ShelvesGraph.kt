package com.itapp.shelves_impl.di

import com.itapp.shelves_api.domain.usecase.GetShelvesUseCase
import com.itapp.shelves_impl.data.api.ShelvesDataSource
import com.itapp.shelves_impl.data.api.ShelvesDataSourceImpl
import com.itapp.shelves_impl.data.repository.ShelvesRepositoryImpl
import com.itapp.shelves_impl.domain.repository.ShelvesRepository
import com.itapp.shelves_impl.domain.usecase.GetShelvesUseCaseImpl
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient

@DependencyGraph
interface ShelvesGraph {

    @Binds
    val ShelvesDataSourceImpl.bind: ShelvesDataSource

    @Binds
    val ShelvesRepositoryImpl.bind : ShelvesRepository

    @Binds
    val GetShelvesUseCaseImpl.bind: GetShelvesUseCase

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient()
    }
}