package com.itapp.shelves_render_api.shelf.grid

import com.itapp.shelves_render_api.shelf.root.model.shelf.ShelfModel
import com.itapp.shelves_render_api.shelf.root.model.shelfitem.PosterModel

data class GridShelfModel(
    override val id: Long,
    override val header: String,
    val items: List<PosterModel>
) : ShelfModel