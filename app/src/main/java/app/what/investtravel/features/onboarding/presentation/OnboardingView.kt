package app.what.investtravel.features.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.what.foundation.core.Listener
import app.what.foundation.ui.keyboardAsState
import app.what.investtravel.features.onboarding.domain.models.OnboardingEvent
import app.what.investtravel.features.onboarding.domain.models.OnboardingState

@Composable
fun OnboardingView(
    state: OnboardingState,
    listener: Listener<OnboardingEvent>
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .background(colorScheme.background)
) {
    val keyboardState by keyboardAsState()
    Button(
        onClick = {
            listener(OnboardingEvent.FinishClicked)
        }
    ) {
        Text("Завершить онбоардинг")
    }
}
