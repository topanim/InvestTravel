package app.what.investtravel.features.assistant.domain

import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.foundation.utils.suspendCall
import app.what.investtravel.features.assistant.domain.models.AssistantAction
import app.what.investtravel.features.assistant.domain.models.AssistantEvent
import app.what.investtravel.features.assistant.domain.models.AssistantState
import app.what.investtravel.features.assistant.domain.models.Message
import kotlinx.coroutines.delay

class AssistantController : UIController<AssistantState, AssistantAction, AssistantEvent>(
    AssistantState()
) {
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

        suspendCall(viewModelScope) {
            delay(2000L)
            // TODO: Код запроса
            val questionResult = listOf(
                "Армянский адвокат понял, что дело не из простых, после того как судья пятый раз обыграл его в нарды",
                "На выборах в Национальное Собрание Армении большинство мест получила партия в нарды",
                "Получив второй раз с орбиты на день рождения чётки и нарды, мальчик начал подозревать, что его папа вовсе не космонавт",
                "Выслал Бог Адама и Еву на землю, а там под деревом сидят армяне в нарды играют.\n" +
                        "Адам спрашивает:\n" +
                        "— Бог, кто это?\n" +
                        "Бог отвечает:\n" +
                        "— Не знаю, они до меня здесь были…",

                ).random()
            val newMessage =
                if (questionResult != "") Message(authorIsMe = false, content = questionResult)
                else Message(authorIsMe = false, content = "Произошла ошибка, попробуйте еще раз")

            safeUpdateState {
                copy(
                    messages = messages + newMessage,
                    isAiThinking = false
                )
            }
        }
    }
}
