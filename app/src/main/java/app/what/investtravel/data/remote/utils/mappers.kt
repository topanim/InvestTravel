package app.what.investtravel.data.remote.utils

import app.what.investtravel.data.remote.RouteRequest
import app.what.investtravel.features.travel.presentation.pages.UserPreferences

fun UserPreferences.toRoute(): RouteRequest = RouteRequest(
    startDate = this.startDate,
    endDate = this.endDate,
    startLatitude = this.startLatitude,
    startLongitude = this.startLongitude,
    foodTime = this.foodTime,
    fastFoodTime = this.fastFoodTime,
    mealsPerDay = this.mealsPerDay,
    tourism = this.tourism,
    tourismTime = this.tourismTime,
    shoppingTime = this.shoppingTime,
    leisureTime = this.leisureTime,
    maxDistanceKm = this.maxDistanceKm,
    preferNearby = this.preferNearby,
    avoidNightTime = this.avoidNightTime,
    requireFoodPoints = this.requireFoodPoints,
    art = this.art,
    artTime = this.artTime,
    barTime = this.barTime,
    cafeTime = this.cafeTime,
    restaurant = this.restaurant





)