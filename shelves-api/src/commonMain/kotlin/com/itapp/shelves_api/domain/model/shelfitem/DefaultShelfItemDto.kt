package com.itapp.shelves_api.domain.model.shelfitem

data class DefaultShelfItemDto(
    override val id: Long,
    val title: String,
    val description: String,
    val url: String,
    val labelInfo: String,
    val price: Double
) : ShelfItemDto