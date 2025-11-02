package com.itapp.auth_impl.phone_validation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.itapp.auth_api.phone_validation.PhoneValidationComponent

@Composable
fun PhoneValidationScreen(
    modifier: Modifier,
    component: PhoneValidationComponent
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Phone экран")
        Button(onClick = { component.onNextClicked() }) {
            Text(text = "Следующий экран")
        }
    }
}