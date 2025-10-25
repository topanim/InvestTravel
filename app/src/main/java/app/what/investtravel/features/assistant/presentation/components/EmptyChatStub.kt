package app.what.investtravel.features.assistant.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.what.investtravel.ui.components.Fallback

@Composable
internal fun EmptyChatStub(
    modifier: Modifier = Modifier,
    onStart: () -> Unit
) = Fallback(
    "Это - ваш личный нейросетевой ассистент-эколог",
    modifier, action = "Начать диалог" to onStart
)