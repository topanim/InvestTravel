package app.what.investtravel.features.someFeature

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.yandex.mapkit.MapKit


@Composable
fun SomeFeature() = Column {
    Text("Hello World")
    YandexMapKit(
        darkMode = isDarkTheme,
        points = state.points,
        onResetFocus = { listener(GeneralEvent.OnClearSelection) },
        modifier = Modifier
            .padding(top = spacing.sm.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
    ) { _, point ->
        listener(GeneralEvent.OnMarkClicked(point))
        true
    }
}