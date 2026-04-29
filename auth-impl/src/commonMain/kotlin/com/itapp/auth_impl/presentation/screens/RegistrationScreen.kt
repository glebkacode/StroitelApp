package com.itapp.auth_impl.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.uikit.theme.StroitelTheme
import org.jetbrains.compose.resources.stringResource
import stroitelapp.auth_impl.generated.resources.Res
import stroitelapp.auth_impl.generated.resources.registration_button
import stroitelapp.auth_impl.generated.resources.registration_error_first_name
import stroitelapp.auth_impl.generated.resources.registration_error_last_name
import stroitelapp.auth_impl.generated.resources.registration_error_login
import stroitelapp.auth_impl.generated.resources.registration_error_password
import stroitelapp.auth_impl.generated.resources.registration_error_phone
import stroitelapp.auth_impl.generated.resources.registration_hint_first_name
import stroitelapp.auth_impl.generated.resources.registration_hint_last_name
import stroitelapp.auth_impl.generated.resources.registration_hint_login
import stroitelapp.auth_impl.generated.resources.registration_hint_password
import stroitelapp.auth_impl.generated.resources.registration_hint_phone
import stroitelapp.auth_impl.generated.resources.registration_title

private val ErrorColor = Color(0xFFE53935)

@Composable
fun RegistrationScreen(
    modifier: Modifier,
    component: RegistrationComponent,
) {
    val uiState by component.uiState.collectAsState()

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { component.onBackClicked() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp),
                    tint = StroitelTheme.colorScheme.text.moscow,
                )
            }
            Text(
                text = stringResource(Res.string.registration_title),
                color = StroitelTheme.colorScheme.text.moscow,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        RegistrationTextField(
            value = uiState.firstName,
            placeholder = stringResource(Res.string.registration_hint_first_name),
            isError = uiState.firstNameError,
            errorText = stringResource(Res.string.registration_error_first_name),
            onValueChange = { component.onFirstNameChanged(it) },
            onFocusLost = { component.onFieldFocusLost(RegistrationComponent.Field.FIRST_NAME) },
        )

        RegistrationTextField(
            value = uiState.lastName,
            placeholder = stringResource(Res.string.registration_hint_last_name),
            isError = uiState.lastNameError,
            errorText = stringResource(Res.string.registration_error_last_name),
            onValueChange = { component.onLastNameChanged(it) },
            onFocusLost = { component.onFieldFocusLost(RegistrationComponent.Field.LAST_NAME) },
        )

        RegistrationTextField(
            value = uiState.login,
            placeholder = stringResource(Res.string.registration_hint_login),
            isError = uiState.loginError,
            errorText = stringResource(Res.string.registration_error_login),
            onValueChange = { component.onLoginChanged(it) },
            onFocusLost = { component.onFieldFocusLost(RegistrationComponent.Field.LOGIN) },
        )

        RegistrationTextField(
            value = uiState.password,
            placeholder = stringResource(Res.string.registration_hint_password),
            isError = uiState.passwordError,
            errorText = stringResource(Res.string.registration_error_password),
            onValueChange = { component.onPasswordChanged(it) },
            onFocusLost = { component.onFieldFocusLost(RegistrationComponent.Field.PASSWORD) },
            visualTransformation = if (uiState.isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { component.onPasswordVisibilityToggle() }) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = "Toggle password visibility",
                        tint = StroitelTheme.colorScheme.text.moscow,
                    )
                }
            },
        )

        RegistrationTextField(
            value = uiState.phone,
            placeholder = stringResource(Res.string.registration_hint_phone),
            isError = uiState.phoneError,
            errorText = stringResource(Res.string.registration_error_phone),
            onValueChange = { component.onPhoneChanged(it) },
            onFocusLost = { component.onFieldFocusLost(RegistrationComponent.Field.PHONE) },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { component.onRegisterClicked() },
            colors = ButtonDefaults.buttonColors(
                containerColor = StroitelTheme.colorScheme.text.moscow,
                contentColor = StroitelTheme.colorScheme.text.piter,
                disabledContainerColor = Color(0xFFBDBDBD),
                disabledContentColor = Color(0xFF757575),
            ),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.registration_button),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun RegistrationTextField(
    value: String,
    placeholder: String,
    isError: Boolean,
    errorText: String,
    onValueChange: (String) -> Unit,
    onFocusLost: () -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val indicatorColor = if (isError) ErrorColor else StroitelTheme.colorScheme.text.moscow

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    onFocusLost()
                }
            },
        value = value,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFFADADAD),
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
            )
        },
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        isError = isError,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = StroitelTheme.colorScheme.text.transparent,
            unfocusedContainerColor = StroitelTheme.colorScheme.text.transparent,
            disabledContainerColor = StroitelTheme.colorScheme.text.transparent,
            focusedIndicatorColor = indicatorColor,
            unfocusedIndicatorColor = indicatorColor,
            errorContainerColor = StroitelTheme.colorScheme.text.transparent,
            errorIndicatorColor = ErrorColor,
        ),
        onValueChange = onValueChange,
    )

    if (isError) {
        Text(
            text = errorText,
            color = ErrorColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp),
        )
    }
}
