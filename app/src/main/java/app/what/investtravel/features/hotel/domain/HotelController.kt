package app.what.investtravel.features.hotel.domain

import app.what.foundation.core.UIController
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.data.remote.HotelsService
import app.what.investtravel.features.hotel.domain.models.HotelAction
import app.what.investtravel.features.hotel.domain.models.HotelEvent
import app.what.investtravel.features.hotel.domain.models.HotelState


class HotelController(
    private val hotelService: HotelsService,
    private val settings: AppValues
) : UIController<HotelState, HotelAction, HotelEvent>(
    HotelState()
) {
    init {
        settings.authToken.set("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxMywicm9sZV9pZCI6MCwiZXhwIjoxNzYxNzcyNzU4fQ.6GqV4BVDlFIBy34HB6ISy-vnuwxJ7cy0X4aZodvHAHo")
    }

    override fun obtainEvent(viewEvent: HotelEvent) = when (viewEvent) {
        else -> {}
    }

    fun fetchHotels() {
        hotelService.searchHotels(

        )
    }
}