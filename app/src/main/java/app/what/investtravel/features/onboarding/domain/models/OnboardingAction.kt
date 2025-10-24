package app.what.investtravel.features.onboarding.domain.models

sealed interface OnboardingAction {
    object NavigateToMain : OnboardingAction
}