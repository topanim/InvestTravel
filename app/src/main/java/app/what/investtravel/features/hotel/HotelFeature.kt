package app.what.investtravel.features.hotel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.what.foundation.core.Feature
import app.what.investtravel.features.hotel.domain.HotelController
import app.what.investtravel.features.hotel.domain.models.HotelEvent
import app.what.investtravel.features.hotel.navigation.HotelProvider
import app.what.investtravel.features.hotel.presentation.HotelView
import app.what.navigation.core.NavComponent
import app.what.navigation.core.rememberNavigator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HotelFeature(
    override val data: HotelProvider
) : Feature<HotelController, HotelEvent>(),
    NavComponent<HotelProvider>,
    KoinComponent {
    override val controller: HotelController by inject()

    @Composable
    override fun content(modifier: Modifier) = Column(
        modifier.fillMaxSize()
    ) {
        val viewState by controller.collectStates()
        val viewAction by controller.collectActions()
        val navigator = rememberNavigator()

        LaunchedEffect(Unit) {
            listener(HotelEvent.Init)
        }

        HotelView(viewState, listener)
    }
}