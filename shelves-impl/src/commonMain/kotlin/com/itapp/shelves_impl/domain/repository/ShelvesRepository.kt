package com.itapp.shelves_impl.domain.repository

import com.itapp.shelves_api.domain.model.shelf.ShelfDto

interface ShelvesRepository {
    suspend fun getShelves(): List<ShelfDto>
}