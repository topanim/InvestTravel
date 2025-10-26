package app.what.investtravel

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.features.main.navigation.MainProvider
import app.what.investtravel.features.main.navigation.mainRegistry
import app.what.investtravel.features.onboarding.navigation.onboardingRegistry
import app.what.investtravel.ui.theme.AppTheme
import app.what.navigation.core.NavigationHost
import app.what.navigation.core.ProvideGlobalDialog
import app.what.navigation.core.ProvideGlobalSheet
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
            settings.authToken.set("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxMywicm9sZV9pZCI6MCwiZXhwIjoxNzYxNzcyNzU4fQ.6GqV4BVDlFIBy34HB6ISy-vnuwxJ7cy0X4aZodvHAHo")
            AppTheme(settings) {
                ProvideGlobalDialog {
                    ProvideGlobalSheet {
                        NavigationHost(
                            start =// if (settings.isFirstLaunch.get()!!) OnboardingProvider
                                MainProvider
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
