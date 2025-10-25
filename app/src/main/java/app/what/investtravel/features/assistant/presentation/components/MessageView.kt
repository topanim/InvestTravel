package app.what.investtravel.features.assistant.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.what.investtravel.features.assistant.domain.models.Message

@Composable
internal fun MessageView(
    modifier: Modifier = Modifier,
    message: Message
) = Box(
    modifier = modifier.fillMaxWidth()
) {
    Row(
        modifier = Modifier
            .clip(shapes.medium)
            .background(
                color = if (message.authorIsMe) colorScheme.secondary
                else colorScheme.primary
            )
            .align(
                if (message.authorIsMe) Alignment.CenterEnd
                else Alignment.CenterStart
            ),
    ) {
        Text(
            text = message.content,
            style = typography.bodyMedium,
            color = if (message.authorIsMe) colorScheme.onSecondary
            else colorScheme.onPrimary,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 12.dp
            )
        )
    }
}