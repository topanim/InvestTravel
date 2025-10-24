package app.what.investtravel.features.dev.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.what.investtravel.ui.components.Fallback

@Composable
fun FeaturePane() = Fallback(
    text = "В разработке",
    modifier = Modifier.fillMaxSize()
)