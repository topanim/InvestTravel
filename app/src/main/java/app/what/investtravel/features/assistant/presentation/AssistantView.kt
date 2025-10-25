package app.what.investtravel.features.assistant.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.what.foundation.core.Listener
import app.what.foundation.ui.Gap
import app.what.foundation.ui.animations.AnimatedEnter
import app.what.foundation.ui.keyboardAsState
import app.what.foundation.ui.useState
import app.what.investtravel.features.assistant.domain.models.AssistantEvent
import app.what.investtravel.features.assistant.domain.models.AssistantState
import app.what.investtravel.features.assistant.presentation.components.ChatField
import app.what.investtravel.features.assistant.presentation.components.EmptyChatStub
import app.what.investtravel.features.assistant.presentation.components.MessageView

@Composable
internal fun AssistantView(
    state: AssistantState,
    listener: Listener<AssistantEvent>
) {
    val (messageText, setMessageText) = useState(state.messageText)

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (!state.isChatStarted) {
            EmptyChatStub { listener(AssistantEvent.OnStartChattingClicked) }
        } else {
            val keyboardState by keyboardAsState()

            Box(
                Modifier
                    .animateContentSize()
                    .height(if (keyboardState) 20.dp else 120.dp)
            )

            AnimatedEnter {
                Text(
                    "Твой нейро помощник",
                    style = typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 46.sp,
                    color = colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Gap(8)

            AnimatedEnter(delay = 200) {
                Text(
                    "+ спроси у него что угодно!",
                    color = colorScheme.primary,
                    style = typography.bodyMedium,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Gap(16)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                items(state.messages, key = { it.id }) {
                    MessageView(message = it)
                }
            }

            ChatField(
                modifier = Modifier.padding(bottom = 120.dp),
                placeholder = "Напишите что-нибудь...",
                value = messageText,
                onValueChange = setMessageText,
                isIdle = state.isAiThinking,
                onSend = { listener(AssistantEvent.OnMessageSendClicked(it)) }
            )
        }
    }
}
