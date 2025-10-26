package app.what.investtravel.features.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.what.investtravel.features.auth.domain.AuthController
import app.what.investtravel.features.auth.domain.models.AuthEvent
import app.what.investtravel.features.main.navigation.MainProvider
import app.what.navigation.core.rememberNavigator

@Composable
fun AuthScreen(
    controller: AuthController,
    modifier: Modifier = Modifier
) {
    val state by controller.collectStates()
    val action by controller.collectActions()
    val navigator = rememberNavigator()

    LaunchedEffect(action) {
        when (action) {
            is app.what.investtravel.features.auth.domain.models.AuthAction.NavigateToMain -> {
                navigator.c.navigate(MainProvider)
            }
            is app.what.investtravel.features.auth.domain.models.AuthAction.ShowError -> {
                // Показать ошибку (уже отображается в UI)
            }
            else -> {}
        }
        controller.clearAction()
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Заголовок
                Text(
                    text = if (state.isLoginMode) "Вход в систему" else "Регистрация",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // Поле Email
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { controller.obtainEvent(AuthEvent.EmailChanged(it)) },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.errorMessage != null
                )

                // Поле Пароль
                var passwordVisible by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { controller.obtainEvent(AuthEvent.PasswordChanged(it)) },
                    label = { Text("Пароль") },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Пароль")
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.errorMessage != null
                )

                // Поле подтверждения пароля (только для регистрации)
                if (!state.isLoginMode) {
                    var confirmPasswordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = { controller.obtainEvent(AuthEvent.ConfirmPasswordChanged(it)) },
                        label = { Text("Подтвердите пароль") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Подтверждение пароля")
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (confirmPasswordVisible) "Скрыть пароль" else "Показать пароль"
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = state.errorMessage != null
                    )
                }

                // Сообщение об ошибке
                state.errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Кнопка входа/регистрации
                Button(
                    onClick = {
                        if (state.isLoginMode) {
                            controller.obtainEvent(AuthEvent.LoginClicked)
                        } else {
                            controller.obtainEvent(AuthEvent.RegisterClicked)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = if (state.isLoginMode) "Войти" else "Зарегистрироваться",
                            fontSize = 16.sp
                        )
                    }
                }

                // Кнопка переключения режима
                TextButton(
                    onClick = {
                        if (state.isLoginMode) {
                            controller.obtainEvent(AuthEvent.SwitchToRegister)
                        } else {
                            controller.obtainEvent(AuthEvent.SwitchToLogin)
                        }
                    }
                ) {
                    Text(
                        text = if (state.isLoginMode) {
                            "Еще не в системе? Зарегистрироваться"
                        } else {
                            "Уже есть аккаунт? Войти"
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}