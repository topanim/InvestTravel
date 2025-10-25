package app.what.investtravel.features.hotel.navigation

import app.what.investtravel.features.hotel.HotelFeature
import app.what.navigation.core.NavProvider
import app.what.navigation.core.Registry
import app.what.navigation.core.register
import kotlinx.serialization.Serializable

@Serializable
object HotelProvider : NavProvider()

val hotelRegistry: Registry = {
    register(HotelFeature::class)
}

