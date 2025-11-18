package com.itapp.products_impl.presentation.list.mapping

import com.itapp.shelves_api.domain.model.shelf.CatalogShelfDto
import com.itapp.shelves_api.domain.model.shelf.CatalogShelfFilterDto
import com.itapp.shelves_api.domain.model.shelf.DefaultShelfDto
import com.itapp.shelves_api.domain.model.shelf.ShelfDto
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent.FilterOption
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent.Model

fun ShelfDto.toModel(): Model {
    return when (this) {
        is DefaultShelfDto -> toModel()
        is CatalogShelfDto -> toModel()
        else -> throw IllegalArgumentException("Unknown shelf dto")
    }
}

fun DefaultShelfDto.toModel(): Model.Default {
    return Model.Default(
        id = id,
        header = header,
        items = items.map { it.toModel() }
    )
}

fun CatalogShelfDto.toModel(): Model.Catalog {
    return Model.Catalog(
        id = id,
        header = header,
        items = items.map { it.toModel() },
        filterOptions = filterOptions.map { it.toModel() }
    )
}

fun CatalogShelfFilterDto.toModel(): FilterOption {
    return FilterOption(
        id = id,
        option = option
    )
}