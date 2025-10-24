package app.what.investtravel

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.features.main.navigation.MainProvider
import app.what.investtravel.features.main.navigation.mainRegistry
import app.what.navigation.core.NavigationHost
import app.what.navigation.core.ProvideGlobalDialog
import app.what.navigation.core.ProvideGlobalSheet
import app.what.investtravel.features.onboarding.navigation.OnboardingProvider
import app.what.investtravel.features.onboarding.navigation.onboardingRegistry
import app.what.investtravel.ui.theme.AppTheme
import com.yandex.mapkit.MapKitFactory
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            // НЕ ПЕРЕМЕЩАТЬ!!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setNavigationBarContrastEnforced(false)
            }

            val settings = koinInject<AppValues>()

            AppTheme(settings) {
                ProvideGlobalDialog {
                    ProvideGlobalSheet {
                        NavigationHost(modifier = Modifier.statusBarsPadding(),
                            start = if (settings.isFirstLaunch.get()!!) OnboardingProvider
                            else MainProvider
                        ) {
                            mainRegistry()
                            onboardingRegistry()
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}
