package com.itapp.shelves_impl.data.api

import com.itapp.shelves_impl.data.model.ShelfDtoResponse

interface ShelvesDataSource {
    suspend fun getShelves(): List<ShelfDtoResponse>
}