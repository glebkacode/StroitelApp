package com.itapp.shelves_impl.fake

import com.itapp.shelves_api.domain.model.shelf.ShelfDto
import com.itapp.shelves_impl.domain.repository.ShelvesRepository

class FakeShelvesRepository : ShelvesRepository {

    val getShelvesCalls = mutableListOf<Unit>()

    var shelvesToReturn: List<ShelfDto> = emptyList()
    var exceptionToThrow: Exception? = null

    override suspend fun getShelves(): List<ShelfDto> {
        exceptionToThrow?.let { throw it }
        getShelvesCalls.add(Unit)
        return shelvesToReturn
    }

    fun reset() {
        getShelvesCalls.clear()
        shelvesToReturn = emptyList()
        exceptionToThrow = null
    }
}
