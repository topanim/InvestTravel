package app.what.investtravel.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey val id: Int = 0,
    val name: String? = null,
    val description: String? = null,
    val address: String? = null,
    val city: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val stars: Int? = null,
    val pricePerNight: Int? = null,
    val currency: String? = null,
    val amenities: String? = null,
    val images: String? = null,
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)