package app.what.investtravel.features.profile.domain

import app.what.foundation.core.UIController
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.features.profile.domain.models.ProfileAction
import app.what.investtravel.features.profile.domain.models.ProfileEvent
import app.what.investtravel.features.profile.domain.models.ProfileState


class ProfileController(
    private val settings: AppValues
) : UIController<ProfileState, ProfileAction, ProfileEvent>(
    ProfileState()
) {
    override fun obtainEvent(viewEvent: ProfileEvent) = when (viewEvent) {
        else -> {}
    }
}