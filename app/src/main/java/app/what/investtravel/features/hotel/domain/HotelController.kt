package app.what.investtravel.features.hotel.domain

import android.location.Geocoder
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.what.foundation.core.UIController
import app.what.foundation.data.RemoteState
import app.what.foundation.utils.suspendCall
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.data.remote.HotelResponse
import app.what.investtravel.data.remote.HotelsService
import app.what.investtravel.features.hotel.domain.models.HotelAction
import app.what.investtravel.features.hotel.domain.models.HotelEvent
import app.what.investtravel.features.hotel.domain.models.HotelFilters
import app.what.investtravel.features.hotel.domain.models.HotelState
import app.what.investtravel.features.hotel.presentation.HotelsPagingSource
import app.what.investtravel.features.travel.presentation.pages.getCity
import app.what.investtravel.features.travel.presentation.pages.getLocation
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect


class HotelController(
    private val hotelService: HotelsService,
    settings: AppValues,
    private val geocoder: Geocoder,
    val fusedLocationClient: FusedLocationProviderClient,
) : UIController<HotelState, HotelAction, HotelEvent>(
    HotelState()
) {
    init {
        settings.authToken.set("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxMywicm9sZV9pZCI6MCwiZXhwIjoxNzYxNzcyNzU4fQ.6GqV4BVDlFIBy34HB6ISy-vnuwxJ7cy0X4aZodvHAHo")
    }

    override fun obtainEvent(viewEvent: HotelEvent) = when (viewEvent) {
        HotelEvent.Init -> fetchHotels()
        is HotelEvent.HotelSelected -> {}
        HotelEvent.LoadNextPage -> {}
        HotelEvent.Refresh -> fetchHotels()
        is HotelEvent.UpdateFilters -> updateState { copy(filters = viewEvent.filters) }
    }

    fun fetchHotels() {
        getLocation(fusedLocationClient) { lat, lon ->
            val city = geocoder.getCity(lat, lon)

            suspendCall(viewModelScope) {
                safeUpdateState { copy(hotelsFetchState = RemoteState.Loading) }
                try {
                    val pager = Pager(
                        config = PagingConfig(
                            pageSize = 10,
                            enablePlaceholders = false,
                            initialLoadSize = 10
                        ),
                        pagingSourceFactory = {HotelsPagingSource(hotelService, viewState.filters)}
                    ).flow.cachedIn(viewModelScope)
                    safeUpdateState {
                        copy(
                            hotelsFetchState = RemoteState.Success,
                            hotels = pager
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