package app.what.investtravel.features.travel.domain.models

import app.what.investtravel.data.remote.AiRouteRequest
import app.what.investtravel.features.travel.presentation.pages.UserPreferences

sealed interface TravelEvent {
    class UpdateObjectChecked(val travel: Travel, val objectIndex: Int, val checked: Boolean) : TravelEvent
    class TravelSelected(val value: Travel, val transportType: String = "driving") : TravelEvent
    object TravelUnselected : TravelEvent
    data class SaveTravel(val value: UserPreferences) : TravelEvent
    data class SaveAiTravel(val value: AiRouteRequest) : TravelEvent
    data class DeleteTravel(val value: Travel) : TravelEvent
    object FetchTravels : TravelEvent
    data class SetToAi(val value: TravelObject) : TravelEvent
    object ShowSheet : TravelEvent
}