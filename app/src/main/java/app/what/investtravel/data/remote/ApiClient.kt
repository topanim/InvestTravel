package app.what.investtravel.data.remote

import app.what.investtravel.data.local.settings.AppValues
import io.ktor.client.HttpClient

class ApiClient(
    private val client: HttpClient,
    private val appValues: AppValues
) {
    // TODO: use appValues.authToken.get() or .set()
    companion object {
        const val BASE_URL = "http://45.155.207.232:1478"
    }

    suspend fun <T> safeRequest(block: suspend HttpClient.() -> T): Result<T> {
        return try {
            android.util.Log.d("ApiClient", "Making API request")
            val result = client.block()
            android.util.Log.d("ApiClient", "API request successful")
            Result.success(result)
        } catch (e: Exception) {
            android.util.Log.e("ApiClient", "API request failed", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun updateToken(newToken: String) {
        appValues.authToken.set(newToken)
    }

    fun clearToken() {
        appValues.authToken.set(null)
    }
}