package com.itapp.shelves_impl.data.mapper

import com.itapp.shelves_api.domain.model.shelf.ShelfDto
import com.itapp.shelves_impl.data.model.ShelfDtoResponse

private const val DEFAULT_SHELF_TYPE = "default"
private const val CATALOG_SHELF_TYPE = "catalog"

fun ShelfDtoResponse.toDomain(): ShelfDto {
    return when (type) {
        DEFAULT_SHELF_TYPE -> toDefaultShelf()
        CATALOG_SHELF_TYPE -> toCatalogShelf()
        else -> throw IllegalArgumentException("Unknown shelf type: $type")
    }
}