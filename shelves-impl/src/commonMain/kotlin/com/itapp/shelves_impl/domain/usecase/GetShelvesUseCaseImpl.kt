package com.itapp.shelves_impl.domain.usecase

import com.itapp.shelves_api.domain.model.shelf.ShelfDto
import com.itapp.shelves_api.domain.usecase.GetShelvesUseCase
import com.itapp.shelves_impl.domain.repository.ShelvesRepository
import dev.zacsweers.metro.Inject

@Inject
class GetShelvesUseCaseImpl(
    private val shelvesRepository: ShelvesRepository
) : GetShelvesUseCase() {

    override suspend fun run(input: Unit): List<ShelfDto> {
        return shelvesRepository.getShelves()
    }
}