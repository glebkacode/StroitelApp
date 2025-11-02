package com.itapp.auth_impl.password_validation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.itapp.auth_api.password_validation.PasswordValidationComponent

@Composable
fun PasswordValidationScreen(
    modifier: Modifier,
    component: PasswordValidationComponent
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Password экран")
        Button(onClick = { component.onNextClicked() }) {
            Text(text = "Следующий экран")
        }
    }
}