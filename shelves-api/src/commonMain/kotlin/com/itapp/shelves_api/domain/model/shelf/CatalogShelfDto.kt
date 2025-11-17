package com.itapp.shelves_api.domain.model.shelf

import com.itapp.shelves_api.domain.model.shelfitem.ShelfItemDto

data class CatalogShelfDto(
    override val id: Long,
    override val header: String,
    override val items: List<ShelfItemDto>,
    val filterOptions: List<CatalogShelfFilterDto>
) : ShelfDto

data class CatalogShelfFilterDto(
    val id: Long,
    val option: String
)