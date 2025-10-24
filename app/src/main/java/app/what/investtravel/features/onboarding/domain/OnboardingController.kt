package app.what.investtravel.features.onboarding.domain

import app.what.foundation.core.UIController
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.features.onboarding.domain.models.OnboardingAction
import app.what.investtravel.features.onboarding.domain.models.OnboardingEvent
import app.what.investtravel.features.onboarding.domain.models.OnboardingState


class OnboardingController(
    private val settings: AppValues
) : UIController<OnboardingState, OnboardingAction, OnboardingEvent>(
    OnboardingState()
) {
    override fun obtainEvent(viewEvent: OnboardingEvent) = when (viewEvent) {
        OnboardingEvent.Init -> {}
        is OnboardingEvent.FinishClicked ->
            institutionAndProviderSelected(viewEvent)
    }

    private fun institutionAndProviderSelected(
        viewEvent: OnboardingEvent.FinishClicked
    ) {
        finishAndGoToMain()
    }

    private fun finishAndGoToMain() {
        settings.isFirstLaunch.set(false)
        setAction(OnboardingAction.NavigateToMain)
    }
}