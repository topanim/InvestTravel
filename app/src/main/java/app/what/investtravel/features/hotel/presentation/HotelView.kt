package app.what.investtravel.features.hotel.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import app.what.foundation.core.Listener
import app.what.foundation.ui.bclick
import app.what.foundation.ui.useState
import app.what.investtravel.R
import app.what.investtravel.data.remote.HotelResponse
import app.what.investtravel.features.hotel.domain.models.HotelEvent
import app.what.investtravel.features.hotel.domain.models.HotelFilters
import app.what.investtravel.features.hotel.domain.models.HotelState
import app.what.investtravel.features.main.NavBarController
import app.what.investtravel.ui.components.MapKitController
import app.what.investtravel.ui.components.YandexMapKit
import coil3.compose.AsyncImage
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.image.ImageProvider

@Composable
fun HotelView(
    state: HotelState,
    listener: Listener<HotelEvent>
) {
    var selectedHotel: HotelResponse? by useState(null)
    val pagerState = rememberPagerState(pageCount = { 2 })
    var showFilters by useState(false)
    var showBookingDialog by useState(false)
    val hotelsFlow = state.hotels.collectAsLazyPagingItems()

    LaunchedEffect(selectedHotel) {
        NavBarController.setVisibility(selectedHotel == null)
        if (selectedHotel != null) pagerState.animateScrollToPage(1)
        else pagerState.animateScrollToPage(0)
    }

    // Диалог фильтров
    if (showFilters) {
        HotelFiltersDialog(
            filters = state.filters,
            onDismiss = { showFilters = false },
            onApply = { newFilters ->
                listener(HotelEvent.UpdateFilters(newFilters))
                showFilters = false
            }
        )
    }

    // Диалог бронирования
    if (showBookingDialog && selectedHotel != null) {
        BookingDialog(
            hotel = selectedHotel!!,
            onDismiss = { showBookingDialog = false },
            onConfirm = {
                showBookingDialog = false
                // Здесь можно добавить логику обработки бронирования
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> HotelsListScreen(
                    state = state,
                    hotelsFlow = hotelsFlow,
                    onHotelSelected = { selectedHotel = it },
                    onShowFilters = { showFilters = true },
                    onRefresh = { listener(HotelEvent.Refresh) },
                    listener
                )

                1 -> HotelDetailScreen(
                    hotel = selectedHotel,
                    onBack = { selectedHotel = null },
                    onBookClick = { showBookingDialog = true }
                )
            }
        }
    }
}

// Функция для проверки разрешения
private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

// Функция для запроса разрешения
private fun requestLocationPermission(
    context: Context,
    onResult: (Boolean) -> Unit
) {
    if (context is ComponentActivity) {
        val permissionLauncher = context.activityResultRegistry
            .register(
                "location_permission",
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                onResult(granted)
            }

        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

@Composable
fun HotelsListScreen(
    state: HotelState,
    hotelsFlow: LazyPagingItems<HotelResponse>,
    onHotelSelected: (HotelResponse) -> Unit,
    onShowFilters: () -> Unit,
    onRefresh: () -> Unit,
    listener: Listener<HotelEvent>,
) {
    // Нативный запрос разрешений
    var locationPermissionGranted by useState(false)
    val context = LocalContext.current

    // Проверяем разрешение при запуске
    LaunchedEffect(Unit) {
        locationPermissionGranted = hasLocationPermission(context)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()) {
        // Хедер с фильтрами
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Text(
                "Отели",
                style = typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )

            IconButton(onClick = onShowFilters) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Фильтры",
                    tint = colorScheme.primary
                )
            }
        }

        // Карта с отелями
        HotelsMapView(
            hotels = hotelsFlow.itemSnapshotList.items,
            onHotelSelected = onHotelSelected,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Баннер геолокации если нет разрешения
        if (!locationPermissionGranted) {
            LocationPermissionBanner(
                onRequestPermission = {
                    requestLocationPermission(context) { granted ->
                        locationPermissionGranted = granted
                        if (granted) {
                            // Можно обновить данные с учетом местоположения
                            listener(HotelEvent.Refresh)
                        }
                    }
                }
            )
        }

        // Примененные фильтры
        AppliedFiltersChips(state.filters) {
            listener(HotelEvent.UpdateFilters(HotelFilters())) // Сброс фильтров
        }

        // Индикатор загрузки при первоначальном поиске отелей
        if (state.hotelsFetchState == app.what.foundation.data.RemoteState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp,
                        color = colorScheme.primary
                    )
                    Text(
                        text = "Поиск отелей...",
                        style = typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                PullToRefreshBox(
                    state.isLoading, onRefresh
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorScheme.background),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(hotelsFlow.itemCount) { index ->
                            hotelsFlow[index]?.let {
                                HotelCard(
                                    hotel = it,
                                    onHotelSelected = onHotelSelected
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Индикатор загрузки для пагинации
                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        // Триггер для загрузки следующей страницы
                        item {
                            LaunchedEffect(Unit) {
                                if (state.hasNextPage && !state.isLoadingMore) {
                                    listener(HotelEvent.LoadNextPage)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationPermissionBanner(onRequestPermission: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Для точного поиска отелей включите геолокацию",
                    style = typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
                Text(
                    "Мы сможем показать отели рядом с вами",
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Включить")
            }
        }
    }
}

@Composable
fun AppliedFiltersChips(filters: HotelFilters, onClear: () -> Unit) {
    val appliedFilters = buildList {
        if (filters.checkIn != null) add("Заезд: ${filters.checkIn}")
        if (filters.checkOut != null) add("Выезд: ${filters.checkOut}")
        if (filters.guests > 1) add("Гости: ${filters.guests}")
        if (filters.rooms > 1) add("Комнаты: ${filters.rooms}")
        if (filters.minPrice != null) add("От ${filters.minPrice} ₽")
        if (filters.maxPrice != null) add("До ${filters.maxPrice} ₽")
        if (!filters.stars.isNullOrEmpty()) add("Звезды: ${filters.stars}")
    }

    if (appliedFilters.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            appliedFilters.forEach { filter ->
                FilterChip(
                    selected = false,
                    onClick = onClear,
                    label = { Text(filter, style = typography.labelSmall) },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Удалить",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = colorScheme.primaryContainer,
                        labelColor = colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
fun HotelFiltersDialog(
    filters: HotelFilters,
    onDismiss: () -> Unit,
    onApply: (HotelFilters) -> Unit
) {
    var localFilters by useState(filters)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Фильтры поиска",
                    style = typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = {
                        localFilters = HotelFilters()
                    }
                ) {
                    Text(
                        "Сбросить",
                        color = colorScheme.error
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Даты заезда/выезда
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Даты пребывания",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = localFilters.checkIn ?: "",
                            onValueChange = { localFilters = localFilters.copy(checkIn = it) },
                            label = { Text("Заезд") },
                            placeholder = { Text("дд.мм.гггг") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = localFilters.checkOut ?: "",
                            onValueChange = { localFilters = localFilters.copy(checkOut = it) },
                            label = { Text("Выезд") },
                            placeholder = { Text("дд.мм.гггг") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Divider()

                // Гости и комнаты
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Гости и номера",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = localFilters.guests.toString(),
                            onValueChange = {
                                val newValue = it.toIntOrNull() ?: 1
                                localFilters = localFilters.copy(guests = maxOf(1, newValue))
                            },
                            label = { Text("Гости") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = localFilters.rooms.toString(),
                            onValueChange = {
                                val newValue = it.toIntOrNull() ?: 1
                                localFilters = localFilters.copy(rooms = maxOf(1, newValue))
                            },
                            label = { Text("Комнаты") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Divider()

                // Цена
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Цена за ночь",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = localFilters.minPrice?.toString() ?: "",
                            onValueChange = {
                                localFilters = localFilters.copy(minPrice = it.toDoubleOrNull())
                            },
                            label = { Text("От") },
                            placeholder = { Text("0") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = localFilters.maxPrice?.toString() ?: "",
                            onValueChange = {
                                localFilters = localFilters.copy(maxPrice = it.toDoubleOrNull())
                            },
                            label = { Text("До") },
                            placeholder = { Text("50000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Divider()

                // Звезды
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Категория отеля",
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )

                    val starOptions = listOf("1", "2", "3", "4", "5")
                    val selectedStars = localFilters.stars?.split(",")?.toSet() ?: emptySet()

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        starOptions.forEach { stars ->
                            FilterChip(
                                selected = selectedStars.contains(stars),
                                onClick = {
                                    val newStars = if (selectedStars.contains(stars)) {
                                        selectedStars - stars
                                    } else {
                                        selectedStars + stars
                                    }
                                    localFilters = localFilters.copy(
                                        stars = newStars.joinToString(",")
                                            .takeIf { it.isNotEmpty() }
                                    )
                                },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(stars)
                                        Icon(
                                            Icons.Filled.Star,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onApply(localFilters) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                ) {
                    Text("Применить")
                }
            }
        }
    )
}

// Карточка отеля в списке
@Composable
fun HotelCard(
    hotel: HotelResponse,
    onHotelSelected: (HotelResponse) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .bclick { onHotelSelected(hotel) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column {
            // Баннер отеля
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Основное изображение
                AsyncImage(
                    model = hotel.images?.firstOrNull() ?: "",
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
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                // Рейтинг и звезды в правом верхнем углу
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            color = colorScheme.primaryContainer.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Звезды
                    Row {
                        repeat(hotel.stars) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Количество отзывов (заглушка, т.к. нет в модели)
                    Text(
                        text = "125 отзывов", // Заглушка
                        style = typography.labelSmall,
                        color = colorScheme.onPrimaryContainer
                    )
                }
            }

            // Информация об отеле
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Название отеля
                Text(
                    text = hotel.name,
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Адрес
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${hotel.city}, ${hotel.address}",
                        style = typography.bodyMedium,
                        color = colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Цена и кнопка выбора
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "За ночь",
                            style = typography.labelSmall,
                            color = colorScheme.secondary
                        )
                        Text(
                            text = "${hotel.pricePerNight.toInt()} ${hotel.currency}",
                            style = typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.primary
                        )
                    }

                    Button(
                        onClick = { onHotelSelected(hotel) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Выбрать",
                            style = typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

// Детальный экран отеля
@Composable
fun HotelDetailScreen(
    hotel: HotelResponse?,
    onBack: () -> Unit,
    onBookClick: () -> Unit
) {
    if (hotel == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Отель не найден")
        }
        return
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .systemBarsPadding()
            .background(colorScheme.background)
    ) {
        // Хедер с изображением и кнопкой назад
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = hotel.images?.firstOrNull() ?: "",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Кнопка назад
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = colorScheme.surface.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Назад",
                    tint = colorScheme.onSurface
                )
            }

            // Статус отеля
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        color = when (hotel.status) {
                            "active" -> Color.Green.copy(alpha = 0.9f)
                            else -> Color.Red.copy(alpha = 0.9f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when (hotel.status) {
                        "active" -> "Доступен для бронирования"
                        else -> "Не доступен"
                    },
                    style = typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Основная информация
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Название и звезды
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hotel.name,
                        style = typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Звезды
                    Row {
                        repeat(hotel.stars) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Цена
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "за ночь",
                        style = typography.labelMedium,
                        color = colorScheme.secondary
                    )
                    Text(
                        text = "${hotel.pricePerNight.toInt()} ${hotel.currency}",
                        style = typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Разделитель
            Divider(
                color = colorScheme.outline.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Описание
            Text(
                text = "Описание",
                style = typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hotel.description ?: "Описание отсутствует",
                style = typography.bodyLarge,
                color = colorScheme.onSurfaceVariant,
                lineHeight = typography.bodyLarge.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Контактная информация
            ContactInfoSection(hotel)

            Spacer(modifier = Modifier.height(24.dp))

            // Заглушка карты
            MapPlaceholder(hotel)

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка бронирования
            Button(
                onClick = onBookClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = hotel.status == "active",
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary,
                    disabledContainerColor = colorScheme.surfaceVariant,
                    disabledContentColor = colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (hotel.status == "active") {
                        "Забронировать за ${hotel.pricePerNight.toInt()} ${hotel.currency}"
                    } else {
                        "Бронирование недоступно"
                    },
                    style = typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Секция контактной информации
@Composable
fun ContactInfoSection(hotel: HotelResponse) {
    Column {
        Text(
            text = "Контактная информация",
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Сайт
        if (!hotel.website.isNullOrEmpty()) {
            val context = LocalContext.current
            ContactItem(
                icon = Icons.Filled.Search,
                title = "Сайт",
                value = hotel.website,
                isClickable = true,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(hotel.website))
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Телефон
        if (!hotel.phone.isNullOrEmpty()) {
            val context = LocalContext.current
            ContactItem(
                icon = Icons.Filled.Phone,
                title = "Телефон",
                value = hotel.phone,
                isClickable = true,
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hotel.phone}"))
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Email
        if (!hotel.email.isNullOrEmpty()) {
            val context = LocalContext.current
            ContactItem(
                icon = Icons.Filled.Email,
                title = "Email",
                value = hotel.email,
                isClickable = true,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${hotel.email}"))
                    context.startActivity(intent)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Адрес
        ContactItem(
            icon = Icons.Filled.LocationOn,
            title = "Адрес",
            value = "${hotel.city}, ${hotel.address}",
            isClickable = false
        )
    }
}

// Элемент контактной информации
@Composable
fun ContactItem(
    icon: ImageVector,
    title: String,
    value: String,
    isClickable: Boolean,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isClickable) Modifier.bclick { onClick?.invoke() }
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = typography.labelMedium,
                color = colorScheme.secondary
            )
            Text(
                text = value,
                style = typography.bodyMedium,
                color = colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isClickable) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colorScheme.outline,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Заглушка для карты
@Composable
fun MapPlaceholder(hotel: HotelResponse) {
    Column {
        Text(
            text = "Расположение",
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(shapes.medium)
                .border(
                    width = 1.dp,
                    color = colorScheme.outline.copy(alpha = 0.3f),
                    shape = shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            val mapController = remember { MapKitController() }
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                val pos = Point(hotel.latitude, hotel.longitude)
                mapController.createPlacemark(
                    pos, ImageProvider.fromResource(context, R.drawable.il_green_bush)
                )
                mapController.animateMoveTo(pos, 20f)
            }

            YandexMapKit(controller = mapController)
        }
    }
}

@Composable
fun BookingDialog(
    hotel: HotelResponse,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var checkIn by useState("")
    var checkOut by useState("")
    var guests by useState("1")
    var rooms by useState("1")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Бронирование отеля",
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    hotel.name,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.primary
                )

                Divider()

                OutlinedTextField(
                    value = checkIn,
                    onValueChange = { checkIn = it },
                    label = { Text("Дата заезда") },
                    placeholder = { Text("дд.мм.гггг") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = checkOut,
                    onValueChange = { checkOut = it },
                    label = { Text("Дата выезда") },
                    placeholder = { Text("дд.мм.гггг") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = guests,
                        onValueChange = { guests = it },
                        label = { Text("Гости") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = rooms,
                        onValueChange = { rooms = it },
                        label = { Text("Комнаты") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Итого:",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        "${hotel.pricePerNight.toInt()} ${hotel.currency} / ночь",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    )
                ) {
                    Text("Забронировать")
                }
            }
        }
    )
}

// Карта с отображением всех отелей
@Composable
fun HotelsMapView(
    hotels: List<HotelResponse>,
    onHotelSelected: (HotelResponse) -> Unit,
    modifier: Modifier = Modifier
) {
    val mapController = remember { MapKitController() }
    val context = LocalContext.current

    LaunchedEffect(hotels) {
        mapController.clear()
        
        if (hotels.isNotEmpty()) {
            // Добавляем плейсмарки для всех отелей
            hotels.forEach { hotel ->
                val point = Point(hotel.latitude, hotel.longitude)
                mapController.createPlacemark(
                    pos = point,
                    icon = ImageProvider.fromResource(context, R.drawable.il_green_bush),
                    text = hotel.name
                )
            }
            
            // Центрируем карту на первом отеле
            val firstHotel = hotels.first()
            mapController.animateMoveTo(
                pos = Point(firstHotel.latitude, firstHotel.longitude),
                zoom = 12f
            )
        }
    }

    Box(
        modifier = modifier
            .clip(shapes.medium)
            .background(colorScheme.surfaceVariant)
    ) {
        YandexMapKit(controller = mapController)
        
        // Заголовок карты
        Text(
            text = "Отели на карте (${hotels.size})",
            style = typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface,
            modifier = Modifier
                .padding(12.dp)
                .background(
                    color = colorScheme.surface.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}