package app.what.investtravel.features.travel.domain

import android.util.Log
import androidx.lifecycle.viewModelScope
import app.what.foundation.core.UIController
import app.what.foundation.data.RemoteState
import app.what.foundation.utils.suspendCall
import app.what.investtravel.data.local.database.PointsDao
import app.what.investtravel.data.local.database.RoutesDAO
import app.what.investtravel.data.local.mappers.toEntity
import app.what.investtravel.data.local.mappers.toPointEntities
import app.what.investtravel.data.remote.AiService
import app.what.investtravel.data.remote.GenerateCommentRequest
import app.what.investtravel.data.remote.RoutesService
import app.what.investtravel.data.remote.utils.toRoute
import app.what.investtravel.features.travel.domain.models.Travel
import app.what.investtravel.features.travel.domain.models.TravelAction
import app.what.investtravel.features.travel.domain.models.TravelEvent
import app.what.investtravel.features.travel.domain.models.TravelObject
import app.what.investtravel.features.travel.domain.models.TravelState
import app.what.investtravel.features.travel.presentation.pages.UserPreferences
import app.what.investtravel.ui.components.MapKitController
import com.yandex.mapkit.geometry.Point
import com.yandex.runtime.image.ImageProvider
import io.ktor.client.plugins.logging.Logging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

class TravelController : UIController<TravelState, TravelAction, TravelEvent>(
    TravelState()
) {
    init {
        fetchTravels()
    }
    val routeService: RoutesService by inject(RoutesService::class.java)
    val pointsDao: PointsDao by inject(PointsDao::class.java)
    val routesDao: RoutesDAO by inject(RoutesDAO::class.java)

    val aiService: AiService by inject(AiService::class.java)

    val mapController = MapKitController()

    override fun obtainEvent(viewEvent: TravelEvent) = when (viewEvent) {
        is TravelEvent.TravelSelected -> selectTravel(viewEvent.value)
        is TravelEvent.TravelUnselected -> updateState { copy(selectedTravel = null) }
        is TravelEvent.SaveTravel -> sendTravel(viewEvent.value)
        is TravelEvent.SetToAi -> sendToAi(viewEvent.value)
        is TravelEvent.ShowSheet -> updateState { copy(showSheet = false) }
        else -> {}
    }

    private fun selectTravel(value: Travel) {
        updateState { copy(selectedTravel = value) }
    }

    private fun sendToAi(value: TravelObject){
        updateState { copy(showSheet = true) }
        viewModelScope.launch(Dispatchers.IO) {
            aiService.generateComment(GenerateCommentRequest(value.name)).onSuccess {
              updateState { copy(aiComment = it.comment) }
            }.onFailure {
                updateState { copy(showSheet = false) }
            }

            }
        }



    private fun sendTravel(userPreferences: UserPreferences){
        viewModelScope.launch(Dispatchers.IO) {
            updateState { copy(travelsFetchState = RemoteState.Loading) }
           routeService.generateRoute(userPreferences.toRoute()).onSuccess {
               val respToEntity = it.toEntity()
               val pointsToEntity = it.points.toPointEntities(respToEntity.id)
               routesDao.insert(respToEntity)
               pointsDao.insert(pointsToEntity)
               updateState { copy(travelsFetchState = RemoteState.Success) }
           }.onFailure {
               Log.d("ответ",it.toString())
           }
        }
    }

    fun fetchTravels() {
        suspendCall(viewModelScope) {
            updateState { copy(travelsFetchState = RemoteState.Loading) }
            delay(2000L)
            val travels = listOf(
                Travel(
                    name = "Исторический центр Ростова",
                    distance = 3.5, // км
                    time = 1.5, // часа
                    objects = listOf(
                        TravelObject(
                            bannerUri = "https://i.dailymail.co.uk/i/newpix/2018/05/30/08/4CB9BEF100000578-5746007-image-a-55_1527664623381.jpg",
                            name = "Театральная площадь",
                            type = "Площадь",
                            lat = 47.22251,
                            lon = 39.71867
                        ),
                        TravelObject(
                            bannerUri = "https://i.dailymail.co.uk/i/newpix/2018/05/30/08/4CB9BEF100000578-5746007-image-a-55_1527664623381.jpg",
                            name = "Памятник \"Тачанка-ростовчанка\"",
                            type = "Памятник",
                            lat = 47.2364,
                            lon = 39.7139
                        ),
                        TravelObject(
                            bannerUri = "https://i.dailymail.co.uk/i/newpix/2018/05/30/08/4CB9BEF100000578-5746007-image-a-55_1527664623381.jpg",
                            name = "Улица Большая Садовая",
                            type = "Улица",
                            lat = 47.22486,
                            lon = 39.70229
                        )
                    )
                ),
                Travel(
                    name = "Набережная Дона",
                    distance = 4.2,
                    time = 2.0,
                    objects = listOf(
                        TravelObject(
                            bannerUri = "https://upload.wikimedia.org/wikipedia/commons/3/3c/FC_Rostov_vs._FC_Enisey%2C_19_August_2018.jpg",
                            name = "Набережная реки Дон",
                            type = "Набережная",
                            lat = 47.21750,
                            lon = 39.73350
                        ),
                        TravelObject(
                            bannerUri = "https://upload.wikimedia.org/wikipedia/commons/3/3c/FC_Rostov_vs._FC_Enisey%2C_19_August_2018.jpg",
                            name = "Ростовский кафедральный собор",
                            type = "Собор",
                            lat = 47.23194,
                            lon = 39.70861
                        ),
                        TravelObject(
                            bannerUri = "https://upload.wikimedia.org/wikipedia/commons/3/3c/FC_Rostov_vs._FC_Enisey%2C_19_August_2018.jpg",
                            name = "Зелёный остров",
                            type = "Парк",
                            lat = 47.20000,
                            lon = 39.75000
                        )
                    )
                ),
                Travel(
                    name = "Разноплановый Ростов",
                    distance = 12.0,
                    time = 3.5,
                    objects = listOf(
                        TravelObject(
                            bannerUri = "https://upload.wikimedia.org/wikipedia/commons/3/3c/FC_Rostov_vs._FC_Enisey%2C_19_August_2018.jpg",
                            name = "Железнодорожный вокзал Ростов-Главный",
                            type = "Вокзал",
                            lat = 47.2192,
                            lon = 39.7048
                        ),
                        TravelObject(
                            bannerUri = "https://upload.wikimedia.org/wikipedia/commons/3/3c/FC_Rostov_vs._FC_Enisey%2C_19_August_2018.jpg",
                            name = "Аэропорт Платов",
                            type = "Аэропорт",
                            lat = 47.4933,
                            lon = 39.9247
                        ),
                        TravelObject(
                            bannerUri = "https://upload.wikimedia.org/wikipedia/commons/3/3c/FC_Rostov_vs._FC_Enisey%2C_19_August_2018.jpg",
                            name = "Стадион \"Ростов Арена\"",
                            type = "Стадион",
                            lat = 47.2094,
                            lon = 39.7353
                        )
                    )
                )
            )
            updateState { copy(travelsFetchState = RemoteState.Success, travels = travels) }
        }
    }

    fun sdsd() {
        //        controller.createRoute(
//            listOf(
//                Point(47.236833, 39.712609),
//                Point(47.254877, 39.716081)
//            )
//        ) {
//            Auditor.debug("d", "asdafasffasf")
//            Auditor.debug("d", MapKitController.calculateRouteDistance(it).toString())
//            Auditor.debug("d", MapKitController.calculateRouteTime(it).toString())
//        }

//        val icon = ImageProvider.fromResource(context, R.drawable.placeholder)
//        controller.createPlacemark(Point(47.236833, 39.712609), icon)
//        controller.createPlacemark(Point(47.254877, 39.716081), icon)
    }
}