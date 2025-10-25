package app.what.investtravel.features.hotel.domain.models

import app.what.foundation.data.RemoteState
import app.what.investtravel.data.remote.HotelResponse

data class HotelState(
    val hotels: List<HotelResponse> = emptyList(),
    val hotelsFetchState: RemoteState = RemoteState.Idle,
    val filters: HotelFilters = HotelFilters(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasNextPage: Boolean = true,
    val error: String? = null
)

data class HotelFilters(
    val checkIn: String? = null,
    val checkOut: String? = null,
    val guests: Int = 1,
    val rooms: Int = 1,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val stars: String? = null
)

