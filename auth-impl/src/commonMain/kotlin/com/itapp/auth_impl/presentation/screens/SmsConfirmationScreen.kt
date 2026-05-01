package com.itapp.auth_impl.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itapp.uikit.theme.StroitelTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SmsConfirmationScreen(
    modifier: Modifier,
    onConfirmed: () -> Unit,
    onBack: () -> Unit,
) {
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.weight(1f))

        Text(
            text = "Enter SMS code",
            color = StroitelTheme.colorScheme.text.moscow,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = code,
            placeholder = {
                Text(
                    text = "4 digits",
                    color = Color(0xFFADADAD),
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                )
            },
            onValueChange = { input ->
                if (input.length <= 4 && input.all { it.isDigit() }) {
                    code = input
                    error = null
                }
            },
            isError = error != null,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = StroitelTheme.colorScheme.text.transparent,
                unfocusedContainerColor = StroitelTheme.colorScheme.text.transparent,
                disabledContainerColor = StroitelTheme.colorScheme.text.transparent,
                focusedIndicatorColor = StroitelTheme.colorScheme.text.moscow,
                unfocusedIndicatorColor = StroitelTheme.colorScheme.text.moscow,
            ),
        )

        if (error != null) {
            Text(
                text = error!!,
                color = Color(0xFFE53935),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                scope.launch {
                    isLoading = true
                    delay(300)
                    if (code == "1234") {
                        isLoading = false
                        onConfirmed()
                    } else {
                        error = "Wrong code"
                        isLoading = false
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = StroitelTheme.colorScheme.text.moscow,
                contentColor = StroitelTheme.colorScheme.text.piter,
                disabledContainerColor = Color(0xFFBDBDBD),
                disabledContentColor = Color(0xFF757575),
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = if (isLoading) "..." else "Confirm",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
            )
        }

        Spacer(Modifier.weight(1f))

        TextButton(onClick = onBack) {
            Text(
                text = "Back",
                color = StroitelTheme.colorScheme.text.moscow,
                fontSize = 15.sp,
            )
        }
    }
}
