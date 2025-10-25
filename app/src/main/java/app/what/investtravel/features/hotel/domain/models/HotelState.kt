package app.what.investtravel.features.hotel.domain.models

import app.what.foundation.data.RemoteState
import app.what.investtravel.data.remote.HotelResponse

data class HotelState(
    val hotels: List<HotelResponse> = emptyList(),
    val hotelsFetchState: RemoteState = RemoteState.Idle
)


