package app.what.investtravel.features.travel.presentation.pages

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import app.what.investtravel.data.remote.AiRouteRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TravelCreatePage(
    onSaveTravel: (UserPreferences) -> Unit,
    onSaveAiTravel: (AiRouteRequest) -> Unit,
    isLoading: Boolean = false
) {
    BackHandler { }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Детальные настройки", "AI Маршрут")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
        // Заголовок
        Text(
            text = "Создание маршрута",
            style = typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )

        // Вкладки
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        // Контент вкладок
        when (selectedTabIndex) {
            0 -> DetailedSettingsTab(onSaveTravel = onSaveTravel)
            1 -> AiRouteTab(onSaveAiTravel = onSaveAiTravel)
        }
        }

        // Индикатор загрузки
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorScheme.surface.copy(alpha = 0.8f)),
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
                        "Создание маршрута...",
                        style = typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun DetailedSettingsTab(
    onSaveTravel: (UserPreferences) -> Unit
) {
    val context = LocalContext.current
    
    // Устанавливаем значения по умолчанию
    val now = Instant.now()
    val startDefault = now.plusSeconds(3600) // +1 час
    val endDefault = now.plusSeconds(86400) // +1 день
    
    val startDateTime = remember { mutableStateOf<String?>(null) }
    val endDateTime = remember { mutableStateOf<String?>(null) }
    val startDate = remember { mutableStateOf<String?>(null) }
    val startTime = remember { mutableStateOf<String?>(null) }
    val endDate = remember { mutableStateOf<String?>(null) }
    val endTime = remember { mutableStateOf<String?>(null) }
    
    // Инициализируем значения по умолчанию
    LaunchedEffect(Unit) {
        val startDateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC).format(startDefault)
        val startTimeStr = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC).format(startDefault)
        val endDateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC).format(endDefault)
        val endTimeStr = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC).format(endDefault)
        
        startDate.value = startDateStr
        startTime.value = startTimeStr
        endDate.value = endDateStr
        endTime.value = endTimeStr
    }

    val foodTime = remember { mutableStateOf(0f) }
    val restaurant = remember { mutableStateOf(0f) }
    val fastFoodTime = remember { mutableStateOf(0f) }
    val cafeTime = remember { mutableStateOf(0f) }
    val barTime = remember { mutableStateOf(0f) }
    val tourismTime = remember { mutableStateOf(0f) }
    val tourism = remember { mutableStateOf(0f) }
    val artTime = remember { mutableStateOf(0f) }
    val art = remember { mutableStateOf(0f) }
    val leisureTime = remember { mutableStateOf(0f) }
    val shoppingTime = remember { mutableStateOf(0f) }
    val mealsPerDay = remember { mutableStateOf(3) }
    val startLatitude = remember { mutableStateOf<Double>(0.0) }
    val startLongitude = remember { mutableStateOf<Double>(0.0) }
    val maxDistanceKm = remember { mutableStateOf(50) }
    val preferNearby = remember { mutableStateOf<Boolean?>(null) }
    val avoidNightTime = remember { mutableStateOf<Boolean?>(null) }
    val requireFoodPoints = remember { mutableStateOf<Boolean?>(null) }
    val address = remember { mutableStateOf<String?>(null) }

    // Вычисляем доступное время в часах
    val availableTimeHours = remember(startDateTime.value, endDateTime.value) {
        if (startDateTime.value != null && endDateTime.value != null) {
            try {
                val start = Instant.parse(startDateTime.value)
                val end = Instant.parse(endDateTime.value)
                val hours = (end.epochSecond - start.epochSecond) / 3600.0
                hours.coerceIn(0.0, 168.0) // максимум неделя
            } catch (e: Exception) {
                0.0
            }
        } else {
            0.0
        }
    }

    // Вычисляем использованное время
    val usedTimeHours = remember(
        foodTime.value,
        restaurant.value,
        fastFoodTime.value,
        cafeTime.value,
        barTime.value,
        tourismTime.value,
        tourism.value,
        artTime.value,
        art.value,
        leisureTime.value,
        shoppingTime.value
    ) {
        foodTime.value + restaurant.value + fastFoodTime.value + cafeTime.value + barTime.value +
                tourismTime.value + tourism.value + artTime.value + art.value + leisureTime.value + shoppingTime.value
    }

    // Оставшееся время
    val remainingTime = availableTimeHours - usedTimeHours

    LaunchedEffect(startLatitude.value) {
        address.value =
            getAddressFromCoordinates(context, startLatitude.value, startLongitude.value)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Даты и время
        SectionCard(title = "Временной интервал") {
            DateTimeSelector(
                label = "Начало",
                selectedDate = startDate.value,
                selectedTime = startTime.value,
                onDateSelected = { startDate.value = it },
                onTimeSelected = { startTime.value = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateTimeSelector(
                label = "Конец",
                selectedDate = endDate.value,
                selectedTime = endTime.value,
                onDateSelected = { endDate.value = it },
                onTimeSelected = { endTime.value = it }
            )

            // Объединение даты и времени
            LaunchedEffect(startDate.value, startTime.value) {
                if (startDate.value != null && startTime.value != null) {
                    startDateTime.value = "${startDate.value}T${startTime.value}:00.000Z"
                }
            }

            LaunchedEffect(endDate.value, endTime.value) {
                if (endDate.value != null && endTime.value != null) {
                    endDateTime.value = "${endDate.value}T${endTime.value}:00.000Z"
                }
            }

            // Отображение выбранного промежутка
            if (startDateTime.value != null && endDateTime.value != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Доступно: ${availableTimeHours.toInt()}ч, Использовано: ${usedTimeHours.toInt()}ч",
                    style = typography.bodySmall,
                    color = if (remainingTime >= 0) colorScheme.primary else colorScheme.error
                )
            }
        }

        // Местоположение
        SectionCard(title = "Местоположение") {
            MyPlaceRequest(startLatitude.value, startLongitude.value) { lat, lon ->
                startLatitude.value = lat
                startLongitude.value = lon
            }
            if (address.value != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            colorScheme.primaryContainer.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = address.value ?: "",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Питание
        SectionCard(title = "⏱️ Питание") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeSliderField(
                    "Время еды",
                    foodTime.value,
                    remainingTime,
                    availableTimeHours
                ) { foodTime.value = it }
                TimeSliderField(
                    "Рестораны",
                    restaurant.value,
                    remainingTime,
                    availableTimeHours
                ) { restaurant.value = it }
                TimeSliderField(
                    "Фаст-фуд",
                    fastFoodTime.value,
                    remainingTime,
                    availableTimeHours
                ) { fastFoodTime.value = it }
                TimeSliderField(
                    "Время в кафе",
                    cafeTime.value,
                    remainingTime,
                    availableTimeHours
                ) { cafeTime.value = it }
                TimeSliderField(
                    "Время в барах",
                    barTime.value,
                    remainingTime,
                    availableTimeHours
                ) { barTime.value = it }
            }
        }

        // Туризм
        SectionCard(title = "⏱️ Туризм") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeSliderField(
                    "Время на туризм",
                    tourismTime.value,
                    remainingTime,
                    availableTimeHours
                ) { tourismTime.value = it }
                TimeSliderField(
                    "Туризм",
                    tourism.value,
                    remainingTime,
                    availableTimeHours
                ) { tourism.value = it }
            }
        }

        // Культура и досуг
        SectionCard(title = "⏱️ Культура и досуг") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TimeSliderField(
                    "Время на искусство",
                    artTime.value,
                    remainingTime,
                    availableTimeHours
                ) { artTime.value = it }
                TimeSliderField(
                    "Искусство",
                    art.value,
                    remainingTime,
                    availableTimeHours
                ) { art.value = it }
                TimeSliderField(
                    "Время на досуг",
                    leisureTime.value,
                    remainingTime,
                    availableTimeHours
                ) { leisureTime.value = it }
                TimeSliderField(
                    "Время на шоппинг",
                    shoppingTime.value,
                    remainingTime,
                    availableTimeHours
                ) { shoppingTime.value = it }
            }
        }

        // Дополнительные настройки
        SectionCard(title = "Дополнительно") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SimpleSliderField(
                    "Приёмов пищи в день",
                    mealsPerDay.value,
                    1,
                    4
                ) { mealsPerDay.value = it }
                SimpleSliderField(
                    "Максимальная дистанция (км)",
                    maxDistanceKm.value,
                    1,
                    100
                ) { maxDistanceKm.value = it }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuestionCheckBox("Рядом", preferNearby.value) { preferNearby.value = it }
                    QuestionCheckBox("Избегать ночи", avoidNightTime.value) {
                        avoidNightTime.value = it
                    }
                    QuestionCheckBox(
                        "Места для еды",
                        requireFoodPoints.value
                    ) { requireFoodPoints.value = it }
                }
            }
        }

        // Кнопка создания
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Статистика
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Всего времени",
                            style = typography.labelSmall,
                            color = colorScheme.secondary
                        )
                        Text(
                            "${availableTimeHours.toInt()} ч",
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            "Использовано",
                            style = typography.labelSmall,
                            color = colorScheme.secondary
                        )
                        Text(
                            "${usedTimeHours.toInt()} ч",
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingTime >= 0) colorScheme.primary else colorScheme.error
                        )
                    }
                    Column {
                        Text(
                            "Осталось",
                            style = typography.labelSmall,
                            color = colorScheme.secondary
                        )
                        Text(
                            "${remainingTime.toInt()} ч",
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (remainingTime >= 0) colorScheme.primary else colorScheme.error
                        )
                    }
                }

                Button(
                    onClick = {
                        onSaveTravel(
                            UserPreferences(
                                startDate = startDateTime.value ?: "",
                                endDate = endDateTime.value ?: "",
                                foodTime = foodTime.value.toInt(),
                                restaurant = restaurant.value.toInt(),
                                fastFoodTime = fastFoodTime.value.toInt(),
                                cafeTime = cafeTime.value.toInt(),
                                barTime = barTime.value.toInt(),
                                tourismTime = tourismTime.value.toInt(),
                                tourism = tourism.value.toInt(),
                                artTime = artTime.value.toInt(),
                                art = art.value.toInt(),
                                leisureTime = leisureTime.value.toInt(),
                                shoppingTime = shoppingTime.value.toInt(),
                                mealsPerDay = mealsPerDay.value,
                                maxDistanceKm = maxDistanceKm.value.toDouble(),
                                preferNearby = preferNearby.value ?: false,
                                avoidNightTime = avoidNightTime.value ?: false,
                                requireFoodPoints = requireFoodPoints.value ?: false,
                                startLatitude = startLatitude.value,
                                startLongitude = startLongitude.value
                            )
                        )
                    },
                    enabled = startDateTime.value != null &&
                            endDateTime.value != null &&
                            remainingTime >= 0 &&
                            mealsPerDay.value in 1..4 &&
                            maxDistanceKm.value in 1..100,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary,
                        disabledContainerColor = colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Создать маршрут",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AiRouteTab(
    onSaveAiTravel: (AiRouteRequest) -> Unit
) {
    val context = LocalContext.current
    
    // Устанавливаем значения по умолчанию
    val now = Instant.now()
    val startDefault = now.plusSeconds(3600) // +1 час
    val endDefault = now.plusSeconds(86400) // +1 день
    
    val startDateTime = remember { mutableStateOf<String?>(null) }
    val endDateTime = remember { mutableStateOf<String?>(null) }
    val startDate = remember { mutableStateOf<String?>(null) }
    val startTime = remember { mutableStateOf<String?>(null) }
    val endDate = remember { mutableStateOf<String?>(null) }
    val endTime = remember { mutableStateOf<String?>(null) }
    val userPreferences = remember { mutableStateOf("") }
    val startLatitude = remember { mutableStateOf<Double>(0.0) }
    val startLongitude = remember { mutableStateOf<Double>(0.0) }
    val address = remember { mutableStateOf<String?>(null) }
    
    // Инициализируем значения по умолчанию
    LaunchedEffect(Unit) {
        val startDateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC).format(startDefault)
        val startTimeStr = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC).format(startDefault)
        val endDateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC).format(endDefault)
        val endTimeStr = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC).format(endDefault)
        
        startDate.value = startDateStr
        startTime.value = startTimeStr
        endDate.value = endDateStr
        endTime.value = endTimeStr
    }

    // Вычисляем продолжительность поездки в часах
    val tripDurationHours = remember(startDateTime.value, endDateTime.value) {
        if (startDateTime.value != null && endDateTime.value != null) {
            try {
                val start = Instant.parse(startDateTime.value)
                val end = Instant.parse(endDateTime.value)
                val hours = (end.epochSecond - start.epochSecond) / 3600.0
                hours.coerceIn(0.0, 168.0).toInt() // максимум неделя
            } catch (e: Exception) {
                0
            }
        } else {
            0
        }
    }

    LaunchedEffect(startLatitude.value) {
        address.value =
            getAddressFromCoordinates(context, startLatitude.value, startLongitude.value)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Временной интервал
        SectionCard(title = "Временной интервал") {
            DateTimeSelector(
                label = "Начало",
                selectedDate = startDate.value,
                selectedTime = startTime.value,
                onDateSelected = { startDate.value = it },
                onTimeSelected = { startTime.value = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DateTimeSelector(
                label = "Конец",
                selectedDate = endDate.value,
                selectedTime = endTime.value,
                onDateSelected = { endDate.value = it },
                onTimeSelected = { endTime.value = it }
            )

            // Объединение даты и времени
            LaunchedEffect(startDate.value, startTime.value) {
                if (startDate.value != null && startTime.value != null) {
                    startDateTime.value = "${startDate.value}T${startTime.value}:00.000Z"
                }
            }

            LaunchedEffect(endDate.value, endTime.value) {
                if (endDate.value != null && endTime.value != null) {
                    endDateTime.value = "${endDate.value}T${endTime.value}:00.000Z"
                }
            }

            // Отображение продолжительности
            if (tripDurationHours > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Продолжительность: ${tripDurationHours}ч",
                    style = typography.bodyMedium,
                    color = colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Местоположение
        SectionCard(title = "Местоположение") {
            MyPlaceRequest(startLatitude.value, startLongitude.value) { lat, lon ->
                startLatitude.value = lat
                startLongitude.value = lon
            }
            if (address.value != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            colorScheme.primaryContainer.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = address.value ?: "",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurface
                    )
                }
            }
        }

        // Предпочтения пользователя
        SectionCard(title = "Ваши предпочтения") {
            OutlinedTextField(
                value = userPreferences.value,
                onValueChange = { userPreferences.value = it },
                label = { Text("Опишите, что вас интересует") },
                placeholder = { Text("Например: Я люблю искусство и культуру, хочу посетить музеи и галереи. Также интересуюсь историей города. Люблю хорошую еду, но не фанат фаст-фуда.") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )
        }

        // Кнопка создания
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "AI создаст персональный маршрут на основе ваших предпочтений",
                    style = typography.bodyMedium,
                    color = colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {
                        if (startDateTime.value != null && endDateTime.value != null && userPreferences.value.isNotBlank()) {
                            onSaveAiTravel(
                                AiRouteRequest(
                                    userPreferences = userPreferences.value,
                                    tripDurationHours = tripDurationHours,
                                    startDate = startDateTime.value!!,
                                    endDate = endDateTime.value!!
                                )
                            )
                        }
                    },
                    enabled = startDateTime.value != null &&
                            endDateTime.value != null &&
                            userPreferences.value.isNotBlank() &&
                            tripDurationHours > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary,
                        disabledContainerColor = colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Создать AI маршрут",
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeSelector(
    label: String,
    selectedDate: String?,
    selectedTime: String?,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val timePickerState = remember {
        val now = LocalTime.now()
        TimePickerState(now.hour, now.minute, true)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedDate != null,
            onClick = { showDatePicker = true },
            label = { Text(selectedDate ?: "Выберите дату") },
            modifier = Modifier.weight(1f)
        )

        FilterChip(
            selected = selectedTime != null,
            onClick = { showTimePicker = true },
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Notifications, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(selectedTime ?: "Выберите время")
                }
            },
            modifier = Modifier.weight(1f)
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(millisToDateString(millis))
                        showDatePicker = false
                    }
                }) { Text("OK") }
            }
        ) {
            DatePicker(datePickerState)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Выберите время") },
            confirmButton = {
                Button(onClick = {
                    val time =
                        String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    onTimeSelected(time)
                    showTimePicker = false
                }) {
                    Text("Выбрать")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSliderField(
    title: String,
    value: Float,
    remainingTime: Double,
    availableTime: Double,
    onValueChange: (Float) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(value) }
    var showSlider by remember { mutableStateOf(false) }
    val mutableInteractionSource = remember { MutableInteractionSource() }

    val maxValue = remember(remainingTime) {
        if (value + remainingTime >= 0) {
            value + remainingTime
        } else {
            0f
        }.toFloat().coerceAtMost(168f) // максимум неделя
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        FilterChip(
            selected = sliderValue > 0f,
            onClick = { showSlider = !showSlider },
            label = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Notifications, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(title, style = typography.bodyMedium)
                    }
                    if (sliderValue > 0f) {
                        Text(
                            "${sliderValue.toInt()}ч",
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(showSlider) {
            Column {
                Slider(
                    value = sliderValue,
                    onValueChange = { newValue ->
                        if (newValue <= maxValue) {
                            sliderValue = newValue
                        }
                    },
                    onValueChangeFinished = { onValueChange(sliderValue) },
                    interactionSource = mutableInteractionSource,
                    steps = (maxValue - 1).toInt().coerceIn(0, 100),
                    valueRange = 0f..maxValue,
                    colors = SliderDefaults.colors(),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primary)
                        ) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                )
                if (maxValue <= 0f) {
                    Text(
                        "Достигнут лимит времени",
                        style = typography.bodySmall,
                        color = colorScheme.error,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSliderField(
    title: String,
    value: Int,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit
) {
    var sliderValue by remember { mutableIntStateOf(value) }
    var showSlider by remember { mutableStateOf(false) }
    val mutableInteractionSource = remember { MutableInteractionSource() }

    Column(modifier = Modifier.fillMaxWidth()) {
        FilterChip(
            selected = sliderValue > minValue,
            onClick = { showSlider = !showSlider },
            label = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(title, style = typography.bodyMedium)
                    if (sliderValue > minValue) {
                        Text(
                            sliderValue.toString(),
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(showSlider) {
            Column {
                Slider(
                    value = sliderValue.toFloat(),
                    onValueChange = { newValue ->
                        sliderValue = newValue.toInt()
                    },
                    onValueChangeFinished = { onValueChange(sliderValue) },
                    interactionSource = mutableInteractionSource,
                    steps = (maxValue - minValue - 1).coerceIn(0, 100),
                    valueRange = minValue.toFloat()..maxValue.toFloat(),
                    colors = SliderDefaults.colors(),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primary)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun QuestionCheckBox(title: String, checked: Boolean?, onValueChange: (Boolean) -> Unit) {
    var showCheckRow by remember { mutableStateOf(false) }
    Column(Modifier) {
        FilterChip(
            label = { Text(title) },
            selected = checked != null,
            onClick = { showCheckRow = !showCheckRow }
        )
        this.AnimatedVisibility(showCheckRow) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Да", modifier = Modifier
                        .weight(1f)
                        .background(colorScheme.primary)
                        .clickable {
                            onValueChange(true)
                        },
                    textAlign = TextAlign.Center,
                    color = colorScheme.onPrimary

                )
                VerticalDivider(modifier = Modifier.wrapContentHeight())
                Text(
                    "Нет", modifier = Modifier
                        .weight(1f)
                        .background(colorScheme.error)
                        .clickable {
                            onValueChange(false)
                        },
                    textAlign = TextAlign.Center,
                    color = colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun MyPlaceRequest(lat: Double, lot: Double, takeCoordinates: (Double, Double) -> Unit) {

    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation(fusedLocationClient) { lat, lon ->
                takeCoordinates(lat, lon)
            }
        } else {
            Toast.makeText(context, "Разрешение отклонено", Toast.LENGTH_SHORT).show()
        }
    }
    FilterChip(
        label = { Text("Мое местоположение") },
        selected = lat != 0.0,
        onClick = {
            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            when {
                ContextCompat.checkSelfPermission(context, permission)
                        == PackageManager.PERMISSION_GRANTED -> {
                    // Разрешение уже есть
                    getLocation(fusedLocationClient) { lat, lon ->
                        takeCoordinates(lat, lon)
                    }
                }

                else -> permissionLauncher.launch(permission)

            }
        }
    )
}

fun Geocoder.getCity(lat: Double, lon: Double): String? {
    return try {
        val addresses = getFromLocation(lat, lon, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            // Формируем строку, например: "улица, город, страна"
            address.locality ?: "Ростов-на-Дону"
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@SuppressLint("MissingPermission")
fun getLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReady: (Double, Double) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationReady(location.latitude, location.longitude)
        }
    }
}

@Composable
fun ShowResultCard(
    startTime: String,
    endTime: String,
    foodTime: Float,
    restaurant: Float,
    fastFoodTime: Float,
    cafeTime: Float,
    barTime: Float,
    tourismTime: Float,
    tourism: Float,
    artTime: Float,
    art: Float,
    leisureTime: Float,
    shoppingTime: Float,
    mealsPerDay: Float,
    startAddress: String?,
    maxDistanceKm: Float,
    preferNearby: Boolean?,
    avoidNightTime: Boolean?,
    requireFoodPoints: Boolean?,
) {
    Column(modifier = Modifier.wrapContentSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.Start
            ) {

                Text("Состав маршрута :", fontSize = 30.sp)
                if (startTime.isNotBlank()) {
                    TextForCard("Время начала", value = isoToShortDate(startTime))
                }
                if (endTime.isNotBlank()) {
                    TextForCard("Время конца", value = isoToShortDate(endTime))
                }
                TextForCard("Время еды", value = foodTime.toInt().toString())
                TextForCard("Рестораны", value = restaurant.toInt().toString())
                TextForCard("Время на перекус", value = fastFoodTime.toInt().toString())
                TextForCard("Время на кафе ", value = cafeTime.toInt().toString())
                TextForCard("Время на бары", value = barTime.toInt().toString())
                TextForCard("Время на туризм", value = tourismTime.toInt().toString())
                TextForCard("Туризм", value = tourism.toInt().toString())
                TextForCard("Время на искусство", value = artTime.toInt().toString())
                TextForCard("Искусство", value = art.toInt().toString())
                TextForCard("Время на досуг", value = leisureTime.toInt().toString())
                TextForCard("Мое местоположение", value = "$startAddress")
                TextForCard("Время на покупки", value = shoppingTime.toInt().toString())
                TextForCard("Приёмов пищи в день", value = mealsPerDay.toInt().toString())
                TextForCard("Максимальная дистанция", value = maxDistanceKm.toInt().toString())
                TextForCard("Предпочтения", value = preferNearby.toString())
                TextForCard("Избегать ночи", value = avoidNightTime.toString())
                TextForCard("Точки питания", value = requireFoodPoints.toString())
            }
        }

    }
}

data class UserPreferences(
    val startDate: String,
    val endDate: String,
    val foodTime: Int = 0,
    val restaurant: Int = 0,
    val fastFoodTime: Int = 0,
    val cafeTime: Int = 0,
    val barTime: Int = 0,
    val tourismTime: Int = 0,
    val tourism: Int = 0,
    val artTime: Int = 0,
    val art: Int = 0,
    val leisureTime: Int = 0,
    val shoppingTime: Int = 0,
    val mealsPerDay: Int = 3,
    val startLatitude: Double = 0.0,
    val startLongitude: Double = 0.0,
    val maxDistanceKm: Double = 50.0,
    val preferNearby: Boolean = true,
    val avoidNightTime: Boolean = true,
    val requireFoodPoints: Boolean = true
)


@Composable
fun TextForCard(title: String, value: String) {
    if (value.isNotBlank() && value != "0" && value != "null") {
        val displayValue = when (value) {
            "true" -> "Да"
            "false" -> "Нет"
            else -> value
        }
        Text("$title - $displayValue")
    }
}

@Composable
fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.primary
            )
            Divider()
            content()
        }
    }
}

fun hasAnyValue(
    startTime: androidx.compose.runtime.MutableState<String>,
    endTime: androidx.compose.runtime.MutableState<String>,
    foodTime: androidx.compose.runtime.MutableState<Float>,
    restaurant: androidx.compose.runtime.MutableState<Float>,
    fastFoodTime: androidx.compose.runtime.MutableState<Float>,
    cafeTime: androidx.compose.runtime.MutableState<Float>,
    barTime: androidx.compose.runtime.MutableState<Float>,
    tourismTime: androidx.compose.runtime.MutableState<Float>,
    tourism: androidx.compose.runtime.MutableState<Float>,
    artTime: androidx.compose.runtime.MutableState<Float>,
    art: androidx.compose.runtime.MutableState<Float>,
    leisureTime: androidx.compose.runtime.MutableState<Float>,
    shoppingTime: androidx.compose.runtime.MutableState<Float>,
    mealsPerDay: androidx.compose.runtime.MutableState<Float>,
    maxDistanceKm: androidx.compose.runtime.MutableState<Float>,
    preferNearby: androidx.compose.runtime.MutableState<Boolean?>,
    avoidNightTime: androidx.compose.runtime.MutableState<Boolean?>,
    requireFoodPoints: androidx.compose.runtime.MutableState<Boolean?>,
    startLatitude: androidx.compose.runtime.MutableState<Double>,
    startLongitude: androidx.compose.runtime.MutableState<Double>
): Boolean {
    return startTime.value.isNotBlank() ||
            endTime.value.isNotBlank() ||
            foodTime.value > 0 ||
            restaurant.value > 0 ||
            fastFoodTime.value > 0 ||
            cafeTime.value > 0 ||
            barTime.value > 0 ||
            tourismTime.value > 0 ||
            tourism.value > 0 ||
            artTime.value > 0 ||
            art.value > 0 ||
            leisureTime.value > 0 ||
            shoppingTime.value > 0 ||
            mealsPerDay.value > 0 ||
            maxDistanceKm.value > 0 ||
            preferNearby.value != null ||
            avoidNightTime.value != null ||
            requireFoodPoints.value != null ||
            startLatitude.value != 0.0 ||
            startLongitude.value != 0.0
}

fun getAddressFromCoordinates(context: Context, latitude: Double, longitude: Double): String? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            listOfNotNull(
                address.thoroughfare,
                address.subThoroughfare,
                address.locality,
                address.countryName
            ).joinToString(", ")
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun millisToDateString(millis: Long): String {
    val instant = Instant.ofEpochMilli(millis)
    return DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withZone(ZoneOffset.UTC)
        .format(instant)
}

fun millisToIso8601(millis: Long): String {
    val instant = Instant.ofEpochMilli(millis)
    return DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneOffset.UTC)
        .format(instant)
}

fun isoToShortDate(isoString: String): String {
    val instant = Instant.parse(isoString)
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}



