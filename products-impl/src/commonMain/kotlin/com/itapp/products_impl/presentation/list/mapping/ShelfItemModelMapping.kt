package com.itapp.products_impl.presentation.list.mapping

import com.itapp.shelves_api.domain.model.shelfitem.DefaultShelfItemDto
import com.itapp.shelves_api.domain.model.shelfitem.ShelfItemDto
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent.ModelItem

fun ShelfItemDto.toModel(): ModelItem {
    return when (this) {
        is DefaultShelfItemDto -> toModel()
    }
}

fun DefaultShelfItemDto.toModel(): ModelItem.Default {
    return ModelItem.Default(
        id = id,
        title = title,
        description = description,
        url = url,
        labelInfo = labelInfo,
        price = price
    )
}