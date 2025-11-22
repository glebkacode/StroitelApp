package com.itapp.shelves_render_impl.shelfitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itapp.shelves_render_api.shelf.root.model.shelfitem.PosterModel
import com.itapp.uikit.theme.StroitelTheme

@Composable
fun Poster(
    model: PosterModel,
    onClick: () -> Unit,
    onMoreInfoClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = model.title,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = model.description,
                fontSize = 10.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${model.price}",
                    fontSize = 12.sp,
                    color = StroitelTheme.colorScheme.text.moscow
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = { onMoreInfoClick() }) {
                    Text(
                        text = "ПОДРОБНЕЕ О МОДЕЛИ",
                        color = StroitelTheme.colorScheme.text.piter,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}