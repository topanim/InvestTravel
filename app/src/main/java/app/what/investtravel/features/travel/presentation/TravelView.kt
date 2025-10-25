package app.what.investtravel.features.travel.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DrawerValue
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.what.foundation.core.Listener
import app.what.foundation.ui.Gap
import app.what.foundation.ui.VerticalGap
import app.what.foundation.ui.bclick
import app.what.investtravel.features.main.NavBarController
import app.what.investtravel.features.travel.domain.models.Travel
import app.what.investtravel.features.travel.domain.models.TravelEvent
import app.what.investtravel.features.travel.domain.models.TravelObject
import app.what.investtravel.features.travel.domain.models.TravelState
import app.what.investtravel.ui.components.MapKitController
import app.what.investtravel.ui.components.YandexMapKit
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun TravelView(
    state: State<TravelState>,
    controller: MapKitController,
    listener: Listener<TravelEvent>
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
fun TravelObjectItem(item: TravelObject) {
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
                    onClick = { /* Навигация к объекту на карте */ },
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

@Composable
fun TravelSheet(
    state: State<TravelState>,
    listener: Listener<TravelEvent>
) {
    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(state.value.selectedTravel) {
        if (state.value.selectedTravel != null) pagerState.animateScrollToPage(1)
        else pagerState.animateScrollToPage(0)
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                when (pagerState.currentPage) {
                    0 -> "Путешествия"
                    else -> state.value.selectedTravel?.name ?: "Объекты"
                },
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            // Индикатор страниц
            Row {
                repeat(2) { index ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) {
                                    colorScheme.primary
                                } else {
                                    colorScheme.outline.copy(alpha = 0.3f)
                                }
                            )
                    )
                    if (index < 1) VerticalGap(4)
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> TravelsPage(state, listener)
                1 -> TravelDetailPage(state)
            }
        }

        if (pagerState.currentPage == 1) {
            Button(
                onClick = {
                    listener(TravelEvent.TravelUnselected)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.secondaryContainer,
                    contentColor = colorScheme.onSecondaryContainer
                ),
                shape = shapes.medium
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                VerticalGap(8)
                Text("Назад к путешествиям")
            }
        }
    }
}

@Composable
fun TravelsPage(
    state: State<TravelState>,
    listener: Listener<TravelEvent>
) = LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(vertical = 8.dp)
) {
    items(state.value.travels) { travel ->
        TravelItem(travel) {
            listener(TravelEvent.TravelSelected(travel))
        }
        VerticalGap(8)
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TravelDetailPage(
    state: State<TravelState>
) = LazyColumn(
    modifier = Modifier.fillMaxSize(),
) {
    item {
        // Статистика маршрута
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerHigh)
        ) {
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                // Общая статистика
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "${
                            String.format(
                                "%.1f",
                                state.value.selectedTravel?.distance?.div(1000) ?: 0.0
                            )
                        } км",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                    Text(
                        "Дистанция",
                        style = typography.labelSmall,
                        color = colorScheme.secondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "${state.value.selectedTravel?.time?.toInt() ?: 0} мин",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                    Text(
                        "Время",
                        style = typography.labelSmall,
                        color = colorScheme.secondary
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "${state.value.selectedTravel?.objects?.size ?: 0}",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                    Text(
                        "Объекты",
                        style = typography.labelSmall,
                        color = colorScheme.secondary
                    )
                }
            }
        }

        VerticalGap(16)

        Text(
            "Объекты маршрута",
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        VerticalGap(8)
    }

    items(state.value.selectedTravel?.objects ?: emptyList()) { travelObject ->
        TravelObjectItem(travelObject)
        VerticalGap(8)
    }
}
