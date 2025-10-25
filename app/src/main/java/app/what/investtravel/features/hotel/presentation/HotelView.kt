package app.what.investtravel.features.hotel.presentation

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.what.foundation.core.Listener
import app.what.foundation.ui.bclick
import app.what.foundation.ui.useState
import app.what.investtravel.data.remote.HotelResponse
import app.what.investtravel.features.hotel.domain.models.HotelEvent
import app.what.investtravel.features.hotel.domain.models.HotelState
import app.what.investtravel.features.main.NavBarController
import app.what.investtravel.ui.components.MapKitController
import app.what.investtravel.ui.components.YandexMapKit
import app.what.investtravel.ui.theme.icons.WHATIcons
import app.what.investtravel.ui.theme.icons.filled.Features
import coil3.compose.AsyncImage
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.image.ImageProvider
import androidx.core.graphics.createBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import app.what.investtravel.R

@Composable
fun HotelView(
    state: HotelState,
    listener: Listener<HotelEvent>
) {
    var selectedHotel: HotelResponse? by useState(null)
    val pagerState = rememberPagerState(pageCount = { 2 })

    LaunchedEffect(selectedHotel) {
        NavBarController.setVisibility(selectedHotel != null)
        if (selectedHotel != null) pagerState.animateScrollToPage(1)
        else pagerState.animateScrollToPage(0)
    }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false
    ) { page ->
        when (page) {
            0 -> HotelsListScreen(state.hotels) {
                selectedHotel = it
            }

            1 -> HotelDetailScreen(
                hotel = selectedHotel,
                onBack = { selectedHotel = null }
            )
        }
    }
}

@Composable
fun HotelsListScreen(
    hotels: List<HotelResponse>,
    onHotelSelected: (HotelResponse) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(hotels) { hotel ->
            HotelCard(
                hotel = hotel,
                onHotelSelected = onHotelSelected
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
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
    onBack: () -> Unit
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
                onClick = { /* Обработка оплаты */ },
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
            ContactItem(
                icon = WHATIcons.Features,
                title = "Сайт",
                value = hotel.website,
                isClickable = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Телефон
        if (!hotel.phone.isNullOrEmpty()) {
            ContactItem(
                icon = Icons.Filled.Phone,
                title = "Телефон",
                value = hotel.phone,
                isClickable = true
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Email
        if (!hotel.email.isNullOrEmpty()) {
            ContactItem(
                icon = Icons.Filled.Email,
                title = "Email",
                value = hotel.email,
                isClickable = true
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
    isClickable: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isClickable) Modifier.bclick { /* Обработка клика */ }
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
                mapController.animateMoveTo(pos, 50f)
            }

            YandexMapKit(controller = mapController)
        }
    }
}