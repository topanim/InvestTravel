package app.what.investtravel.features.travel.presentation.pages

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
    var newTravel by remember { mutableStateOf(false) }

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
                    else -> if(newTravel) "Новое путешевствие" else state.value.selectedTravel?.name ?: "Объекты"
                },
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary,
            )
            if(pagerState.currentPage == 0) {
                Button({
                    scope.launch {
                        newTravel = true
                        pagerState.animateScrollToPage(1)
                    }
                }) {
                    Text("Создать путешевствие")
                }
            }

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
                1 -> if(newTravel) TravelCreatePage(
                    onSaveTravel = {
                        scope.launch {
                            listener.invoke(TravelEvent.SaveTravel(it))
                            newTravel = false
                            pagerState.animateScrollToPage(0)
                        }
                    }
                ) else TravelDetailPage(state,listener)
            }
        }

        if (pagerState.currentPage == 1) {
            Button(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                        listener(TravelEvent.TravelUnselected)
                        newTravel = false
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
    state: State<TravelState>,
    listener:Listener<TravelEvent>
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
        TravelObjectItem(travelObject){
            listener.invoke(TravelEvent.SetToAi(travelObject))
        }
        VerticalGap(8)
    }
}
