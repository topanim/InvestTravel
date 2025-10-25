package app.what.investtravel.features.assistant.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.what.foundation.core.Feature
import app.what.investtravel.features.assistant.domain.AssistantController
import app.what.investtravel.features.assistant.domain.models.AssistantEvent
import app.what.investtravel.features.assistant.navigation.AssistantProvider
import app.what.navigation.core.NavComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class AssistantFeature(
    override val data: AssistantProvider
) :
    Feature<AssistantController, AssistantEvent>(),
    NavComponent<AssistantProvider>,
    KoinComponent {
    override val controller: AssistantController by inject()

    @Composable
    override fun content(modifier: Modifier) {
        val state by controller.collectStates()
        val action by controller.collectActions()

        LaunchedEffect(Unit) {
            listener(AssistantEvent.Init)
        }

        AssistantView(state, listener)

        when (action) {
            else -> {}
        }
    }
}
