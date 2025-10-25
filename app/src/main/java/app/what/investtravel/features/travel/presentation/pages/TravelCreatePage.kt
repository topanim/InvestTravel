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
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import app.what.investtravel.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun TravelCreatePage(
    onSaveTravel: (UserPreferences) -> Unit
) {
    BackHandler {

    }
    val context = LocalContext.current
    val startTime = remember { mutableStateOf<String>("") }
    val endTime = remember { mutableStateOf<String>("") }
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
    val mealsPerDay = remember { mutableStateOf(0f) }
    val startLatitude = remember { mutableStateOf<Double>(0.0) }
    val startLongitude = remember { mutableStateOf<Double>(0.0) }
    val maxDistanceKm = remember { mutableStateOf(0f) }
    val preferNearby = remember { mutableStateOf<Boolean?>(null) }
    val avoidNightTime = remember { mutableStateOf<Boolean?>(null) }
    val requireFoodPoints = remember { mutableStateOf<Boolean?>(null) }
    val address = remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(startLatitude.value) {
        address.value = getAddressFromCoordinates(context, startLatitude.value, startLongitude.value)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Шапка
        Text(
            text = "Создание маршрута",
            style = typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // Даты
        SectionCard(title = "Даты путешествия") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SelectDate("Время начала") { startTime.value = it }
                }
                Column(modifier = Modifier.weight(1f)) {
                    SelectDate("Время конца") { endTime.value = it }
                }
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
        SectionCard(title = "Питание") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SliderField("Время еды", foodTime.value) { foodTime.value = it }
                SliderField("Рестораны", restaurant.value) { restaurant.value = it }
                SliderField("Фаст-фуд", fastFoodTime.value) { fastFoodTime.value = it }
                SliderField("Время в кафе", cafeTime.value) { cafeTime.value = it }
                SliderField("Время в барах", barTime.value) { barTime.value = it }
            }
        }
        
        // Туризм
        SectionCard(title = "Туризм") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SliderField("Время на туризм", tourismTime.value) { tourismTime.value = it }
                SliderField("Туризм", tourism.value) { tourism.value = it }
            }
        }
        
        // Культура и досуг
        SectionCard(title = "Культура и досуг") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SliderField("Время на искусство", artTime.value) { artTime.value = it }
                SliderField("Искусство", art.value) { art.value = it }
                SliderField("Время на досуг", leisureTime.value) { leisureTime.value = it }
                SliderField("Время на шоппинг", shoppingTime.value) { shoppingTime.value = it }
            }
        }
        
        // Дополнительные настройки
        SectionCard(title = "Дополнительные настройки") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SliderField("Приёмов пищи в день", mealsPerDay.value) { mealsPerDay.value = it }
                SliderField("Максимальная дистанция", maxDistanceKm.value) { maxDistanceKm.value = it }
            }
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuestionCheckBox("Рядом", preferNearby.value) { preferNearby.value = it }
                QuestionCheckBox("Избегать ночи", avoidNightTime.value) { avoidNightTime.value = it }
                QuestionCheckBox("Места для еды", requireFoodPoints.value) { requireFoodPoints.value = it }
            }
        }
        
        // Предпросмотр и кнопка создания
        AnimatedVisibility(
            visible = hasAnyValue(startTime, endTime, foodTime, restaurant, fastFoodTime, 
                cafeTime, barTime, tourismTime, tourism, artTime, art, leisureTime, 
                shoppingTime, mealsPerDay, maxDistanceKm, preferNearby, avoidNightTime, 
                requireFoodPoints, startLatitude, startLongitude),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ShowResultCard(
                    startTime.value,
                    endTime.value,
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
                    shoppingTime.value,
                    mealsPerDay.value,
                    address.value,
                    maxDistanceKm.value,
                    preferNearby.value,
                    avoidNightTime.value,
                    requireFoodPoints.value
                )
                
                Button(
                    onClick = {
                        onSaveTravel(
                            UserPreferences(
                                startDate = startTime.value,
                                endDate = endTime.value,
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
                                mealsPerDay = mealsPerDay.value.toInt(),
                                maxDistanceKm = maxDistanceKm.value.toDouble(),
                                preferNearby = preferNearby.value ?: false,
                                avoidNightTime = avoidNightTime.value ?: false,
                                requireFoodPoints = requireFoodPoints.value ?: false,
                                startLatitude = startLatitude.value,
                                startLongitude = startLongitude.value
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectDate(title: String, selectDate: (String) -> Unit) {
    val currentDate = LocalDate.now()
    val datePickerState = rememberDatePickerState(
        initialSelectedDate = currentDate
    )

    var showPicker by remember { mutableStateOf(false) }
    FilterChip(
        label = { Text(title) },
        selected = showPicker,
        onClick = { showPicker = true }
    )

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectDate(millisToIso8601(datePickerState.selectedDateMillis ?: 0L))
                    showPicker = false
                }) {
                    Text("OK")
                }
            }

        ) {
            DatePicker(datePickerState)
        }
    }
}

@Composable
fun MyPlaceRequest(lat:Double,lot:Double,takeCoordinates:(Double, Double)->Unit) {

    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation(fusedLocationClient) { lat, lon ->
                takeCoordinates(lat,lon)
            }
        } else {
            Toast.makeText(context,"Разрешение отклонено", Toast.LENGTH_SHORT).show()
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
                        takeCoordinates(lat,lon)
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
fun millisToIso8601(millis: Long): String {
    val instant = Instant.ofEpochMilli(millis)
    return DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withZone(ZoneOffset.UTC)
        .format(instant)
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SliderField(
    title: String,
    value: Float,
    rangeEnd: Float = 100f,
    onValueChange: (Float) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(value) }
    var showSlider by remember { mutableStateOf(false) }
    var mutableInteractionSource = remember { MutableInteractionSource() }
    Column {
        FilterChip(
            label = { Text(title) },
            selected = sliderValue != 0f,
            onClick = { showSlider = !showSlider }
        )
        AnimatedVisibility(
            showSlider
        ) {
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    onValueChange(sliderValue)
                },
                interactionSource = mutableInteractionSource,
                colors = SliderDefaults.colors(),
                steps = 200,
                valueRange = 0f..rangeEnd,
                thumb = { sliderState ->
                    Label(
                        label = {
                            PlainTooltip(
                                modifier = Modifier
                                    .sizeIn(45.dp, 25.dp)
                                    .wrapContentWidth()
                            ) {
                                Text(sliderValue.roundToInt().toString())
                            }
                        },
                        interactionSource = mutableInteractionSource,
                    ) {
                        Box(
                            modifier = Modifier
                                .wrapContentSize()
                                .clip(CircleShape)
                                .background(colorScheme.secondary)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }

                    }
                }
            )
        }
    }

}
fun isoToShortDate(isoString: String): String {
    val instant = Instant.parse(isoString) // парсим ISO 8601
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
        .withZone(ZoneId.systemDefault()) // локальная зона
    return formatter.format(instant)
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
            Column(modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.Start) {

                Text("Состав маршрута :", fontSize = 30.sp)
                if(startTime.isNotBlank()) {
                    TextForCard("Время начала", value = isoToShortDate(startTime))
                }
                if(endTime.isNotBlank()) {
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
    val startLatitude: Double=0.0,
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



