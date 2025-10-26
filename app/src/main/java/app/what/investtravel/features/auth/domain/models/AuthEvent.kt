package app.what.investtravel.features.auth.domain.models

sealed interface AuthEvent {
    data class EmailChanged(val email: String) : AuthEvent
    data class PasswordChanged(val password: String) : AuthEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : AuthEvent
    object LoginClicked : AuthEvent
    object RegisterClicked : AuthEvent
    object SwitchToRegister : AuthEvent
    object SwitchToLogin : AuthEvent
    object ClearError : AuthEvent
}