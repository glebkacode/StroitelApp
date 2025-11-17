package com.itapp.shelves_impl.data.api

import com.itapp.shelves_impl.data.model.ShelfDtoResponse
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val SHELVES_BASE_URL = "https://api.yourservice.com"

@Inject
class ShelvesDataSourceImpl(
    private val httpClient: HttpClient
) : ShelvesDataSource {

    override suspend fun getShelves(): List<ShelfDtoResponse> {
        return httpClient.get(SHELVES_BASE_URL).body()
    }
}