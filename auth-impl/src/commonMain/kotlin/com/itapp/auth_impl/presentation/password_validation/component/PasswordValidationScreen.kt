package com.itapp.auth_impl.presentation.password_validation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.uikit.theme.StroitelTheme
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import stroitelapp.auth_impl.generated.resources.Res
import stroitelapp.auth_impl.generated.resources.auth_login_title
import stroitelapp.auth_impl.generated.resources.ic_logo
import stroitelapp.auth_impl.generated.resources.password_validation_button
import stroitelapp.auth_impl.generated.resources.password_validation_hint_text
import stroitelapp.auth_impl.generated.resources.password_validation_remind_password

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordValidationScreen(
    modifier: Modifier,
    component: PasswordValidationComponent
) {
    val uiState by component.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Вход",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { component.onBackClicked() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 21.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Логотип
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_logo),
                    contentDescription = "Логотип",
                    modifier = Modifier.size(50.dp)
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "МАГАЗИН",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "СТРОИТЕЛЬ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Подзаголовок
            Text(
                text = stringResource(Res.string.auth_login_title),
                color = StroitelTheme.colorScheme.text.moscow,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Поле ввода пароля
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password,
                placeholder = {
                    Text(
                        text = stringResource(Res.string.password_validation_hint_text),
                        color = Color(0xFFADADAD),
                        fontWeight = FontWeight.Normal,
                        fontSize = 20.sp,
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = StroitelTheme.colorScheme.text.transparent,
                    unfocusedContainerColor = StroitelTheme.colorScheme.text.transparent,
                    disabledContainerColor = StroitelTheme.colorScheme.text.transparent,
                    focusedIndicatorColor = Color(0xFF000000),
                    unfocusedIndicatorColor = Color(0xFF000000),
                    disabledIndicatorColor = StroitelTheme.colorScheme.text.transparent,
                    errorIndicatorColor = StroitelTheme.colorScheme.text.transparent,
                ),
                onValueChange = { text -> component.onPasswordChanged(text) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Ссылка "Напомнить пароль"
            Text(
                text = stringResource(Res.string.password_validation_remind_password),
                color = StroitelTheme.colorScheme.text.moscow,
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .clickable { component.onRemindPasswordClicked() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка "Продолжить"
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { component.onContinueClicked() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = StroitelTheme.colorScheme.text.moscow,
                    contentColor = StroitelTheme.colorScheme.text.piter,
                    disabledContainerColor = Color(0xFFBDBDBD),
                    disabledContentColor = Color(0xFF757575)
                ),
                shape = RoundedCornerShape(7.dp)
            ) {
                Text(
                    text = stringResource(Res.string.password_validation_button),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
fun PasswordValidationScreenPreview() {
    MaterialTheme {
        PasswordValidationScreen(
            modifier = Modifier,
            component = object : PasswordValidationComponent {
                override val uiState: StateFlow<PasswordValidationComponent.UiState>
                    get() = TODO("Not yet implemented")

                override fun onPasswordChanged(text: String) {}

                override fun onContinueClicked() {}

                override fun onRemindPasswordClicked() {}

                override fun onBackClicked() {}

                @Composable
                override fun render(modifier: Modifier) {}
            }
        )
    }
}
