package app.what.investtravel.features.travel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.what.foundation.core.Feature
import app.what.foundation.ui.theme.LocalThemeIsDark
import app.what.investtravel.features.travel.domain.TravelController
import app.what.investtravel.features.travel.domain.models.TravelEvent
import app.what.investtravel.features.travel.navigation.TravelProvider
import app.what.investtravel.features.travel.presentation.TravelView
import app.what.navigation.core.NavComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TravelFeature(
    override val data: TravelProvider
) : Feature<TravelController, TravelEvent>(),
    NavComponent<TravelProvider>,
    KoinComponent {
    override val controller: TravelController by inject()

    @Composable
    override fun content(modifier: Modifier) {
        val state = controller.collectStates()
        val action by controller.collectActions()
        val themeIsDark = LocalThemeIsDark.current

        LaunchedEffect(themeIsDark) {
            controller.mapController.setNightMode(themeIsDark)
        }

        TravelView(state, controller.mapController, listener)
    }
}