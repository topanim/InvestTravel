package app.what.investtravel.features.auth.domain

import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.foundation.services.AppLogger.Companion.Auditor
import app.what.foundation.utils.suspendCall
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.data.remote.AuthService
import app.what.investtravel.data.remote.LoginRequest
import app.what.investtravel.data.remote.UserCreate
import app.what.investtravel.data.remote.UsersService
import app.what.investtravel.features.auth.domain.models.AuthAction
import app.what.investtravel.features.auth.domain.models.AuthEvent
import app.what.investtravel.features.auth.domain.models.AuthState

class AuthController(
    private val authService: AuthService,
    private val usersService: UsersService,
    private val appValues: AppValues
) : UIController<AuthState, AuthAction, AuthEvent>(
    AuthState()
) {
    override fun obtainEvent(viewEvent: AuthEvent) = when (viewEvent) {
        is AuthEvent.EmailChanged -> {
            updateState { copy(email = viewEvent.email, errorMessage = null) }
        }

        is AuthEvent.PasswordChanged -> {
            updateState { copy(password = viewEvent.password, errorMessage = null) }
        }

        is AuthEvent.ConfirmPasswordChanged -> {
            updateState { copy(confirmPassword = viewEvent.confirmPassword, errorMessage = null) }
        }

        is AuthEvent.LoginClicked -> {
            handleLogin()
        }

        is AuthEvent.RegisterClicked -> {
            handleRegister()
        }

        is AuthEvent.SwitchToRegister -> {
            updateState { copy(isLoginMode = false, errorMessage = null) }
        }

        is AuthEvent.SwitchToLogin -> {
            updateState { copy(isLoginMode = true, errorMessage = null) }
        }

        is AuthEvent.ClearError -> {
            updateState { copy(errorMessage = null) }
        }
    }

    private fun handleLogin() {
        val currentState = viewState
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            updateState { copy(errorMessage = "Пожалуйста, заполните все поля") }
            return
        }

        if (!isValidEmail(currentState.email)) {
            updateState { copy(errorMessage = "Введите корректный email") }
            return
        }

        updateState { copy(isLoading = true, errorMessage = null) }
        suspendCall(viewModelScope) {
            val response = authService.login(
                LoginRequest(
                    login = currentState.email,
                    password = currentState.password
                )
            )

            Auditor.debug("d", response.getOrNull().toString())
            Auditor.debug("d", response.exceptionOrNull().toString())
            appValues.authToken.set(response.getOrNull()!!.accessToken)

            setAction(AuthAction.NavigateToMain)
            updateState { copy(isLoading = false) }
        }
    }

    private fun handleRegister() {
        val currentState = viewState
        if (currentState.email.isBlank() || currentState.password.isBlank() || currentState.confirmPassword.isBlank()) {
            updateState { copy(errorMessage = "Пожалуйста, заполните все поля") }
            return
        }

        if (!isValidEmail(currentState.email)) {
            updateState { copy(errorMessage = "Введите корректный email") }
            return
        }

        if (currentState.password != currentState.confirmPassword) {
            updateState { copy(errorMessage = "Пароли не совпадают") }
            return
        }

        if (currentState.password.length < 6) {
            updateState { copy(errorMessage = "Пароль должен содержать минимум 6 символов") }
            return
        }

        updateState { copy(isLoading = true, errorMessage = null) }

        suspendCall(viewModelScope) {
            val response = usersService.createUser(
                UserCreate(
                    login = currentState.email,
                    password = currentState.password,
                    name = currentState.email,
                    email = currentState.email
                )
            )

            val authResponse = authService.login(
                LoginRequest(
                    login = currentState.email,
                    password = currentState.password
                )
            )

            appValues.authToken.set(authResponse.getOrNull()!!.accessToken)

            setAction(AuthAction.NavigateToMain)
            updateState { copy(isLoading = false) }
        }

        // Здесь будет логика регистрации
        // Пока что просто симулируем успешную регистрацию
        setAction(AuthAction.NavigateToMain)
        updateState { copy(isLoading = false) }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}