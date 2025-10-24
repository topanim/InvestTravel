package app.what.investtravel.features.someFeature

import androidx.navigation.compose.composable
import app.what.navigation.core.NavProvider
import app.what.navigation.core.Registry
import kotlinx.serialization.Serializable

@Serializable
object SomeFeatureProvider : NavProvider()

val someFeatureRegistry: Registry = {
    composable<SomeFeatureProvider> {
         SomeFeature()
    }
}