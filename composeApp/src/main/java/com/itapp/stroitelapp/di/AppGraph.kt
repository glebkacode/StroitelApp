package com.itapp.stroitelapp.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@DependencyGraph
interface AppGraph {
    val storeFactory: StoreFactory
    val httpClient: HttpClient

    @Provides
    private fun provideStoreFactory(): StoreFactory = DefaultStoreFactory()

    @Provides
    private fun provideHttpClient(): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                },
            )
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTP
                host = "10.0.2.2"
                port = 8080
            }
        }
    }
}
