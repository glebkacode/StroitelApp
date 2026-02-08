package com.itapp.auth_impl.presentation.registration.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.uikit.theme.StroitelTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import stroitelapp.auth_impl.generated.resources.Res
import stroitelapp.auth_impl.generated.resources.registration_button
import stroitelapp.auth_impl.generated.resources.registration_first_name_hint
import stroitelapp.auth_impl.generated.resources.registration_last_name_hint
import stroitelapp.auth_impl.generated.resources.registration_login_hint
import stroitelapp.auth_impl.generated.resources.registration_password_hint
import stroitelapp.auth_impl.generated.resources.registration_phone_hint
import stroitelapp.auth_impl.generated.resources.registration_title

private val OrangeButtonColor = Color(0xFFEE7100)

@Composable
fun RegistrationScreen(
    modifier: Modifier,
    component: RegistrationComponent
) {
    val uiState by component.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 21.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar with back button and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { component.onBackClicked() }) {
                Text(
                    text = "\u2190",
                    color = StroitelTheme.colorScheme.text.moscow,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp
                )
            }
            Text(
                text = stringResource(Res.string.registration_title),
                color = StroitelTheme.colorScheme.text.moscow,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // First Name field
        RegistrationTextField(
            value = uiState.firstName,
            placeholder = stringResource(Res.string.registration_first_name_hint),
            enabled = !uiState.isLoading,
            onValueChange = { component.onFirstNameChanged(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Last Name field
        RegistrationTextField(
            value = uiState.lastName,
            placeholder = stringResource(Res.string.registration_last_name_hint),
            enabled = !uiState.isLoading,
            onValueChange = { component.onLastNameChanged(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login field
        RegistrationTextField(
            value = uiState.login,
            placeholder = stringResource(Res.string.registration_login_hint),
            enabled = !uiState.isLoading,
            onValueChange = { component.onLoginChanged(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Password field with visibility toggle
        RegistrationTextField(
            value = uiState.password,
            placeholder = stringResource(Res.string.registration_password_hint),
            enabled = !uiState.isLoading,
            isPassword = true,
            isPasswordVisible = uiState.isPasswordVisible,
            onTogglePasswordVisibility = { component.onTogglePasswordVisibility() },
            onValueChange = { component.onPasswordChanged(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Phone field
        RegistrationTextField(
            value = uiState.phone,
            placeholder = stringResource(Res.string.registration_phone_hint),
            enabled = !uiState.isLoading,
            onValueChange = { component.onPhoneChanged(it) }
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Register button
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = { component.onRegisterClicked() },
            enabled = !uiState.isLoading && isFormValid(uiState),
            colors = ButtonDefaults.buttonColors(
                containerColor = OrangeButtonColor,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFBDBDBD),
                disabledContentColor = Color(0xFF757575)
            ),
            shape = RoundedCornerShape(7.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(Res.string.registration_button),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RegistrationTextField(
    value: String,
    placeholder: String,
    enabled: Boolean,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFFADADAD),
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
            )
        },
        visualTransformation = if (isPassword && !isPasswordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (isPassword && onTogglePasswordVisibility != null) {
            {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Text(
                        text = if (isPasswordVisible) "\uD83D\uDC41" else "\uD83D\uDC41\u200D\uD83D\uDDE8",
                        fontSize = 20.sp,
                        color = Color(0xFFADADAD).copy(alpha = 0.3f)
                    )
                }
            }
        } else null,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = OrangeButtonColor,
            unfocusedIndicatorColor = Color(0xFFADADAD),
            disabledIndicatorColor = Color(0xFFADADAD),
        ),
        enabled = enabled,
        singleLine = true,
        onValueChange = onValueChange
    )
}

private fun isFormValid(uiState: RegistrationComponent.UiState): Boolean {
    return uiState.firstName.isNotBlank() &&
            uiState.lastName.isNotBlank() &&
            uiState.login.isNotBlank() &&
            uiState.password.isNotBlank() &&
            uiState.phone.isNotBlank()
}

@Preview
@Composable
fun RegistrationScreenPreview() {
    MaterialTheme {
        RegistrationScreen(
            modifier = Modifier,
            component = object : RegistrationComponent {
                override val uiState: StateFlow<RegistrationComponent.UiState> =
                    MutableStateFlow(RegistrationComponent.UiState())

                override fun onFirstNameChanged(text: String) {}
                override fun onLastNameChanged(text: String) {}
                override fun onLoginChanged(text: String) {}
                override fun onPasswordChanged(text: String) {}
                override fun onPhoneChanged(text: String) {}
                override fun onTogglePasswordVisibility() {}
                override fun onRegisterClicked() {}
                override fun onBackClicked() {}

                @Composable
                override fun render(modifier: Modifier) {}
            }
        )
    }
}
