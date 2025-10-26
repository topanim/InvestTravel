package app.what.investtravel.features.auth

import app.what.foundation.core.Feature
import app.what.foundation.core.UIComponent
import app.what.investtravel.features.auth.presentation.AuthScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.what.investtravel.features.auth.domain.AuthController
import app.what.investtravel.features.auth.domain.models.AuthEvent
import app.what.investtravel.features.auth.navigation.AuthProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import app.what.investtravel.features.main.domain.MainController
import app.what.investtravel.features.main.domain.models.MainEvent
import app.what.investtravel.features.main.navigation.MainProvider
import org.koin.java.KoinJavaComponent.inject
import app.what.navigation.core.NavComponent
import kotlin.getValue


    class AuthFeature(
        override val data: AuthProvider
    ) : Feature<AuthController, AuthEvent>(),
        NavComponent<AuthProvider>,
        KoinComponent {
        override val controller: AuthController by inject()

        @Composable
        override fun content(modifier: Modifier) {
            AuthScreen(
                controller = controller,
                modifier = modifier
            )

        }

    }
