package app.what.investtravel.data.local.mappers

import app.what.investtravel.data.local.entity.RouteEntity
import app.what.investtravel.data.local.entity.RoutePointEntity
import app.what.investtravel.data.remote.RoutePointResponse
import app.what.investtravel.data.remote.RouteResponse
import kotlinx.serialization.json.Json

fun RouteResponse.toEntity(): RouteEntity {
    return RouteEntity(
        id = id,
        name = name,
        description = description,
        startDate = startDate,
        endDate = endDate,
        totalDurationHours = totalDurationHours,
        totalDistanceKm = totalDistanceKm,
        totalObjects = totalObjects,
        categoriesCovered = Json.encodeToString(categoriesCovered)
    )
}
fun List<RoutePointResponse>.toPointEntities(localRouteId: Int): List<RoutePointEntity> {
    return this.map {
        RoutePointEntity(
            routeId = localRouteId,
            order = it.order,
            name = it.name,
            category = it.category,
            subcategory = it.subcategory,
            latitude = it.latitude,
            longitude = it.longitude,
            address = it.address,
            arrivalTime = it.arrivalTime,
            departureTime = it.departureTime,
            durationMinutes = it.durationMinutes,
            travelTimeMinutes = it.travelTimeMinutes,
            description = it.description,
            distanceToNextKm = it.distanceToNextKm,
            imageUrl = it.imageUrl, // Сохраняем URL изображения
            checked = false // По умолчанию не посещено
        )
    }
}