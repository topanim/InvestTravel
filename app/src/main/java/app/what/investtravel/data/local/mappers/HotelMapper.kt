package app.what.investtravel.data.local.mappers

import app.what.investtravel.data.local.entity.HotelEntity
import app.what.investtravel.data.remote.HotelResponse
import kotlinx.serialization.json.Json

fun HotelResponse.toEntity(): HotelEntity {
    return HotelEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,
        address = this.address,
        stars = this.stars,
        phone = this.phone,
        website = this.website,
        email = this.email,
        amenities = this.amenities?.let { Json.encodeToString(it) },
        images = this.images?.let { Json.encodeToString(it) },
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        currency = this.currency,
        pricePerNight = this.pricePerNight,
    )
}
fun List<HotelResponse>.toEntity(): List<HotelEntity> {
    return this.map { it.toEntity() }
}