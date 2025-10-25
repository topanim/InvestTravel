package app.what.investtravel.features.hotel.domain

import app.what.foundation.core.UIController
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.features.hotel.domain.models.HotelAction
import app.what.investtravel.features.hotel.domain.models.HotelEvent
import app.what.investtravel.features.hotel.domain.models.HotelState


class HotelController(
    private val settings: AppValues
) : UIController<HotelState, HotelAction, HotelEvent>(
    HotelState()
) {
    override fun obtainEvent(viewEvent: HotelEvent) = when (viewEvent) {
        else -> {}
    }
}