package app.what.investtravel.features.travel.domain

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.foundation.data.RemoteState
import app.what.investtravel.R
import app.what.investtravel.data.local.database.PointsDao
import app.what.investtravel.data.local.database.RoutesDAO
import app.what.investtravel.data.local.mappers.toEntity
import app.what.investtravel.data.local.mappers.toPointEntities
import app.what.investtravel.data.remote.AiRouteRequest
import app.what.investtravel.data.remote.AiService
import app.what.investtravel.data.remote.GenerateCommentRequest
import app.what.investtravel.data.remote.RoutesService
import app.what.investtravel.data.remote.utils.toRoute
import app.what.investtravel.features.travel.domain.models.Travel
import app.what.investtravel.features.travel.domain.models.TravelAction
import app.what.investtravel.features.travel.domain.models.TravelEvent
import app.what.investtravel.features.travel.domain.models.TravelObject
import app.what.investtravel.features.travel.domain.models.TravelState
import app.what.investtravel.features.travel.presentation.pages.UserPreferences
import app.what.investtravel.ui.components.MapKitController
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TravelController(
    private val getImageProvider: (Int) -> ImageProvider
) : UIController<TravelState, TravelAction, TravelEvent>(
    TravelState()
) {
    val routeService: RoutesService by inject(RoutesService::class.java)
    val pointsDao: PointsDao by inject(PointsDao::class.java)
    val routesDao: RoutesDAO by inject(RoutesDAO::class.java)

    val aiService: AiService by inject(AiService::class.java)

    val mapController = MapKitController()

    override fun obtainEvent(viewEvent: TravelEvent) = when (viewEvent) {
        is TravelEvent.UpdateObjectChecked -> updateObjectChecked(
            viewEvent.travel,
            viewEvent.objectIndex,
            viewEvent.checked
        )

        is TravelEvent.TravelSelected -> selectTravel(viewEvent.value, viewEvent.transportType)
        is TravelEvent.TravelUnselected -> updateState { copy(selectedTravel = null) }
        is TravelEvent.SaveTravel -> sendTravel(viewEvent.value)
        is TravelEvent.SaveAiTravel -> sendAiTravel(viewEvent.value)
        is TravelEvent.DeleteTravel -> deleteTravel(viewEvent.value)
        is TravelEvent.FetchTravels -> fetchTravels()
        is TravelEvent.SetToAi -> sendToAi(viewEvent.value)
        is TravelEvent.ShowSheet -> updateState { copy(showSheet = false) }
        else -> {}
    }

    private fun handleRouteSuccess(
        route: Any?,
        routePoints: List<Point>,
        transportType: String,
        value: Travel
    ) {
        try {
            Log.d(
                "TravelController",
                "Route created successfully on selection with transport: $transportType"
            )

            // Рассчитываем расстояние и время одинаково для всех типов транспорта
            val calculatedDistance =
                MapKitController.calculateDirectDistance(routePoints) / 1000 // в км
            val calculatedTime = MapKitController.calculateTimeByTransport(
                calculatedDistance * 1000,
                when (transportType) {
                    "walking" -> "walking"
                    "bicycle" -> "bicycle"
                    else -> "car"
                }
            ) / 60 // в часах

            Log.d(
                "TravelController",
                "Calculated distance: $calculatedDistance km, time: $calculatedTime hours"
            )

            // Обновляем состояние с рассчитанными значениями
            updateState {
                copy(
                    selectedTravel = value.copy(
                        distance = calculatedDistance,
                        time = calculatedTime
                    )
                )
            }

            // Камера перейдет к началу маршрута
            val firstPoint = routePoints.first()
            mapController.animateMoveTo(firstPoint, zoom = 13f)
        } catch (e: Exception) {
            Log.e("TravelController", "Error in handleRouteSuccess", e)
        }
    }

    private fun selectTravel(value: Travel, transportType: String = "driving") {
        updateState { copy(selectedTravel = value) }

        // Очищаем карту и строим маршрут для выбранного путешествия
        viewModelScope.launch(Dispatchers.Main) {
            mapController.clear()

            // Показываем все точки на карте (включая посещенные)
            value.objects.forEachIndexed { index, obj ->
                val color =
                    if (obj.checked) Color(0xFF9E9E9E).toArgb() else Color(0xFF2196F3).toArgb() // Серый для посещенных
                val strokeColor =
                    if (obj.checked) Color(0xFF616161).toArgb() else Color(0xFF1565C0).toArgb()
                mapController.createCircle(
                    Point(obj.lat, obj.lon),
                    radius = 20f,
                    color = color,
                    strokeColor1 = strokeColor
                )

                // Добавляем подпись с названием объекта
                mapController.createPlacemark(
                    Point(obj.lat, obj.lon),
                    getImageProvider(R.drawable.il_green_bush),
                    obj.name
                )
            }

            // Создаем маршрут только из непосещенных объектов
            val routePoints = value.objects.filter { !it.checked }.map { Point(it.lat, it.lon) }

            if (routePoints.isNotEmpty()) {
                try {
                    // Добавляем метки на все точки маршрута
                    routePoints.forEachIndexed { index, point ->
                        val color = when (index) {
                            0 -> Color(0xFF4CAF50).toArgb() // Зеленый - старт
                            routePoints.lastIndex -> Color(0xFFF44336).toArgb() // Красный - финиш
                            else -> Color(0xFF2196F3).toArgb() // Синий - промежуточные
                        }

                        val strokeColor1 = when (index) {
                            0 -> Color(0xFF2E7D32).toArgb()
                            routePoints.lastIndex -> Color(0xFFC62828).toArgb()
                            else -> Color(0xFF1565C0).toArgb()
                        }

                        mapController.createCircle(point, radius = 20f, color = color, strokeColor1 = strokeColor1)
                        mapController.createPlacemark()
                    }

                    if (false) mapController.createRoute(
                        points = routePoints,
                        vehicleType = when (transportType) {
                            "walking" -> "walking"
                            "bicycle" -> "bicycle"
                            else -> "car"
                        },
                        onFailure = { error ->
                            // Убеждаемся, что обработка идет на главном потоке
                            if (Looper.getMainLooper() != Looper.myLooper()) {
                                Handler(Looper.getMainLooper()).post {
                                    Log.e(
                                        "TravelController",
                                        "Error creating route on selection: $error",
                                        Exception()
                                    )
                                }
                            } else {
                                Log.e(
                                    "TravelController",
                                    "Error creating route on selection: $error",
                                    Exception()
                                )
                            }
                        },
                        onSuccess = { route ->
                            // Убеждаемся, что обработка идет на главном потоке
                            if (Looper.getMainLooper() != Looper.myLooper()) {
                                Handler(Looper.getMainLooper()).post {
                                    handleRouteSuccess(route, routePoints, transportType, value)
                                }
                            } else {
                                handleRouteSuccess(route, routePoints, transportType, value)
                            }
                        }
                    )
                } catch (e: Exception) {
                    Log.e("TravelController", "Exception creating route on selection", e)
                }
            }
        }
    }

    private fun sendToAi(value: TravelObject) {
        updateState { copy(showSheet = true, aiComment = "") }
        viewModelScope.launch(Dispatchers.IO) {
            aiService.generateComment(GenerateCommentRequest(value.name)).onSuccess {
                updateState { copy(aiComment = it.comment) }
            }.onFailure {
                updateState { copy(showSheet = false) }
            }
        }
    }

    private fun sendTravel(userPreferences: UserPreferences) {
        Log.d("TravelController", "sendTravel called with preferences: $userPreferences")
        updateState { copy(travelsFetchState = RemoteState.Loading) }

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TravelController", "Calling routeService.generateRoute")
            routeService.generateRoute(userPreferences.toRoute()).onSuccess { routeResponse ->
                Log.d("TravelController", "Route generation successful: ${routeResponse.name}")
                try {
                    // Генерируем маршрут на карте и получаем реальные данные расстояния и времени
                    val routePoints = routeResponse.points.map {
                        Point(it.latitude, it.longitude)
                    }

                    // Создаем маршрут на карте (должен быть на Main dispatcher)
                    // MapKit Controller уже сам добавит полилинию на карту при вызове createRoute
                    Log.d("TravelController", "Creating map route with ${routePoints.size} points")
                    val route = try {
                        createMapRoute(routePoints, "car")
                    } catch (e: Exception) {
                        Log.e(
                            "TravelController",
                            "Failed to create map route, using fallback calculation",
                            e
                        )
                        // Если создание маршрута на карте не удалось, используем расчет по прямой
                        null
                    }

                    val calculatedDistance = if (route != null) {
                        MapKitController.calculateRouteDistance(route) / 1000 // в км
                    } else {
                        MapKitController.calculateDirectDistance(routePoints) / 1000 // в км
                    }

                    val calculatedTime = if (route != null) {
                        MapKitController.calculateRouteTime(route) / 60 // в часах
                    } else {
                        MapKitController.calculateTimeByTransport(
                            calculatedDistance * 1000,
                            "car"
                        ) / 60 // в часах
                    }

                    Log.d(
                        "TravelController",
                        "Calculated distance: $calculatedDistance km, time: $calculatedTime hours"
                    )

                    // Обновляем данные с рассчитанными значениями
                    val updatedRoute = routeResponse.copy(
                        totalDistanceKm = calculatedDistance.toFloat(),
                        totalDurationHours = calculatedTime.toFloat()
                    )

                    val respToEntity = updatedRoute.toEntity()
                    val rowId = routesDao.insert(respToEntity)
                    val localId = rowId.toInt()

                    Log.d(
                        "TravelController",
                        "Inserted route with localId: $localId (rowId: $rowId)"
                    )

                    val pointsToEntity = updatedRoute.points.toPointEntities(localId)
                    pointsDao.insert(pointsToEntity)

                    Log.d(
                        "TravelController",
                        "Inserted ${pointsToEntity.size} points for route $localId"
                    )

                    // Загружаем обновленный список маршрутов
                    Log.d("TravelController", "Calling refreshTravelsList after route creation")
                    refreshTravelsList()
                } catch (e: Exception) {
                    Log.e("TravelController", "Error saving travel to DB", e)
                    e.printStackTrace()
                    updateState { copy(travelsFetchState = RemoteState.Error(Exception())) }
                }
            }.onFailure { e ->
                Log.e("TravelController", "Error generating route", e)
                e.printStackTrace()
                updateState { copy(travelsFetchState = RemoteState.Error(Exception(e))) }
            }
        }
    }

    // Создает маршрут на карте и возвращает DrivingRoute
    private suspend fun createMapRoute(
        points: List<Point>,
        vehicleType: String = "car"
    ): com.yandex.mapkit.directions.driving.DrivingRoute =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { continuation ->
                try {
                    mapController.createRoute(
                        points = points,
                        vehicleType = vehicleType,
                        onFailure = { error ->
                            Log.e("TravelController", "Error creating map route: $error")
                            continuation.resumeWithException(Exception("Failed to create map route: $error"))
                        },
                        onSuccess = { route ->
                            Log.d("TravelController", "Map route created successfully")
                            if (route != null) {
                                continuation.resume(route)
                            } else {
                                // Для walking/bicycle маршрутов route будет null, это нормально
                                if (vehicleType == "walking" || vehicleType == "bicycle") {
                                    continuation.resumeWithException(Exception("Walking/bicycle routes don't return DrivingRoute"))
                                } else {
                                    continuation.resumeWithException(Exception("Route is null"))
                                }
                            }
                        }
                    )
                } catch (e: Exception) {
                    Log.e("TravelController", "Exception in createMapRoute", e)
                    continuation.resumeWithException(e)
                }
            }
        }

    fun updateObjectChecked(travel: Travel, objectIndex: Int, checked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Находим объект в базе данных и обновляем его статус
                val routeWithPoints = routesDao.selectAllRoutes().find {
                    it.route.name == travel.name
                }

                if (routeWithPoints != null) {
                    val pointToUpdate = routeWithPoints.points[objectIndex]
                    pointsDao.updateChecked(pointToUpdate.localId, checked)

                    Log.d(
                        "TravelController",
                        "Updated object ${pointToUpdate.name} checked status to $checked"
                    )

                    // Обновляем состояние
                    withContext(Dispatchers.Main) {
                        val updatedObjects = travel.objects.toMutableList()
                        updatedObjects[objectIndex] =
                            updatedObjects[objectIndex].copy(checked = checked)
                        val updatedTravel = travel.copy(objects = updatedObjects)

                        updateState {
                            copy(selectedTravel = updatedTravel)
                        }

                        // Перестраиваем маршрут
                        selectTravel(updatedTravel, "driving")
                    }
                }
            } catch (e: Exception) {
                Log.e("TravelController", "Error updating object checked status", e)
            }
        }
    }

    fun fetchTravels() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TravelController", "fetchTravels called")
            updateState { copy(travelsFetchState = RemoteState.Loading) }
            try {
                val routesWithPoints = routesDao.selectAllRoutes()
                Log.d("TravelController", "Fetched ${routesWithPoints.size} routes from DB")

                val travels = routesWithPoints.map { routeWithPoints ->
                    Log.d(
                        "TravelController",
                        "Route: ${routeWithPoints.route.name}, points: ${routeWithPoints.points.size}"
                    )
                    Travel(
                        name = routeWithPoints.route.name ?: "Маршрут ${routeWithPoints.route.id}",
                        distance = routeWithPoints.route.totalDistanceKm.toDouble(),
                        time = routeWithPoints.route.totalDurationHours.toDouble(),
                        objects = routeWithPoints.points.map { point ->
                            TravelObject(
                                bannerUri = point.imageUrl
                                    ?: "https://via.placeholder.com/300x200/4CAF50/FFFFFF?text=${point.name}",
                                name = point.name ?: "Точка ${point.order}",
                                type = point.category ?: "Точка",
                                lat = point.latitude,
                                lon = point.longitude,
                                address = point.address,
                                description = point.description,
                                durationMinutes = point.durationMinutes,
                                arrivalTime = point.arrivalTime,
                                departureTime = point.departureTime,
                                checked = point.checked // Загружаем статус посещения
                            )
                        }
                    )
                }

                Log.d("TravelController", "Mapped to ${travels.size} Travel objects")
                // Если маршруты уже загружены, не меняем состояние загрузки
                val currentState = viewState
                if (currentState.travelsFetchState == RemoteState.Loading && currentState.travels.isEmpty()) {
                    updateState { copy(travelsFetchState = RemoteState.Success, travels = travels) }
                } else {
                    updateState { copy(travels = travels) }
                }
                Log.d("TravelController", "State updated with ${travels.size} travels")
            } catch (e: Exception) {
                Log.e("TravelController", "Error fetching travels", e)
                e.printStackTrace()
                updateState { copy(travelsFetchState = RemoteState.Error(e)) }
            }
        }
    }

    private fun sendAiTravel(aiRouteRequest: AiRouteRequest) {
        updateState { copy(travelsFetchState = RemoteState.Loading) }

        viewModelScope.launch(Dispatchers.IO) {
            aiService.generateAiRoute(aiRouteRequest).onSuccess { aiRouteResponse ->
                try {
                    if (!aiRouteResponse.success) {
                        Log.e(
                            "TravelController",
                            "AI route generation failed: ${aiRouteResponse.errorMessage}"
                        )
                        updateState {
                            copy(
                                travelsFetchState = RemoteState.Error(
                                    Exception(
                                        aiRouteResponse.errorMessage ?: "AI route generation failed"
                                    )
                                )
                            )
                        }
                        return@onSuccess
                    }

                    val routeResponse = aiRouteResponse.route
                    Log.d(
                        "TravelController",
                        "AI route generated successfully: ${routeResponse.name}"
                    )
                    Log.d(
                        "TravelController",
                        "AI recommendations: ${aiRouteResponse.aiRecommendations}"
                    )

                    // Генерируем маршрут на карте и получаем реальные данные расстояния и времени
                    val routePoints = routeResponse.points.map {
                        Point(it.latitude, it.longitude)
                    }

                    // Создаем маршрут на карте
                    Log.d(
                        "TravelController",
                        "Creating AI map route with ${routePoints.size} points"
                    )
                    val route = try {
                        createMapRoute(routePoints, "car")
                    } catch (e: Exception) {
                        Log.e(
                            "TravelController",
                            "Failed to create AI map route, using fallback calculation",
                            e
                        )
                        // Если создание маршрута на карте не удалось, используем расчет по прямой
                        null
                    }

                    val calculatedDistance = if (route != null) {
                        MapKitController.calculateRouteDistance(route) / 1000 // в км
                    } else {
                        MapKitController.calculateDirectDistance(routePoints) / 1000 // в км
                    }

                    val calculatedTime = if (route != null) {
                        MapKitController.calculateRouteTime(route) / 60 // в часах
                    } else {
                        MapKitController.calculateTimeByTransport(
                            calculatedDistance * 1000,
                            "car"
                        ) / 60 // в часах
                    }

                    Log.d(
                        "TravelController",
                        "AI Calculated distance: $calculatedDistance km, time: $calculatedTime hours"
                    )

                    // Обновляем данные с рассчитанными значениями
                    val updatedRoute = routeResponse.copy(
                        totalDistanceKm = calculatedDistance.toFloat(),
                        totalDurationHours = calculatedTime.toFloat()
                    )

                    val respToEntity = updatedRoute.toEntity()
                    val rowId = routesDao.insert(respToEntity)
                    val localId = rowId.toInt()

                    Log.d(
                        "TravelController",
                        "Inserted AI route with localId: $localId (rowId: $rowId)"
                    )

                    val pointsToEntity = updatedRoute.points.toPointEntities(localId)
                    pointsDao.insert(pointsToEntity)

                    Log.d(
                        "TravelController",
                        "Inserted ${pointsToEntity.size} points for AI route $localId"
                    )

                    // Загружаем обновленный список маршрутов
                    refreshTravelsList()
                } catch (e: Exception) {
                    Log.e("TravelController", "Error saving AI travel to DB", e)
                    e.printStackTrace()
                    updateState { copy(travelsFetchState = RemoteState.Error(Exception())) }
                }
            }.onFailure { e ->
                Log.e("TravelController", "Error generating AI route", e)
                updateState { copy(travelsFetchState = RemoteState.Error(Exception())) }
            }
        }
    }

    private fun deleteTravel(travel: Travel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Находим маршрут в базе данных по имени и удаляем его
                val routesWithPoints = routesDao.selectAllRoutes()
                val routeToDelete = routesWithPoints.find { routeWithPoints ->
                    routeWithPoints.route.name == travel.name
                }

                if (routeToDelete != null) {
                    // Удаляем точки маршрута
                    pointsDao.deleteByRouteId(routeToDelete.route.localId)
                    // Удаляем сам маршрут
                    routesDao.delete(routeToDelete.route)

                    Log.d("TravelController", "Deleted travel: ${travel.name}")

                    // Обновляем список маршрутов
                    refreshTravelsList()
                } else {
                    Log.w("TravelController", "Travel not found in database: ${travel.name}")
                }
            } catch (e: Exception) {
                Log.e("TravelController", "Error deleting travel", e)
                e.printStackTrace()
            }
        }
    }

    // Обновляет список маршрутов без изменения состояния загрузки
    private fun refreshTravelsList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("TravelController", "Starting refreshTravelsList")
                val routesWithPoints = routesDao.selectAllRoutes()
                Log.d("TravelController", "Refreshed ${routesWithPoints.size} routes from DB")

                val travels = routesWithPoints.map { routeWithPoints ->
                    Travel(
                        name = routeWithPoints.route.name ?: "Маршрут ${routeWithPoints.route.id}",
                        distance = routeWithPoints.route.totalDistanceKm.toDouble(),
                        time = routeWithPoints.route.totalDurationHours.toDouble(),
                        objects = routeWithPoints.points.map { point ->
                            TravelObject(
                                bannerUri = point.imageUrl
                                    ?: "https://via.placeholder.com/300x200/4CAF50/FFFFFF?text=${point.name}"
                                    ?: "https://mospravda.ru/wp-content/uploads/2024/10/pamyatnik-gagarinu-_-leninskiy-prospekt.jpg",
                                name = point.name ?: "Точка ${point.order}",
                                type = point.category ?: "Точка",
                                lat = point.latitude,
                                lon = point.longitude,
                                address = point.address,
                                description = point.description,
                                durationMinutes = point.durationMinutes,
                                arrivalTime = point.arrivalTime,
                                departureTime = point.departureTime,
                                checked = point.checked // Загружаем статус посещения
                            )
                        }
                    )
                }

                Log.d("TravelController", "Refreshed to ${travels.size} Travel objects")
                // Обновляем только список маршрутов, не меняя состояние загрузки
                updateState { copy(travels = travels) }
                Log.d("TravelController", "State updated with ${travels.size} travels")
            } catch (e: Exception) {
                Log.e("TravelController", "Error refreshing travels", e)
                e.printStackTrace()
                updateState { copy(travelsFetchState = RemoteState.Error(e)) }
            }
        }
    }
}