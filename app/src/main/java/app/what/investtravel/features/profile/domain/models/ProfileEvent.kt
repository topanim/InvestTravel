package app.what.investtravel.features.profile.domain.models

sealed interface ProfileEvent {
    object Init : ProfileEvent
    data class UpdateUserName(val name: String) : ProfileEvent
}