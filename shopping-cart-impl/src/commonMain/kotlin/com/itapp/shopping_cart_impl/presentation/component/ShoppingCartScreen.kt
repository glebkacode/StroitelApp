package com.itapp.shopping_cart_impl.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.itapp.shopping_cart_api.ShoppingCartComponent
import com.itapp.uikit.theme.StroitelTheme

@Composable
internal fun ShoppingCartScreen(
    modifier: Modifier,
    component: ShoppingCartComponent
) {
    val uiState by component.uiState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Shopping Cart",
            color = StroitelTheme.colorScheme.text.moscow,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        )
    }
}
