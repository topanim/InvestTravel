package app.what.investtravel.features.assistant.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.ButtonMode
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import app.what.foundation.ui.Show
import app.what.investtravel.ui.components.SearchTextField
import app.what.investtravel.ui.theme.icons.WHATIcons

@Composable
internal fun ChatField(
    modifier: Modifier = Modifier,
    value: String,
    isIdle: Boolean,
    placeholder: String = "",
    onValueChange: (String) -> Unit,
    onSend: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val onSubmit = { onSend(value); onValueChange("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
    ) {
        SearchTextField(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            value = value,
            onValueChange = onValueChange,
            actions = KeyboardActions {
                if (isIdle) return@KeyboardActions
                focusManager.clearFocus()
                if (value.isNotEmpty()) onSubmit()
            },
            placeholder = placeholder,
            trailing = {
                IconButton(
                    enabled = value.isNotEmpty() && !isIdle,
                    onClick = onSubmit
                ) {
                    Icons.Filled.Send.Show(color = colorScheme.primary)
                }
            }
        )
    }
}