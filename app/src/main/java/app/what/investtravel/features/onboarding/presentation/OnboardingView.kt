package app.what.investtravel.features.onboarding.presentation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.what.foundation.core.Listener
import app.what.investtravel.features.onboarding.domain.models.OnboardingEvent
import app.what.investtravel.features.onboarding.domain.models.OnboardingState

@Composable
fun OnboardingView(
    state: OnboardingState,
    listener: Listener<OnboardingEvent>
) {
    Button(onClick = {
        listener(OnboardingEvent.FinishClicked)
    }) {
        Text("Завершить онбординг")
    }
}
