package app.what.investtravel.data.remote

import app.what.investtravel.data.local.settings.AppValues
import io.ktor.client.HttpClient

class ApiClient(
    private val client: HttpClient,
    private val appValues: AppValues,
    private var token: String? = null
) {
    // TODO: use appValues.authToken.get() or .set()
    companion object {
        const val BASE_URL = "http://45.155.207.232:1478"
    }

    suspend fun <T> safeRequest(block: suspend HttpClient.() -> T): Result<T> {
        return try {
            Result.success(client.block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateToken(newToken: String) {
        token = newToken
    }

    fun clearToken() {
        token = null
    }
}