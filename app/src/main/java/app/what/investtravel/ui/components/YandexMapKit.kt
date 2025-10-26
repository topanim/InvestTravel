package app.what.investtravel.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider

class MapKitController {
    private var _map: Map? = null

    private val map get() = _map!!

    fun setMap(value: Map) {
        _map = value
        map.mapObjects.conflictResolutionMode = ConflictResolutionMode.IGNORE
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
            return 300.0
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

    fun createRoute(
        points: List<Point>,
        vehicleOptions: VehicleOptions = VehicleOptions(),
        drivingOptions: DrivingOptions = DrivingOptions().apply { routesCount = 1 },
        onFailure: (Error) -> Unit = { Auditor.debug("d", it.toString()) },
        onSuccess: (DrivingRoute) -> Unit
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

        val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute?>) {
                Auditor.debug("d", "Successful navigation marshroute")

                val selectedRoute = drivingRoutes.firstOrNull()
                val routePoints = selectedRoute?.geometry?.points ?: emptyList()
                val polyline = Polyline(routePoints)
                val strokeColor = Color(0xFF3F51B5) // Пример цвета
                val outlineColor1 = Color(0xFF1A237E)
                val polylineObj = map.mapObjects.addPolyline(polyline).apply {
                    strokeWidth = 5f
                    setStrokeColor(strokeColor.toArgb())
                    outlineWidth = 1f
                    outlineColor = outlineColor1.toArgb()
                }

                mapObjects.add(polylineObj)

                Auditor.debug("d", "Points count: ${routePoints.size}")
                Auditor.debug("d", calculateRouteDistance(selectedRoute!!).toString())
                Auditor.debug("d", calculateRouteTime(selectedRoute).toString())
                onSuccess(drivingRoutes.firstOrNull()!!)
            }

            override fun onDrivingRoutesError(error: com.yandex.runtime.Error) {
                Auditor.debug("d", "Routing error: $error")
                onFailure(error)
            }
        }

        Auditor.debug("d", "Requesting navigation marshroute")

        drivingRouter.requestRoutes(
            rPoints,
            drivingOptions,
            vehicleOptions,
            drivingRouteListener
        )
    }
}

@Composable
fun YandexMapKit(
    modifier: Modifier = Modifier,
    controller: MapKitController
) {
    AndroidView(
        modifier = modifier,
        factory = {
            MapView(it).apply {
                controller.setMap(mapWindow.map)
                controller.moveTo(Point(47.2357, 39.7015)) // Ростов-на-Дону
            }
        }
    ) { mapView -> Auditor.debug("d", "Updating map") }
}