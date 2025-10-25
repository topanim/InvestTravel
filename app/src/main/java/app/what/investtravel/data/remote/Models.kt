package app.what.investtravel.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Auth Models
@Serializable
data class LoginRequest(
    @SerialName("login") val login: String,
    @SerialName("password") val password: String
)

@Serializable
data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String = "bearer"
)

// User Models
@Serializable
data class UserCreate(
    @SerialName("login") val login: String,
    @SerialName("role_id") val roleId: Int? = 0,
    @SerialName("name") val name: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("password") val password: String = ""
)

@Serializable
data class UserGet(
    @SerialName("id") val id: Int,
    @SerialName("login") val login: String,
    @SerialName("role") val role: Role,
    @SerialName("name") val name: String?
)

@Serializable
data class UserMoreModel(
    @SerialName("id") val id: Int,
    @SerialName("login") val login: String,
    @SerialName("email") val email: String?
)

// Role Models
@Serializable
data class Role(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

@Serializable
data class RoleCreate(
    @SerialName("name") val name: String
)

@Serializable
data class RoleGet(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

// Route Models
@Serializable
data class RouteRequest(
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("food_time") val foodTime: Int = 0,
    @SerialName("restaurant") val restaurant: Int = 0,
    @SerialName("fast_food_time") val fastFoodTime: Int = 0,
    @SerialName("cafe_time") val cafeTime: Int = 0,
    @SerialName("bar_time") val barTime: Int = 0,
    @SerialName("tourism_time") val tourismTime: Int = 0,
    @SerialName("tourism") val tourism: Int = 0,
    @SerialName("art_time") val artTime: Int = 0,
    @SerialName("art") val art: Int = 0,
    @SerialName("leisure_time") val leisureTime: Int = 0,
    @SerialName("shopping_time") val shoppingTime: Int = 0,
    @SerialName("meals_per_day") val mealsPerDay: Int = 3,
    @SerialName("start_latitude") val startLatitude: Double,
    @SerialName("start_longitude") val startLongitude: Double,
    @SerialName("max_distance_km") val maxDistanceKm: Double = 50.0,
    @SerialName("prefer_nearby") val preferNearby: Boolean = true,
    @SerialName("avoid_night_time") val avoidNightTime: Boolean = true,
    @SerialName("require_food_points") val requireFoodPoints: Boolean = true
)

@Serializable
data class RouteResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("start_date") val startDate: String,
    @SerialName("end_date") val endDate: String,
    @SerialName("total_duration_hours") val totalDurationHours: Double,
    @SerialName("total_distance_km") val totalDistanceKm: Double,
    @SerialName("total_objects") val totalObjects: Int,
    @SerialName("categories_covered") val categoriesCovered: List<String>,
    @SerialName("points") val points: List<RoutePointResponse>,
    @SerialName("route_summary") val routeSummary: Map<String, String> // TODO: Map<String, Any>
)

@Serializable
data class RoutePointResponse(
    @SerialName("order") val order: Int,
    @SerialName("name") val name: String,
    @SerialName("category") val category: String,
    @SerialName("subcategory") val subcategory: String?,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("address") val address: String?,
    @SerialName("arrival_time") val arrivalTime: String,
    @SerialName("departure_time") val departureTime: String,
    @SerialName("duration_minutes") val durationMinutes: Int,
    @SerialName("travel_time_minutes") val travelTimeMinutes: Int?,
    @SerialName("description") val description: String?,
    @SerialName("distance_to_next_km") val distanceToNextKm: Double?
)

@Serializable
data class RouteOptimizationRequest(
    @SerialName("route_id") val routeId: Int,
    @SerialName("optimization_type") val optimizationType: String = "distance",
    @SerialName("constraints") val constraints: Map<String, String> = emptyMap()// TODO: Map<String, Any>
)

@Serializable
data class RouteStats(
    @SerialName("total_routes") val totalRoutes: Int,
    @SerialName("active_routes") val activeRoutes: Int,
    @SerialName("total_distance_km") val totalDistanceKm: Double,
    @SerialName("average_route_duration") val averageRouteDuration: Double,
    @SerialName("most_popular_categories") val mostPopularCategories: List<String>,
    @SerialName("routes_by_month") val routesByMonth: Map<String, Int>
)

// Common Models
@Serializable
data class ErrorResponse(
    @SerialName("detail") val detail: List<ValidationError>
)

@Serializable
data class ValidationError(
    @SerialName("loc") val location: List<String>, // TODO: List<Any>
    @SerialName("msg") val message: String,
    @SerialName("type") val type: String
)