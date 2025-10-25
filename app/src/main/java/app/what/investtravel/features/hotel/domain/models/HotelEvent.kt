package app.what.investtravel.features.hotel.domain.models

sealed interface HotelEvent {
    object Init : HotelEvent
}