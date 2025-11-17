package com.itapp.shelves_impl.data.mapper

import com.itapp.shelves_api.domain.model.shelf.CatalogShelfDto
import com.itapp.shelves_api.domain.model.shelf.CatalogShelfFilterDto
import com.itapp.shelves_impl.data.model.FilterDtoResponse
import com.itapp.shelves_impl.data.model.ShelfDtoResponse

fun ShelfDtoResponse.toCatalogShelf(): CatalogShelfDto {
    return CatalogShelfDto(
        id = id,
        header = header,
        items = items.map { it.toDefaultShelfItem() },
        filterOptions = filterOptions.map { it.toCatalogFilterOption() }
    )
}

fun FilterDtoResponse.toCatalogFilterOption(): CatalogShelfFilterDto {
    return CatalogShelfFilterDto(
        id = id,
        option = option
    )
}