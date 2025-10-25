package app.what.investtravel.data.remote


import app.what.investtravel.data.local.settings.AppValues
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType


class HotelsService(
    private val apiClient: ApiClient,
    private val appValues: AppValues
) {

    // Search Hotels
    suspend fun searchHotels(
        city: String,
        checkIn: String? = null,
        checkOut: String? = null,
        guests: Int = 1,
        rooms: Int = 1,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        stars: String? = null,
        amenities: String? = null,
        page: Int = 1,
        size: Int = 10
    ): Result<HotelListResponse> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/hotels/search") {
                parameter("token", appValues.authToken.get())
                parameter("city", city)
                checkIn?.let { parameter("check_in", it) }
                checkOut?.let { parameter("check_out", it) }
                parameter("guests", guests)
                parameter("rooms", rooms)
                minPrice?.let { parameter("min_price", it) }
                maxPrice?.let { parameter("max_price", it) }
                stars?.let { parameter("stars", it) }
                amenities?.let { parameter("amenities", it) }
                parameter("page", page)
                parameter("size", size)
            }.body()
        }
    }

    // Get Hotels by City
    suspend fun getHotelsByCity(
        city: String,
        page: Int = 1,
        size: Int = 10
    ): Result<HotelListResponse> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/hotels/city/$city") {
                parameter("page", page)
                parameter("size", size)
                parameter("token", appValues.authToken.get())
            }.body()
        }
    }

    // Get Hotel by ID
    suspend fun getHotelById(hotelId: Int): Result<HotelResponse> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/hotels/$hotelId") {
                parameter("token", appValues.authToken.get())
            }.body()
        }
    }

    // Book Hotel
    suspend fun bookHotel(bookingRequest: HotelBookingRequest): Result<HotelBookingResponse> {
        return apiClient.safeRequest {
            post(ApiClient.BASE_URL + "/hotels/book") {
                parameter("token", appValues.authToken.get())
                setBody(bookingRequest)
            }.body()
        }
    }

    // Get My Bookings
    suspend fun getMyBookings(
        page: Int = 1,
        size: Int = 10
    ): Result<UserBookingsResponse> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/hotels/bookings/my") {
                parameter("token", appValues.authToken.get())
                parameter("page", page)
                parameter("size", size)
            }.body()
        }
    }

    // Create Payment
    suspend fun createPayment(paymentRequest: HotelPaymentRequest): Result<HotelPaymentResponse> {
        return apiClient.safeRequest {
            post(ApiClient.BASE_URL + "/hotels/payments/create") {
                parameter("token", appValues.authToken.get())
                setBody(paymentRequest)
            }.body()
        }
    }

    // Get My Payments
    suspend fun getMyPayments(
        page: Int = 1,
        size: Int = 10
    ): Result<UserPaymentsResponse> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/hotels/payments/my") {
                parameter("token", appValues.authToken.get())
                parameter("page", page)
                parameter("size", size)
            }.body()
        }
    }

    // Simulate Payment Success (for testing)
    suspend fun simulatePaymentSuccess(paymentId: Int): Result<Map<String, Any>> {
        return apiClient.safeRequest {
            post(ApiClient.BASE_URL + "/hotels/payments/$paymentId/simulate-success") {
                parameter("token", appValues.authToken.get())
            }.body()
        }
    }

    // Cancel Booking
    suspend fun cancelBooking(bookingId: Int): Result<Map<String, Any>> {
        return apiClient.safeRequest {
            post(ApiClient.BASE_URL + "/hotels/bookings/$bookingId/cancel") {
                parameter("token", appValues.authToken.get())
            }.body()
        }
    }

    // Payment Callback (usually called by payment provider)
    suspend fun paymentCallback(callbackRequest: PaymentCallbackRequest): Result<Map<String, Any>> {
        return apiClient.safeRequest {
            post(ApiClient.BASE_URL + "/hotels/payments/callback") {
                parameter("token", appValues.authToken.get())
                setBody(callbackRequest)
            }.body()
        }
    }
}

class AuthService(
    private val apiClient: ApiClient,
    private val appValues: AppValues
) {
    suspend fun login(loginRequest: LoginRequest): Result<TokenResponse> {
        return apiClient.safeRequest {
            post(ApiClient.BASE_URL + "/auth/login/") {
                parameter("token", appValues.authToken.get())
                setBody(loginRequest)
            }.body()
        }
    }
}

// Users Service
class UsersService(
    private val apiClient: ApiClient,
    private val appValues: AppValues
) {
    suspend fun createUser(userCreate: UserCreate): Result<UserCreate> {
        return apiClient.safeRequest {
            post(ApiClient.BASE_URL + "/users/") {
                parameter("token", appValues.authToken.get())
                setBody(userCreate)
            }.body()
        }
    }

    suspend fun getUsers(): Result<List<UserGet>> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/users/").body()
        }
    }

    suspend fun getUser(userId: Int): Result<UserCreate> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/users/$userId") {
                parameter("token", appValues.authToken.get())
            }.body()
        }
    }

    suspend fun updateUser(userId: Int, userCreate: UserCreate): Result<UserCreate> {
        return apiClient.safeRequest {
            put(ApiClient.BASE_URL + "/users/$userId") {
                parameter("token", appValues.authToken.get())
                setBody(userCreate)
            }.body()
        }
    }

    suspend fun deleteUser(userId: Int): Result<Unit> {
        return apiClient.safeRequest {
            delete(ApiClient.BASE_URL + "/users/$userId") {
                parameter("token", appValues.authToken.get())
            }
        }
    }

    suspend fun getCurrentUser(): Result<UserMoreModel> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/users/user/me") {
                parameter("token", appValues.authToken.get())
            }.body()
        }
    }
}

// Routes Service
class RoutesService(
    private val apiClient: ApiClient,
    private val appValues: AppValues
) {
    suspend fun generateRoute(routeRequest: RouteRequest): Result<RouteResponse> {
        return apiClient.safeRequest {
            post(ApiClient.BASE_URL + "/routes/generate") {
                parameter("token", appValues.authToken.get())
                contentType(ContentType.Application.Json)
                setBody(routeRequest)
            }.body()
        }
    }

    suspend fun getRoutes(
        skip: Int = 0,
        limit: Int = 100,
        category: String? = null,
        search: String? = null
    ): Result<List<RouteResponse>> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/routes/") {
                parameter("token", appValues.authToken.get())
                parameter("skip", skip)
                parameter("limit", limit)
                category?.let { parameter("category", it) }
                search?.let { parameter("search", it) }
            }.body()
        }
    }

    suspend fun getRoute(routeId: Int): Result<RouteResponse> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/routes/$routeId") {
                parameter("token", appValues.authToken.get())
            }.body()
        }
    }

    suspend fun updateRoute(routeId: Int, routeData: Map<String, Any>): Result<RouteResponse> {
        return apiClient.safeRequest {
            put(ApiClient.BASE_URL + "/routes/$routeId") {
                parameter("token", appValues.authToken.get())
                setBody(routeData)
            }.body()
        }
    }

    suspend fun deleteRoute(routeId: Int): Result<Unit> {
        return apiClient.safeRequest {
            delete(ApiClient.BASE_URL + "/routes/$routeId") {
                parameter("token", appValues.authToken.get())
            }
        }
    }

    suspend fun duplicateRoute(routeId: Int, newName: String? = null): Result<RouteResponse> {
        return apiClient.safeRequest {
            post("/routes/$routeId/duplicate") {
                parameter("token", appValues.authToken.get())
                newName?.let { parameter("new_name", it) }
            }.body()
        }
    }

    suspend fun optimizeRoute(
        routeId: Int,
        optimizationRequest: RouteOptimizationRequest
    ): Result<RouteResponse> {
        return apiClient.safeRequest {
            post(ApiClient.BASE_URL + "/routes/$routeId/optimize") {
                parameter("token", appValues.authToken.get())
                setBody(optimizationRequest)
            }.body()
        }
    }

    suspend fun getRouteStats(): Result<RouteStats> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/routes/stats/overview") {
                parameter("token", appValues.authToken.get())
            }.body()
        }
    }

    suspend fun getRoutesNearby(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 10.0
    ): Result<List<RouteResponse>> {
        return apiClient.safeRequest {
            get(ApiClient.BASE_URL + "/routes/nearby") {
                parameter("token", appValues.authToken.get())
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("radius_km", radiusKm)
            }.body()
        }
    }
}
class AiService(
    private val apiClient: ApiClient,
    private val appValues: AppValues
) {
    suspend fun generateComment(data: GenerateCommentRequest): Result<GenerateCommentResponse>{
        return apiClient.safeRequest {
            post("ai/ai/generate-comment") {
                parameter("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoxMywicm9sZV9pZCI6MCwiZXhwIjoxNzYxNzcyNzU4fQ.6GqV4BVDlFIBy34HB6ISy-vnuwxJ7cy0X4aZodvHAHo")
                contentType(ContentType.Application.Json)
                setBody(data)
            }.body()
        }
    }
}