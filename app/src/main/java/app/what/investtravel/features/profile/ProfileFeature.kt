package app.what.investtravel.features.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.what.foundation.core.Feature
import app.what.investtravel.features.main.navigation.MainProvider
import app.what.navigation.core.NavComponent
import app.what.navigation.core.rememberNavigator
import app.what.investtravel.features.onboarding.presentation.OnboardingView
import app.what.investtravel.features.profile.domain.ProfileController
import app.what.investtravel.features.profile.domain.models.ProfileAction
import app.what.investtravel.features.profile.domain.models.ProfileEvent
import app.what.investtravel.features.profile.navigation.ProfileProvider
import app.what.investtravel.features.profile.presentation.ProfileView
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProfileFeature(
    override val data: ProfileProvider
) : Feature<ProfileController, ProfileEvent>(),
    NavComponent<ProfileProvider>,
    KoinComponent {
    override val controller: ProfileController by inject()

    @Composable
    override fun content(modifier: Modifier) = Column(
        modifier.fillMaxSize()
    ) {
        val viewState by controller.collectStates()
        val viewAction by controller.collectActions()
        val navigator = rememberNavigator()

        LaunchedEffect(Unit) {
            listener(ProfileEvent.Init)
        }

        ProfileView(viewState, listener)
    }
}