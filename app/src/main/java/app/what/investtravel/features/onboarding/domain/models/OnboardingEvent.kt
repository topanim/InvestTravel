package app.what.investtravel.features.onboarding.domain.models

sealed interface OnboardingEvent {
    object Init : OnboardingEvent
    object FinishClicked : OnboardingEvent
}