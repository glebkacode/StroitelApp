package com.itapp.products_impl.presentation.list.mapping

import com.itapp.shelves_api.domain.model.shelf.CatalogShelfDto
import com.itapp.shelves_api.domain.model.shelf.DefaultShelfDto
import com.itapp.shelves_api.domain.model.shelf.ShelfDto
import com.itapp.shelves_render_api.shelf.grid.GridShelfModel
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfModel
import com.itapp.shelves_render_api.shelf.root.model.shelf.ShelfModel

fun ShelfDto.toModel(): ShelfModel {
    return when (this) {
        is DefaultShelfDto -> toModel()
        is CatalogShelfDto -> toModel()
        else -> throw IllegalArgumentException("Unknown shelf dto")
    }
}

fun DefaultShelfDto.toModel(): HorizontalShelfModel {
    return HorizontalShelfModel(
        id = id,
        header = header,
        items = items.map { it.toModel() }
    )
}

fun CatalogShelfDto.toModel(): GridShelfModel {
    return GridShelfModel(
        id = id,
        header = header,
        items = items.map { it.toModel() }
    )
}
