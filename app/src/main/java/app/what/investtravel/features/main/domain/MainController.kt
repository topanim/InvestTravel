package app.what.investtravel.features.main.domain

import app.what.foundation.core.UIController
import app.what.investtravel.features.main.domain.models.MainAction
import app.what.investtravel.features.main.domain.models.MainEvent
import app.what.investtravel.features.main.domain.models.MainState

class MainController : UIController<MainState, MainAction, MainEvent>(
    MainState()
) {
    override fun obtainEvent(viewEvent: MainEvent) = when (viewEvent) {
        else -> {}
    }
}