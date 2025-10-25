package app.what.investtravel.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class HotelListResponse(
    @SerialName("hotels") val hotels: List<HotelResponse>,
    @SerialName("total") val total: Int,
    @SerialName("page") val page: Int,
    @SerialName("size") val size: Int,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class HotelResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("address") val address: String,
    @SerialName("city") val city: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("phone") val phone: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("website") val website: String? = null,
    @SerialName("stars") val stars: Int = 3,
    @SerialName("price_per_night") val pricePerNight: Float,
    @SerialName("currency") val currency: String = "RUB",
    @SerialName("amenities") val amenities: List<String>? = null,
    @SerialName("images") val images: List<String>? = null,
    @SerialName("status") val status: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
enum class HotelStatus {
    @SerialName("active") ACTIVE,
    @SerialName("inactive") INACTIVE,
    @SerialName("maintenance") MAINTENANCE
}

// Booking Models
@Serializable
data class HotelBookingRequest(
    @SerialName("hotel_id") val hotelId: Int,
    @SerialName("check_in_date") val checkInDate: String,
    @SerialName("check_out_date") val checkOutDate: String,
    @SerialName("guests_count") val guestsCount: Int = 1,
    @SerialName("rooms_count") val roomsCount: Int = 1,
    @SerialName("special_requests") val specialRequests: String? = null
)

@Serializable
data class HotelBookingResponse(
    @SerialName("id") val id: Int,
    @SerialName("hotel_id") val hotelId: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("check_in_date") val checkInDate: String,
    @SerialName("check_out_date") val checkOutDate: String,
    @SerialName("guests_count") val guestsCount: Int,
    @SerialName("rooms_count") val roomsCount: Int,
    @SerialName("total_price") val totalPrice: Double,
    @SerialName("currency") val currency: String,
    @SerialName("status") val status: BookingStatus,
    @SerialName("special_requests") val specialRequests: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
enum class BookingStatus {
    @SerialName("pending") PENDING,
    @SerialName("confirmed") CONFIRMED,
    @SerialName("cancelled") CANCELLED,
    @SerialName("completed") COMPLETED
}

// Payment Models
@Serializable
data class HotelPaymentRequest(
    @SerialName("booking_id") val bookingId: Int,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("payment_provider") val paymentProvider: String? = "mock"
)

@Serializable
data class HotelPaymentResponse(
    @SerialName("id") val id: Int,
    @SerialName("booking_id") val bookingId: Int,
    @SerialName("hotel_id") val hotelId: Int,
    @SerialName("user_id") val userId: Int,
    @SerialName("amount") val amount: Double,
    @SerialName("currency") val currency: String,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("payment_provider") val paymentProvider: String?,
    @SerialName("external_payment_id") val externalPaymentId: String? = null,
    @SerialName("status") val status: PaymentStatus,
    @SerialName("payment_url") val paymentUrl: String? = null,
    @SerialName("failure_reason") val failureReason: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("paid_at") val paidAt: String? = null
)

@Serializable
enum class PaymentStatus {
    @SerialName("pending") PENDING,
    @SerialName("completed") COMPLETED,
    @SerialName("failed") FAILED,
    @SerialName("cancelled") CANCELLED,
    @SerialName("refunded") REFUNDED
}

// User Bookings & Payments Responses
@Serializable
data class UserBookingsResponse(
    @SerialName("bookings") val bookings: List<HotelBookingResponse>,
    @SerialName("total") val total: Int,
    @SerialName("page") val page: Int,
    @SerialName("size") val size: Int
)

@Serializable
data class UserPaymentsResponse(
    @SerialName("payments") val payments: List<HotelPaymentResponse>,
    @SerialName("total") val total: Int,
    @SerialName("page") val page: Int,
    @SerialName("size") val size: Int
)

// Payment Callback
@Serializable
data class PaymentCallbackRequest(
    @SerialName("payment_id") val paymentId: String,
    @SerialName("status") val status: String,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("currency") val currency: String? = null,
    @SerialName("signature") val signature: String? = null
)

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
    @SerialName("total_duration_hours") val totalDurationHours: Int,
    @SerialName("total_distance_km") val totalDistanceKm: Int,
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
    @SerialName("distance_to_next_km") val distanceToNextKm: Int
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
@Serializable
data class GenerateCommentRequest(
    @SerialName("text") val text: String
)
@Serializable
data class GenerateCommentResponse(
    @SerialName("comment") val comment: String,
    @SerialName("success") val success: Boolean,
)
