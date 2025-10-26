package app.what.investtravel.features.travel.presentation.pages

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.what.foundation.core.Listener
import app.what.foundation.ui.VerticalGap
import app.what.investtravel.data.remote.AiRouteRequest
import app.what.investtravel.features.travel.domain.models.TravelEvent
import app.what.investtravel.features.travel.domain.models.TravelState
import app.what.investtravel.features.travel.presentation.TravelItem
import app.what.investtravel.features.travel.presentation.TravelObjectItem
import kotlinx.coroutines.launch


@Composable
fun TravelSheet(
    state: State<TravelState>,
    listener: Listener<TravelEvent>
) {
    val pagerState = rememberPagerState { 3 }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.value.selectedTravel) {
        if (state.value.selectedTravel != null) pagerState.animateScrollToPage(1)
        else pagerState.animateScrollToPage(0)
    }
    

    Column(Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                when (pagerState.currentPage) {
                    0 -> "Путешествия"
                    1 -> state.value.selectedTravel?.name ?: "Объекты"
                    2 -> "Новое путешествие"
                    else -> "Путешествия"
                },
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
            )
            if (pagerState.currentPage == 0) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Создать путешествие",
                        tint = colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Индикатор страниц
            Row {
                repeat(3) { index ->
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
                1 -> TravelDetailPage(state, listener)
                2 -> TravelCreatePage(
                    onSaveTravel = {
                        scope.launch {
                            listener.invoke(TravelEvent.SaveTravel(it))
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    onSaveAiTravel = {
                        scope.launch {
                            listener.invoke(TravelEvent.SaveAiTravel(it))
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    isLoading = state.value.isCreatingRoute
                )
            }
        }

        if (pagerState.currentPage == 1 || pagerState.currentPage == 2) {
            Button(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                        listener(TravelEvent.TravelUnselected)
                    }
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
) {
    // Загружаем маршруты только если состояние Idle
    LaunchedEffect(state.value.travelsFetchState) {
        Log.d("TravelsPage", "LaunchedEffect triggered, travelsFetchState: ${state.value.travelsFetchState}")
        if (state.value.travelsFetchState == app.what.foundation.data.RemoteState.Idle) {
            Log.d("TravelsPage", "Fetching travels...")
            listener(TravelEvent.FetchTravels)
        }
    }
    
    LaunchedEffect(state.value.travelsFetchState, state.value.travels.size) {
        Log.d("TravelsPage", "travelsFetchState: ${state.value.travelsFetchState}, travels count: ${state.value.travels.size}")
    }
    
    when {
        state.value.travelsFetchState == app.what.foundation.data.RemoteState.Loading && state.value.travels.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp,
                        color = colorScheme.primary
                    )
                    Text(
                        "Загрузка маршрутов...",
                        style = typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        state.value.travels.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Нет сохраненных маршрутов",
                    style = typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.value.travels) { travel ->
                    TravelItem(
                        travel,
                        onClick = {
                            listener(TravelEvent.TravelSelected(travel, "driving"))
                        },
                        onDelete = {
                            listener(TravelEvent.DeleteTravel(travel))
                        }
                    )
                }
            }
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
fun TravelDetailPage(
    state: State<TravelState>,
    listener: Listener<TravelEvent>
) = LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    item {
        // Статистика маршрута
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Дистанция
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "${String.format("%.1f", state.value.selectedTravel?.distance?.div(1000f) ?: 0.0)} км",
                            style = typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Дистанция",
                            style = typography.bodySmall,
                            color = colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }

                    // Время
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "${state.value.selectedTravel?.time?.div(60)?.toInt() ?: 0} ч",
                            style = typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Время",
                            style = typography.bodySmall,
                            color = colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }

                    // Объекты
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "${state.value.selectedTravel?.objects?.size ?: 0}",
                            style = typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Объекты",
                            style = typography.bodySmall,
                            color = colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        Text(
            "Объекты маршрута",
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )
    }

    items(state.value.selectedTravel?.objects ?: emptyList()) { travelObject ->
        TravelObjectItem(
            travelObject,
            setToAi = {
                listener(TravelEvent.SetToAi(travelObject))
            },
            onCheckedChange = { isChecked ->
                // Обновляем статус объекта в базе данных
                val objectIndex = state.value.selectedTravel?.objects?.indexOf(travelObject) ?: 0
                listener(TravelEvent.UpdateObjectChecked(state.value.selectedTravel!!, objectIndex, isChecked))
            }
        )
        VerticalGap(8)
    }
}
