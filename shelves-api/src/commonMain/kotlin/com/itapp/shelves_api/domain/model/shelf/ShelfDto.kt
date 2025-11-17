package com.itapp.shelves_api.domain.model.shelf

import com.itapp.shelves_api.domain.model.shelfitem.ShelfItemDto

interface ShelfDto {
    val id: Long
    val header: String
    val items: List<ShelfItemDto>
}