package app.what.investtravel.features.profile.navigation

import app.what.investtravel.features.profile.ProfileFeature
import app.what.navigation.core.NavProvider
import app.what.navigation.core.Registry
import app.what.navigation.core.register
import kotlinx.serialization.Serializable

@Serializable
object ProfileProvider : NavProvider()

val profileRegistry: Registry = {
    register(ProfileFeature::class)
}

