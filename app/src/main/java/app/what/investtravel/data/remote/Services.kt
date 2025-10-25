package app.what.investtravel.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

// Auth Service
class AuthService(private val apiClient: ApiClient) {
    suspend fun login(loginRequest: LoginRequest): Result<TokenResponse> {
        return apiClient.safeRequest {
            post("/auth/login/") {
                setBody(loginRequest)
            }.body()
        }
    }
}

// Users Service
class UsersService(private val apiClient: ApiClient) {
    suspend fun createUser(userCreate: UserCreate): Result<UserCreate> {
        return apiClient.safeRequest {
            post("/users/") {
                setBody(userCreate)
            }.body()
        }
    }

    suspend fun getUsers(): Result<List<UserGet>> {
        return apiClient.safeRequest {
            get("/users/").body()
        }
    }

    suspend fun getUser(userId: Int): Result<UserCreate> {
        return apiClient.safeRequest {
            get("/users/$userId").body()
        }
    }

    suspend fun updateUser(userId: Int, userCreate: UserCreate): Result<UserCreate> {
        return apiClient.safeRequest {
            put("/users/$userId") {
                setBody(userCreate)
            }.body()
        }
    }

    suspend fun deleteUser(userId: Int): Result<Unit> {
        return apiClient.safeRequest {
            delete("/users/$userId")
        }
    }

    suspend fun getCurrentUser(): Result<UserMoreModel> {
        return apiClient.safeRequest {
            get("/users/user/me").body()
        }
    }
}

// Routes Service
class RoutesService(private val apiClient: ApiClient) {
    suspend fun generateRoute(routeRequest: RouteRequest): Result<RouteResponse> {
        return apiClient.safeRequest {
            post("/routes/generate") {
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
            get("/routes/") {
                parameter("skip", skip)
                parameter("limit", limit)
                category?.let { parameter("category", it) }
                search?.let { parameter("search", it) }
            }.body()
        }
    }

    suspend fun getRoute(routeId: Int): Result<RouteResponse> {
        return apiClient.safeRequest {
            get("/routes/$routeId").body()
        }
    }

    suspend fun updateRoute(routeId: Int, routeData: Map<String, Any>): Result<RouteResponse> {
        return apiClient.safeRequest {
            put("/routes/$routeId") {
                setBody(routeData)
            }.body()
        }
    }

    suspend fun deleteRoute(routeId: Int): Result<Unit> {
        return apiClient.safeRequest {
            delete("/routes/$routeId")
        }
    }

    suspend fun duplicateRoute(routeId: Int, newName: String? = null): Result<RouteResponse> {
        return apiClient.safeRequest {
            post("/routes/$routeId/duplicate") {
                newName?.let { parameter("new_name", it) }
            }.body()
        }
    }

    suspend fun optimizeRoute(
        routeId: Int,
        optimizationRequest: RouteOptimizationRequest
    ): Result<RouteResponse> {
        return apiClient.safeRequest {
            post("/routes/$routeId/optimize") {
                setBody(optimizationRequest)
            }.body()
        }
    }

    suspend fun getRouteStats(): Result<RouteStats> {
        return apiClient.safeRequest {
            get("/routes/stats/overview").body()
        }
    }

    suspend fun getRoutesNearby(
        latitude: Double,
        longitude: Double,
        radiusKm: Double = 10.0
    ): Result<List<RouteResponse>> {
        return apiClient.safeRequest {
            get("/routes/nearby") {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("radius_km", radiusKm)
            }.body()
        }
    }
}