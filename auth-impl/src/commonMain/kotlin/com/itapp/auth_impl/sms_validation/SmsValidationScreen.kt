package com.itapp.auth_impl.sms_validation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.itapp.auth_api.sms_validation.SmsValidationComponent

@Composable
fun SmsValidationScreen(
    modifier: Modifier,
    component: SmsValidationComponent
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sms экран")
    }
}