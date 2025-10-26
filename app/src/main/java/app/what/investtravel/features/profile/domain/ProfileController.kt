package app.what.investtravel.features.profile.domain

import android.util.Log
import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.data.remote.UsersService
import app.what.investtravel.features.profile.domain.models.ProfileAction
import app.what.investtravel.features.profile.domain.models.ProfileEvent
import app.what.investtravel.features.profile.domain.models.ProfileState
import kotlinx.coroutines.launch


class ProfileController(
    private val settings: AppValues,
    private val userService: UsersService
) : UIController<ProfileState, ProfileAction, ProfileEvent>(
    ProfileState(userName = settings.userName.get() ?: "")
) {
    override fun obtainEvent(viewEvent: ProfileEvent) = when (viewEvent) {
        is ProfileEvent.UpdateUserName -> {
            updateState { copy(userName = viewEvent.name) }
        }
        is ProfileEvent.Init -> {}
    }
    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch{
            userService.getCurrentUser().onSuccess {
                Log.d("ProfileController", "User name: ${it}")
                updateState { copy(userName = it.login ) }
            }
        }

    }
}