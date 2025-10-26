package app.what.investtravel.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val id: Int,
    val name: String? = null,
    val description: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val totalDurationHours: Float = 0f,
    val totalDistanceKm: Float = 0f,
    val totalObjects: Int = 0,
    val categoriesCovered: String? = null, // JSON
    val routeSummary: String? = null // JSON
)

@Entity(tableName = "route_points")
data class RoutePointEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val routeId: Int, // связываем с RouteEntity.localId
    val order: Int = 0,
    val name: String? = null,
    val category: String? = null,
    val subcategory: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String? = null,
    val arrivalTime: String? = null,
    val departureTime: String? = null,
    val durationMinutes: Int = 0,
    val travelTimeMinutes: Int? = 0,
    val description: String? = null,
    val distanceToNextKm: Float? = null,
    val imageUrl: String? = null, // URL изображения баннера
    val checked: Boolean = false // Отметка о посещении
)

data class RouteWithPoints(
    @Embedded val route: RouteEntity,
    @Relation(
        parentColumn = "localId", // PK таблицы RouteEntity
        entityColumn = "routeId"  // FK в RoutePointEntity
    )
    val points: List<RoutePointEntity>
)