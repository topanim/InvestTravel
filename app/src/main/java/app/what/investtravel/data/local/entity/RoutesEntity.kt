package app.what.investtravel.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val id: Int,
    val name: String?,
    val description: String?,
    val startDate: String?,
    val endDate: String?,
    val totalDurationHours: Int?,
    val totalDistanceKm: Int?,
    val totalObjects: Int?,
    val categoriesCovered: String?, // Сохраняем как JSON-строку
    val routeSummary: String? = null // тоже как JSON
)
@Entity(tableName = "route_points")
data class RoutePointEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val routeId: Int?, // внешний ключ на RouteEntity.id
    val order: Int?,
    val name: String?,
    val category: String?,
    val subcategory: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val arrivalTime: String?,
    val departureTime: String?,
    val durationMinutes: Int?,
    val travelTimeMinutes: Int?,
    val description: String?,
    val distanceToNextKm: Int?
)
data class RouteWithPoints(
    @Embedded val route: RouteEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "routeId"
    )
    val points: List<RoutePointEntity>
)