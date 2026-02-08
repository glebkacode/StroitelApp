package com.itapp.auth_impl.presentation.sms_validation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.uikit.theme.StroitelTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import stroitelapp.auth_impl.generated.resources.Res
import stroitelapp.auth_impl.generated.resources.sms_validation_button
import stroitelapp.auth_impl.generated.resources.sms_validation_hint_text
import stroitelapp.auth_impl.generated.resources.sms_validation_title

@Composable
fun SmsValidationScreen(
    modifier: Modifier,
    component: SmsValidationComponent
) {
    val uiState by component.uiState.collectAsState()

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { component.onBackClicked() }) {
                Text(
                    text = "<",
                    color = StroitelTheme.colorScheme.text.moscow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(Res.string.sms_validation_title),
            color = StroitelTheme.colorScheme.text.moscow,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = uiState.phoneNumber,
            color = StroitelTheme.colorScheme.text.moscow,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.smsCode,
            placeholder = {
                Text(
                    text = stringResource(Res.string.sms_validation_hint_text),
                    color = Color(0xFFADADAD),
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = StroitelTheme.colorScheme.text.transparent,
                unfocusedContainerColor = StroitelTheme.colorScheme.text.transparent,
                disabledContainerColor = StroitelTheme.colorScheme.text.transparent,
                focusedIndicatorColor = StroitelTheme.colorScheme.text.moscow,
                unfocusedIndicatorColor = StroitelTheme.colorScheme.text.moscow,
                disabledIndicatorColor = StroitelTheme.colorScheme.text.transparent,
                errorIndicatorColor = Color.Red,
            ),
            isError = uiState.error != null,
            enabled = !uiState.isLoading,
            onValueChange = { text -> component.onSmsCodeChanged(text) }
        )

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error!!,
                color = Color.Red,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { component.onConfirmClicked() },
            enabled = !uiState.isLoading && uiState.smsCode.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = StroitelTheme.colorScheme.text.moscow,
                contentColor = StroitelTheme.colorScheme.text.piter,
                disabledContainerColor = Color(0xFFBDBDBD),
                disabledContentColor = Color(0xFF757575)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = StroitelTheme.colorScheme.text.piter,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(Res.string.sms_validation_button),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview
@Composable
fun SmsValidationScreenPreview() {
    MaterialTheme {
        SmsValidationScreen(
            modifier = Modifier,
            component = object : SmsValidationComponent {
                override val uiState: StateFlow<SmsValidationComponent.UiState> =
                    MutableStateFlow(
                        SmsValidationComponent.UiState(
                            phoneNumber = "+7 900 123 45 67"
                        )
                    )

                override fun onSmsCodeChanged(text: String) {}
                override fun onConfirmClicked() {}
                override fun onBackClicked() {}

                @Composable
                override fun render(modifier: Modifier) {}
            }
        )
    }
}
