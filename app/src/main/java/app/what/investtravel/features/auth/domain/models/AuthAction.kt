package app.what.investtravel.features.auth.domain.models

sealed interface AuthAction {
    object NavigateToMain : AuthAction
    object NavigateToRegister : AuthAction
    object NavigateToLogin : AuthAction
    data class ShowError(val message: String) : AuthAction
}