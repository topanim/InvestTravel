package app.what.investtravel.features.main.navigation

import app.what.navigation.core.NavProvider
import app.what.navigation.core.Registry
import app.what.navigation.core.register
import app.what.investtravel.features.main.MainFeature
import kotlinx.serialization.Serializable

@Serializable
object MainProvider : NavProvider()

val mainRegistry: Registry = {
    register(MainFeature::class)
}

