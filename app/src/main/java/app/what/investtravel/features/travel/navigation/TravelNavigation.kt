package app.what.investtravel.features.travel.navigation

import app.what.navigation.core.NavProvider
import app.what.navigation.core.Registry
import app.what.navigation.core.register
import app.what.investtravel.features.travel.TravelFeature
import kotlinx.serialization.Serializable

@Serializable
object TravelProvider : NavProvider()

val travelRegistry: Registry = {
    register(TravelFeature::class)
}

