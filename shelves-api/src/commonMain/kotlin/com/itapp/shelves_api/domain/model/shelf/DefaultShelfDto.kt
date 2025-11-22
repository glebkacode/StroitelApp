package com.itapp.shelves_api.domain.model.shelf

import com.itapp.shelves_api.domain.model.shelfitem.DefaultShelfItemDto

data class DefaultShelfDto(
    override val id: Long,
    override val header: String,
    override val items: List<DefaultShelfItemDto>,
) : ShelfDto