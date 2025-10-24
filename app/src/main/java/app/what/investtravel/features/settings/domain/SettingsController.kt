package app.what.investtravel.features.settings.domain

import app.what.foundation.core.UIController
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.features.settings.domain.models.SettingsAction
import app.what.investtravel.features.settings.domain.models.SettingsEvent
import app.what.investtravel.features.settings.domain.models.SettingsState
import app.what.investtravel.utils.AppUtils

class SettingsController(
    private val settings: AppValues,
    private val appUtils: AppUtils
) : UIController<SettingsState, SettingsAction, SettingsEvent>(
    SettingsState()
) {
    override fun obtainEvent(viewEvent: SettingsEvent) {}
}