package app.what.investtravel.features.profile.domain.models

sealed interface ProfileAction {
    object NavigateToMain : ProfileAction
}