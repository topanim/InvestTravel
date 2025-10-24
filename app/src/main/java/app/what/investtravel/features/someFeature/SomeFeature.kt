package app.what.investtravel.features.someFeature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.what.foundation.services.AppLogger.Companion.Auditor
import app.what.foundation.ui.controllers.rememberSheetController
import app.what.investtravel.ui.components.PlaceCard
import app.what.investtravel.ui.components.YandexMapKit
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point

class Some(
    val onP: (List<Point>) -> Unit
) {
    init {
        val drivingRouter =
            DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        val vehicleOptions = VehicleOptions()
        val drivingOptions = DrivingOptions().apply {
            routesCount = 3
        }
        val points = buildList {
            add(
                RequestPoint(
                    Point(39.712609,47.236833),
                    RequestPointType.WAYPOINT,
                    null,
                    null,
                    null
                )
            )
            add(
                RequestPoint(
                    Point(39.716081, 47.254877),
                    RequestPointType.WAYPOINT,
                    null,
                    null,
                    null
                )
            )
        }
        val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute?>) {
                Auditor.debug("d", "Successful navigation marshroute")
                val points = drivingRoutes.map { it?.geometry?.points ?: emptyList() }.flatten()
                Auditor.debug("d", points.toString())
                onP(points)
            }

            override fun onDrivingRoutesError(error: com.yandex.runtime.Error) {
                Auditor.debug("d", error.toString())
            }
        }
        val drivingSession = drivingRouter.requestRoutes(
            points,
            drivingOptions,
            vehicleOptions,
            drivingRouteListener
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SomeFeature() = Column {
    val navigationPoints = remember { mutableStateListOf<Point>() }
    val some = remember {
        Some {
            navigationPoints.clear()
            navigationPoints.addAll(it)
        }
    }

    YandexMapKit(
        darkMode = true,
        points = navigationPoints,
        onResetFocus = { },
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
    ) { _, point -> true }

    val controller = rememberSheetController()

    controller.open(){
        PlaceCard("https://tobolinfo.kz/wp-content/uploads/2019/06/IMG_20190526_161606-1500x2000.jpg",
            "Туалет",
            "Туалет",
            false
        )
    }

}