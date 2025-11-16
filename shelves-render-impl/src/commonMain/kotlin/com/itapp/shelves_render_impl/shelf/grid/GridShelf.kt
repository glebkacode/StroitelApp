package com.itapp.shelves_render_impl.shelf.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itapp.shelves_render_api.shelf.grid.GridShelfComponent

@Composable
fun GridShelf(
    modifier: Modifier,
    component: GridShelfComponent
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Grid Полка - ${component.index}")
    }
}