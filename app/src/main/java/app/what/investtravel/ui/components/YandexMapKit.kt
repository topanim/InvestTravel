package app.what.investtravel.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import app.what.foundation.services.AppLogger.Companion.Auditor
import com.yandex.mapkit.Animation
import com.yandex.mapkit.ConflictResolutionMode
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.PolylinePosition
import com.yandex.mapkit.geometry.geo.PolylineIndex
import com.yandex.mapkit.geometry.geo.PolylineUtils
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider

class MapKitController {
    private var _map: Map? = null
    private var context: android.content.Context? = null

    private val map get() = _map!!

    fun setMap(value: Map) {
        _map = value
        map.mapObjects.conflictResolutionMode = ConflictResolutionMode.MAJOR
    }

    fun setContext(ctx: android.content.Context) {
        context = ctx
    }

    private val drivingRouter =
        DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)

    private val mapObjects = mutableListOf<MapObject>()

    fun setNightMode(value: Boolean) {
        map.isNightModeEnabled = value
    }

    companion object {
        fun routeProgress(route: DrivingRoute): Double {
            val startPosition = PolylinePosition(0, 0.0)
            val distanceFull = route.metadataAt(startPosition).weight.distance.value
            val distanceLeft = route.metadata.weight.distance.value
            return 1f - distanceLeft / distanceFull
        }

        fun distanceBetweenPointsOnRoute(route: DrivingRoute, first: Point, second: Point): Double {
            val polylineIndex = PolylineUtils.createPolylineIndex(route.geometry)
            val firstPosition = polylineIndex.closestPolylinePosition(
                first,
                PolylineIndex.Priority.CLOSEST_TO_RAW_POINT,
                1.0
            )!!
            val secondPosition = polylineIndex.closestPolylinePosition(
                second,
                PolylineIndex.Priority.CLOSEST_TO_RAW_POINT,
                1.0
            )!!
            return PolylineUtils.distanceBetweenPolylinePositions(
                route.geometry,
                firstPosition,
                secondPosition
            )
        }

        fun calculateRouteDistance(route: DrivingRoute) = distanceBetweenPointsOnRoute(
            route,
            route.geometry.points.first(),
            route.geometry.points.last()
        )

        fun calculateRouteTime(route: DrivingRoute): Double {
            val distance = calculateRouteDistance(route)
            return distance / 1000.0 * 60.0 / 5.0 // 5 км/ч средняя скорость пешехода
        }

        // Рассчитывает расстояние между точками по прямой (формула гаверсинуса)
        fun calculateDirectDistance(points: List<Point>): Double {
            var totalDistance = 0.0
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i + 1]
                totalDistance += calculateHaversineDistance(p1, p2)
            }
            return totalDistance
        }

        // Формула гаверсинуса для расчета расстояния между двумя точками на Земле
        private fun calculateHaversineDistance(point1: Point, point2: Point): Double {
            val earthRadius = 6371000.0 // Радиус Земли в метрах

            val lat1Rad = Math.toRadians(point1.latitude)
            val lat2Rad = Math.toRadians(point2.latitude)
            val deltaLatRad = Math.toRadians(point2.latitude - point1.latitude)
            val deltaLonRad = Math.toRadians(point2.longitude - point1.longitude)

            val a = kotlin.math.sin(deltaLatRad / 2) * kotlin.math.sin(deltaLatRad / 2) +
                    kotlin.math.cos(lat1Rad) * kotlin.math.cos(lat2Rad) *
                    kotlin.math.sin(deltaLonRad / 2) * kotlin.math.sin(deltaLonRad / 2)

            val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))

            return earthRadius * c
        }

        // Рассчитывает время на основе расстояния и скорости транспорта
        fun calculateTimeByTransport(distance: Double, vehicleType: String): Double {
            val speedKmh = when (vehicleType) {
                "walking" -> 5.0 // 5 км/ч для пешехода
                "bicycle" -> 15.0 // 15 км/ч для велосипеда
                else -> 50.0 // 50 км/ч для автомобиля
            }
            return distance / 1000.0 * 60.0 / speedKmh // результат в минутах
        }

    }

    fun clear() {
        map.mapObjects.clear()
        mapObjects.clear()
    }

    fun moveTo(
        pos: Point,
        zoom: Float = 12f,
        azimuth: Float = 150.0f,
        tilt: Float = 30.0f
    ) = map.move(
        CameraPosition(
            pos,
            zoom,
            azimuth,
            tilt
        )
    )

    fun animateMoveTo(
        pos: Point,
        zoom: Float = 12f,
        azimuth: Float = 150.0f,
        tilt: Float = 30.0f
    ) = map.move(
        CameraPosition(
            pos,
            zoom,
            azimuth,
            tilt
        ),
        Animation(Animation.Type.LINEAR, 1f),
        null
    )

    fun createPlacemark(
        pos: Point,
        icon: ImageProvider,
        text: String? = null
    ): PlacemarkMapObject {
        val placemarkObj = map.mapObjects.addPlacemark().apply {
            geometry = pos
            setIcon(icon)
            text?.let {
                setText(it, TextStyle())
            }
        }

        Auditor.debug("d", placemarkObj.toString())

        mapObjects.add(placemarkObj)

        return placemarkObj
    }

    fun createPlacemark(point: Point, imageProvider: ImageProvider, text: String) {
        map.mapObjects.addPlacemark().apply {
            geometry = point
            setIcon(imageProvider)
            setText(text, TextStyle())
        }
    }

    fun createCircle(
        pos: Point,
        radius: Float = 30f,
        color: Int = Color(0xFF3F51B5).toArgb(),
        strokeColor1: Int = Color(0xFF1A237E).toArgb()
    ): CircleMapObject {
        val circleObj = map.mapObjects.addCircle(
            com.yandex.mapkit.geometry.Circle(pos, radius)
        ).apply {
            fillColor = color
            strokeColor = strokeColor1
            strokeWidth = 2f
        }

        mapObjects.add(circleObj)

        return circleObj
    }

    fun createRoute(
        points: List<Point>,
        vehicleType: String = "car",
        vehicleOptions: VehicleOptions = VehicleOptions(),
        drivingOptions: DrivingOptions = DrivingOptions().apply { routesCount = 1 },
        onFailure: (Error) -> Unit = { Auditor.debug("d", it.toString()) },
        onSuccess: (DrivingRoute?) -> Unit
    ) {
        val rPoints = buildList {
            points.forEach {
                add(
                    RequestPoint(
                        it,
                        RequestPointType.WAYPOINT,
                        null,
                        null,
                        null
                    )
                )
            }
        }

        // Для пешеходного маршрута рисуем прямую линию
        if (vehicleType == "bicycle" || vehicleType == "walking") {
            val directPolyline = Polyline(points)
            val strokeColor = Color(0xFFFF9800) // Оранжевый для пешеходного/велосипедного маршрута
            val outlineColor1 = Color(0xFFE65100)

            val polylineObj = map.mapObjects.addPolyline(directPolyline).apply {
                strokeWidth = 6f
                setStrokeColor(strokeColor.toArgb())
                outlineWidth = 2f
                outlineColor = outlineColor1.toArgb()
            }

            mapObjects.add(polylineObj)

            // Добавляем метки на все точки маршрута
            points.forEachIndexed { index, point ->
                val color = when (index) {
                    0 -> Color(0xFF4CAF50).toArgb() // Зеленый - старт
                    points.lastIndex -> Color(0xFFF44336).toArgb() // Красный - финиш
                    else -> Color(0xFF2196F3).toArgb() // Синий - промежуточные
                }

                val strokeColor1 = when (index) {
                    0 -> Color(0xFF2E7D32).toArgb()
                    points.lastIndex -> Color(0xFFC62828).toArgb()
                    else -> Color(0xFF1565C0).toArgb()
                }

                createCircle(point, radius = 20f, color = color, strokeColor1 = strokeColor1)
            }

            // Добавляем подписи ко всем точкам
//            points.forEachIndexed { index, point ->
//                val label = when (index) {
//                    0 -> "СТАРТ"
//                    points.lastIndex -> "ФИНИШ"
//                    else -> "Точка ${index + 1}"
//                }
//                map.mapObjects.addPlacemark().apply {
//                    geometry = point
//                    setText(label, TextStyle())
//                }
//            }

            Auditor.debug("d", "Pedestrian/Bicycle route created with ${points.size} points")
            // Для пешеходного/велосипедного маршрута возвращаем null, так как расчеты будем делать отдельно
            onSuccess(null as DrivingRoute)
            return
        }

        // Автомобильный маршрут
        val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute?>) {
                Auditor.debug("d", "Successful driving route")

                val selectedRoute = drivingRoutes.firstOrNull()
                val routePoints = selectedRoute?.geometry?.points ?: emptyList()
                val polyline = Polyline(routePoints)

                // Желтый цвет для автомобильного маршрута
                val strokeColor = Color(0xFFFFC107)
                val outlineColor1 = Color(0xFFE65100)

                val polylineObj = map.mapObjects.addPolyline(polyline).apply {
                    strokeWidth = 6f
                    setStrokeColor(strokeColor.toArgb())
                    outlineWidth = 2f
                    outlineColor = outlineColor1.toArgb()
                }

                mapObjects.add(polylineObj)

                // Добавляем метки на все точки маршрута
                points.forEachIndexed { index, point ->
                    val color = when (index) {
                        0 -> Color(0xFF4CAF50).toArgb() // Зеленый - старт
                        points.lastIndex -> Color(0xFFF44336).toArgb() // Красный - финиш
                        else -> Color(0xFF2196F3).toArgb() // Синий - промежуточные
                    }

                    val strokeColor1 = when (index) {
                        0 -> Color(0xFF2E7D32).toArgb()
                        points.lastIndex -> Color(0xFFC62828).toArgb()
                        else -> Color(0xFF1565C0).toArgb()
                    }

                    createCircle(point, radius = 20f, color = color, strokeColor1 = strokeColor1)
                }

                // Добавляем подписи ко всем точкам
                points.forEachIndexed { index, point ->
                    val label = when (index) {
                        0 -> "СТАРТ"
                        points.lastIndex -> "ФИНИШ"
                        else -> "Точка ${index + 1}"
                    }
                    map.mapObjects.addPlacemark().apply {
                        geometry = point
                        setText(label, TextStyle())
                    }
                }

                Auditor.debug("d", "Driving route points count: ${routePoints.size}")
                Auditor.debug("d", calculateRouteDistance(selectedRoute!!).toString())
                Auditor.debug("d", calculateRouteTime(selectedRoute).toString())
                onSuccess(selectedRoute)
            }

            override fun onDrivingRoutesError(error: com.yandex.runtime.Error) {
                Auditor.debug("d", "Driving routing error: $error")
                onFailure(error)
            }
        }

        Auditor.debug("d", "Requesting driving route")
        drivingRouter.requestRoutes(rPoints, drivingOptions, vehicleOptions, drivingRouteListener)
    }
}

@Composable
fun YandexMapKit(
    modifier: Modifier = Modifier,
    controller: MapKitController
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = {
            MapView(it).apply {
                controller.setContext(context)
                controller.setMap(mapWindow.map)
                controller.moveTo(Point(47.2357, 39.7015)) // Ростов-на-Дону
            }
        }
    ) { mapView -> Auditor.debug("d", "Updating map") }
}