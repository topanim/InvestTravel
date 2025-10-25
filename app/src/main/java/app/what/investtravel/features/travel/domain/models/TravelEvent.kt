package app.what.investtravel.features.travel.domain.models

import app.what.investtravel.features.travel.presentation.pages.UserPreferences

sealed interface TravelEvent {
    class TravelSelected(val value: Travel) : TravelEvent
    object TravelUnselected : TravelEvent
    data class SaveTravel(val value: UserPreferences) : TravelEvent
}