package com.itapp.shelves_impl.data.repository

import com.itapp.shelves_api.domain.model.shelf.ShelfDto
import com.itapp.shelves_impl.data.api.ShelvesDataSource
import com.itapp.shelves_impl.data.mapper.toDomain
import com.itapp.shelves_impl.domain.repository.ShelvesRepository
import dev.zacsweers.metro.Inject

@Inject
class ShelvesRepositoryImpl(
    private val remoteDataSource: ShelvesDataSource
) : ShelvesRepository {

    override suspend fun getShelves(): List<ShelfDto> {
        return remoteDataSource.getShelves().map { it.toDomain() }
    }
}