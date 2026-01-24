package com.itapp.shelves_impl.data.mapper

import com.itapp.shelves_api.domain.model.shelf.DefaultShelfDto
import com.itapp.shelves_api.domain.model.shelfitem.DefaultShelfItemDto
import com.itapp.shelves_impl.data.model.SelfItemDtoResponse
import com.itapp.shelves_impl.data.model.ShelfDtoResponse

fun ShelfDtoResponse.toDefaultShelf(): DefaultShelfDto {
    return DefaultShelfDto(
        id = id,
        header = header,
        items = items.map { it.toDefaultShelfItem() }
    )
}

fun SelfItemDtoResponse.toDefaultShelfItem(): DefaultShelfItemDto {
    return DefaultShelfItemDto(
        id = id,
        title = title,
        description = description,
        url = url,
        labelInfo = labelInfo,
        price = price
    )
}