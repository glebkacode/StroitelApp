package com.itapp.shelves_impl.fake

import com.itapp.shelves_impl.data.api.ShelvesDataSource
import com.itapp.shelves_impl.data.model.ShelfDtoResponse

class FakeShelvesDataSource : ShelvesDataSource {

    val getShelvesCalls = mutableListOf<Unit>()

    var shelvesToReturn: List<ShelfDtoResponse> = emptyList()
    var exceptionToThrow: Exception? = null

    override suspend fun getShelves(): List<ShelfDtoResponse> {
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
