package com.itapp.auth_impl.presentation.phone_validation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.uikit.theme.StroitelTheme
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import stroitelapp.auth_impl.generated.resources.Res
import stroitelapp.auth_impl.generated.resources.auth_login_text
import stroitelapp.auth_impl.generated.resources.auth_login_title
import stroitelapp.auth_impl.generated.resources.phone_validation_hint_text

@Composable
fun PhoneValidationScreen(
    modifier: Modifier,
    component: PhoneValidationComponent
) {
    val uiState by component.uiState.collectAsState()

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(Res.string.auth_login_title),
            color = StroitelTheme.colorScheme.text.moscow,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.phone,
            placeholder = {
                Text(
                    text = stringResource(Res.string.phone_validation_hint_text),
                    color = Color(0xFFADADAD),
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = StroitelTheme.colorScheme.text.transparent,
                unfocusedContainerColor = StroitelTheme.colorScheme.text.transparent,
                disabledContainerColor = StroitelTheme.colorScheme.text.transparent,
                focusedIndicatorColor = StroitelTheme.colorScheme.text.moscow,
                unfocusedIndicatorColor = StroitelTheme.colorScheme.text.moscow,
                disabledIndicatorColor = StroitelTheme.colorScheme.text.transparent,
                errorIndicatorColor = StroitelTheme.colorScheme.text.transparent,
            ),
            onValueChange = { text -> component.onPhoneChanged(text) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { component.onLoginClicked() },
            colors = ButtonDefaults.buttonColors(
                containerColor = StroitelTheme.colorScheme.text.moscow,
                contentColor = StroitelTheme.colorScheme.text.piter,
                disabledContainerColor = Color(0xFFBDBDBD),
                disabledContentColor = Color(0xFF757575)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.auth_login_text),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview
@Composable
fun PhoneValidationScreenPreview() {
    MaterialTheme {
        PhoneValidationScreen(
            modifier = Modifier,
            component = object : PhoneValidationComponent {
                override val uiState: StateFlow<PhoneValidationComponent.UiState>
                    get() = TODO("Not yet implemented")

                override fun onPhoneChanged(text: String) {}

                override fun onLoginClicked() {}

                @Composable
                override fun render(modifier: Modifier) {}
            }
        )
    }
}