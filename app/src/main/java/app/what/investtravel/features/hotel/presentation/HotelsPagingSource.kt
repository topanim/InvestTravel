package app.what.investtravel.features.hotel.presentation

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.what.investtravel.data.remote.HotelResponse
import app.what.investtravel.data.remote.HotelsService
import app.what.investtravel.features.hotel.domain.models.HotelFilters

class HotelsPagingSource(
    private val hotelsService: HotelsService,
    private val filters: HotelFilters
): PagingSource<Int, HotelResponse>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HotelResponse> {
        return try {
            val page = params.key ?: 1
            val result = hotelsService.searchHotels(
                checkIn = filters.checkIn,
                checkOut = filters.checkOut,
                guests = filters.guests,
                rooms = filters.rooms,
                minPrice = filters.minPrice,
                maxPrice = filters.maxPrice,
                stars = filters.stars,
                city = filters.city,
                page = page,
            )
            val response = result.getOrNull()
            val hotels = response?.hotels ?: emptyList()
            LoadResult.Page(
                data = hotels,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (hotels.isEmpty()) null else page.plus(1),
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, HotelResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}