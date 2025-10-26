package app.what.investtravel.features.assistant.domain

import android.util.Log
import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.investtravel.data.remote.AiService
import app.what.investtravel.data.remote.GenerateCommentRequest
import app.what.investtravel.features.assistant.domain.models.AssistantAction
import app.what.investtravel.features.assistant.domain.models.AssistantEvent
import app.what.investtravel.features.assistant.domain.models.AssistantState
import app.what.investtravel.features.assistant.domain.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class AssistantController : UIController<AssistantState, AssistantAction, AssistantEvent>(
    AssistantState()
) {
    val aiService: AiService by inject(AiService::class.java)
    override fun obtainEvent(viewEvent: AssistantEvent) = when (viewEvent) {
        AssistantEvent.Init -> init()
        AssistantEvent.OnStartChattingClicked -> startChatting()

        is AssistantEvent.OnMessageSendClicked -> sendMessage(viewEvent.message)
    }

    private fun init() {}

    private fun startChatting() = updateState {
        copy(
            isChatStarted = true,
            messages = listOf(
                Message(
                    authorIsMe = false,
                    content = "Привет! Чем я могу помочь?"
                )
            )
        )
    }

    private fun sendMessage(message: String) {
        updateState {
            copy(
                isAiThinking = true,
                messages = messages + Message(
                    authorIsMe = true,
                    content = message
                )
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                aiService.generateComment(GenerateCommentRequest(message))
                    .onSuccess { response ->
                        Log.d("AssistantController", "AI response received: ${response.comment}")
                        safeUpdateState {
                            copy(
                                messages = messages + Message(
                                    authorIsMe = false,
                                    content = response.comment
                                ),
                                isAiThinking = false
                            )
                        }
                    }
                    .onFailure { error ->
                        Log.e("AssistantController", "Error generating AI response", error)
                        safeUpdateState {
                            copy(
                                messages = messages + Message(
                                    authorIsMe = false,
                                    content = "Извините, произошла ошибка. Попробуйте еще раз."
                                ),
                                isAiThinking = false
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("AssistantController", "Exception generating AI response", e)
                safeUpdateState {
                    copy(
                        messages = messages + Message(
                            authorIsMe = false,
                            content = "Произошла ошибка при обработке запроса. Попробуйте еще раз."
                        ),
                        isAiThinking = false
                    )
                }
            }
        }
    }
}
