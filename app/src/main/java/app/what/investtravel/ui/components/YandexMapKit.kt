package app.what.investtravel.ui.components

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import app.what.investtravel.R
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView


object MapDefaults {
    val RostovOnDon = Point(47.2357, 39.7015)
}

@Composable
fun YandexMapKit(
    modifier: Modifier = Modifier,
    darkMode: Boolean = false,
    points: List<Point> = listOf(),
    onResetFocus: () -> Unit = {},
    onTap: (MapObject, Point) -> Boolean = { _, _ -> false }
) {
    val context = LocalContext.current
    val placeMarkTapListener = MapObjectTapListener { mapObject, point ->
        onTap(mapObject, point)
    }

    val strokeColor = colorScheme.tertiary.copy(alpha = .8f)
    val outlineColour = colorScheme.tertiary

    AndroidView(
        modifier = modifier,
        factory = {
            MapView(it).apply {
                mapWindow.map.isNightModeEnabled = darkMode
                mapWindow.map.move(
                    CameraPosition(
                        Point(47.2357, 39.7015),
                        12f,
                        150.0f,
                        30.0f
                    )
                )
            }
        }
    ) { mapView ->
        mapView.mapWindow.map.mapObjects.clear()
        val polyline = Polyline(points)

        val polylineObject = mapView.mapWindow.map.mapObjects.addPolyline(polyline)
        polylineObject.apply {
            strokeWidth = 5f
            setStrokeColor(strokeColor.value.toInt())
            outlineWidth = 1f
            outlineColor = outlineColour.value.toInt()
        }
//        points.forEach {
//            mapView.mapWindow.map.mapObjects
//                .addPlacemark()
//                .apply {
//                    geometry = it
//                    setIcon(ImageProvider.fromResource(context, R.drawable.ic_launcher_background))
//                    addTapListener(placeMarkTapListener)
//                }
//        }
    }
}