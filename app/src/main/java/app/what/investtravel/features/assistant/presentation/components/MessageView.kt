package app.what.investtravel.features.assistant.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.what.investtravel.features.assistant.domain.models.Message
import java.util.regex.Pattern

@Composable
internal fun MessageView(
    modifier: Modifier = Modifier,
    message: Message
) {
    val context = LocalContext.current
    val text = message.content

    Box(
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
            if (!message.authorIsMe) {
                // Для ответов AI используем markdown и кликабельные ссылки
                val annotatedString = buildAnnotatedString {
                    parseMarkdownAndLinks(text, colorScheme.onPrimary)
                }

                ClickableText(
                    text = annotatedString,
                    style = typography.bodyMedium.copy(color = colorScheme.onPrimary),
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 12.dp
                    ),
                    onClick = { offset ->
                        // Пытаемся найти ссылку по позиции клика
                        val urlPattern = Pattern.compile(
                            "(?:(?:https?|ftp)://|www\\.)[^\\s]+",
                            Pattern.CASE_INSENSITIVE
                        )
                        val matcher = urlPattern.matcher(text)

                        while (matcher.find()) {
                            if (offset >= matcher.start() && offset <= matcher.end()) {
                                val url = matcher.group()
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Игнорируем ошибки
                                }
                                return@ClickableText
                            }
                        }
                    }
                )
            } else {
                // Для сообщений пользователя просто текст
                androidx.compose.material3.Text(
                    text = text,
                    style = typography.bodyMedium,
                    color = colorScheme.onSecondary,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 12.dp
                    )
                )
            }
        }
    }
}

private fun AnnotatedString.Builder.parseMarkdownAndLinks(
    text: String,
    defaultColor: androidx.compose.ui.graphics.Color
) {
    val urlPattern = Pattern.compile(
        "(?:(?:https?|ftp)://|www\\.)[^\\s]+",
        Pattern.CASE_INSENSITIVE
    )

    var lastIndex = 0
    val matcher = urlPattern.matcher(text)

    while (matcher.find()) {
        // Добавляем текст до ссылки с markdown
        appendWithMarkdown(text.substring(lastIndex, matcher.start()), defaultColor)

        // Обрабатываем ссылку
        val url = matcher.group()
        val displayUrl = if (url.startsWith("www.")) {
            url
        } else {
            try {
                Uri.parse(url).authority ?: url
            } catch (e: Exception) {
                url
            }
        }

        withStyle(
            style = SpanStyle(
                color = defaultColor.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append(displayUrl)
        }

        lastIndex = matcher.end()
    }

    // Добавляем оставшийся текст
    appendWithMarkdown(text.substring(lastIndex), defaultColor)
}

private fun AnnotatedString.Builder.appendWithMarkdown(
    text: String,
    defaultColor: androidx.compose.ui.graphics.Color
) {
    // Обработка **bold**
    val boldPattern = Pattern.compile("\\*\\*(.+?)\\*\\*")
    var lastIndex = 0
    val boldMatcher = boldPattern.matcher(text)
    val segments = mutableListOf<TextSegment>()

    while (boldMatcher.find()) {
        if (boldMatcher.start() > lastIndex) {
            segments.add(
                TextSegment(
                    text.substring(lastIndex, boldMatcher.start()),
                    isBold = false
                )
            )
        }
        segments.add(
            TextSegment(
                boldMatcher.group(1),
                isBold = true
            )
        )
        lastIndex = boldMatcher.end()
    }

    if (lastIndex < text.length) {
        segments.add(
            TextSegment(
                text.substring(lastIndex),
                isBold = false
            )
        )
    }

    // Если не нашли Markdown, просто добавляем весь текст
    if (segments.isEmpty()) {
        append(text)
        return
    }

    // Строим annotated string из сегментов
    segments.forEach { segment ->
        if (segment.isBold) {
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = defaultColor
                )
            ) {
                append(segment.text)
            }
        } else {
            append(segment.text)
        }
    }
}

private data class TextSegment(
    val text: String,
    val isBold: Boolean
)