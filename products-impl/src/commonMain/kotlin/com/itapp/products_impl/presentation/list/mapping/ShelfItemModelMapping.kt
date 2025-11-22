package com.itapp.products_impl.presentation.list.mapping

import com.itapp.shelves_api.domain.model.shelfitem.DefaultShelfItemDto
import com.itapp.shelves_render_api.shelf.root.model.shelfitem.PosterModel

fun DefaultShelfItemDto.toModel(): PosterModel {
    return PosterModel(
        id = id,
        title = title,
        description = description,
        url = url,
        labelInfo = labelInfo,
        price = price
    )
}