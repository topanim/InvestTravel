package app.what.investtravel.features.hotel.domain

import android.location.Geocoder
import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.foundation.data.RemoteState
import app.what.foundation.utils.suspendCall
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.data.remote.HotelsService
import app.what.investtravel.features.hotel.domain.models.HotelAction
import app.what.investtravel.features.hotel.domain.models.HotelEvent
import app.what.investtravel.features.hotel.domain.models.HotelState
import app.what.investtravel.features.travel.presentation.pages.getCity
import app.what.investtravel.features.travel.presentation.pages.getLocation
import com.google.android.gms.location.FusedLocationProviderClient


class HotelController(
    private val hotelService: HotelsService,
    private val settings: AppValues,
    private val geocoder: Geocoder,
    val fusedLocationClient: FusedLocationProviderClient
) : UIController<HotelState, HotelAction, HotelEvent>(
    HotelState()
) {
    init {
        settings.authToken.set("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxMywicm9sZV9pZCI6MCwiZXhwIjoxNzYxNzcyNzU4fQ.6GqV4BVDlFIBy34HB6ISy-vnuwxJ7cy0X4aZodvHAHo")
    }

    override fun obtainEvent(viewEvent: HotelEvent) = when (viewEvent) {
        HotelEvent.Init -> fetchHotels()
    }

    fun fetchHotels() {
        getLocation(fusedLocationClient) { lat, lon ->
            val city = geocoder.getCity(lat, lon)

            suspendCall(viewModelScope) {
                safeUpdateState { copy(hotelsFetchState = RemoteState.Loading) }
                try {
                    val hotels = hotelService.searchHotels(
                        city = city ?: ""
                    ).getOrNull()!!

                    safeUpdateState {
                        copy(
                            hotelsFetchState = RemoteState.Success,
                            hotels = hotels.hotels
                        )
                    }
                } catch (e: Exception) {
                    safeUpdateState {
                        copy(
                            hotelsFetchState = RemoteState.Error(e)
                        )
                    }
                }
            }
        }
    }
}