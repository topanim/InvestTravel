package app.what.investtravel.features.travel.domain.models

import app.what.foundation.core.Monitor.Companion.monitored
import app.what.foundation.data.RemoteState

data class TravelState(
    val travels: List<Travel> = emptyList(),
    val travelsFetchState: RemoteState = RemoteState.Idle,
    val selectedTravel: Travel? = null,
    val showSheet: Boolean = false,
    val aiComment: String = "",
    val isCreatingRoute: Boolean = false
)

data class Travel(
    val name: String,
    val distance: Double,
    val time: Double,
    val objects: List<TravelObject>
)

data class TravelObject(
    val bannerUri: String,
    val name: String,
    val type: String,
    val lat: Double,
    val lon: Double,
    val address: String? = null,
    val description: String? = null,
    val durationMinutes: Int = 0,
    val arrivalTime: String? = null,
    val departureTime: String? = null,
    var checked: Boolean = false
)