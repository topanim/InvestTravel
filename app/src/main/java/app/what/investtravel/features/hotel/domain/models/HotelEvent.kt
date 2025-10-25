package app.what.investtravel.features.hotel.domain.models

import app.what.investtravel.data.remote.HotelResponse

sealed interface HotelEvent {
    object Init : HotelEvent
    object Refresh : HotelEvent
    object LoadNextPage : HotelEvent
    data class UpdateFilters(val filters: HotelFilters) : HotelEvent
    data class HotelSelected(val hotel: HotelResponse) : HotelEvent
}