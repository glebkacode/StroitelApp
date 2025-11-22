package com.itapp.shelves_render_impl.shelf.horizontal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itapp.shelves_render_api.shelf.horizontal.HorizontalShelfComponent
import com.itapp.shelves_render_impl.shelfitem.Poster
import com.itapp.uikit.theme.StroitelTheme

@Composable
fun HorizontalShelf(
    modifier: Modifier,
    component: HorizontalShelfComponent
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.height(80.dp)
                .fillMaxWidth()
                .background(StroitelTheme.colorScheme.text.moscow),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = component.model.header,
                fontSize = 20.sp,
                color = StroitelTheme.colorScheme.text.piter,
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(component.model.items) { posterModel ->
                Poster(
                    model = posterModel,
                    onClick = {},
                    onMoreInfoClick = {}
                )
            }
        }
    }
}