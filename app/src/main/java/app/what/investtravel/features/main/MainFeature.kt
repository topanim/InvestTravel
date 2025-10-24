package app.what.investtravel.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.what.foundation.core.Feature
import app.what.foundation.ui.animations.AnimatedEnter
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.features.dev.navigation.DevProvider
import app.what.investtravel.features.dev.navigation.devRegistry
import app.what.investtravel.features.main.domain.MainController
import app.what.investtravel.features.main.domain.models.MainEvent
import app.what.investtravel.features.main.navigation.MainProvider
import app.what.investtravel.features.settings.navigation.SettingsProvider
import app.what.investtravel.features.settings.navigation.settingsRegistry
import app.what.investtravel.features.someFeature.SomeFeatureProvider
import app.what.investtravel.features.someFeature.someFeatureRegistry
import app.what.investtravel.ui.theme.icons.WHATIcons
import app.what.investtravel.ui.theme.icons.filled.Code
import app.what.navigation.core.NavComponent
import app.what.navigation.core.NavigationHost
import app.what.navigation.core.Registry
import app.what.navigation.core.bottom_navigation.BottomNavBar
import app.what.navigation.core.bottom_navigation.NavAction
import app.what.navigation.core.bottom_navigation.navItem
import app.what.navigation.core.rememberHostNavigator
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainFeature(
    override val data: MainProvider
) : Feature<MainController, MainEvent>(),
    NavComponent<MainProvider>,
    KoinComponent {

    override val controller: MainController by inject()

    private companion object {
        val children = listOf(
            navItem("ХЗ", Icons.Default.DateRange, SomeFeatureProvider),
            navItem("Настройки", Icons.Default.Settings, SettingsProvider)
        )

        val childrenRegistry: Registry = {
            settingsRegistry()
            someFeatureRegistry()
            devRegistry()
        }
    }

    @Composable
    override fun content(modifier: Modifier) {
        val navigator = rememberHostNavigator()
        val appValues: AppValues = koinInject()
        val devFeaturesEnabled by appValues.devFeaturesEnabled.collect()

        Box(
            Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {

            NavigationHost(
                navigator = navigator,
                modifier = modifier.windowInsetsPadding(
                    WindowInsets.systemBars.only(
                        WindowInsetsSides.Top
                    )
                ),
                start = SomeFeatureProvider,
                registry = childrenRegistry
            )

            // Анимированный BottomNavBar
            AnimatedEnter(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BottomNavBar(
                    navigator = navigator,
                    screens = children,
                ) {
                    if (!devFeaturesEnabled!!) null
                    else NavAction("Для разработчиков", WHATIcons.Code) {
                        navigator.c.navigate(DevProvider)
                    }
                }
            }
        }
    }
}