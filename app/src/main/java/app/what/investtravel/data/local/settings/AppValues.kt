package app.what.investtravel.data.local.settings

import android.content.Context
import androidx.compose.ui.graphics.Color
import app.what.foundation.data.settings.PreferenceStorage
import app.what.foundation.data.settings.types.Named
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable
enum class ThemeType(override val displayName: String) : Named {
    Light("Светлая"),
    Dark("Тёмная"),
    System("Системная")
}

@Serializable
enum class ThemeStyle(override val displayName: String) : Named {
    Default("По умолчанию"),
    Material("Material"),
    CustomColor("Свой цвет")
}

class AppValues(context: Context) {
    private val prefs = context.getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE)
    private val preferencesFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val storage = PreferenceStorage(prefs, preferencesFlow)

    init {
        prefs.registerOnSharedPreferenceChangeListener { _, key ->
            key?.let { preferencesFlow.tryEmit(it) }
        }
    }

    val authToken = storage.createValue("auth_token", null, String.serializer())
    val userName = storage.createValue("user_name", "", String.serializer())
    val isFirstLaunch = storage.createValue("is_first_launch", true, Boolean.serializer())
    val themeType = storage.createValue("theme_type", ThemeType.System, ThemeType.serializer())
    val themeStyle = storage.createValue("theme_style", ThemeStyle.Default, ThemeStyle.serializer())
    val themeColor = storage.createValue("theme_color", Color(0xFF94FF28).value, ULong.serializer())
    val devFeaturesEnabled =
        storage.createValue("dev_features_enabled", false, Boolean.serializer())
}

