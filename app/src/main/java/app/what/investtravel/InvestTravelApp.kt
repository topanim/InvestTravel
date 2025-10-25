package app.what.investtravel

//import app.what.foundation.services.auto_update.GitHubUpdateService
import android.app.Application
import android.location.Geocoder
import androidx.room.Room
import app.what.foundation.services.AppLogger
import app.what.foundation.services.AppLogger.Companion.Auditor
import app.what.foundation.services.crash.CrashHandler
import app.what.investtravel.data.local.database.AppDatabase
import app.what.investtravel.data.local.settings.AppValues
import app.what.investtravel.data.remote.ApiClient
import app.what.investtravel.data.remote.AuthService
import app.what.investtravel.data.remote.HotelsService
import app.what.investtravel.data.remote.RoutesService
import app.what.investtravel.data.remote.UsersService
import app.what.investtravel.features.assistant.domain.AssistantController
import app.what.investtravel.features.dev.presentation.NetworkMonitorPlugin
import app.what.investtravel.features.hotel.domain.HotelController
import app.what.investtravel.features.main.domain.MainController
import app.what.investtravel.features.onboarding.domain.OnboardingController
import app.what.investtravel.features.profile.domain.ProfileController
import app.what.investtravel.features.settings.domain.SettingsController
import app.what.investtravel.features.travel.domain.TravelController
import app.what.investtravel.libs.FileManager
import app.what.investtravel.libs.GoogleDriveParser
import app.what.investtravel.utils.AppUtils
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.MapKitFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.security.cert.X509Certificate
import java.util.Locale
import javax.net.ssl.X509TrustManager

class InvestTravelApp : Application() {
    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey("56a9a2d2-2738-4544-9bdf-2f323b59ec6a")
        MapKitFactory.initialize(applicationContext)
        CrashHandler.initialize(applicationContext)
        AppLogger.initialize(applicationContext)
        Auditor.info("core", "App started")

        startKoin {
            androidContext(this@InvestTravelApp)
            modules(generalModule)
        }
    }
}

val generalModule = module {
    single { AppValues(get()) }
    single { AppUtils(get()) }
    single { GoogleDriveParser(get()) }
    single { FileManager(get()) }

    single { ApiClient(get(), get()) }
    single { AuthService(get(), get()) }
    single { UsersService(get(), get()) }
    single { RoutesService(get(), get()) }
    single { HotelsService(get(), get()) }

    single { Geocoder(get(), Locale.getDefault()) }
    single { LocationServices.getFusedLocationProviderClient(androidContext()) }

    single<HotelController> { HotelController(get(), get(), get(), get()) }
    single<ProfileController> { ProfileController(get()) }
    single<AssistantController> { AssistantController() }
    single<TravelController> { TravelController() }
    single<SettingsController> { SettingsController(get(), get()) }
    single<OnboardingController> { OnboardingController(get()) }
    single<MainController> { MainController() }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "investtravel.db"
        ).build()
    }

    single {
        HttpClient(CIO) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
            }
            install(NetworkMonitorPlugin)

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Auditor.debug("ktor", message)
                    }
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    classDiscriminator = "type"
                })
            }
            engine {
                https {
                    trustManager = object : X509TrustManager {
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun checkServerTrusted(
                            chain: Array<X509Certificate>,
                            authType: String
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    }
                }
            }
        }
    }
}