package app.what.investtravel.features.travel.presentation.pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import app.what.foundation.ui.Gap
import app.what.foundation.ui.useState
import app.what.investtravel.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun TravelCreatePage(
    saveTravel: () -> Unit
) {
    BackHandler {

    }
    val selectedDate = remember { mutableStateOf(false) }
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
    val startLatitude = remember { mutableStateOf(0f) }
    val startLongitude = remember { mutableStateOf(0f) }
    val maxDistanceKm = remember { mutableStateOf(0f) }
    val preferNearby = remember { mutableStateOf<Boolean?>(null) }
    val avoidNightTime = remember { mutableStateOf<Boolean?>(null) }
    val requireFoodPoints = remember { mutableStateOf<Boolean?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier

        ) {
            SelectDate("Время начала") {
                startTime.value = it
            }
            SelectDate("Время конца") {
                endTime.value = it
            }
            SliderField("Время еды", foodTime.value) {
                foodTime.value = it
            }
            SliderField("Рестораны", restaurant.value) {
                restaurant.value = it
            }
            SliderField("Фаст фуд", foodTime.value) {
                fastFoodTime.value = it
            }
            SliderField("Времени до кафе", cafeTime.value) {
                cafeTime.value = it
            }
            SliderField("Времени до бара", barTime.value) {
                barTime.value = it
            }
            SliderField("Время путешевствия", tourismTime.value) {
                tourismTime.value = it
            }
            SliderField("Путешевствие", tourism.value) {
                tourism.value = it
            }
            SliderField("Время на искусство", artTime.value) {
                artTime.value = it
            }
            SliderField("Искусство", art.value) {
                art.value = it
            }
            SliderField("Время на досуг", leisureTime.value) {
                leisureTime.value = it
            }
            SliderField("Время на шоппинг", shoppingTime.value) {
                shoppingTime.value = it
            }
            SliderField("Приёмов пищи в день", mealsPerDay.value) {
                mealsPerDay.value = it
            }
            SliderField("максимальная дистанция", mealsPerDay.value) {
                maxDistanceKm.value = it
            }
            QuestionCheckBox("Рядом", preferNearby.value) {
                preferNearby.value = it
            }
            QuestionCheckBox("Избегать ночи", avoidNightTime.value) {
                avoidNightTime.value = it
            }
            QuestionCheckBox("Места для еды", requireFoodPoints.value) {
                requireFoodPoints.value = it
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        this.AnimatedVisibility(
            startTime.value.isNotBlank() || endTime.value.isNotBlank() || foodTime.value > 0 || restaurant.value > 0 || fastFoodTime.value > 0 || cafeTime.value > 0 || barTime.value > 0 || tourismTime.value > 0 || tourism.value > 0 || artTime.value > 0 || art.value > 0 || leisureTime.value > 0 || shoppingTime.value > 0 || mealsPerDay.value > 0 || maxDistanceKm.value > 0 || preferNearby.value != null || avoidNightTime.value != null || requireFoodPoints.value != null
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
                maxDistanceKm.value,
                preferNearby.value,
                avoidNightTime.value,
                requireFoodPoints.value

            )
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
                    val formatter = DateTimeFormatter.ofPattern("dd.MM.yy")
                        .withZone(ZoneId.systemDefault())
                    val date = formatter.format(
                        Instant.ofEpochMilli(
                            datePickerState.selectedDateMillis ?: 0L
                        )
                    )
                    selectDate(date)
                    showPicker = false
                }) {
                    Text("OK")
                }
            }

        ) {
            androidx.compose.material3.DatePicker(datePickerState)
        }
    }
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
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_directions_run_24),
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
//    startLatitude: Float,
//    startLongitude:Float,
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
            Column(modifier = Modifier.padding(10.dp)) {
                Text("Состав маршрута :", fontSize = 30.sp)
                TextForCard("Время начала", value = startTime)
                TextForCard("Время конца", value = endTime)
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
                TextForCard("Время на покупки", value = shoppingTime.toInt().toString())
                TextForCard("Приёмов пищи в день", value = mealsPerDay.toInt().toString())
                TextForCard("Максимальная дистанция", value = maxDistanceKm.toInt().toString())
                TextForCard("Предпочтения", value = preferNearby.toString())
                TextForCard("Избегать ночи", value = avoidNightTime.toString())
                TextForCard("Точки питания", value = requireFoodPoints.toString())
            }
        }
        Button(
            {}, modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Создать марштрут")
        }

    }

}


@Composable
fun TextForCard(title: String, value: String) {
    if (value.isNotBlank() && value != "0" && value != "null") {
        Row {
            Text("$title - ")
            Text(value)
        }
    }
}



