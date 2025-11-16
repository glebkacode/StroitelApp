package com.itapp.shelves_render_impl.shelf.video

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itapp.shelves_render_api.shelf.video.VideoShelfComponent

@Composable
fun VideoShelf(
    modifier: Modifier,
    component: VideoShelfComponent
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Video Полка - ${component.index}")
    }
}