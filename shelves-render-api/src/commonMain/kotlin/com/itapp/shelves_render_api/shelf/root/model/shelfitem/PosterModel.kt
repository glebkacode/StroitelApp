package com.itapp.shelves_render_api.shelf.root.model.shelfitem

data class PosterModel(
    override val id: Long,
    val title: String,
    val description: String,
    val url: String,
    val labelInfo: String,
    val price: Double
) : ShelfItemModel