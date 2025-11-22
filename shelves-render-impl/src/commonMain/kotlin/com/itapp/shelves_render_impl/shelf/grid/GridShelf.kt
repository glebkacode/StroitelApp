package com.itapp.shelves_render_impl.shelf.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itapp.shelves_render_api.shelf.grid.GridShelfComponent
import com.itapp.shelves_render_impl.shelfitem.Poster

@Composable
fun GridShelf(
    modifier: Modifier,
    component: GridShelfComponent
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = component.model.header,
            modifier = Modifier.height(48.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(component.model.items) { posterModel ->
                Poster(
                    model = posterModel,
                    onClick = {}
                )
            }
        }
    }
}