package app.what.investtravel.features.travel.domain.models

sealed interface TravelEvent {
    class TravelSelected(val value: Travel) : TravelEvent
    object TravelUnselected : TravelEvent
}