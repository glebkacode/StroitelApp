package com.itapp.auth_impl.phone_validation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import org.jetbrains.compose.resources.stringResource
import stroitelapp.auth_impl.generated.resources.Res
import stroitelapp.auth_impl.generated.resources.auth_login_text
import stroitelapp.auth_impl.generated.resources.auth_login_title
import stroitelapp.auth_impl.generated.resources.phone_validation_hint_text
import stroitelapp.auth_impl.generated.resources.phone_validation_sign_up

@Composable
fun PhoneValidationScreen(
    modifier: Modifier,
    component: PhoneValidationComponent
) {
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(Res.string.auth_login_title),
            color = Color(0xFFEE7100)
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = phone,
            placeholder = {
                Text(
                    text = stringResource(Res.string.phone_validation_hint_text),
                    color = Color(0xFFADADAD)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFFEE7100),
                unfocusedIndicatorColor = Color(0xFFEE7100),
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            onValueChange = { text -> phone = text }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { component.onNextClicked() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEE7100),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFBDBDBD),
                disabledContentColor = Color(0xFF757575)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = stringResource(Res.string.auth_login_text))
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = {}
        ) {
            Text(
                text = stringResource(Res.string.phone_validation_sign_up),
                color = Color(0xFFEE7100)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}