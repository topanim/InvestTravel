package app.what.investtravel.features.assistant.navigation

import app.what.navigation.core.NavProvider
import app.what.navigation.core.Registry
import app.what.navigation.core.register
import app.what.investtravel.features.assistant.presentation.AssistantFeature
import kotlinx.serialization.Serializable

@Serializable
object AssistantProvider : NavProvider()

val assistantRegistry: Registry = {
    register(AssistantFeature::class)
}


