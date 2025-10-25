package app.what.investtravel.features.travel.presentation

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.what.foundation.core.Listener
import app.what.foundation.ui.Gap
import app.what.foundation.ui.VerticalGap
import app.what.foundation.ui.bclick
import app.what.foundation.ui.controllers.rememberSheetController
import app.what.investtravel.features.main.NavBarController
import app.what.investtravel.features.travel.domain.models.Travel
import app.what.investtravel.features.travel.domain.models.TravelEvent
import app.what.investtravel.features.travel.domain.models.TravelObject
import app.what.investtravel.features.travel.domain.models.TravelState
import app.what.investtravel.features.travel.presentation.pages.TravelSheet
import app.what.investtravel.ui.components.MapKitController
import app.what.investtravel.ui.components.YandexMapKit
import app.what.investtravel.utils.TextSpeaker
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelView(
    state: State<TravelState>,
    controller: MapKitController,
    listener: Listener<TravelEvent>
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sheetState = rememberSheetController()

    LaunchedEffect(drawerState.currentValue) {
        NavBarController.setVisibility(drawerState.isClosed)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(),
                drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                drawerContainerColor = colorScheme.surfaceContainerLow
            ) {
                TravelSheet(state, listener)
            }
        },
        content = {
            Box(Modifier.fillMaxSize()) {
                YandexMapKit(
                    controller = controller,
                    modifier = Modifier.fillMaxSize()
                )

                // Кнопка открытия drawer
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart),
                    containerColor = colorScheme.primaryContainer,
                    contentColor = colorScheme.onPrimaryContainer
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Open menu",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }
    )
    if(!sheetState.opened){
        sheetState.close()
        listener.invoke(TravelEvent.ShowSheet)
    }
    if (state.value.showSheet) {
        sheetState.open {
            AiCommentLayout(state.value.aiComment)
        }
    }
}

@Composable
fun AiCommentLayout(comment: String) {
    val context = LocalContext.current
    val textSpeaker = remember { TextSpeaker(context) }
    DisposableEffect(Unit) {
        onDispose {
            textSpeaker.shutdown()
        }
    }
    if (comment.isEmpty()) {
        CircularProgressIndicator(modifier = Modifier.size(150.dp))
    } else {
        LazyColumn(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 16.dp),
        ) {
            item {
                Button(
                    {
                        textSpeaker.speak(comment.replace("*", ""))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row() {
                        Icon(
                            Icons.Default.PlayArrow, "",
                            modifier = Modifier.size(40.dp)
                        )
                        Gap(10)
                        Text("Прослушать", fontSize = 30.sp)
                    }

                }
            }
            item {
                VerticalGap(15)
                Text(comment, fontSize = 20.sp, textAlign = TextAlign.Start)

            }
        }

    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TravelItem(item: Travel, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(shapes.medium)
            .background(colorScheme.surfaceContainerHigh)
            .bclick(block = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Иконка маршрута
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Gap(12)

                // Основная информация
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        item.name,
                        style = typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    VerticalGap(4)

                    // Статистика маршрута
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Дистанция
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Gap(4)
                            Text(
                                "${String.format("%.1f", item.distance / 1000)} км",
                                style = typography.bodyMedium,
                                color = colorScheme.secondary
                            )
                        }

                        Gap(16)

                        // Время
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Gap(4)
                            Text(
                                "${item.time.toInt()} мин",
                                style = typography.bodyMedium,
                                color = colorScheme.secondary
                            )
                        }

                        Gap(16)

                        // Количество объектов
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Gap(4)
                            Text(
                                "${item.objects.size}",
                                style = typography.bodyMedium,
                                color = colorScheme.secondary
                            )
                        }
                    }
                }

                // Стрелка перехода
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Open details",
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Прогресс-бар (опционально)
            LinearProgressIndicator(
                progress = { item.objects.filter { it.checked }.size / item.objects.size.toFloat() }, // Здесь можно добавить реальный прогресс
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(4.dp)
                    .clip(CircleShape),
                color = colorScheme.primary,
                trackColor = colorScheme.surfaceVariant
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TravelObjectItem(item: TravelObject, setToAi: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer)
    ) {
        Column {
            // Баннер
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(shapes.medium),
                contentAlignment = Alignment.BottomStart
            ) {
                AsyncImage(
                    model = item.bannerUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Градиент поверх изображения
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.4f)
                                ),
                                startY = 0.6f
                            )
                        )
                )

                // Тип объекта на баннере
                Text(
                    item.type.uppercase(),
                    style = typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                        .background(
                            color = colorScheme.primary.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Контент
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Checkbox(
                    checked = item.checked,
                    onCheckedChange = { item.checked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorScheme.primary,
                        uncheckedColor = colorScheme.outline
                    )
                )

                VerticalGap(12)

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        item.name,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    VerticalGap(4)

                    // Дополнительная информация
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = null,
                            tint = colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        VerticalGap(4)
                        Text(
                            "${String.format("%.4f", item.lat)}, ${
                                String.format(
                                    "%.4f",
                                    item.lon
                                )
                            }",
                            style = typography.bodySmall,
                            color = colorScheme.secondary
                        )
                    }
                }

                // Кнопка навигации
                IconButton(
                    onClick = { setToAi() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = colorScheme.secondaryContainer,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Navigate to ${item.name}",
                        tint = colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}